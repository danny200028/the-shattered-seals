package com.shatteredseals.adventure;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.world.SanctuaryPlacement;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Endgame and world interaction layer. No world-edit commands, free items or
 * mandatory underground gates. Structures are generated in NEW chunks only;
 * the Sanctuary and underground progress is saved per player and on blocks.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class FinalAdventure {
    private static final String[] REGIONS = {
        "Smugglers' Tunnels", "Emberfault Chasms", "Drowned Ossuary",
        "Starshard Caverns", "Rootbound Grottos", "Black Crown Depths"
    };
    private static final String[] IDS = {
        "smugglers_tunnels", "emberfault_chasms", "drowned_ossuary",
        "starshard_caverns", "rootbound_grottos", "black_crown_depths"
    };
    private static final String[] LORE = {
        "A hidden trade route, abandoned when the first seal failed.",
        "The Cinder Warden's forges overheated deep below the wastes.",
        "Those who broke the moor's oath were laid in flooded stone.",
        "The last star readers mined this crystalline chart.",
        "Roots still protect the Sixfold Temple's oldest warning.",
        "The Hollow King's sentries once gathered under his throne."
    };
    @SuppressWarnings("unchecked")
    private static final EntityType<? extends Mob>[] ELITES = new EntityType[] {
        EntityType.SKELETON, EntityType.BLAZE, EntityType.DROWNED,
        EntityType.STRAY, EntityType.HUSK, EntityType.WITHER_SKELETON
    };
    private static final String[] ELITE_NAMES = {
        "Contraband Revenant", "Emberfault Sentinel", "Ossuary Dredger",
        "Starshard Watcher", "Rootbound Guardian", "Black Crown Executioner"
    };
    // Sun=0, Tide=1, Ash=2. Each chamber's order is fixed and discoverable.
    private static final int[][] VAULT_ORDERS = {
        {0, 1, 2}, {2, 0, 1}, {1, 2, 0}, {1, 0, 2}, {0, 2, 1}, {2, 1, 0}
    };
    private static final String[] RUNES = {"Sun", "Tide", "Ash"};
    private static final int[] REWARD_HEALTH = {38, 45, 55, 62, 68, 85};
    public static final String[] SETTINGS = {"hud", "ambience", "enchantments", "flavour"};
    public static final String[] SETTING_KEYS = {"SSShowCombatHud", "SSShowAmbientEffects", "SSShowEnchantments", "SSShowFlavourText"};
    public static final String[] COPY_KEYS = {
        "SSDepthsSitesMask", "SSDepthsVaultsMask", "SSDepthsEliteMask",
        "SSSecretWon", "SSSanctuaryVisited", "SSChronicleComplete",
        "SSSanctuaryKeystoneGranted", "SSSanctuaryPlaced",
        "SSBiomeDiscoveryMask", "SSLandmarkDiscoveryMask", "SSIntroSeen",
        "SSShowCombatHud", "SSShowAmbientEffects", "SSShowFlavourText"
    };

    public static int[] state(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        return new int[] {data.getInt("SSDepthsSitesMask"), data.getInt("SSDepthsVaultsMask"),
            data.getInt("SSDepthsEliteMask"), data.getBoolean("SSSecretWon") ? 1 : 0,
            data.getBoolean("SSSanctuaryVisited") ? 1 : 0};
    }

    public static int[] options(ServerPlayer player) {
        int[] out = new int[SETTINGS.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = i == 2 ? (player.getTags().contains("ss_quiet_enchants") ? 0 : 1)
                : enabled(player, SETTING_KEYS[i]) ? 1 : 0;
        }
        return out;
    }

    public static boolean enabled(ServerPlayer player, String setting) {
        return !player.getPersistentData().contains(setting) || player.getPersistentData().getInt(setting) != 0;
    }

    public static int setOption(ServerPlayer player, int slot, boolean enabled) {
        if (slot < 0 || slot >= SETTINGS.length) return 0;
        if (slot == 2) {
            if (enabled) player.removeTag("ss_quiet_enchants");
            else player.addTag("ss_quiet_enchants");
        } else player.getPersistentData().putInt(SETTING_KEYS[slot], enabled ? 1 : 0);
        say(player, SETTINGS[slot] + " " + (enabled ? "enabled" : "disabled")
            + ". Critical trial and boss hazard warnings always remain visible.", ChatFormatting.AQUA);
        return 1;
    }

    public static int locateSanctuary(ServerPlayer player) {
        if (DungeonChapter.score(player, "ss_vk_smug") < 1
                && DungeonChapter.score(player, "ss_ng") < 1) {
            say(player, "Defeat the Smuggler King first to unlock the Warden's Sanctuary map.", ChatFormatting.YELLOW);
            return 0;
        }
        return find(player, "wardens_sanctuary", "The Warden's Sanctuary");
    }

    public static int locateDepths(ServerPlayer player, int chapter) {
        if (chapter < 1 || chapter > 6) return 0;
        if (DungeonChapter.score(player, "ss_vk_smug") < 1) {
            say(player, "Discover the story's first seal before hunting ancient depths.", ChatFormatting.YELLOW);
            return 0;
        }
        return find(player, IDS[chapter - 1] + "_ruin", REGIONS[chapter - 1]);
    }

    private static int find(ServerPlayer player, String structure, String title) {
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(Level.OVERWORLD)) {
            say(player, "Return to the Overworld to search for " + title + ".", ChatFormatting.YELLOW);
            return 0;
        }
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, structure);
        BlockPos pos = level.findNearestMapStructure(
            TagKey.create(Registries.STRUCTURE, key), player.blockPosition(), 100, false);
        if (pos == null) {
            say(player, "No " + title + " found nearby. Search farther in new terrain.", ChatFormatting.YELLOW);
            return 0;
        }
        say(player, title + " | X " + pos.getX() + " Z " + pos.getZ()
            + ("wardens_sanctuary".equals(structure)
                ? " | Surface sanctuary"
                : " | Underground near Y -30; mine carefully toward the chamber."),
            ChatFormatting.AQUA);
        return 1;
    }

    private static int region(net.minecraft.world.entity.Entity entity) {
        for (int i = 0; i < 6; i++)
            if (entity.getTags().contains("ss_depths_region_" + (i + 1))) return i;
        return -1;
    }

    @SubscribeEvent
    public static void onClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND
                || !(event.getEntity() instanceof ServerPlayer player)
                || !player.serverLevel().dimension().equals(Level.OVERWORLD)) return;
        ServerLevel world = player.serverLevel();
        BlockPos clicked = event.getPos();
        if (event.getItemStack().is(ShatteredSeals.SANCTUARY_KEYSTONE.get())) {
            if (event.getFace() == Direction.UP) SanctuaryPlacement.place(player, clicked, event.getItemStack());
            else say(player, "Click the TOP of the ground block to place your Sanctuary.", ChatFormatting.YELLOW);
            consume(event);
            return;
        }
        var block = world.getBlockState(clicked).getBlock();
        if (block != Blocks.CHISELED_DEEPSLATE && block != Blocks.CHISELED_COPPER
                && block != Blocks.PRISMARINE && block != Blocks.POLISHED_BLACKSTONE) return;
        for (Marker marker : world.getEntitiesOfClass(Marker.class, new AABB(clicked).inflate(5))) {
            int region = region(marker);
            if (region >= 0 && marker.getTags().contains("ss_depths_vault")
                    && (block == Blocks.CHISELED_COPPER || block == Blocks.PRISMARINE
                        || block == Blocks.POLISHED_BLACKSTONE)
                    && clicked.getZ() == marker.blockPosition().getZ() - 1
                    && Math.abs(clicked.getX() - marker.blockPosition().getX()) <= 3) {
                int rune = block == Blocks.CHISELED_COPPER ? 0 : block == Blocks.PRISMARINE ? 1 : 2;
                pressVaultRune(player, marker, region, rune);
                consume(event);
                return;
            }
            if (region >= 0 && marker.getTags().contains("ss_depths_site")
                    && block == Blocks.CHISELED_DEEPSLATE
                    && marker.blockPosition().equals(clicked.above())) {
                challenge(player, marker, region);
                consume(event);
                return;
            }
            if (marker.getTags().contains("ss_sanctuary_room")
                    && block == Blocks.CHISELED_DEEPSLATE
                    && marker.blockPosition().equals(clicked.above())) {
                readRoom(player, marker);
                consume(event);
                return;
            }
        }
    }

    private static void consume(PlayerInteractEvent.RightClickBlock event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void readRoom(ServerPlayer player, Marker room) {
        if (room.getTags().contains("ss_sanctuary_trophy_hall")) {
            int[] trial = AdventureContracts.trialState(player);
            int[] progress = state(player);
            int bossCount = 0;
            for (DungeonChapter chapter : DungeonChapter.values())
                if (DungeonChapter.score(player, chapter.victoryScore) > 0) bossCount++;
            say(player, "Trophy Hall: " + bossCount + "/6 bosses, "
                + Integer.bitCount(trial[2] & 63) + "/6 trial medals this NG+ cycle, "
                + Integer.bitCount(progress[2] & 63) + "/6 underground elites, "
                + (progress[3] > 0 ? "secret victory recorded." : "secret challenge undiscovered."),
                ChatFormatting.GOLD);
        } else if (room.getTags().contains("ss_sanctuary_chronicle_archive")) {
            int mask = player.getPersistentData().getInt("SSDepthsEliteMask") & 63;
            say(player, "CHRONICLE ARCHIVE: " + Integer.bitCount(mask) + "/6 recovered.", ChatFormatting.GOLD);
            for (int i = 0; i < 6; i++)
                if ((mask & (1 << i)) != 0) say(player, REGIONS[i] + ": " + LORE[i], ChatFormatting.AQUA);
            if (mask == 63) say(player, "All six histories restored. The Archivist's Crown is yours.", ChatFormatting.LIGHT_PURPLE);
        } else if (room.getTags().contains("ss_sanctuary_armoury")) {
            int[] kills = com.shatteredseals.combat.SwordMastery.snapshot(player);
            int total = 0;
            for (int count : kills) total += com.shatteredseals.combat.SwordMastery.rank(count);
            say(player, "Armoury: " + total + "/30 cosmetic legendary sword mastery ranks. J > Swords.", ChatFormatting.GOLD);
        } else if (room.getTags().contains("ss_sanctuary_cartographers_room")) {
            int[] world = state(player);
            say(player, "Cartography: " + Integer.bitCount(world[0] & 63) + "/6 depths, "
                + Integer.bitCount(world[1] & 63) + "/6 hidden vaults. J > More > Depths.",
                ChatFormatting.AQUA);
        } else if (room.getTags().contains("ss_sanctuary_war_room")) {
            say(player, "War Room: open J > Bounty or Trials to manage live contracts and boss modifiers.",
                ChatFormatting.AQUA);
        } else if (room.getTags().contains("ss_sanctuary_enchanters_workshop")) {
            say(player, "Enchanter's Workshop: J > Books and Forge. All original XP and material costs apply.",
                ChatFormatting.AQUA);
        } else if (room.getTags().contains("ss_sanctuary_portal_chamber")) {
            say(player, "Portal Chamber: Waystones and ordinary vanilla portals retain their own costs.",
                ChatFormatting.AQUA);
        } else if (room.getTags().contains("ss_sanctuary_secret_arena")) {
            say(player, "The final centrepiece awaits six Shattered Trial medals in the current NG+ cycle.",
                ChatFormatting.LIGHT_PURPLE);
        } else {
            say(player, "Sanctuary room: ordinary Minecraft crafting, storage, farming and survival rules apply.",
                ChatFormatting.GRAY);
        }
    }

    private static void pressVaultRune(ServerPlayer player, Marker marker, int region, int rune) {
        BlockPos gate = marker.blockPosition().offset(0, -1, 2);
        if (!player.serverLevel().getBlockState(gate).is(Blocks.IRON_BARS)) {
            say(player, "The Hidden Vault has already been opened. Its chest can be looted once.", ChatFormatting.GRAY);
            return;
        }
        String key = "SSVaultStep_" + marker.blockPosition().asLong();
        CompoundTag saved = player.getPersistentData();
        int step = Math.clamp(saved.getInt(key), 0, 2);
        if (VAULT_ORDERS[region][step] != rune) {
            saved.putInt(key, VAULT_ORDERS[region][0] == rune ? 1 : 0);
            say(player, "The vault rejects the runes. Seek the sequence carved into its history.", ChatFormatting.RED);
            return;
        }
        step++;
        saved.putInt(key, step);
        player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(),
            SoundSource.PLAYERS, 0.6F, 0.9F + step * 0.15F);
        if (step < 3) {
            say(player, RUNES[rune] + " attuned: " + step + "/3.", ChatFormatting.AQUA);
            return;
        }
        ServerLevel level = player.serverLevel();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                BlockPos pos = gate.offset(dx, dy, 0);
                if (level.getBlockState(pos).is(Blocks.IRON_BARS))
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        saved.putInt("SSDepthsVaultsMask", saved.getInt("SSDepthsVaultsMask") | (1 << region));
        say(player, "HIDDEN VAULT OPENED: " + REGIONS[region] + ". The cache is yours to discover.", ChatFormatting.GREEN);
    }

    private static void challenge(ServerPlayer player, Marker altar, int index) {
        if (player.serverLevel().getDifficulty() == Difficulty.PEACEFUL) {
            say(player, "Underground elites only answer in hostile difficulty.", ChatFormatting.RED);
            return;
        }
        String clear = "SSDepthsElite_" + altar.blockPosition().asLong();
        if (player.getPersistentData().getBoolean(clear)) {
            say(player, "You have already defeated this chamber's elite. The lore remains recorded.", ChatFormatting.GRAY);
            return;
        }
        ServerLevel level = player.serverLevel();
        for (Mob enemy : level.getEntitiesOfClass(Mob.class, new AABB(altar.blockPosition()).inflate(30),
                mob -> mob.isAlive() && mob.getTags().contains("ss_depths_guardian")))
            if (enemy.getPersistentData().getLong("SSDepthsOrigin") == altar.blockPosition().asLong()) {
                say(player, "The elite still guards this chamber.", ChatFormatting.YELLOW);
                return;
            }
        Mob enemy = ELITES[index].create(level);
        if (enemy == null) return;
        BlockPos pos = altar.blockPosition().offset(3, 0, 2);
        enemy.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot() + 180, 0);
        if (!level.noCollision(enemy)) {
            say(player, "Clear a safe space beside the altar before summoning the guardian.", ChatFormatting.RED);
            return;
        }
        enemy.addTag("ss_depths_guardian");
        enemy.addTag("ss_depths_region_" + (index + 1));
        enemy.getPersistentData().putUUID("SSDepthsOwner", player.getUUID());
        enemy.getPersistentData().putLong("SSDepthsOrigin", altar.blockPosition().asLong());
        enemy.setPersistenceRequired();
        enemy.setCustomName(Component.literal(ELITE_NAMES[index]).withStyle(ChatFormatting.LIGHT_PURPLE));
        enemy.setCustomNameVisible(true);
        if (enemy.getAttribute(Attributes.MAX_HEALTH) != null) {
            enemy.getAttribute(Attributes.MAX_HEALTH).setBaseValue(REWARD_HEALTH[index]);
            enemy.setHealth(REWARD_HEALTH[index]);
        }
        if (index == 0 || index == 5)
            enemy.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        if (level.addFreshEntity(enemy)) {
            say(player, ELITE_NAMES[index] + " emerges. Expect warned strikes; retreat is possible.",
                ChatFormatting.LIGHT_PURPLE);
        }
    }

    public static boolean secretUnlocked(int medalBits, int ngCycle) {
        return ngCycle >= 1 && (medalBits & 63) == 63;
    }

    public static int summonSecret(ServerPlayer player) {
        if (player.serverLevel().getDifficulty() == Difficulty.PEACEFUL) {
            say(player, "The secret challenge requires hostile difficulty.", ChatFormatting.RED);
            return 0;
        }
        int bits = AdventureContracts.trialState(player)[2];
        if (!secretUnlocked(bits, DungeonChapter.score(player, "ss_ng"))) {
            say(player, "Earn all six trial medals during one NG+ cycle before this challenge.", ChatFormatting.YELLOW);
            return 0;
        }
        ServerLevel level = player.serverLevel();
        Marker arena = null;
        for (Marker marker : level.getEntitiesOfClass(Marker.class,
                new AABB(player.blockPosition()).inflate(10),
                m -> m.getTags().contains("ss_secret_arena"))) {
            arena = marker;
            break;
        }
        if (arena == null) {
            say(player, "Stand at the Warden's Sanctuary secret arena to begin.", ChatFormatting.YELLOW);
            return 0;
        }
        for (Mob existing : level.getEntitiesOfClass(Mob.class, new AABB(arena.blockPosition()).inflate(40),
                mob -> mob.isAlive() && mob.getTags().contains("ss_secret_boss"))) {
            say(player, "The final challenger is already alive.", ChatFormatting.YELLOW);
            return 0;
        }
        Mob boss = EntityType.EVOKER.create(level);
        if (boss == null) return 0;
        BlockPos pos = arena.blockPosition().offset(3, 0, 2);
        boss.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot() + 180, 0);
        if (!level.noCollision(boss)) {
            say(player, "Clear the sanctuary arena before summoning.", ChatFormatting.YELLOW);
            return 0;
        }
        boss.addTag("ss_secret_boss");
        boss.getPersistentData().putUUID("SSSecretOwner", player.getUUID());
        boss.setCustomName(Component.literal("The Unbound Warden").withStyle(ChatFormatting.DARK_PURPLE));
        boss.setCustomNameVisible(true);
        boss.setPersistenceRequired();
        if (boss.getAttribute(Attributes.MAX_HEALTH) != null) {
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(240.0);
            boss.setHealth(240.0F);
        }
        if (boss.getAttribute(Attributes.MOVEMENT_SPEED) != null)
            boss.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29);
        if (!level.addFreshEntity(boss)) return 0;
        say(player, "SECRET ENCOUNTER: The Unbound Warden. Dodge the announced pulses and defeat its summons.",
            ChatFormatting.LIGHT_PURPLE);
        return 1;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player.tickCount % 20 != 0
                || !player.serverLevel().dimension().equals(Level.OVERWORLD)) return;
        ServerLevel world = player.serverLevel();
        CompoundTag award = player.getPersistentData();
        if ((award.getInt("SSDepthsEliteMask") & 63) == 63
                && !award.getBoolean("SSChronicleComplete")) {
            award.putBoolean("SSChronicleComplete", true);
            give(player, new ItemStack(ShatteredSeals.ARCHIVISTS_CROWN.get()));
            say(player, "All six Buried Chronicles restored! The Archivist's Crown has been awarded.", ChatFormatting.GOLD);
        }
        if (DungeonChapter.score(player, "ss_vk_smug") > 0
                && !award.getBoolean("SSSanctuaryKeystoneGranted")) {
            award.putBoolean("SSSanctuaryKeystoneGranted", true);
            if (!award.getBoolean("SSSanctuaryPlaced")) {
                give(player, new ItemStack(ShatteredSeals.SANCTUARY_KEYSTONE.get()));
                say(player, "Warden's Sanctuary Keystone acquired! Find a wide clearing and right-click the ground.", ChatFormatting.GOLD);
            }
        }
        long now = player.getServer().overworld().getGameTime();
        // Limit marker and encounter queries: once every four seconds.
        if (player.tickCount % 80 == 0) {
            CompoundTag data = player.getPersistentData();
            for (Marker marker : world.getEntitiesOfClass(Marker.class,
                    new AABB(player.blockPosition()).inflate(14))) {
                int index = region(marker);
                if (index >= 0 && marker.getTags().contains("ss_depths_site")) {
                    int bit = 1 << index, previous = data.getInt("SSDepthsSitesMask");
                    if ((previous & bit) == 0) {
                        data.putInt("SSDepthsSitesMask", previous | bit);
                        say(player, "SHATTERED DEPTHS FOUND: " + REGIONS[index] + ". " + LORE[index],
                            ChatFormatting.AQUA);
                    }
                }
                if (marker.getTags().contains("ss_sanctuary_room")
                        && !data.getBoolean("SSSanctuaryVisited")) {
                    data.putBoolean("SSSanctuaryVisited", true);
                    say(player, "WARDEN'S SANCTUARY FOUND: the fifteen connected rooms are yours to furnish.",
                        ChatFormatting.GOLD);
                }
            }
            if (data.getBoolean("SSSanctuaryVisited")) updateTrophies(player);
        }
        // Sparse original scores, never spammed and fully opt-out with ambience.
        if (player.tickCount % 1800 == 0 && enabled(player, "SSShowAmbientEffects")) {
            boolean atHome = !world.getEntitiesOfClass(Marker.class,
                new AABB(player.blockPosition()).inflate(24),
                m -> m.getTags().contains("ss_sanctuary_room")).isEmpty();
            if (atHome) world.playSound(null, player.blockPosition(),
                ShatteredSeals.SANCTUARY_THEME.get(), SoundSource.AMBIENT, 0.45F, 1.0F);
            else if (world.getBiome(player.blockPosition()).unwrapKey()
                    .map(key -> key.location().getNamespace().equals(ShatteredSeals.MOD_ID)).orElse(false))
                world.playSound(null, player.blockPosition(),
                    ShatteredSeals.EXPLORE_THEME.get(), SoundSource.AMBIENT, 0.35F, 1.0F);
        }
        // Telegraph every special pulse two seconds before it strikes.
        for (Mob boss : world.getEntitiesOfClass(Mob.class,
                new AABB(player.blockPosition()).inflate(32),
                mob -> mob.isAlive() && (mob.getTags().contains("ss_depths_guardian")
                    || mob.getTags().contains("ss_secret_boss")))) {
            CompoundTag data = boss.getPersistentData();
            String owner = boss.getTags().contains("ss_secret_boss") ? "SSSecretOwner" : "SSDepthsOwner";
            if (!data.hasUUID(owner) || !data.getUUID(owner).equals(player.getUUID())) continue;
            long interval = boss.getTags().contains("ss_secret_boss") ? 160 : 140;
            long due = data.getLong("SSNextAttack");
            if (due <= 0) { data.putLong("SSNextAttack", now + interval); continue; }
            if (now >= due - 40 && !data.getBoolean("SSAttackWarned")) {
                data.putBoolean("SSAttackWarned", true);
                player.displayClientMessage(Component.literal("WARNING: "
                    + (boss.getTags().contains("ss_secret_boss")
                        ? "The Unbound Warden is preparing a void surge. Evade the centre!"
                        : "An underground guardian is charging a shockwave. Retreat!"))
                    .withStyle(ChatFormatting.RED), true);
                world.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    boss.getX(), boss.getY() + 1, boss.getZ(), 20, 3, 1, 3, 0.04);
            }
            if (now >= due) {
                data.putLong("SSNextAttack", now + interval);
                data.putBoolean("SSAttackWarned", false);
                double radius = boss.getTags().contains("ss_secret_boss") ? 5.0 : 4.0;
                if (boss.distanceToSqr(player) <= radius * radius) {
                    player.hurt(world.damageSources().magic(),
                        boss.getTags().contains("ss_secret_boss") ? 5.0F : 3.0F);
                    player.push(player.getX() - boss.getX(), 0.25, player.getZ() - boss.getZ());
                }
                world.playSound(null, boss.blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK,
                    SoundSource.HOSTILE, 0.8F, 0.7F);
            }
        }
    }

    private static void updateTrophies(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        for (Marker marker : level.getEntitiesOfClass(Marker.class,
                new AABB(player.blockPosition()).inflate(16),
                m -> m.getTags().contains("ss_sanctuary_trophy_hall"))) {
            // Replace only original untouched stand caps; never overwrite player builds.
            int i = 0;
            for (int x : new int[] {-4, 0, 4}) for (int z : new int[] {-4, 4}) {
                BlockPos cap = marker.blockPosition().offset(x, 1, z);
                if (DungeonChapter.score(player, DungeonChapter.values()[i].victoryScore) > 0) {
                    if (level.getBlockState(cap).is(Blocks.POLISHED_BLACKSTONE_WALL))
                        level.setBlock(cap, Blocks.GILDED_BLACKSTONE.defaultBlockState(), 3);
                    String trophyTag = "ss_sanctuary_boss_trophy_" + i;
                    if (level.getEntitiesOfClass(Display.ItemDisplay.class,
                            new AABB(cap).inflate(1.3), e -> e.getTags().contains(trophyTag)).isEmpty()) {
                        Display.ItemDisplay display = EntityType.ITEM_DISPLAY.create(level);
                        if (display != null) {
                            display.setPos(cap.getX() + 0.5, cap.getY() + 1.1, cap.getZ() + 0.5);
                            display.getSlot(0).set(new ItemStack(ShatteredSeals.bossMask(i)));
                            display.setNoGravity(true);
                            display.addTag(trophyTag);
                            level.addFreshEntity(display);
                        }
                    }
                }
                i++;
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer dead) {
            // A failed secret challenge ends cleanly, without arena damage.
            for (Mob boss : dead.serverLevel().getEntitiesOfClass(Mob.class,
                    new AABB(dead.blockPosition()).inflate(32),
                    mob -> mob.getTags().contains("ss_secret_boss")
                        && mob.getPersistentData().hasUUID("SSSecretOwner")
                        && mob.getPersistentData().getUUID("SSSecretOwner").equals(dead.getUUID()))) {
                boss.discard();
            }
            return;
        }
        if (!(event.getEntity() instanceof Mob defeated)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        CompoundTag mob = defeated.getPersistentData();
        if (defeated.getTags().contains("ss_depths_guardian")
                && mob.hasUUID("SSDepthsOwner") && mob.getUUID("SSDepthsOwner").equals(player.getUUID())) {
            int index = region(defeated);
            if (index < 0) return;
            String clear = "SSDepthsElite_" + mob.getLong("SSDepthsOrigin");
            CompoundTag playerData = player.getPersistentData();
            if (playerData.getBoolean(clear)) return;
            playerData.putBoolean(clear, true);
            playerData.putInt("SSDepthsEliteMask", playerData.getInt("SSDepthsEliteMask") | (1 << index));
            ItemStack lore = new ItemStack(Items.PAPER);
            lore.set(DataComponents.CUSTOM_NAME,
                Component.literal("Buried Chronicle: " + REGIONS[index]).withStyle(ChatFormatting.AQUA));
            give(player, lore);
            player.giveExperiencePoints(30);
            if (DungeonChapter.score(player, "ss_level") < 60)
                DungeonChapter.setScore(player, "ss_xp", DungeonChapter.score(player, "ss_xp") + 250);
            else if (DungeonChapter.score(player, "ss_ng") > 0)
                DungeonChapter.setScore(player, "ss_mxp", DungeonChapter.score(player, "ss_mxp") + 250);
            if ((playerData.getInt("SSDepthsEliteMask") & 63) == 63
                    && !playerData.getBoolean("SSChronicleComplete")) {
                playerData.putBoolean("SSChronicleComplete", true);
                ItemStack crown = new ItemStack(ShatteredSeals.ARCHIVISTS_CROWN.get());
                crown.set(DataComponents.CUSTOM_NAME,
                    Component.literal("Archivist's Crown | Six Buried Chronicles").withStyle(ChatFormatting.LIGHT_PURPLE));
                give(player, crown);
                say(player, "CHRONICLE ARCHIVE COMPLETE! The Archivist's Crown has been awarded.", ChatFormatting.GOLD);
            }
            say(player, "Underground elite defeated! +30 vanilla XP, +250 RPG XP and one Buried Chronicle.",
                ChatFormatting.GOLD);
        }
        if (defeated.getTags().contains("ss_secret_boss")
                && mob.hasUUID("SSSecretOwner") && mob.getUUID("SSSecretOwner").equals(player.getUUID())) {
            CompoundTag data = player.getPersistentData();
            if (data.getBoolean("SSSecretWon")) {
                say(player, "The Unbound Warden falls again. The unique trophy was awarded already.",
                    ChatFormatting.GREEN);
                return;
            }
            data.putBoolean("SSSecretWon", true);
            ItemStack trophy = new ItemStack(Items.PURPLE_BANNER);
            trophy.set(DataComponents.CUSTOM_NAME,
                Component.literal("Unbound Warden | Sanctuary Centrepiece").withStyle(ChatFormatting.LIGHT_PURPLE));
            give(player, trophy);
            say(player, "SECRET CHALLENGE COMPLETE! A unique cosmetic banner has been awarded.",
                ChatFormatting.GOLD);
        }
    }

    private static void give(ServerPlayer player, ItemStack item) {
        if (!player.getInventory().add(item)) player.drop(item, false);
    }

    private static void say(ServerPlayer player, String message, ChatFormatting color) {
        player.sendSystemMessage(Component.literal(message).withStyle(color));
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntity() instanceof ServerPlayer player)) return;
        CompoundTag original = event.getOriginal().getPersistentData();
        CompoundTag saved = player.getPersistentData();
        for (String key : COPY_KEYS) if (original.contains(key)) saved.put(key, original.get(key).copy());
        // Old saves retain Chronicle completion and placement flags on respawn.
        // Vault steps and cleared site identifiers are player-owned and prefix-scoped.
        for (String key : original.getAllKeys()) {
            if ((key.startsWith("SSVaultStep_") || key.startsWith("SSDepthsElite_"))
                    && original.contains(key)) saved.put(key, original.get(key).copy());
        }
    }
    private FinalAdventure() {}
}
