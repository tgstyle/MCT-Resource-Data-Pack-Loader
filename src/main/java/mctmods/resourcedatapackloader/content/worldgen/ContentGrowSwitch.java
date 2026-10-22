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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentGrowSwitch {
    private static final List<String> KEYS = List.of("crops", "saplings", "animals", "breeding");
    static final double MOST = 100.0D;
    private static final Map<Spot, Pending> PENDING = new HashMap<>();
    private static boolean draining;

    private record Pending(Block block, int extra) {}

    private record Spot(ServerLevel level, BlockPos pos) {
        boolean in(ServerLevel other) { return level == other; }
    }

    private ContentGrowSwitch() {}

    @Nullable static GrowSwitchDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth that is not an object, ignoring it", owner);
            return null;
        }
        GrowSwitchDef def = new GrowSwitchDef();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            int kind = KEYS.indexOf(entry.getKey());
            if (kind < 0) {
                ContentLog.LOGGER.error("{} gives growth.{}, which is not a growth key, ignoring it", owner, entry.getKey());
                continue;
            }
            Double value = ContentSpawnSwitch.number(entry.getValue());
            if (value == null || value < 0.0D || value > MOST) { ContentLog.LOGGER.error("{} gives growth.{} that is not a number from 0 to 100, ignoring it", owner, entry.getKey()); }
            else { def.scales[kind] = value.floatValue(); }
        }
        return def;
    }

    static int roll(float scale, RandomSource random) {
        int whole = (int) scale;
        float fraction = scale - whole;
        return fraction > 0.0F && random.nextFloat() < fraction ? whole + 1 : whole;
    }

    private static int steps(ServerLevel level, BlockPos pos, int kind, RandomSource random) { return roll(ContentEnvironments.growth(level, pos).scale(kind), random); }

    public static void onCropGrow(CropGrowEvent.Pre event) {
        if (draining || !(event.getLevel() instanceof ServerLevel level)) { return; }
        int steps = steps(level, event.getPos(), GrowSwitchDef.CROPS, level.getRandom());
        if (steps == 0) { event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW); }
        else if (steps > 1) { queue(level, event.getPos().immutable(), new Pending(event.getState().getBlock(), steps - 1)); }
    }

    private static void queue(ServerLevel level, BlockPos pos, Pending added) {
        PENDING.merge(new Spot(level, pos), added, (now, more) -> now.block() == more.block() ? new Pending(now.block(), now.extra() + more.extra()) : more);
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || PENDING.isEmpty()) { return; }
        Map<Spot, Pending> held = new HashMap<>();
        for (Iterator<Map.Entry<Spot, Pending>> it = PENDING.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Spot, Pending> entry = it.next();
            if (entry.getKey().in(level)) {
                held.put(entry.getKey(), entry.getValue());
                it.remove();
            }
        }
        draining = true;
        try {
            for (Map.Entry<Spot, Pending> entry : held.entrySet()) { drain(level, entry.getKey().pos(), entry.getValue()); }
        }
        finally { draining = false; }
    }

    private static void drain(ServerLevel level, BlockPos pos, Pending pending) {
        if (!level.isLoaded(pos)) { return; }
        BlockPos at = pos;
        if (!level.getBlockState(at).is(pending.block())) { at = pos.below(); }
        if (!level.getBlockState(at).is(pending.block())) { at = pos.above(); }
        for (int count = 0; count < pending.extra(); count++) {
            BlockState now = level.getBlockState(at);
            if (!now.is(pending.block())) { return; }
            now.randomTick(level, at, level.getRandom());
        }
    }

    public static void age(LivingEntity living) {
        if (!(living instanceof Animal animal) || !(animal.level() instanceof ServerLevel level) || !animal.isAlive()) { return; }
        int age = animal.getAge();
        if (age == 0 || age < 0 && !animal.canAgeUp()) { return; }
        int delta = steps(level, animal.blockPosition(), age < 0 ? GrowSwitchDef.ANIMALS : GrowSwitchDef.BREEDING, animal.getRandom()) - 1;
        if (delta < 0) { animal.setAge(age < 0 ? age - 1 : age + 1); }
        else if (delta > 0) {
            int move = Math.min(delta, Math.abs(age) - 1);
            animal.setAge(age < 0 ? age + move : age - move);
        }
    }

    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getParentA() instanceof Animal parent) || !(parent.level() instanceof ServerLevel level)) { return; }
        if (ContentEnvironments.growth(level, parent.blockPosition()).scale(GrowSwitchDef.BREEDING) <= 0.0F) { event.setCanceled(true); }
    }

    public static boolean skips(ServerLevel level, BlockPos pos, Block block, RandomSource random) {
        if (draining) { return false; }
        int steps = steps(level, pos, GrowSwitchDef.SAPLINGS, random);
        if (steps > 1) { queue(level, pos.immutable(), new Pending(block, steps - 1)); }
        return steps == 0;
    }

    static void forget() {
        PENDING.clear();
        draining = false;
    }
}
