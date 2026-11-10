package com.shatteredseals.world;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

/**
 * Prevent unwanted vanilla mob births in or around the Warden's Sanctuary.
 * Does NOT remove existing creatures, pets, boss entities, or mobs summoned
 * explicitly by players or the Shattered Seals storyline.
 *
 * The original Sanctuary foyer marker is present in all saves, including
 * v1.0.58-v1.0.64, so older built Sanctuaries benefit without re-placement.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class SanctuarySpawnWard {
    // 81x49 footprint centred on (40,24), with 12 blocks of safe grounds
    // beyond the outer walls. Strict axis-aligned bounds avoid round gaps.
    public static final int HORIZONTAL_HALF_WIDTH = 52;
    public static final int HORIZONTAL_HALF_DEPTH = 36;

    public static boolean insideWard(BlockPos foyer, BlockPos proposed) {
        return Math.abs(proposed.getX() - foyer.getX()) <= HORIZONTAL_HALF_WIDTH
            && Math.abs(proposed.getZ() - foyer.getZ()) <= HORIZONTAL_HALF_DEPTH
            && proposed.getY() >= foyer.getY() - 32
            && proposed.getY() <= foyer.getY() + 64;
    }

    private static boolean automatedSpawn(MobSpawnType reason) {
        // Manual spawn eggs/commands, placed pets, breeding, buckets and
        // storyline summoned mobs remain available to the player.
        return reason != MobSpawnType.COMMAND
            && reason != MobSpawnType.SPAWN_EGG
            && reason != MobSpawnType.DISPENSER
            && reason != MobSpawnType.MOB_SUMMONED
            && reason != MobSpawnType.BREEDING
            && reason != MobSpawnType.BUCKET
            && reason != MobSpawnType.CONVERSION;
    }

    @SubscribeEvent
    public static void onMobSpawn(MobSpawnEvent.PositionCheck event) {
        if (!automatedSpawn(event.getSpawnType())) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) return;
        if (!"minecraft".equals(BuiltInRegistries.ENTITY_TYPE.getKey(
                event.getEntity().getType()).getNamespace())) return;

        BlockPos target = BlockPos.containing(event.getX(), event.getY(), event.getZ());
        // Search loaded marker entities; never load world chunks or mutate mobs.
        // A foyer marker is the authoritative location for every Sanctuary.
        for (Marker marker : level.getEntitiesOfClass(Marker.class,
                new AABB(target).inflate(72.0, 96.0, 72.0),
                m -> m.getTags().contains("ss_sanctuary_foyer"))) {
            if (insideWard(marker.blockPosition(), target)) {
                event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
                return;
            }
        }
    }

    private SanctuarySpawnWard() {}
}
