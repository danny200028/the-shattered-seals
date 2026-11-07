package com.shatteredseals.client;

import com.shatteredseals.net.SkillSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Real Minecraft Screen, wired to server-authoritative scoreboard triggers. */
public final class SkillTreeScreen extends Screen {
    private static final String[] CLASSES = {"Vanguard", "Warden", "Pathfinder", "Arcanist"};
    private static final String[][] BRANCHES = {
        {"Berserker", "Weapon Master", "Juggernaut"},
        {"Guardian", "Protector", "Immortal"},
        {"Marksman", "Hunter", "Shadow"},
        {"Elementalist", "Spellweaver", "Soulbinder"}
    };
    private static final String[][] MASTERIES = {
        {"Blood Frenzy", "Perfect Edge", "Unbreakable"},
        {"Iron Bastion", "Aegis Surge", "Undying Oath"},
        {"Eagle Eye", "Apex Hunter", "Ghostwalk"},
        {"Elemental Dominion", "Arcane Overdrive", "Soul Ascendance"}
    };
    private int selectedClass = 0;
    private boolean confirmingReset;
    private int refreshTicks;
    private boolean requestedInitial;

    public SkillTreeScreen() { super(Component.literal("The Shattered Seals | Skill Tree")); }

    @Override protected void init() {
        super.init();
        if (!requestedInitial) { requestedInitial = true; ClientSkillState.clear(); ClientSkillState.request(); }
        int x = width / 2 - 173, y = height / 2 - 123;
        SkillSnapshot data = ClientSkillState.current();
        boolean ready = data != null;
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            Button tab = Button.builder(Component.literal(CLASSES[i]), b -> {
                selectedClass = idx;
                confirmingReset = false;
                rebuildWidgets();
            }).bounds(x + i * 87, y + 4, 84, 20).build();
            tab.active = i != selectedClass;
            addRenderableWidget(tab);
        }
        int base = selectedClass * 3;
        for (int i = 0; i < 3; i++) {
            final int number = base + i + 1;
            final int branch = i;
            final int by = y + 66 + i * 47;
            final int rank = ready ? data.branchRanks()[number - 1] : 0;
            final boolean mastered = ready && data.branchMasteries()[number - 1] > 0;
            Button upgrade = Button.builder(Component.literal("+ Rank"), b -> perform("ss_branch", number))
                .bounds(x + 205, by, 66, 20).build();
            upgrade.active = ready && rank < 5 && data.points() >= 1;
            addRenderableWidget(upgrade);
            Button mastery = Button.builder(Component.literal(mastered ? "MASTERED" : "Master"),
                b -> perform("ss_ascend", number)).bounds(x + 274, by, 70, 20).build();
            mastery.active = ready && !mastered && rank >= 5 && data.level() >= 50
                             && data.kills() >= 100 && data.points() >= 8;
            addRenderableWidget(mastery);
        }
        Button core = Button.builder(Component.literal("+ Core Ability (1 SP)"), b -> perform("ss_buy", selectedClass + 1))
            .bounds(x, y + 211, 151, 20).build();
        core.active = ready && data.points() >= 1;
        addRenderableWidget(core);

        Button switcher = Button.builder(Component.literal(ready && data.activeClass() == selectedClass+1
            ? "Active Class" : "Switch to " + CLASSES[selectedClass]),
            b -> perform("ss_switch", selectedClass+1)).bounds(x+155,y+211,189,20).build();
        switcher.active = ready && data.activeClass() != selectedClass+1;
        addRenderableWidget(switcher);

        addRenderableWidget(Button.builder(Component.literal(
            confirmingReset ? "CONFIRM: Use Dragon Egg" : "Reset skill points (Dragon Egg)"), b -> {
            if (!confirmingReset) { confirmingReset = true; rebuildWidgets(); }
            else { confirmingReset = false; perform("ss_respec", 1); }
        }).bounds(x,y+235,245,20).build());
        if (confirmingReset) addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> {
            confirmingReset = false; rebuildWidgets();
        }).bounds(x+249,y+235,95,20).build());
    }

    private static void perform(String objective, int value) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.connection != null)
            mc.player.connection.sendCommand("trigger " + objective + " set " + value);
    }

    @Override public void tick() {
        super.tick();
        if (++refreshTicks >= 20) {
            refreshTicks = 0;
            ClientSkillState.request();
            // Widgets update when a packet arrives; at 1Hz, no chat interaction needed.
        }
    }

    /** Called by client network handler only after the server returns fresh scores. */
    public void refresh() { rebuildWidgets(); }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int x = width / 2 - 173, y = height / 2 - 123;
        graphics.fill(x - 9, y - 32, x + 353, y + 265, 0xE9181824);
        graphics.drawCenteredString(font, title, width/2, y-22, 0xFFD797);
        SkillSnapshot data = ClientSkillState.current();
        if (data == null) {
            graphics.drawString(font, "Loading progression from server...", x, y+33, 0xDDDDDD);
        } else {
            graphics.drawString(font,
                "Level " + data.level() + "   XP " + data.xp() + "/" + data.requiredXp()
                + "   Skill Points " + data.points(), x, y+31, 0xE9CF93);
            graphics.drawString(font,
                "Kills " + data.kills() + "  |  NG+ Mastery " + data.masteryRank()
                + "  |  Active: " + (data.activeClass() > 0 && data.activeClass() <= 4
                     ? CLASSES[data.activeClass()-1] : "Unselected"),
                x, y+44, 0xBBC3D6);
            for (int i = 0; i < 3; i++) {
                int idx = selectedClass*3 + i;
                int by = y+66+i*47;
                graphics.drawString(font,
                    BRANCHES[selectedClass][i] + "  " + data.branchRanks()[idx] + "/5",
                    x+3, by+1, 0xFFFFFF);
                boolean unlocked = data.branchMasteries()[idx] > 0;
                graphics.drawString(font,
                    MASTERIES[selectedClass][i] + (unlocked ? " [UNLOCKED]" : " [LOCKED]"),
                    x+3, by+14, unlocked ? 0xB4F2C5 : 0xC7B1E6);
            }
        }
        graphics.drawString(font, "Masteries: Level 50 | 100 kills | Rank 5/5 | 8 SP",
            x, y+202, 0xAAB2C2);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override public void onClose() { super.onClose(); ClientSkillState.clear(); }
    @Override public boolean isPauseScreen() { return false; }
}
