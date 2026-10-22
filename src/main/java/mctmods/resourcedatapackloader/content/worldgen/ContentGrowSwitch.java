package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.GrowSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentGrowSwitch {
    private static final List<String> KEYS = Arrays.asList("crops", "saplings", "animals", "breeding");
    static final float MOST = 100.0F;
    private static final Map<Integer, Map<BlockPos, Pending>> PENDING = new ConcurrentHashMap<>();
    private static boolean draining;

    private ContentGrowSwitch() {}

    private static final class Pending {
        final Block block;
        int extra;

        Pending(Block block, int extra) {
            this.block = block;
            this.extra = extra;
        }
    }

    @Nullable static GrowSwitchDef parse(String owner, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth that is not an object of crops, saplings, animals and breeding keys, ignoring it", owner);
            return null;
        }
        float[] scales = GrowSwitchDef.filled(1.0F);
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            int kind = KEYS.indexOf(entry.getKey());
            if (kind < 0) {
                ContentLog.LOGGER.error("{} gives growth.{}, which is not a growth key, ignoring it", owner, entry.getKey());
                continue;
            }
            Float value = ContentSpawnSwitch.number(owner, "growth." + entry.getKey(), entry.getValue(), false);
            if (value == null) { continue; }
            if (value > MOST) {
                ContentLog.LOGGER.error("{} gives growth.{} as {}, which is above {}, ignoring it", owner, entry.getKey(), value, (int) MOST);
                continue;
            }
            scales[kind] = value;
        }
        return new GrowSwitchDef(scales);
    }

    static int steps(World world, BlockPos pos, int kind, Random rand) { return roll(ContentEnvironments.growth(world, pos).scale(kind), rand); }

    static int roll(float scale, Random rand) {
        int whole = (int) scale;
        float fraction = scale - whole;
        return fraction <= 0.0F ? whole : whole + (rand.nextFloat() < fraction ? 1 : 0);
    }

    public static boolean skips(World world, BlockPos pos, Block block, Random rand) {
        if (draining || world.isRemote) { return false; }
        int steps = steps(world, pos, GrowSwitchDef.SAPLINGS, rand);
        if (steps > 1) { queue(world, pos, block, steps - 1); }
        return steps == 0;
    }

    private static void queue(World world, BlockPos pos, Block block, int extra) {
        PENDING.computeIfAbsent(world.provider.getDimension(), key -> new LinkedHashMap<>()).merge(pos.toImmutable(), new Pending(block, extra), (held, added) -> {
            if (held.block == added.block) {
                held.extra += added.extra;
                return held;
            }
            return added;
        });
    }

    @SubscribeEvent public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        World world = event.getWorld();
        if (draining || world.isRemote) { return; }
        BlockPos pos = event.getPos();
        int steps = steps(world, pos, GrowSwitchDef.CROPS, world.rand);
        if (steps == 0) { event.setResult(Event.Result.DENY); }
        else if (steps > 1) { queue(world, pos, event.getState().getBlock(), steps - 1); }
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (event.phase != TickEvent.Phase.END || world.isRemote || PENDING.isEmpty()) { return; }
        Map<BlockPos, Pending> queued = PENDING.remove(world.provider.getDimension());
        if (queued == null) { return; }
        draining = true;
        try {
            for (Map.Entry<BlockPos, Pending> entry : queued.entrySet()) { drain(world, entry.getKey(), entry.getValue()); }
        }
        finally { draining = false; }
    }

    private static void drain(World world, BlockPos pos, Pending pending) {
        if (!world.isBlockLoaded(pos)) { return; }
        BlockPos at = pos;
        if (world.getBlockState(at).getBlock() != pending.block) { at = pos.down(); }
        if (world.getBlockState(at).getBlock() != pending.block) { at = pos.up(); }
        for (int index = 0; index < pending.extra; index++) {
            IBlockState state = world.getBlockState(at);
            if (state.getBlock() != pending.block) { return; }
            pending.block.randomTick(world, at, state, world.rand);
        }
    }

    @SubscribeEvent public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!(living instanceof EntityAnimal) || living.world.isRemote || !living.isEntityAlive()) { return; }
        EntityAnimal animal = (EntityAnimal) living;
        int age = animal.getGrowingAge();
        if (age == 0) { return; }
        int toward = age < 0 ? 1 : -1;
        int delta = steps(living.world, animal.getPosition(), age < 0 ? GrowSwitchDef.ANIMALS : GrowSwitchDef.BREEDING, animal.getRNG()) - 1;
        if (delta < 0) { animal.setGrowingAge(age - toward); }
        else if (delta > 0) { animal.setGrowingAge(age + toward * Math.min(delta, Math.abs(age) - 1)); }
    }

    @SubscribeEvent public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getParentA() instanceof EntityAnimal)) { return; }
        EntityAnimal parent = (EntityAnimal) event.getParentA();
        if (ContentEnvironments.growth(parent.world, parent.getPosition()).scale(GrowSwitchDef.BREEDING) == 0.0F) { event.setCanceled(true); }
    }

    public static void serverStopped() {
        PENDING.clear();
        draining = false;
    }
}
