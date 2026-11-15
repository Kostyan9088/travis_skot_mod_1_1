package com.example.astroworld;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public final class ModSounds {
    public static final SoundEvent SICKO_MODE = SoundEvent.of(AstroworldMod.id("sicko_mode"));

    private ModSounds() {
    }

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, AstroworldMod.id("sicko_mode"), SICKO_MODE);
    }
}
