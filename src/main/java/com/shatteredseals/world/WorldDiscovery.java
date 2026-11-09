package com.shatteredseals.world;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.adventure.DungeonChapter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Light-touch, save-safe chapter exploration. No free items, XP or buffs.
 * A named marker inside a generated shrine is the only way to claim a
 * landmark discovery; stepping into its biome does not mark its shrine.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class WorldDiscovery {
    private static final String[] IDS = {
        "smugglers_wilds", "cinder_wastes", "hollow_moor",
        "arcane_highlands", "sixfold_expanse", "hollow_kings_dominion"
    };
    private static final String[] NAMES = {
        "Smugglers' Wilds", "Cinder Wastes", "Hollow Moor",
        "Arcane Highlands", "Sixfold Expanse", "Hollow King's Dominion"
    };
    private static final String[] SECRETS = {
        "The smugglers hid more than gold. Their routes began in these wilds.",
        "Old anvils cooled here when the Cinder Warden first rose.",
        "The forgotten dead whisper of a broken oath in the moor.",
        "These stones held the last map of the stars before the seals cracked.",
        "Six vows were carved here, one for each force that bound the world.",
        "The king's sentries watched from this ruin long after the throne fell."
    };
    private static final String INTRO_KEY = "SSIntroSeen";

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        CompoundTag saved = player.getPersistentData();
        if (saved.getBoolean(INTRO_KEY)) return;
        saved.putBoolean(INTRO_KEY, true);
        // Existing adventurers must never be thrown back into the tutorial.
        if (DungeonChapter.score(player, "ss_path") > 0
                || DungeonChapter.score(player, "ss_level") > 1
                || DungeonChapter.score(player, "ss_vk_smug") > 0) {
            player.sendSystemMessage(Component.literal(
                "The Living World awaits. Open J > Explore to see your discoveries.")
                .withStyle(ChatFormatting.AQUA));
            return;
        }
        showIntroduction(player);
    }

    /** The optional introduction can be replayed without changing any progress. */
    public static int showIntroduction(ServerPlayer player) {
        player.sendSystemMessage(Component.literal(
            "THE SHATTERED SEALS | Six wards once held the Hollow King.")
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        player.sendSystemMessage(Component.literal(
            "Those wards are broken. Find their six fortresses and face their guardians.")
            .withStyle(ChatFormatting.GRAY));
        player.sendSystemMessage(Component.literal(
            "Press K to choose your class, J for the Codex and dungeon locator, L for quests.")
            .withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.literal(
            "Hold right-click with a sword to guard. Your first target is Smugglers' Den.")
            .withStyle(ChatFormatting.GREEN));
        player.sendSystemMessage(Component.literal(
            "Tutorial is optional and never resets your progression. Replay: /shatteredseals intro")
            .withStyle(ChatFormatting.DARK_GRAY));
        return 1;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player.tickCount % 100 != 0
                || !player.serverLevel().dimension().equals(Level.OVERWORLD)) return;

        ServerLevel world = player.serverLevel();
        CompoundTag saved = player.getPersistentData();
        var key = world.getBiome(player.blockPosition()).unwrapKey().orElse(null);
        int biomeIndex = -1;
        if (key != null && ShatteredSeals.MOD_ID.equals(key.location().getNamespace())) {
            for (int i = 0; i < IDS.length; i++) {
                if (IDS[i].equals(key.location().getPath())) {
                    biomeIndex = i;
                    break;
                }
            }
        }
        if (biomeIndex >= 0) {
            int bit = 1 << biomeIndex;
            int visited = saved.getInt("SSBiomeDiscoveryMask");
            if ((visited & bit) == 0) {
                saved.putInt("SSBiomeDiscoveryMask", visited | bit);
                player.sendSystemMessage(Component.literal(
                    "NEW DISCOVERY: " + NAMES[biomeIndex] + " | See J > Explore.")
                    .withStyle(ChatFormatting.AQUA));
                world.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(),
                    SoundSource.PLAYERS, 0.7F, 1.25F);
            }
            // Restrained ambience, five particles per five seconds. No new shaders.
            if (player.tickCount % 200 == 0) {
                double x = player.getX(), y = player.getY() + 1, z = player.getZ();
                switch (biomeIndex) {
                    case 1 -> world.sendParticles(ParticleTypes.ASH, x, y, z, 5, 4, 1, 4, 0.005);
                    case 2 -> world.sendParticles(ParticleTypes.SOUL, x, y, z, 3, 4, 1, 4, 0.005);
                    case 3 -> world.sendParticles(ParticleTypes.END_ROD, x, y, z, 3, 3, 1, 3, 0.003);
                    case 4 -> world.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, x, y, z, 4, 4, 1, 4, 0.005);
                    case 5 -> world.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 4, 3, 1, 3, 0.002);
                    default -> { /* wilds: preserve quiet forest atmosphere */ }
                }
            }
        }
        // Search a small volume around the player, at most once in five seconds.
        int found = saved.getInt("SSLandmarkDiscoveryMask");
        if ((found & 63) == 63) return;
        for (Marker marker : world.getEntitiesOfClass(Marker.class,
                new AABB(player.blockPosition()).inflate(11))) {
            for (int i = 0; i < IDS.length; i++) {
                int bit = 1 << i;
                if ((found & bit) != 0 || !marker.getTags().contains("ss_landmark_" + IDS[i])) continue;
                found |= bit;
                player.sendSystemMessage(Component.literal(
                    "LOST SHRINE FOUND: " + NAMES[i] + " | " + SECRETS[i])
                    .withStyle(ChatFormatting.GOLD));
                world.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(),
                    SoundSource.PLAYERS, 0.6F, 0.9F);
            }
        }
        if (found != saved.getInt("SSLandmarkDiscoveryMask")) {
            saved.putInt("SSLandmarkDiscoveryMask", found);
        }
    }
    private WorldDiscovery() {}
}
