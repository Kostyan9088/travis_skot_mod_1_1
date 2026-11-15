package com.example.astroworld.client;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

/** АВТОГЕНЕРАЦИЯ: tools/generate_avatar.py. Не правьте вручную — правьте скрипт и запустите его заново. */
final class AvatarGeometry {
    static final int TEX_GRID = 128;
    static final String[] BRAID_NAMES = {
            "braid_back_0", "braid_back_1", "braid_back_2", "braid_back_3", "braid_back_4", "braid_back_5", "braid_back_6", "braid_side_r_0", "braid_side_r_1", "braid_side_r_2", "braid_side_l_0", "braid_side_l_1", "braid_side_l_2", "braid_front_r", "braid_front_l"
    };

    private AvatarGeometry() {
    }

    /** Добавляет дополнительные кубоиды как ДОЧЕРНИЕ части базовых — pivot-точки конечностей не меняются. */
    static void addExtras(ModelPartData head, ModelPartData body, ModelPartData rightArm, ModelPartData leftArm,
                          ModelPartData rightLeg, ModelPartData leftLeg) {
        head.addChild("braid_back_0", ModelPartBuilder.create().uv(99, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 8.0f, 1.0f, Dilation.NONE), ModelTransform.of(-3.3f, -4f, 4.4f, 0.1f, 0f, -0.045f));
        head.addChild("braid_back_1", ModelPartBuilder.create().uv(69, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 10.0f, 1.0f, Dilation.NONE), ModelTransform.of(-2.2f, -3.6f, 4.4f, 0.13f, 0f, -0.03f));
        head.addChild("braid_back_2", ModelPartBuilder.create().uv(79, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 9.0f, 1.0f, Dilation.NONE), ModelTransform.of(-1.1f, -4f, 4.4f, 0.16f, 0f, -0.015f));
        head.addChild("braid_back_3", ModelPartBuilder.create().uv(64, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 11.0f, 1.0f, Dilation.NONE), ModelTransform.of(0f, -3.6f, 4.4f, 0.1f, 0f, 0f));
        head.addChild("braid_back_4", ModelPartBuilder.create().uv(84, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 9.0f, 1.0f, Dilation.NONE), ModelTransform.of(1.1f, -4f, 4.4f, 0.13f, 0f, 0.015f));
        head.addChild("braid_back_5", ModelPartBuilder.create().uv(74, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 10.0f, 1.0f, Dilation.NONE), ModelTransform.of(2.2f, -3.6f, 4.4f, 0.16f, 0f, 0.03f));
        head.addChild("braid_back_6", ModelPartBuilder.create().uv(104, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 8.0f, 1.0f, Dilation.NONE), ModelTransform.of(3.3f, -4f, 4.4f, 0.1f, 0f, 0.045f));
        head.addChild("braid_side_r_0", ModelPartBuilder.create().uv(109, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 7.0f, 1.0f, Dilation.NONE), ModelTransform.of(-4.55f, -3.4f, -1.2f, 0f, 0f, 0.12f));
        head.addChild("braid_side_r_1", ModelPartBuilder.create().uv(89, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 9.0f, 1.0f, Dilation.NONE), ModelTransform.of(-4.55f, -3.1f, 0.6f, 0.04f, 0f, 0.12f));
        head.addChild("braid_side_r_2", ModelPartBuilder.create().uv(114, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 7.0f, 1.0f, Dilation.NONE), ModelTransform.of(-4.55f, -2.8f, 2.4f, 0.08f, 0f, 0.12f));
        head.addChild("braid_side_l_0", ModelPartBuilder.create().uv(119, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 7.0f, 1.0f, Dilation.NONE), ModelTransform.of(4.55f, -3.4f, -1.2f, 0f, 0f, -0.12f));
        head.addChild("braid_side_l_1", ModelPartBuilder.create().uv(94, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 9.0f, 1.0f, Dilation.NONE), ModelTransform.of(4.55f, -3.1f, 0.6f, 0.04f, 0f, -0.12f));
        head.addChild("braid_side_l_2", ModelPartBuilder.create().uv(124, 0).cuboid(-0.5f, 0f, -0.5f, 1.0f, 7.0f, 1.0f, Dilation.NONE), ModelTransform.of(4.55f, -2.8f, 2.4f, 0.08f, 0f, -0.12f));
        head.addChild("braid_front_r", ModelPartBuilder.create().uv(64, 13).cuboid(-0.5f, 0f, -0.5f, 1.0f, 6.0f, 1.0f, Dilation.NONE), ModelTransform.of(-3.9f, -4.4f, -4.55f, -0.05f, 0f, -0.04f));
        head.addChild("braid_front_l", ModelPartBuilder.create().uv(69, 13).cuboid(-0.5f, 0f, -0.5f, 1.0f, 6.0f, 1.0f, Dilation.NONE), ModelTransform.of(3.9f, -4.4f, -4.55f, -0.05f, 0f, 0.04f));
        head.addChild("stud_r", ModelPartBuilder.create().uv(124, 36).cuboid(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, Dilation.NONE), ModelTransform.of(-4.6f, -2.6f, 0.4f, 0f, 0f, 0f));
        head.addChild("stud_l", ModelPartBuilder.create().uv(124, 43).cuboid(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f, Dilation.NONE), ModelTransform.of(4.6f, -2.6f, 0.4f, 0f, 0f, 0f));
        body.addChild("hood", ModelPartBuilder.create().uv(74, 13).cuboid(-3.5f, -2f, 2.2f, 7.0f, 5.0f, 2.0f, new Dilation(0.3f)), ModelTransform.of(0f, 0f, 0f, -0.12f, 0f, 0f));
        body.addChild("hood_rim", ModelPartBuilder.create().uv(102, 21).cuboid(-3f, -0.4f, -2f, 6.0f, 2.0f, 4.0f, new Dilation(0.75f)), ModelTransform.NONE);
        body.addChild("cord_r", ModelPartBuilder.create().uv(123, 21).cuboid(-0.5f, 0f, -0.5f, 1.0f, 5.0f, 1.0f, Dilation.NONE), ModelTransform.of(-1.7f, 1.6f, -2.9f, 0f, 0f, 0f));
        body.addChild("cord_l", ModelPartBuilder.create().uv(115, 29).cuboid(-0.5f, 0f, -0.5f, 1.0f, 4.0f, 1.0f, Dilation.NONE), ModelTransform.of(1.7f, 1.6f, -2.9f, 0f, 0f, 0f));
        body.addChild("rig", ModelPartBuilder.create().uv(93, 13).cuboid(-3.5f, 1f, -3f, 7.0f, 6.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        body.addChild("pouch_r", ModelPartBuilder.create().uv(120, 29).cuboid(-3.4f, 4f, -4f, 2.0f, 3.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        body.addChild("pouch_l", ModelPartBuilder.create().uv(117, 36).cuboid(1.4f, 4f, -4f, 2.0f, 3.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        body.addChild("strap_r", ModelPartBuilder.create().uv(64, 29).cuboid(-3.5f, -0.9f, -2.5f, 1.0f, 1.0f, 5.0f, new Dilation(0.1f)), ModelTransform.NONE);
        body.addChild("strap_l", ModelPartBuilder.create().uv(77, 29).cuboid(2.5f, -0.9f, -2.5f, 1.0f, 1.0f, 5.0f, new Dilation(0.1f)), ModelTransform.NONE);
        body.addChild("pocket", ModelPartBuilder.create().uv(64, 62).cuboid(-3.5f, 7.5f, -3.2f, 7.0f, 3.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        body.addChild("hem", ModelPartBuilder.create().uv(90, 29).cuboid(-4f, 10f, -2f, 8.0f, 2.0f, 4.0f, new Dilation(0.6f)), ModelTransform.NONE);
        body.addChild("chain_a", ModelPartBuilder.create().uv(64, 67).cuboid(-3f, 0f, -3.2f, 6.0f, 1.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        body.addChild("chain_b", ModelPartBuilder.create().uv(116, 62).cuboid(-2f, 2f, -3.3f, 4.0f, 1.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        body.addChild("pendant", ModelPartBuilder.create().uv(117, 43).cuboid(-1f, 3.2f, -3.5f, 2.0f, 3.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        rightArm.addChild("cuff_r", ModelPartBuilder.create().uv(64, 36).cuboid(-3f, 6f, -2f, 4.0f, 2.0f, 4.0f, new Dilation(0.6f)), ModelTransform.NONE);
        leftArm.addChild("cuff_l", ModelPartBuilder.create().uv(81, 36).cuboid(-1f, 6f, -2f, 4.0f, 2.0f, 4.0f, new Dilation(0.6f)), ModelTransform.NONE);
        rightArm.addChild("bracelet_r", ModelPartBuilder.create().uv(83, 43).cuboid(-3f, 8f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.3f)), ModelTransform.NONE);
        leftArm.addChild("watch_band_l", ModelPartBuilder.create().uv(100, 43).cuboid(-1f, 8f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.3f)), ModelTransform.NONE);
        leftArm.addChild("watch_face_l", ModelPartBuilder.create().uv(81, 62).cuboid(3.3f, 8f, -1f, 1.0f, 1.0f, 2.0f, Dilation.NONE), ModelTransform.NONE);
        rightLeg.addChild("cargo_r", ModelPartBuilder.create().uv(110, 13).cuboid(-3.5f, 3f, -1.5f, 1.0f, 4.0f, 3.0f, Dilation.NONE), ModelTransform.NONE);
        rightLeg.addChild("cargo_flap_r", ModelPartBuilder.create().uv(115, 50).cuboid(-3.6f, 2.6f, -1.6f, 1.0f, 1.0f, 3.0f, new Dilation(0.1f)), ModelTransform.NONE);
        rightLeg.addChild("knee_r", ModelPartBuilder.create().uv(64, 50).cuboid(-2f, 5f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.7f)), ModelTransform.NONE);
        rightLeg.addChild("anklecuff_r", ModelPartBuilder.create().uv(81, 50).cuboid(-2f, 7.5f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.7f)), ModelTransform.NONE);
        rightLeg.addChild("shoe_collar_r", ModelPartBuilder.create().uv(98, 50).cuboid(-2f, 8.8f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.4f)), ModelTransform.NONE);
        rightLeg.addChild("shoe_upper_r", ModelPartBuilder.create().uv(64, 21).cuboid(-2f, 9.5f, -3f, 4.0f, 2.0f, 5.0f, new Dilation(0.3f)), ModelTransform.NONE);
        rightLeg.addChild("shoe_sole_r", ModelPartBuilder.create().uv(98, 36).cuboid(-2f, 11.4f, -3f, 4.0f, 1.0f, 5.0f, new Dilation(0.25f)), ModelTransform.NONE);
        rightLeg.addChild("shoe_tongue_r", ModelPartBuilder.create().uv(88, 62).cuboid(-1f, 0f, -0.5f, 2.0f, 2.0f, 1.0f, Dilation.NONE), ModelTransform.of(0f, 9.3f, -2.4f, -0.35f, 0f, 0f));
        rightLeg.addChild("shoe_heel_r", ModelPartBuilder.create().uv(95, 62).cuboid(-1f, 9f, 1.9f, 2.0f, 2.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
        leftLeg.addChild("cargo_l", ModelPartBuilder.create().uv(119, 13).cuboid(2.5f, 3f, -1.5f, 1.0f, 4.0f, 3.0f, Dilation.NONE), ModelTransform.NONE);
        leftLeg.addChild("cargo_flap_l", ModelPartBuilder.create().uv(115, 56).cuboid(2.6f, 2.6f, -1.6f, 1.0f, 1.0f, 3.0f, new Dilation(0.1f)), ModelTransform.NONE);
        leftLeg.addChild("knee_l", ModelPartBuilder.create().uv(64, 56).cuboid(-2f, 5f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.7f)), ModelTransform.NONE);
        leftLeg.addChild("anklecuff_l", ModelPartBuilder.create().uv(81, 56).cuboid(-2f, 7.5f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.7f)), ModelTransform.NONE);
        leftLeg.addChild("shoe_collar_l", ModelPartBuilder.create().uv(98, 56).cuboid(-2f, 8.8f, -2f, 4.0f, 1.0f, 4.0f, new Dilation(0.4f)), ModelTransform.NONE);
        leftLeg.addChild("shoe_upper_l", ModelPartBuilder.create().uv(83, 21).cuboid(-2f, 9.5f, -3f, 4.0f, 2.0f, 5.0f, new Dilation(0.3f)), ModelTransform.NONE);
        leftLeg.addChild("shoe_sole_l", ModelPartBuilder.create().uv(64, 43).cuboid(-2f, 11.4f, -3f, 4.0f, 1.0f, 5.0f, new Dilation(0.25f)), ModelTransform.NONE);
        leftLeg.addChild("shoe_tongue_l", ModelPartBuilder.create().uv(102, 62).cuboid(-1f, 0f, -0.5f, 2.0f, 2.0f, 1.0f, Dilation.NONE), ModelTransform.of(0f, 9.3f, -2.4f, -0.35f, 0f, 0f));
        leftLeg.addChild("shoe_heel_l", ModelPartBuilder.create().uv(109, 62).cuboid(-1f, 9f, 1.9f, 2.0f, 2.0f, 1.0f, Dilation.NONE), ModelTransform.NONE);
    }
}
