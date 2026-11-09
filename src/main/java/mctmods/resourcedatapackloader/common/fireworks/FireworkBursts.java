package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class FireworkBursts {
    private static final List<Pending> PENDING = new ArrayList<>();
    private static final RandomSource RANDOM = RandomSource.create();
    private static final float FOUNTAIN_SLOPE = 0.35F;
    private static final float SOUND_VOLUME = 4.0F;

    private FireworkBursts() {}

    public static void fire(ServerLevel level, double x, double y, double z, List<FireworkDef.Burst> bursts) {
        for (FireworkDef.Burst burst : bursts) {
            if (burst.delay() <= 0) {
                run(level, x, y, z, burst);
            } else {
                synchronized (PENDING) { PENDING.add(new Pending(level, x, y, z, burst, Math.min(burst.delay(), FireworkDef.MAX_DELAY))); }
            }
        }
    }

    public static void onTick(ServerTickEvent.Post event) {
        List<Pending> due = new ArrayList<>();
        synchronized (PENDING) {
            Iterator<Pending> it = PENDING.iterator();
            while (it.hasNext()) {
                Pending pending = it.next();
                if (event.getServer().getLevel(pending.level.dimension()) != pending.level) {
                    it.remove();
                } else if (--pending.left <= 0) {
                    it.remove();
                    due.add(pending);
                }
            }
        }
        for (Pending pending : due) { run(pending.level, pending.x, pending.y, pending.z, pending.burst); }
    }

    private static void run(ServerLevel level, double x, double y, double z, FireworkDef.Burst burst) {
        ContentLog.LOGGER.debug("FireworkBursts fired a {} burst at {} {} {} in dimension {}", burst.pattern(), x, y, z, level.dimension().location());
        playSound(level, x, y, z, burst.sound());
        Item item = burst.particle() == null ? null : BuiltInRegistries.ITEM.getOptional(burst.particle()).orElse(null);
        if (item == null) { return; }
        ParticleOptions option = FireworkDef.PATTERN_FOUNTAIN.equals(burst.pattern()) ? new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item)) : new FireworkParticleOption(FireworkParticles.BURST.get(), new ItemStack(item));
        if (FireworkDef.PATTERN_POINTS.equals(burst.pattern())) {
            for (float[] point : burst.points()) {
                if (point.length < 3) { continue; }
                spawn(level, x, y, z, burst, option, point[0], point[1], point[2]);
            }
            return;
        }
        for (int i = 0; i < burst.count(); i++) {
            double[] dir = direction(burst.pattern());
            spawn(level, x, y, z, burst, option, dir[0], dir[1], dir[2]);
        }
    }

    private static void spawn(ServerLevel level, double x, double y, double z, FireworkDef.Burst burst, ParticleOptions option, double dx, double dy, double dz) {
        double jitter = burst.spread();
        double vx = dx + jitter(jitter);
        double vy = dy + jitter(jitter);
        double vz = dz + jitter(jitter);
        for (ServerPlayer player : level.players()) {
            level.sendParticles(player, option, true, x, y, z, 0, vx, vy, vz, burst.speed());
        }
    }

    private static double jitter(double spread) {
        return spread <= 0 ? 0 : (RANDOM.nextDouble() * 2 - 1) * spread;
    }

    private static double[] direction(String pattern) {
        double angle = RANDOM.nextDouble() * Math.PI * 2;
        double cos = Mth.cos((float) angle);
        double sin = Mth.sin((float) angle);
        if (FireworkDef.PATTERN_RING.equals(pattern)) { return new double[] {cos, 0, sin}; }
        if (FireworkDef.PATTERN_FOUNTAIN.equals(pattern)) {
            double lean = RANDOM.nextDouble() * FOUNTAIN_SLOPE;
            return new double[] {cos * lean, 1, sin * lean};
        }
        double y = RANDOM.nextDouble() * 2 - 1;
        double r = Mth.sqrt((float) (1 - y * y));
        return new double[] {cos * r, y, sin * r};
    }

    private static void playSound(ServerLevel level, double x, double y, double z, @Nullable ResourceLocation id) {
        if (id == null) { return; }
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getOptional(id).orElse(null);
        if (sound == null) { return; }
        level.playSound(null, x, y, z, sound, SoundSource.AMBIENT, SOUND_VOLUME, 1.0F);
    }

    private static final class Pending {
        final ServerLevel level;
        final double x;
        final double y;
        final double z;
        final FireworkDef.Burst burst;
        int left;

        Pending(ServerLevel level, double x, double y, double z, FireworkDef.Burst burst, int left) {
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
            this.burst = burst;
            this.left = left;
        }
    }
}
