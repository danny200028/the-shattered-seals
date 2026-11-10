#!/usr/bin/env python3
"""Deterministic, save-safe Sanctuary and underground structures for Minecraft 1.21.1.

Produces native gzipped structure NBT and 1.21.1 registry JSON at build time.
Underground structures generate in new chunks. The Sanctuary is a separate
blueprint for future Keystone placements.
"""
from __future__ import annotations

import gzip
import json
import struct
from pathlib import Path
from generate_dungeons import (
    DATA, THEMES, compound, named, nbt_int, nbt_str, nbt_list,
    nbt_compound, nbt_string, palette_block, template_entity, entity_tags, write_json
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

AIR = "minecraft:air"
BRICK = "minecraft:deepslate_bricks"
TRIM = "minecraft:polished_blackstone_bricks"
TILE = "minecraft:deepslate_tiles"
TIMBER = "minecraft:dark_oak_planks"
FENCE = "minecraft:dark_oak_fence"
CARDINAL = ((1, 0), (-1, 0), (0, 1), (0, -1))


def footprint(x0, x1, z0, z1, bevel):
    return {(x, z) for x in range(x0, x1 + 1) for z in range(z0, z1 + 1)
            if min(x - x0, x1 - x) + min(z - z0, z1 - z) >= bevel}


def boundary(plan):
    return {(x, z) for x, z in plan
            if any((x + dx, z + dz) not in plan for dx, dz in CARDINAL)}


def staircase(blocks, xs, floor, start, direction):
    """Build a supported flight. The high half of each tread faces uphill."""
    facing = "south" if direction == 1 else "north"
    stair = ("minecraft:polished_blackstone_brick_stairs"
             f"[facing={facing},half=bottom,shape=straight,waterlogged=false]")
    upper = floor + 11
    shaft = set()
    for rise in range(1, 12):
        z = start + direction * (rise - 1)
        y = floor + rise
        for x in xs:
            for support_y in range(floor + 1, y):
                set_block(blocks, x, support_y, z, TRIM)
            set_block(blocks, x, y, z, stair)
            # Three clear blocks accommodate the player during the step up.
            for clear_y in range(y + 1, y + 4):
                set_block(blocks, x, clear_y, z, AIR)
            if y < upper <= y + 3:
                shaft.add((x, z))
    return shaft


def guard_opening(blocks, opening, deck, floor):
    for x, z in opening:
        for dx, dz in CARDINAL:
            nx, nz = x + dx, z + dz
            if (nx, nz) in deck and (nx, nz) not in opening:
                if "_stairs[" in blocks[(nx, floor, nz)][0]:
                    continue
                if blocks.get((nx, floor + 1, nz), (AIR, None))[0] == AIR:
                    set_block(blocks, nx, floor + 1, nz, FENCE)


def stair_sign(blocks, x, y, z, destination, rotation):
    messages = [json.dumps({"text": line}) for line in ("Grand Stair", destination, "", "")]
    lines = nbt_list("messages", 8, [nbt_string(line) for line in messages])
    set_block(blocks, x, y, z, f"minecraft:dark_oak_sign[rotation={rotation},waterlogged=false]",
              compound(nbt_str("id", "minecraft:sign"), nbt_compound("front_text", lines),
                       nbt_compound("back_text", lines)))


def sanctuary_architecture(blocks):
    """Build joined envelopes around the ground-floor furnishings and markers."""
    lower = footprint(0, 80, 2, 48, 4) | footprint(34, 46, 0, 4, 0)
    keep = footprint(16, 64, 4, 32, 6)
    garden = {(x, z) for x in range(49, 64) for z in range(33, 48)}
    lower_edge = boundary(lower)
    keep_edge = boundary(keep)

    # A continuous chamfered curtain wall joins both lower storeys.
    for x, z in lower:
        set_block(blocks, x, 0, z, TILE if (x, z) in lower_edge else BRICK)
        if (x, z) not in garden:
            set_block(blocks, x, 11, z, TIMBER)
        set_block(blocks, x, 22, z, "minecraft:glass" if (x, z) in garden else TILE)
    for x, z in lower_edge:
        straight = x in (0, 80) or z in (0, 2, 48)
        window = straight and (x + z) % 8 in (3, 4)
        for y in range(1, 22):
            if y == 11:
                set_block(blocks, x, y, z, TRIM)
            elif z == 0 and abs(x - 40) <= 3 and y <= 7 - abs(x - 40):
                set_block(blocks, x, y, z, AIR)
            else:
                material = ("minecraft:blue_stained_glass" if window and y in (4, 5, 15, 16, 17)
                            else TRIM if (x + z) % 8 == 0 else BRICK)
                set_block(blocks, x, y, z, material)
        if (x + z) % 2 == 0:
            set_block(blocks, x, 23, z, "minecraft:deepslate_brick_wall")

    # Joined partitions. The only openings are arched doorways between room centres.
    for x, z in lower - lower_edge:
        if x % 16 != 0 and z % 16 != 0:
            continue
        offset = min(abs(z % 16 - 8) if x % 16 == 0 else 8,
                     abs(x % 16 - 8) if z % 16 == 0 else 8)
        central = (x in (32, 48) and 16 < z < 32) or (z in (16, 32) and 32 < x < 48)
        for y in range(1, 11):
            if offset <= (5 if central else 2) and y <= (8 if central else 6) - offset:
                continue
            set_block(blocks, x, y, z, TRIM if y >= 9 else BRICK)

    # Room-specific chamfers and rounded corners, with stepped vault ribs.
    for index in (0, 1, 3, 5, 6, 8, 9, 10, 11, 12, 14):
        cx, cz = 8 + 16 * (index % 5), 8 + 16 * (index // 5)
        for dx in range(-7, 8):
            for dz in range(-7, 8):
                x, z = cx + dx, cz + dz
                if (x, z) not in lower or (x, z) in lower_edge:
                    continue
                ax, az = abs(dx), abs(dz)
                if index in (0, 5, 8, 11, 14):
                    corner = ax + az >= 12
                else:
                    corner = ax * ax + az * az >= 74
                if corner:
                    for y in range(1, 11):
                        set_block(blocks, x, y, z, BRICK)
                elif az in (3, 6):
                    for y in range(10 - max(0, ax - 4), 11):
                        set_block(blocks, x, y, z, TRIM)
        for dx in (-4, 4):
            set_block(blocks, cx + dx, 10, cz, "minecraft:sea_lantern")

    # Ceiling lamps frame the workspaces and keep the Grand Stair in view.
    for x, z in ((36, 5), (44, 5), (36, 8), (44, 8), (68, 8),
                 (76, 8), (40, 24), (40, 40), (52, 32), (60, 32)):
        set_block(blocks, x, 10, z, "minecraft:sea_lantern")

    # Upper gallery wings are divided by pierced ribs, with wide connecting aisles.
    for x in (16, 32, 48, 64):
        for z in range(1, 48):
            if (x, z) not in lower - lower_edge:
                continue
            offset = abs(z % 16 - 8)
            for y in range(12, 22):
                if offset <= 4 and y <= 19 - offset:
                    continue
                set_block(blocks, x, y, z, TRIM if y == 21 else BRICK)
    for x in range(8, 80, 8):
        for z in (8, 24, 40):
            set_block(blocks, x, 21, z, "minecraft:sea_lantern")

    # The third-floor keep has its own closed perimeter and a joined stepped vault.
    def roof(x):
        return 39 - min(9, abs(x - 40) // 2)

    for x, z in keep:
        for y in range(min(roof(x), roof(x - 1), roof(x + 1)), roof(x) + 1):
            set_block(blocks, x, y, z, TILE)
        if (x, z) in keep_edge:
            for y in range(23, roof(x)):
                window = (x + z) % 8 in (3, 4) and 25 <= y <= 28
                set_block(blocks, x, y, z, "minecraft:purple_stained_glass" if window else BRICK)
        elif z in (8, 16, 24):
            set_block(blocks, x, roof(x) - 2, z,
                      "minecraft:sea_lantern" if x % 8 == 0 else TRIM)

    # Corner turrets rise out of the shoulder roof. Their interiors open down
    # into the gallery and their walls meet a solid cap under the battlements.
    for cx, cz in ((8, 8), (72, 8), (8, 40), (72, 40)):
        tower = footprint(cx - 4, cx + 4, cz - 4, cz + 4, 3)
        edge = boundary(tower)
        for x, z in tower:
            set_block(blocks, x, 22, z, TRIM if (x, z) in edge else AIR)
            set_block(blocks, x, 32, z, TILE)
            if (x, z) in edge:
                for y in range(23, 32):
                    window = (x == cx or z == cz) and 26 <= y <= 29
                    set_block(blocks, x, y, z, "minecraft:blue_stained_glass" if window else BRICK)
                if (x + z) % 2 == 0:
                    set_block(blocks, x, 33, z, "minecraft:deepslate_brick_wall")
        set_block(blocks, cx, 32, cz, "minecraft:sea_lantern")

    # The entrance faces the first flight. Both flights share a generous
    # switchback landing, with the return flight visible from the first.
    first_shaft = staircase(blocks, range(37, 40), 0, 11, 1)
    second_shaft = staircase(blocks, range(42, 45), 11, 21, -1)
    guard_opening(blocks, first_shaft | garden, lower - lower_edge, 11)
    guard_opening(blocks, second_shaft, keep - keep_edge, 22)
    stair_sign(blocks, 36, 1, 10, "Second Floor", 0)
    stair_sign(blocks, 45, 12, 22, "Third Floor", 8)


def archive_furnishings(blocks):
    # Shelving stops on either side of the west and south arches.
    for x, rows in ((65, (3, 4, 5, 11, 12, 13)), (79, range(7, 14))):
        for z in rows:
            for y in range(1, 5):
                set_block(blocks, x, y, z, "minecraft:bookshelf")
    for x in (*range(66, 70), *range(75, 79)):
        for y in range(1, 4):
            set_block(blocks, x, y, 15, "minecraft:chiseled_bookshelf")
    for x, z in ((67, 3), (71, 3), (75, 3), (67, 13), (71, 13), (75, 13)):
        set_block(blocks, x, 1, z, "minecraft:lectern")


def sanctuary() -> None:
    # Fifteen connected functional rooms. No auto
    # generated equipment, stored loot, resources, or free fast travel.
    blocks, entities = {}, []
    w, d, h = 81, 49, 42
    sanctuary_architecture(blocks)
    for i, room in enumerate(ROOMS):
        cx, cz = 8 + (i % 5) * 16, 8 + (i // 5) * 16
        room_tags = ["ss_sanctuary_room", "ss_sanctuary_" + room]
        if room == "treasure_vault":
            room_tags.append("ss_sanctuary_chronicle_archive")
        entities.append(template_entity("minecraft:marker", cx, 1, cz,
            entity_tags(*room_tags)))
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
    # The hidden arena is physically separate from functional storage.
    for x in range(68, 77):
        for z in range(36, 45):
            set_block(blocks, x, 0, z, "minecraft:polished_blackstone")
    for x, z in ((68, 36), (76, 36), (68, 44), (76, 44)):
        set_block(blocks, x, 1, z, "minecraft:soul_lantern")
    set_block(blocks, 72, 0, 40, "minecraft:crying_obsidian")
    entities.append(template_entity("minecraft:marker", 72, 1, 40,
        entity_tags("ss_secret_arena")))
    archive_furnishings(blocks)
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
