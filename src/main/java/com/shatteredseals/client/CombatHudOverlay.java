package com.shatteredseals.client;

import com.shatteredseals.net.SkillSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Survival-HUD-friendly combat overlay.
 * The Minecraft hotbar occupies center +/-91 GUI pixels. Ability 1+2
 * stack outside its left edge; abilities 3+4 stack outside the right.
 * Class and resource readouts share the vanilla hearts/hunger alignment,
 * sitting above both vanilla status bars rather than overlapping them.
 */
@EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT)
public final class CombatHudOverlay {
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
        CombatHudLayout.Positions layout = CombatHudLayout.measure(g.guiWidth(), g.guiHeight());
        int accent = ACCENTS[classIndex];
        int resource = Math.max(0, Math.min(100, snapshot.resources()[classIndex]));
        drawStatus(g, mc, layout, classIndex, resource, accent);

        // Very narrow window sizes may not fit a panel outside each hotbar
        // edge. Avoid drawing over the vanilla hotbar in those cases.
        if (layout.panelWidth() < 15) return;
        for (int i = 0; i < 4; i++) {
            drawAbility(g, mc, layout, classIndex, i, snapshot.cooldowns()[i], resource, accent);
        }
    }

    private static void drawStatus(GuiGraphics g, Minecraft mc, CombatHudLayout.Positions p,
                                   int classIndex, int resource, int accent) {
        int left = p.hotbarLeft();
        int right = p.hotbarRight();
        int labelY = p.titleY();
        int barY = p.barY();

        // Keep the label below Minecraft's action-bar level/SP line and
        // keep the resource bar above hearts/hunger (see CombatHudLayout).
        // Slight translucent backing maintains legibility on bright sand/snow
        // without covering any vanilla hearts, hunger or experience.
        g.fill(left - 3, labelY - 3, right + 3, barY + 7, 0xA91B2030);
        // The vanilla action bar already names the selected class.
        // A single centered resource readout avoids repeating it and keeps
        // all four classes consistent (GUARD, FURY, FOCUS and ARCANE).
        String amount = RESOURCES[classIndex] + " " + resource + "/100";
        g.drawString(mc.font, amount, (left + right - mc.font.width(amount)) / 2,
                     labelY, accent, true);
        int fullWidth = right - left;
        g.fill(left, barY, right, barY + 5, 0xDD222633);
        g.fill(left, barY, left + fullWidth * resource / 100, barY + 5, accent);
    }

    private static void drawAbility(GuiGraphics g, Minecraft mc, CombatHudLayout.Positions p,
                                    int classIndex, int slot, int ticks, int resource, int accent) {
        int x = p.abilityX(slot);
        int y = p.abilityY(slot);
        int width = p.panelWidth();
        int height = CombatHudLayout.PANEL_HEIGHT;
        boolean cooling = ticks > 0;
        int border = cooling ? 0xFF687080 : accent;

        g.fill(x, y, x + width, y + height, 0xE21B2030);
        // Thin border keeps the buttons visible without bright 2px framing.
        g.fill(x, y, x + width, y + 1, border);
        g.fill(x, y + height - 1, x + width, y + height, border);
        g.fill(x, y, x + 1, y + height, border);
        g.fill(x + width - 1, y, x + width, y + height, border);
        if (!cooling && resource < COSTS[slot]) {
            g.fill(x + 1, y + 11, x + width - 1, y + height - 1, 0xAA421B21);
        }

        g.drawString(mc.font, KEYS[slot], x + 4, y + 2, 0xFFFFFFFF, true);
        if (cooling) {
            String remaining = String.format(java.util.Locale.ROOT, "%.1fs", ticks / 20.0);
            int rightAligned = x + width - 3 - mc.font.width(remaining);
            if (rightAligned > x + 10) {
                g.drawString(mc.font, remaining, rightAligned, y + 2, 0xFFFFD18C, true);
            }
        }

        // Clip long ability names on narrow GUI scales instead of letting
        // them spill onto the hotbar, hearts, or the other side of the screen.
        String name = mc.font.plainSubstrByWidth(ABILITIES[classIndex][slot], width - 6);
        int color = cooling ? 0xFFB6BCC8 : resource < COSTS[slot] ? 0xFFFF8B8B : 0xFFFFFFFF;
        g.drawString(mc.font, name, x + (width - mc.font.width(name)) / 2, y + 12, color, false);
    }

    private CombatHudOverlay() {}
}
