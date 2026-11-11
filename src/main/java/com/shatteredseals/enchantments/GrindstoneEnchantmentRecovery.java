package com.shatteredseals.enchantments;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.GrindstoneEvent;

/**
 * Genuine in-game grindstone enchant extraction for Minecraft 1.21.1.
 *
 * Vanilla grindstone INPUT slots do NOT accept ordinary books. The earlier
 * tool + blank book recipe therefore could not actually be entered in-game,
 * despite its directly constructed event tests passing.
 *
 * New workflow: place exactly ONE custom-enchanted item in EITHER input slot;
 * leave the other slot EMPTY. The output becomes a single genuine enchanted
 * book at the original enchantment level, with zero XP. Taking it consumes
 * exactly that one enchantment from the tool while leaving the tool in its
 * input slot, including its damage, name, and any other enchantments.
 * Repeat until all custom enchants have been extracted.
 *
 * This method uses only items which vanilla lets into a grindstone. No item
 * dropping, player-inventory insertion, or hidden currency is needed. Normal
 * vanilla grindstone behavior is retained for items with no custom enchants
 * and for two-item repair operations.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class GrindstoneEnchantmentRecovery {
    public record Extraction(boolean gearOnTop, Holder<Enchantment> enchantment,
                             int level) {}

    /**
     * Input validation mirrors what the vanilla grindstone UI permits: one
     * piece of enchanted gear, and nothing at all in the other input slot.
     */
    public static Extraction select(ItemStack top, ItemStack bottom) {
        if (top.isEmpty()) return find(bottom, false);
        if (bottom.isEmpty()) return find(top, true);
        return null; // Never intercept two-item vanilla repair combinations.
    }

    private static Extraction find(ItemStack gear, boolean gearOnTop) {
        if (gear.isEmpty() || gear.getCount() != 1 || gear.is(Items.ENCHANTED_BOOK)
                || gear.is(Items.BOOK)) return null;
        ItemEnchantments enchantments = gear.getOrDefault(
            DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        Holder<Enchantment> selected = null;
        int level = 0;
        String firstId = null;
        for (var entry : enchantments.entrySet()) {
            var key = entry.getKey().unwrapKey().orElse(null);
            if (key == null || !ShatteredSeals.MOD_ID.equals(key.location().getNamespace())
                    || entry.getIntValue() <= 0) continue;
            String id = key.location().toString();
            // Stable extraction order, independent of registry iteration order.
            if (firstId == null || id.compareTo(firstId) < 0) {
                firstId = id;
                selected = entry.getKey();
                level = entry.getIntValue();
            }
        }
        return selected == null ? null : new Extraction(gearOnTop, selected, level);
    }

    public static ItemStack extractedBook(Extraction extraction) {
        return EnchantedBookItem.createForEnchantment(
            new EnchantmentInstance(extraction.enchantment(), extraction.level()));
    }

    public static ItemStack remainingGear(ItemStack gear, Extraction extraction) {
        ItemStack copy = gear.copy();
        EnchantmentHelper.updateEnchantments(copy, mutable ->
            mutable.removeIf(enchantment -> enchantment.equals(extraction.enchantment())));
        return copy;
    }

    @SubscribeEvent
    public static void onInputChange(GrindstoneEvent.OnPlaceItem event) {
        Extraction extraction = select(event.getTopItem(), event.getBottomItem());
        if (extraction == null) return;
        event.setOutput(extractedBook(extraction));
        // The enchantment is recovered rather than destroyed, so no vanilla XP.
        event.setXp(0);
    }

    @SubscribeEvent
    public static void onOutputTaken(GrindstoneEvent.OnTakeItem event) {
        Extraction extraction = select(event.getTopItem(), event.getBottomItem());
        if (extraction == null) return;

        ItemStack original = extraction.gearOnTop()
            ? event.getTopItem() : event.getBottomItem();
        ItemStack updated = remainingGear(original, extraction);
        if (extraction.gearOnTop()) {
            event.setNewTopItem(updated);
            event.setNewBottomItem(ItemStack.EMPTY);
        } else {
            event.setNewTopItem(ItemStack.EMPTY);
            event.setNewBottomItem(updated);
        }
        event.setXp(0);
    }

    private GrindstoneEnchantmentRecovery() {}
}
