package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;

public final class FluidReactionQueue {
    public static final int RECHECK = 1;
    private static final Map<ResourceKey<Level>, Dimension> DIMENSIONS = new HashMap<>();
    private static boolean warned;

    private FluidReactionQueue() {}

    public static boolean wanted() {
        if (VanillaFluidRules.wanted()) { return true; }
        for (FluidDef def : ContentRegistry.fluidDefs()) {
            if (!def.behavior().reactions().isEmpty()) { return true; }
        }
        return false;
    }

    public static boolean spend(ServerLevel level, BlockPos pos, int cost) {
        Dimension dimension = DIMENSIONS.computeIfAbsent(level.dimension(), key -> new Dimension());
        long chunk = ChunkPos.asLong(pos);
        int inChunk = dimension.chunks.getOrDefault(chunk, 0);
        if (over(inChunk, cost, Config.content.fluidReactionsPerChunk()) || over(dimension.spent, cost, Config.content.fluidReactionsPerDimension())) { return false; }
        dimension.chunks.put(chunk, inChunk + cost);
        dimension.spent += cost;
        return true;
    }

    private static boolean over(int spent, int cost, int limit) { return spent > 0 && spent + cost > limit; }

    private static boolean spent(Dimension dimension, BlockPos pos) {
        if (dimension.spent >= Config.content.fluidReactionsPerDimension()) { return true; }
        return dimension.chunks.getOrDefault(ChunkPos.asLong(pos), 0) >= Config.content.fluidReactionsPerChunk();
    }

    public static void watch(ServerLevel level, BlockPos pos, int delay, boolean intervalOnly) {
        Dimension dimension = DIMENSIONS.computeIfAbsent(level.dimension(), key -> new Dimension());
        long at = pos.asLong();
        Watch held = dimension.watched.get(at);
        if (held != null) {
            held.due = Math.min(held.due, level.getGameTime() + delay);
            held.intervalOnly &= intervalOnly;
            return;
        }
        if (dimension.watched.size() >= Config.content.fluidWatched()) {
            if (!warned) {
                warned = true;
                ContentLog.LOGGER.warn("More than {} fluid contacts are waiting on a reaction budget or an interval, so further ones wait for the next neighbor change or random tick. Raise fluidWatched or fluidReactionsPerChunk if reactions are being missed", Config.content.fluidWatched());
            }
            return;
        }
        dimension.watched.put(at, new Watch(level.getGameTime() + delay, intervalOnly));
    }

    public static boolean watching(ServerLevel level, BlockPos pos) {
        Dimension dimension = DIMENSIONS.get(level.dimension());
        return dimension != null && dimension.watched.containsKey(pos.asLong());
    }

    public static void onTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) { return; }
        Dimension dimension = DIMENSIONS.get(level.dimension());
        if (dimension == null) { return; }
        dimension.spent = 0;
        dimension.chunks.clear();
        if (dimension.watched.isEmpty()) { return; }
        long now = level.getGameTime();
        List<Long> due = new ArrayList<>();
        for (Map.Entry<Long, Watch> entry : dimension.watched.entrySet()) {
            if (entry.getValue().due <= now) { due.add(entry.getKey()); }
        }
        for (long at : due) {
            BlockPos pos = BlockPos.of(at);
            if (spent(dimension, pos)) { continue; }
            Watch watch = dimension.watched.remove(at);
            if (!level.isLoaded(pos)) { continue; }
            BlockState state = level.getBlockState(pos);
            if (!FluidReactions.rulesOf(state).isEmpty()) { FluidReactions.check(level, pos, state, watch.intervalOnly); }
        }
    }

    public static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) { DIMENSIONS.remove(level.dimension()); }
    }

    private static final class Dimension {
        private final Map<Long, Integer> chunks = new HashMap<>();
        private final Map<Long, Watch> watched = new LinkedHashMap<>();
        private int spent;
    }

    private static final class Watch {
        private long due;
        private boolean intervalOnly;

        private Watch(long due, boolean intervalOnly) {
            this.due = due;
            this.intervalOnly = intervalOnly;
        }
    }
}
