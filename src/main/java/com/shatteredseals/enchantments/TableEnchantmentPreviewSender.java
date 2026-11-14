package com.shatteredseals.enchantments;

import com.shatteredseals.net.TableEnchantPreview;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Mirrors EnchantmentMenu.getEnchantmentList on the authoritative server.
 * Minecraft syncs only its one-enchantment clue, so the client must not
 * guess the complete list from that clue or a truncated enchantment seed.
 */
@EventBusSubscriber(modid = "shatteredseals")
public final class TableEnchantmentPreviewSender {
    private record Last(int menuId, int seed, ItemStack item, int[] costs) {
        boolean matches(EnchantmentMenu menu, ItemStack current) {
            return menuId == menu.containerId && seed == menu.getEnchantmentSeed()
                && ItemStack.matches(item, current) && Arrays.equals(costs, menu.costs);
        }
    }

    private static final Map<ServerPlayer, Last> LAST = new WeakHashMap<>();

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.containerMenu instanceof EnchantmentMenu menu)) {
            LAST.remove(player);
            return;
        }
        ItemStack item = menu.slots.get(0).getItem();
        Last previous = LAST.get(player);
        if (previous != null && previous.matches(menu, item)) return;

        int[] costs = menu.costs.clone();
        String[] names = new String[3];
        for (int slot = 0; slot < 3; slot++) {
            List<EnchantmentInstance> chosen = selectVanillaEnchantments(
                player.registryAccess(), item, menu.getEnchantmentSeed(), slot, costs[slot]);
            names[slot] = chosen.stream()
                .map(entry -> Enchantment.getFullname(entry.enchantment, entry.level).getString())
                .collect(Collectors.joining("\n"));
        }

        LAST.put(player, new Last(menu.containerId, menu.getEnchantmentSeed(), item.copy(), costs));
        PacketDistributor.sendToPlayer(player,
            new TableEnchantPreview(menu.containerId, Item.getId(item.getItem()),
                menu.getEnchantmentSeed(), costs, names));
    }

    /**
     * Mirrors the real vanilla EnchantmentMenu.getEnchantmentList algorithm.
     * Returns every enchantment, including any extras beyond the tooltip clue.
     * Exposed to Minecraft GameTests so we can verify MULTI-enchant rolls exist.
     */
    public static List<EnchantmentInstance> selectVanillaEnchantments(
            net.minecraft.core.RegistryAccess registryAccess,
            ItemStack item, int enchantmentSeed, int slot, int cost) {
        if (item.isEmpty() || cost <= 0 || slot < 0 || slot > 2) return List.of();
        var tag = registryAccess.registryOrThrow(Registries.ENCHANTMENT)
            .getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
        if (tag.isEmpty()) return List.of();

        RandomSource random = RandomSource.create(enchantmentSeed + slot);
        HolderSet.Named<Enchantment> eligible = tag.get();
        List<EnchantmentInstance> chosen = EnchantmentHelper.selectEnchantment(
            random, item, cost, eligible.stream());
        // Vanilla discards one extra enchant for BOOKS, not for equipment.
        if (item.is(Items.BOOK) && chosen.size() > 1) {
            chosen.remove(random.nextInt(chosen.size()));
        }
        return chosen;
    }

    private TableEnchantmentPreviewSender() {}
}
