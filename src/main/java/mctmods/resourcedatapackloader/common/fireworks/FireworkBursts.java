package mctmods.resourcedatapackloader.common.fireworks;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class FireworkBursts {
    private static final List<Pending> PENDING = new ArrayList<>();
    private static final Random RANDOM = new Random();
    private static final float FOUNTAIN_SLOPE = 0.35F;

    private FireworkBursts() {}

    public static void fire(WorldServer world, double x, double y, double z, List<FireworkDef.Burst> bursts) {
        for (FireworkDef.Burst burst : bursts) {
            if (burst.delay <= 0) {
                run(world, x, y, z, burst);
            } else {
                synchronized (PENDING) { PENDING.add(new Pending(world, x, y, z, burst, Math.min(burst.delay, FireworkDef.MAX_DELAY))); }
            }
        }
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        List<Pending> due = new ArrayList<>();
        synchronized (PENDING) {
            Iterator<Pending> it = PENDING.iterator();
            while (it.hasNext()) {
                Pending pending = it.next();
                if (DimensionManager.getWorld(pending.world.provider.getDimension()) != pending.world) {
                    it.remove();
                } else if (--pending.left <= 0) {
                    it.remove();
                    due.add(pending);
                }
            }
        }
        for (Pending pending : due) { run(pending.world, pending.x, pending.y, pending.z, pending.burst); }
    }

    private static void run(WorldServer world, double x, double y, double z, FireworkDef.Burst burst) {
        ContentLog.LOGGER.debug("FireworkBursts fired a {} burst at {} {} {} in dimension {}", burst.pattern, x, y, z, world.provider.getDimension());
        playSound(world, x, y, z, burst.sound);
        Item item = Registries.find(ForgeRegistries.ITEMS, burst.particle);
        if (item == null) { return; }
        List<Float> velocities = new ArrayList<>();
        if (FireworkDef.PATTERN_POINTS.equals(burst.pattern)) {
            for (float[] point : burst.points) {
                if (point.length < 3) { continue; }
                spawn(velocities, burst, point[0], point[1], point[2]);
            }
        } else {
            for (int i = 0; i < burst.count; i++) {
                double[] dir = direction(burst.pattern);
                spawn(velocities, burst, dir[0], dir[1], dir[2]);
            }
        }
        float[] flat = new float[velocities.size()];
        for (int i = 0; i < flat.length; i++) { flat[i] = velocities.get(i); }
        if (FireworkDef.PATTERN_FOUNTAIN.equals(burst.pattern)) {
            spawnFountain(world, x, y, z, Item.getIdFromItem(item), flat);
        } else {
            RDPLNetwork.sendFireworkBurst(world, x, y, z, Item.getIdFromItem(item), flat);
        }
    }

    private static void spawnFountain(WorldServer world, double x, double y, double z, int itemId, float[] velocities) {
        for (int i = 0; i + 2 < velocities.length; i += 3) {
            world.spawnParticle(EnumParticleTypes.ITEM_CRACK, x, y, z, 0, velocities[i], velocities[i + 1], velocities[i + 2], 1.0D, itemId, 0);
        }
    }

    private static void spawn(List<Float> velocities, FireworkDef.Burst burst, double dx, double dy, double dz) {
        double jitter = burst.spread;
        velocities.add((float) ((dx + jitter(jitter)) * burst.speed));
        velocities.add((float) ((dy + jitter(jitter)) * burst.speed));
        velocities.add((float) ((dz + jitter(jitter)) * burst.speed));
    }

    private static double jitter(double spread) {
        return spread <= 0 ? 0 : (RANDOM.nextDouble() * 2 - 1) * spread;
    }

    private static double[] direction(String pattern) {
        double angle = RANDOM.nextDouble() * Math.PI * 2;
        double cos = MathHelper.cos((float) angle);
        double sin = MathHelper.sin((float) angle);
        if (FireworkDef.PATTERN_RING.equals(pattern)) { return new double[] {cos, 0, sin}; }
        if (FireworkDef.PATTERN_FOUNTAIN.equals(pattern)) {
            double lean = RANDOM.nextDouble() * FOUNTAIN_SLOPE;
            return new double[] {cos * lean, 1, sin * lean};
        }
        double y = RANDOM.nextDouble() * 2 - 1;
        double r = MathHelper.sqrt(1 - y * y);
        return new double[] {cos * r, y, sin * r};
    }

    private static void playSound(WorldServer world, double x, double y, double z, ResourceLocation id) {
        SoundEvent sound = Registries.find(ForgeRegistries.SOUND_EVENTS, id);
        if (sound == null) { return; }
        world.playSound(null, x, y, z, sound, SoundCategory.AMBIENT, 4.0F, 1.0F);
    }

    private static final class Pending {
        final WorldServer world;
        final double x;
        final double y;
        final double z;
        final FireworkDef.Burst burst;
        int left;

        Pending(WorldServer world, double x, double y, double z, FireworkDef.Burst burst, int left) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.burst = burst;
            this.left = left;
        }
    }
}
