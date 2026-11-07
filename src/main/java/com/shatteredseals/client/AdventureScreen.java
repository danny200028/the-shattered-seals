package com.shatteredseals.client;

import com.shatteredseals.adventure.DungeonChapter;
import com.shatteredseals.net.SkillSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * In-game adventure hub, using server-owned progression snapshots.
 * The buttons only send vanilla triggers. All purchasing and crafting checks
 * are enforced by datapack functions on the integrated/dedicated server.
 */
public final class AdventureScreen extends Screen {
    private enum Page { JOURNAL, ARSENAL, FORGE, MASTERY }
    private static final DungeonChapter[] CHAPTERS = DungeonChapter.values();
    private static final String[] NUMERALS = {"I", "II", "III", "IV", "V", "VI"};
    private static final String[] WEAPONS = {
        "Emberbound", "King's Fang", "Warden's Oath",
        "Starfall", "Gravekeeper", "King's Bane"
    };
    private static final String[] PERKS = { "Defender", "Swiftblade", "Warbringer" };
    private static final String[] PERK_HELP = {
        "Resistance when fighting in NG+",
        "Haste for gathering and close combat",
        "Strength for your hardest encounters"
    };

    private Page page = Page.JOURNAL;
    private boolean requestedInitial;
    private int refreshTicks;

    public AdventureScreen() {
        super(Component.literal("The Shattered Seals | Adventurer's Codex"));
    }

    @Override protected void init() {
        super.init();
        if (!requestedInitial) {
            requestedInitial = true;
            ClientSkillState.clear();
            ClientSkillState.request();
        }
        int x = width / 2 - 205, y = height / 2 - 143;
        Page[] pages = Page.values();
        String[] names = { "Quest Journal", "Arsenal", "Relic Forge", "NG+ Mastery" };
        for (int i = 0; i < pages.length; i++) {
            final Page destination = pages[i];
            Button tab = Button.builder(Component.literal(names[i]), b -> {
                page = destination;
                rebuildWidgets();
            }).bounds(x + 4 + i * 102, y + 16, 99, 21).build();
            tab.active = page != destination;
            addRenderableWidget(tab);
        }
        SkillSnapshot state = ClientSkillState.current();
        if (page == Page.MASTERY) {
            for (int i = 0; i < 3; i++) {
                final int index = i;
                int rank = state == null ? 0 : state.masteryPerks()[i];
                Button buy = Button.builder(Component.literal(rank >= 5 ? "MAXED" : "+ Rank (1 MP)"),
                    b -> perform("ss_mperk", index + 1))
                    .bounds(x + 285, y + 93 + i * 47, 114, 22).build();
                buy.active = state != null && state.ngCycle() >= 1
                    && state.masteryPoints() >= 1 && rank < 5;
                addRenderableWidget(buy);
            }
        } else if (page == Page.FORGE) {
            addRenderableWidget(Button.builder(Component.literal("Upgrade held relic"),
                b -> perform("ss_upgrade", 1))
                .bounds(x + 123, y + 163, 162, 23).build());
        } else if (page == Page.JOURNAL) {
            addRenderableWidget(Button.builder(Component.literal("Mark next dungeon"),
                b -> markDungeon())
                .bounds(x + 194, y + 235, 203, 21).build());
            if (state != null && state.level() >= 60 && state.bossWins()[5] > 0 && !isReplay(state)) {
                addRenderableWidget(Button.builder(Component.literal(state.ngCycle() == 0 ? "Begin New Game+" : "Start next NG+ cycle"),
                    b -> perform("ss_story", 3)).bounds(x + 8, y + 235, 180, 21).build());
            }
        } else if (page == Page.ARSENAL) {
            addRenderableWidget(Button.builder(Component.literal("Open Relic Forge"),
                b -> { page = Page.FORGE; rebuildWidgets(); })
                .bounds(x + 220, y + 227, 177, 21).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Skill Tree (K)"),
            b -> Minecraft.getInstance().setScreen(new SkillTreeScreen()))
            .bounds(x + 4, y + 261, 126, 21).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
            .bounds(x + 314, y + 261, 86, 21).build());
    }

    private static void perform(String objective, int value) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.connection != null) {
            mc.player.connection.sendCommand("trigger " + objective + " set " + value);
            ClientSkillState.request();
        }
    }

    private void markDungeon() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.connection != null) {
            mc.player.connection.sendCommand("shatteredseals locate");
            onClose();
        }
    }

    @Override public void tick() {
        super.tick();
        if (++refreshTicks >= 20) {
            refreshTicks = 0;
            ClientSkillState.request();
        }
    }

    /** Only refreshed after a new authoritative server snapshot arrives. */
    public void refresh() { rebuildWidgets(); }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int x = width / 2 - 205, y = height / 2 - 143;
        graphics.fill(x - 8, y - 20, x + 415, y + 293, 0xED121A2A);
        graphics.fill(x - 8, y - 20, x + 415, y - 17, 0xFFD1A35D);
        graphics.drawCenteredString(font, title, width / 2, y - 11, 0xFFF5DBA2);
        SkillSnapshot data = ClientSkillState.current();
        if (data == null) {
            line(graphics, "Retrieving your adventure from the server...", x + 8, y + 58, 0xFFDDDDDD);
        } else {
            line(graphics, "Level " + data.level() + "   Skill Points: " + data.points()
                + "   NG+ Cycle: " + data.ngCycle() + "   Mastery Rank: " + data.masteryRank(),
                x + 8, y + 53, 0xFFF0CD86);
            switch (page) {
                case JOURNAL -> renderJournal(graphics, data, x, y);
                case ARSENAL -> renderArsenal(graphics, x, y);
                case FORGE -> renderForge(graphics, x, y);
                case MASTERY -> renderMastery(graphics, data, x, y);
            }
        }
        super.render(graphics, mouseX, mouseY, delta);
    }

    private void renderJournal(GuiGraphics graphics, SkillSnapshot data, int x, int y) {
        boolean replay = isReplay(data);
        line(graphics, replay ? "NEW GAME+ | CYCLE " + data.ngCycle() : "THE SIX SHATTERED SEALS",
             x + 8, y + 75, 0xFFE4AF76);
        int next = replay ? data.replayChapter() - 1 : nextChapter(data);
        for (int i = 0; i < CHAPTERS.length; i++) {
            boolean defeated = replay ? i < next : data.bossWins()[i] > 0;
            boolean active = i == next;
            int color = defeated ? 0xFF79D4AA : active ? 0xFFEAC99C : 0xFF8993A8;
            line(graphics, (defeated ? "[CLEARED]  " : active ? "[ACTIVE]   " : "[LOCKED]  ")
                + NUMERALS[i] + ". " + CHAPTERS[i].dungeon + "  |  " + CHAPTERS[i].boss,
                x + 8, y + 94 + 15 * i, color);
        }
        if (next < 0) {
            line(graphics, data.level() < 60 ? "All six seals broken. Reach level 60 for New Game+."
                 : "All six seals broken. Begin your next circuit when ready.", x + 8, y + 217, 0xFFC6CBDF);
            return;
        }
        DungeonChapter chapter = CHAPTERS[next];
        int level = replay ? 60 : chapter.recommendedLevel;
        line(graphics, "1. Mark the fortress and enter. Suggested level " + level + ".", x + 8, y + 183, 0xFFC6CBDF);
        line(graphics, data.puzzleStep() >= 3 ? "2. Arena ward opened. Take the sigil from the chest."
             : "2. Ask the keeper and attune the runes (" + data.puzzleStep() + "/3).", x + 8, y + 195, 0xFFC6CBDF);
        line(graphics, "3. Use the sigil on the obsidian pad and defeat " + chapter.boss + ".", x + 8, y + 207, 0xFFC6CBDF);
        String side = switch (data.sideQuestState()) {
            case 1 -> "Defeat " + chapter.miniboss;
            case 2 -> "Return to the keeper for your reward";
            case 3 -> "Completed";
            default -> "Speak to the keeper";
        };
        line(graphics, chapter.sideQuest + "  |  " + side, x + 8, y + 219, 0xFFE4AF76);
    }

    private static boolean isReplay(SkillSnapshot data) {
        return data.replayChapter() >= 1 && data.replayChapter() <= CHAPTERS.length;
    }

    private static int nextChapter(SkillSnapshot data) {
        for (int i = 0; i < CHAPTERS.length; i++) {
            if (data.bossWins()[i] == 0) return i;
        }
        return -1;
    }

    private void renderArsenal(GuiGraphics graphics, int x, int y) {
        line(graphics, "SIX WARDEN RELICS", x + 8, y + 77, 0xFFE4AF76);
        for (int i = 0; i < WEAPONS.length; i++)
            line(graphics, (i + 1) + ". " + WEAPONS[i] + "  |  Dormant / Awakened / Ascended",
                x + 8, y + 99 + i * 21, 0xFFCEDDF2);
        line(graphics, "Bosses award the relics. Upgrade them at the forge.", x + 8, y + 241, 0xFFC6CBDF);
    }

    private void renderForge(GuiGraphics graphics, int x, int y) {
        line(graphics, "RELIC FORGE", x + 8, y + 77, 0xFFE4AF76);
        line(graphics, "1. Hold a Dormant or Awakened Warden Relic.", x + 8, y + 103, 0xFFE0E3EA);
        line(graphics, "2. Collect its required upgrade materials.", x + 8, y + 126, 0xFFE0E3EA);
        line(graphics, "3. Use the button below to attempt an upgrade.", x + 8, y + 149, 0xFFE0E3EA);
        line(graphics, "Materials are checked and consumed by the server.", x + 8, y + 203, 0xFF95C9AE);
        line(graphics, "An invalid or unaffordable upgrade does not spend items.", x + 8, y + 223, 0xFFC6CBDF);
    }

    private void renderMastery(GuiGraphics graphics, SkillSnapshot data, int x, int y) {
        line(graphics, "AVAILABLE MASTERY POINTS: " + data.masteryPoints(), x + 8, y + 76, 0xFFE4AF76);
        for (int i = 0; i < PERKS.length; i++) {
            line(graphics, PERKS[i] + "  Rank " + data.masteryPerks()[i] + "/5",
                x + 8, y + 97 + 47 * i, 0xFFF0DAAF);
            line(graphics, PERK_HELP[i], x + 8, y + 111 + 47 * i, 0xFFAFC4D5);
        }
        line(graphics, data.ngCycle() > 0 ? "Each mastery rank costs 1 NG+ mastery point."
                 : "Unlock NG+ after level 60 and defeating the Hollow King.",
            x + 8, y + 240, 0xFFC6CBDF);
    }

    private void line(GuiGraphics graphics, String message, int x, int y, int color) {
        graphics.drawString(font, message, x, y, color);
    }

    @Override public void onClose() { super.onClose(); ClientSkillState.clear(); }
    @Override public boolean isPauseScreen() { return false; }
}
