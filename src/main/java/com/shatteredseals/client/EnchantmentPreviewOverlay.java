package com.shatteredseals.client;

import com.shatteredseals.net.TableEnchantPreview;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Hover over any of the three vanilla enchanting offers to see EVERY
 * enchantment and its actual level before spending lapis or XP.
 */
@EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT)
public final class EnchantmentPreviewOverlay {
    private static TableEnchantPreview current;

    public static void accept(TableEnchantPreview packet) {
        current = packet;
    }

    @SubscribeEvent
    public static void render(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof EnchantmentScreen screen)) {
            current = null;
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.player.containerMenu instanceof EnchantmentMenu menu)) return;
        TableEnchantPreview preview = current;
        if (preview == null || preview.menuId() != menu.containerId
                || preview.itemId() != Item.getId(menu.slots.get(0).getItem().getItem())
                // The vanilla DataSlot may only sync the lower 16 bits of the seed.
                || (preview.seed() & 0xFFFF) != (menu.getEnchantmentSeed() & 0xFFFF)) return;
        for (int i = 0; i < 3; i++) {
            if (menu.costs[i] != preview.costs()[i]) return;
        }

        // Vanilla enchantment screen: 176x166, three buttons at x+60, y+14+19*i.
        int left = (screen.width - 176) / 2;
        int top = (screen.height - 166) / 2;
        int mouseX = event.getMouseX();
        int mouseY = event.getMouseY();
        for (int slot = 0; slot < 3; slot++) {
            int rowY = top + 14 + slot * 19;
            if (mouseX < left + 60 || mouseX >= left + 168
                    || mouseY < rowY || mouseY >= rowY + 19
                    || menu.costs[slot] <= 0) continue;

            String names = preview.enchantments()[slot];
            if (names.isBlank()) return;
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("Will enchant with:").withStyle(ChatFormatting.GOLD));
            for (String name : names.split("\n")) {
                if (!name.isBlank()) lines.add(Component.literal(name).withStyle(ChatFormatting.AQUA));
            }
            lines.add(Component.literal("Requires level " + menu.costs[slot]
                + " | Uses " + (slot + 1) + " lapis and XP levels")
                .withStyle(ChatFormatting.GRAY));
            event.getGuiGraphics().renderTooltip(mc.font, lines, Optional.empty(), mouseX, mouseY);
            return;
        }
    }

    private EnchantmentPreviewOverlay() {}
}
