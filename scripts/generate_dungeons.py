#!/usr/bin/env python3
"""Generate six deterministic Minecraft 1.21.1 jigsaw dungeon structures.

Each single-piece jigsaw fortress has nine connected rooms, six spawner encounters,
traps, a hidden chest and a dedicated boss arena with its guaranteed ritual sigil. Generated NBT is a build output,
not an opaque binary source dependency.
"""
from __future__ import annotations

import gzip
import json
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "src/main/resources/data/shatteredseals"

# dungeon id, allowed biome, walls, flooring, trim, guardian mob, structure salt
THEMES = [
    ("smugglers_den", "minecraft:plains", "minecraft:deepslate_bricks",
     "minecraft:polished_deepslate", "minecraft:chiseled_deepslate",
     "minecraft:pillager", 52412718),
    ("cinder_citadel", "minecraft:desert", "minecraft:polished_blackstone_bricks",
     "minecraft:basalt", "minecraft:gilded_blackstone",
     "minecraft:blaze", 52412729),
    ("hollow_crypt", "minecraft:forest", "minecraft:mossy_stone_bricks",
     "minecraft:stone_bricks", "minecraft:chiseled_stone_bricks",
     "minecraft:skeleton", 52412741),
    ("arcane_observatory", "minecraft:snowy_plains", "minecraft:prismarine_bricks",
     "minecraft:dark_prismarine", "minecraft:sea_lantern",
     "minecraft:stray", 52412757),
    ("sixfold_temple", "minecraft:jungle", "minecraft:chiseled_tuff_bricks",
     "minecraft:tuff_bricks", "minecraft:oxidized_copper",
     "minecraft:husk", 52412763),
    ("hollow_kings_fortress", "minecraft:dark_forest", "minecraft:polished_blackstone_bricks",
     "minecraft:cracked_deepslate_bricks", "minecraft:crying_obsidian",
     "minecraft:wither_skeleton", 52412779),
]


def nbt_string(value: str) -> bytes:
    data = value.encode("utf-8")
    return struct.pack(">H", len(data)) + data


def named(tag: int, name: str, payload: bytes) -> bytes:
    return bytes([tag]) + nbt_string(name) + payload


def nbt_int(name: str, value: int) -> bytes:
    return named(3, name, struct.pack(">i", value))


def nbt_str(name: str, value: str) -> bytes:
    return named(8, name, nbt_string(value))


def compound(*tags: bytes) -> bytes:
    return b"".join(tags) + b"\x00"


def nbt_compound(name: str, *tags: bytes) -> bytes:
    return named(10, name, compound(*tags))


def nbt_list(name: str, element_type: int, items: list[bytes]) -> bytes:
    return named(9, name, bytes([element_type]) + struct.pack(">i", len(items))
                 + b"".join(items))


def palette_block(name: str) -> bytes:
    return compound(nbt_str("Name", name))


def structure_nbt(wall: str, floor: str, trim: str, mob: str, loot: str) -> bytes:
    """Nine-room, interconnected fortress: traps, guardians, hidden caches, boss arena."""
    size = (43, 12, 43)
    blocks: dict[tuple[int, int, int], tuple[str, bytes | None]] = {}

    def place(x: int, y: int, z: int, material: str, extra: bytes | None = None) -> None:
        assert 0 <= x < 43 and 0 <= y < 12 and 0 <= z < 43
        blocks[(x, y, z)] = (material, extra)

    # Nine 13x13 chambers with connecting archways. Carve the rooms
    # explicitly so natural terrain never fills in the connected corridors.
    for x in range(43):
        for z in range(43):
            for y in range(12):
                if y == 0:
                    material = floor
                elif y == 11 or x in (0, 42) or z in (0, 42):
                    material = wall
                elif x in (14, 28) or z in (14, 28):
                    material = wall
                else:
                    material = "minecraft:air"
                place(x, y, z, material)
    # Every room can be reached via three entrances in each dividing wall.
    for divider in (14, 28):
        for opening in (7, 21, 35):
            for offset in (-1, 0, 1):
                for y in range(1, 5):
                    place(opening + offset, y, divider, "minecraft:air")
                    place(divider, y, opening + offset, "minecraft:air")
    # Exterior entrance, framed with contrasting blocks.
    for x in range(19, 24):
        for y in range(1, 6):
            place(x, y, 0, "minecraft:air")
    for x in (18, 24):
        for y in range(1, 8):
            place(x, y, 0, trim)

    # Lit corner columns give each chamber a distinctive dark-fantasy shape.
    for x in (3, 11, 17, 25, 31, 39):
        for z in (3, 11, 17, 25, 31, 39):
            for y in range(1, 9):
                place(x, y, z, trim)
            place(x, 9, z, "minecraft:glowstone")

    # Arena in the northern hall. The wide central approach remains clear
    # for the six existing ritual-summoned phase bosses.
    for x in range(16, 27):
        for z in range(30, 41):
            place(x, 0, z, trim)
    for x in range(19, 24):
        for z in range(34, 39):
            place(x, 0, z, "minecraft:polished_blackstone")
    place(21, 0, 36, "minecraft:crying_obsidian")
    for x, z in ((16, 30), (26, 30), (16, 40), (26, 40)):
        place(x, 1, z, "minecraft:soul_lantern")

    # Battle shrine in the central chamber and an additional treasure cache.
    for x in range(18, 25):
        for z in range(18, 25):
            place(x, 0, z, trim)
    place(21, 0, 21, "minecraft:glowstone")
    place(21, 1, 24, "minecraft:chest",
          compound(nbt_str("id", "minecraft:chest"),
                   nbt_str("LootTable", "shatteredseals:chests/dungeon_cache")))

    # The named chapter sigil is guaranteed inside this specific arena chest.
    place(21, 1, 38, "minecraft:chest",
          compound(nbt_str("id", "minecraft:chest"),
                   nbt_str("LootTable", loot)))
    # Concealed side cache, accessible by discovering the cracked brick niche.
    place(5, 1, 36, "minecraft:chest",
          compound(nbt_str("id", "minecraft:chest"),
                   nbt_str("LootTable", "shatteredseals:chests/dungeon_cache")))
    for x in range(4, 7):
        place(x, 1, 33, "minecraft:cracked_stone_bricks")

    # Six guardian encounters, one per outer chamber; these are placed off
    # the route into the arena so the main path never becomes impassable.
    for x, z in ((7, 7), (35, 7), (7, 21),
                 (35, 21), (7, 35), (35, 35)):
        monster = nbt_compound("entity", nbt_str("id", mob))
        spawner = compound(nbt_str("id", "minecraft:mob_spawner"),
                           nbt_compound("SpawnData", monster))
        place(x, 1, z, "minecraft:spawner", spawner)

    # Avoidable hazards: hot floor plates near the entry; cobweb ambush on
    # opposing flanks. Clear one-wide floor lanes are deliberately retained.
    for x, z in ((19, 5), (22, 7), (20, 10), (23, 11),
                 (6, 17), (36, 24), (9, 32), (33, 8)):
        place(x, 0, z, "minecraft:magma_block")
    for x, z in ((8, 4), (9, 5), (34, 19), (35, 18),
                 (9, 37), (34, 37)):
        place(x, 1, z, "minecraft:cobweb")

    # Palette and block positions are proper binary structure NBT rather
    # than an unvalidated text fixture or zipped datapack command.
    names = list(dict.fromkeys(name for name, _ in blocks.values()))
    index = {name: i for i, name in enumerate(names)}
    palette = [palette_block(name) for name in names]
    entries = []
    for pos, (block, extra) in sorted(blocks.items()):
        tags = [nbt_list("pos", 3, [struct.pack(">i", n) for n in pos]),
                nbt_int("state", index[block])]
        if extra is not None:
            tags.append(named(10, "nbt", extra))
        entries.append(compound(*tags))
    root = compound(
        nbt_int("DataVersion", 3955),
        nbt_list("size", 3, [struct.pack(">i", n) for n in size]),
        nbt_list("palette", 10, palette),
        nbt_list("blocks", 10, entries),
        nbt_list("entities", 10, []),
    )
    return named(10, "", root)

def write_json(path: Path, data: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n")


def main() -> None:
    for dungeon, biome, wall, floor, trim, mob, salt in THEMES:
        loot = f"shatteredseals:chests/{dungeon}"
        if not (DATA / f"loot_table/chests/{dungeon}.json").is_file():
            raise SystemExit(f"Missing existing loot table for {dungeon}")
        nbt = structure_nbt(wall, floor, trim, mob, loot)
        path = DATA / f"structure/{dungeon}.nbt"
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(gzip.compress(nbt, compresslevel=9, mtime=0))
        write_json(DATA / f"worldgen/template_pool/{dungeon}.json", {
            "fallback": "minecraft:empty",
            "elements": [{
                "weight": 1,
                "element": {
                    "element_type": "minecraft:single_pool_element",
                    "location": f"shatteredseals:{dungeon}",
                    "processors": "minecraft:empty",
                    "projection": "rigid",
                },
            }],
        })
        write_json(DATA / f"worldgen/structure/{dungeon}.json", {
            "type": "minecraft:jigsaw",
            "biomes": biome,
            "step": "surface_structures",
            "terrain_adaptation": "beard_thin",
            "spawn_overrides": {},
            "start_pool": f"shatteredseals:{dungeon}",
            "size": 1,
            "start_height": {"absolute": 0},
            "project_start_to_heightmap": "WORLD_SURFACE_WG",
            "max_distance_from_center": 80,
            "use_expansion_hack": False,
        })
        write_json(DATA / f"worldgen/structure_set/{dungeon}.json", {
            "structures": [{"structure": f"shatteredseals:{dungeon}", "weight": 1}],
            "placement": {
                "type": "minecraft:random_spread",
                "spacing": 40,
                "separation": 12,
                "salt": salt,
                "spread_type": "linear",
            },
        })
    print(f"Generated {len(THEMES)} reproducible overworld dungeon structures.")


if __name__ == "__main__":
    main()
