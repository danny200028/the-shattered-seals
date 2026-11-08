package com.shatteredseals.client;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Lightweight first-person visual feedback for classic-style sword blocking.
 * Never modifies gameplay damage, which ClassicSwordGuard owns on the server.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID, value = Dist.CLIENT)
public final class SwordGuardFeedback {
    @SubscribeEvent
    public static void renderStatus(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!SkillTreeClient.isSwordGuardHeld(mc) || mc.options.hideGui) return;
        var g = event.getGuiGraphics();
        var hud = CombatHudLayout.measure(g.guiWidth(), g.guiHeight());
        if (hud.panelWidth() < 34) return;
        g.drawString(mc.font, "BLOCK", hud.leftX() + 3, hud.firstY() - 12,
            0xFF9CD9FF, true);
    }

    private SwordGuardFeedback() {}
}
