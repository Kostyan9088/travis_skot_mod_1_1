package com.example.astroworld.client;

import com.example.astroworld.AstroworldMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public final class AvatarModelLayers {
    /** Собственный слой модели аватара (HD-раскладка 128x128 логических единиц). */
    public static final EntityModelLayer AVATAR = new EntityModelLayer(AstroworldMod.id("cactus_avatar"), "main");

    private AvatarModelLayers() {
    }
}
