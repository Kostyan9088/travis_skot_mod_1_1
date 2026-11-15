package com.example.astroworld;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class AstroworldMod implements ModInitializer {
    public static final String MOD_ID = "astroworld";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModSounds.register();
        ModEntities.register();
        ModItems.register();
        ConcertManager.init();
    }
}
