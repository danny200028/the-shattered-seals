package com.shatteredseals.net;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

/** Client asks the authoritative server for an RPG progression snapshot. */
public record SkillRequest(int nonce) implements CustomPacketPayload {
    public static final Type<SkillRequest> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "skill_request"));
    public static final StreamCodec<ByteBuf, SkillRequest> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, SkillRequest::nonce, SkillRequest::new);
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
