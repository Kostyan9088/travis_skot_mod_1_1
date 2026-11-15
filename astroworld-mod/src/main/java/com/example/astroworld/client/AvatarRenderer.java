package com.example.astroworld.client;

import com.example.astroworld.AstroworldMod;
import com.example.astroworld.CactusAvatarEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Рисует аватар: x6 размер, пульсация в такт, HD-текстура 512x512 (логическая сетка 128x128).
 * Слой с глубиной (getEntityTranslucent) нужен, чтобы многослойная одежда и брейды не "просвечивали" друг сквозь друга;
 * свет принудительно максимальный — персонаж светится на сцене даже ночью.
 */
public class AvatarRenderer extends MobEntityRenderer<CactusAvatarEntity, AvatarModel> {
    private static final Identifier TEXTURE = AstroworldMod.id("textures/entity/cactus_avatar.png");
    private static final int FULL_BRIGHT = 0xF000F0;

    public AvatarRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new AvatarModel(ctx.getPart(AvatarModelLayers.AVATAR)), 0.0f);
    }

    @Override
    public Identifier getTexture(CactusAvatarEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(CactusAvatarEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, FULL_BRIGHT);
    }

    @Override
    protected void scale(CactusAvatarEntity entity, MatrixStack matrices, float amount) {
        float s = 6.0f + 0.25f * MathHelper.sin((entity.age + amount) * 0.35f);
        matrices.scale(s, s, s);
    }

    @Override
    protected RenderLayer getRenderLayer(CactusAvatarEntity entity, boolean showBody,
                                         boolean translucent, boolean showOutline) {
        return RenderLayer.getEntityTranslucent(TEXTURE);
    }
}
