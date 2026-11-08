package com.shatteredseals.client;

import com.shatteredseals.ShatteredSeals;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Live enchantment levels, including legacy 1.0.53 books after anvil combining. */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID, value = Dist.CLIENT)
public final class CodexBookTooltips {
    private record BookInfo(String rarity, String equipment) {}
    private static final Map<String, BookInfo> CATALOGUE = Map.ofEntries(
        Map.entry("keen_edge", new BookInfo("COMMON", "SWORD")),
        Map.entry("stagger", new BookInfo("COMMON", "WEAPON")),
        Map.entry("lifebloom", new BookInfo("COMMON", "ARMOR")),
        Map.entry("stoneguard", new BookInfo("COMMON", "CHEST")),
        Map.entry("fleetfoot", new BookInfo("COMMON", "FEET")),
        Map.entry("deep_miner", new BookInfo("COMMON", "PICKAXE")),
        Map.entry("lumberjack", new BookInfo("COMMON", "AXE")),
        Map.entry("harvest_blessing", new BookInfo("COMMON", "HOE")),
        Map.entry("durable", new BookInfo("COMMON", "TOOL")),
        Map.entry("archers_focus", new BookInfo("COMMON", "BOW")),
        Map.entry("aqua_step", new BookInfo("COMMON", "FEET")),
        Map.entry("treasure_sense", new BookInfo("COMMON", "TOOL")),
        Map.entry("vampiric_edge", new BookInfo("RARE", "SWORD")),
        Map.entry("frostbite", new BookInfo("RARE", "WEAPON")),
        Map.entry("thunderstrike", new BookInfo("RARE", "WEAPON")),
        Map.entry("guardian_aura", new BookInfo("RARE", "CHEST")),
        Map.entry("phoenix_skin", new BookInfo("RARE", "ARMOR")),
        Map.entry("shadowstep", new BookInfo("RARE", "FEET")),
        Map.entry("berserker", new BookInfo("RARE", "AXE")),
        Map.entry("vein_seeker", new BookInfo("RARE", "PICKAXE")),
        Map.entry("soul_collector", new BookInfo("RARE", "SWORD")),
        Map.entry("hunters_precision", new BookInfo("RARE", "BOW")),
        Map.entry("second_wind", new BookInfo("RARE", "LEGS")),
        Map.entry("magnetic_harvest", new BookInfo("RARE", "TOOL")),
        Map.entry("soul_reaper", new BookInfo("LEGENDARY", "SWORD")),
        Map.entry("infernal_wrath", new BookInfo("LEGENDARY", "WEAPON")),
        Map.entry("celestial_judgement", new BookInfo("LEGENDARY", "SWORD")),
        Map.entry("titans_resolve", new BookInfo("LEGENDARY", "CHEST")),
        Map.entry("immortal_spirit", new BookInfo("LEGENDARY", "ARMOR")),
        Map.entry("voidwalker", new BookInfo("LEGENDARY", "FEET")),
        Map.entry("worldbreaker", new BookInfo("LEGENDARY", "PICKAXE")),
        Map.entry("eternal_harvest", new BookInfo("LEGENDARY", "HOE")),
        Map.entry("stormcaller", new BookInfo("LEGENDARY", "BOW")),
        Map.entry("dragons_fury", new BookInfo("LEGENDARY", "SWORD")),
        Map.entry("guardian_angel", new BookInfo("LEGENDARY", "ARMOR")),
        Map.entry("kings_execution", new BookInfo("LEGENDARY", "AXE"))
    );
    private static final String[] ROMAN = {"?", "I", "II", "III"};

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(Items.ENCHANTED_BOOK)) return;
        ItemEnchantments stored = event.getItemStack().get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.size() == 0) return;

        var lines = event.getToolTip();
        // Correct stale Level I lore visually; do not mutate saved book items.
        lines.removeIf(line -> {
            String text = line.getString();
            return ((text.startsWith("COMMON |") || text.startsWith("RARE |")
                    || text.startsWith("LEGENDARY |")) && text.contains(" | Level "))
                    || text.startsWith("Combine matching books in an anvil to reach");
        });

        int position = Math.min(1, lines.size());
        for (var entry : stored.entrySet()) {
            var key = entry.getKey().unwrapKey().orElse(null);
            if (key == null || !ShatteredSeals.MOD_ID.equals(key.location().getNamespace())) continue;
            BookInfo info = CATALOGUE.get(key.location().getPath());
            if (info == null) continue;
            int rank = entry.getIntValue();
            String level = rank >= 1 && rank <= 3 ? ROMAN[rank] : String.valueOf(rank);
            ChatFormatting color = switch (info.rarity()) {
                case "LEGENDARY" -> ChatFormatting.GOLD;
                case "RARE" -> ChatFormatting.AQUA;
                default -> ChatFormatting.GRAY;
            };
            lines.add(position++, Component.literal(info.rarity() + " | " + info.equipment()
                + " | Level " + level + " / III").withStyle(color));
            if (rank >= 1 && rank < 3) {
                lines.add(position++, Component.literal("Combine two matching Level " + level
                    + " books in an anvil for Level " + ROMAN[rank + 1] + ".")
                    .withStyle(ChatFormatting.DARK_GRAY));
            } else if (rank == 3) {
                lines.add(position++, Component.literal("Maximum enchantment level reached.")
                    .withStyle(ChatFormatting.DARK_GREEN));
            }
        }
    }
    private CodexBookTooltips() {}
}
