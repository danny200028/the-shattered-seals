#!/usr/bin/env python3
"""Generate six deterministic Minecraft 1.21.1 jigsaw dungeon structures.

Single-piece templates use the vanilla jigsaw structure registry and contain
real loot-table chests and monster spawners. Generated NBT is a build output,
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
    size = (21, 10, 21)
    blocks: dict[tuple[int, int, int], tuple[str, bytes | None]] = {}
    # Place air explicitly so uneven landscape is carved away inside the room.
    for x in range(21):
        for z in range(21):
            for y in range(10):
                if y == 0:
                    block = floor
                elif y == 9:
                    block = wall
                elif x in (0, 20) or z in (0, 20):
                    block = wall
                else:
                    block = "minecraft:air"
                blocks[(x, y, z)] = (block, None)
    # Raised corner pillars, lit caps and deliberately unobstructed doorway.
    for x in (3, 17):
        for z in (3, 17):
            for y in range(1, 9):
                blocks[(x, y, z)] = (trim, None)
            blocks[(x, 8, z)] = ("minecraft:glowstone", None)
    for x in range(9, 12):
        for y in range(1, 5):
            blocks[(x, y, 0)] = ("minecraft:air", None)
    # Central boss ritual floor, loot pedestal and four guarded wings.
    for x in range(7, 14):
        for z in range(7, 14):
            blocks[(x, 0, z)] = (trim, None)
    loot_nbt = compound(nbt_str("id", "minecraft:chest"),
                        nbt_str("LootTable", loot))
    blocks[(10, 1, 16)] = ("minecraft:chest", loot_nbt)
    for x, z in ((5, 5), (15, 5), (5, 15), (15, 15)):
        monster = nbt_compound("entity", nbt_str("id", mob))
        spawner = compound(nbt_str("id", "minecraft:mob_spawner"),
                           nbt_compound("SpawnData", monster))
        blocks[(x, 1, z)] = ("minecraft:spawner", spawner)

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
