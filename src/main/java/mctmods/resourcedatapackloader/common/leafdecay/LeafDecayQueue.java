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
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;

public final class LeafDecayQueue {
    private static final int BUDGET = 64;
    private static final int CAP = 8192;
    private static final Map<ResourceKey<Level>, Pending> LEVELS = new HashMap<>();

    private LeafDecayQueue() {}

    public static boolean wanted() { return ContentLeafDecay.wanted(); }

    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        BlockState state = event.getState();
        if (!(state.getBlock() instanceof LeavesBlock) || !(event.getLevel() instanceof ServerLevel level)) { return; }
        if (!ContentLeafDecay.load() || !LeafDecays.qualifies(state)) { return; }
        LeafDecayRule rule = ContentLeafDecay.ruleFor(state, level);
        if (rule == null) { return; }
        Pending pending = LEVELS.computeIfAbsent(level.dimension(), key -> new Pending());
        BlockPos pos = event.getPos().immutable();
        if (pending.queued.size() >= CAP || !pending.queued.add(pos)) { return; }
        pending.due.computeIfAbsent(level.getGameTime() + rule.delay(level.random), key -> new ArrayList<>()).add(pos);
    }

    public static void onTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) { return; }
        Pending pending = LEVELS.get(level.dimension());
        if (pending == null) { return; }
        long now = level.getGameTime();
        int budget = BUDGET;
        while (budget > 0 && !pending.due.isEmpty() && pending.due.firstKey() <= now) {
            List<BlockPos> batch = pending.due.firstEntry().getValue();
            while (budget > 0 && !batch.isEmpty()) {
                BlockPos pos = batch.remove(batch.size() - 1);
                pending.queued.remove(pos);
                budget--;
                decay(level, pos);
            }
            if (batch.isEmpty()) { pending.due.pollFirstEntry(); }
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
        private final TreeMap<Long, List<BlockPos>> due = new TreeMap<>();
        private final Set<BlockPos> queued = new HashSet<>();
    }
}
