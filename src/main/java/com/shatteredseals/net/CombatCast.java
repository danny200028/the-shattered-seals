package com.shatteredseals.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Input event only. All combat authorization and cooldown checks are server-side. */
public record CombatCast(int slot) implements CustomPacketPayload {
    public static final Type<CombatCast> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "combat_cast"));
    public static final StreamCodec<ByteBuf, CombatCast> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, CombatCast::slot, CombatCast::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
