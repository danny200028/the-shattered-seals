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
    private enum Page { JOURNAL, ARSENAL, RELICS, FORGE, MASTERY, ENCHANTS, BESTIARY, EXPLORE, TRIALS, BOUNTIES }
    private static final DungeonChapter[] CHAPTERS = DungeonChapter.values();
    private static final String[] NUMERALS = {"I", "II", "III", "IV", "V", "VI"};
    private static final String[] WEAPONS = {
        "Emberbound", "King's Fang", "Warden's Oath",
        "Starfall", "Gravekeeper", "King's Bane"
    };
    private static final String[] PERKS = { "Defender", "Swiftblade", "Warbringer" };
    private static final String[] RUNES = {"Sun", "Tide", "Ash"};
    private static final String[] SWORD_POWERS = {
        "Inferno Slash | 5% | 45s", "Executioner's Mark | 6% | 40s",
        "Guardian's Wrath | 5% | 50s", "Celestial Strike | 4% | 55s",
        "Soul Harvest | 6% | 45s", "Shattered Reality | 3% | 60s"
    };
    private static final String[] SWORD_DESCRIPTIONS = {
        "Burn nearby enemies; stronger fire area at higher tiers.",
        "Weaken and reveal targets; Ascended adds Wither.",
        "Knock enemies back; higher tiers grant shield and Resistance.",
        "Reveal foes, strike nearby enemies and slow at higher tiers.",
        "Heal after a hit; higher tiers add Wither and Regeneration.",
        "Void burst and Weakness; higher tiers slow larger groups."
    };
    private static final String[] RELIC_NAMES = {
        "Ember Guard", "Rift Step", "Echo Ping", "Warden's Pulse",
        "Frost Snare", "Delver's Draft", "Soul Mend", "Storm Call", "Shadow Cloak"
    };
    private static final String[] RELIC_DESCRIPTIONS = {
        "Fire Resistance and healing", "Short teleport and Slow Falling",
        "Reveals nearby foes", "Resistance and levitation pulse",
        "Slows nearby foes; grants Speed", "Mining Haste and exploration buffs",
        "Regeneration and Absorption, at a hunger cost", "Lightning strike and Resistance",
        "Invisibility and Night Vision, at a Weakness cost"
    };
    private static final String[] TIER_NAMES = {"Common", "Rare", "Legendary"};
    private static final int[] TIER_PRICES = {10, 20, 30};
    // Exact native-enchantment registry order, grouped into purchasable book rarities.
    private static final String[][] ENCHANT_NAMES = {
        {"Keen Edge", "Stagger", "Lifebloom", "Stoneguard", "Fleetfoot", "Deep Miner",
         "Lumberjack", "Harvest Blessing", "Durable", "Archer's Focus", "Aqua Step", "Treasure Sense"},
        {"Vampiric Edge", "Frostbite", "Thunderstrike", "Guardian Aura", "Phoenix Skin",
         "Shadowstep", "Berserker", "Vein Seeker", "Soul Collector", "Hunter's Precision",
         "Second Wind", "Magnetic Harvest"},
        {"Soul Reaper", "Infernal Wrath", "Celestial Judgement", "Titan's Resolve",
         "Immortal Spirit", "Voidwalker", "Worldbreaker", "Eternal Harvest",
         "Stormcaller", "Dragon's Fury", "Guardian Angel", "King's Execution"}
    };
    private static final String[][] ENCHANT_HELP = {
        {"Sword: extra melee damage", "Weapons: extra knockback", "Armour: extra max health",
         "Chestplate: damage reduction", "Boots: movement speed", "Pickaxe: mining speed",
         "Axe: woodcutting speed", "Hoe: harvest block XP", "Tools: reduced durability wear",
         "Bow: ranged damage", "Boots: swim movement", "Tools: bonus mining XP"},
        {"Sword: proc regeneration", "Weapons: proc Slowness", "Weapons: magic burst",
         "Chestplate: periodic Resistance", "Armour: periodic fire protection",
         "Boots: extra movement", "Axe: increased damage", "Pickaxe: extra ore XP",
         "Sword: bonus mob XP", "Bow: ranged damage", "Leggings: periodic healing",
         "Tools: mining efficiency"},
        {"Sword: proc regeneration", "Weapons: burning strikes", "Sword: magic damage burst",
         "Chestplate: damage reduction", "Armour: added max health", "Boots: speed",
         "Pickaxe: strong mining efficiency", "Hoe: bonus harvesting XP",
         "Bow: magic strike burst", "Sword: ignite on hit", "Armour: periodic Absorption",
         "Axe: increased damage"}
    };
    private static final String[] BOSS_LORE = {
        "Controls the old smuggling roads. He strikes from a distance.",
        "The fortress heart burns. Watch for sudden flares and pressure.",
        "An oathbound warrior; skeletons gather when its seal weakens.",
        "An arcane watcher that marks prey with cold starlight.",
        "A sentinel of six runes. Its patterns change as it weakens.",
        "The last seal's guardian. Survive the darkness to end the cycle."
    };
    private static final String[] BOSS_COUNTERS = {
        "Break line of sight; avoid crossbow volleys.",
        "Watch fire bursts; control distance and cover.",
        "Move between attacks; avoid getting surrounded.",
        "Keep moving; use cover and watch its phase cues.",
        "Clear summoned enemies; respond to the phase warnings.",
        "Guard frontal blows, reposition and respect phase changes."
    };
    private static final String[] BOSS_DROPS = {
        "King's Fang", "Emberbound Blade", "Warden's Oath",
        "Starfall", "Gravekeeper", "King's Bane"
    };
    private static final String[] BIOME_NAMES = {
        "Smugglers' Wilds", "Cinder Wastes", "Hollow Moor",
        "Arcane Highlands", "Sixfold Expanse", "Hollow King's Dominion"
    };
    private static final String[] BIOME_HINTS = {
        "Scarred tracks and ruined cargo camps",
        "Basalt and ash around the citadel",
        "Graves beneath the pale woodland",
        "White cliffs and scattered starstones",
        "Overgrown shrines to the six runes",
        "Black stone in the last king's shadow"
    };
    private static final String[] PERK_HELP = {
        "Resistance when fighting in NG+",
        "Haste for gathering and close combat",
        "Strength for your hardest encounters"
    };

    private Page page = Page.JOURNAL;
    private boolean requestedInitial;
    private int refreshTicks;
    private int enchantTier;
    private boolean showRuneHint;

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
        String[] names = { "Journal", "Swords", "Relics", "Forge", "Mastery", "Books", "Bosses", "Explore", "Trials", "Bounty" };
        for (int i = 0; i < pages.length; i++) {
            final Page destination = pages[i];
            Button tab = Button.builder(Component.literal(names[i]), b -> {
                page = destination;
                rebuildWidgets();
            }).bounds(x + 4 + i * 40, y + 16, 39, 21).build();
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
            } else {
                addRenderableWidget(Button.builder(Component.literal(showRuneHint ? "Hide rune hint" : "Reveal rune hint"),
                    b -> { showRuneHint = !showRuneHint; rebuildWidgets(); })
                    .bounds(x + 8, y + 235, 180, 21).build());
            }
        } else if (page == Page.ARSENAL) {
            addRenderableWidget(Button.builder(Component.literal("Open Relic Forge"),
                b -> { page = Page.FORGE; rebuildWidgets(); })
                .bounds(x + 134, y + 261, 175, 21).build());
        } else if (page == Page.ENCHANTS) {
            for (int i = 0; i < 3; i++) {
                final int chosen = i;
                Button tier = Button.builder(Component.literal(TIER_NAMES[i]),
                    b -> { enchantTier = chosen; rebuildWidgets(); })
                    .bounds(x + 8 + i * 130, y + 78, 123, 20).build();
                tier.active = enchantTier != i;
                addRenderableWidget(tier);
            }
            addRenderableWidget(Button.builder(Component.literal(
                "Buy random " + TIER_NAMES[enchantTier] + " book (" + TIER_PRICES[enchantTier] + " XP levels)"),
                b -> perform("ss_ebook", enchantTier + 1))
                .bounds(x + 8, y + 237, 391, 20).build());
        } else if (page == Page.TRIALS) {
            boolean unlocked = state != null && state.level() >= 60
                && state.bossWins()[5] > 0 && state.ngCycle() >= 1
                && isReplay(state);
            int[] trial = state == null ? new int[4] : state.trials();
            boolean claimed = state != null && isReplay(state)
                && (trial[2] & (1 << (state.replayChapter() - 1))) != 0;
            String[] labels = {"Hazard Pulse", "Scarcity", "Both (NG+3)"};
            for (int i = 0; i < 3; i++) {
                final int mask = i + 1;
                Button trialButton = Button.builder(Component.literal(labels[i]),
                    b -> sendAdventure("trial arm " + mask))
                    .bounds(x + 8 + i * 130, y + 132, 124, 22).build();
                trialButton.active = unlocked && !claimed && (mask != 3 || state.ngCycle() >= 3);
                addRenderableWidget(trialButton);
            }
            addRenderableWidget(Button.builder(Component.literal("Cancel armed trial"),
                b -> sendAdventure("trial cancel"))
                .bounds(x + 8, y + 218, 190, 22).build());
        } else if (page == Page.BOUNTIES) {
            int[] bounty = state == null ? new int[6] : state.bounties();
            String[] contractNames = {"Hunt", "Survey", "Guardian"};
            for (int i = 0; i < 3; i++) {
                final int kind = i + 1;
                Button accept = Button.builder(Component.literal("Accept " + contractNames[i]),
                    b -> sendAdventure("bounty accept " + kind))
                    .bounds(x + 8 + i * 130, y + 128, 124, 22).build();
                accept.active = state != null && bounty[0] == 0 && bounty[4] == 0
                    && (kind != 3 || nextChapter(state) >= 0 || isReplay(state));
                addRenderableWidget(accept);
            }
            Button claim = Button.builder(Component.literal("Claim completed bounty"),
                b -> sendAdventure("bounty claim"))
                .bounds(x + 8, y + 215, 190, 22).build();
            claim.active = bounty[0] > 0 && bounty[2] >= bounty[3];
            addRenderableWidget(claim);
            Button abandon = Button.builder(Component.literal("Abandon bounty"),
                b -> sendAdventure("bounty abandon"))
                .bounds(x + 208, y + 215, 190, 22).build();
            abandon.active = bounty[0] > 0;
            addRenderableWidget(abandon);
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

    private static void sendAdventure(String command) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.connection != null) {
            mc.player.connection.sendCommand("shatteredseals " + command);
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
        // Keep all custom label text above vanilla/NeoForge screen rendering.
        // The widgets do not overlap the Codex text regions.
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(font, title, width / 2, y - 11, 0xFFF5DBA2);
        SkillSnapshot data = ClientSkillState.current();
        if (data == null) {
            line(graphics, "Retrieving your adventure from the server...", x + 8, y + 58, 0xFFDDDDDD);
        } else {
            if (page != Page.ENCHANTS) line(graphics, "Level " + data.level() + "   Skill Points: " + data.points()
                + "   NG+ Cycle: " + data.ngCycle() + "   Mastery Rank: " + data.masteryRank(),
                x + 8, y + 53, 0xFFF0CD86);
            switch (page) {
                case JOURNAL -> renderJournal(graphics, data, x, y);
                case ARSENAL -> renderArsenal(graphics, data, x, y);
                case RELICS -> renderRelics(graphics, x, y);
                case ENCHANTS -> renderEnchants(graphics, x, y, mouseX, mouseY);
                case FORGE -> renderForge(graphics, x, y);
                case MASTERY -> renderMastery(graphics, data, x, y);
                case BESTIARY -> renderBestiary(graphics, data, x, y);
                case EXPLORE -> renderExplore(graphics, data, x, y);
                case TRIALS -> renderTrials(graphics, data, x, y);
                case BOUNTIES -> renderBounties(graphics, data, x, y);
            }
        }
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
        if (showRuneHint) {
            line(graphics, font.plainSubstrByWidth("Keeper: " + chapter.clue, 390),
                x + 8, y + 183, 0xFFF2D296);
            line(graphics, "Rune order: " + RUNES[chapter.runeAt(0)] + " > "
                + RUNES[chapter.runeAt(1)] + " > " + RUNES[chapter.runeAt(2)]
                + "  (" + data.puzzleStep() + "/3)", x + 8, y + 195, 0xFF9EDEDC);
        } else {
            line(graphics, "1. Mark the fortress and enter. Suggested level " + level + ".", x + 8, y + 183, 0xFFC6CBDF);
            line(graphics, data.puzzleStep() >= 3 ? "2. Arena ward opened. Take the sigil from the chest."
                 : "2. Ask the keeper and attune the runes (" + data.puzzleStep() + "/3).", x + 8, y + 195, 0xFFC6CBDF);
        }
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

    private void renderArsenal(GuiGraphics graphics, SkillSnapshot data, int x, int y) {
        line(graphics, "SIX LEGENDARY SWORDS | PASSIVES ON MELEE HITS", x + 8, y + 77, 0xFFE4AF76);
        for (int i = 0; i < WEAPONS.length; i++) {
            line(graphics, font.plainSubstrByWidth(WEAPONS[i] + "  |  Mastery " + com.shatteredseals.combat.SwordMastery.rank(data.swordMastery()[i]) + "/5 (" + data.swordMastery()[i] + " kills)  |  " + SWORD_POWERS[i], 392),
                x + 8, y + 92 + i * 24, 0xFFE1CFAB);
            line(graphics, SWORD_DESCRIPTIONS[i],
                x + 12, y + 104 + i * 24, 0xFFAFC4D5);
        }
        line(graphics, "Mastery is cosmetic. Dormant/Awakened/Ascended power remains unchanged.",
            x + 8, y + 240, 0xFF9BCDAD);
    }

    private void renderRelics(GuiGraphics graphics, int x, int y) {
        line(graphics, "ACTIVE RELICS  |  RIGHT-CLICK WHILE HOLDING TO CAST", x + 8, y + 77, 0xFFE4AF76);
        for (int i = 0; i < RELIC_NAMES.length; i++) {
            line(graphics, RELIC_NAMES[i] + ": " + RELIC_DESCRIPTIONS[i],
                x + 8, y + 95 + i * 16, 0xFFCEDDF2);
        }
        line(graphics, "All share a 20-second cooldown. Higher power tiers grant bonus effects.",
            x + 8, y + 244, 0xFF9BCDAD);
    }

    private void renderEnchants(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        line(graphics, "ENCHANTMENT ARCHIVE  |  " + TIER_NAMES[enchantTier] + "  |  12 UNIQUE BOOKS",
            x + 8, y + 54, 0xFFE4AF76);
        for (int i = 0; i < 12; i++) {
            int column = i / 6;
            int row = i % 6;
            int px = x + 8 + column * 196;
            int py = y + 105 + row * 21;
            line(graphics, ENCHANT_NAMES[enchantTier][i] + "  I-III", px, py, 0xFFE5DBB6);
            line(graphics, font.plainSubstrByWidth(ENCHANT_HELP[enchantTier][i], 185),
                 px + 2, py + 10, 0xFFAFC4D5);
            if (mouseX >= px && mouseX < px + 192 && mouseY >= py && mouseY < py + 21)
                graphics.renderTooltip(font, Component.literal(
                    ENCHANT_NAMES[enchantTier][i] + ": " + ENCHANT_HELP[enchantTier][i]), mouseX, mouseY);
        }
        line(graphics, "Book I + Book I = II; Book II + Book II = III. Apply in an anvil.",
            x + 8, y + 230, 0xFF9BCDAD);
    }

    private void renderBestiary(GuiGraphics graphics, SkillSnapshot data, int x, int y) {
        line(graphics, "BOSS BESTIARY | BATTLE RECORDS AND COUNTERS", x + 8, y + 76, 0xFFE4AF76);
        int next = nextChapter(data);
        for (int i = 0; i < CHAPTERS.length; i++) {
            int wins = data.bossWins()[i];
            boolean revealed = wins > 0 || i == next;
            int top = y + 92 + 27 * i;
            line(graphics, (wins > 0 ? "[DEFEATED x" + wins + "] " : revealed ? "[TARGET] " : "[UNKNOWN] ")
                    + CHAPTERS[i].boss,
                x + 8, top, wins > 0 ? 0xFF79D4AA : revealed ? 0xFFE9C78B : 0xFF8993A8);
            if (wins > 0) {
                line(graphics, font.plainSubstrByWidth(BOSS_LORE[i], 375), x + 12, top + 10, 0xFFCEDDF2);
                line(graphics, font.plainSubstrByWidth("Counter: " + BOSS_COUNTERS[i]
                    + " | First reward: " + BOSS_DROPS[i], 375), x + 12, top + 19, 0xFF90BDCE);
            } else {
                line(graphics, revealed ? font.plainSubstrByWidth("Tactics: " + BOSS_COUNTERS[i], 370)
                    : "Defeat earlier guardians to reveal this entry.", x + 12, top + 12, 0xFF95A2B8);
            }
        }
        line(graphics, "Records count boss victories; later encounters remain hidden until reached.",
            x + 8, y + 256, 0xFFAFC4D5);
    }

    private void renderExplore(GuiGraphics graphics, SkillSnapshot data, int x, int y) {
        line(graphics, "LIVING WORLD | BIOMES AND HIDDEN SHRINES", x + 8, y + 75, 0xFFE4AF76);
        int discovered = Integer.bitCount(data.discoveredBiomes() & 63);
        int landmarks = Integer.bitCount(data.discoveredLandmarks() & 63);
        line(graphics, "Biomes visited: " + discovered + "/6   Lost shrines: " + landmarks + "/6",
            x + 8, y + 90, 0xFF9BDBC0);
        for (int i = 0; i < BIOME_NAMES.length; i++) {
            boolean found = (data.discoveredBiomes() & (1 << i)) != 0;
            boolean shrine = (data.discoveredLandmarks() & (1 << i)) != 0;
            int top = y + 109 + i * 23;
            line(graphics, (found ? "[VISITED] " : "[UNDISCOVERED] ") + BIOME_NAMES[i]
                + "  |  Shrine: " + (shrine ? "FOUND" : "UNKNOWN"),
                x + 8, top, found ? 0xFF8ADBB2 : 0xFF99A3AF);
            line(graphics, font.plainSubstrByWidth(found ? BIOME_HINTS[i] :
                 "Visit new Overworld terrain to unlock clues.", 380),
                x + 12, top + 10, 0xFFAFC4D5);
        }
        line(graphics, "Explore new chunks. Existing terrain will not be replaced.",
            x + 8, y + 251, 0xFFCCC9C0);
    }


    private void renderTrials(GuiGraphics graphics, SkillSnapshot data, int x, int y) {
        int[] trial = data.trials();
        line(graphics, "SHATTERED TRIALS | OPTIONAL NG+ BOSS MODIFIERS", x + 8, y + 78, 0xFFE4AF76);
        boolean unlocked = data.level() >= 60 && data.bossWins()[5] > 0 && data.ngCycle() >= 1;
        if (!unlocked || !isReplay(data)) {
            line(graphics, "Requires level 60, the Hollow King and an active NG+ chapter.",
                 x + 8, y + 103, 0xFFCCBDAA);
            line(graphics, "Shattered Trials never block normal dungeon completion.",
                 x + 8, y + 116, 0xFFAFC4D5);
            return;
        }
        int active = data.replayChapter();
        boolean won = (trial[2] & (1 << (active - 1))) != 0;
        line(graphics, "Current arena: " + CHAPTERS[active - 1].dungeon
            + " | Medals this cycle: " + Integer.bitCount(trial[2] & 63) + "/6",
            x + 8, y + 99, 0xFF9BDBC0);
        line(graphics, won ? "Medal already claimed for this chapter and cycle."
            : "Arm a modifier, then summon this chapter's normal boss.",
            x + 8, y + 115, won ? 0xFF8ADBB2 : 0xFFE0E3EA);
        line(graphics, "Hazard Pulse: a warned, chapter-specific area attack.",
            x + 8, y + 174, 0xFFCEDDF2);
        line(graphics, "Scarcity: healing halved while fighting the trial boss.",
            x + 8, y + 188, 0xFFCEDDF2);
        line(graphics, "Two modifiers together unlock in NG+ cycle 3.",
            x + 8, y + 202, 0xFF9DBAD2);
        line(graphics, trial[0] == active ? "ARMED: " + (trial[1] == 3 ? "Both modifiers"
             : trial[1] == 1 ? "Hazard Pulse" : "Scarcity") :
             "Trial records are saved across logout and NG+ cycles.",
             x + 8, y + 247, 0xFFDBBE81);
    }

    private void renderBounties(GuiGraphics graphics, SkillSnapshot data, int x, int y) {
        int[] bounty = data.bounties();
        line(graphics, "ADVENTURER'S BOUNTY BOARD | ONE ACTIVE CONTRACT",
             x + 8, y + 79, 0xFFE4AF76);
        line(graphics, "Hunt mobs, survey chapter biomes or defeat a chapter guardian.",
             x + 8, y + 98, 0xFFCEDDF2);
        line(graphics, "Contracts target your current chapter. Rewards never include relics.",
             x + 8, y + 112, 0xFFAFC4D5);
        if (bounty[0] > 0 && bounty[1] >= 1 && bounty[1] <= 6) {
            String mission = switch (bounty[0]) {
                case 1 -> "Hunt " + switch (bounty[1]) {
                    case 1 -> "Pillagers";
                    case 2 -> "Zombies";
                    case 3 -> "Skeletons";
                    case 4 -> "Spiders";
                    case 5 -> "Creepers";
                    default -> "Endermen";
                };
                case 2 -> "Survey " + BIOME_NAMES[bounty[1] - 1];
                case 3 -> "Defeat " + CHAPTERS[bounty[1] - 1].boss;
                default -> "Unknown";
            };
            line(graphics, "ACTIVE: " + mission, x + 8, y + 164, 0xFFEBCB8F);
            line(graphics, "Progress: " + bounty[2] + "/" + bounty[3]
                + (bounty[2] >= bounty[3] ? "  | READY TO CLAIM" : ""),
                x + 8, y + 182, bounty[2] >= bounty[3] ? 0xFF79D4AA : 0xFFC3C9D4);
        } else {
            line(graphics, bounty[4] > 0 ? "Next contract available in " + bounty[4] + " seconds."
                : "No active contract. Choose one above.",
                x + 8, y + 164, 0xFFB9D6C2);
        }
        line(graphics, "Rewards: bounded XP and iron, gold or amethyst.",
             x + 8, y + 244, 0xFFAFC4D5);
        line(graphics, "Completed contracts: " + bounty[5] + " | 10-minute claim cooldown",
             x + 8, y + 255, 0xFF9BCDAD);
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
