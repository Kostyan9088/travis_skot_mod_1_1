package com.example.astroworld;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

/** Держит единственный активный концерт на сервере и тикает его. */
public final class ConcertManager {
    /** 80 секунд (в диапазоне 60–90). Меняйте при желании. */
    public static final int DURATION_TICKS = 1600;

    private static ConcertSession active;

    private ConcertManager() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (active != null && !active.tick()) {
                active = null;
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (active != null) {
                try {
                    active.finish();
                } catch (Exception ignored) {
                    // сервер всё равно останавливается
                }
                active = null;
            }
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> active = null);
    }

    public static boolean start(ServerPlayerEntity host) {
        if (active != null) {
            return false;
        }
        active = new ConcertSession(host);
        active.begin();
        return true;
    }
}
