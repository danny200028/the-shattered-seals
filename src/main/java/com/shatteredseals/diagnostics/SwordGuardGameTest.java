package com.shatteredseals.diagnostics;

import com.shatteredseals.ShatteredSeals;
import com.shatteredseals.combat.ClassicSwordGuard;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Run the actual Java guard-direction maths in a loaded Minecraft server. */
@GameTestHolder(ShatteredSeals.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SwordGuardGameTest {
    @GameTest(template = "smugglers_den", timeoutTicks = 200)
    public static void directionAndCone(GameTestHelper helper) {
        Vec3 player = new Vec3(0, 70, 0);
        Vec3 facing = new Vec3(0, 0, 1);
        if (!ClassicSwordGuard.isFacingAttack(player, facing, new Vec3(0, 70, 3))) {
            helper.fail("Front-on melee hits must count as sword guarding");
            return;
        }
        if (ClassicSwordGuard.isFacingAttack(player, facing, new Vec3(0, 70, -3))) {
            helper.fail("Rear hits must not count as sword guarding");
            return;
        }
        if (ClassicSwordGuard.isFacingAttack(player, facing, new Vec3(3, 70, 0))) {
            helper.fail("Side hits must not count as sword guarding");
            return;
        }
        if (ClassicSwordGuard.isFacingAttack(player, Vec3.ZERO, new Vec3(0, 70, 3))) {
            helper.fail("Zero-length facing vectors must not count as blocking");
            return;
        }
        helper.succeed();
    }
    private SwordGuardGameTest() {}
}
