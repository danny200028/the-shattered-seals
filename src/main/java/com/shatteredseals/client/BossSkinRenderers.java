package com.shatteredseals.client;

import com.shatteredseals.ShatteredSeals;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Original full-body boss skins; ordinary vanilla mobs retain vanilla textures. */
@EventBusSubscriber(modid=ShatteredSeals.MOD_ID, value=Dist.CLIENT,
    bus=EventBusSubscriber.Bus.MOD)
public final class BossSkinRenderers {
    private static ResourceLocation skin(String boss) {
        return ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, "textures/entity/boss/" + boss + ".png");
    }
    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityType.PILLAGER, Smuggler::new);
        event.registerEntityRenderer(EntityType.HUSK, Cinder::new);
        event.registerEntityRenderer(EntityType.WITHER_SKELETON, Knight::new);
        event.registerEntityRenderer(EntityType.EVOKER, Seer::new);
        event.registerEntityRenderer(EntityType.RAVAGER, Royal::new);
    }
    public static final class Smuggler extends PillagerRenderer {
        public Smuggler(EntityRendererProvider.Context ctx) { super(ctx); }
        @Override public ResourceLocation getTextureLocation(Pillager entity) {
            return entity.getTags().contains("ss_smuggler_king") ? skin("smuggler_king") : super.getTextureLocation(entity);
        }
    }
    public static final class Cinder extends HuskRenderer {
        public Cinder(EntityRendererProvider.Context ctx) { super(ctx); }
        @Override public ResourceLocation getTextureLocation(Zombie entity) {
            return entity.getTags().contains("ss_cinder_warden") ? skin("cinder_warden") : super.getTextureLocation(entity);
        }
    }
    public static final class Knight extends WitherSkeletonRenderer {
        public Knight(EntityRendererProvider.Context ctx) { super(ctx); }
        @Override public ResourceLocation getTextureLocation(WitherSkeleton entity) {
            return entity.getTags().contains("ss_hollow_knight") ? skin("hollow_knight") : super.getTextureLocation(entity);
        }
    }
    public static final class Seer extends EvokerRenderer<Evoker> {
        public Seer(EntityRendererProvider.Context ctx) { super(ctx); }
        @Override public ResourceLocation getTextureLocation(Evoker entity) {
            return entity.getTags().contains("ss_arcane_seer") ? skin("arcane_seer") : super.getTextureLocation(entity);
        }
    }
    public static final class Royal extends RavagerRenderer {
        public Royal(EntityRendererProvider.Context ctx) { super(ctx); }
        @Override public ResourceLocation getTextureLocation(Ravager entity) {
            if (entity.getTags().contains("ss_sixfold_guardian")) return skin("sixfold_guardian");
            if (entity.getTags().contains("ss_hollow_king")) return skin("hollow_king");
            return super.getTextureLocation(entity);
        }
    }
    private BossSkinRenderers() {}
}
