package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraftforge.common.util.BlockSnapshot;

public final class TreeRecorder {
    public static final ThreadLocal<Boolean> RECORDING = ThreadLocal.withInitial(() -> Boolean.FALSE);
    public static final int MAX_ENTRIES = 4096;
    public static final int LIMIT_XZ = 24;
    public static final int LIMIT_Y = 16;
    private static final int MAX_CALLS = 16;
    private static final int RECURSION = 512;
    private static final ThreadLocal<List<Runnable>> EDGES = ThreadLocal.withInitial(ArrayList::new);

    private TreeRecorder() {}

    public static final class Result {
        public final List<Integer> offsets = new ArrayList<>();
        public final List<BlockState> states = new ArrayList<>();
        public final List<BlockState> unders = new ArrayList<>();
        public final List<CompoundTag> tiles = new ArrayList<>();
        public final List<Integer> saplingOffsets = new ArrayList<>();
        public final List<BlockState> saplingStates = new ArrayList<>();
        public boolean overBounds;
        public boolean placed;
    }

    public static boolean recording() { return RECORDING.get(); }

    public static boolean heldEdge(LevelAccessor level, int flags, DiscreteVoxelShape shape, int x, int y, int z) {
        if (!RECORDING.get()) { return false; }
        EDGES.get().add(() -> StructureTemplate.updateShapeAtEdge(level, flags, shape, x, y, z));
        return true;
    }

    public static boolean outOfBounds(int dx, int dy, int dz) { return dx < -LIMIT_XZ || dx >= LIMIT_XZ || dz < -LIMIT_XZ || dz >= LIMIT_XZ || dy < -LIMIT_Y || dy >= LIMIT_Y * 3; }

    public static boolean write(Level level, BlockPos pos, BlockState state, int flags) {
        boolean prior = level.captureBlockSnapshots;
        level.captureBlockSnapshots = false;
        try { return level.setBlock(pos, state, flags); }
        finally { level.captureBlockSnapshots = prior; }
    }

    @Nullable public static Result record(ServerLevel level, BlockPos pos, @Nullable BlockState sapling, long seed, String source, @Nullable Consumer<RandomSource> own, Predicate<Result> whole) {
        int saved = level.capturedBlockSnapshots.size();
        boolean prior = level.captureBlockSnapshots;
        RECORDING.set(Boolean.TRUE);
        try {
            level.captureBlockSnapshots = true;
            grow(level, pos, sapling, RandomSource.create(seed), own, saved);
            Result result = read(level, pos, sapling, saved);
            if (result != null && whole.test(result)) {
                commitWhole(level, saved, prior);
                result.placed = true;
            }
            return result;
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Tree record {} at {} {} {} failed: {}", source, pos.getX(), pos.getY(), pos.getZ(), ex.toString());
            return null;
        }
        finally {
            restore(level, saved, prior);
            EDGES.get().clear();
            RECORDING.set(Boolean.FALSE);
        }
    }

    public static void commitWhole(Level level, int saved, boolean prior) {
        List<BlockSnapshot> list = level.capturedBlockSnapshots;
        List<BlockSnapshot> tail = new ArrayList<>(list.subList(saved, list.size()));
        list.subList(saved, list.size()).clear();
        level.captureBlockSnapshots = false;
        try {
            for (BlockSnapshot snap : tail) {
                BlockState oldState = snap.getReplacedBlock();
                BlockState newState = level.getBlockState(snap.getPos());
                newState.onPlace(level, snap.getPos(), oldState, false);
                level.markAndNotifyBlock(snap.getPos(), level.getChunkAt(snap.getPos()), oldState, newState, snap.getFlag(), RECURSION);
            }
            replayEdges();
        }
        finally { level.captureBlockSnapshots = prior; }
    }

    private static void replayEdges() {
        List<Runnable> edges = EDGES.get();
        if (edges.isEmpty()) { return; }
        RECORDING.set(Boolean.FALSE);
        try {
            for (Runnable edge : edges) { edge.run(); }
        }
        finally {
            edges.clear();
            RECORDING.set(Boolean.TRUE);
        }
    }

    private static void grow(ServerLevel level, BlockPos pos, @Nullable BlockState sapling, RandomSource rand, @Nullable Consumer<RandomSource> own, int saved) {
        if (own == null && sapling != null) { level.setBlock(pos, sapling, 2); }
        for (int call = 0; call < MAX_CALLS; call++) {
            if (own != null) { own.accept(rand); }
            else {
                BlockState now = level.getBlockState(pos);
                Block block = now.getBlock();
                if (!(block instanceof BonemealableBlock growable)) { return; }
                growable.performBonemeal(level, rand, pos, now);
            }
            if (!onlyStart(level.capturedBlockSnapshots, saved, pos)) { return; }
        }
    }

    private static boolean onlyStart(List<BlockSnapshot> list, int saved, BlockPos start) {
        if (list.size() <= saved) { return false; }
        for (int i = saved; i < list.size(); i++) {
            if (!list.get(i).getPos().equals(start)) { return false; }
        }
        return true;
    }

    @Nullable private static Result read(Level level, BlockPos start, @Nullable BlockState sapling, int saved) {
        List<BlockSnapshot> list = level.capturedBlockSnapshots;
        Map<BlockPos, BlockState> first = new LinkedHashMap<>();
        for (int i = saved; i < list.size(); i++) { first.putIfAbsent(list.get(i).getPos(), list.get(i).getReplacedBlock()); }
        if (list.size() <= saved || onlyStart(list, saved, start)) { return null; }
        Result result = new Result();
        for (Map.Entry<BlockPos, BlockState> entry : first.entrySet()) {
            BlockPos at = entry.getKey();
            BlockState under = entry.getValue();
            BlockState now = level.getBlockState(at);
            boolean isStart = at.equals(start);
            boolean isSapling = isStart || under.getBlock() instanceof SaplingBlock || under.getBlock() instanceof ContentSaplingBlock;
            if (!isSapling && now.getBlock() == under.getBlock()) { continue; }
            int dx = at.getX() - start.getX();
            int dy = at.getY() - start.getY();
            int dz = at.getZ() - start.getZ();
            if (outOfBounds(dx, dy, dz) || result.offsets.size() >= MAX_ENTRIES) {
                Result over = new Result();
                over.overBounds = true;
                return over;
            }
            int packed = TreeEntry.pack(dx, dy, dz);
            result.offsets.add(packed);
            result.states.add(now);
            result.unders.add(under);
            result.tiles.add(tileOf(level, at));
            if (isSapling) {
                result.saplingOffsets.add(packed);
                result.saplingStates.add(isStart && sapling != null ? sapling : under);
            }
        }
        return result.offsets.size() == result.saplingOffsets.size() ? null : result;
    }

    @Nullable private static CompoundTag tileOf(Level level, BlockPos at) {
        BlockEntity tile = level.getBlockEntity(at);
        if (tile == null) { return null; }
        CompoundTag tag = tile.saveWithFullMetadata();
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");
        return tag;
    }

    public static void restore(Level level, int saved, boolean prior) {
        List<BlockSnapshot> list = level.capturedBlockSnapshots;
        int end = list.size();
        level.captureBlockSnapshots = false;
        try {
            for (int i = end - 1; i >= saved; i--) { put(level, list.get(i)); }
        }
        finally {
            if (list.size() > saved) { list.subList(saved, list.size()).clear(); }
            level.captureBlockSnapshots = prior;
        }
    }

    private static void put(Level level, BlockSnapshot snap) {
        BlockPos at = snap.getPos();
        LevelChunk chunk = level.getChunkAt(at);
        chunk.removeBlockEntity(at);
        chunk.setBlockState(at, snap.getReplacedBlock(), false);
        CompoundTag tag = snap.getTag();
        if (tag == null) { return; }
        BlockEntity tile = chunk.getBlockEntity(at);
        if (tile != null) {
            tile.load(tag);
            tile.setChanged();
        }
    }
}
