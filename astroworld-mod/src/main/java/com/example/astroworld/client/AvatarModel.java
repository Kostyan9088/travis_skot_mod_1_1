package com.example.astroworld.client;

import com.example.astroworld.CactusAvatarEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.math.MathHelper;

/**
 * Модель персонажа. Основа — тот же скелет, что у игрока/зомби (head, hat, body, right_arm, left_arm,
 * right_leg, left_leg) с ТЕМИ ЖЕ pivot-точками, поэтому любые анимации BipedEntityModel работают как раньше.
 * Всё новое (брейды, худи, жилет, цепи, браслеты, кроссовки) — ДОЧЕРНИЕ части этого скелета
 * (см. автогенерируемый AvatarGeometry) и поворачивается вместе с родителем.
 *
 * Слои одежды (как в скине игрока): hat = волосы, jacket = худи, *_sleeve = рукава, *_pants = штаны.
 */
public class AvatarModel extends BipedEntityModel<CactusAvatarEntity> {
    private final ModelPart[] braids;
    private final float[] braidBasePitch;
    private final float[] braidBaseRoll;

    public AvatarModel(ModelPart root) {
        super(root);
        ModelPart headPart = root.getChild("head");
        String[] names = AvatarGeometry.BRAID_NAMES;
        this.braids = new ModelPart[names.length];
        this.braidBasePitch = new float[names.length];
        this.braidBaseRoll = new float[names.length];
        for (int i = 0; i < names.length; i++) {
            this.braids[i] = headPart.getChild(names[i]);
            this.braidBasePitch[i] = this.braids[i].pitch;
            this.braidBaseRoll[i] = this.braids[i].roll;
        }
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        // --- голова + слой волос (pivot 0,0,0 как в ванили) ---
        ModelPartData head = root.addChild("head",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        root.addChild("hat",
                ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.5f)),
                ModelTransform.NONE);

        // --- туловище + худи ---
        ModelPartData body = root.addChild("body",
                ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f), ModelTransform.NONE);
        body.addChild("jacket",
                ModelPartBuilder.create().uv(16, 32).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, new Dilation(0.5f)),
                ModelTransform.NONE);

        // --- руки (pivot ±5, 2, 0) + рукава ---
        ModelPartData rightArm = root.addChild("right_arm",
                ModelPartBuilder.create().uv(40, 16).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(-5.0f, 2.0f, 0.0f));
        rightArm.addChild("right_sleeve",
                ModelPartBuilder.create().uv(40, 32).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(0.5f)),
                ModelTransform.NONE);
        ModelPartData leftArm = root.addChild("left_arm",
                ModelPartBuilder.create().uv(32, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(5.0f, 2.0f, 0.0f));
        leftArm.addChild("left_sleeve",
                ModelPartBuilder.create().uv(48, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(0.5f)),
                ModelTransform.NONE);

        // --- ноги (pivot ±1.9, 12, 0) + штаны ---
        ModelPartData rightLeg = root.addChild("right_leg",
                ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(-1.9f, 12.0f, 0.0f));
        rightLeg.addChild("right_pants",
                ModelPartBuilder.create().uv(0, 32).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(0.5f)),
                ModelTransform.NONE);
        ModelPartData leftLeg = root.addChild("left_leg",
                ModelPartBuilder.create().uv(16, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(1.9f, 12.0f, 0.0f));
        leftLeg.addChild("left_pants",
                ModelPartBuilder.create().uv(0, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, new Dilation(0.5f)),
                ModelTransform.NONE);

        // --- брейды, жилет, цепи, браслеты, кроссовки ---
        AvatarGeometry.addExtras(head, body, rightArm, leftArm, rightLeg, leftLeg);

        return TexturedModelData.of(data, AvatarGeometry.TEX_GRID, AvatarGeometry.TEX_GRID);
    }

    @Override
    public void setAngles(CactusAvatarEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

        float t = animationProgress * 0.28f;
        float wave = MathHelper.sin(t);

        // руки вверх и машут в такт
        this.rightArm.pitch = -2.7f + wave * 0.45f;
        this.leftArm.pitch = -2.7f - wave * 0.45f;
        this.rightArm.roll = 0.30f + MathHelper.cos(t) * 0.25f;
        this.leftArm.roll = -0.30f - MathHelper.cos(t) * 0.25f;

        // ноги пританцовывают
        this.rightLeg.pitch = wave * 0.55f;
        this.leftLeg.pitch = -wave * 0.55f;

        // кивок головой в бит
        this.head.pitch = -0.15f + MathHelper.sin(t * 2.0f) * 0.12f;
        this.hat.copyTransform(this.head);

        // брейды покачиваются (каждая со своей фазой)
        for (int i = 0; i < this.braids.length; i++) {
            float phase = i * 0.7f;
            this.braids[i].pitch = this.braidBasePitch[i] + MathHelper.sin(t * 2.0f + phase) * 0.10f;
            this.braids[i].roll = this.braidBaseRoll[i] + MathHelper.cos(t + phase) * 0.05f;
        }
    }
}
