package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.combat.SwordMastery;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Protects six cosmetic-only reward unlocks and pre-existing sword mastery rules. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SwordMasteryCosmeticGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void masteryThresholdsAreUnchanged(GameTestHelper helper) {
        if (SwordMastery.rank(0) != 0 || SwordMastery.rank(7) != 0
                || SwordMastery.rank(8) != 1 || SwordMastery.rank(19) != 1
                || SwordMastery.rank(20) != 2 || SwordMastery.rank(39) != 2
                || SwordMastery.rank(40) != 3 || SwordMastery.rank(69) != 3
                || SwordMastery.rank(70) != 4 || SwordMastery.rank(109) != 4
                || SwordMastery.rank(110) != 5 || SwordMastery.rank(10000) != 5
                || SwordMastery.hasCosmetics(109) || !SwordMastery.hasCosmetics(110)) {
            helper.fail("Existing five-rank progression or rank-five gate changed");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void eachSwordEarnsAUniqueTitle(GameTestHelper helper) {
        Set<String> titles = new HashSet<>();
        Set<String> ids = new HashSet<>();
        if (SwordMastery.swordCount() != 6) {
            helper.fail("All six legendary blades must have cosmetic unlocks");
            return;
        }
        for (int i = 0; i < SwordMastery.swordCount(); i++) {
            if (!titles.add(SwordMastery.title(i)) || SwordMastery.title(i).isBlank()
                    || !ids.add(SwordMastery.swordId(i))) {
                helper.fail("Each legendary sword must have a unique ID and earned title");
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void onlyRealLegendarySwordsEnableCosmetics(GameTestHelper helper) {
        if (SwordMastery.swordIndex(new ItemStack(Items.DIAMOND_SWORD)) != -1) {
            helper.fail("Ordinary swords must not trigger mastery cosmetics");
            return;
        }
        for (int i = 0; i < SwordMastery.swordCount(); i++) {
            for (String tier : new String[]{"dormant", "awakened", "ascended"}) {
                ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
                CompoundTag tag = new CompoundTag();
                tag.putString("ss_weapon", SwordMastery.swordId(i));
                tag.putString("ss_tier", tier);
                sword.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                if (SwordMastery.swordIndex(sword) != i) {
                    helper.fail("Mastery sword lookup failed for " + SwordMastery.swordId(i));
                    return;
                }
            }
        }
        ItemStack fake = new ItemStack(Items.DIAMOND_SWORD);
        CompoundTag tag = new CompoundTag();
        tag.putString("ss_weapon", SwordMastery.swordId(0));
        tag.putString("ss_tier", "mythic");
        fake.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        if (SwordMastery.swordIndex(fake) != -1) {
            helper.fail("Invalid sword tiers must not activate cosmetics");
            return;
        }
        helper.succeed();
    }
    private SwordMasteryCosmeticGameTests() {}
}
