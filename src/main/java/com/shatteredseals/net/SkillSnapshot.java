package com.shatteredseals.net;

import com.shatteredseals.adventure.DungeonChapter;
import com.shatteredseals.adventure.AdventureContracts;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

/** Server-owned state; client cannot use this packet to change skill points. */
public record SkillSnapshot(int level, int xp, int requiredXp, int points,
                            int kills, int hostileKills, int activeClass, int masteryRank,
                            int[] branchRanks, int[] branchMasteries, int[] coreRanks, int[] cooldowns, int[] resources,
                            int ngCycle, int replayChapter, int masteryPoints, int[] masteryPerks,
                            int[] bossWins, int puzzleStep, int sideQuestState, int discoveredBiomes, int discoveredLandmarks,
                            int[] trials, int[] bounties, int[] swordMastery, int[] finalWorld, int[] options) implements CustomPacketPayload {
    public static final Type<SkillSnapshot> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "skill_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillSnapshot> STREAM_CODEC = StreamCodec.of(
        (buf, value) -> {
            buf.writeVarInt(value.level());
            buf.writeVarInt(value.xp());
            buf.writeVarInt(value.requiredXp());
            buf.writeVarInt(value.points());
            buf.writeVarInt(value.kills());
            buf.writeVarInt(value.hostileKills());
            buf.writeVarInt(value.activeClass());
            buf.writeVarInt(value.masteryRank());
            for (int i = 0; i < 12; i++) buf.writeVarInt(value.branchRanks()[i]);
            for (int i = 0; i < 12; i++) buf.writeVarInt(value.branchMasteries()[i]);
            for (int i = 0; i < 4; i++) buf.writeVarInt(value.coreRanks()[i]);
            for (int i = 0; i < 4; i++) buf.writeVarInt(value.cooldowns()[i]);
            for (int i = 0; i < 4; i++) buf.writeVarInt(value.resources()[i]);
            buf.writeVarInt(value.ngCycle());
            buf.writeVarInt(value.replayChapter());
            buf.writeVarInt(value.masteryPoints());
            for (int i = 0; i < 3; i++) buf.writeVarInt(value.masteryPerks()[i]);
            for (int i = 0; i < 6; i++) buf.writeVarInt(value.bossWins()[i]);
            buf.writeVarInt(value.puzzleStep());
            buf.writeVarInt(value.sideQuestState());
            buf.writeVarInt(value.discoveredBiomes());
            buf.writeVarInt(value.discoveredLandmarks());
            for (int i = 0; i < 4; i++) buf.writeVarInt(value.trials()[i]);
            for (int i = 0; i < 6; i++) buf.writeVarInt(value.bounties()[i]);
            for (int i = 0; i < 6; i++) buf.writeVarInt(value.swordMastery()[i]);
            for (int i = 0; i < 5; i++) buf.writeVarInt(value.finalWorld()[i]);
            for (int i = 0; i < 4; i++) buf.writeVarInt(value.options()[i]);
        },
        buf -> {
            int level = buf.readVarInt();
            int xp = buf.readVarInt();
            int requiredXp = buf.readVarInt();
            int points = buf.readVarInt();
            int kills = buf.readVarInt();
            int hostileKills = buf.readVarInt();
            int activeClass = buf.readVarInt();
            int masteryRank = buf.readVarInt();
            int[] ranks = new int[12];
            int[] masteries = new int[12];
            for (int i = 0; i < 12; i++) ranks[i] = buf.readVarInt();
            for (int i = 0; i < 12; i++) masteries[i] = buf.readVarInt();
            int[] coreRanks = new int[4];
            for (int i = 0; i < 4; i++) coreRanks[i] = buf.readVarInt();
            int[] cooldowns = new int[4];
            for (int i = 0; i < 4; i++) cooldowns[i] = buf.readVarInt();
            int[] resources = new int[4];
            for (int i = 0; i < 4; i++) resources[i] = buf.readVarInt();
            int ngCycle = buf.readVarInt();
            int replayChapter = buf.readVarInt();
            int masteryPoints = buf.readVarInt();
            int[] masteryPerks = new int[3];
            int[] bossWins = new int[6];
            for (int i = 0; i < 3; i++) masteryPerks[i] = buf.readVarInt();
            for (int i = 0; i < 6; i++) bossWins[i] = buf.readVarInt();
            int puzzleStep = buf.readVarInt();
            int sideState = buf.readVarInt();
            int biomes = buf.readVarInt();
            int landmarks = buf.readVarInt();
            int[] trials = new int[4];
            int[] bounties = new int[6];
            for (int i = 0; i < trials.length; i++) trials[i] = buf.readVarInt();
            for (int i = 0; i < bounties.length; i++) bounties[i] = buf.readVarInt();
            int[] swordMastery = new int[6];
            for (int i = 0; i < 6; i++) swordMastery[i] = buf.readVarInt();
            int[] finalWorld = new int[5];
            for (int i = 0; i < 5; i++) finalWorld[i] = buf.readVarInt();
            int[] options = new int[4];
            for (int i = 0; i < 4; i++) options[i] = buf.readVarInt();
            return new SkillSnapshot(level, xp, requiredXp, points, kills, hostileKills,
                                     activeClass, masteryRank, ranks, masteries, coreRanks, cooldowns, resources,
                                     ngCycle, replayChapter, masteryPoints, masteryPerks, bossWins,
                                     puzzleStep, sideState, biomes, landmarks, trials, bounties, swordMastery, finalWorld, options);
        });

    private static int read(Scoreboard scoreboard, ServerPlayer player, String objective) {
        Objective o = scoreboard.getObjective(objective);
        if (o == null) return 0;
        ReadOnlyScoreInfo info = scoreboard.getPlayerScoreInfo(player, o);
        return info == null ? 0 : info.value();
    }

    public static SkillSnapshot capture(ServerPlayer player) {
        Scoreboard board = player.getScoreboard();
        DungeonChapter chapter = DungeonChapter.current(player);
        int[] ranks = new int[12];
        int[] masteries = new int[12];
        for (int i = 0; i < 12; i++) {
            int classIndex = i / 3 + 1;
            int branchIndex = i % 3 + 1;
            ranks[i] = read(board, player, "ss_b" + classIndex + branchIndex);
            masteries[i] = read(board, player, "ss_a" + classIndex + branchIndex);
        }
        return new SkillSnapshot(
            read(board, player, "ss_level"), read(board, player, "ss_xp"),
            read(board, player, "ss_need"), read(board, player, "ss_points"),
            read(board, player, "ss_dkills"), read(board, player, "ss_hkills"), read(board, player, "ss_path"),
            read(board, player, "ss_master"), ranks, masteries,
            new int[] {read(board, player, "ss_t_van"), read(board, player, "ss_t_ward"),
                       read(board, player, "ss_t_path"), read(board, player, "ss_t_arc")},
            new int[] {read(board, player, "ss_burst_cd"), read(board, player, "ss_burst_cd2"),
                       read(board, player, "ss_burst_cd3"), read(board, player, "ss_burst_cd4")},
            new int[] {read(board, player, "ss_fury"), read(board, player, "ss_guard"),
                       read(board, player, "ss_focus"), read(board, player, "ss_arcane")},
            read(board, player, "ss_ng"), read(board, player, "ss_ngchapter"),
            read(board, player, "ss_mpoints"),
            new int[] {read(board, player, "ss_mp_guard"), read(board, player, "ss_mp_haste"),
                       read(board, player, "ss_mp_fury")},
            new int[] {read(board, player, "ss_vk_smug"), read(board, player, "ss_vk_cind"),
                       read(board, player, "ss_vk_holl"), read(board, player, "ss_vk_arca"),
                       read(board, player, "ss_vk_six"), read(board, player, "ss_vk_king")},
            chapter == null ? 0 : read(board, player, chapter.puzzleScore()),
            chapter == null ? 0 : read(board, player, chapter.sideScore()),
            player.getPersistentData().getInt("SSBiomeDiscoveryMask"),
            player.getPersistentData().getInt("SSLandmarkDiscoveryMask"),
            AdventureContracts.trialState(player), AdventureContracts.bountyState(player),
            com.shatteredseals.combat.SwordMastery.snapshot(player),
            com.shatteredseals.adventure.FinalAdventure.state(player),
            com.shatteredseals.adventure.FinalAdventure.options(player));
    }
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
