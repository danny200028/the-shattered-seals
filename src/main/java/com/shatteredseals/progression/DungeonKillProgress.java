package com.shatteredseals.progression;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.DungeonChapter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Hostile mobs killed by players anywhere give character XP; only dungeon enemies earn milestones.
 * Keep the original ss_dkills milestone score unchanged for existing worlds.
 * Passive animals never count. Dungeon crossbow repair is unrelated.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class DungeonKillProgress {
    private static final TagKey<Structure> DUNGEONS = TagKey.create(Registries.STRUCTURE,
        ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, "dungeon_kill_locations"));

    /**
     * Existing 1.21.1 dungeon spawners saved entity NBT without a weapon.
     * Those pillagers skip normal crossbow equipment setup and cannot fire.
     * Repair only unarmed Smugglers' Den pillagers, never player equipment
     * or ordinary village patrols, including on legacy chunk/entity load.
     */
    @SubscribeEvent
    public static void onSmugglersDenPillagerSpawn(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof Mob mob)
                || mob.getType() != EntityType.PILLAGER || !mob.getMainHandItem().isEmpty()
                || mob.getTags().contains("ss_boss")) return;
        if (mob.getTags().contains("ss_chapter_1")
                || isInDungeon(level, mob.blockPosition(), "smugglers_den")) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        }
    }

    private static boolean isInDungeon(ServerLevel level, net.minecraft.core.BlockPos pos, String structure) {
        return level.structureManager().getStructureWithPieceAt(pos, TagKey.create(
            Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, structure))).isValid();
    }

    @SubscribeEvent
    public static void onHostileMobDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob victim)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !isEligibleHostileMob(victim)) return;
        increment(player, "ss_hkills"); // Any hostile mob grants character XP.
        if (isEligibleDungeonEnemy(victim)) increment(player, "ss_dkills"); // Milestones only.
    }

    /** Hostile Minecraft monsters in any biome/dimension, including dungeon mobs. */
    public static boolean isEligibleHostileMob(Mob victim) {
        // Enemy is the vanilla hostile-mob category, not passive farm animals.
        // The killer must also be a ServerPlayer, checked by the death handler.
        return victim instanceof Enemy;
    }

    /** Dungeon enemies include tagged encounters and old dungeon spawner hostiles. */
    public static boolean isEligibleDungeonEnemy(Mob victim) {
        if (!isEligibleHostileMob(victim)) return false;
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

    private static void increment(ServerPlayer player, String name) {
        var board = player.getScoreboard();
        var objective = board.getObjective(name);
        if (objective == null) return;
        int previous = DungeonChapter.score(player, name);
        if (previous >= 0 && previous < Integer.MAX_VALUE)
            board.getOrCreatePlayerScore(player, objective).set(previous + 1);
    }

    private DungeonKillProgress() {}
}
