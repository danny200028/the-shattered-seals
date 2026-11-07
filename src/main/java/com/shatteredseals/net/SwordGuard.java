package com.shatteredseals.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** A short-lived player input intent, never an authority to reduce damage. */
public record SwordGuard(boolean guarding) implements CustomPacketPayload {
    public static final Type<SwordGuard> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "sword_guard"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SwordGuard> STREAM_CODEC = StreamCodec.of(
        (buf, value) -> buf.writeBoolean(value.guarding()),
        buf -> new SwordGuard(buf.readBoolean()));
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
