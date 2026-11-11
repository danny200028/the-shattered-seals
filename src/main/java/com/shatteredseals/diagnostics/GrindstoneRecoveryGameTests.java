package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.enchantments.GrindstoneEnchantmentRecovery;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Real NeoForge grindstone events against the game enchantment registry. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GrindstoneRecoveryGameTests {
    private static Holder<Enchantment> enchant(GameTestHelper helper, String name) {
        var key = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, name));
        return helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
            .getHolderOrThrow(key);
    }

    private static int rank(ItemStack item, Holder<Enchantment> enchantment, boolean book) {
        return item.getOrDefault(book
            ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS,
            ItemEnchantments.EMPTY).getLevel(enchantment);
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void twoLevelThreeBooksAndToolPreservation(GameTestHelper helper) {
        var soul = enchant(helper, "soul_reaper");
        var frost = enchant(helper, "frostbite");
        ItemStack tool = new ItemStack(Items.DIAMOND_SWORD);
        tool.enchant(soul, 3);
        tool.enchant(frost, 2);
        tool.setDamageValue(99);
        ItemStack books = new ItemStack(Items.BOOK, 2);

        for (int n = 0; n < 2; n++) {
            GrindstoneEvent.OnPlaceItem preview = new GrindstoneEvent.OnPlaceItem(tool, books, -1);
            GrindstoneEnchantmentRecovery.onInputChange(preview);
            ItemStack result = preview.getOutput();
            if (!result.is(Items.ENCHANTED_BOOK) || preview.getXp() != 0
                    || rank(result, soul, true) + rank(result, frost, true) == 0
                    || result.get(DataComponents.STORED_ENCHANTMENTS).size() != 1) {
                helper.fail("Grindstone failed to preview one true-level custom book");
                return;
            }
            int beforeCount = (rank(tool, soul, false) > 0 ? 1 : 0)
                            + (rank(tool, frost, false) > 0 ? 1 : 0);
            GrindstoneEvent.OnTakeItem take = new GrindstoneEvent.OnTakeItem(tool, books, -1);
            GrindstoneEnchantmentRecovery.onOutputTaken(take);
            tool = take.getNewTopItem();
            books = take.getNewBottomItem();
            int afterCount = (rank(tool, soul, false) > 0 ? 1 : 0)
                           + (rank(tool, frost, false) > 0 ? 1 : 0);
            if (beforeCount - afterCount != 1 || tool.getDamageValue() != 99
                    || take.getXp() != 0) {
                helper.fail("Taking the book failed to consume exactly one enchantment");
                return;
            }
        }
        if (rank(tool, soul, false) != 0 || rank(tool, frost, false) != 0
                || !books.isEmpty() || tool.getDamageValue() != 99) {
            helper.fail("Extraction duplicated enchantments or lost the original tool");
            return;
        }
        GrindstoneEvent.OnPlaceItem exhausted = new GrindstoneEvent.OnPlaceItem(
            tool, new ItemStack(Items.BOOK), -1);
        GrindstoneEnchantmentRecovery.onInputChange(exhausted);
        if (!exhausted.getOutput().isEmpty()) {
            helper.fail("The last enchantment could be copied repeatedly");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void inputSlotsAndVanillaOperationsRemainSafe(GameTestHelper helper) {
        var soul = enchant(helper, "soul_edge");
        ItemStack tool = new ItemStack(Items.DIAMOND_AXE);
        tool.enchant(soul, 2);
        ItemStack books = new ItemStack(Items.BOOK, 4);
        GrindstoneEvent.OnPlaceItem preview = new GrindstoneEvent.OnPlaceItem(books, tool, -1);
        GrindstoneEnchantmentRecovery.onInputChange(preview);
        if (rank(preview.getOutput(), soul, true) != 2) {
            helper.fail("Reversed slots failed to keep the original enchantment level");
            return;
        }
        GrindstoneEvent.OnTakeItem take = new GrindstoneEvent.OnTakeItem(books, tool, -1);
        GrindstoneEnchantmentRecovery.onOutputTaken(take);
        if (take.getNewTopItem().getCount() != 3
                || rank(take.getNewBottomItem(), soul, false) != 0
                || !take.getNewBottomItem().is(Items.DIAMOND_AXE)) {
            helper.fail("Reversed slots lost the tool or more than one book");
            return;
        }

        // Without an ordinary book, the vanilla grindstone behavior must run.
        var normal = new GrindstoneEvent.OnPlaceItem(tool, ItemStack.EMPTY, -1);
        GrindstoneEnchantmentRecovery.onInputChange(normal);
        if (!normal.getOutput().isEmpty() || normal.getXp() != -1
                || GrindstoneEnchantmentRecovery.select(tool, ItemStack.EMPTY) != null) {
            helper.fail("Extraction broke normal vanilla disenchanting");
            return;
        }
        helper.succeed();
    }

    private GrindstoneRecoveryGameTests() {}
}
