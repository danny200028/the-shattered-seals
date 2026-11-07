#!/usr/bin/env python3
"""Automated structural regression tests for The Shattered Seals release."""
from pathlib import Path
import re
import unittest

ROOT=Path(__file__).resolve().parents[1]
FUNCTIONS=ROOT/"src/main/resources/data/shatteredseals/function"

def source(name):
    p=FUNCTIONS/(name+".mcfunction")
    if not p.is_file():
        raise AssertionError(f"Missing function: {name}")
    return p.read_text()

class ReleaseTests(unittest.TestCase):
    def test_required_gameplay_functions(self):
        for name in ("progression/tick","progression/level_up","progression/level_cap",
                     "progression/level_up_apply","progression/reset","progression/reset_confirm",
                     "progression/reset_apply","progression/master_up","progression/recalculate",
                     "talents/master_buy"):
            with self.subTest(name=name):
                source(name)

    def test_level_60_cap(self):
        level=source("progression/level_up")
        self.assertIn("ss_level matches ..59",level)
        self.assertIn("progression/level_up_apply",level)
        cap=source("progression/level_cap")
        self.assertIn("ss_level 60",cap)
        self.assertIn("ss_xp 0",cap)
        self.assertIn("ss_points 3",source("progression/level_up_apply"))

    def test_egg_only_consumed_on_confirmation(self):
        self.assertIn("ss_respec matches 1",source("progression/reset"))
        self.assertIn("ss_respec matches 2",source("progression/reset"))
        self.assertNotIn("clear @s minecraft:dragon_egg",source("progression/reset"))
        confirm=source("progression/reset_confirm")
        self.assertIn("execute store success score @s ss_egg run clear @s minecraft:dragon_egg 1",confirm)
        self.assertIn("execute if score @s ss_egg matches 1 run function shatteredseals:progression/reset_apply",confirm)

    def test_mastery_refund_ledger(self):
        reset=source("progression/reset_apply")
        self.assertIn("ss_points += @s ss_spent",reset)
        self.assertIn("scoreboard players set @s ss_spent 0",reset)
        for i in range(1,13):
            cls=(i-1)//3+1
            branch=(i-1)%3+1
            objective=f"ss_a{cls}{branch}"
            with self.subTest(mastery=i):
                apply=source(f"talents/master_apply_{i}")
                self.assertIn("scoreboard players remove @s ss_points 8",apply)
                self.assertIn("scoreboard players add @s ss_spent 8",apply)
                self.assertIn(f"scoreboard players set @s {objective} 1",apply)
                self.assertIn(f"scoreboard players set @s {objective} 0",reset)

    def test_ng_plus_mastery_gate(self):
        tick=source("progression/tick")
        self.assertIn("ss_level=60..,ss_ng=1..",tick)
        up=source("progression/master_up")
        self.assertIn("scoreboard players add @s ss_master 1",up)
        self.assertIn("scoreboard players add @s ss_mpoints 1",up)

    def test_ng_mastery_perk_shop(self):
        tick=source("progression/tick")
        self.assertIn("scoreboard players enable @a ss_mperk",tick)
        self.assertIn("shatteredseals:mastery/passives",tick)
        shop=source("mastery/shop")
        refund=source("progression/reset_apply")
        for perk in ("guard","haste","fury"):
            objective="ss_mp_"+perk
            with self.subTest(perk=perk):
                buy=source("mastery/buy_"+perk)
                self.assertIn("scoreboard players remove @s ss_mpoints 1",buy)
                self.assertIn(f"scoreboard players add @s {objective} 1",buy)
                self.assertIn(f"scoreboard players operation @s ss_mpoints += @s {objective}",refund)
                self.assertIn(f"scoreboard players set @s {objective} 0",refund)
                self.assertIn(f"ss_mp_{perk} matches ..4",shop)
        self.assertEqual(source("mastery/passives").count("effect give @s"),15)

    def test_ng_mastery_objective_init_and_buff_uptime(self):
        load=source("load")
        tick=source("progression/tick")
        init=source("progression/init")
        passives=source("mastery/passives")
        self.assertIn("scoreboard objectives add ss_reset_confirm dummy",load)
        for perk in ("guard","haste","fury"):
            name="ss_mp_"+perk
            self.assertIn(f"scoreboard players set @s {name} 0",init)
            self.assertIn(f"scoreboard players add @a {name} 0",tick)
            effect={"guard":"resistance","haste":"haste","fury":"strength"}[perk]
            for rank in range(1,6):
                self.assertIn(f"scoreboard players",tick)
                self.assertIn(f"ss_mp_{perk} matches {rank} run effect give @s minecraft:{effect} 6 ",passives)

    def test_reset_requires_prior_server_confirmation(self):
        reset=source("progression/reset")
        load=source("load")
        init=source("progression/init")
        self.assertIn("scoreboard objectives add ss_reset_confirm dummy",load)
        self.assertIn("scoreboard players set @s ss_reset_confirm 0",init)
        self.assertIn("ss_respec matches 1 run scoreboard players set @s ss_reset_confirm 1",reset)
        self.assertIn("ss_respec matches 2 if score @s ss_reset_confirm matches 1 run function shatteredseals:progression/reset_confirm",reset)
        self.assertIn("ss_respec matches 2 run scoreboard players set @s ss_reset_confirm 0",reset)
        gui=(ROOT/"src/main/java/com/shatteredseals/client/SkillTreeScreen.java").read_text()
        self.assertIn('confirmingReset = true; perform("ss_respec", 1);',gui)
        self.assertIn('perform("ss_respec", 2);',gui)

    def test_classic_attack_speed_and_hard_world(self):
        world_tick=source("tick")
        load=source("load")
        self.assertIn("minecraft:generic.attack_speed base set 64",world_tick)
        self.assertIn("execute if score #clock ss_clock matches 0 as @a",world_tick)
        self.assertIn("difficulty hard",load)

    def test_internal_function_references(self):
        files=list(FUNCTIONS.rglob("*.mcfunction"))
        self.assertGreater(len(files),50)
        missing=[]
        for file in files:
            for target in re.findall(r"\bfunction\s+shatteredseals:([a-z0-9_./-]+)",file.read_text()):
                if not (FUNCTIONS/(target+".mcfunction")).is_file():
                    missing.append(f"{file.relative_to(FUNCTIONS)} -> {target}")
        self.assertFalse(missing,"Broken internal function references: "+"; ".join(missing[:30]))

if __name__=="__main__":
    unittest.main(verbosity=2)
