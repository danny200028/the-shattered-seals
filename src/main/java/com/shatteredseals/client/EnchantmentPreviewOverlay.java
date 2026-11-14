package com.shatteredseals.client;

import com.mojang.datafixers.util.Either;
import com.shatteredseals.net.TableEnchantPreview;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

/**
 * Replace the vanilla ONE-enchantment clue inside its OWN tooltip, rather than
 * rendering a second tooltip that overlaps it on small and large GUI scales.
 *
 * A single enchantment can legitimately be the complete vanilla result.
 * Never fabricate additional enchants to make a list seem longer.
 */
@EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT)
public final class EnchantmentPreviewOverlay {
    private static TableEnchantPreview current;

    public static void accept(TableEnchantPreview packet) {
        current = packet;
    }

    @SubscribeEvent
    public static void gather(RenderTooltipEvent.GatherComponents event) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof EnchantmentScreen screen)
                || mc.player == null
                || !(mc.player.containerMenu instanceof EnchantmentMenu menu)
                // Enchanting clues are non-item tooltips; leave inventory item tooltips alone.
                || !event.getItemStack().isEmpty()) return;

        TableEnchantPreview preview = current;
        if (preview == null || preview.menuId() != menu.containerId
                || preview.itemId() != Item.getId(menu.slots.get(0).getItem().getItem())
                // DataSlot sync exposes only the lower sixteen bits of the seed.
                || (preview.seed() & 0xFFFF) != (menu.getEnchantmentSeed() & 0xFFFF)) return;
        for (int i = 0; i < 3; i++) {
            if (menu.costs[i] != preview.costs()[i]) return;
        }

        // GatherComponents lacks mouse coordinates; obtain GUI-scaled coordinates.
        int mouseX = (int) (mc.mouseHandler.xpos()
            * (double) mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
        int mouseY = (int) (mc.mouseHandler.ypos()
            * (double) mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
        int left = (screen.width - 176) / 2;
        int top = (screen.height - 166) / 2;
        for (int slot = 0; slot < 3; slot++) {
            int rowY = top + 14 + slot * 19;
            if (mouseX < left + 60 || mouseX >= left + 168
                    || mouseY < rowY || mouseY >= rowY + 19
                    || menu.costs[slot] <= 0) continue;
            String names = preview.enchantments()[slot];
            if (names.isBlank()) return;

            List<String> enchants = names.lines().filter(name -> !name.isBlank()).toList();
            if (enchants.isEmpty()) return;

            // Replace the existing vanilla tooltip rather than adding a second
            // one on top of it. Minecraft will handle its size and placement.
            List<Either<FormattedText, TooltipComponent>> lines = event.getTooltipElements();
            lines.clear();
            lines.add(Either.left(Component.literal(
                "Enchantments (" + enchants.size() + ")").withStyle(ChatFormatting.GOLD)));
            for (String enchant : enchants) {
                lines.add(Either.left(Component.literal(enchant).withStyle(ChatFormatting.AQUA)));
            }
            if (enchants.size() == 1) {
                lines.add(Either.left(Component.literal("Only one enchantment rolled.")
                    .withStyle(ChatFormatting.DARK_GRAY)));
            }
            lines.add(Either.left(Component.literal("Requires level " + menu.costs[slot])
                .withStyle(ChatFormatting.GRAY)));
            lines.add(Either.left(Component.literal("Costs " + (slot + 1)
                + " XP level" + (slot == 0 ? "" : "s") + " + "
                + (slot + 1) + " lapis").withStyle(ChatFormatting.GRAY)));
            event.setMaxWidth(Math.min(240, Math.max(110, event.getScreenWidth() - 20)));
            return;
        }
    }

    private EnchantmentPreviewOverlay() {}
}
