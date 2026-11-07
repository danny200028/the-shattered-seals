package com.shatteredseals.net;

import com.shatteredseals.client.ClientSkillState;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "shatteredseals", bus = EventBusSubscriber.Bus.MOD)
public final class SkillNetworking {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("2");
        registrar.playToServer(SkillRequest.TYPE, SkillRequest.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                // All score reads happen on the server. No client-supplied values are trusted.
                PacketDistributor.sendToPlayer(player, SkillSnapshot.capture(player));
            }
        });
        registrar.playToServer(CombatCast.TYPE, CombatCast.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player && payload.slot() >= 1 && payload.slot() <= 4) {
                // Trigger objective is enabled by the server tick. The player cannot
                // choose damage, target, class or bypass server-side cooldowns.
                player.getServer().getCommands().performPrefixedCommand(
                    player.createCommandSourceStack(), "trigger ss_burst set " + payload.slot());
            }
        });
        registrar.playToClient(SkillSnapshot.TYPE, SkillSnapshot.STREAM_CODEC,
            (payload, context) -> ClientSkillState.accept(payload));
    }
    private SkillNetworking() {}
}
