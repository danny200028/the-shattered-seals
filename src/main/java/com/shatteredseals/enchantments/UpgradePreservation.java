package com.shatteredseals.enchantments;

import com.shatteredseals.ShatteredSeals;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Server-authoritative, synchronous vanilla-function upgrade bridge.
 * Never generates items: merges original enchanting onto the ONE new sword.
 * Original enchantments survive Dormant -> Awakened -> Ascended upgrades.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class UpgradePreservation {
    private record Saved(String weapon, String tier, ItemEnchantments enchants, boolean[] openings) {}
    private static final Map<UUID, Saved> PENDING = new HashMap<>();

    public static int remember(ServerPlayer player) {
        PENDING.remove(player.getUUID());
        ItemStack original = player.getMainHandItem();
        if (original.isEmpty() || !original.is(ItemTags.SWORDS)) return 0;
        CustomData custom = original.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return 0;
        CompoundTag marker = custom.copyTag();
        String weapon = marker.getString("ss_weapon");
        if (weapon.isEmpty()) return 0;
        String next = switch (marker.getString("ss_tier")) {
            case "dormant" -> "awakened";
            case "awakened" -> "ascended";
            default -> "";
        };
        if (next.isEmpty()) return 0;
        ItemEnchantments enchants = EnchantmentHelper.getEnchantmentsForCrafting(original);
        if (enchants.isEmpty()) return 1;
        var inventory = player.getInventory();
        boolean[] openings = new boolean[inventory.getContainerSize()];
        for (int i = 0; i < openings.length; i++)
            openings[i] = inventory.getItem(i).isEmpty() || i == inventory.selected;
        PENDING.put(player.getUUID(), new Saved(weapon, next, enchants, openings));
        return 1;
    }

    public static int restore(ServerPlayer player) {
        Saved previous = PENDING.remove(player.getUUID());
        if (previous == null) return 1;
        var inventory = player.getInventory();
        for (int i = 0; i < previous.openings().length; i++) {
            if (!previous.openings()[i]) continue;
            ItemStack next = inventory.getItem(i);
            if (next.isEmpty() || !next.is(ItemTags.SWORDS)) continue;
            CustomData custom = next.get(DataComponents.CUSTOM_DATA);
            if (custom == null) continue;
            CompoundTag marker = custom.copyTag();
            if (!previous.weapon().equals(marker.getString("ss_weapon"))
                    || !previous.tier().equals(marker.getString("ss_tier"))) continue;
            ItemEnchantments builtIn = EnchantmentHelper.getEnchantmentsForCrafting(next);
            EnchantmentHelper.updateEnchantments(next, mutable -> {
                for (var entry : previous.enchants().entrySet())
                    mutable.set(entry.getKey(), preserveLevel(
                        builtIn.getLevel(entry.getKey()), entry.getIntValue()));
            });
            inventory.setChanged();
            player.sendSystemMessage(Component.literal(
                "Forge safeguard: all your existing enchantments were preserved.")
                .withStyle(ChatFormatting.GREEN));
            return 1;
        }
        player.sendSystemMessage(Component.literal(
            "Forge warning: upgraded sword not found in the expected inventory slot.")
            .withStyle(ChatFormatting.RED));
        return 0;
    }

    public static int preserveLevel(int builtIn, int original) {
        return Math.max(builtIn, original);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING.remove(event.getEntity().getUUID());
    }
    private UpgradePreservation() {}
}
