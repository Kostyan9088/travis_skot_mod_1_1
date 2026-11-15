package com.example.astroworld;

import net.minecraft.util.Identifier;

public final class ConcertPackets {
    /** сервер -> клиент: начать концерт (int: длительность в тиках) */
    public static final Identifier START = AstroworldMod.id("concert_start");
    /** сервер -> клиент: закончить концерт */
    public static final Identifier STOP = AstroworldMod.id("concert_stop");
    /** сервер -> клиент: вспышка на бит-дропе */
    public static final Identifier BEAT = AstroworldMod.id("concert_beat");

    private ConcertPackets() {
    }
}
