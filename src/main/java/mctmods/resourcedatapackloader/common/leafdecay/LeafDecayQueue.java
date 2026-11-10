package mctmods.resourcedatapackloader.common.leafdecay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class LeafDecayQueue {
    private static final int BUDGET = 64;
    private static final int CAP = 8192;
    private static final Map<ResourceKey<Level>, Pending> PENDING = new HashMap<>();

    private LeafDecayQueue() {}

    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getState().getBlock() instanceof LeavesBlock) || !ContentLeafDecay.load()) { return; }
        BlockState state = event.getState();
        if (!LeafDecays.qualifies(state)) { return; }
        LeafDecayRule rule = ContentLeafDecay.ruleFor(state, level);
        if (rule == null) { return; }
        Pending pending = PENDING.computeIfAbsent(level.dimension(), _ -> new Pending());
        BlockPos pos = event.getPos().immutable();
        if (pending.queued.size() >= CAP || !pending.queued.add(pos)) { return; }
        pending.due.computeIfAbsent(level.getGameTime() + rule.delay(level.getRandom()), _ -> new ArrayList<>()).add(pos);
    }

    public static void onTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        Pending pending = PENDING.get(level.dimension());
        if (pending == null || pending.due.isEmpty()) { return; }
        long now = level.getGameTime();
        List<BlockPos> taken = new ArrayList<>();
        Iterator<Map.Entry<Long, List<BlockPos>>> each = pending.due.entrySet().iterator();
        while (each.hasNext() && taken.size() < BUDGET) {
            Map.Entry<Long, List<BlockPos>> slot = each.next();
            if (slot.getKey() > now) { break; }
            List<BlockPos> positions = slot.getValue();
            while (!positions.isEmpty() && taken.size() < BUDGET) {
                BlockPos pos = positions.removeLast();
                pending.queued.remove(pos);
                taken.add(pos);
            }
            if (positions.isEmpty()) { each.remove(); }
        }
        for (BlockPos pos : taken) { decay(level, pos); }
    }

    private static void decay(ServerLevel level, BlockPos pos) {
        if (!level.shouldTickBlocksAt(pos)) { return; }
        BlockState state = level.getBlockState(pos);
        if (LeafDecays.qualifies(state)) { state.randomTick(level, pos, level.getRandom()); }
    }

    public static void onUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) { PENDING.remove(level.dimension()); }
    }

    public static void clear() { PENDING.clear(); }

    private static final class Pending {
        private final TreeMap<Long, List<BlockPos>> due = new TreeMap<>();
        private final Set<BlockPos> queued = new HashSet<>();
    }
}
