package com.example.astroworld.client;

import com.example.astroworld.ConcertPalette;
import com.example.astroworld.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;

/** Клиентское состояние концерта: цвет неба/тумана, вспышки, музыка. */
public final class ConcertClientState {
    private static boolean active;
    private static int tick;
    private static int duration;
    private static float flash;
    private static PositionedSoundInstance music;

    private ConcertClientState() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void start(MinecraftClient client, int durationTicks) {
        stop(client);
        active = true;
        tick = 0;
        flash = 0.0f;
        duration = durationTicks;
        music = PositionedSoundInstance.master(ModSounds.SICKO_MODE, 1.0f, 1.0f);
        client.getSoundManager().play(music);
    }

    public static void stop(MinecraftClient client) {
        if (music != null && client != null) {
            client.getSoundManager().stop(music);
        }
        music = null;
        active = false;
        flash = 0.0f;
        tick = 0;
    }

    public static void beat() {
        flash = 1.0f;
    }

    public static void tick(MinecraftClient client) {
        if (!active) {
            return;
        }
        tick++;
        flash = Math.max(0.0f, flash - 0.05f);
        if (tick > duration + 100) { // страховка, если пакет STOP потерялся
            stop(client);
        }
    }

    /** Цвет {r,g,b} для неба/тумана/облаков с учётом вспышки на бит-дропе. */
    public static float[] color(float brightness) {
        float[] c = ConcertPalette.at(tick / 50.0f);
        float f = flash * 0.85f;
        float r = Math.min(1.0f, (c[0] + (1.0f - c[0]) * f) * brightness);
        float g = Math.min(1.0f, (c[1] + (1.0f - c[1]) * f) * brightness);
        float b = Math.min(1.0f, (c[2] + (1.0f - c[2]) * f) * brightness);
        return new float[]{r, g, b};
    }
}
