package com.shatteredseals.adventure;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ShatteredSeals.MOD_ID)
public final class DungeonAdventure {
    private static final String[] RUNES = {"Sun", "Tide", "Ash"};
    private static final String QUEST_ORIGIN = "ShatteredSealsChallenge";

    @SubscribeEvent
    public static void interactRune(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();
        BlockPos pos = event.getPos().above();
        for (Marker marker : level.getEntitiesOfClass(Marker.class, new AABB(pos).inflate(0.1),
                 candidate -> candidate.blockPosition().equals(pos))) {
            DungeonChapter chapter = chapterOf(marker);
            if (chapter == null) continue;
            for (int rune = 0; rune < RUNES.length; rune++) {
                if (marker.getTags().contains("ss_rune_" + rune)) {
                    pressRune(player, chapter, rune);
                    consume(event);
                    return;
                }
            }
            if (marker.getTags().contains("ss_challenge")) {
                challenge(player, chapter, pos);
                consume(event);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void talkToKeeper(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getEntity() instanceof ServerPlayer player)
                || !event.getTarget().getTags().contains("ss_keeper")) return;
        DungeonChapter chapter = chapterOf(event.getTarget());
        if (chapter == null) return;
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        player.sendSystemMessage(Component.literal(event.getTarget().getName().getString() + "  |  " + chapter.clue)
            .withStyle(ChatFormatting.GOLD));
        int state = DungeonChapter.score(player, chapter.sideScore());
        switch (state) {
            case 0 -> {
                DungeonChapter.setScore(player, chapter.sideScore(), 1);
                message(player, chapter.sideQuest + " accepted. Use the engraved challenge stone and defeat "
                    + chapter.miniboss + ".", ChatFormatting.AQUA);
            }
            case 1 -> message(player, "Use the engraved challenge stone and defeat " + chapter.miniboss + ".", ChatFormatting.AQUA);
            case 2 -> {
                DungeonChapter.setScore(player, chapter.sideScore(), 3);
                String tier = chapter.number() <= 2 ? "rare" : chapter.number() <= 5 ? "epic" : "legendary";
                player.getServer().getCommands().performPrefixedCommand(
                    player.createCommandSourceStack().withPermission(2).withSuppressedOutput(),
                    "loot give @s loot shatteredseals:repeat/" + tier);
                player.giveExperiencePoints(15 + chapter.number() * 10);
                // A replayed chest has already been opened. Award exactly one new
                // ritual sigil for completing this chapter's keeper challenge.
                if (DungeonChapter.score(player, "ss_ng") >= 1
                    && DungeonChapter.score(player, "ss_ngchapter") == chapter.number()) {
                    player.getServer().getCommands().performPrefixedCommand(
                        player.createCommandSourceStack().withPermission(2).withSuppressedOutput(),
                        "loot give @s loot " + chapter.replaySigilLoot());
                    message(player, "The keeper renews your summoning sigil for this New Game+ chapter.",
                        ChatFormatting.GOLD);
                }
                message(player, chapter.sideQuest + " completed. The keeper rewards you with Warden's Spoils.",
                    ChatFormatting.GREEN);
            }
            default -> message(player, chapter.sideQuest + " is complete.", ChatFormatting.GRAY);
        }
    }

    @SubscribeEvent
    public static void onMinibossDeath(LivingDeathEvent event) {
        if (!event.getEntity().getTags().contains("ss_miniboss")
                || !(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        DungeonChapter chapter = chapterOf(event.getEntity());
        if (chapter != null && DungeonChapter.score(player, chapter.sideScore()) == 1) {
            DungeonChapter.setScore(player, chapter.sideScore(), 2);
            message(player, chapter.miniboss + " defeated. Return to the keeper for your reward.", ChatFormatting.GREEN);
        }
    }

    private static void pressRune(ServerPlayer player, DungeonChapter chapter, int rune) {
        int step = Math.clamp(DungeonChapter.score(player, chapter.puzzleScore()), 0, RUNES.length);
        if (step == RUNES.length) {
            message(player, "The arena's rune ward is already open.", ChatFormatting.GRAY);
            return;
        }
        if (chapter.runeAt(step) != rune) {
            DungeonChapter.setScore(player, chapter.puzzleScore(), chapter.runeAt(0) == rune ? 1 : 0);
            message(player, "The ward rejects the order. Listen to the keeper's clue.", ChatFormatting.RED);
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 0.8F, 0.7F);
            return;
        }
        DungeonChapter.setScore(player, chapter.puzzleScore(), step + 1);
        player.playSound(SoundEvents.NOTE_BLOCK_CHIME.value(), 0.7F, 0.9F + step * 0.2F);
        if (step + 1 == RUNES.length) {
            message(player, "The arena ward breaks. Claim the sigil and stand on the crying obsidian ritual pad.",
                ChatFormatting.GREEN);
        } else {
            message(player, RUNES[rune] + " rune attuned (" + (step + 1) + "/" + RUNES.length + ").",
                ChatFormatting.AQUA);
        }
    }

    private static void challenge(ServerPlayer player, DungeonChapter chapter, BlockPos origin) {
        int state = DungeonChapter.score(player, chapter.sideScore());
        if (state != 1) {
            message(player, state == 0 ? "Speak to the keeper to accept the challenge." : "This challenge is defeated.",
                ChatFormatting.YELLOW);
            return;
        }
        ServerLevel level = player.serverLevel();
        if (level.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
            message(player, "The challenge requires a hostile difficulty.", ChatFormatting.RED);
            return;
        }
        if (!level.getEntitiesOfClass(Mob.class, new AABB(origin).inflate(64),
                mob -> mob.isAlive() && mob.getTags().contains("ss_miniboss")
                    && mob.getPersistentData().getLong(QUEST_ORIGIN) == origin.asLong()).isEmpty()) {
            message(player, chapter.miniboss + " is already nearby.", ChatFormatting.YELLOW);
            return;
        }
        Mob mob = chapter.mob.create(level);
        if (mob == null) return;
        BlockPos spawn = origin.below().east(2);
        mob.moveTo(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.getYRot() + 180, 0);
        if (!level.noCollision(mob)) {
            message(player, "Clear the chamber around the challenge stone.", ChatFormatting.RED);
            return;
        }
        mob.addTag("ss_miniboss");
        mob.addTag(chapter.tag());
        mob.getPersistentData().putLong(QUEST_ORIGIN, origin.asLong());
        mob.setCustomName(Component.literal(chapter.miniboss).withStyle(ChatFormatting.LIGHT_PURPLE));
        mob.setCustomNameVisible(true);
        mob.setPersistenceRequired();
        mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(chapter.health);
        mob.setHealth(chapter.health);
        if (chapter.weapon != Items.AIR) mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(chapter.weapon));
        if (level.addFreshEntity(mob)) {
            message(player, chapter.miniboss + " answers the challenge.", ChatFormatting.LIGHT_PURPLE);
        }
    }

    private static DungeonChapter chapterOf(net.minecraft.world.entity.Entity entity) {
        for (DungeonChapter chapter : DungeonChapter.values()) {
            if (entity.getTags().contains(chapter.tag())) return chapter;
        }
        return null;
    }

    private static void consume(PlayerInteractEvent.RightClickBlock event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void message(ServerPlayer player, String message, ChatFormatting color) {
        player.sendSystemMessage(Component.literal(message).withStyle(color));
    }

    private DungeonAdventure() {}
}
