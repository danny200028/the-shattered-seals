package com.shatteredseals.adventure;

import com.shatteredseals.ShatteredSeals;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * v1.0.56: two opt-in, server-owned activities. No new power tiers.
 * CompoundTag state belongs to the player (never to a client packet).
 * One bounty can be active, with 10-minute cooldown after reward collection.
 * One NG+ trial can be scored per chapter per cycle, even if the boss is replayed.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class AdventureContracts {
    private static final String SAVE = "SSContractsV56";
    private static final long BOUNTY_COOLDOWN = 12000L;
    private static final String[] BIOMES = {
        "smugglers_wilds", "cinder_wastes", "hollow_moor",
        "arcane_highlands", "sixfold_expanse", "hollow_kings_dominion"
    };
    private static final EntityType<?>[] PREY = {
        EntityType.PILLAGER, EntityType.ZOMBIE, EntityType.SKELETON,
        EntityType.SPIDER, EntityType.CREEPER, EntityType.ENDERMAN
    };
    private static final String[] PREY_NAMES = {
        "Pillager", "Zombie", "Skeleton", "Spider", "Creeper", "Enderman"
    };
    private static final String[] WARNINGS = {
        "Smuggler's volley: move out of the danger circle!",
        "Cinder eruption: retreat from the glowing ward!",
        "Hollow chill: leave the cursed ground!",
        "Arcane fracture: scatter before the starburst!",
        "Sixfold surge: break formation!",
        "Black Crown collapse: evade the king's wrath!"
    };

    private static CompoundTag data(ServerPlayer player) {
        return player.getPersistentData().getCompound(SAVE);
    }
    private static void save(ServerPlayer player, CompoundTag value) {
        player.getPersistentData().put(SAVE, value);
    }
    private static long clock(ServerPlayer player) {
        return player.getServer().overworld().getGameTime();
    }
    private static int chapter(ServerPlayer player) {
        DungeonChapter current = DungeonChapter.current(player);
        return current == null ? 6 : current.number();
    }
    public static String huntName(int oneBasedChapter) {
        return PREY_NAMES[Math.clamp(oneBasedChapter, 1, 6) - 1];
    }
    public static int required(int kind, int oneBasedChapter) {
        return switch (kind) {
            case 1 -> Math.min(7, 3 + oneBasedChapter);
            case 2, 3 -> 1;
            default -> 0;
        };
    }
    public static boolean trialUnlocked(int level, int kingVictories, int ng, int replayChapter) {
        return level >= 60 && kingVictories >= 1 && ng >= 1
            && replayChapter >= 1 && replayChapter <= 6;
    }
    public static boolean validTrialMask(int ng, int mask) {
        return mask == 1 || mask == 2 || (mask == 3 && ng >= 3);
    }
    private static void notify(ServerPlayer player, String value, ChatFormatting color) {
        player.sendSystemMessage(Component.literal(value).withStyle(color));
    }
    private static void resetCycle(ServerPlayer player, CompoundTag state) {
        int current = DungeonChapter.score(player, "ss_ng");
        if (state.getInt("trialCycle") != current) {
            state.putInt("trialCycle", current);
            state.putInt("trialWins", 0);
            state.putInt("trialChapter", 0);
            state.putInt("trialMask", 0);
            save(player, state);
        }
    }

    /** Only the player, not a client-supplied scoreboard value, may configure a trial. */
    public static int armTrial(ServerPlayer player, int mask) {
        CompoundTag state = data(player);
        resetCycle(player, state);
        int ng = DungeonChapter.score(player, "ss_ng");
        int replay = DungeonChapter.score(player, "ss_ngchapter");
        if (!trialUnlocked(DungeonChapter.score(player, "ss_level"),
                DungeonChapter.score(player, "ss_vk_king"), ng, replay)) {
            notify(player, "Shattered Trials require level 60, a Hollow King victory and an active NG+ chapter.", ChatFormatting.RED);
            return 0;
        }
        if (!validTrialMask(ng, mask)) {
            notify(player, "Choose Hazard Pulse (1) or Scarcity (2). Dual trials require NG+ cycle 3.", ChatFormatting.RED);
            return 0;
        }
        if ((state.getInt("trialWins") & (1 << (replay - 1))) != 0) {
            notify(player, "This chapter's trial medal has already been earned during this NG+ cycle.", ChatFormatting.YELLOW);
            return 0;
        }
        state.putInt("trialChapter", replay);
        state.putInt("trialMask", mask);
        save(player, state);
        notify(player, "Trial armed for chapter " + replay + ": " + (mask == 3 ? "Hazard Pulse + Scarcity"
            : mask == 1 ? "Hazard Pulse" : "Scarcity") + ". Summon its boss at the real arena.", ChatFormatting.GOLD);
        return 1;
    }

    public static int cancelTrial(ServerPlayer player) {
        CompoundTag state = data(player);
        state.putInt("trialChapter", 0);
        state.putInt("trialMask", 0);
        save(player, state);
        notify(player, "Unstarted trial cancelled. Existing combat and chapter progress are unchanged.", ChatFormatting.GRAY);
        return 1;
    }

    /** Called on natural boss entity joins. Only an armed, nearby owner at the matching chapter binds it. */
    @SubscribeEvent
    public static void onBossSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Mob boss) || !(event.getLevel() instanceof ServerLevel level)
                || !boss.getTags().contains("ss_boss") || boss.getTags().contains("ss_trial")
                || boss.getTags().contains("ss_watch_linked")) return;
        DungeonChapter target = null;
        for (DungeonChapter chapter : DungeonChapter.values()) {
            if (boss.getTags().contains("ss_" + chapter.boss.toLowerCase(java.util.Locale.ROOT).replace(' ', '_'))) {
                target = chapter;
                break;
            }
        }
        if (target == null) return;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(boss) > 256.0) continue;
            CompoundTag state = data(player);
            resetCycle(player, state);
            if (state.getInt("trialChapter") != target.number()
                    || !trialUnlocked(DungeonChapter.score(player, "ss_level"),
                        DungeonChapter.score(player, "ss_vk_king"),
                        DungeonChapter.score(player, "ss_ng"),
                        DungeonChapter.score(player, "ss_ngchapter"))) continue;
            int mask = state.getInt("trialMask");
            if (!validTrialMask(DungeonChapter.score(player, "ss_ng"), mask)) continue;
            boss.addTag("ss_trial");
            boss.getPersistentData().putUUID("SSTrialOwner", player.getUUID());
            boss.getPersistentData().putInt("SSTrialChapter", target.number());
            boss.getPersistentData().putInt("SSTrialCycle", DungeonChapter.score(player, "ss_ng"));
            boss.getPersistentData().putInt("SSTrialMask", mask);
            state.putInt("trialChapter", 0);
            state.putInt("trialMask", 0);
            save(player, state);
            notify(player, "SHATTERED TRIAL STARTED: " + target.dungeon + ". Watch for telegraphed hazards.", ChatFormatting.LIGHT_PURPLE);
            break;
        }
    }

    /** Every second, check eligible objectives; hazard pulses happen once per 5 seconds. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 20 != 0) return;
        if (!player.serverLevel().dimension().equals(Level.OVERWORLD)) return;
        CompoundTag state = data(player);
        resetCycle(player, state);
        if (state.getInt("bountyKind") == 2 && state.getInt("bountyProgress") == 0) {
            int target = state.getInt("bountyChapter");
            var biome = player.serverLevel().getBiome(player.blockPosition()).unwrapKey().orElse(null);
            if (target >= 1 && target <= 6 && biome != null
                    && biome.location().getNamespace().equals(ShatteredSeals.MOD_ID)
                    && biome.location().getPath().equals(BIOMES[target - 1])) {
                state.putInt("bountyProgress", 1);
                save(player, state);
                notify(player, "Survey complete. Return to J > Bounties to claim your reward.", ChatFormatting.GREEN);
            }
        }
        long time = clock(player);
        for (Mob boss : player.serverLevel().getEntitiesOfClass(Mob.class,
                new AABB(player.blockPosition()).inflate(32),
                mob -> mob.isAlive() && mob.getTags().contains("ss_trial"))) {
            CompoundTag trial = boss.getPersistentData();
            if (!trial.hasUUID("SSTrialOwner") || !trial.getUUID("SSTrialOwner").equals(player.getUUID())) continue;
            int mask = trial.getInt("SSTrialMask");
            int c = trial.getInt("SSTrialChapter");
            if (c < 1 || c > 6 || (mask & 1) == 0) continue;
            long next = trial.getLong("SSTrialNextPulse");
            if (next <= 0L) {
                trial.putLong("SSTrialNextPulse", time + 100L);
                trial.putBoolean("SSTrialWarned", false);
                continue;
            }
            if (time >= next - 20L && !trial.getBoolean("SSTrialWarned")) {
                trial.putBoolean("SSTrialWarned", true);
                player.displayClientMessage(Component.literal("TRIAL WARNING: " + WARNINGS[c - 1])
                    .withStyle(ChatFormatting.RED), true);
                player.serverLevel().sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    boss.getX(), boss.getY() + 1, boss.getZ(), 24, 3, 1, 3, 0.04);
            }
            if (time >= next) {
                trial.putLong("SSTrialNextPulse", time + 100L);
                trial.putBoolean("SSTrialWarned", false);
                if (player.distanceToSqr(boss) <= 64.0) {
                    player.hurt(player.serverLevel().damageSources().magic(), 2.0F + c * 0.5F);
                    switch (c) {
                        case 1, 4 -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 0));
                        case 2 -> player.setRemainingFireTicks(Math.max(player.getRemainingFireTicks(), 50));
                        case 3, 6 -> player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
                        case 5 -> player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 80, 0));
                        default -> { }
                    }
                }
            }
        }
    }

    /** Scarcity is arena-limited and affects only the trial owner during combat. */
    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        for (Mob boss : player.serverLevel().getEntitiesOfClass(Mob.class,
                new AABB(player.blockPosition()).inflate(24),
                mob -> mob.isAlive() && mob.getTags().contains("ss_trial"))) {
            CompoundTag trial = boss.getPersistentData();
            if ((trial.getInt("SSTrialMask") & 2) != 0 && trial.hasUUID("SSTrialOwner")
                    && trial.getUUID("SSTrialOwner").equals(player.getUUID())) {
                event.setAmount(event.getAmount() * 0.5F);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)
                || player.serverLevel().isClientSide()) return;
        if (event.getEntity() instanceof Mob mob) {
            // Exclude summoned guardians, bosses, pets, named mobs and fast kill farms.
            CompoundTag state = data(player);
            int kind = state.getInt("bountyKind");
            int c = state.getInt("bountyChapter");
            if (kind == 1 && c >= 1 && c <= 6 && mob.getType() == PREY[c - 1]
                    && !mob.hasCustomName() && mob.getTags().stream().noneMatch(tag -> tag.startsWith("ss_"))
                    && clock(player) - state.getLong("bountyLastKill") >= 40L) {
                state.putLong("bountyLastKill", clock(player));
                advanceBounty(player, state);
            }
            if (mob.getTags().contains("ss_boss")) {
                if (kind == 3 && c >= 1 && c <= 6
                    && mob.getTags().contains("ss_" + DungeonChapter.values()[c - 1].boss
                        .toLowerCase(java.util.Locale.ROOT).replace(' ', '_'))) {
                    advanceBounty(player, state);
                }
                if (mob.getTags().contains("ss_trial")) awardTrial(player, mob);
            }
        }
    }

    private static void advanceBounty(ServerPlayer player, CompoundTag state) {
        int goal = required(state.getInt("bountyKind"), state.getInt("bountyChapter"));
        int progress = Math.min(goal, state.getInt("bountyProgress") + 1);
        state.putInt("bountyProgress", progress);
        save(player, state);
        if (progress == goal) {
            notify(player, "Bounty completed! Open J > Bounties to claim your reward.", ChatFormatting.GREEN);
        }
    }

    private static void awardTrial(ServerPlayer player, Mob boss) {
        CompoundTag trial = boss.getPersistentData();
        if (!trial.hasUUID("SSTrialOwner") || !trial.getUUID("SSTrialOwner").equals(player.getUUID())) return;
        int cycle = trial.getInt("SSTrialCycle");
        int c = trial.getInt("SSTrialChapter");
        if (c < 1 || c > 6 || cycle != DungeonChapter.score(player, "ss_ng")) return;
        CompoundTag state = data(player);
        resetCycle(player, state);
        int bit = 1 << (c - 1);
        if ((state.getInt("trialWins") & bit) != 0) return;
        state.putInt("trialWins", state.getInt("trialWins") | bit);
        save(player, state);
        ItemStack medal = new ItemStack(Items.PAPER);
        medal.set(DataComponents.CUSTOM_NAME, Component.literal("Shattered Trial Medal | " + DungeonChapter.values()[c - 1].dungeon)
            .withStyle(ChatFormatting.GOLD));
        deliver(player, medal);
        deliver(player, new ItemStack(Items.AMETHYST_SHARD, 2 + (trial.getInt("SSTrialMask") == 3 ? 1 : 0)));
        player.giveExperiencePoints(Math.min(180, 60 + c * 12));
        notify(player, "Trial conquered! Earned a chapter medal and a modest resource reward.", ChatFormatting.GOLD);
    }

    public static int acceptBounty(ServerPlayer player, int kind) {
        if (kind < 1 || kind > 3) return 0;
        CompoundTag state = data(player);
        if (state.getInt("bountyKind") != 0) {
            notify(player, "Only one bounty may be active. Complete or abandon it first.", ChatFormatting.YELLOW);
            return 0;
        }
        if (state.contains("bountyLastClaim") && clock(player) - state.getLong("bountyLastClaim") < BOUNTY_COOLDOWN) {
            notify(player, "The Bounty Board is recovering. Check its cooldown in J > Bounties.", ChatFormatting.YELLOW);
            return 0;
        }
        if (kind == 3 && DungeonChapter.current(player) == null) {
            notify(player, "Guardian bounties require an active main-story or NG+ dungeon.", ChatFormatting.YELLOW);
            return 0;
        }
        int target = chapter(player);
        state.putInt("bountyKind", kind);
        state.putInt("bountyChapter", target);
        state.putInt("bountyProgress", 0);
        state.putLong("bountyAccepted", clock(player));
        state.putLong("bountyLastKill", -100L);
        save(player, state);
        String label = kind == 1 ? "Defeat " + required(kind, target) + " " + huntName(target) + " enemies"
            : kind == 2 ? "Survey the " + BIOMES[target - 1].replace('_', ' ') + " biome"
            : "Defeat " + DungeonChapter.values()[target - 1].boss;
        notify(player, "Bounty accepted: " + label + ". Claim in J > Bounties when complete.", ChatFormatting.AQUA);
        return 1;
    }

    public static int claimBounty(ServerPlayer player) {
        CompoundTag state = data(player);
        int kind = state.getInt("bountyKind");
        int c = state.getInt("bountyChapter");
        if (kind < 1 || kind > 3 || c < 1 || c > 6 || state.getInt("bountyProgress") < required(kind, c)) {
            notify(player, "No completed bounty is ready to claim.", ChatFormatting.YELLOW);
            return 0;
        }
        state.putInt("bountyKind", 0);
        state.putInt("bountyChapter", 0);
        state.putInt("bountyProgress", 0);
        state.putLong("bountyLastClaim", clock(player));
        state.putInt("bountyClaims", Math.min(99999, state.getInt("bountyClaims") + 1));
        save(player, state);
        Item resource = kind == 1 ? Items.IRON_INGOT : kind == 2 ? Items.AMETHYST_SHARD : Items.GOLD_INGOT;
        deliver(player, new ItemStack(resource, kind == 3 ? 3 : 2));
        player.giveExperiencePoints(kind == 3 ? 80 : kind == 1 ? 45 : 35);
        notify(player, "Bounty claimed. Materials and capped vanilla XP awarded; next contract in 10 minutes.",
            ChatFormatting.GREEN);
        return 1;
    }

    public static int abandonBounty(ServerPlayer player) {
        CompoundTag state = data(player);
        if (state.getInt("bountyKind") == 0) {
            notify(player, "No active bounty to abandon.", ChatFormatting.YELLOW);
            return 0;
        }
        state.putInt("bountyKind", 0);
        state.putInt("bountyProgress", 0);
        state.putInt("bountyChapter", 0);
        save(player, state);
        notify(player, "Bounty abandoned. No reward was granted.", ChatFormatting.GRAY);
        return 1;
    }

    private static void deliver(ServerPlayer player, ItemStack reward) {
        if (!player.getInventory().add(reward)) player.drop(reward, false);
    }

    /** Integers only: client UI receives read-only server state, never control of rewards. */
    public static int[] trialState(ServerPlayer player) {
        CompoundTag state = data(player);
        resetCycle(player, state);
        return new int[]{state.getInt("trialChapter"), state.getInt("trialMask"),
            state.getInt("trialWins"), state.getInt("trialCycle")};
    }
    public static int[] bountyState(ServerPlayer player) {
        CompoundTag state = data(player);
        long remaining = state.contains("bountyLastClaim")
            ? Math.max(0, BOUNTY_COOLDOWN - (clock(player) - state.getLong("bountyLastClaim"))) : 0;
        return new int[]{state.getInt("bountyKind"), state.getInt("bountyChapter"),
            state.getInt("bountyProgress"),
            required(state.getInt("bountyKind"), state.getInt("bountyChapter")),
            (int)((remaining + 19) / 20), state.getInt("bountyClaims")};
    }

    /** NeoForge respawn cloning must preserve custom-contract data alongside scoreboard progress. */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() || !(event.getEntity() instanceof ServerPlayer player)) return;
        CompoundTag original = event.getOriginal().getPersistentData();
        if (original.contains(SAVE)) player.getPersistentData().put(SAVE, original.getCompound(SAVE).copy());
    }
    private AdventureContracts() {}
}
