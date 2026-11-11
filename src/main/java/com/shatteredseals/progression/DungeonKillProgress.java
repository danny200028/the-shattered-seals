package com.shatteredseals.progression;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.DungeonChapter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Only hostile dungeon encounters count toward kill milestones and RPG kill XP.
 * The structure check covers pre-update dungeon spawners whose mobs lack a custom tag.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class DungeonKillProgress {
    private static final TagKey<Structure> DUNGEONS = TagKey.create(Registries.STRUCTURE,
        ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, "dungeon_kill_locations"));

    @SubscribeEvent
    public static void onDungeonEnemyDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob victim)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !isEligibleDungeonEnemy(victim)) return;
        var board = player.getScoreboard();
        var objective = board.getObjective("ss_dkills");
        if (objective == null) return;
        int previous = DungeonChapter.score(player, "ss_dkills");
        if (previous >= 0 && previous < Integer.MAX_VALUE)
            board.getOrCreatePlayerScore(player, objective).set(previous + 1);
    }

    /** Dedicated encounter tags or a real dungeon structure; never arbitrary vanilla farm mobs. */
    public static boolean isEligibleDungeonEnemy(Mob victim) {
        if (!(victim instanceof Enemy)) return false;
        if (victim.getTags().contains("ss_dungeon_enemy")
                || victim.getTags().contains("ss_boss")
                || victim.getTags().contains("ss_minion")
                || victim.getTags().contains("ss_miniboss")
                || victim.getTags().contains("ss_depths_guardian")
                || victim.getTags().contains("ss_secret_boss")
                || victim.getTags().stream().anyMatch(tag -> tag.startsWith("ss_elite_"))) return true;
        if (!(victim.level() instanceof ServerLevel level)) return false;
        return level.structureManager().getStructureWithPieceAt(victim.blockPosition(), DUNGEONS).isValid();
    }

    private DungeonKillProgress() {}
}
