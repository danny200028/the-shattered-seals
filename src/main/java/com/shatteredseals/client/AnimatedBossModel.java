package com.shatteredseals.client;

import com.shatteredseals.ShatteredSeals;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/**
 * Six truly different full-body meshes with procedural movement, attacks,
 * hurt reactions, death poses and transition animations. This is entirely
 * native 1.21.1 ModelPart animation, not a skin applied to a vanilla model.
 *
 * Underlying entity identity, movement AI, combat, phases, drops and hitboxes
 * remain vanilla-backed and server authoritative for old-world compatibility.
 */
public final class AnimatedBossModel<T extends Mob> extends HierarchicalModel<T> {
    public static final String[] NAMES = {
        "smuggler_king", "cinder_warden", "hollow_knight",
        "arcane_seer", "sixfold_guardian", "hollow_king"
    };
    public static final ModelLayerLocation[] LAYERS = new ModelLayerLocation[NAMES.length];
    static {
        for (int i = 0; i < NAMES.length; i++)
            LAYERS[i] = new ModelLayerLocation(
                ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, "boss/" + NAMES[i]),
                "main");
    }
    private final int variant;
    private final ModelPart root, head, torso, rightArm, leftArm, rightLeg, leftLeg;
    private final ModelPart crown, wings, tail, leftHorn, rightHorn;
    private final Map<UUID, PhaseMemo> phases = new HashMap<>();

    private static final class PhaseMemo {
        int phase;
        int enteredTick;
        PhaseMemo(int phase, int enteredTick) {
            this.phase = phase;
            this.enteredTick = enteredTick;
        }
    }

    public AnimatedBossModel(ModelPart root, int variant) {
        this.root = root;
        this.variant = variant;
        head = root.getChild("head");
        torso = root.getChild("torso");
        rightArm = root.getChild("right_arm");
        leftArm = root.getChild("left_arm");
        rightLeg = root.getChild("right_leg");
        leftLeg = root.getChild("left_leg");
        crown = head.getChild("crown");
        leftHorn = head.getChild("left_horn");
        rightHorn = head.getChild("right_horn");
        wings = torso.getChild("wings");
        tail = torso.getChild("tail");
    }

    @Override public ModelPart root() {
        return root;
    }

    private static PartDefinition child(PartDefinition parent, String name,
            int u, int v, float x, float y, float z, float dx, float dy, float dz,
            float ox, float oy, float oz) {
        return parent.addOrReplaceChild(name,
            CubeListBuilder.create().texOffs(u, v).addBox(x, y, z, dx, dy, dz),
            PartPose.offset(ox, oy, oz));
    }

    public static LayerDefinition createBodyLayer(int variant) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition base = mesh.getRoot();
        final boolean beast = variant >= 4;
        // Every boss defines its own complete silhouette. The names of its
        // articulated components are shared, not the actual vanilla model.
        PartDefinition head, torso;
        if (beast) {
            float breadth = variant == 5 ? 11.0F : 9.0F;
            torso = child(base,"torso",0,40,-breadth, -8, -13,
                breadth*2, 15, 27, 0, 8, 0);
            head = child(base,"head",0,0,-7,-8,-8,14,14,13,0,5,-14);
            child(base,"right_arm",55,51,-4,-2,-4,7,17,8,-7,9,-8);
            child(base,"left_arm",55,51,-3,-2,-4,7,17,8,7,9,-8);
            child(base,"right_leg",82,50,-4,-2,-4,7,17,8,-7,9,10);
            child(base,"left_leg",82,50,-3,-2,-4,7,17,8,7,9,10);
        } else {
            float w = variant == 2 ? 6.4F : variant == 1 ? 6.0F : 5.0F;
            torso = child(base,"torso",0,38,-w,0,-3.6F,w*2,14,7.2F,0,0,0);
            head = child(base,"head",0,0,-4.5F,-9,-4.5F,9,9,9,0,0,0);
            float armWidth = variant == 2 ? 5 : 4;
            child(base,"right_arm",54,45,-armWidth,-2,-2.5F,armWidth,15,5,-w-1.5F,2,0);
            child(base,"left_arm",54,45,0,-2,-2.5F,armWidth,15,5,w+1.5F,2,0);
            child(base,"right_leg",82,48,-2.5F,0,-2.5F,5,12,5,-3,13,0);
            child(base,"left_leg",82,48,-2.5F,0,-2.5F,5,12,5,3,13,0);
        }
        switch (variant) {
            case 0 -> { // Tricorn + long coat + angular shoulders: Smuggler King
                child(head,"crown",36,0,-9,-10,-3,18,3,6,0,0,0);
                child(head,"left_horn",0,0,-10,-10,-2,3,6,4,0,0,0);
                child(head,"right_horn",0,0,7,-10,-2,3,6,4,0,0,0);
                child(torso,"wings",0,78,-8,6,2,16,13,3,0,0,0);
                child(torso,"tail",0,93,-3,11,4,6,10,3,0,0,0);
                child(torso,"bandolier",32,80,-2,0,-4,4,14,1,0,0,0);
            }
            case 1 -> { // Furnace chest, chimneys and magma blades: Cinder Warden
                child(head,"crown",36,0,-6,-12,-5,12,4,10,0,0,0);
                child(head,"left_horn",34,14,-8,-16,-3,3,10,5,0,0,0);
                child(head,"right_horn",34,14,5,-16,-3,3,10,5,0,0,0);
                child(torso,"wings",0,78,-9,0,2,18,6,5,0,0,0);
                child(torso,"tail",0,95,-3,12,3,6,8,5,0,0,0);
                child(torso,"furnace",64,88,-4,2,-5,8,7,2,0,0,0);
            }
            case 2 -> { // Heavy armoured knight, helm, pauldrons and cloak
                child(head,"crown",36,0,-6,-11,-6,12,5,12,0,0,0);
                child(head,"left_horn",34,14,-8,-15,-4,4,9,7,0,0,0);
                child(head,"right_horn",34,14,4,-15,-4,4,9,7,0,0,0);
                child(torso,"wings",0,78,-9,3,2,18,18,4,0,0,0);
                child(torso,"tail",0,95,-3,13,5,6,9,4,0,0,0);
                child(torso,"breastplate",64,88,-6,0,-5,12,8,2,0,0,0);
            }
            case 3 -> { // Levitation robes, observatory halo and spectral wings
                child(head,"crown",36,0,-8,-14,-2,16,3,4,0,0,0);
                child(head,"left_horn",34,14,-9,-15,0,3,12,3,0,0,0);
                child(head,"right_horn",34,14,6,-15,0,3,12,3,0,0,0);
                child(torso,"wings",0,78,-13,1,1,26,18,2,0,0,0);
                child(torso,"tail",0,95,-6,12,-2,12,14,11,0,0,0);
                child(torso,"orb",64,88,-3,2,-7,6,6,3,0,0,0);
            }
            case 4 -> { // Six-pillar antlered nature guardian with moss flanks
                child(head,"crown",36,0,-8,-15,-3,16,6,7,0,0,0);
                child(head,"left_horn",34,14,-11,-23,-4,5,18,7,0,0,0);
                child(head,"right_horn",34,14,6,-23,-4,5,18,7,0,0,0);
                child(torso,"wings",0,78,-12,-12,-10,24,6,20,0,0,0);
                child(torso,"tail",0,95,-3,-1,12,6,8,19,0,0,0);
                child(torso,"root_mane",64,88,-10,-12,-12,20,6,11,0,0,0);
            }
            default -> { // Dark crowned colossus, jagged spines and great tail
                child(head,"crown",36,0,-9,-17,-4,18,8,9,0,0,0);
                child(head,"left_horn",34,14,-12,-25,-7,6,20,8,0,0,0);
                child(head,"right_horn",34,14,6,-25,-7,6,20,8,0,0,0);
                child(torso,"wings",0,78,-14,-17,-12,28,10,24,0,0,0);
                child(torso,"tail",0,95,-4,-1,12,8,11,23,0,0,0);
                child(torso,"crown_spine",64,88,-3,-19,-3,6,12,8,0,0,0);
            }
        }
        return LayerDefinition.create(mesh,128,128);
    }

    private static float sine(float input) { return Mth.sin(input); }
    private static float clamp(float value) { return Mth.clamp(value, 0.0F, 1.0F); }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch) {
        root.getAllParts().forEach(ModelPart::resetPose);
        float t = ageInTicks, pace = Math.min(limbSwingAmount, 1.0F);
        boolean beast = variant >= 4;
        int phase = entity.getTags().contains("ss_phase3") ? 3 :
            entity.getTags().contains("ss_phase2") ? 2 : 1;
        PhaseMemo memo = phases.computeIfAbsent(entity.getUUID(),
            uuid -> new PhaseMemo(phase, entity.tickCount));
        if (memo.phase != phase) {
            memo.phase = phase;
            memo.enteredTick = entity.tickCount;
        }
        // A visible 32-tick transformation occurs on each actual server
        // phase-tag transition, with no client gameplay authority.
        float timeInPhase = entity.tickCount - memo.enteredTick;
        float transform = timeInPhase < 32
            ? sine((timeInPhase / 32.0F) * Mth.PI) : 0.0F;
        float attack = clamp(entity.getAttackAnim(ageInTicks - entity.tickCount));
        float hurt = entity.hurtTime > 0 ? (entity.hurtTime / 10.0F) : 0.0F;
        float breath = sine(t * 0.09F) * (phase == 3 ? 0.075F : 0.035F);
        float speed = variant == 2 ? 0.75F : variant >= 4 ? 0.85F : 1.0F;
        float stride = sine(limbSwing * 0.67F * speed) * 1.15F * pace;
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.7F;
        head.xRot = headPitch * Mth.DEG_TO_RAD * 0.7F;
        torso.xRot = breath;
        rightLeg.xRot = stride;
        leftLeg.xRot = -stride;
        rightArm.xRot = -stride;
        leftArm.xRot = stride;
        crown.zRot = sine(t * 0.055F) * 0.03F;
        leftHorn.zRot = -transform * 0.18F;
        rightHorn.zRot = transform * 0.18F;
        tail.xRot = sine(t * 0.1F) * 0.16F;
        wings.yRot = sine(t * 0.08F) * 0.08F;
        if (beast) {
            // Four-beat quadruped gait and an actual forward head-lunge.
            rightArm.xRot = stride; leftArm.xRot = -stride;
            rightLeg.xRot = -stride; leftLeg.xRot = stride;
            head.xRot -= attack * 0.65F;
            torso.xRot = breath + transform * -0.13F;
            tail.yRot = sine(t * 0.11F) * 0.23F;
        } else {
            // Named melee strike / spellcasting arms, not vanilla biped animation.
            rightArm.xRot -= attack * (variant == 2 ? 2.2F : 1.65F);
            rightArm.zRot = -0.18F - attack * 0.65F;
            leftArm.zRot = 0.1F + transform * 0.9F;
            rightArm.zRot -= transform * 0.75F;
        }
        switch (variant) {
            case 0 -> {
                wings.xRot = sine(t * 0.11F) * 0.11F;
                rightArm.yRot = attack * -0.4F;
            }
            case 1 -> {
                head.xRot += sine(t * 0.13F) * 0.08F;
                wings.zRot = transform * 0.2F;
                tail.xRot = sine(t * 0.27F) * 0.22F;
            }
            case 2 -> {
                torso.yRot = sine(t * 0.06F) * 0.06F;
                rightArm.xRot -= attack * 0.35F;
                wings.xRot = sine(t * 0.07F) * 0.09F;
            }
            case 3 -> {
                root.y = sine(t * 0.085F) * 0.8F - 1.0F;
                leftArm.zRot = 0.45F + sine(t * 0.11F) * 0.16F + transform;
                rightArm.zRot = -0.45F - sine(t * 0.11F) * 0.16F - transform;
                wings.xRot = sine(t * 0.09F) * 0.3F;
                head.yRot += sine(t * 0.065F) * 0.08F;
            }
            case 4 -> {
                head.yRot += sine(t * 0.07F) * 0.09F;
                wings.xRot = sine(t * 0.07F) * 0.07F;
            }
            default -> {
                head.xRot -= attack * 0.35F + transform * 0.2F;
                crown.zRot += sine(t * 0.08F) * 0.08F;
                tail.yRot += sine(t * 0.07F) * 0.29F;
            }
        }
        // Telegraph the existing hurt state; never change the actual damage.
        torso.zRot = hurt * sine(t * 1.2F) * 0.25F;
        if (entity.deathTime > 0) {
            float collapse = clamp(entity.deathTime / 20.0F);
            head.xRot = collapse * 0.6F;
            rightArm.xRot = collapse * 0.9F;
            leftArm.xRot = collapse * 0.9F;
            wings.xRot = collapse * -0.3F;
            root.y += collapse * 3.0F;
        }
    }
}
