package com.shatteredseals.adventure;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Operator-only entry point for testing the real secret encounter. */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class SecretBossDebugCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ss")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("debug")
                .then(Commands.literal("summon")
                    .then(Commands.literal("unbound_warden")
                        .executes(context -> FinalAdventure.summonSecret(
                            context.getSource().getPlayerOrException(), true))))));
    }

    private SecretBossDebugCommand() {}
}
