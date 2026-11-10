package com.shatteredseals.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Original story-themed menu branding and World Loading artwork.
 * Preserves all vanilla title menu buttons. Initial native/NeoForge boot
 * splash stays unchanged to avoid disrupting the modloader's safety UI.
 */
@EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT)
public final class BrandedScreens {
    private static final ResourceLocation TITLE =
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "textures/gui/title_banner.png");
    private static final ResourceLocation LOADING =
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "textures/gui/loading_banner.png");

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        Minecraft mc = Minecraft.getInstance();
        GuiGraphics g = event.getGuiGraphics();
        if (event.getScreen() instanceof TitleScreen) {
            int x = (g.guiWidth() - 256) / 2;
            g.fill(x - 8, 0, x + 264, 84, 0xEC101624);
            g.blit(TITLE, x, 2, 0, 0, 256, 80, 256, 80);
            g.drawCenteredString(mc.font, "AN RPG OF SIX BROKEN SEALS",
                g.guiWidth() / 2, 86, 0xFFD7B785);
        } else {
            String name = event.getScreen().getClass().getSimpleName();
            if (!name.equals("ReceivingLevelScreen")
                    && !name.equals("LevelLoadingScreen")
                    && !name.equals("ProgressScreen")) return;
            int x = g.guiWidth() / 2 - 128;
            int y = Math.max(4, g.guiHeight() / 2 - 85);
            g.fill(0, 0, g.guiWidth(), g.guiHeight(), 0xE9101828);
            g.pose().pushPose();
            g.pose().translate(x, y, 0);
            g.pose().scale(0.5F, 0.5F, 1.0F);
            g.blit(LOADING, 0, 0, 0, 0, 512, 256, 512, 256);
            g.pose().popPose();
            g.drawCenteredString(mc.font, "THE CHRONICLE IS OPENING...",
                g.guiWidth() / 2, y + 138, 0xFFE6D0A8);
        }
    }

    private BrandedScreens() {}
}
