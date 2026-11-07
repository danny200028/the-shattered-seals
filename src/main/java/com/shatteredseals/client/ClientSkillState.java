package com.shatteredseals.client;

import com.shatteredseals.net.SkillRequest;
import com.shatteredseals.net.SkillSnapshot;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;

/** Client cache of server-confirmed values, never the source of truth. */
public final class ClientSkillState {
    private static SkillSnapshot latest;
    private ClientSkillState() {}

    public static void accept(SkillSnapshot value) {
        Minecraft.getInstance().execute(() -> {
            latest = value;
            if (Minecraft.getInstance().screen instanceof SkillTreeScreen screen) screen.refresh();
        });
    }
    public static SkillSnapshot current() { return latest; }
    public static void clear() { latest = null; }
    public static void request() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.connection != null)
            PacketDistributor.sendToServer(new SkillRequest(1));
    }
}
