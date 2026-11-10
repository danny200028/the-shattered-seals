package com.shatteredseals.world;

import com.shatteredseals.ShatteredSeals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

/**
 * A confirmed, one-use Sanctuary placement that LEVELS naturally generated
 * hills, stone, dirt, vegetation and water. The player no longer needs an
 * 81x49 flat clearing. All blocks in the full template volume and foundations
 * are preflighted before anything is modified.
 *
 * Chests/block entities, obsidian, crafted masonry, player flooring and
 * unbreakable blocks are rejected. Naturally-looking player constructions
 * made of logs, leaves, dirt or stone cannot be distinguished from terrain,
 * so a copied world and the explicit two-click warning are still important.
 */
public final class SanctuaryPlacement {
    private static final int MAX_FOUNDATION_DROP = 32;

    /**
     * May be levelled by Sanctuary placement; intentionally excludes crafted
     * building materials, inventories, farms, machinery and bedrock.
     * Used by server GameTests to guard against accidental policy changes.
     */
    public static boolean canClear(BlockState state) {
        return state.isAir() || state.canBeReplaced()
            || state.is(Blocks.WATER) || state.is(Blocks.LAVA)
            || state.is(BlockTags.DIRT)
            || state.is(BlockTags.BASE_STONE_OVERWORLD)
            || state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)
            || state.is(BlockTags.SAND) || state.is(BlockTags.FLOWERS)
            || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY)
            || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.MUD)
            || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.ICE)
            || state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE)
            || state.is(Blocks.CALCITE) || state.is(Blocks.TUFF)
            || state.is(Blocks.GRANITE) || state.is(Blocks.DIORITE)
            || state.is(Blocks.ANDESITE) || state.is(Blocks.DRIPSTONE_BLOCK)
            || state.is(Blocks.BASALT) || state.is(Blocks.SMOOTH_BASALT);
    }

    private static boolean naturalSupport(BlockState state) {
        // Trees are vegetation, not a stable foundation. Continue through
        // canopy/trunks to find soil/stone, filling gaps with stone bricks.
        return !state.canBeReplaced() && state.getFluidState().isEmpty()
            && !state.is(BlockTags.LOGS) && !state.is(BlockTags.LEAVES);
    }

    public static boolean place(ServerPlayer player, BlockPos clicked, ItemStack stack) {
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(Level.OVERWORLD)) {
            notify(player, "Place the Sanctuary in the Overworld.", ChatFormatting.YELLOW);
            return false;
        }
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
        int width = blueprint.getSize().getX();
        int length = blueprint.getSize().getZ();
        int height = blueprint.getSize().getY();
        BlockPos origin = clicked.above().offset(-width / 2, 0, -length / 2);
        BlockPos farCorner = origin.offset(width - 1, 0, length - 1);
        if (!level.getWorldBorder().isWithinBounds(origin)
                || !level.getWorldBorder().isWithinBounds(farCorner)) {
            notify(player, "The Sanctuary footprint crosses the world border.", ChatFormatting.YELLOW);
            return false;
        }
        if (origin.getY() - MAX_FOUNDATION_DROP <= level.getMinBuildHeight()
                || origin.getY() + height >= level.getMaxBuildHeight()) {
            notify(player, "Choose terrain within the safe world height.", ChatFormatting.RED);
            return false;
        }
        // Validate the ENTIRE footprint before the first terrain change.
        // This includes high ground at all elevations, not just the floor.
        List<BlockPos> toClear = new ArrayList<>();
        for (int x = 0; x < width; x++) for (int z = 0; z < length; z++) {
            boolean supported = false;
            for (int depth = 1; depth <= MAX_FOUNDATION_DROP; depth++) {
                BlockPos foot = origin.offset(x, -depth, z);
                if (!level.hasChunkAt(foot)) {
                    notify(player, "Move near the centre so every Sanctuary chunk is loaded.",
                        ChatFormatting.YELLOW);
                    return false;
                }
                BlockState state = level.getBlockState(foot);
                if (level.getBlockEntity(foot) != null || !canClear(state)) {
                    notify(player, "A protected block lies beneath the Sanctuary foundation.",
                        ChatFormatting.RED);
                    return false;
                }
                if (naturalSupport(state)) { supported = true; break; }
            }
            if (!supported) {
                notify(player, "Ground drops more than 32 blocks below the Sanctuary. Move off the cliff.",
                    ChatFormatting.YELLOW);
                return false;
            }
            for (int y = 0; y < height; y++) {
                BlockPos pos = origin.offset(x, y, z);
                if (!level.hasChunkAt(pos)) {
                    notify(player, "Move near the centre so every Sanctuary chunk is loaded.",
                        ChatFormatting.YELLOW);
                    return false;
                }
                BlockState state = level.getBlockState(pos);
                if (level.getBlockEntity(pos) != null || !canClear(state)) {
                    notify(player, "Protected construction at X " + pos.getX()
                        + " Y " + pos.getY() + " Z " + pos.getZ()
                        + ". Move the Keystone away from buildings or chests.",
                        ChatFormatting.RED);
                    return false;
                }
                if (!state.isAir()) toClear.add(pos);
            }
        }
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
                + ", " + toClear.size() + " natural blocks will be CLEARED (hills, trees, water). "
                + "Right-click the SAME block within 10 seconds to confirm. "
                + "Back up any dirt/wood/stone builds in the footprint.",
                ChatFormatting.AQUA);
            return false;
        }
        // Clear natural hills and trees above the floors and beneath gables;
        // template placement alone cannot remove vegetation in unoccupied
        // roof-template voxels. Keep a rollback map for failed placement.
        Map<BlockPos, BlockState> replaced = new HashMap<>();
        for (BlockPos pos : toClear) {
            replaced.put(pos, level.getBlockState(pos));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        }
        if (!blueprint.placeInWorld(level, origin, origin,
                new StructurePlaceSettings().setIgnoreEntities(false), level.random, 3)) {
            for (var entry : replaced.entrySet())
                level.setBlock(entry.getKey(), entry.getValue(), 3);
            notify(player, "Sanctuary placement failed; cleared terrain restored and Keystone retained.",
                ChatFormatting.RED);
            return false;
        }
        // Natural supporting terrain is never destroyed below the base; fill
        // holes, water and vegetation down to the first stable solid block.
        for (int x = 0; x < width; x++) for (int z = 0; z < length; z++) {
            for (int depth = 1; depth <= MAX_FOUNDATION_DROP; depth++) {
                BlockPos foot = origin.offset(x, -depth, z);
                if (naturalSupport(level.getBlockState(foot))) break;
                level.setBlock(foot, Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }
        data.remove("SSSanctuaryConfirmPos");
        data.remove("SSSanctuaryConfirmTick");
        data.putInt("SSSanctuaryX", clicked.getX());
        data.putInt("SSSanctuaryY", clicked.getY() + 1);
        data.putInt("SSSanctuaryZ", clicked.getZ());
        data.putBoolean("SSSanctuaryPlaced", true);
        if (!player.isCreative()) stack.shrink(1);
        notify(player, "WARDEN'S SANCTUARY ESTABLISHED! The hillside was levelled safely.",
            ChatFormatting.GOLD);
        return true;
    }

    private static void notify(ServerPlayer player, String message, ChatFormatting color) {
        player.sendSystemMessage(Component.literal(message).withStyle(color));
    }
    private SanctuaryPlacement() {}
}
