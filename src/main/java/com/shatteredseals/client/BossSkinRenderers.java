package com.shatteredseals.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shatteredseals.ShatteredSeals;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Fully replaces all six storyline boss models with original articulated
 * meshes while preserving the vanilla renderer for EVERY ordinary entity.
 *
 * We deliberately do not replace entity types or their saved NBT. The same
 * boss tags, hitboxes, AI, encounter functions and equipment survive updates,
 * and boss entities spawned by older mod versions get the new visual model.
 */
@EventBusSubscriber(modid=ShatteredSeals.MOD_ID, value=Dist.CLIENT,
    bus=EventBusSubscriber.Bus.MOD)
public final class BossSkinRenderers {
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (int i=0; i<AnimatedBossModel.LAYERS.length; i++) {
            final int index=i;
            event.registerLayerDefinition(AnimatedBossModel.LAYERS[index],
                () -> AnimatedBossModel.createBodyLayer(index));
        }
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityType.PILLAGER, ctx ->
            new SplitRenderer<Pillager>(ctx, new PillagerRenderer(ctx), 0,"ss_smuggler_king"));
        event.registerEntityRenderer(EntityType.HUSK, ctx ->
            new SplitRenderer<Zombie>(ctx, new HuskRenderer(ctx), 1,"ss_cinder_warden"));
        event.registerEntityRenderer(EntityType.WITHER_SKELETON, ctx ->
            new SplitRenderer<WitherSkeleton>(ctx, new WitherSkeletonRenderer(ctx), 2,"ss_hollow_knight"));
        event.registerEntityRenderer(EntityType.EVOKER, ctx ->
            new SplitRenderer<Evoker>(ctx, new EvokerRenderer(ctx), 3,"ss_arcane_seer"));
        event.registerEntityRenderer(EntityType.RAVAGER, ctx ->
            new SplitRenderer<Ravager>(ctx, new RavagerRenderer(ctx), 4,"ss_sixfold_guardian","ss_hollow_king"));
    }

    /** Native 1.21.1 renderer, not a skin over the vanilla creature skeleton. */
    private static final class AnimatedRenderer<T extends Mob> extends MobRenderer<T,AnimatedBossModel<T>> {
        private final int variant;
        AnimatedRenderer(EntityRendererProvider.Context ctx,int variant) {
            super(ctx, new AnimatedBossModel<>(ctx.bakeLayer(AnimatedBossModel.LAYERS[variant]),variant),
                variant>=4 ? 1.1F : 0.7F);
            this.variant=variant;
        }
        @Override public ResourceLocation getTextureLocation(T entity) {
            int skin=variant==4 && entity.getTags().contains("ss_hollow_king") ? 5 : variant;
            return ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID,
                "textures/entity/boss/" + AnimatedBossModel.NAMES[skin] + ".png");
        }
    }

    private static final class SplitRenderer<T extends Mob> extends EntityRenderer<T> {
        private final EntityRenderer<T> vanilla;
        private final EntityRenderer<T> first;
        private final EntityRenderer<T> second;
        private final String primary;
        private final String alternate;

        SplitRenderer(EntityRendererProvider.Context ctx,EntityRenderer<T> vanilla,
                int variant,String primary) {
            this(ctx,vanilla,variant,primary,null);
        }
        SplitRenderer(EntityRendererProvider.Context ctx,EntityRenderer<T> vanilla,
                int variant,String primary,String alternate) {
            super(ctx);
            this.vanilla=vanilla;
            this.first=new AnimatedRenderer<>(ctx,variant);
            this.second=alternate==null ? null : new AnimatedRenderer<>(ctx,5);
            this.primary=primary;
            this.alternate=alternate;
            this.shadowRadius=variant>=4?1.05F:0.7F;
        }
        private EntityRenderer<T> select(T entity) {
            if(alternate!=null && entity.getTags().contains(alternate)) return second;
            if(entity.getTags().contains(primary)) return first;
            return vanilla;
        }
        @Override public ResourceLocation getTextureLocation(T entity) {
            return select(entity).getTextureLocation(entity);
        }
        @Override public boolean shouldRender(T entity,Frustum frustum,double x,double y,double z) {
            return select(entity).shouldRender(entity,frustum,x,y,z);
        }
        @Override public void render(T entity,float rotation,float partialTick,
                PoseStack pose,MultiBufferSource buffers,int light) {
            select(entity).render(entity,rotation,partialTick,pose,buffers,light);
        }
    }

    private BossSkinRenderers() {}
}
