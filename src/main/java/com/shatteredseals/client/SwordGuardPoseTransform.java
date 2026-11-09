package com.shatteredseals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.shatteredseals.ShatteredSeals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * The RenderHandEvent transform previously ran before vanilla's per-item
 * transforms and could be masked by the rendering stack. NeoForge's item
 * extension hook runs immediately before the actual first-person item
 * transforms, so holding right-click has a visible sword-block pose.
 *
 * Preserve normal animation by returning false after applying a guard
 * transform; no equip, swing or attack animations are cancelled.
 */
@EventBusSubscriber(modid = ShatteredSeals.MOD_ID, value = Dist.CLIENT,
    bus = EventBusSubscriber.Bus.MOD)
public final class SwordGuardPoseTransform {
    private static final IClientItemExtensions GUARD_POSE = new IClientItemExtensions() {
        @Override
        public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm,
                ItemStack item, float partialTick, float equipProgress, float swingProgress) {
            if (!SkillTreeClient.isSwordGuardHeld(Minecraft.getInstance())
                    || item != player.getMainHandItem()
                    || arm != player.getMainArm()) return false;
            // Strong 1.8-style cross-body posture on the REAL item model,
            // followed by vanilla animations and the sword model's transforms.
            pose.translate(-0.30, 0.42, -0.19);
            pose.mulPose(Axis.YP.rotationDegrees(-35.0F));
            pose.mulPose(Axis.ZP.rotationDegrees(-68.0F));
            pose.mulPose(Axis.XP.rotationDegrees(-18.0F));
            return false;
        }
    };

    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(GUARD_POSE,
            Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
            Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
    }

    private SwordGuardPoseTransform() {}
}
