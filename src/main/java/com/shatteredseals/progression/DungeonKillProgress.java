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
 * Hostile mobs killed by players anywhere count toward RPG kill XP and milestones.
 * The original ss_dkills objective name remains unchanged to preserve existing saves.
 * Passive animals never count. Dungeon crossbow repair is unrelated.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class DungeonKillProgress {
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
        var board = player.getScoreboard();
        var objective = board.getObjective("ss_dkills");
        if (objective == null) return;
        int previous = DungeonChapter.score(player, "ss_dkills");
        if (previous >= 0 && previous < Integer.MAX_VALUE)
            board.getOrCreatePlayerScore(player, objective).set(previous + 1);
    }

    /** Hostile Minecraft monsters in any biome/dimension, including dungeon mobs. */
    public static boolean isEligibleHostileMob(Mob victim) {
        // Enemy is the vanilla hostile-mob category, not passive farm animals.
        // The killer must also be a ServerPlayer, checked by the death handler.
        return victim instanceof Enemy;
    }

    private DungeonKillProgress() {}
}
