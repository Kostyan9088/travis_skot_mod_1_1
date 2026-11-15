package com.example.astroworld;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/**
 * Гигантский "аватар" на сцене. Невидимый для логики мира: не получает урон, не сохраняется,
 * не толкается. Вся красота (масштаб x6, полупрозрачность, свечение, анимация рук) рисуется на клиенте.
 */
public class CactusAvatarEntity extends MobEntity {

    public CactusAvatarEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        this.noClip = true;
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setSilent(true);
        this.setPersistent();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public void checkDespawn() {
        // никогда не исчезает сам
    }
}
