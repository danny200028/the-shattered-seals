"""Whole-blueprint Sanctuary acceptance: navigable routes, not merely landmarks.

The previous 1.0.64 build passed sampled-block GameTests but had no real route
from the ground floor to the upper decks. This test checks the generated NBT
SOURCE layout, including all later decoration/stair passes, with a 3D walk BFS.
"""
from collections import deque
from pathlib import Path
from unittest import TestCase, mock
import sys

ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/"scripts"))
import generate_final_world as world

AIR="minecraft:air"
NOT_WALKABLE={"minecraft:water","minecraft:lava"}
STAIR="minecraft:polished_blackstone_brick_stairs[facing=north]"


class SanctuaryNavigationTests(TestCase):
    @classmethod
    def setUpClass(cls):
        with mock.patch.object(world,"save_structure") as writer:
            world.sanctuary()
        assert writer.call_count==1
        assert writer.call_args.args[0]=="wardens_sanctuary"
        cls.size=writer.call_args.args[1]
        cls.blocks=writer.call_args.args[2]
        cls.entities=writer.call_args.args[3]

    @classmethod
    def at(cls,x,y,z):
        return cls.blocks.get((x,y,z),(AIR,None))[0]

    @classmethod
    def standable(cls,x,y,z):
        if not (0<=x<81 and 0<=y<40 and 0<=z<49):
            return False
        base=cls.at(x,y,z)
        if base==AIR or base in NOT_WALKABLE or base.endswith("_fence") \
                or base.endswith("_wall") or base.endswith("_glass_pane"):
            return False
        return (cls.at(x,y+1,z)==AIR and cls.at(x,y+2,z)==AIR)

    @classmethod
    def reachable(cls,start,goal):
        if not cls.standable(*start) or not cls.standable(*goal):
            return False,0
        todo=deque([start])
        seen={start}
        while todo:
            x,y,z=todo.popleft()
            if (x,y,z)==goal:
                return True,len(seen)
            for dx,dz in ((1,0),(-1,0),(0,1),(0,-1)):
                for dy in (-1,0,1):
                    target=(x+dx,y+dy,z+dz)
                    if target not in seen and cls.standable(*target):
                        seen.add(target)
                        todo.append(target)
        return False,len(seen)

    def test_unchanged_functional_ground_level(self):
        for pos,kind in (
            ((40,1,24),"minecraft:lodestone"),
            ((38,1,8),"minecraft:enchanting_table"),
            ((68,1,21),"minecraft:chest"),
            ((4,1,40),"minecraft:furnace"),
            ((36,1,40),"minecraft:brewing_stand"),
            ((72,0,40),"minecraft:crying_obsidian"),
            ((4,2,4),"minecraft:polished_blackstone_wall"),
        ):
            self.assertEqual(self.at(*pos),kind,pos)

    def test_persistent_room_and_boss_markers(self):
        self.assertEqual(self.size,(81,42,49))
        self.assertEqual(len(self.entities),15+1+6, "15 room markers, arena, six armor stands")
        # The game adds one marker for each of the 15 rooms and one boss arena marker.
        self.assertGreater(len(self.entities),15)

    def test_no_upper_stair_obstructions(self):
        for x,base_y in ((11,0),(69,11)):
            for step in range(11):
                z=18+step
                y=base_y+1+step
                for x_column in range(x-1,x+2):
                    self.assertEqual(self.at(x_column,y,z),STAIR,
                        f"Stair missing at {(x_column,y,z)}")
                    self.assertEqual(self.at(x_column,y+1,z),AIR,
                        f"Stair head blocked at {(x_column,y+1,z)}")
                    self.assertEqual(self.at(x_column,y+2,z),AIR,
                        f"Stair head blocked at {(x_column,y+2,z)}")
            for x_column in range(x-1,x+2):
                self.assertTrue(self.standable(x_column,base_y,17),
                    f"Blocked foot of staircase {x} at ground y {base_y}")
                self.assertTrue(self.standable(x_column,base_y+11,29),
                    f"Blocked landing of staircase {x} at y {base_y+11}")

    def test_real_walk_from_gate_to_second_and_third_floor(self):
        entrance=(40,0,2)
        second=(11,11,29)
        third=(69,22,29)
        ok,n=self.reachable(entrance,second)
        self.assertTrue(ok,f"Ground to second floor UNREACHABLE; visited {n} walkable blocks")
        ok,n=self.reachable(second,third)
        self.assertTrue(ok,f"Second to third floor UNREACHABLE; visited {n} walkable blocks")

    def test_non_rectangular_footprint_and_covered_terraces(self):
        self.assertEqual(self.at(0,0,0),AIR,"Original square exterior remains")
        self.assertNotEqual(self.at(8,0,8),AIR,"Chamber floor disappeared")
        self.assertEqual(self.at(3,39,9),"minecraft:deepslate_brick_wall")
        self.assertEqual(self.at(55,32,40),"minecraft:glass")
        self.assertEqual(self.at(40,39,20),"minecraft:deepslate_tiles")
        self.assertTrue(self.standable(40,22,24),"Upper atrium bridge blocked")


if __name__=="__main__":
    import unittest
    unittest.main()
