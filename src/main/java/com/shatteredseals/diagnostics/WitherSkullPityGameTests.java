package com.shatteredseals.diagnostics;

import com.mojang.authlib.GameProfile;
import com.shatteredseals.ShatteredSeals;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import com.shatteredseals.progression.WitherSkullPity;
import java.util.ArrayList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Tests actual death-drop event handling, plus natural-drop resets. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WitherSkullPityGameTests {
    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void twentiethDryPlayerKillGuaranteesExactlyOneSkull(GameTestHelper helper) {
        var skeleton = EntityType.WITHER_SKELETON.create(helper.getLevel());
        if (skeleton == null) { helper.fail("Could not create Wither Skeleton"); return; }
        ServerPlayer player = testPlayer(helper);

        for (int kill = 1; kill <= WitherSkullPity.MAX_DRY_KILLS; kill++) {
            var drops = new ArrayList<ItemEntity>();
            WitherSkullPity.onDrops(new LivingDropsEvent(
                skeleton, player.damageSources().playerAttack(player), drops, true));
            long skulls = drops.stream()
                .filter(drop -> drop.getItem().is(Items.WITHER_SKELETON_SKULL)).count();
            long expected = kill == WitherSkullPity.MAX_DRY_KILLS ? 1 : 0;
            if (skulls != expected) {
                helper.fail("Skull guarantee triggered on wrong kill: " + kill);
                return;
            }
        }
        if (player.getPersistentData().getInt(WitherSkullPity.COUNTER_KEY) != 0) {
            helper.fail("Guaranteed skull did not reset the player's streak");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 100)
    public static void vanillaSkullResetsStreakWithoutDuplicate(GameTestHelper helper) {
        var skeleton = EntityType.WITHER_SKELETON.create(helper.getLevel());
        if (skeleton == null) { helper.fail("Could not create Wither Skeleton"); return; }
        ServerPlayer player = testPlayer(helper);
        player.getPersistentData().putInt(WitherSkullPity.COUNTER_KEY, 19);

        var drops = new ArrayList<ItemEntity>();
        drops.add(new ItemEntity(helper.getLevel(), 0, 0, 0,
            new ItemStack(Items.WITHER_SKELETON_SKULL)));
        WitherSkullPity.onDrops(new LivingDropsEvent(
            skeleton, player.damageSources().playerAttack(player), drops, true));
        if (drops.size() != 1
                || player.getPersistentData().getInt(WitherSkullPity.COUNTER_KEY) != 0) {
            helper.fail("Natural skull must reset the count without an extra skull");
            return;
        }

        for (int i = 1; i <= 19; i++) {
            var emptyDrops = new ArrayList<ItemEntity>();
            WitherSkullPity.onDrops(new LivingDropsEvent(
                skeleton, player.damageSources().playerAttack(player), emptyDrops, true));
            if (!emptyDrops.isEmpty()) {
                helper.fail("Natural skull failed to reset the bad-luck streak");
                return;
            }
        }
        if (player.getPersistentData().getInt(WitherSkullPity.COUNTER_KEY) != 19) {
            helper.fail("Dry streak did not advance after reset");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "smugglers_den", timeoutTicks = 80)
    public static void environmentalAndOtherMobDeathsDoNotCount(GameTestHelper helper) {
        var skeleton = EntityType.WITHER_SKELETON.create(helper.getLevel());
        var zombie = EntityType.ZOMBIE.create(helper.getLevel());
        if (skeleton == null || zombie == null) {
            helper.fail("Could not create test mobs");
            return;
        }
        ServerPlayer player = testPlayer(helper);
        var drops = new ArrayList<ItemEntity>();
        WitherSkullPity.onDrops(new LivingDropsEvent(
            skeleton, helper.getLevel().damageSources().generic(), drops, false));
        WitherSkullPity.onDrops(new LivingDropsEvent(
            zombie, player.damageSources().playerAttack(player), drops, true));
        if (player.getPersistentData().getInt(WitherSkullPity.COUNTER_KEY) != 0
                || !drops.isEmpty()) {
            helper.fail("Other deaths should not affect skull progress");
            return;
        }
        helper.succeed();
    }

    /**
     * GameTestHelper.makeMockPlayer returns a non-server Player, so the real
     * server-authoritative drop listener correctly ignores it. Construct a
     * detached ServerPlayer and swallow notifications (no network connection).
     */
    private static ServerPlayer testPlayer(GameTestHelper helper) {
        return new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "SkullHuntTest"),
                ClientInformation.createDefault()) {
            @Override
            public void displayClientMessage(Component message, boolean actionBar) {
                // Detached GameTest player has no connection to receive chat.
            }
        };
    }

    private WitherSkullPityGameTests() {}
}
