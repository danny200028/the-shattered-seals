package com.shatteredseals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shatteredseals.ShatteredSeals;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

/**
 * Lightweight first-person visual feedback for classic-style sword blocking.
 * Never modifies gameplay damage, which ClassicSwordGuard owns on the server.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID, value = Dist.CLIENT)
public final class SwordGuardFeedback {
    @SubscribeEvent
    public static void renderSword(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (!SkillTreeClient.isSwordGuardHeld(mc)
            || event.getHand() != InteractionHand.MAIN_HAND
            || !event.getItemStack().is(ItemTags.SWORDS)) return;

        PoseStack pose = event.getPoseStack();
        pose.translate(-0.18D, 0.10D, -0.12D);
        pose.mulPose(Axis.YP.rotationDegrees(-35.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(-55.0F));
    }

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
