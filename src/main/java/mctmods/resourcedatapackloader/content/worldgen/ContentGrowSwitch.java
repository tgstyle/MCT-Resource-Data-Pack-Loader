package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.GrowSwitchDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentGrowSwitch {
    private static final List<String> KEYS = List.of("crops", "saplings", "animals", "breeding");
    static final float MOST = 100.0F;
    private static final Map<ServerLevel, Map<BlockPos, Pending>> PENDING = new IdentityHashMap<>();
    private static boolean draining;

    private record Pending(Block block, int extra) {}

    private ContentGrowSwitch() {}

    @Nullable static GrowSwitchDef parse(String owner, @Nullable JsonElement element) {
        if (element == null) { return null; }
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth that is not an object, ignoring it", owner);
            return null;
        }
        float[] scales = {1.0F, 1.0F, 1.0F, 1.0F};
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            int kind = KEYS.indexOf(entry.getKey());
            if (kind < 0) {
                ContentLog.LOGGER.error("{} gives growth.{}, which is not a growth key, ignoring it", owner, entry.getKey());
                continue;
            }
            float amount = ContentSpawnSwitch.amount(owner, "growth." + entry.getKey(), entry.getValue());
            if (amount > MOST) { ContentLog.LOGGER.error("{} gives growth.{} as {}, which is above {}, ignoring it", owner, entry.getKey(), amount, MOST); }
            else if (amount >= 0.0F) { scales[kind] = amount; }
        }
        return new GrowSwitchDef(scales);
    }

    private static int steps(ServerLevel level, BlockPos pos, int kind, RandomSource random) { return roll(ContentEnvironments.growth(level, pos).scale(kind), random); }

    static int roll(float scale, RandomSource random) {
        int whole = (int) scale;
        float fraction = scale - whole;
        return fraction <= 0.0F ? whole : whole + (random.nextFloat() < fraction ? 1 : 0);
    }

    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (draining || !(event.getLevel() instanceof ServerLevel level)) { return; }
        int steps = steps(level, event.getPos(), GrowSwitchDef.CROPS, level.getRandom());
        if (steps == 0) { event.setResult(Event.Result.DENY); }
        else if (steps > 1) {
            BlockPos at = event.getPos();
            Block block = event.getState().getBlock();
            if (!event.getState().isRandomlyTicking()) {
                BlockPos below = at.below();
                BlockPos above = at.above();
                if (level.getBlockState(below).getBlock() instanceof GrowingPlantHeadBlock head) { at = below; block = head; }
                else if (level.getBlockState(above).getBlock() instanceof GrowingPlantHeadBlock head) { at = above; block = head; }
                else { return; }
            }
            queue(level, at, block, steps - 1);
        }
    }

    private static void queue(ServerLevel level, BlockPos pos, Block block, int extra) { PENDING.computeIfAbsent(level, key -> new HashMap<>()).merge(pos.immutable(), new Pending(block, extra), ContentGrowSwitch::combine); }

    private static Pending combine(Pending held, Pending added) { return held.block() == added.block() ? new Pending(held.block(), held.extra() + added.extra()) : added; }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) { return; }
        Map<BlockPos, Pending> queued = PENDING.remove(level);
        if (queued == null) { return; }
        draining = true;
        try {
            for (Map.Entry<BlockPos, Pending> entry : queued.entrySet()) { drain(level, entry.getKey(), entry.getValue()); }
        }
        finally { draining = false; }
    }

    private static void drain(ServerLevel level, BlockPos pos, Pending pending) {
        if (!level.isLoaded(pos)) { return; }
        BlockPos at = locate(level, pos, pending.block());
        if (at == null) { return; }
        for (int done = 0; done < pending.extra(); done++) {
            BlockState now = level.getBlockState(at);
            if (!now.is(pending.block())) { return; }
            now.randomTick(level, at, level.getRandom());
        }
    }

    @Nullable private static BlockPos locate(ServerLevel level, BlockPos pos, Block block) {
        if (level.getBlockState(pos).is(block)) { return pos; }
        BlockPos below = pos.below();
        if (level.getBlockState(below).is(block)) { return below; }
        BlockPos above = pos.above();
        return level.getBlockState(above).is(block) ? above : null;
    }

    public static void age(LivingEntity entity) {
        if (!(entity instanceof Animal animal) || !(animal.level() instanceof ServerLevel level) || !animal.isAlive()) { return; }
        int age = animal.getAge();
        if (age == 0) { return; }
        int delta = steps(level, animal.blockPosition(), age < 0 ? GrowSwitchDef.ANIMALS : GrowSwitchDef.BREEDING, animal.getRandom()) - 1;
        int toward = age < 0 ? 1 : -1;
        if (delta < 0) { animal.setAge(age - toward); }
        else if (delta > 0) { animal.setAge(age + toward * Math.min(delta, Math.abs(age) - 1)); }
    }

    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (event.getParentA() instanceof Animal animal && animal.level() instanceof ServerLevel level && ContentEnvironments.growth(level, animal.blockPosition()).scale(GrowSwitchDef.BREEDING) == 0.0F) { event.setCanceled(true); }
    }

    public static boolean skips(ServerLevel level, BlockPos pos, Block block, RandomSource random) {
        if (draining) { return false; }
        int steps = steps(level, pos, GrowSwitchDef.SAPLINGS, random);
        if (steps > 1) { queue(level, pos, block, steps - 1); }
        return steps == 0;
    }

    public static void forget() {
        PENDING.clear();
        draining = false;
    }
}
