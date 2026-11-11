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
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.GameType;
import net.minecraft.world.SimpleContainer;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Real Minecraft 1.21.1 grindstone menu regression, not just unit tests
 * that artificially supply inputs which the menu refuses to accept.
 */
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
    public static void sequentialExtractionPreservesItemAndAllOtherEnchantments(GameTestHelper helper) {
        var soul = enchant(helper, "soul_reaper");
        var frost = enchant(helper, "frostbite");
        ItemStack gear = new ItemStack(Items.DIAMOND_SWORD);
        gear.enchant(soul, 3);
        gear.enchant(frost, 2);
        gear.setDamageValue(99);

        for (int n = 0; n < 2; n++) {
            var preview = new GrindstoneEvent.OnPlaceItem(gear, ItemStack.EMPTY, -1);
            GrindstoneEnchantmentRecovery.onInputChange(preview);
            ItemStack book = preview.getOutput();
            if (!book.is(Items.ENCHANTED_BOOK) || preview.getXp() != 0
                    || rank(book, soul, true) + rank(book, frost, true) == 0
                    || book.get(DataComponents.STORED_ENCHANTMENTS).size() != 1) {
                helper.fail("Single-item grindstone preview didn't return original custom enchantment book");
                return;
            }
            int before = (rank(gear, soul, false) > 0 ? 1 : 0)
                       + (rank(gear, frost, false) > 0 ? 1 : 0);
            var taken = new GrindstoneEvent.OnTakeItem(gear, ItemStack.EMPTY, -1);
            GrindstoneEnchantmentRecovery.onOutputTaken(taken);
            gear = taken.getNewTopItem();
            int after = (rank(gear, soul, false) > 0 ? 1 : 0)
                      + (rank(gear, frost, false) > 0 ? 1 : 0);
            if (before - after != 1 || gear.getDamageValue() != 99
                    || !taken.getNewBottomItem().isEmpty() || taken.getXp() != 0) {
                helper.fail("Grindstone duplicated an enchant or lost the input item");
                return;
            }
        }

        if (rank(gear, soul, false) != 0 || rank(gear, frost, false) != 0
                || gear.getDamageValue() != 99) {
            helper.fail("Two-step extraction failed to return all enchantments");
            return;
        }
        var empty = new GrindstoneEvent.OnPlaceItem(gear, ItemStack.EMPTY, -1);
        GrindstoneEnchantmentRecovery.onInputChange(empty);
        if (!empty.getOutput().isEmpty()) {
            helper.fail("Grindstone can re-extract an enchantment after it has been removed");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void reverseInputAndVanillaTwoItemRepairRemainUnaffected(GameTestHelper helper) {
        var soul = enchant(helper, "soul_edge");
        ItemStack tool = new ItemStack(Items.DIAMOND_AXE);
        tool.enchant(soul, 2);
        var preview = new GrindstoneEvent.OnPlaceItem(ItemStack.EMPTY, tool, -1);
        GrindstoneEnchantmentRecovery.onInputChange(preview);
        if (rank(preview.getOutput(), soul, true) != 2) {
            helper.fail("Reverse input cannot return exact-level custom book");
            return;
        }
        var taken = new GrindstoneEvent.OnTakeItem(ItemStack.EMPTY, tool, -1);
        GrindstoneEnchantmentRecovery.onOutputTaken(taken);
        if (!taken.getNewTopItem().isEmpty()
                || rank(taken.getNewBottomItem(), soul, false) != 0
                || !taken.getNewBottomItem().is(Items.DIAMOND_AXE)) {
            helper.fail("Reverse input consumed equipment or retained removed enchantment");
            return;
        }

        // Two item input must not intercept vanilla repairs or combine recipes.
        var normal = new GrindstoneEvent.OnPlaceItem(tool, new ItemStack(Items.DIAMOND_AXE), -1);
        GrindstoneEnchantmentRecovery.onInputChange(normal);
        if (!normal.getOutput().isEmpty() || normal.getXp() != -1
                || GrindstoneEnchantmentRecovery.select(tool, new ItemStack(Items.DIAMOND_AXE)) != null) {
            helper.fail("Custom recovery overrides vanilla two-item grindstone behavior");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void realGrindstoneMenuAcceptsToolAndProducesBook(GameTestHelper helper) {
        // This test catches the original bug: a plain book is NOT an accepted
        // input for vanilla GrindstoneMenu, so event-only tests were insufficient.
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var access = ContainerLevelAccess.create(helper.getLevel(),
            helper.absolutePos(new net.minecraft.core.BlockPos(1, 1, 1)));
        var menu = new GrindstoneMenu(17, player.getInventory(), access);
        var soul = enchant(helper, "soul_edge");
        ItemStack weapon = new ItemStack(Items.DIAMOND_SWORD);
        weapon.enchant(soul, 2);

        if (menu.getSlot(1).mayPlace(new ItemStack(Items.BOOK))) {
            helper.fail("Unexpected vanilla menu behavior: ordinary book became a valid input");
            return;
        }
        if (!menu.getSlot(0).mayPlace(weapon)) {
            helper.fail("Grindstone unexpectedly rejects custom enchanted gear");
            return;
        }

        menu.getSlot(0).set(weapon);
        menu.slotsChanged(new SimpleContainer(2));
        ItemStack book = menu.getSlot(2).getItem();
        if (!book.is(Items.ENCHANTED_BOOK) || rank(book, soul, true) != 2) {
            helper.fail("Real grindstone menu did not display the recovered book");
            return;
        }

        menu.getSlot(2).onTake(player, book);
        ItemStack changedGear = menu.getSlot(0).getItem();
        if (!changedGear.is(Items.DIAMOND_SWORD)
                || rank(changedGear, soul, false) != 0) {
            helper.fail("Real output click failed to transfer enchantment and preserve gear");
            return;
        }
        helper.succeed();
    }

    private GrindstoneRecoveryGameTests() {}
}
