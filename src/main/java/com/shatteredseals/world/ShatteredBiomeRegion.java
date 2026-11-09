package com.shatteredseals.world;

import com.mojang.datafixers.util.Pair;
import com.shatteredseals.ShatteredSeals;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.api.Region;
import terrablender.api.RegionType;

/**
 * A single low-weight TerraBlender region holds all six chapter biomes.
 * Only matching vanilla climate cells are replaced, leaving the bulk of
 * vanilla geography unchanged. Never rewrite already-generated chunks.
 */
public final class ShatteredBiomeRegion extends Region {
    public static final ResourceKey<Biome> SMUGGLERS =
        key("smugglers_wilds");
    public static final ResourceKey<Biome> CINDER =
        key("cinder_wastes");
    public static final ResourceKey<Biome> HOLLOW =
        key("hollow_moor");
    public static final ResourceKey<Biome> ARCANE =
        key("arcane_highlands");
    public static final ResourceKey<Biome> SIXFOLD =
        key("sixfold_expanse");
    public static final ResourceKey<Biome> KING =
        key("hollow_kings_dominion");

    public ShatteredBiomeRegion() {
        // TerraBlender weights this region against vanilla regions.
        super(ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, "chapter_overworld"),
            RegionType.OVERWORLD, 1);
    }

    private static ResourceKey<Biome> key(String name) {
        return ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(ShatteredSeals.MOD_ID, name));
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
                          Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(Biomes.PLAINS, SMUGGLERS);
            builder.replaceBiome(Biomes.DESERT, CINDER);
            builder.replaceBiome(Biomes.FOREST, HOLLOW);
            builder.replaceBiome(Biomes.SNOWY_PLAINS, ARCANE);
            builder.replaceBiome(Biomes.JUNGLE, SIXFOLD);
            builder.replaceBiome(Biomes.DARK_FOREST, KING);
        });
    }

    public static SurfaceRules.RuleSource surfaceRules() {
        // Only the natural surface layer changes; caves and ore generation remain normal.
        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.isBiome(SMUGGLERS),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.PODZOL.defaultBlockState()))),
            SurfaceRules.ifTrue(SurfaceRules.isBiome(CINDER),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.BASALT.defaultBlockState()))),
            SurfaceRules.ifTrue(SurfaceRules.isBiome(HOLLOW),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState()))),
            SurfaceRules.ifTrue(SurfaceRules.isBiome(ARCANE),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.CALCITE.defaultBlockState()))),
            SurfaceRules.ifTrue(SurfaceRules.isBiome(SIXFOLD),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.MOSS_BLOCK.defaultBlockState()))),
            SurfaceRules.ifTrue(SurfaceRules.isBiome(KING),
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.POLISHED_BLACKSTONE.defaultBlockState())))
        );
    }
    private ShatteredBiomeRegion(Void unused) {
        this();
    }
}
