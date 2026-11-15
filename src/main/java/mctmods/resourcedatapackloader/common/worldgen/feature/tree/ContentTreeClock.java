package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.rules.ContentGrowSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentTreeRules;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentTreeClock {
    private static final int BUCKETS = 20;
    private static final int BUDGET = 16;
    private static final Map<Integer, State> STATES = new HashMap<>();

    static { ContentTreeStore.UNLOAD_LISTENERS.add(ContentTreeClock::unload); }

    private ContentTreeClock() {}

    private static final class State {
        private final List<Set<Long>> buckets = new ArrayList<>();
        private final Map<Long, Integer> queue = new LinkedHashMap<>();
        private final Map<Long, String> reasons = new HashMap<>();
        private final Set<Long> fresh = new HashSet<>();
        private int spent;

        private State() {
            for (int i = 0; i < BUCKETS; i++) { buckets.add(new HashSet<>()); }
        }
    }

    public static void track(WorldServer world, TreeEntry entry) { state(world).buckets.get(bucket(entry.pos)).add(entry.pos); }

    public static boolean spend(WorldServer world) {
        State state = state(world);
        if (state.spent >= BUDGET) { return false; }
        state.spent++;
        return true;
    }

    @Nullable public static String reason(WorldServer world, long pos) {
        State state = STATES.get(world.provider.getDimension());
        return state == null ? null : state.reasons.get(pos);
    }

    public static void credit(WorldServer world, TreeEntry entry, long ticks) {
        entry.progress += ticks;
        ContentTreeStore.dirty(world, entry);
        TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
        if (growth != null && growth.timed() && entry.progress >= growth.stepTicks) { state(world).queue.merge(entry.pos, 0, Integer::sum); }
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onChunkLoad(ChunkDataEvent.Load event) {
        if (!(event.getWorld() instanceof WorldServer)) { return; }
        WorldServer world = (WorldServer) event.getWorld();
        State state = state(world);
        long now = world.getTotalWorldTime();
        ChunkPos chunk = event.getChunk().getPos();
        for (TreeEntry entry : ContentTreeStore.entries(world, chunk.x, chunk.z)) {
            track(world, entry);
            TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
            if (growth == null || !growth.timed()) { continue; }
            if (growth.catchUp == TreeGrowthDef.CatchUp.PAUSE) { entry.seen = now; }
            else { state.fresh.add(entry.pos); }
        }
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof WorldServer)) { return; }
        WorldServer world = (WorldServer) event.world;
        State state = state(world);
        for (long pos : new ArrayList<>(state.buckets.get((int) (world.getTotalWorldTime() % BUCKETS)))) { visit(world, state, pos); }
        while (state.spent < BUDGET && !state.queue.isEmpty()) { run(world, state, state.queue.keySet().iterator().next()); }
        if (state.spent > 0) { ContentLog.LOGGER.info("Tree clock {}: {} steps, {} queued", world.provider.getDimension(), state.spent, state.queue.size()); }
        state.spent = 0;
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) { STATES.remove(event.getWorld().provider.getDimension()); }
    }

    private static void unload(WorldServer world, ChunkPos chunk) {
        State state = STATES.get(world.provider.getDimension());
        if (state == null) { return; }
        for (Set<Long> bucket : state.buckets) { bucket.removeIf(pos -> inChunk(pos, chunk)); }
        state.queue.keySet().removeIf(pos -> inChunk(pos, chunk));
        state.fresh.removeIf(pos -> inChunk(pos, chunk));
    }

    private static boolean inChunk(long pos, ChunkPos chunk) {
        BlockPos at = BlockPos.fromLong(pos);
        return at.getX() >> 4 == chunk.x && at.getZ() >> 4 == chunk.z;
    }

    private static void visit(WorldServer world, State state, long pos) {
        TreeEntry entry = ContentTreeStore.get(world, pos);
        if (entry == null) {
            state.buckets.get(bucket(pos)).remove(pos);
            return;
        }
        if (entry.phase == TreeEntry.PHASE_FELLED) {
            ContentTreeFelling.release(world, entry);
            return;
        }
        TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
        if (growth == null) {
            BlockPos at = BlockPos.fromLong(pos);
            ContentTreeStore.remove(world, pos);
            state.buckets.get(bucket(pos)).remove(pos);
            ContentLog.LOGGER.info("Tree at {} {} {} dropped: rule {} gone", at.getX(), at.getY(), at.getZ(), entry.rule);
            return;
        }
        boolean sapling = entry.phase == 0;
        Map<BlockPos, IBlockState> current = sapling ? Collections.emptyMap() : ContentTreeGrowth.current(world, entry);
        TreeShape shape = sapling ? null : ContentTreeGrowth.shape(world, entry);
        if (ContentTreeFelling.visit(world, entry, current, shape)) { state.reasons.put(pos, "chunk not loaded"); }
        if (ContentTreeStore.get(world, pos) != entry || entry.phase == TreeEntry.PHASE_FELLED) { return; }
        if (growth.timed()) { credit(world, state, entry, growth); }
        else if (!sapling) { roll(world, state, entry, growth); }
    }

    private static void credit(WorldServer world, State state, TreeEntry entry, TreeGrowthDef growth) {
        String blocked = ContentTreeGrowth.conditions(world, entry, growth);
        if ("chunk not loaded".equals(blocked)) {
            state.reasons.put(entry.pos, blocked);
            if (growth.catchUp == TreeGrowthDef.CatchUp.PAUSE) { entry.seen = world.getTotalWorldTime(); }
            else { state.fresh.add(entry.pos); }
            return;
        }
        long now = world.getTotalWorldTime();
        long elapsed = Math.max(0L, now - entry.seen);
        entry.seen = now;
        boolean fresh = state.fresh.remove(entry.pos);
        if (blocked == null) { entry.progress += (long) (elapsed * scale(world, entry)); }
        else { state.reasons.put(entry.pos, blocked); }
        if (fresh && growth.catchUpLimit > 0) { entry.progress = Math.min(entry.progress, (long) growth.catchUpLimit * growth.stepTicks); }
        ContentTreeStore.dirty(world, entry);
        if (entry.progress >= growth.stepTicks) { state.queue.merge(entry.pos, 0, Integer::sum); }
    }

    private static void roll(WorldServer world, State state, TreeEntry entry, TreeGrowthDef growth) {
        int speed = world.getGameRules().getInt("randomTickSpeed");
        Random random = world.rand;
        int ticks = 0;
        if (speed > 0) {
            for (int i = 0; i < BUCKETS; i++) { ticks += random.nextInt(4096) < speed ? 1 : 0; }
        }
        if (ticks == 0) { return; }
        String blocked = ContentTreeGrowth.conditions(world, entry, growth);
        if (blocked != null) {
            state.reasons.put(entry.pos, blocked);
            return;
        }
        float scale = scale(world, entry);
        int owed = 0;
        for (int i = 0; i < ticks; i++) {
            for (int tries = ContentGrowSwitch.draws(scale, random); tries > 0; tries--) {
                if (growth.chance < 0.0 || random.nextDouble() < growth.chance) { owed++; }
                else { state.reasons.put(entry.pos, "chance"); }
            }
        }
        if (owed > 0) { state.queue.merge(entry.pos, owed, Integer::sum); }
    }

    private static float scale(WorldServer world, TreeEntry entry) {
        IBlockState sapling = entry.saplings.get(TreeEntry.pack(0, 0, 0));
        return sapling == null ? 1.0F : ContentGrowSwitch.scale(world, BlockPos.fromLong(entry.pos), sapling.getBlock());
    }

    private static void run(WorldServer world, State state, long pos) {
        TreeEntry entry = ContentTreeStore.get(world, pos);
        TreeGrowthDef growth = entry == null ? null : ContentTreeRules.byId(entry.rule);
        if (growth == null) {
            state.queue.remove(pos);
            return;
        }
        boolean timed = growth.timed();
        while (true) {
            int owed = timed ? (int) Math.min(entry.progress / growth.stepTicks, Integer.MAX_VALUE) : state.queue.get(pos);
            if (owed <= 0) {
                state.queue.remove(pos);
                return;
            }
            if (!spend(world)) {
                state.reasons.put(pos, "budget");
                return;
            }
            String reason = null;
            if (entry.phase == 0) {
                IBlockState sapling = entry.saplings.get(TreeEntry.pack(0, 0, 0));
                BlockPos at = BlockPos.fromLong(pos);
                IBlockState now = world.getBlockState(at);
                if (sapling != null && now.getBlock() == sapling.getBlock()) { ContentTreeGrowth.drive(world, at, now, world.rand); }
            }
            else { reason = ContentTreeGrowth.step(world, entry, growth, timed); }
            if (reason != null) {
                state.reasons.put(pos, reason);
                state.queue.remove(pos);
                return;
            }
            state.reasons.remove(pos);
            if (timed) {
                entry.progress -= growth.stepTicks;
                ContentTreeStore.dirty(world, entry);
            }
            else { state.queue.put(pos, owed - 1); }
            TreeEntry born = ContentTreeStore.get(world, pos);
            if (born != entry) {
                if (!timed || born == null || born.phase == TreeEntry.PHASE_FELLED) {
                    state.queue.remove(pos);
                    return;
                }
                born.progress = entry.progress;
                ContentTreeStore.dirty(world, born);
                entry = born;
            }
        }
    }

    private static State state(WorldServer world) { return STATES.computeIfAbsent(world.provider.getDimension(), k -> new State()); }

    private static int bucket(long pos) { return Math.floorMod(Long.hashCode(pos), BUCKETS); }
}
