package com.shatteredseals.client;

import com.shatteredseals.adventure.DungeonChapter;
import com.shatteredseals.net.SkillSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Read-only, optional story tracker. The client draws authoritative server
 * snapshots, never predicts quest completion or performs reward commands.
 */
@EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT)
public final class QuestTrackerOverlay {
    private static final DungeonChapter[] CHAPTERS = DungeonChapter.values();
    private static boolean visible = true;
    private static final int WIDTH = 250;
    private static final int HEIGHT = 78;

    public static void toggle() { visible = !visible; }
    public static boolean isVisible() { return visible; }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!visible || mc.player == null || mc.options.hideGui || mc.screen != null) return;
        SkillSnapshot state = ClientSkillState.current();
        if (state == null) return;

        int next = nextChapter(state);
        GuiGraphics g = event.getGuiGraphics();
        // Never cover the health bar or ability hotbar, even at narrower GUI scales.
        int x = Math.max(4, g.guiWidth() - WIDTH - 8);
        int y = 7;
        g.fill(x, y, x + WIDTH, y + HEIGHT, 0xDA111827);
        g.fill(x, y, x + WIDTH, y + 2, 0xFFD6A65B);
        g.drawString(mc.font, "THE SHATTERED SEALS  |  J: CODEX", x + 8, y + 8, 0xFFFFDB9B, true);

        if (next == -1) {
            g.drawString(mc.font, "The six seals are broken!", x + 8, y + 25, 0xFF9FDEB7, false);
            String nextStep = state.level() < 60
                ? "Reach level 60 to unlock New Game+."
                : "Open the Codex to begin a new circuit.";
            g.drawString(mc.font, nextStep, x + 8, y + 41, 0xFFD5DAE5, false);
        } else {
            DungeonChapter chapter = CHAPTERS[next];
            boolean replay = state.replayChapter() >= 1 && state.replayChapter() <= CHAPTERS.length;
            g.drawString(mc.font, (replay ? "NG+ " : "CHAPTER ") + chapter.number()
                + "/6: " + chapter.dungeon, x + 8, y + 24, 0xFFF5D8A6, false);
            int runeStep = Math.max(0, Math.min(3, state.puzzleStep()));
            String objective = runeStep == 3
                ? "Ward cleared. Use sigil at arena altar."
                : "Find keeper. Attune runes: " + runeStep + "/3";
            g.drawString(mc.font, objective, x + 8, y + 39, 0xFFE0E7EF, false);
            String side = switch (state.sideQuestState()) {
                case 1 -> "Hunt: " + chapter.miniboss;
                case 2 -> "Side quest: Claim keeper reward";
                case 3 -> "Side quest: Completed";
                default -> "Side quest: Speak to the keeper";
            };
            g.drawString(mc.font, side, x + 8, y + 53, 0xFFB5D7C8, false);
        }
        g.drawString(mc.font, "L: Hide tracker  |  Locate in Codex", x + 8,
            y + 66, 0xFF9AA9BB, false);
    }

    private static int nextChapter(SkillSnapshot state) {
        if (state.replayChapter() >= 1 && state.replayChapter() <= CHAPTERS.length) {
            return state.replayChapter() - 1;
        }
        for (int index = 0; index < CHAPTERS.length; index++) {
            if (state.bossWins()[index] == 0) return index;
        }
        return -1;
    }

    private QuestTrackerOverlay() {}
}
