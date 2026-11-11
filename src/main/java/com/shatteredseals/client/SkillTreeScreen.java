package com.shatteredseals.client;

import com.shatteredseals.net.SkillSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
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
    // Every description is taken from live progression/combat functions.
    private static final String[] CORE_NAME = {"Vanguard Might", "Warden Fortitude", "Pathfinder Agility", "Arcanist Focus"};
    private static final String[] CORE_HELP = {
        "Strength I at ranks 1-3, Strength II at ranks 4-5 while Vanguard is active.",
        "Resistance I at ranks 1-3, Resistance II at ranks 4-5 while Warden is active.",
        "Speed I at ranks 1-3, Speed II at ranks 4-5 while Pathfinder is active.",
        "Night Vision at ranks 1-5, plus Haste I at ranks 4-5 while Arcanist is active."
    };
    private static final String[][] BRANCH_HELP = {
        {"Rank 3 grants Strength I; rank 5 unlocks mastery.",
         "Rank 3 grants Haste I; rank 5 unlocks mastery.",
         "Rank 3 grants Resistance I; rank 5 unlocks mastery."},
        {"Rank 3 grants Resistance I; rank 5 unlocks mastery.",
         "Rank 3 grants Absorption I; rank 5 unlocks mastery.",
         "Rank 3 grants Regeneration I; rank 5 unlocks mastery."},
        {"Rank 3 grants Speed I; rank 5 unlocks mastery.",
         "Rank 3 grants Jump Boost I; rank 5 unlocks mastery.",
         "Rank 3 grants Night Vision; rank 5 unlocks mastery."},
        {"Rank 3 grants Fire Resistance; rank 5 unlocks mastery.",
         "Rank 3 grants Haste I; rank 5 unlocks mastery.",
         "Rank 3 grants Regeneration I; rank 5 unlocks mastery."}
    };
    private static final String[][] MASTERY_HELP = {
        {"Blood Frenzy: Strength IV and Speed II for 12s.",
         "Perfect Edge: Haste IV and Strength III for 18s.",
         "Unbreakable: Resistance III and Absorption IV for 18s."},
        {"Iron Bastion: Resistance IV for 18s; brief Slowness I.",
         "Aegis Surge: Absorption V for 25s, Regeneration II for 12s.",
         "Undying Oath: Regeneration III and Fire Resistance for 18s."},
        {"Eagle Eye: Speed III 20s and Strength II 12s.",
         "Apex Hunter: Night Vision, Regeneration II, Jump Boost II.",
         "Ghostwalk: Invisibility and Speed III for 14s."},
        {"Elemental Dominion: Reveal nearby foes; Fire Resistance and Strength II.",
         "Arcane Overdrive: Haste IV, Speed II, Absorption II for 20s.",
         "Soul Ascendance: Regeneration III, Absorption IV, Resistance II."}
    };
    private static final String[][] ACTIVE_HELP = {
        {"Q Blood Cleave: 12 Fury, 6 AoE damage, 8s cooldown.",
         "R Rallying Roar: 20 Fury, Strength and Resistance, 11s cooldown.",
         "V Earthbreaker: 28 Fury, AoE damage and Slow, 13s cooldown.",
         "G Last Stand: 45 Fury, Absorption and Regen, 25s cooldown."},
        {"Q Bastion Pulse: 12 Guard, Slowness and shielding, 12s cooldown.",
         "R Guardian Aura: 20 Guard, Absorption and Resistance, 13s cooldown.",
         "V Shield Rebuke: 28 Guard, magic damage and Weakness, 11s cooldown.",
         "G Undying Oath: 45 Guard, Resistance and Regen, 30s cooldown."},
        {"Q Hunter's Mark: 12 Focus, reveal, Speed, Strength, 9s cooldown.",
         "R Windstep: 20 Focus, Speed and Jump Boost, 8s cooldown.",
         "V Predator Focus: 28 Focus, Strength and Night Vision, 11s cooldown.",
         "G Shadow Escape: 45 Focus, Invisibility and Speed, 20s cooldown."},
        {"Q Arcane Nova: 12 Arcane, magic AoE and Weakness, 11s cooldown.",
         "R Frost Ring: 20 Arcane, AoE Slowness, 10s cooldown.",
         "V Storm Spark: 28 Arcane, up to four magic hits, 13s cooldown.",
         "G Arcane Overdrive: 45 Arcane, Strength and Resistance, 25s cooldown."}
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
            tab.setTooltip(Tooltip.create(Component.literal(
                String.join("\\n", ACTIVE_HELP[i]))));
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
            upgrade.setTooltip(Tooltip.create(Component.literal(
                BRANCHES[selectedClass][branch] + " rank " + rank + "/5. " +
                BRANCH_HELP[selectedClass][branch] + " Cost: 1 skill point.")));
            addRenderableWidget(upgrade);
            Button mastery = Button.builder(Component.literal(mastered ? "MASTERED" : "Master"),
                b -> perform("ss_ascend", number)).bounds(x + 274, by, 70, 20).build();
            mastery.active = ready && !mastered && rank >= 5 && data.level() >= 50
                             && data.kills() >= 100 && data.points() >= 8;
            mastery.setTooltip(Tooltip.create(Component.literal(
                MASTERY_HELP[selectedClass][branch] + " Unlock: level 50, 100 dungeon kills, branch 5/5, 8 SP.")));
            addRenderableWidget(mastery);
        }
        int coreRank = ready ? data.coreRanks()[selectedClass] : 0;
        Button core = Button.builder(Component.literal("Core " + coreRank + "/5 (1 SP)"),
            b -> perform("ss_buy", selectedClass + 1))
            .bounds(x, y + 211, 151, 20).build();
        core.active = ready && data.points() >= 1 && coreRank < 5;
        core.setTooltip(Tooltip.create(Component.literal(
            CORE_NAME[selectedClass] + " rank " + coreRank + "/5: " + CORE_HELP[selectedClass])));
        addRenderableWidget(core);

        Button switcher = Button.builder(Component.literal(ready && data.activeClass() == selectedClass+1
            ? "Active Class" : "Switch to " + CLASSES[selectedClass]),
            b -> perform("ss_switch", selectedClass+1)).bounds(x+155,y+211,189,20).build();
        switcher.active = ready && data.activeClass() != selectedClass+1;
        addRenderableWidget(switcher);

        addRenderableWidget(Button.builder(Component.literal(
            confirmingReset ? "CONFIRM: Use Dragon Egg" : "Reset skill points (Dragon Egg)"), b -> {
            if (!confirmingReset) { confirmingReset = true; perform("ss_respec", 1); rebuildWidgets(); }
            else { confirmingReset = false; perform("ss_respec", 2); }
        }).bounds(x,y+235,245,20).build());
        if (confirmingReset) addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> {
            confirmingReset = false; perform("ss_respec", 3); rebuildWidgets();
        }).bounds(x+249,y+235,95,20).build());
        else addRenderableWidget(Button.builder(Component.literal("RPG Hub"),
            b -> Minecraft.getInstance().setScreen(new AdventureScreen()))
            .bounds(x+249,y+235,95,20).build());
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
        // Render base widgets before text: the labels must always be drawn
        // after any vanilla/NeoForge screen background and blur passes.
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width/2, y-22, 0xFFFFD797);
        SkillSnapshot data = ClientSkillState.current();
        if (data == null) {
            graphics.drawString(font, "Loading progression from server...", x, y+33, 0xFFDDDDDD);
        } else {
            graphics.drawString(font,
                "Level " + data.level() + "   XP " + data.xp() + "/" + data.requiredXp()
                + "   Skill Points " + data.points(), x, y+31, 0xFFE9CF93);
            graphics.drawString(font,
                "Dungeon kills " + data.kills() + "  |  NG+ Mastery " + data.masteryRank()
                + "  |  Active: " + (data.activeClass() > 0 && data.activeClass() <= 4
                     ? CLASSES[data.activeClass()-1] : "Unselected"),
                x, y+44, 0xFFBBC3D6);
            for (int i = 0; i < 3; i++) {
                int idx = selectedClass*3 + i;
                int by = y+66+i*47;
                graphics.drawString(font,
                    BRANCHES[selectedClass][i] + "  " + data.branchRanks()[idx] + "/5",
                    x+3, by+1, 0xFFFFFFFF);
                boolean unlocked = data.branchMasteries()[idx] > 0;
                graphics.drawString(font,
                    MASTERIES[selectedClass][i] + (unlocked ? " [UNLOCKED]" : " [LOCKED]"),
                    x+3, by+14, unlocked ? 0xFFB4F2C5 : 0xFFC7B1E6);
                graphics.drawString(font, font.plainSubstrByWidth(BRANCH_HELP[selectedClass][i], 192),
                    x+3, by+27, 0xFFB3BCCA);
            }
        }
        graphics.drawString(font, "Masteries: Level 50 | 100 dungeon kills | Rank 5/5 | 8 SP",
            x, y+202, 0xFFAAB2C2);
    }

    @Override public void onClose() {
        if (confirmingReset) {
            confirmingReset = false;
            perform("ss_respec", 3);
        }
        super.onClose();
        ClientSkillState.clear();
    }
    @Override public boolean isPauseScreen() { return false; }
}
