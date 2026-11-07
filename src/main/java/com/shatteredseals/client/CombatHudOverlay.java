package com.shatteredseals.client;

import com.shatteredseals.net.SkillSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Compact combat overlay. Cooldowns come from the server's scoreboard snapshot. */
@EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT)
public final class CombatHudOverlay {
    private static final String[] CLASS_NAMES = {"VANGUARD", "WARDEN", "PATHFINDER", "ARCANIST"};
    private static final String[][] ABILITIES = {
        {"Cleave", "Rally", "Quake", "Last Stand"},
        {"Bastion", "Guardian", "Rebuke", "Oath"},
        {"Mark", "Windstep", "Focus", "Escape"},
        {"Nova", "Frost", "Spark", "Overdrive"}
    };
    private static final String[] RESOURCES = {"FURY", "GUARD", "FOCUS", "ARCANE"};
    private static final int[] COSTS = {12, 20, 28, 45};
    private static final String[] KEYS = {"Q", "R", "V", "G"};
    private static final int[] ACCENTS = {0xFFE26A56, 0xFF7DB4E8, 0xFF82D79B, 0xFFC68BE9};

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.screen != null) return;
        SkillSnapshot snapshot = ClientSkillState.current();
        if (snapshot == null) return;
        int classIndex = snapshot.activeClass() - 1;
        if (classIndex < 0 || classIndex >= ABILITIES.length) return;

        GuiGraphics g = event.getGuiGraphics();
        int center = g.guiWidth() / 2;
        int bottom = g.guiHeight() - 57;
        int width = 48;
        int gap = 5;
        int start = center - (width * 4 + gap * 3) / 2;
        int accent = ACCENTS[classIndex];
        int resource = Math.max(0, Math.min(100, snapshot.resources()[classIndex]));
        String heading = CLASS_NAMES[classIndex] + "  |  " + RESOURCES[classIndex] + " " + resource + "/100";
        g.drawString(mc.font, heading, center - mc.font.width(heading) / 2, bottom - 27, accent, true);
        int barWidth = width * 4 + gap * 3;
        g.fill(start, bottom - 15, start + barWidth, bottom - 9, 0xDD222633);
        g.fill(start, bottom - 15, start + barWidth * resource / 100, bottom - 9, accent);

        for (int i = 0; i < 4; i++) {
            int x = start + i * (width + gap);
            int ticks = snapshot.cooldowns()[i];
            boolean cooling = ticks > 0;
            g.fill(x, bottom, x + width, bottom + 31, 0xD91B2030);
            g.fill(x, bottom, x + width, bottom + 2, cooling ? 0xFF687080 : accent);
            g.fill(x, bottom + 29, x + width, bottom + 31, cooling ? 0xFF687080 : accent);
            g.fill(x, bottom, x + 2, bottom + 31, cooling ? 0xFF687080 : accent);
            g.fill(x + width - 2, bottom, x + width, bottom + 31, cooling ? 0xFF687080 : accent);

            String key = KEYS[i];
            g.drawString(mc.font, key, x + 4, bottom + 5, 0xFFFFFFFF, true);
            String name = ABILITIES[classIndex][i];
            if (resource < COSTS[i] && !cooling) {
                g.fill(x + 2, bottom + 3, x + width - 2, bottom + 17, 0xAA421B21);
            }
            g.drawString(mc.font, name, x + (width - mc.font.width(name)) / 2,
                         bottom + 18, cooling ? 0xFFB6BCC8 : resource < COSTS[i] ? 0xFFFF8B8B : 0xFFFFFFFF, false);
            if (cooling) {
                g.fill(x + 2, bottom + 3, x + width - 2, bottom + 17, 0xBB090D17);
                String remaining = String.format(java.util.Locale.ROOT, "%.1fs", ticks / 20.0);
                g.drawString(mc.font, remaining, x + (width - mc.font.width(remaining)) / 2,
                             bottom + 5, 0xFFFFD18C, true);
            }
        }
    }
    private CombatHudOverlay() {}
}
