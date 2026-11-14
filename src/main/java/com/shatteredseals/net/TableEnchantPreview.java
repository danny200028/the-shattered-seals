package com.shatteredseals.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * One server-authoritative preview per enchanting-table menu state.
 * The packet is read-only: it cannot purchase an enchantment or alter XP.
 */
public record TableEnchantPreview(int menuId, int itemId, int seed, int[] costs,
                                  String[] enchantments) implements CustomPacketPayload {
    public static final Type<TableEnchantPreview> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath("shatteredseals", "table_enchant_preview"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TableEnchantPreview> STREAM_CODEC =
        StreamCodec.of((buf, packet) -> {
            buf.writeVarInt(packet.menuId());
            buf.writeVarInt(packet.itemId());
            buf.writeInt(packet.seed());
            for (int i = 0; i < 3; i++) buf.writeVarInt(packet.costs()[i]);
            for (int i = 0; i < 3; i++) buf.writeUtf(packet.enchantments()[i], 4096);
        }, buf -> {
            int menuId = buf.readVarInt();
            int itemId = buf.readVarInt();
            int seed = buf.readInt();
            int[] costs = new int[3];
            String[] enchants = new String[3];
            for (int i = 0; i < 3; i++) costs[i] = buf.readVarInt();
            for (int i = 0; i < 3; i++) enchants[i] = buf.readUtf(4096);
            return new TableEnchantPreview(menuId, itemId, seed, costs, enchants);
        });

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
