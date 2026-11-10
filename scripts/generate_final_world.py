#!/usr/bin/env python3
"""Deterministic, save-safe Sanctuary and underground structures for Minecraft 1.21.1.

Produces native gzipped structure NBT and 1.21.1 registry JSON at build time.
Only natural, previously ungenerated chunks receive structures; no runtime /fill
ever touches a player's modified base or an explored cave.
"""
from __future__ import annotations

import gzip
import json
import struct
from pathlib import Path
from generate_dungeons import (
    DATA, THEMES, compound, named, nbt_int, nbt_str, nbt_list,
    nbt_compound, palette_block, template_entity, entity_tags, write_json
)

REGIONS = [
    ("smugglers_tunnels", "minecraft:mossy_cobblestone", "minecraft:deepslate_bricks", "minecraft:polished_deepslate", "minecraft:skeleton"),
    ("emberfault_chasms", "minecraft:basalt", "minecraft:polished_blackstone_bricks", "minecraft:magma_block", "minecraft:blaze"),
    ("drowned_ossuary", "minecraft:bone_block", "minecraft:mossy_stone_bricks", "minecraft:prismarine", "minecraft:drowned"),
    ("starshard_caverns", "minecraft:calcite", "minecraft:prismarine_bricks", "minecraft:amethyst_block", "minecraft:stray"),
    ("rootbound_grottos", "minecraft:moss_block", "minecraft:tuff_bricks", "minecraft:mangrove_roots", "minecraft:husk"),
    ("black_crown_depths", "minecraft:deepslate_tiles", "minecraft:polished_blackstone_bricks", "minecraft:crying_obsidian", "minecraft:wither_skeleton"),
]
ROOMS = [
    "trophy_hall", "armoury", "enchanters_workshop", "cartographers_room", "treasure_vault",
    "portal_chamber", "war_room", "foyer", "bedroom", "bulk_storage",
    "smelting_room", "crafting_workshop", "brewing_nook", "garden_and_farm", "secret_arena",
]

def block_palette(name: str) -> bytes:
    if "[" not in name:
        return palette_block(name)
    base, state = name.split("[", 1)
    properties = [nbt_str(k.strip(), v.strip()) for k, v in (part.split("=") for part in state.rstrip("]").split(","))]
    return compound(nbt_str("Name", base), nbt_compound("Properties", *properties))

def save_structure(name: str, size: tuple[int, int, int],
                   blocks: dict, entities: list) -> None:
    palette = list(dict.fromkeys(material for material, extra in blocks.values()))
    indexes = {material: index for index, material in enumerate(palette)}
    entries = []
    for pos, (material, extra) in sorted(blocks.items()):
        tags = [nbt_list("pos", 3, [struct.pack(">i", n) for n in pos]),
                nbt_int("state", indexes[material])]
        if extra is not None:
            tags.append(named(10, "nbt", extra))
        entries.append(compound(*tags))
    payload = compound(
        nbt_int("DataVersion", 3955),
        nbt_list("size", 3, [struct.pack(">i", n) for n in size]),
        nbt_list("palette", 10, [block_palette(n) for n in palette]),
        nbt_list("blocks", 10, entries),
        nbt_list("entities", 10, entities)
    )
    path = DATA / f"structure/{name}.nbt"
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(gzip.compress(named(10, "", payload), compresslevel=9, mtime=0))

def set_block(blocks: dict, x: int, y: int, z: int, kind: str, extra=None):
    blocks[(x, y, z)] = (kind, extra)

def filled_chest(blocks: dict, x: int, y: int, z: int, loot: str | None = None):
    nbt = compound(nbt_str("id", "minecraft:chest"), *(
        (nbt_str("LootTable", loot),) if loot is not None else ()
    ))
    set_block(blocks, x, y, z, "minecraft:chest", nbt)

def decorate_sanctuary(blocks: dict, w: int, d: int) -> None:
    """Fortified gabled hall, four corner towers and a Chronicle Archive.
    
    All decorations are in the new template, so untouched existing saves stay
    intact and new generation / Keystone placement receives the same design.
    """
    # Manuscript gallery occupies the former under-decorated treasure vault.
    for x in (65, 79):
        for z in range(3, 14):
            for y in range(1, 5):
                set_block(blocks, x, y, z, "minecraft:bookshelf")
    for x in range(66, 79):
        for y in range(1, 4):
            set_block(blocks, x, y, 15, "minecraft:chiseled_bookshelf")
    for x, z in ((67, 3), (71, 3), (75, 3), (67, 13), (71, 13), (75, 13)):
        set_block(blocks, x, 1, z, "minecraft:lectern")
    for x, z in ((66, 2), (78, 2), (66, 14), (78, 14)):
        set_block(blocks, x, 1, z, "minecraft:soul_lantern")
    # Windows, pilasters and external sculpted wall ribs.
    for x in range(w):
        for z in (0, d - 1):
            if x % 16 in (0, 1, 15):
                for y in range(1, 9):
                    if not (z == 0 and 38 <= x <= 42 and y <= 5):
                        set_block(blocks, x, y, z, "minecraft:polished_deepslate")
            elif x % 8 in (7, 0) and not (z == 0 and 38 <= x <= 42):
                for y in range(3, 7):
                    set_block(blocks, x, y, z, "minecraft:blue_stained_glass_pane")
                set_block(blocks, x, 7, z, "minecraft:chiseled_stone_bricks")
    for z in range(d):
        for x in (0, w - 1):
            if z % 16 in (0, 1, 15):
                for y in range(1, 9):
                    set_block(blocks, x, y, z, "minecraft:polished_deepslate")
            elif z % 8 in (7, 0):
                for y in range(3, 7):
                    set_block(blocks, x, y, z, "minecraft:blue_stained_glass_pane")
    # Fifteen steep pitch roofs, dark ridges, and stone-edged eaves.
    # Garden wing has daylight instead of an opaque gable.
    for col in range(5):
        for row in range(3):
            if (col, row) == (3, 2):
                continue
            x0, z0 = col * 16, row * 16
            for x in range(x0, min(x0 + 17, w)):
                for rise in range(8):
                    for z in (z0 + rise, z0 + 16 - rise):
                        if z < d:
                            set_block(blocks, x, 10 + rise, z, "minecraft:dark_oak_planks")
                            if rise in (0, 3, 6):
                                set_block(blocks, x, 11 + rise, z, "minecraft:deepslate_tile_slab")
                set_block(blocks, x, 18, z0 + 8, "minecraft:polished_blackstone")
    # Skylit greenhouse roof above the garden.
    for x in range(49, 64):
        for z in range(33, 48):
            rise = min(z - 33, 47 - z)
            set_block(blocks, x, 10 + rise, z, "minecraft:glass")
    for x in (49, 63):
        for z in (33, 47):
            for y in range(10, 18):
                set_block(blocks, x, y, z, "minecraft:dark_oak_log")
    # Corner towers with crenellations and battlements.
    for ax in (0, w - 5):
        for az in (0, d - 5):
            for x in range(ax, ax + 5):
                for z in range(az, az + 5):
                    if x in (ax, ax + 4) or z in (az, az + 4):
                        for y in range(10, 17):
                            set_block(blocks, x, y, z, "minecraft:deepslate_bricks")
                    set_block(blocks, x, 16, z, "minecraft:polished_blackstone")
                    if (x + z) % 2 == 0 and (x in (ax, ax + 4) or z in (az, az + 4)):
                        set_block(blocks, x, 17, z, "minecraft:deepslate_brick_wall")
    for x in range(36, 45):
        for y in range(1, 10):
            if x in (36, 44) or y in (8, 9):
                set_block(blocks, x, y, 0, "minecraft:polished_blackstone_bricks")
    for x in (36, 44):
        set_block(blocks, x, 6, 1, "minecraft:sea_lantern")

def remodel_sanctuary(blocks: dict) -> None:
    """Grand interconnected interior with an open central nave and gallery.

    Preserve all room markers, ground-level utility blocks, chest contents and
    the 81x49x20 template bounds. This is only applied to NEW placements.
    """
    # Merge the foyer with its neighbouring halls using broad vaulted portals.
    # The older five-block openings remain, but the central procession is wider.
    for x in (32, 48):
        for z in range(19, 30):
            for y in range(1, 7):
                set_block(blocks, x, y, z, "minecraft:air")
        for z in (18, 30):
            for y in range(1, 8):
                set_block(blocks, x, y, z, "minecraft:polished_blackstone_bricks")
        for z in range(20, 29):
            set_block(blocks, x, 7, z, "minecraft:polished_blackstone_bricks")
    for z in (16, 32):
        for x in range(35, 46):
            for y in range(1, 7):
                set_block(blocks, x, y, z, "minecraft:air")
        for x in (34, 46):
            for y in range(1, 8):
                set_block(blocks, x, y, z, "minecraft:polished_blackstone_bricks")
        for x in range(36, 45):
            set_block(blocks, x, 7, z, "minecraft:polished_blackstone_bricks")

    # Carve shallow alcoves into the internal dividing walls, away from doors.
    # They make the wings feel like one castle rather than fifteen boxes.
    for x in (16, 32, 48, 64):
        for z in (4, 12, 20, 28, 36, 44):
            if x in (32, 48) and z in (20, 28):
                continue
            for dz in (-1, 0, 1):
                for y in range(2, 5):
                    set_block(blocks, x, y, z + dz, "minecraft:air")
            for dz in (-2, 2):
                for y in range(1, 6):
                    set_block(blocks, x, y, z + dz, "minecraft:polished_deepslate")
            set_block(blocks, x, 5, z, "minecraft:chiseled_stone_bricks")
    for z in (16, 32):
        for x in (4, 12, 20, 28, 52, 60, 68, 76):
            for dx in (-1, 0, 1):
                for y in range(2, 5):
                    set_block(blocks, x + dx, y, z, "minecraft:air")
            for dx in (-2, 2):
                for y in range(1, 6):
                    set_block(blocks, x + dx, y, z, "minecraft:polished_deepslate")
            set_block(blocks, x, 5, z, "minecraft:chiseled_stone_bricks")

    # Raised spectator gallery in the foyer, with a clear central aisle.
    # No changes at y=1 near the lodestone at (40,1,24).
    for z in range(19, 30):
        for x in list(range(34, 37)) + list(range(44, 47)):
            set_block(blocks, x, 6, z, "minecraft:dark_oak_planks")
        for x in (37, 43):
            set_block(blocks, x, 6, z, "minecraft:dark_oak_fence")
    for z in (19, 29):
        for x in range(37, 44):
            set_block(blocks, x, 6, z, "minecraft:dark_oak_fence")
    # Symmetrical staircases from the main floor up to the gallery.
    for z in range(19, 25):
        height = z - 18
        for x in (35, 45):
            set_block(blocks, x, height, z,
                      "minecraft:polished_blackstone_brick_stairs[facing=north]")
            for y in range(1, height):
                set_block(blocks, x, y, z, "minecraft:polished_blackstone_bricks")
    # Vaulted hall ribbing and chandelier over the central floor.
    for z in (18, 24, 30):
        for x in range(35, 46):
            set_block(blocks, x, 8, z, "minecraft:polished_blackstone_bricks")
        for x in (37, 43):
            set_block(blocks, x, 7, z, "minecraft:chiseled_deepslate")
    for x, z in ((40, 20), (40, 28)):
        set_block(blocks, x, 8, z, "minecraft:chain")
        set_block(blocks, x, 7, z, "minecraft:lantern")

    # Distinctive hall accents, avoiding all functional floor-level blocks.
    for x, z in ((8, 8), (24, 8), (40, 8), (56, 8),
                 (8, 24), (24, 24), (56, 24), (72, 24),
                 (8, 40), (24, 40), (40, 40), (72, 40)):
        for y in range(5, 8):
            for dx in (-4, 4):
                set_block(blocks, x + dx, y, z - 6, "minecraft:polished_blackstone_bricks")
        for dx in (-4, 4):
            set_block(blocks, x + dx, 7, z - 6, "minecraft:sea_lantern")


def sculpt_sanctuary(blocks: dict) -> None:
    """Break the uniform square interiors into varied vaulted polygonal chambers.

    Retain functional floor blocks, room markers, all existing doorways, and
    the central gallery. This only affects newly generated/placed templates.
    """
    protected = {2, 4, 7, 8, 9, 12, 13, 14}  # archive, storage, garden, arena, and central nave
    for index in range(15):
        if index in protected:
            continue
        cx, cz = 8 + index % 5 * 16, 8 + index // 5 * 16
        for dx in range(-7, 8):
            for dz in range(-7, 8):
                ax, az = abs(dx), abs(dz)
                # Chamfer the corners to produce a broad octagonal chamber.
                if ax + az < 11 or ax < 3 or az < 3:
                    continue
                x, z = cx + dx, cz + dz
                for y in range(2, 8):
                    old = blocks.get((x, y, z), ("minecraft:air", None))[0]
                    if old == "minecraft:air":
                        set_block(blocks, x, y, z, "minecraft:polished_deepslate")
                # Dark buttress capitals emphasize the non-rectangular walls.
                if ax + az == 11:
                    old = blocks.get((x, 8, z), ("minecraft:air", None))[0]
                    if old == "minecraft:air":
                        set_block(blocks, x, 8, z, "minecraft:chiseled_stone_bricks")
    # Open a larger arched passage between the trophy hall and armoury.
    # Leave floor, displays and room-centre markers untouched.
    for z in range(5, 12):
        for y in range(2, 7):
            set_block(blocks, 16, y, z, "minecraft:air")
    # Round the foyer-to-wing transitions with graduated high arch crowns.
    for x in (32, 48):
        for dz in range(-5, 6):
            crown = 7 if abs(dz) <= 2 else 6 if abs(dz) <= 4 else 5
            for y in range(2, crown):
                set_block(blocks, x, y, 24 + dz, "minecraft:air")


def sanctuary() -> None:
    # 5 x 3 connected rooms, each with a 15x15 usable interior. No auto
    # generated equipment, stored loot, resources, or free fast travel.
    blocks, entities = {}, []
    w, d, h = 81, 49, 20
    for x in range(w):
        for z in range(d):
            for y in range(10):
                room = min(x // 16, 4) + 5 * min(z // 16, 2)
                floor = "minecraft:polished_deepslate" if room < 7 else "minecraft:stone_bricks"
                material = (floor if y == 0 else
                    "minecraft:deepslate_tiles" if y == 9 else
                    "minecraft:stone_bricks" if x % 16 == 0 or z % 16 == 0 else
                    "minecraft:air")
                set_block(blocks, x, y, z, material)
    # Wide connected arches and the front gate. All wings are traversable.
    for x in (16, 32, 48, 64):
        for z in (8, 24, 40):
            for dz in (-2, -1, 0, 1, 2):
                for y in range(1, 5):
                    set_block(blocks, x, y, z + dz, "minecraft:air")
    for z in (16, 32):
        for x in (8, 24, 40, 56, 72):
            for dx in (-2, -1, 0, 1, 2):
                for y in range(1, 5):
                    set_block(blocks, x + dx, y, z, "minecraft:air")
    for x in range(38, 43):
        for y in range(1, 6):
            set_block(blocks, x, y, 0, "minecraft:air")
    for i, room in enumerate(ROOMS):
        cx, cz = 8 + (i % 5) * 16, 8 + (i // 5) * 16
        room_tags = ["ss_sanctuary_room", "ss_sanctuary_" + room]
        if room == "treasure_vault":
            room_tags.append("ss_sanctuary_chronicle_archive")
        entities.append(template_entity("minecraft:marker", cx, 1, cz,
            entity_tags(*room_tags)))
        # Each room has unobstructed centre, unique named markers and safe light.
        for dx, dz in ((-6, -6), (6, -6), (-6, 6), (6, 6)):
            set_block(blocks, cx + dx, 8, cz + dz, "minecraft:sea_lantern")
        set_block(blocks, cx, 0, cz, "minecraft:chiseled_deepslate")
    # Trophy Hall: six empty stone stands; the boss's real dropped trophy can be displayed.
    for cx in (4, 8, 12):
        for cz in (4, 12):
            set_block(blocks, cx, 1, cz, "minecraft:polished_blackstone")
            set_block(blocks, cx, 2, cz, "minecraft:polished_blackstone_wall")
    # Armoury: armour stands are intentionally empty, for the player's gear.
    for cx in (20, 24, 28):
        for cz in (4, 12):
            entities.append(template_entity("minecraft:armor_stand", cx, 1, cz,
                named(1, "NoGravity", b"\x01"), named(1, "PersistenceRequired", b"\x01")))
    # Enchanting and cartography: normal Minecraft costs and recipes.
    for x, kind in ((38, "minecraft:enchanting_table"), (42, "minecraft:grindstone"),
                    (36, "minecraft:anvil"), (44, "minecraft:lectern"),
                    (54, "minecraft:cartography_table"), (58, "minecraft:lectern")):
        set_block(blocks, x, 1, 8, kind)
    for x, z in ((68, 5), (72, 5), (76, 5), (68, 11), (72, 11), (76, 11)):
        filled_chest(blocks, x, 1, z)
    # Portal chamber: decorative inert gate, no free teleport or portal activation.
    for z in (21, 27):
        for y in range(1, 5):
            set_block(blocks, 3, y, z, "minecraft:crying_obsidian")
    for z in range(21, 28):
        set_block(blocks, 3, 5, z, "minecraft:polished_blackstone")
    # War Room: maps, books and a physical table.
    for x in range(20, 29):
        for z in range(22, 27):
            set_block(blocks, x, 1, z, "minecraft:dark_oak_planks")
    set_block(blocks, 24, 2, 24, "minecraft:lectern")
    # Foyer respawn lodestone, not an automatic world spawn override.
    set_block(blocks, 40, 1, 24, "minecraft:lodestone")
    # Bedroom, storage and utilities.
    set_block(blocks, 53, 1, 24, "minecraft:red_bed[facing=south,part=foot,occupied=false]")
    set_block(blocks, 53, 1, 25, "minecraft:red_bed[facing=south,part=head,occupied=false]")
    for x in (68, 72, 76):
        for z in (21, 24, 27):
            filled_chest(blocks, x, 1, z)
    for x, kind in ((4, "minecraft:furnace"), (7, "minecraft:blast_furnace"),
                    (10, "minecraft:smoker"), (13, "minecraft:furnace")):
        set_block(blocks, x, 1, 40, kind)
    for x, kind in ((20, "minecraft:crafting_table"), (23, "minecraft:anvil"),
                    (26, "minecraft:smithing_table"), (29, "minecraft:stonecutter")):
        set_block(blocks, x, 1, 40, kind)
    for x in (36, 40, 44):
        set_block(blocks, x, 1, 40, "minecraft:brewing_stand")
    set_block(blocks, 40, 1, 36, "minecraft:cauldron")
    # Garden with water and tilled crop rows; seedlings are supplied by player.
    for x in range(52, 61):
        for z in range(37, 44):
            set_block(blocks, x, 0, z, "minecraft:farmland")
    for x in range(52, 61):
        set_block(blocks, x, 0, 40, "minecraft:water[level=0]")
    for x in range(49, 64):
        for z in range(33, 48):
            set_block(blocks, x, 9, z, "minecraft:glass")
    # The hidden arena is physically separate from functional storage.
    for x in range(68, 77):
        for z in range(36, 45):
            set_block(blocks, x, 0, z, "minecraft:polished_blackstone")
    for x, z in ((68, 36), (76, 36), (68, 44), (76, 44)):
        set_block(blocks, x, 1, z, "minecraft:soul_lantern")
    set_block(blocks, 72, 0, 40, "minecraft:crying_obsidian")
    entities.append(template_entity("minecraft:marker", 72, 1, 40,
        entity_tags("ss_secret_arena")))
    decorate_sanctuary(blocks, w, d)
    remodel_sanctuary(blocks)
    sculpt_sanctuary(blocks)
    save_structure("wardens_sanctuary", (w, h, d), blocks, entities)

def depths_site(index: int, wall: str, floor: str, trim: str) -> None:
    blocks, entities = {}, []
    for x in range(21):
        for z in range(21):
            for y in range(9):
                kind = floor if y == 0 else wall if y == 8 or x in (0, 20) or z in (0, 20) else "minecraft:air"
                set_block(blocks, x, y, z, kind)
    for z in (0, 20):
        for x in range(8, 13):
            for y in range(1, 5):
                set_block(blocks, x, y, z, "minecraft:air")
    for x, z in ((3, 3), (17, 3), (3, 17), (17, 17)):
        for y in range(1, 7):
            set_block(blocks, x, y, z, trim)
        set_block(blocks, x, 7, z, "minecraft:sea_lantern")
    set_block(blocks, 10, 0, 10, trim)
    set_block(blocks, 10, 1, 10, "minecraft:chiseled_deepslate")
    filled_chest(blocks, 15, 1, 10, "shatteredseals:chests/depths_cache")
    entities.append(template_entity("minecraft:marker", 10, 2, 10,
        entity_tags("ss_depths_site", "ss_depths_region_" + str(index + 1))))
    save_structure(REGIONS[index][0] + "_ruin", (21, 9, 21), blocks, entities)

def hidden_vault(index: int, wall: str, floor: str, trim: str) -> None:
    blocks, entities = {}, []
    for x in range(13):
        for z in range(13):
            for y in range(9):
                kind = floor if y == 0 else wall if y == 8 or x in (0, 12) or z in (0, 12) else "minecraft:air"
                set_block(blocks, x, y, z, kind)
    for x in range(5, 8):
        for y in range(1, 5):
            set_block(blocks, x, y, 0, "minecraft:air")
    for x in (3, 6, 9):
        set_block(blocks, x, 1, 4, ("minecraft:chiseled_copper", "minecraft:prismarine", "minecraft:polished_blackstone")[(x - 3) // 3])
    for x in range(4, 9):
        for y in range(1, 4):
            set_block(blocks, x, y, 7, "minecraft:iron_bars")
    filled_chest(blocks, 6, 1, 10, "shatteredseals:chests/hidden_vault")
    entities.append(template_entity("minecraft:marker", 6, 2, 5,
        entity_tags("ss_depths_vault", "ss_depths_region_" + str(index + 1))))
    save_structure(REGIONS[index][0] + "_vault", (13, 9, 13), blocks, entities)

def register_structure(name: str, biome, spacing: int, salt: int,
                       height: int | None = None):
    write_json(DATA / f"worldgen/template_pool/{name}.json", {
        "fallback": "minecraft:empty",
        "elements": [{"weight": 1, "element": {
            "element_type": "minecraft:single_pool_element",
            "location": f"shatteredseals:{name}", "processors": "minecraft:empty",
            "projection": "rigid"}}]
    })
    properties = {
        "type": "minecraft:jigsaw", "biomes": biome,
        "step": "surface_structures" if height is None else "underground_structures",
        "terrain_adaptation": "beard_thin" if height is None else "none",
        "spawn_overrides": {}, "start_pool": f"shatteredseals:{name}",
        "size": 1,
        "start_height": {"absolute": 0 if height is None else height},
        "max_distance_from_center": 80,
        "use_expansion_hack": False
    }
    if height is None:
        properties["project_start_to_heightmap"] = "WORLD_SURFACE_WG"
    write_json(DATA / f"worldgen/structure/{name}.json", properties)
    write_json(DATA / f"worldgen/structure_set/{name}.json", {
        "structures": [{"structure": f"shatteredseals:{name}", "weight": 1}],
        "placement": {"type": "minecraft:random_spread", "spacing": spacing,
            "separation": max(5, spacing // 4), "salt": salt,
            "spread_type": "linear"}
    })
    write_json(DATA / f"tags/worldgen/structure/{name}.json", {"values": [f"shatteredseals:{name}"]})

def decorate_caves(index: int, biome: str, feature_block: str) -> None:
    # Small natural-looking rock seams at Y -48 through +16, no ore replacement
    # outside our rare six chapter biomes and no vanilla cave carver removal.
    name = biome + "_depths_vein"
    target = lambda tag: {"target": {"predicate_type": "minecraft:tag_match", "tag": tag},
        "state": {"Name": feature_block}}
    write_json(DATA / f"worldgen/configured_feature/{name}.json", {
        "type": "minecraft:ore",
        "config": {"size": 7, "discard_chance_on_air_exposure": 0,
            "targets": [target("minecraft:stone_ore_replaceables"),
                        target("minecraft:deepslate_ore_replaceables")]}
    })
    write_json(DATA / f"worldgen/placed_feature/{name}.json", {
        "feature": f"shatteredseals:{name}",
        "placement": [
            {"type": "minecraft:rarity_filter", "chance": 5},
            {"type": "minecraft:in_square"},
            {"type": "minecraft:height_range", "height": {
                "type": "minecraft:uniform",
                "min_inclusive": {"absolute": -48},
                "max_inclusive": {"absolute": 16}}},
            {"type": "minecraft:biome"}
        ]
    })
    biome_file = DATA / f"worldgen/biome/{biome}.json"
    definition = json.loads(biome_file.read_text())
    placed = f"shatteredseals:{name}"
    if placed not in definition["features"][6]:
        definition["features"][6].append(placed)
    write_json(biome_file, definition)

def main() -> None:
    sanctuary()
    # Keystone-only: do not register the Sanctuary for natural generation.
    for index, (dungeon, biome, wall, floor, trim, mob, salt) in enumerate(THEMES):
        region, feature_block, cave_wall, accent, guardian = REGIONS[index]
        depths_site(index, cave_wall, floor, accent)
        hidden_vault(index, cave_wall, floor, accent)
        register_structure(region + "_ruin", biome, 38, salt + 7100, -32)
        register_structure(region + "_vault", biome, 78, salt + 9100, -34)
        decorate_caves(index, biome.split(":")[1], feature_block)
    print("Generated Keystone-only Warden Sanctuary, six underground ruins and six hidden vaults.")

if __name__ == "__main__":
    main()
