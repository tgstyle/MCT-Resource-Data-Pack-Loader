package mctmods.resourcedatapackloader.common.leafdecay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class LeafDecayQueue {
    private static final int BUDGET = 64;
    private static final int CAP = 8192;
    private static final Map<ResourceKey<Level>, Pending> LEVELS = new HashMap<>();

    private LeafDecayQueue() {}

    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        BlockState state = event.getState();
        if (!LeafDecays.qualifies(state) || !ContentLeafDecay.active()) { return; }
        LeafDecayRule rule = ContentLeafDecay.ruleFor(state, level);
        if (rule == null) { return; }
        Pending pending = LEVELS.computeIfAbsent(level.dimension(), key -> new Pending());
        BlockPos pos = event.getPos().immutable();
        if (pending.queued.size() >= CAP || !pending.queued.add(pos)) { return; }
        pending.byDue.computeIfAbsent(level.getGameTime() + rule.delay(level.random), due -> new ArrayList<>()).add(pos);
    }

    public static void onTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        Pending pending = LEVELS.get(level.dimension());
        if (pending == null) { return; }
        long now = level.getGameTime();
        int taken = 0;
        while (taken < BUDGET && !pending.byDue.isEmpty() && pending.byDue.firstKey() <= now) {
            List<BlockPos> due = pending.byDue.firstEntry().getValue();
            BlockPos pos = due.removeLast();
            if (due.isEmpty()) { pending.byDue.pollFirstEntry(); }
            pending.queued.remove(pos);
            taken++;
            decay(level, pos);
        }
    }

    private static void decay(ServerLevel level, BlockPos pos) {
        if (!level.shouldTickBlocksAt(pos)) { return; }
        BlockState state = level.getBlockState(pos);
        if (LeafDecays.qualifies(state)) { state.randomTick(level, pos, level.random); }
    }

    public static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) { LEVELS.remove(level.dimension()); }
    }

    public static void clear() { LEVELS.clear(); }

    private static final class Pending {
        private final TreeMap<Long, List<BlockPos>> byDue = new TreeMap<>();
        private final Set<BlockPos> queued = new HashSet<>();
    }
}
