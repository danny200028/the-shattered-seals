package com.shatteredseals.client;

import com.shatteredseals.net.DungeonWaypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/** Optional client bridge to the pinned Xaero Minimap. The server can run without it. */
public final class XaeroWaypoints {
    public static void accept(DungeonWaypoint target) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || !minecraft.level.dimension().equals(Level.OVERWORLD)) return;
        try {
            if (save(target)) {
                minecraft.player.displayClientMessage(Component.literal("Map waypoint: " + target.name())
                    .withStyle(ChatFormatting.GOLD), false);
            }
        } catch (ReflectiveOperationException | LinkageError e) {
            // Coordinates were sent independently so they are available with any minimap.
            System.getLogger("The Shattered Seals").log(System.Logger.Level.DEBUG, "Xaero waypoint bridge unavailable", e);
        }
    }

    private static boolean save(DungeonWaypoint target) throws ReflectiveOperationException {
        Class<?> sessionClass = Class.forName("xaero.common.XaeroMinimapSession");
        Object session = sessionClass.getMethod("getCurrentSession").invoke(null);
        if (session == null) return false;
        Object manager = sessionClass.getMethod("getWaypointsManager").invoke(session);
        Object world = manager.getClass().getMethod("getCurrentWorld").invoke(manager);
        if (world == null) return false;
        Object set = world.getClass().getMethod("getCurrentSet").invoke(world);
        if (set == null) return false;

        String name = "Seal - " + target.name();
        Class<?> waypoint = Class.forName("xaero.common.minimap.waypoints.Waypoint");
        var getName = waypoint.getMethod("getName");
        var getX = waypoint.getMethod("getX");
        var getZ = waypoint.getMethod("getZ");
        for (Object existing : (Iterable<?>) set.getClass().getMethod("getWaypoints").invoke(set)) {
            if (name.equals(getName.invoke(existing)) && (int) getX.invoke(existing) == target.x()
                    && (int) getZ.invoke(existing) == target.z()) return true;
        }

        Class<?> color = Class.forName("xaero.hud.minimap.waypoint.WaypointColor");
        Object point = waypoint.getConstructor(int.class, int.class, int.class, String.class, String.class, color)
            .newInstance(target.x(), target.y(), target.z(), name, "S", color.getField("GOLD").get(null));
        set.getClass().getMethod("add", waypoint).invoke(set, point);
        try {
            Object io = manager.getClass().getMethod("getWorldManagerIO").invoke(manager);
            io.getClass().getMethod("saveWorld", Class.forName("xaero.hud.minimap.world.MinimapWorld")).invoke(io, world);
        } catch (ReflectiveOperationException | LinkageError e) {
            set.getClass().getMethod("remove", waypoint).invoke(set, point);
            throw e;
        }
        return true;
    }

    private XaeroWaypoints() {}
}
