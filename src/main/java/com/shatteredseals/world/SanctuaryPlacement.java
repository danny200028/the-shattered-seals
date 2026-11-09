package com.shatteredseals.world;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

/**
 * A one-use, player-chosen Sanctuary template placement. Its entrance/foyer is
 * centred on the clicked ground block, and a limited foundation follows slopes.
 * Every block is checked before any changes are made; no chests, bases, other
 * non-replaceable constructions or unloaded chunks may be overwritten.
 */
public final class SanctuaryPlacement {
    private static final int MAX_FOUNDATION_DROP = 8;
    public static boolean place(ServerPlayer player, BlockPos clicked, ItemStack stack) {
        ServerLevel level = player.serverLevel();
        if (player.getPersistentData().getBoolean("SSSanctuaryPlaced")) {
            notify(player, "Your one-time Sanctuary placement has already been used.", ChatFormatting.YELLOW);
            return false;
        }
        if (!player.getPersistentData().getBoolean("SSSanctuaryKeystoneGranted")
                || com.shatteredseals.adventure.DungeonChapter.score(player, "ss_vk_smug") <= 0) {
            notify(player, "Defeat the Smuggler King to unlock your Sanctuary.", ChatFormatting.YELLOW);
            return false;
        }
        var template = level.getStructureManager().get(ResourceLocation.fromNamespaceAndPath(
            ShatteredSeals.MOD_ID, "wardens_sanctuary"));
        if (template.isEmpty()) {
            notify(player, "Sanctuary blueprint unavailable. Report this build issue.", ChatFormatting.RED);
            return false;
        }
        var blueprint = template.get();
        int width = blueprint.getSize().getX(), length = blueprint.getSize().getZ();
        int height = blueprint.getSize().getY();
        BlockPos origin = clicked.above().offset(-width / 2, 0, -length / 2);
        if (origin.getY() - MAX_FOUNDATION_DROP <= level.getMinBuildHeight()
                || origin.getY() + height >= level.getMaxBuildHeight()) {
            notify(player, "Choose a ground level within the safe world height.", ChatFormatting.RED);
            return false;
        }
        for (int x = 0; x < width; x++) for (int z = 0; z < length; z++) {
            int drop = -1;
            for (int depth = 1; depth <= MAX_FOUNDATION_DROP; depth++) {
                BlockPos foot = origin.offset(x, -depth, z);
                if (!level.hasChunkAt(foot)) {
                    notify(player, "Move closer to the full 81 x 49 Sanctuary footprint before placing.", ChatFormatting.YELLOW);
                    return false;
                }
                BlockState state = level.getBlockState(foot);
                if (!state.canBeReplaced() && !state.getFluidState().isEmpty()) {
                    notify(player, "Clear liquids beneath the future foundation first.", ChatFormatting.YELLOW);
                    return false;
                }
                if (!state.canBeReplaced()) { drop = depth; break; }
            }
            if (drop == -1) {
                notify(player, "The terrain drops more than 8 blocks. Choose a gentler site.", ChatFormatting.YELLOW);
                return false;
            }
            for (int y = 0; y < height; y++) {
                BlockPos pos = origin.offset(x, y, z);
                if (!level.hasChunkAt(pos)) {
                    notify(player, "The entire Sanctuary footprint must be in loaded chunks.", ChatFormatting.YELLOW);
                    return false;
                }
                BlockState state = level.getBlockState(pos);
                if (!state.canBeReplaced() && !(y == 0 && naturalSurface(state))) {
                    notify(player, "Placement blocked by terrain or a structure. Choose a clear, flat site.", ChatFormatting.YELLOW);
                    return false;
                }
                if (level.getBlockEntity(pos) != null || !state.getFluidState().isEmpty()) {
                    notify(player, "Placement would overwrite a container or liquid. Site rejected.", ChatFormatting.RED);
                    return false;
                }
            }
        }
        // Intentional two-click confirmation. The player sees the footprint's
        // four corners before irreversible changes; a different site resets it.
        var data = player.getPersistentData();
        long tick = level.getGameTime();
        if (!data.contains("SSSanctuaryConfirmPos")
                || data.getLong("SSSanctuaryConfirmPos") != clicked.asLong()
                || tick < data.getLong("SSSanctuaryConfirmTick")
                || tick - data.getLong("SSSanctuaryConfirmTick") > 200) {
            data.putLong("SSSanctuaryConfirmPos", clicked.asLong());
            data.putLong("SSSanctuaryConfirmTick", tick);
            for (int cx : new int[] {0, width - 1}) {
                for (int cz : new int[] {0, length - 1}) {
                    BlockPos corner = origin.offset(cx, 2, cz);
                    level.sendParticles(ParticleTypes.END_ROD,
                        corner.getX() + 0.5, corner.getY() + 0.5, corner.getZ() + 0.5,
                        16, 0.2, 0.7, 0.2, 0.03);
                }
            }
            notify(player, "Sanctuary preview: " + width + " x " + length
                + " blocks, ground Y " + clicked.getY()
                + ". Right-click this SAME block again within 10 seconds to confirm.",
                ChatFormatting.AQUA);
            return false;
        }
        data.remove("SSSanctuaryConfirmPos");
        data.remove("SSSanctuaryConfirmTick");
        if (!blueprint.placeInWorld(level, origin, origin,
                new StructurePlaceSettings().setIgnoreEntities(false), level.random, 3)) {
            notify(player, "Sanctuary placement failed. The Keystone was not consumed.", ChatFormatting.RED);
            return false;
        }
        // Foundations only start after the blueprint was placed, to avoid
        // leaving an orphaned stone platform on a rejected placement.
        for (int x = 0; x < width; x++) for (int z = 0; z < length; z++) {
            for (int depth = 1; depth <= MAX_FOUNDATION_DROP; depth++) {
                BlockPos foot = origin.offset(x, -depth, z);
                if (!level.getBlockState(foot).canBeReplaced()) break;
                level.setBlock(foot, Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }
        data.putInt("SSSanctuaryX", clicked.getX());
        data.putInt("SSSanctuaryY", clicked.getY() + 1);
        data.putInt("SSSanctuaryZ", clicked.getZ());
        data.putBoolean("SSSanctuaryPlaced", true);
        if (!player.isCreative()) stack.shrink(1);
        notify(player, "WARDEN'S SANCTUARY ESTABLISHED! Chronicle Archive and trophy halls are ready.", ChatFormatting.GOLD);
        return true;
    }
    private static boolean naturalSurface(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)
            || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL)
            || state.is(Blocks.STONE) || state.is(Blocks.GRAVEL)
            || state.is(Blocks.SAND) || state.is(Blocks.MOSS_BLOCK)
            || state.is(Blocks.SNOW_BLOCK);
    }
    private static void notify(ServerPlayer player, String message, ChatFormatting color) {
        player.sendSystemMessage(Component.literal(message).withStyle(color));
    }
    private SanctuaryPlacement() {}
}
