package com.shatteredseals.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Server-located Overworld target. The client only renders or saves the waypoint. */
public record DungeonWaypoint(String name, int x, int y, int z) implements CustomPacketPayload {
    public static final Type<DungeonWaypoint> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "dungeon_waypoint"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DungeonWaypoint> STREAM_CODEC = StreamCodec.of(
        (buf, value) -> {
            buf.writeUtf(value.name(), 80);
            buf.writeInt(value.x());
            buf.writeInt(value.y());
            buf.writeInt(value.z());
        },
        buf -> new DungeonWaypoint(buf.readUtf(80), buf.readInt(), buf.readInt(), buf.readInt()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
