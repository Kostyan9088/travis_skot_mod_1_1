package com.example.astroworld;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Один запущенный концерт. Скрипт по тикам (20 тиков = 1 секунда):
 * бит-дропы на 15 / 30 / 45 / 60 / 75 секундах, хайп-надписи, аватар, пиротехника.
 */
public class ConcertSession {
    /** Тики бит-дропов. Хотите другие тайминги под свою аудиозапись — меняйте здесь. */
    private static final int[] DROPS = {300, 600, 900, 1200, 1500};
    private static final String[] DROP_TITLES = {"SICKO MODE", "IT'S LIT!", "STRAIGHT UP!", "ALRIGHT!", "ASTRONOMICAL!"};
    private static final Formatting[] BAR_COLORS = {
            Formatting.LIGHT_PURPLE, Formatting.GOLD, Formatting.RED, Formatting.YELLOW
    };

    private final MinecraftServer server;
    private final ServerWorld world;
    private final Vec3d center;
    private final Set<UUID> audience = new LinkedHashSet<>();
    private final boolean controlTime;
    private final long originalTime;
    private final boolean daylightCycle;
    private final Random random = new Random();
    /** ударные волны: {x, y, z, возраст, тип} */
    private final List<double[]> waves = new ArrayList<>();

    private CactusAvatarEntity avatar;
    private Vec3d avatarPos = Vec3d.ZERO;
    private double slotAngle;
    private int tick = 0;

    public ConcertSession(ServerPlayerEntity host) {
        this.world = host.getServerWorld();
        this.server = world.getServer();
        this.center = host.getPos();
        this.controlTime = world.getRegistryKey() == World.OVERWORLD;
        this.originalTime = world.getTimeOfDay();
        this.daylightCycle = world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE);

        // Аватар появляется там, куда смотрит игрок
        double yawRad = Math.toRadians(host.getYaw());
        this.slotAngle = Math.atan2(Math.cos(yawRad), -Math.sin(yawRad));

        for (ServerPlayerEntity p : world.getPlayers()) {
            if (p.squaredDistanceTo(center) <= 128.0 * 128.0) {
                audience.add(p.getUuid());
            }
        }
    }

    // ------------------------------------------------------------------ старт / финиш

    public void begin() {
        avatar = ModEntities.AVATAR.create(world);
        avatarPos = targetPos();
        if (avatar != null) {
            placeAvatar();
            world.spawnEntity(avatar);
        }

        for (UUID id : audience) {
            ServerPlayerEntity p = server.getPlayerManager().getPlayer(id);
            if (p != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(ConcertManager.DURATION_TICKS);
                ServerPlayNetworking.send(p, ConcertPackets.START, buf);
            }
        }

        if (controlTime) {
            world.setTimeOfDay(18000L); // полночь
        }

        waves.add(new double[]{center.x, center.y + 1.0, center.z, 0, 0});
        waves.add(new double[]{avatarPos.x, avatarPos.y + 5.0, avatarPos.z, 0, 1});
    }

    public void finish() {
        List<ServerPlayerEntity> v = viewers();
        for (ServerPlayerEntity p : v) {
            sendStop(p);
        }
        if (avatar != null && !avatar.isRemoved()) {
            spawn(v, ParticleTypes.FIREWORK, avatarPos.x, avatarPos.y + 5.0, avatarPos.z, 250, 0, 0, 0, 0.9);
            spawn(v, ParticleTypes.EXPLOSION_EMITTER, avatarPos.x, avatarPos.y + 5.0, avatarPos.z, 1, 0, 0, 0, 0);
            avatar.discard();
        }
        if (controlTime) {
            world.setTimeOfDay(originalTime + (daylightCycle ? tick : 0));
        }
        title(v, "THANK YOU!", "ASTROWORLD", Formatting.GOLD);
    }

    private void sendStop(ServerPlayerEntity p) {
        ServerPlayNetworking.send(p, ConcertPackets.STOP, PacketByteBufs.create());
    }

    // ------------------------------------------------------------------ основной тик

    /** @return false, когда концерт закончился */
    public boolean tick() {
        tick++;
        List<ServerPlayerEntity> v = viewers();
        if (v.isEmpty() || tick >= ConcertManager.DURATION_TICKS) {
            finish();
            return false;
        }

        if (controlTime) {
            world.setTimeOfDay(18000L); // держим полночь
        }

        updateAvatar(v);
        ambientEffects(v);
        updateWaves(v);
        script(v);
        return true;
    }

    private List<ServerPlayerEntity> viewers() {
        List<ServerPlayerEntity> list = new ArrayList<>();
        Iterator<UUID> it = audience.iterator();
        while (it.hasNext()) {
            UUID id = it.next();
            ServerPlayerEntity p = server.getPlayerManager().getPlayer(id);
            if (p == null) {
                it.remove();
            } else if (p.getServerWorld() != world) {
                sendStop(p);
                it.remove();
            } else {
                list.add(p);
            }
        }
        return list;
    }

    // ------------------------------------------------------------------ аватар

    private Vec3d targetPos() {
        double dist = 46.0;
        double x = center.x + Math.cos(slotAngle) * dist;
        double z = center.z + Math.sin(slotAngle) * dist;
        double y = center.y + 16.0 + Math.sin(tick * 0.07) * 2.5; // покачивание
        return new Vec3d(x, y, z);
    }

    private void placeAvatar() {
        double dx = center.x - avatarPos.x;
        double dz = center.z - avatarPos.z;
        float yaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(-dx, dz)));
        avatar.refreshPositionAndAngles(avatarPos.x, avatarPos.y, avatarPos.z, yaw, 0.0f);
        avatar.setHeadYaw(yaw);
        avatar.setBodyYaw(yaw);
    }

    private void updateAvatar(List<ServerPlayerEntity> v) {
        if (avatar == null || avatar.isRemoved()) {
            return;
        }
        Vec3d target = targetPos();
        Vec3d next = avatarPos.add(target.subtract(avatarPos).multiply(0.05));
        double speed = next.distanceTo(avatarPos);
        avatarPos = next;
        placeAvatar();

        // шлейф при "телепорте"
        if (speed > 0.35) {
            for (int i = 0; i < 4; i++) {
                spawn(v, ParticleTypes.DRAGON_BREATH, avatarPos.x, avatarPos.y + i * 3.0, avatarPos.z,
                        6, 1.2, 1.0, 1.2, 0.02);
            }
            spawn(v, ParticleTypes.END_ROD, avatarPos.x, avatarPos.y + 5.0, avatarPos.z, 5, 1.5, 4.0, 1.5, 0.05);
        }
    }

    // ------------------------------------------------------------------ эффекты

    private void ambientEffects(List<ServerPlayerEntity> v) {
        ParticleEffect[] types = {
                ParticleTypes.FLAME, ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.DRAGON_BREATH,
                ParticleTypes.END_ROD, ParticleTypes.FIREWORK
        };

        // огромные вращающиеся кольца в небе
        if (tick % 4 == 0) {
            ParticleEffect type = types[(tick / 4) % types.length];
            double r = 24.0 + 10.0 * Math.sin(tick * 0.05);
            ring(v, type, center.x, center.y + 28.0, center.z, r, 28, tick * 0.02);
        }

        // спираль-башня из END_ROD вокруг игрока
        if (tick % 2 == 0) {
            for (int k = 0; k < 14; k++) {
                double a = tick * 0.15 + k * 0.6;
                spawn(v, ParticleTypes.END_ROD,
                        center.x + Math.cos(a) * 7.0, center.y + k * 3.2, center.z + Math.sin(a) * 7.0,
                        1, 0, 0, 0, 0);
            }
        }

        // огненные фонтаны вокруг сцены
        if (tick % 3 == 0) {
            for (int k = 0; k < 8; k++) {
                double a = Math.PI * 2.0 * k / 8.0 + tick * 0.01;
                double x = center.x + Math.cos(a) * 12.0;
                double z = center.z + Math.sin(a) * 12.0;
                ParticleEffect fire = (k % 2 == 0) ? ParticleTypes.FLAME : ParticleTypes.SOUL_FIRE_FLAME;
                shoot(v, fire, x, center.y, z, 0.0, 0.7, 0.0);
            }
        }

        // фейерверки около аватара
        if (tick % 10 == 0) {
            double x = avatarPos.x + (random.nextDouble() - 0.5) * 30.0;
            double y = avatarPos.y + 6.0 + random.nextDouble() * 14.0;
            double z = avatarPos.z + (random.nextDouble() - 0.5) * 30.0;
            spawn(v, ParticleTypes.FIREWORK, x, y, z, 60, 0, 0, 0, 0.3);
        }

        // неоновая аура аватара
        if (tick % 2 == 0) {
            float[] c = ConcertPalette.at(tick / 50.0f);
            DustParticleEffect dust = new DustParticleEffect(new Vector3f(c[0], c[1], c[2]), 3.0f);
            spawn(v, dust, avatarPos.x, avatarPos.y + 5.5, avatarPos.z, 10, 1.8, 5.0, 1.8, 0.0);
            spawn(v, ParticleTypes.FLAME, avatarPos.x, avatarPos.y + 11.0, avatarPos.z, 4, 1.5, 0.3, 1.5, 0.05);
        }
    }

    private void updateWaves(List<ServerPlayerEntity> v) {
        Iterator<double[]> it = waves.iterator();
        while (it.hasNext()) {
            double[] w = it.next();
            w[3] += 1;
            if (w[3] > 28) {
                it.remove();
                continue;
            }
            double radius = w[3] * 2.5;
            int points = Math.min(56, 16 + (int) radius);
            ParticleEffect type = (w[4] == 0) ? ParticleTypes.FLAME : ParticleTypes.SOUL_FIRE_FLAME;
            ring(v, type, w[0], w[1], w[2], radius, points, 0.0);
            if (((int) w[3]) % 2 == 0) {
                ring(v, ParticleTypes.END_ROD, w[0], w[1] + 0.5, w[2], radius * 0.93, points / 2, 0.3);
            }
        }
    }

    // ------------------------------------------------------------------ сценарий

    private void script(List<ServerPlayerEntity> v) {
        switch (tick) {
            case 1 -> title(v, "ASTROWORLD", "WELCOME TO THE FESTIVAL", Formatting.GOLD);
            case 60 -> title(v, "IT'S LIT!", "", Formatting.RED);
            case 140 -> title(v, "STRAIGHT UP!", "", Formatting.LIGHT_PURPLE);
            case 220 -> title(v, "YEAH!", "", Formatting.AQUA);
            case 270 -> title(v, "GET READY...", "BEAT DROP INCOMING", Formatting.YELLOW);
            case 380 -> title(v, "LET'S GO!", "", Formatting.GOLD);
            case 480 -> title(v, "MAKE SOME NOISE!", "", Formatting.RED);
            case 700 -> title(v, "FEEL THE BASS!", "", Formatting.LIGHT_PURPLE);
            case 800 -> title(v, "JUMP!", "", Formatting.YELLOW);
            case 1000 -> title(v, "TURN IT UP!", "", Formatting.RED);
            case 1100 -> title(v, "ALRIGHT, ALRIGHT!", "", Formatting.GOLD);
            case 1300 -> title(v, "HANDS UP!", "", Formatting.AQUA);
            case 1400 -> title(v, "ONE MORE TIME!", "", Formatting.LIGHT_PURPLE);
            case 1560 -> title(v, "THANK YOU, ASTROWORLD!", "", Formatting.GOLD);
            default -> {
            }
        }

        // экшен-бар каждые 2 секунды
        if (tick % 40 == 0) {
            Formatting color = BAR_COLORS[(tick / 40) % BAR_COLORS.length];
            for (ServerPlayerEntity p : v) {
                p.sendMessage(Text.literal(">> ASTROWORLD LIVE  -  CACTUS JACK <<").formatted(color, Formatting.BOLD), true);
            }
        }

        for (int i = 0; i < DROPS.length; i++) {
            if (tick == DROPS[i]) {
                drop(v, i);
            }
        }
    }

    private void drop(List<ServerPlayerEntity> v, int index) {
        title(v, DROP_TITLES[index % DROP_TITLES.length], "BEAT DROP #" + (index + 1), Formatting.GOLD);

        // аватар "телепортируется" плавно в другой сектор неба
        slotAngle += Math.PI * 2.0 / 3.0 + 0.4;

        // молнии без огня и урона
        for (int i = 0; i < 4; i++) {
            double a = random.nextDouble() * Math.PI * 2.0;
            double r = 18.0 + random.nextDouble() * 24.0;
            double x = center.x + Math.cos(a) * r;
            double z = center.z + Math.sin(a) * r;
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING, MathHelper.floor(x), MathHelper.floor(z));
            LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
            if (bolt != null) {
                bolt.setPosition(x, y, z);
                bolt.setCosmetic(true);
                world.spawnEntity(bolt);
            }
        }

        // ударные волны (на земле и в небе у аватара)
        waves.add(new double[]{center.x, center.y + 1.0, center.z, 0, 0});
        waves.add(new double[]{avatarPos.x, avatarPos.y + 5.0, avatarPos.z, 0, 1});

        // большие вспышки
        spawn(v, ParticleTypes.EXPLOSION_EMITTER, center.x, center.y + 20.0, center.z, 1, 0, 0, 0, 0);
        spawn(v, ParticleTypes.FLASH, avatarPos.x, avatarPos.y + 6.0, avatarPos.z, 1, 0, 0, 0, 0);
        spawn(v, ParticleTypes.FIREWORK, avatarPos.x, avatarPos.y + 6.0, avatarPos.z, 220, 0, 0, 0, 0.9);

        // левитация на 3 секунды + плавное падение, чтобы не получить урон
        for (ServerPlayerEntity p : v) {
            if (p.squaredDistanceTo(center) <= 48.0 * 48.0) {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 60, 0, false, false, true));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200, 0, false, false, true));
            }
            ServerPlayNetworking.send(p, ConcertPackets.BEAT, PacketByteBufs.create());
        }

        world.playSound(null, center.x, center.y, center.z,
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 3.0f, 0.6f);
    }

    // ------------------------------------------------------------------ помощники

    private void title(List<ServerPlayerEntity> v, String main, String sub, Formatting color) {
        for (ServerPlayerEntity p : v) {
            p.networkHandler.sendPacket(new TitleFadeS2CPacket(2, 30, 10));
            p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal(sub).formatted(Formatting.GOLD)));
            p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal(main).formatted(color, Formatting.BOLD)));
        }
    }

    /** Частицы, видимые на большом расстоянии (force = true). */
    private void spawn(List<ServerPlayerEntity> v, ParticleEffect type,
                       double x, double y, double z, int count,
                       double dx, double dy, double dz, double speed) {
        for (ServerPlayerEntity p : v) {
            world.spawnParticles(p, type, true, x, y, z, count, dx, dy, dz, speed);
        }
    }

    /** Одна частица с заданной скоростью (count = 0 -> смещения трактуются как скорость). */
    private void shoot(List<ServerPlayerEntity> v, ParticleEffect type,
                       double x, double y, double z, double vx, double vy, double vz) {
        for (ServerPlayerEntity p : v) {
            world.spawnParticles(p, type, true, x, y, z, 0, vx, vy, vz, 1.0);
        }
    }

    private void ring(List<ServerPlayerEntity> v, ParticleEffect type,
                      double cx, double cy, double cz, double radius, int points, double phase) {
        for (int i = 0; i < points; i++) {
            double a = phase + (Math.PI * 2.0 * i) / points;
            spawn(v, type, cx + Math.cos(a) * radius, cy, cz + Math.sin(a) * radius, 1, 0, 0, 0, 0);
        }
    }
}
