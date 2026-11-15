package com.example.astroworld.mixin;

import com.example.astroworld.client.ConcertClientState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Красит туман/горизонт на время концерта. require = 0: если метод не найден — мод всё равно загрузится. */
@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private static void astroworld$clearColor(CallbackInfo ci) {
        if (ConcertClientState.isActive()) {
            float[] c = ConcertClientState.color(0.6f);
            RenderSystem.clearColor(c[0], c[1], c[2], 0.0f);
        }
    }

    @Inject(method = "applyFogColor", at = @At("TAIL"), require = 0)
    private static void astroworld$fogColor(CallbackInfo ci) {
        if (ConcertClientState.isActive()) {
            float[] c = ConcertClientState.color(0.6f);
            RenderSystem.setShaderFogColor(c[0], c[1], c[2]);
        }
    }
}
