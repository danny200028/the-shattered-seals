package com.shatteredseals.enchantments;

import com.shatteredseals.ShatteredSeals;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Server-only Codex book recycling. The client may REQUEST a recycle
 * with /trigger ss_ebook_recycle set 1 but never specifies XP or the target.
 *
 * Any single-enchantment custom Codex book of level I-III can be recycled,
 * even if the player's only other copy was already applied to equipment.
 * Only ONE book in the main hand is consumed. Gear is never modified.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class DuplicateBookRecycling {
    // Exactly the 36 enchanted books sold by the Codex. Not vanilla books.
    private static final Set<String> COMMON = Set.of(
        "keen_edge", "stagger", "lifebloom", "stoneguard",
        "fleetfoot", "deep_miner", "lumberjack", "harvest_blessing",
        "durable", "archers_focus", "aqua_step", "treasure_sense");
    private static final Set<String> RARE = Set.of(
        "vampiric_edge", "frostbite", "thunderstrike", "guardian_aura",
        "phoenix_skin", "shadowstep", "berserker", "vein_seeker",
        "soul_collector", "hunters_precision", "second_wind", "magnetic_harvest");
    private static final Set<String> LEGENDARY = Set.of(
        "soul_reaper", "infernal_wrath", "celestial_judgement", "titans_resolve",
        "immortal_spirit", "voidwalker", "worldbreaker", "eternal_harvest",
        "stormcaller", "dragons_fury", "guardian_angel", "kings_execution");

    // Half of the Codex purchase prices, rounded up for odd totals.
    public static final int COMMON_REFUND = 80;       // 160 XP purchase
    public static final int RARE_REFUND = 275;        // 550 XP purchase
    public static final int LEGENDARY_REFUND = 698;   // 1395 XP purchase

    public record BookKey(String enchantment, int level) {}
    public enum Result { RECYCLED, INVALID_BOOK, NOT_ALLOWED }

    public static int refundFor(String id) {
        if (COMMON.contains(id)) return COMMON_REFUND;
        if (RARE.contains(id)) return RARE_REFUND;
        if (LEGENDARY.contains(id)) return LEGENDARY_REFUND;
        return 0;
    }

    /** Real registry IDs and enchantment levels, never stale lore text. */
    public static BookKey identify(ItemStack book) {
        if (!book.is(Items.ENCHANTED_BOOK)) return null;
        ItemEnchantments stored = book.getOrDefault(
            DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (stored.size() != 1) return null;
        var entry = stored.entrySet().iterator().next();
        var registryKey = entry.getKey().unwrapKey().orElse(null);
        if (registryKey == null
                || !registryKey.location().getNamespace().equals(ShatteredSeals.MOD_ID)
                || entry.getIntValue() < 1 || entry.getIntValue() > 3) return null;
        String id = registryKey.location().getPath();
        return refundFor(id) == 0 ? null : new BookKey(id, entry.getIntValue());
    }

    /**
     * Validate the actual single stored enchantment and tier before paying.
     * No duplicate, equipped item or inventory search is required.
     */
    public static Result recycle(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) return Result.NOT_ALLOWED;
        ItemStack held = player.getMainHandItem();
        BookKey key = identify(held);
        if (key == null) return Result.INVALID_BOOK;
        int refund = refundFor(key.enchantment());
        // Spend one real book before awarding its tier's XP refund.
        held.shrink(1);
        player.getInventory().setChanged();
        player.giveExperiencePoints(refund);
        return Result.RECYCLED;
    }

    @SubscribeEvent
    public static void handleRequest(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var scoreboard = player.getScoreboard();
        var objective = scoreboard.getObjective("ss_ebook_recycle");
        if (objective == null) return;
        var info = scoreboard.getPlayerScoreInfo(player, objective);
        if (info == null || info.value() < 1) return;
        // Reset FIRST, so a trigger cannot be replayed or pay XP twice.
        scoreboard.getOrCreatePlayerScore(player, objective).set(0);

        ItemStack held = player.getMainHandItem();
        BookKey key = identify(held);
        Result result = recycle(player);
        Component feedback = switch (result) {
            case RECYCLED -> Component.literal(
                "Book recycled! +" + refundFor(key.enchantment())
                + " XP points. Your other gear and books were untouched.").withStyle(ChatFormatting.GREEN);
            case INVALID_BOOK -> Component.literal(
                "Hold a single-enchantment custom Codex book in your main hand.")
                .withStyle(ChatFormatting.RED);
            case NOT_ALLOWED -> Component.literal(
                "Book recycling is available in Survival only.")
                .withStyle(ChatFormatting.RED);
        };
        player.displayClientMessage(feedback, false);
    }

    private DuplicateBookRecycling() {}
}
