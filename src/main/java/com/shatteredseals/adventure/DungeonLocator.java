package com.shatteredseals.adventure;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.net.DungeonWaypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class DungeonLocator {
    private static final String LAST_SEARCH = "ShatteredSealsLastLocator";

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("shatteredseals")
            .then(Commands.literal("locate")
                .executes(context -> locate(context.getSource().getPlayerOrException()))));
    }

    private static int locate(ServerPlayer player) {
        DungeonChapter chapter = DungeonChapter.current(player);
        if (chapter == null) {
            tell(player, "All seals are broken. Use the Codex to start a New Game+ circuit.", ChatFormatting.YELLOW);
            return 0;
        }
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(Level.OVERWORLD)) {
            tell(player, "Return to the Overworld to locate " + chapter.dungeon + ".", ChatFormatting.YELLOW);
            return 0;
        }
        long now = level.getGameTime();
        var data = player.getPersistentData();
        if (data.contains(LAST_SEARCH) && now - data.getLong(LAST_SEARCH) < 60) {
            tell(player, "Give the locator a moment before searching again.", ChatFormatting.YELLOW);
            return 0;
        }
        data.putLong(LAST_SEARCH, now);
        BlockPos pos = level.findNearestMapStructure(
            TagKey.create(Registries.STRUCTURE, chapter.structure), player.blockPosition(), 100, false);
        if (pos == null) {
            tell(player, "No " + chapter.dungeon + " found nearby. Explore farther in the Overworld and try again.",
                ChatFormatting.YELLOW);
            return 0;
        }
        int y = level.hasChunkAt(pos) ? level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) : 80;
        tell(player, chapter.dungeon + " | X " + pos.getX() + "  Z " + pos.getZ(), ChatFormatting.AQUA);
        PacketDistributor.sendToPlayer(player, new DungeonWaypoint(chapter.dungeon, pos.getX(), y, pos.getZ()));
        return 1;
    }

    private static void tell(ServerPlayer player, String text, ChatFormatting color) {
        player.sendSystemMessage(Component.literal(text).withStyle(color));
    }

    private DungeonLocator() {}
}
