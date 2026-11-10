"""Acceptance tests for the finished Sanctuary template."""
from collections import deque
from pathlib import Path
import sys
from unittest import TestCase, mock

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import generate_final_world as world

AIR = "minecraft:air"
STAIRS = "minecraft:polished_blackstone_brick_stairs"
CARDINAL = ((1, 0), (-1, 0), (0, 1), (0, -1))
FIRST = (range(37, 40), 0, 11, 1, "south")
SECOND = (range(42, 45), 11, 21, -1, "north")


class SanctuaryNavigationTests(TestCase):
    @classmethod
    def setUpClass(cls):
        with mock.patch.object(world, "save_structure") as writer:
            world.sanctuary()
        name, cls.size, cls.blocks, cls.entities = writer.call_args.args
        assert name == "wardens_sanctuary"

    def at(self, x, y, z):
        return self.blocks.get((x, y, z), (AIR, None))[0]

    def walkable(self, x, y, z):
        # Restrict walking surfaces to the blueprint's structural materials.
        # This excludes furniture, farmland, fluids, and the tops of railings.
        kind = self.at(x, y, z)
        return (kind in (world.BRICK, world.TRIM, world.TILE, world.TIMBER,
                         "minecraft:chiseled_deepslate", "minecraft:crying_obsidian",
                         "minecraft:polished_blackstone")
                or kind.startswith(STAIRS + "[")) and all(
            self.at(x, y + dy, z) == AIR for dy in (1, 2)
        )

    def uphill(self, lower, higher):
        x, y, z = higher
        step = self.at(x, y, z)
        if not step.startswith(STAIRS + "["):
            return False
        direction = 1 if "facing=south," in step else -1 if "facing=north," in step else 0
        return (direction != 0 and (x, z - direction) == (lower[0], lower[2])
                and self.at(lower[0], lower[1] + 3, lower[2]) == AIR)

    def reachable(self, start):
        self.assertTrue(self.walkable(*start), start)
        todo = deque([start])
        seen = {start}
        while todo:
            x, y, z = current = todo.popleft()
            for dx, dz in CARDINAL:
                for dy in (0, 1, -1):
                    target = (x + dx, y + dy, z + dz)
                    if target in seen or not self.walkable(*target):
                        continue
                    if dy == 1 and not self.uphill(current, target):
                        continue
                    if dy == -1 and not self.uphill(target, current):
                        continue
                    if dy == 0:
                        # A top landing must meet the high side of the stair.
                        if self.at(*current).startswith(STAIRS + "["):
                            direction = 1 if "facing=south," in self.at(*current) else -1
                            if (dx, dz) != (0, direction):
                                continue
                        if self.at(*target).startswith(STAIRS + "["):
                            direction = 1 if "facing=south," in self.at(*target) else -1
                            if (dx, dz) != (0, -direction):
                                continue
                    seen.add(target)
                    todo.append(target)
        return seen

    def test_full_flights_orientation_headroom_support_and_landings(self):
        for xs, floor, start, direction, facing in (FIRST, SECOND):
            for x in xs:
                for rise in range(1, 12):
                    y, z = floor + rise, start + direction * (rise - 1)
                    self.assertEqual(self.at(x, y, z),
                                     f"{STAIRS}[facing={facing},half=bottom,shape=straight,waterlogged=false]")
                    self.assertNotEqual(self.at(x, y - 1, z), AIR, (x, y, z))
                    for dy in (1, 2, 3):
                        self.assertEqual(self.at(x, y + dy, z), AIR, (x, y + dy, z))
                self.assertTrue(self.walkable(x, floor, start - direction))
                self.assertTrue(self.walkable(x, floor + 11, start + 11 * direction))
            # The sides of the stair opening are guarded on the upper floor.
            for offset in (7, 8, 9):
                for x in (xs.start - 1, xs.stop):
                    self.assertEqual(self.at(x, floor + 12, start + direction * offset), world.FENCE)

    def test_gate_rooms_and_upper_floors_are_connected_without_jumping(self):
        entrance = (40, 0, 0)
        for z in range(11):
            self.assertEqual(self.at(39, 2, z), AIR, (39, 2, z))
        reachable = self.reachable(entrance)
        counts = {0: 0, 11: 0, 22: 0}
        for index, (name, (x, floor, z)) in enumerate(world.ROOMS.items()):
            tags = ["ss_sanctuary_room", "ss_sanctuary_" + name]
            if name == "treasure_vault":
                tags.append("ss_sanctuary_chronicle_archive")
            marker = world.template_entity("minecraft:marker", x, floor + 1, z, world.entity_tags(*tags))
            self.assertEqual(self.entities[index], marker, name)
            self.assertTrue(any((x + dx, floor, z + dz) in reachable
                                for dx in range(-2, 3) for dz in range(-2, 3)), name)
            counts[floor] += 1
        self.assertEqual(counts, {0: 7, 11: 5, 22: 3})
        for target in ((38, 11, 22), (43, 11, 22), (43, 22, 10),
                       (8, 11, 24), (72, 11, 24), (25, 22, 16), (55, 22, 16)):
            self.assertTrue(target in reachable, target)
        self.assertIn(entrance, self.reachable((43, 22, 10)))

    def test_closed_exterior_decks_partitions_and_roofs(self):
        # Independent footprint expectations include the gatehouse and front setback.
        shell = {(x, z) for x in range(81) for z in range(49) if self.at(x, 0, z) != AIR}
        self.assertNotIn((0, 2), shell)
        self.assertNotIn((8, 0), shell)
        self.assertIn((40, 0), shell)
        self.assertIn((8, 2), shell)
        for x, z in shell:
            edge = any((x + dx, z + dz) not in shell for dx, dz in CARDINAL)
            if edge:
                for y in range(1, 23):
                    if z == 0 and abs(x - 40) <= 3 and y <= 7 - abs(x - 40):
                        self.assertEqual(self.at(x, y, z), AIR)
                    else:
                        self.assertNotEqual(self.at(x, y, z), AIR, (x, y, z))
            else:
                garden = 49 <= x <= 63 and 33 <= z <= 47
                first_shaft = 37 <= x <= 39 and 18 <= z <= 20
                second_shaft = 42 <= x <= 44 and 12 <= z <= 14
                self.assertEqual(self.at(x, 11, z) == AIR, garden or first_shaft, (x, z))
                turret = any(abs(x - cx) <= 3 and abs(z - cz) <= 3
                             and abs(x - cx) + abs(z - cz) <= 4
                             for cx, cz in ((8, 8), (72, 8), (8, 40), (72, 40)))
                if self.at(x, 22, z) == AIR:
                    self.assertTrue(second_shaft or turret, (x, z))
                if second_shaft:
                    self.assertEqual(self.at(x, 22, z), AIR, (x, z))
                if garden:
                    self.assertEqual(self.at(x, 22, z), "minecraft:glass", (x, z))
                if x % 16 == 0 or z % 16 == 0:
                    offset = min(abs(z % 16 - 8) if x % 16 == 0 else 8,
                                 abs(x % 16 - 8) if z % 16 == 0 else 8)
                    central = (x in (32, 48) and 16 < z < 32) or (z in (16, 32) and 32 < x < 48)
                    crown = (8 if central else 6) - offset if offset <= (5 if central else 2) else 0
                    for y in range(max(1, crown + 1), 11):
                        # The first flight and headroom intentionally cross the central arch.
                        if 37 <= x <= 39 and z == 16 and 7 <= y <= 9:
                            continue
                        self.assertNotEqual(self.at(x, y, z), AIR, (x, y, z))
        for x in range(16, 65):
            for z in range(4, 33):
                if min(x - 16, 64 - x) + min(z - 4, 32 - z) < 6:
                    continue
                roof = 39 - min(9, abs(x - 40) // 2)
                self.assertEqual(self.at(x, roof, z), world.TILE, (x, z))
                if min(x - 16, 64 - x) + min(z - 4, 32 - z) == 6 or x in (16, 64) or z in (4, 32):
                    for y in range(23, roof):
                        self.assertNotEqual(self.at(x, y, z), AIR, (x, y, z))

    def test_rooms_have_distinct_corner_profiles(self):
        # Trophy Hall chamfer, armoury curve and open workshop corner.
        self.assertEqual(self.at(6, 4, 14), AIR)
        self.assertEqual(self.at(3, 4, 15), world.BRICK)
        self.assertEqual(self.at(19, 4, 15), world.BRICK)
        self.assertEqual(self.at(35, 4, 15), AIR)
        self.assertEqual(self.at(26, 4, 15), AIR)

    def test_furnishings_and_markers_survive_in_bounds(self):
        self.assertEqual(self.size, (81, 42, 49))
        self.assertTrue(all(0 <= x < 81 and 0 <= y < 42 and 0 <= z < 49
                            for x, y, z in self.blocks))
        self.assertEqual(len(self.entities), 22)
        self.assertIn(world.entity_tags("ss_secret_arena"), self.entities[-1])
        for pos, kind in (
            ((40, 1, 24), "lodestone"), ((38, 12, 8), "enchanting_table"),
            ((68, 12, 21), "chest"), ((4, 1, 40), "furnace"),
            ((36, 12, 42), "brewing_stand"), ((72, 0, 40), "crying_obsidian"),
            ((4, 2, 4), "polished_blackstone_wall"), ((49, 23, 11), "bookshelf"),
            ((53, 0, 37), "farmland"), ((22, 23, 16), "cartography_table"), ((50, 23, 23), "chiseled_bookshelf"),
        ):
            self.assertEqual(self.at(*pos), f"minecraft:{kind}", pos)
        self.assertEqual(sum(kind == "minecraft:chest" for kind, _ in self.blocks.values()), 15)
