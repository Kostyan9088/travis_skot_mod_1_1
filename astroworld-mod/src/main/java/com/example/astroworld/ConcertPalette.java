package com.example.astroworld;

/** Неоновая палитра концерта: фиолетовый -> оранжевый -> огненно-красный -> маджента -> по кругу. */
public final class ConcertPalette {
    private static final float[][] COLORS = {
            {0.55f, 0.05f, 1.00f},
            {1.00f, 0.45f, 0.00f},
            {0.95f, 0.05f, 0.10f},
            {1.00f, 0.00f, 0.60f}
    };

    private ConcertPalette() {
    }

    /** phase = 1.0 соответствует переходу к следующему цвету. Возвращает {r, g, b} в диапазоне 0..1. */
    public static float[] at(float phase) {
        int n = COLORS.length;
        int base = (int) Math.floor(phase);
        float f = phase - base;
        f = f * f * (3.0f - 2.0f * f); // smoothstep
        int i = ((base % n) + n) % n;
        int j = (i + 1) % n;
        return new float[]{
                COLORS[i][0] + (COLORS[j][0] - COLORS[i][0]) * f,
                COLORS[i][1] + (COLORS[j][1] - COLORS[i][1]) * f,
                COLORS[i][2] + (COLORS[j][2] - COLORS[i][2]) * f
        };
    }
}
