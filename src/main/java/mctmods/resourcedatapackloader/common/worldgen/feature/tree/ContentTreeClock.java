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
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;

public final class ContentTreeClock {
    private static final int BUCKETS = 20;
    private static final int BUDGET = 16;
    private static final Map<ResourceKey<Level>, State> STATES = new HashMap<>();

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

    public static void track(ServerLevel level, TreeEntry entry) { state(level).buckets.get(bucket(entry.pos)).add(entry.pos); }

    public static boolean spend(ServerLevel level) {
        State state = state(level);
        if (state.spent >= BUDGET) { return false; }
        state.spent++;
        return true;
    }

    @Nullable public static String reason(ServerLevel level, long pos) {
        State state = STATES.get(level.dimension());
        return state == null ? null : state.reasons.get(pos);
    }

    public static void credit(ServerLevel level, TreeEntry entry, long ticks) {
        entry.progress += ticks;
        ContentTreeStore.dirty(level, entry);
        TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
        if (growth != null && growth.timed() && entry.progress >= growth.stepTicks()) { state(level).queue.merge(entry.pos, 0, Integer::sum); }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        State state = state(level);
        long now = level.getGameTime();
        ChunkPos chunk = event.getChunk().getPos();
        for (TreeEntry entry : ContentTreeStore.entries(level, chunk.x, chunk.z)) {
            track(level, entry);
            TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
            if (growth == null || !growth.timed()) { continue; }
            if (growth.catchUp() == TreeGrowthDef.CatchUp.PAUSE) { entry.seen = now; }
            else { state.fresh.add(entry.pos); }
        }
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) { return; }
        State state = state(level);
        for (long pos : new ArrayList<>(state.buckets.get((int) (level.getGameTime() % BUCKETS)))) { visit(level, state, pos); }
        while (state.spent < BUDGET && !state.queue.isEmpty()) { run(level, state, state.queue.keySet().iterator().next()); }
        if (state.spent > 0) { ContentLog.LOGGER.info("Tree clock {}: {} steps, {} queued", level.dimension().location(), state.spent, state.queue.size()); }
        state.spent = 0;
    }

    static void forget(ResourceKey<Level> dimension) { STATES.remove(dimension); }

    private static void unload(ServerLevel level, ChunkPos chunk) {
        State state = STATES.get(level.dimension());
        if (state == null) { return; }
        for (Set<Long> bucket : state.buckets) { bucket.removeIf(pos -> inChunk(pos, chunk)); }
        state.queue.keySet().removeIf(pos -> inChunk(pos, chunk));
        state.fresh.removeIf(pos -> inChunk(pos, chunk));
    }

    private static boolean inChunk(long pos, ChunkPos chunk) {
        BlockPos at = BlockPos.of(pos);
        return at.getX() >> 4 == chunk.x && at.getZ() >> 4 == chunk.z;
    }

    private static void visit(ServerLevel level, State state, long pos) {
        TreeEntry entry = ContentTreeStore.get(level, pos);
        if (entry == null) {
            state.buckets.get(bucket(pos)).remove(pos);
            return;
        }
        if (entry.phase == TreeEntry.PHASE_FELLED) {
            ContentTreeFelling.release(level, entry);
            return;
        }
        TreeGrowthDef growth = ContentTreeRules.byId(entry.rule);
        if (growth == null) {
            BlockPos at = BlockPos.of(pos);
            ContentTreeStore.remove(level, pos);
            state.buckets.get(bucket(pos)).remove(pos);
            ContentLog.LOGGER.info("Tree at {} {} {} dropped: rule {} gone", at.getX(), at.getY(), at.getZ(), entry.rule);
            return;
        }
        boolean sapling = entry.phase == 0;
        Map<BlockPos, BlockState> current = sapling ? Collections.emptyMap() : ContentTreeGrowth.current(level, entry);
        TreeShape shape = sapling ? null : ContentTreeGrowth.shape(level, entry);
        if (ContentTreeFelling.visit(level, entry, current, shape)) { state.reasons.put(pos, "chunk not loaded"); }
        if (ContentTreeStore.get(level, pos) != entry || entry.phase == TreeEntry.PHASE_FELLED) { return; }
        if (growth.timed()) { credit(level, state, entry, growth); }
        else if (!sapling) { roll(level, state, entry, growth); }
    }

    private static void credit(ServerLevel level, State state, TreeEntry entry, TreeGrowthDef growth) {
        String blocked = ContentTreeGrowth.conditions(level, entry, growth);
        if ("chunk not loaded".equals(blocked)) {
            state.reasons.put(entry.pos, blocked);
            if (growth.catchUp() == TreeGrowthDef.CatchUp.PAUSE) { entry.seen = level.getGameTime(); }
            else { state.fresh.add(entry.pos); }
            return;
        }
        long now = level.getGameTime();
        long elapsed = Math.max(0L, now - entry.seen);
        entry.seen = now;
        boolean fresh = state.fresh.remove(entry.pos);
        if (blocked == null) { entry.progress += (long) (elapsed * scale(level, entry)); }
        else { state.reasons.put(entry.pos, blocked); }
        if (fresh && growth.catchUpLimit() > 0) { entry.progress = Math.min(entry.progress, (long) growth.catchUpLimit() * growth.stepTicks()); }
        ContentTreeStore.dirty(level, entry);
        if (entry.progress >= growth.stepTicks()) { state.queue.merge(entry.pos, 0, Integer::sum); }
    }

    private static void roll(ServerLevel level, State state, TreeEntry entry, TreeGrowthDef growth) {
        int speed = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        RandomSource random = level.random;
        int ticks = 0;
        if (speed > 0) {
            for (int i = 0; i < BUCKETS; i++) { ticks += random.nextInt(4096) < speed ? 1 : 0; }
        }
        if (ticks == 0) { return; }
        String blocked = ContentTreeGrowth.conditions(level, entry, growth);
        if (blocked != null) {
            state.reasons.put(entry.pos, blocked);
            return;
        }
        float scale = scale(level, entry);
        int owed = 0;
        for (int i = 0; i < ticks; i++) {
            for (int tries = ContentGrowSwitch.draws(scale, random); tries > 0; tries--) {
                if (growth.rolls(random)) { owed++; }
                else { state.reasons.put(entry.pos, "chance"); }
            }
        }
        if (owed > 0) { state.queue.merge(entry.pos, owed, Integer::sum); }
    }

    private static float scale(ServerLevel level, TreeEntry entry) {
        BlockState sapling = entry.saplings.get(TreeEntry.pack(0, 0, 0));
        return sapling == null ? 1.0F : ContentGrowSwitch.scale(level, BlockPos.of(entry.pos), sapling.getBlock());
    }

    private static void run(ServerLevel level, State state, long pos) {
        TreeEntry entry = ContentTreeStore.get(level, pos);
        TreeGrowthDef growth = entry == null ? null : ContentTreeRules.byId(entry.rule);
        if (growth == null) {
            state.queue.remove(pos);
            return;
        }
        boolean timed = growth.timed();
        while (true) {
            int owed = timed ? (int) Math.min(entry.progress / growth.stepTicks(), Integer.MAX_VALUE) : state.queue.get(pos);
            if (owed <= 0) {
                state.queue.remove(pos);
                return;
            }
            if (!spend(level)) {
                state.reasons.put(pos, "budget");
                return;
            }
            String reason = null;
            if (entry.phase == 0) {
                BlockState sapling = entry.saplings.get(TreeEntry.pack(0, 0, 0));
                BlockPos at = BlockPos.of(pos);
                BlockState now = level.getBlockState(at);
                if (sapling != null && now.getBlock() == sapling.getBlock()) { ContentTreeGrowth.drive(level, at, now, level.random); }
            }
            else { reason = ContentTreeGrowth.step(level, entry, growth, timed); }
            if (reason != null) {
                state.reasons.put(pos, reason);
                state.queue.remove(pos);
                return;
            }
            state.reasons.remove(pos);
            if (timed) {
                entry.progress -= growth.stepTicks();
                ContentTreeStore.dirty(level, entry);
            }
            else { state.queue.put(pos, owed - 1); }
            TreeEntry born = ContentTreeStore.get(level, pos);
            if (born != entry) {
                if (!timed || born == null || born.phase == TreeEntry.PHASE_FELLED) {
                    state.queue.remove(pos);
                    return;
                }
                born.progress = entry.progress;
                ContentTreeStore.dirty(level, born);
                entry = born;
            }
        }
    }

    private static State state(ServerLevel level) { return STATES.computeIfAbsent(level.dimension(), k -> new State()); }

    private static int bucket(long pos) { return Math.floorMod(Long.hashCode(pos), BUCKETS); }
}
