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
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class FluidReactionQueue {
    public static final int RECHECK = 1;
    private static final Map<Integer, Dimension> DIMENSIONS = new HashMap<>();
    private static boolean warned;

    private FluidReactionQueue() {}

    public static boolean wanted() {
        if (VanillaFluidRules.wanted()) { return true; }
        for (Map.Entry<ResourceLocation, ContentFluidBlock> entry : ContentRegistry.registeredFluidBlocks()) {
            FluidDef def = ContentRegistry.fluidDef(entry.getKey());
            if (def != null && !def.behavior.reactions.isEmpty()) { return true; }
        }
        return false;
    }

    public static boolean spend(World world, BlockPos pos, int cost) {
        Dimension dimension = DIMENSIONS.computeIfAbsent(world.provider.getDimension(), key -> new Dimension());
        long chunk = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        int inChunk = dimension.chunks.getOrDefault(chunk, 0);
        if (over(inChunk, cost, Config.content.fluidReactionsPerChunk) || over(dimension.spent, cost, Config.content.fluidReactionsPerDimension)) { return false; }
        dimension.chunks.put(chunk, inChunk + cost);
        dimension.spent += cost;
        return true;
    }

    private static boolean over(int spent, int cost, int limit) { return spent > 0 && spent + cost > limit; }

    private static boolean spent(Dimension dimension, BlockPos pos) {
        if (dimension.spent >= Config.content.fluidReactionsPerDimension) { return true; }
        return dimension.chunks.getOrDefault(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4), 0) >= Config.content.fluidReactionsPerChunk;
    }

    public static void watch(World world, BlockPos pos, int delay, boolean intervalOnly) {
        Dimension dimension = DIMENSIONS.computeIfAbsent(world.provider.getDimension(), key -> new Dimension());
        long at = pos.toLong();
        Watch held = dimension.watched.get(at);
        if (held != null) {
            held.due = Math.min(held.due, world.getTotalWorldTime() + delay);
            held.intervalOnly &= intervalOnly;
            return;
        }
        if (dimension.watched.size() >= Config.content.fluidWatched) {
            if (!warned) {
                warned = true;
                ContentLog.LOGGER.warn("More than {} fluid contacts are waiting on a reaction budget or an interval, so further ones wait for the next neighbor change or random tick. Raise fluidWatched or fluidReactionsPerChunk if reactions are being missed", Config.content.fluidWatched);
            }
            return;
        }
        dimension.watched.put(at, new Watch(world.getTotalWorldTime() + delay, intervalOnly));
    }

    public static boolean watching(World world, BlockPos pos) {
        Dimension dimension = DIMENSIONS.get(world.provider.getDimension());
        return dimension != null && dimension.watched.containsKey(pos.toLong());
    }

    @SubscribeEvent public static void onTick(TickEvent.WorldTickEvent event) {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END) { return; }
        Dimension dimension = DIMENSIONS.get(event.world.provider.getDimension());
        if (dimension == null) { return; }
        dimension.spent = 0;
        dimension.chunks.clear();
        if (dimension.watched.isEmpty()) { return; }
        long now = event.world.getTotalWorldTime();
        List<Long> due = new ArrayList<>();
        for (Map.Entry<Long, Watch> entry : dimension.watched.entrySet()) {
            if (entry.getValue().due <= now) { due.add(entry.getKey()); }
        }
        for (long at : due) {
            BlockPos pos = BlockPos.fromLong(at);
            if (spent(dimension, pos)) { continue; }
            Watch watch = dimension.watched.remove(at);
            if (!event.world.isBlockLoaded(pos)) { continue; }
            IBlockState state = event.world.getBlockState(pos);
            if (!FluidReactions.rulesOf(state).isEmpty()) { FluidReactions.check(event.world, pos, state, watch.intervalOnly); }
        }
    }

    @SubscribeEvent public static void onUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) { DIMENSIONS.remove(event.getWorld().provider.getDimension()); }
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
