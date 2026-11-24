package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class TreeShape {
    private static final int MIN_STEPS = 4;
    private static final int MAX_STEPS = 64;
    private static final int REACH = 6;
    private final Map<BlockPos, BlockState> states = new LinkedHashMap<>();
    private final Map<BlockPos, CompoundTag> entities = new HashMap<>();
    private final Set<BlockPos> logs = new LinkedHashSet<>();
    private final Set<BlockPos> leaves = new LinkedHashSet<>();
    private final List<BlockPos> columns = new ArrayList<>();
    private final Set<BlockPos> trunk = new LinkedHashSet<>();
    private final Set<BlockPos> roots = new LinkedHashSet<>();
    private final Set<BlockPos> branches = new LinkedHashSet<>();
    private BlockPos base;
    private int baseY;
    private int height;
    private int radius;
    private int low;
    private int high;
    private int rise = 1;
    private int steps;
    @Nullable private BlockState cap;

    public TreeShape(Map<BlockPos, BlockState> blocks, Map<BlockPos, CompoundTag> blockEntities, int steps) {
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey().immutable();
            BlockState state = entry.getValue();
            states.put(pos, state);
            if (isLog(state)) { logs.add(pos); }
            else if (isLeaves(state)) { leaves.add(pos); }
        }
        for (Map.Entry<BlockPos, CompoundTag> entry : blockEntities.entrySet()) { entities.put(entry.getKey().immutable(), entry.getValue()); }
        findTrunk();
        if (base == null) { return; }
        findRoots();
        measure();
        this.steps = steps > 0 ? steps : Mth.clamp(height + radius, MIN_STEPS, MAX_STEPS);
        if (cap != null) { cap = persistent(cap); }
    }

    static boolean isLog(BlockState state) { return state.is(BlockTags.LOGS); }

    private static boolean isLeaves(BlockState state) { return state.is(BlockTags.LEAVES); }

    private static int ceilDiv(int value, int by) { return (value + by - 1) / by; }

    private void findTrunk() {
        BlockPos start = null;
        for (BlockPos pos : logs) {
            if (pos.getY() < 0 || Math.abs(pos.getX()) > 1 || Math.abs(pos.getZ()) > 1) { continue; }
            if (start == null || lower(pos, start)) { start = pos; }
        }
        if (start == null) { return; }
        baseY = start.getY();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        Set<BlockPos> layer = new HashSet<>(queue);
        while (!queue.isEmpty()) {
            BlockPos at = queue.poll();
            columns.add(at);
            for (Direction side : Direction.Plane.HORIZONTAL) {
                BlockPos next = at.relative(side);
                if (logs.contains(next) && layer.add(next)) { queue.add(next); }
            }
        }
        for (BlockPos column : columns) {
            int top = 0;
            while (logs.contains(column.above(top))) {
                trunk.add(column.above(top));
                top++;
            }
            height = Math.max(height, top);
            if (base == null || lower(column, base)) { base = column; }
        }
        for (BlockPos pos : logs) {
            if (!trunk.contains(pos)) { branches.add(pos); }
        }
    }

    private static boolean lower(BlockPos pos, BlockPos than) {
        if (pos.getY() != than.getY()) { return pos.getY() < than.getY(); }
        int near = pos.getX() * pos.getX() + pos.getZ() * pos.getZ();
        int other = than.getX() * than.getX() + than.getZ() * than.getZ();
        if (near != other) { return near < other; }
        return pos.getX() != than.getX() ? pos.getX() < than.getX() : pos.getZ() < than.getZ();
    }

    private void findRoots() {
        Deque<BlockPos> queue = new ArrayDeque<>(columns);
        Set<BlockPos> seen = new HashSet<>(queue);
        while (!queue.isEmpty()) {
            BlockPos at = queue.poll();
            for (BlockPos next : around(at)) {
                if (next.getY() >= baseY || logs.contains(next) || leaves.contains(next) || !states.containsKey(next) || !seen.add(next)) { continue; }
                roots.add(next);
                queue.add(next);
            }
        }
    }

    private static List<BlockPos> around(BlockPos at) {
        List<BlockPos> found = new ArrayList<>(26);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx != 0 || dy != 0 || dz != 0) { found.add(at.offset(dx, dy, dz)); }
                }
            }
        }
        return found;
    }

    private int distance(BlockPos pos) {
        int best = Integer.MAX_VALUE;
        for (BlockPos column : columns) { best = Math.min(best, Math.max(Math.abs(pos.getX() - column.getX()), Math.abs(pos.getZ() - column.getZ()))); }
        return best;
    }

    private void measure() {
        for (BlockPos pos : logs) { radius = Math.max(radius, distance(pos)); }
        low = Integer.MAX_VALUE;
        high = Integer.MIN_VALUE;
        Map<BlockState, Integer> counts = new HashMap<>();
        for (BlockPos pos : leaves) {
            radius = Math.max(radius, distance(pos));
            low = Math.min(low, pos.getY() - baseY);
            high = Math.max(high, pos.getY() - baseY);
            counts.merge(states.get(pos), 1, Integer::sum);
        }
        if (leaves.isEmpty()) {
            low = 0;
            high = 0;
        }
        rise = Math.max(1, high - (height - 1));
        int most = 0;
        for (Map.Entry<BlockState, Integer> entry : counts.entrySet()) {
            boolean better = entry.getValue() > most || entry.getValue() == most && cap != null && Block.getId(entry.getKey()) < Block.getId(cap);
            if (better) {
                most = entry.getValue();
                cap = entry.getKey();
            }
        }
    }

    public static BlockState persistent(BlockState state) { return state.hasProperty(LeavesBlock.PERSISTENT) ? state.setValue(LeavesBlock.PERSISTENT, true) : state; }

    public boolean noTrunk() { return base == null; }

    public int height() { return height; }

    public int radius() { return radius; }

    public int low() { return low; }

    public int high() { return high; }

    public int rise() { return rise; }

    public int steps() { return steps; }

    @Nullable public BlockPos base() { return base; }

    public Set<BlockPos> trunk() { return trunk; }

    @Nullable public BlockState cap() { return cap; }

    public Map<BlockPos, BlockState> whole() { return states; }

    @Nullable public CompoundTag entity(BlockPos pos) { return entities.get(pos); }

    public Map<BlockPos, BlockState> stepAt(int k, Set<BlockPos> gone) {
        Map<BlockPos, BlockState> out = new LinkedHashMap<>();
        if (base == null) {
            for (Map.Entry<BlockPos, BlockState> entry : states.entrySet()) {
                if (!gone.contains(entry.getKey())) { out.put(entry.getKey(), entry.getValue()); }
            }
            return out;
        }
        boolean full = k >= steps;
        int top = full ? height : Math.max(1, ceilDiv(height * k, steps));
        int reach = full ? Integer.MAX_VALUE : ceilDiv(radius * k, steps);
        int yHi = full ? Integer.MAX_VALUE : top - 1 + ceilDiv(rise * k, steps);
        Set<BlockPos> placed = connected(top, reach, yHi, gone);
        if (full) {
            for (Map.Entry<BlockPos, BlockState> entry : states.entrySet()) {
                BlockPos pos = entry.getKey();
                if (!gone.contains(pos) && (!logs.contains(pos) || placed.contains(pos))) { out.put(pos, entry.getValue()); }
            }
            return out;
        }
        for (BlockPos pos : placed) { out.put(pos, states.get(pos)); }
        Map<BlockPos, BlockState> crown = crown(top, placed, gone);
        Set<BlockPos> nodes = new HashSet<>(crown.keySet());
        Set<BlockPos> candidates = new HashSet<>();
        for (BlockPos pos : leaves) {
            if (!gone.contains(pos) && pos.getY() - baseY <= yHi && distance(pos) <= reach) { candidates.add(pos); }
        }
        nodes.addAll(candidates);
        Set<BlockPos> seen = new HashSet<>(placed);
        List<BlockPos> front = new ArrayList<>(placed);
        for (int level = 0; level < REACH && !front.isEmpty(); level++) {
            List<BlockPos> next = new ArrayList<>();
            for (BlockPos at : front) {
                for (Direction side : Direction.values()) {
                    BlockPos near = at.relative(side);
                    if (nodes.contains(near) && seen.add(near)) { next.add(near); }
                }
            }
            front = next;
        }
        for (BlockPos pos : seen) {
            if (candidates.contains(pos)) { out.put(pos, persistent(states.get(pos))); }
        }
        for (Map.Entry<BlockPos, BlockState> entry : crown.entrySet()) { out.putIfAbsent(entry.getKey(), entry.getValue()); }
        return out;
    }

    private Set<BlockPos> connected(int top, int reach, int yHi, Set<BlockPos> gone) {
        Set<BlockPos> nodes = new HashSet<>();
        List<BlockPos> seeds = new ArrayList<>();
        for (BlockPos pos : trunk) {
            if (pos.getY() - baseY >= top || gone.contains(pos)) { continue; }
            nodes.add(pos);
            if (pos.getY() == baseY) { seeds.add(pos); }
        }
        for (BlockPos pos : roots) {
            if (!gone.contains(pos)) { nodes.add(pos); }
        }
        for (BlockPos pos : branches) {
            if (!gone.contains(pos) && pos.getY() - baseY <= yHi && distance(pos) <= reach) { nodes.add(pos); }
        }
        return flood(seeds, nodes);
    }

    private static Set<BlockPos> flood(Collection<BlockPos> seeds, Set<BlockPos> nodes) {
        Set<BlockPos> seen = new HashSet<>(seeds);
        Deque<BlockPos> queue = new ArrayDeque<>(seeds);
        while (!queue.isEmpty()) {
            for (BlockPos near : around(queue.poll())) {
                if (nodes.contains(near) && seen.add(near)) { queue.add(near); }
            }
        }
        return seen;
    }

    private Map<BlockPos, BlockState> crown(int top, Set<BlockPos> placed, Set<BlockPos> gone) {
        Map<BlockPos, BlockState> crown = new LinkedHashMap<>();
        if (cap == null) { return crown; }
        for (BlockPos column : columns) {
            BlockPos centre = column.above(top);
            List<BlockPos> plus = new ArrayList<>();
            plus.add(centre);
            for (Direction side : Direction.Plane.HORIZONTAL) { plus.add(centre.relative(side)); }
            plus.add(centre.above());
            for (BlockPos pos : plus) {
                if (!placed.contains(pos) && !gone.contains(pos)) { crown.put(pos, cap); }
            }
        }
        return crown;
    }

    public int looseLogs(Map<BlockPos, BlockState> step) {
        Set<BlockPos> nodes = new HashSet<>();
        List<BlockPos> seeds = new ArrayList<>();
        for (BlockPos pos : step.keySet()) {
            if (!logs.contains(pos) && !roots.contains(pos)) { continue; }
            nodes.add(pos);
            if (trunk.contains(pos) && pos.getY() == baseY) { seeds.add(pos); }
        }
        Set<BlockPos> joined = flood(seeds, nodes);
        int loose = 0;
        for (BlockPos pos : step.keySet()) {
            if (logs.contains(pos) && !joined.contains(pos)) { loose++; }
        }
        return loose;
    }
}
