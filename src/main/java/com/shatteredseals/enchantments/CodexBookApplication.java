package com.shatteredseals.enchantments;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

/**
 * Optional left-click shortcut for a Codex enchanted book held on the cursor
 * and a compatible equipment slot in the survival inventory. Anvils continue
 * to work normally, including I+I -> II and II+II -> III.
 *
 * Server performs all validation; the client only suppresses the vanilla
 * cursor swap while awaiting its authoritative inventory update.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class CodexBookApplication {
    @SubscribeEvent
    public static void useBookOnGear(ItemStackedOnOtherEvent event) {
        var player = event.getPlayer();
        if (player.containerMenu != player.inventoryMenu
                || event.getClickAction() != ClickAction.PRIMARY
                || player.isSpectator() || player.isCreative()) return;
        ItemStack book = event.getCarriedItem();
        ItemStack gear = event.getStackedOnItem();
        if (!book.is(Items.ENCHANTED_BOOK) || gear.isEmpty() || gear.is(Items.ENCHANTED_BOOK)) return;
        ItemEnchantments stored = book.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.size() != 1) return;
        var entry = stored.entrySet().iterator().next();
        Holder<Enchantment> enchant = entry.getKey();
        if (!enchant.unwrapKey().map(key -> ShatteredSeals.MOD_ID.equals(key.location().getNamespace()))
                .orElse(false)) return;
        if (!gear.supportsEnchantment(enchant)) return;

        int bookRank = entry.getIntValue();
        int existing = EnchantmentHelper.getEnchantmentsForCrafting(gear).getLevel(enchant);
        int targetRank = existing == bookRank ? existing + 1 : Math.max(existing, bookRank);
        event.setCanceled(true);
        if (player.level().isClientSide) return;
        if (targetRank <= existing || targetRank > enchant.value().getMaxLevel()) {
            player.displayClientMessage(Component.literal(
                "Cannot upgrade this enchantment. Combine equal-ranked books in an anvil first."), true);
            return;
        }
        ItemStack upgraded = gear.copy();
        EnchantmentHelper.updateEnchantments(upgraded, mutable -> mutable.set(enchant, targetRank));
        event.getSlot().set(upgraded);
        // Book was already purchased for 10/20/30 vanilla levels.
        // Never charge again or silently delete an incompatible book.
        ItemStack remaining = book.copy();
        remaining.shrink(1);
        event.getCarriedSlotAccess().set(remaining);
        event.getSlot().setChanged();
        player.displayClientMessage(Component.literal(
            "Applied " + enchant.value().description().getString() + " " + targetRank + "."), true);
    }

    private CodexBookApplication() {}
}
