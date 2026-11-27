package com.shatteredseals.diagnostics;

import com.mojang.authlib.GameProfile;
import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.enchantments.DuplicateBookRecycling;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Real NeoForge tests prove duplicates recycle safely and refund actual XP. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DuplicateBookRecyclingGameTests {
    private static ServerPlayer testPlayer(GameTestHelper helper) {
        return new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
            new GameProfile(UUID.randomUUID(), "BookRecycleTest"),
            ClientInformation.createDefault()) {
            @Override public void displayClientMessage(Component message, boolean actionBar) {}
        };
    }

    private static ItemStack book(GameTestHelper helper, String id, int rank) {
        var key = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, id));
        var enchantment = helper.getLevel().registryAccess()
            .registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
        return EnchantedBookItem.createForEnchantment(
            new EnchantmentInstance(enchantment, rank));
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void duplicateCommonRefundsAndKeepsSecondBook(GameTestHelper helper) {
        ServerPlayer player = testPlayer(helper);
        player.getInventory().selected = 0;
        player.getInventory().setItem(0, book(helper, "keen_edge", 1));
        player.getInventory().setItem(1, book(helper, "keen_edge", 1));
        int beforeXp = player.totalExperience;

        if (DuplicateBookRecycling.recycle(player) != DuplicateBookRecycling.Result.RECYCLED
                || !player.getInventory().getItem(0).isEmpty()
                || player.getInventory().getItem(1).isEmpty()
                || player.totalExperience - beforeXp != DuplicateBookRecycling.COMMON_REFUND) {
            helper.fail("Duplicate common book did not refund 80 XP and retain a spare");
            return;
        }
        // A second request with no held book is rejected without extra XP.
        if (DuplicateBookRecycling.recycle(player) != DuplicateBookRecycling.Result.INVALID_BOOK
                || player.totalExperience - beforeXp != DuplicateBookRecycling.COMMON_REFUND) {
            helper.fail("Replaying the recycle request changed XP or items");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void uniqueAndWrongRankNeverConsumeBooks(GameTestHelper helper) {
        ServerPlayer player = testPlayer(helper);
        player.getInventory().selected = 0;
        player.getInventory().setItem(0, book(helper, "dragons_fury", 3));
        player.getInventory().setItem(1, book(helper, "dragons_fury", 2));
        player.getInventory().setItem(2, book(helper, "keen_edge", 3));
        int beforeXp = player.totalExperience;
        if (DuplicateBookRecycling.recycle(player) != DuplicateBookRecycling.Result.NO_DUPLICATE
                || player.getInventory().getItem(0).isEmpty()
                || player.totalExperience != beforeXp) {
            helper.fail("Different ranks or enchantments must not count as duplicates");
            return;
        }
        player.getInventory().setItem(1, book(helper, "dragons_fury", 3));
        if (DuplicateBookRecycling.recycle(player) != DuplicateBookRecycling.Result.RECYCLED
                || !player.getInventory().getItem(0).isEmpty()
                || player.getInventory().getItem(1).isEmpty()
                || player.totalExperience - beforeXp != DuplicateBookRecycling.LEGENDARY_REFUND) {
            helper.fail("Legendary duplicate must refund exactly 698 XP");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void vanillaBooksAndNonBookItemsNeverRecycle(GameTestHelper helper) {
        ServerPlayer player = testPlayer(helper);
        player.getInventory().selected = 0;
        player.getInventory().setItem(0, new ItemStack(Items.ENCHANTED_BOOK));
        player.getInventory().setItem(1, new ItemStack(Items.ENCHANTED_BOOK));
        int beforeXp = player.totalExperience;
        if (DuplicateBookRecycling.recycle(player) != DuplicateBookRecycling.Result.INVALID_BOOK
                || player.totalExperience != beforeXp
                || player.getInventory().getItem(0).isEmpty()) {
            helper.fail("Blank or vanilla-only enchanted books must not recycle");
            return;
        }
        player.getInventory().setItem(0, new ItemStack(Items.BOOK));
        if (DuplicateBookRecycling.recycle(player) != DuplicateBookRecycling.Result.INVALID_BOOK
                || player.totalExperience != beforeXp) {
            helper.fail("Ordinary books must not generate refunded XP");
            return;
        }
        helper.succeed();
    }

    private DuplicateBookRecyclingGameTests() {}
}
