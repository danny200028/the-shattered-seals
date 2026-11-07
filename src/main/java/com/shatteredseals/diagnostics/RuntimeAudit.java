package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.DungeonChapter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.fml.ModList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only world audit available to a normal survival player via
 * /shatteredseals audit. This does not award items, spawn bosses, change
 * scoreboard values or consume a Dragon Egg.
 */
public final class RuntimeAudit {
    private final List<String> findings = new ArrayList<>();
    private int passed;
    private int failed;
    private int warnings;

    public static int run(ServerPlayer player) {
        RuntimeAudit report = new RuntimeAudit();
        try {
            report.inspect(player);
        } catch (RuntimeException ex) {
            report.fail("Audit interrupted: " + ex.getClass().getSimpleName() + " " + ex.getMessage());
        }
        report.save(player);
        player.sendSystemMessage(Component.literal(
            "Shattered Seals runtime audit: " + report.passed + " PASS, "
                + report.failed + " FAIL, " + report.warnings + " WARN")
            .withStyle(report.failed == 0 ? ChatFormatting.GREEN : ChatFormatting.RED));
        int shown = 0;
        for (String line : report.findings) {
            if ((line.startsWith("FAIL|") || line.startsWith("WARN|")) && shown++ < 8) {
                player.sendSystemMessage(Component.literal(line).withStyle(
                    line.startsWith("FAIL|") ? ChatFormatting.RED : ChatFormatting.YELLOW));
            }
        }
        player.sendSystemMessage(Component.literal(
            "Report saved in this world's shatteredseals-tests/latest.txt. "
                + "Run the Windows Test Report collector from your CurseForge profile.")
            .withStyle(ChatFormatting.AQUA));
        return report.failed == 0 ? 1 : 0;
    }

    private void inspect(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        var server = player.getServer();
        check("Minecraft runtime has Java 21 or later",
            Runtime.version().feature() >= 21);
        check("The Shattered Seals version " + ShatteredSeals.VERSION,
            !ShatteredSeals.VERSION.isBlank());
        warnIf("Overworld selected for dungeon locating", level.dimension() == Level.OVERWORLD);
        warnIf("Hard difficulty is active", level.getDifficulty() == net.minecraft.world.Difficulty.HARD);
        warnIf("Xaero Minimap loaded", ModList.get().isLoaded("xaerominimap"));
        warnIf("Xaero World Map loaded", ModList.get().isLoaded("xaeroworldmap"));
        warnIf("Iris shader loader loaded", ModList.get().isLoaded("iris"));
        warnIf("Sodium renderer loaded", ModList.get().isLoaded("sodium"));

        var board = player.getScoreboard();
        String[] objectives = {
            "ss_level", "ss_xp", "ss_need", "ss_points", "ss_spent",
            "ss_path", "ss_ng", "ss_ngchapter", "ss_mxp", "ss_master",
            "ss_mpoints", "ss_rune1", "ss_side1", "ss_rune2", "ss_side2",
            "ss_rune3", "ss_side3", "ss_rune4", "ss_side4",
            "ss_rune5", "ss_side5", "ss_rune6", "ss_side6",
            "ss_reset_confirm", "ss_reset_timer", "ss_burst_cd", "ss_burst_cd2",
            "ss_burst_cd3", "ss_burst_cd4", "ss_ritual", "ss_switch"
        };
        for (String objective : objectives) {
            check("Server scoreboard objective " + objective,
                board.getObjective(objective) != null);
        }

        int levelScore = DungeonChapter.score(player, "ss_level");
        warnIf("Player progression initialised (level at least 1)", levelScore >= 1);
        check("Character level within designed 1..60 cap",
            levelScore >= 0 && levelScore <= 60);
        check("Current class is valid (unselected or one of four)",
            DungeonChapter.score(player, "ss_path") >= 0
                && DungeonChapter.score(player, "ss_path") <= 4);
        check("NG+ chapter number is valid (0..6)",
            DungeonChapter.score(player, "ss_ngchapter") >= 0
                && DungeonChapter.score(player, "ss_ngchapter") <= 6);
        check("Mastery spent/unspent points nonnegative",
            DungeonChapter.score(player, "ss_mpoints") >= 0);

        var structures = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (DungeonChapter chapter : DungeonChapter.values()) {
            String id = chapter.boss.toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
            check("Worldgen registry contains " + chapter.structure,
                structures.containsKey(chapter.structure));
            check("Chapter mob can be instantiated: " + chapter.miniboss,
                chapter.mob.create(level) != null);
            check("Chapter " + chapter.number() + " rune state in 0..3",
                DungeonChapter.score(player, chapter.puzzleScore()) >= 0
                    && DungeonChapter.score(player, chapter.puzzleScore()) <= 3);
            check("Chapter " + chapter.number() + " keeper state in 0..3",
                DungeonChapter.score(player, chapter.sideScore()) >= 0
                    && DungeonChapter.score(player, chapter.sideScore()) <= 3);

            for (String function : new String[] {
                "ritual/use_" + id,
                "ritual/activate_" + id,
                "boss/summon_" + id,
                "boss/" + id + "/phase_two",
                "boss/" + id + "/phase_three",
                "reward/" + id,
                "reward/first/" + id,
                "reward/repeat/" + id
            }) {
                check("Runtime function " + function, server.getFunctions()
                    .get(ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, function))
                    .isPresent());
            }

            check("Chapter boss chest loot " + chapter.structure.getPath(),
                tablePresent(level, "chests/" + chapter.structure.getPath()));
            check("Boss reward loot " + id, tablePresent(level, "boss/" + id));
            check("NG+ ritual sigil rolls correctly: " + id,
                replaySigilRolls(level, id));
        }

        for (String function : new String[] {
            "progression/tick", "progression/reset", "progression/reset_confirm",
            "progression/reset_apply", "ngplus/start", "mastery/shop",
            "boss/minion_cleanup", "combat/dispatch", "combat/dispatch_2",
            "combat/dispatch_3", "combat/dispatch_4"
        }) {
            check("Runtime function " + function, server.getFunctions()
                .get(ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, function))
                .isPresent());
        }

        // A runtime audit cannot determine whether actual terrain can generate
        // every structure, or whether HUD/shaders and combat look correct.
        notTested("Natural dungeon placement and nine-room traversal");
        notTested("Full six-boss combat, phase AI, attack damage and guarding");
        notTested("Native GUI controls, shader selection and map waypoints");
        notTested("Campaign advancement, respec consumption and NG+ after save/reload");
    }

    private static boolean tablePresent(ServerLevel level, String name) {
        ResourceKey<LootTable> id = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, name));
        return level.getServer().reloadableRegistries().getLootTable(id) != LootTable.EMPTY;
    }

    static boolean replaySigilRolls(ServerLevel level, String boss) {
        ResourceKey<LootTable> id = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, "ritual_replay/" + boss));
        LootTable table = level.getServer().reloadableRegistries().getLootTable(id);
        if (table == LootTable.EMPTY) return false;
        LootParams params = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, net.minecraft.world.phys.Vec3.atLowerCornerOf(
                net.minecraft.core.BlockPos.ZERO))
            .create(LootContextParamSets.CHEST);
        return table.getRandomItems(params).stream().anyMatch(stack -> {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            return data != null && boss.equals(data.copyTag().getString("ss_ritual"));
        });
    }

    private void check(String label, boolean ok) {
        if (ok) {
            passed++;
            findings.add("PASS|" + label);
        } else {
            fail(label);
        }
    }

    private void fail(String label) {
        failed++;
        findings.add("FAIL|" + label);
    }

    private void warnIf(String label, boolean ok) {
        if (ok) {
            passed++;
            findings.add("PASS|" + label);
        } else {
            warnings++;
            findings.add("WARN|" + label);
        }
    }

    private void notTested(String label) {
        findings.add("NOT_TESTED|" + label);
    }

    private void save(ServerPlayer player) {
        try {
            Path folder = player.getServer().getWorldPath(LevelResource.ROOT)
                .resolve("shatteredseals-tests");
            Files.createDirectories(folder);
            List<String> output = new ArrayList<>();
            output.add("THE SHATTERED SEALS SERVER RUNTIME AUDIT");
            output.add("VERSION|" + ShatteredSeals.VERSION);
            output.add("TIMESTAMP|" + Instant.now());
            output.add("SUMMARY|PASS=" + passed + "|FAIL=" + failed + "|WARN=" + warnings);
            output.addAll(findings);
            Files.write(folder.resolve("latest.txt"), output, StandardCharsets.UTF_8);
            // The console provides an external confirmation for the report collector.
            System.getLogger("ShatteredSealsAudit").log(System.Logger.Level.INFO,
                "Server runtime audit saved: {0}; pass={1}, fail={2}, warn={3}",
                folder.resolve("latest.txt"), passed, failed, warnings);
        } catch (IOException ex) {
            player.sendSystemMessage(Component.literal(
                "Could not write the audit report: " + ex.getMessage())
                .withStyle(ChatFormatting.RED));
        }
    }

    private RuntimeAudit() {}
}
