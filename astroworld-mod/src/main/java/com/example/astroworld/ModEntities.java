package com.example.astroworld;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModEntities {
    public static final EntityType<CactusAvatarEntity> AVATAR = Registry.register(
            Registries.ENTITY_TYPE,
            AstroworldMod.id("cactus_avatar"),
            FabricEntityTypeBuilder.<CactusAvatarEntity>create(SpawnGroup.MISC, CactusAvatarEntity::new)
                    .dimensions(EntityDimensions.fixed(3.6f, 10.8f))
                    .build()
    );

    private ModEntities() {
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(AVATAR, MobEntity.createMobAttributes());
    }
}
