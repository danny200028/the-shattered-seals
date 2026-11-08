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
 * Apply a Codex book to a compatible inventory item regardless of which stack
 * was picked up first. Cancel vanilla item swaps on both logical sides, then
 * modify inventory once on the authoritative server.
 *
 * Creative inventory is clientside only, so use an anvil in creative mode.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class CodexBookApplication {
    @SubscribeEvent
    public static void useBookOnGear(ItemStackedOnOtherEvent event) {
        var player = event.getPlayer();
        if (player.containerMenu != player.inventoryMenu
                || event.getClickAction() != ClickAction.PRIMARY
                || player.isSpectator() || player.isCreative()) return;

        ItemStack cursor = event.getCarriedItem();
        ItemStack slotted = event.getStackedOnItem();
        boolean bookOnCursor = cursor.is(Items.ENCHANTED_BOOK) && !slotted.is(Items.ENCHANTED_BOOK);
        boolean bookInSlot = slotted.is(Items.ENCHANTED_BOOK) && !cursor.is(Items.ENCHANTED_BOOK);
        if (!bookOnCursor && !bookInSlot) return;

        ItemStack book = bookOnCursor ? cursor : slotted;
        ItemStack gear = bookOnCursor ? slotted : cursor;
        if (gear.isEmpty()) return;
        ItemEnchantments stored = book.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.size() != 1) return;
        var entry = stored.entrySet().iterator().next();
        Holder<Enchantment> enchant = entry.getKey();
        if (!enchant.unwrapKey().map(key -> ShatteredSeals.MOD_ID.equals(key.location().getNamespace()))
                .orElse(false)) return;

        // Prevent the normal swap while the server validates/consumes the book.
        event.setCanceled(true);
        if (player.level().isClientSide) return;

        if (!gear.supportsEnchantment(enchant)) {
            player.displayClientMessage(Component.literal(
                "That enchantment cannot be applied to this item."), true);
            return;
        }
        int bookRank = entry.getIntValue();
        int existing = EnchantmentHelper.getEnchantmentsForCrafting(gear).getLevel(enchant);
        int targetRank = existing == bookRank ? existing + 1 : Math.max(existing, bookRank);
        if (targetRank <= existing || targetRank > enchant.value().getMaxLevel()) {
            player.displayClientMessage(Component.literal(
                "Cannot improve this enchantment further. Matching books can be combined in an anvil."), true);
            return;
        }

        ItemStack upgraded = gear.copy();
        EnchantmentHelper.updateEnchantments(upgraded, mutable -> mutable.set(enchant, targetRank));
        ItemStack remaining = book.copy();
        remaining.shrink(1);
        if (bookOnCursor) {
            event.getSlot().set(upgraded);
            event.getCarriedSlotAccess().set(remaining);
        } else {
            event.getSlot().set(remaining);
            event.getCarriedSlotAccess().set(upgraded);
        }
        event.getSlot().setChanged();
        player.displayClientMessage(Component.literal(
            "Applied " + enchant.value().description().getString() + " " + targetRank + "."), true);
    }
    private CodexBookApplication() {}
}
