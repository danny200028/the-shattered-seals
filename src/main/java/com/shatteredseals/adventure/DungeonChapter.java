package com.shatteredseals.adventure;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;

/** The order shared by the Codex, arena scripts and generated dungeon markers. */
public enum DungeonChapter {
    SMUGGLERS("smugglers_den", "Smugglers' Den", "Smuggler King", "ss_vk_smug", 8,
        "Mara's Lost Cargo", "The fleet sailed at dawn, crossed the tide, and left only ash.",
        "Brineblade Lieutenant", EntityType.PILLAGER, Items.CROSSBOW, 38, 0, 1, 2),
    CINDER("cinder_citadel", "Cinder Citadel", "Cinder Warden", "ss_vk_cind", 16,
        "The Missing Crucible", "From ash rose the sun. Only the tide could quench it.",
        "Crucible Sentinel", EntityType.BLAZE, Items.AIR, 48, 2, 0, 1),
    HOLLOW("hollow_crypt", "Hollow Crypt", "Hollow Knight", "ss_vk_holl", 25,
        "A Debt to the Buried", "The tide buried the ashes until the sun found the grave.",
        "Gravebound Marshal", EntityType.WITHER_SKELETON, Items.IRON_SWORD, 60, 1, 2, 0),
    ARCANE("arcane_observatory", "Arcane Observatory", "Arcane Seer", "ss_vk_arca", 35,
        "Recover the Star Chart", "The tide reflected the sun before the sky fell to ash.",
        "Astral Bowkeeper", EntityType.STRAY, Items.BOW, 72, 1, 0, 2),
    SIXFOLD("sixfold_temple", "Sixfold Temple", "Sixfold Guardian", "ss_vk_six", 45,
        "The Keeper's Vow", "At dawn the sun burned the grove to ash. The tide renewed it.",
        "Jade Oathbreaker", EntityType.HUSK, Items.IRON_AXE, 90, 0, 2, 1),
    KING("hollow_kings_fortress", "Hollow King's Fortress", "Hollow King", "ss_vk_king", 55,
        "Break the Black Standard", "The world turned to ash. Then came the tide. At last, the sun.",
        "Black Standard Behemoth", EntityType.RAVAGER, Items.AIR, 110, 2, 1, 0);

    public final ResourceLocation structure;
    public final String dungeon;
    public final String boss;
    public final String victoryScore;
    public final int recommendedLevel;
    public final String sideQuest;
    public final String clue;
    public final String miniboss;
    public final EntityType<? extends Mob> mob;
    public final Item weapon;
    public final int health;
    private final int[] runeOrder;

    DungeonChapter(String structure, String dungeon, String boss, String victoryScore, int recommendedLevel,
                   String sideQuest, String clue, String miniboss, EntityType<? extends Mob> mob,
                   Item weapon, int health, int... runeOrder) {
        this.structure = ResourceLocation.fromNamespaceAndPath("shatteredseals", structure);
        this.dungeon = dungeon;
        this.boss = boss;
        this.victoryScore = victoryScore;
        this.recommendedLevel = recommendedLevel;
        this.sideQuest = sideQuest;
        this.clue = clue;
        this.miniboss = miniboss;
        this.mob = mob;
        this.weapon = weapon;
        this.health = health;
        this.runeOrder = runeOrder;
    }

    public int number() { return ordinal() + 1; }
    public String tag() { return "ss_chapter_" + number(); }
    public String puzzleScore() { return "ss_rune" + number(); }
    public String replaySigilLoot() {
        return "shatteredseals:ritual_replay/" + boss.toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
    }
    public String sideScore() { return "ss_side" + number(); }
    public int runeAt(int step) { return runeOrder[step]; }

    public static DungeonChapter current(ServerPlayer player) {
        DungeonChapter[] chapters = values();
        int replay = score(player, "ss_ngchapter");
        if (replay >= 1 && replay <= chapters.length) return chapters[replay - 1];
        for (DungeonChapter chapter : chapters) {
            if (score(player, chapter.victoryScore) < 1) return chapter;
        }
        return null;
    }

    public static int score(ServerPlayer player, String name) {
        Objective objective = player.getScoreboard().getObjective(name);
        if (objective == null) return 0;
        ReadOnlyScoreInfo score = player.getScoreboard().getPlayerScoreInfo(player, objective);
        return score == null ? 0 : score.value();
    }

    public static void setScore(ServerPlayer player, String name, int value) {
        Objective objective = player.getScoreboard().getObjective(name);
        if (objective != null) player.getScoreboard().getOrCreatePlayerScore(player, objective).set(value);
    }
}
