package com.shatteredseals;

import net.neoforged.fml.common.Mod;

@Mod(ShatteredSeals.MOD_ID)
public final class ShatteredSeals {
    public static final String MOD_ID = "shatteredseals";
    public static final String VERSION = "1.0.44";
    private static final System.Logger LOGGER = System.getLogger("The Shattered Seals");

    public ShatteredSeals() {
        LOGGER.log(System.Logger.Level.INFO,
            "The Shattered Seals progression and GUI initialized ({0})", VERSION);
    }
}
