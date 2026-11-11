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
 * Recover Shattered Seals enchantments at their actual saved level, one at a
 * time, using an enchanted piece of equipment and one ordinary book.
 *
 * The normal one-item grindstone recipe remains unchanged when no plain book
 * is supplied. Unlike the vanilla destructive disenchant, extraction transfers
 * a single enchantment from the original gear to the output enchanted book.
 * Returning the modified gear through OnTakeItem ensures that neither normal
 * clicks nor shift-clicks can duplicate the enchantment.
 *
 * The output slot itself holds the recovered book. No delayed item drops or
 * inventory insertion are needed, so a full inventory cannot silently destroy
 * books. Extraction awards no grindstone XP to prevent repeated XP farming.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class GrindstoneEnchantmentRecovery {
    public record Extraction(boolean gearOnTop, Holder<Enchantment> enchantment,
                             int level) {}

    /**
     * Both slots must be present: one plain book (can be a stack) and one
     * singly stacked enchanted item. No normal grindstone operation is
     * changed unless the explicit extraction recipe is present.
     */
    public static Extraction select(ItemStack top, ItemStack bottom) {
        if (top.is(Items.BOOK)) return find(bottom, false);
        if (bottom.is(Items.BOOK)) return find(top, true);
        return null;
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
            // The same item always offers the same first enchant until it is
            // actually removed, even if the registry iteration order changes.
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

    private static ItemStack consumeOneBook(ItemStack books) {
        ItemStack remaining = books.copy();
        remaining.shrink(1);
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
    }

    @SubscribeEvent
    public static void onInputChange(GrindstoneEvent.OnPlaceItem event) {
        Extraction extraction = select(event.getTopItem(), event.getBottomItem());
        if (extraction == null) return;
        event.setOutput(extractedBook(extraction));
        // The enchantment is transferred, not destroyed for vanilla XP.
        event.setXp(0);
    }

    @SubscribeEvent
    public static void onOutputTaken(GrindstoneEvent.OnTakeItem event) {
        Extraction extraction = select(event.getTopItem(), event.getBottomItem());
        if (extraction == null) return;

        ItemStack tool = extraction.gearOnTop()
            ? event.getTopItem() : event.getBottomItem();
        ItemStack books = extraction.gearOnTop()
            ? event.getBottomItem() : event.getTopItem();

        ItemStack changedTool = remainingGear(tool, extraction);
        ItemStack booksLeft = consumeOneBook(books);
        if (extraction.gearOnTop()) {
            event.setNewTopItem(changedTool);
            event.setNewBottomItem(booksLeft);
        } else {
            event.setNewTopItem(booksLeft);
            event.setNewBottomItem(changedTool);
        }
        // OnTake only runs for a collected output. A failed shift-click never
        // consumes either input. No custom player inventory manipulation.
        event.setXp(0);
    }

    private GrindstoneEnchantmentRecovery() {}
}
