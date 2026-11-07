package com.shatteredseals.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.shatteredseals.net.CombatCast;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public final class SkillTreeClient {
    public static final KeyMapping OPEN_SKILLS = new KeyMapping(
        "key.shatteredseals.open_skills", InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_K, "key.categories.shatteredseals");

    public static final KeyMapping CAST_ABILITY = new KeyMapping(
        "key.shatteredseals.cast_ability", InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_Q, "key.categories.shatteredseals");

    public static final KeyMapping CAST_2 = new KeyMapping(
        "key.shatteredseals.cast_2", InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R, "key.categories.shatteredseals");

    public static final KeyMapping CAST_3 = new KeyMapping(
        "key.shatteredseals.cast_3", InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_V, "key.categories.shatteredseals");

    public static final KeyMapping CAST_4 = new KeyMapping(
        "key.shatteredseals.cast_4", InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G, "key.categories.shatteredseals");

    @EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SKILLS);
            event.register(CAST_ABILITY);
            event.register(CAST_2);
            event.register(CAST_3);
            event.register(CAST_4);
        }
    }

    @EventBusSubscriber(modid = "shatteredseals", value = Dist.CLIENT)
    public static final class GameEvents {
        private static int snapshotTicker = 0;
        @SubscribeEvent
        public static void tick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                ClientSkillState.clear();
                snapshotTicker = 0;
                return;
            }
            if (++snapshotTicker >= 5) {
                snapshotTicker = 0;
                ClientSkillState.request();
            }
            if (mc.screen != null) return;
            while (OPEN_SKILLS.consumeClick()) mc.setScreen(new SkillTreeScreen());
            while (CAST_ABILITY.consumeClick()) PacketDistributor.sendToServer(new CombatCast(1));
            while (CAST_2.consumeClick()) PacketDistributor.sendToServer(new CombatCast(2));
            while (CAST_3.consumeClick()) PacketDistributor.sendToServer(new CombatCast(3));
            while (CAST_4.consumeClick()) PacketDistributor.sendToServer(new CombatCast(4));
        }
    }
    private SkillTreeClient() {}
}
