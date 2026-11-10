package com.shatteredseals.adventure;

import com.shatteredseals.ShatteredSeals;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Chronicle books and Archivist Crown effects are server authoritative.
 * Progress and one-time rewards remain in FinalAdventure's existing saved
 * bitmask. The equipment ability never modifies vault blocks or puzzle flags.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class ChronicleRewards {
    public static final int ELITE_BONUS_XP = 100;
    public static final int SENSE_RANGE = 48;
    public static final int SENSE_COOLDOWN_TICKS = 200;
    public static final String EPILOGUE_GRANT_KEY = "SSChronicleEpilogueGranted";
    private static final String[] REGIONS = {
        "Smugglers' Tunnels", "Emberfault Chasms", "Drowned Ossuary",
        "Starshard Caverns", "Rootbound Grottos", "Black Crown Depths"
    };
    private static final String[] HISTORIES = {
        "The Smuggler King led his cargo through tunnels beneath the first seal. "
          + "When the wardens closed the border, smugglers became wardens of their own forbidden road.",
        "Forges beneath Emberfault were fed by a broken ember of the second seal. "
          + "The Cinder Warden promised warmth; his workers found only living ash.",
        "The drowned oathkeepers lay beneath stone when the tidal seal broke. "
          + "Their bells still ring through submerged halls, calling the faithless home.",
        "The final star readers drew their maps with crystalline needles. "
          + "Their observatory could predict the fall of every seal except the last.",
        "Roots curled around the Sixfold Temple to shield an ancient pact. "
          + "Six sentinels swore that no crown would outlive the world they guarded.",
        "Under the Hollow King's throne, guards hid the black standard and the "
          + "last account of the seals. The king feared memory more than any blade."
    };
    private static final String[] SECRETS = {
        "A false wall hides the smugglers' rune vault. Three markings bind its lock.",
        "The furnace drains toward a warded vault; the first rune is carved beside the ash chute.",
        "Three flooded inscriptions were left for those who remember the old oath.",
        "A shattered chart points toward the vault beyond the coldest amethyst seam.",
        "The roots hide a stone threshold: the order matters more than the runes themselves.",
        "The throne's vault remembers each rune in reverse of the king's broken vow."
    };

    /** A real right-clickable vanilla written book with original pages. */
    public static ItemStack chronicleBook(int chapter) {
        if (chapter < 0 || chapter >= REGIONS.length)
            throw new IllegalArgumentException("Chronicle chapter outside 1..6: " + chapter);
        return book("Buried Chronicle " + (chapter + 1),
            "Buried Chronicle: " + REGIONS[chapter],
            "Volume " + (chapter + 1) + " of VI\\n" + REGIONS[chapter]
                + "\\n\\nAn entry reclaimed from a sealed underground chamber.",
            HISTORIES[chapter],
            "VAULT INTELLIGENCE\\n\\n" + SECRETS[chapter]
                + "\\n\\nRune order: " + FinalAdventure.vaultHint(chapter)
                + "\\n\\nThe chiseled deepslate altar summons an elite guardian.");
    }

    public static ItemStack epilogueBook() {
        return book("Six Seals Epilogue", "The Archive's Seventh Leaf",
            "THE SEVENTH LEAF\\n\\nUnlocked by restoring all six Buried Chronicles.",
            "The Sun, Tide and Ash were never three rival powers. Their covenant "
                + "held six borders together. The Hollow King's ambition was to "
                + "control the story of their breaking, not merely the land.",
            "The Archive now remembers. Wear the Archivist's Crown to gain "
                + "extra experience from elite foes and sense unopened hidden "
                + "vaults within 48 blocks. The crown answers once every 10 seconds.");
    }

    private static ItemStack book(String title, String display, String... pages) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        List<Filterable<Component>> rendered = java.util.Arrays.stream(pages)
            .map(text -> Filterable.passThrough(Component.literal(text.replace("\\n", "\n"))))
            .toList();
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
            Filterable.passThrough(title), "The Warden's Archive", 0, rendered, true));
        book.set(DataComponents.CUSTOM_NAME,
            Component.literal(display).withStyle(ChatFormatting.AQUA));
        return book;
    }

    public static boolean wearingCrown(ServerPlayer player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ShatteredSeals.ARCHIVISTS_CROWN.get());
    }

    /** Only the original one-time elite XP award can receive this modifier. */
    public static int eliteXp(ServerPlayer player) {
        return 250 + (wearingCrown(player) ? ELITE_BONUS_XP : 0);
    }

    /** Only one epilogue is granted even on reload or after old save migration. */
    public static void ensureEpilogue(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if ((data.getInt("SSDepthsEliteMask") & 63) != 63
                || data.getBoolean(EPILOGUE_GRANT_KEY)) return;
        data.putBoolean(EPILOGUE_GRANT_KEY, true);
        ItemStack page = epilogueBook();
        if (!player.getInventory().add(page)) player.drop(page, false);
        player.sendSystemMessage(Component.literal(
            "CHRONICLE ARCHIVE: The Seventh Leaf is now readable in your inventory.")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    /** Called once each second from the existing FinalAdventure server tick. */
    public static void tick(ServerPlayer player, ServerLevel world) {
        ensureEpilogue(player);
        if (!wearingCrown(player)) return;
        CompoundTag data = player.getPersistentData();
        long now = world.getGameTime();
        if (now < data.getLong("SSArchivistNextPulse")) return;
        data.putLong("SSArchivistNextPulse", now + SENSE_COOLDOWN_TICKS);
        int foundMask = data.getInt("SSDepthsVaultsMask") & 63;
        Marker closest = null;
        double best = SENSE_RANGE * SENSE_RANGE;
        for (Marker marker : world.getEntitiesOfClass(Marker.class,
                new AABB(player.blockPosition()).inflate(SENSE_RANGE),
                m -> m.getTags().contains("ss_depths_vault"))) {
            int region = -1;
            for (int i = 0; i < 6; i++)
                if (marker.getTags().contains("ss_depths_region_" + (i + 1))) {
                    region = i;
                    break;
                }
            if (region < 0 || (foundMask & (1 << region)) != 0) continue;
            double d = player.distanceToSqr(marker);
            if (d < best) { best = d; closest = marker; }
        }
        if (closest == null) return;
        BlockPos pos = closest.blockPosition();
        player.displayClientMessage(Component.literal("ARCHIVIST'S SIGHT: Hidden Vault near X "
            + pos.getX() + " Y " + pos.getY() + " Z " + pos.getZ()
            + " (" + (int)Math.sqrt(best) + " blocks)")
            .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        world.sendParticles(ParticleTypes.ENCHANT,
            player.getX(), player.getY() + 1.8, player.getZ(),
            22, 0.4, 0.3, 0.4, 0.08);
    }

    /** Old v1.0.60 named Chronicle papers can be upgraded without another kill. */
    @SubscribeEvent
    public static void onLegacyPaper(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != InteractionHand.MAIN_HAND
                || !(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack held = event.getItemStack();
        if (!held.is(Items.PAPER)) return;
        Component name = held.get(DataComponents.CUSTOM_NAME);
        if (name == null) return;
        for (int chapter = 0; chapter < REGIONS.length; chapter++) {
            if (!name.getString().equals("Buried Chronicle: " + REGIONS[chapter])
                    || (player.getPersistentData().getInt("SSDepthsEliteMask") & (1 << chapter)) == 0)
                continue;
            held.shrink(1);
            ItemStack converted = chronicleBook(chapter);
            if (!player.getInventory().add(converted)) player.drop(converted, false);
            player.sendSystemMessage(Component.literal(
                "Recovered your old Chronicle as a readable archive volume.")
                .withStyle(ChatFormatting.GREEN));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }
    }
    private ChronicleRewards() {}
}
