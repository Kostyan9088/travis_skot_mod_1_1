package com.example.astroworld.mixin;

import com.example.astroworld.client.ConcertClientState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Меняет цвет неба и облаков на время концерта. */
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true, require = 0)
    private void astroworld$skyColor(CallbackInfoReturnable<Vec3d> cir) {
        if (ConcertClientState.isActive()) {
            float[] c = ConcertClientState.color(1.0f);
            cir.setReturnValue(new Vec3d(c[0], c[1], c[2]));
        }
    }

    @Inject(method = "getCloudsColor", at = @At("RETURN"), cancellable = true, require = 0)
    private void astroworld$cloudsColor(CallbackInfoReturnable<Vec3d> cir) {
        if (ConcertClientState.isActive()) {
            float[] c = ConcertClientState.color(0.85f);
            cir.setReturnValue(new Vec3d(c[0], c[1], c[2]));
        }
    }
}
