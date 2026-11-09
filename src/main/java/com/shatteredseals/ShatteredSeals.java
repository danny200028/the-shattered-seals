package com.shatteredseals;

import com.shatteredseals.world.ShatteredBiomeRegion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod(ShatteredSeals.MOD_ID)
public final class ShatteredSeals {
    public static final String MOD_ID = "shatteredseals";
    public static final String VERSION = "1.0.56";
    private static final System.Logger LOGGER = System.getLogger("The Shattered Seals");

    public ShatteredSeals(IEventBus modBus) {
        modBus.addListener(this::commonSetup);
        LOGGER.log(System.Logger.Level.INFO,
            "The Shattered Seals progression and GUI initialized ({0})", VERSION);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Regions.register(new ShatteredBiomeRegion());
            SurfaceRuleManager.addSurfaceRules(
                SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ShatteredBiomeRegion.surfaceRules());
            LOGGER.log(System.Logger.Level.INFO,
                "Six rare Shattered Seals biomes registered for Overworld generation.");
        });
    }
}
