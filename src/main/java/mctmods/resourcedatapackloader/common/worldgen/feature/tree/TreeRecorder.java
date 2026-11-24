package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.util.plant.Plants;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;

public final class TreeRecorder {
    public static final ThreadLocal<Boolean> RECORDING = ThreadLocal.withInitial(() -> Boolean.FALSE);
    public static final int MAX_ENTRIES = 4096;
    private static final int MAX_CALLS = 16;
    private static final int LIMIT_XZ = 24;
    private static final int LIMIT_Y = 16;

    private TreeRecorder() {}

    public static final class Result {
        public final List<Integer> offsets = new ArrayList<>();
        public final List<IBlockState> states = new ArrayList<>();
        public final List<IBlockState> unders = new ArrayList<>();
        public final List<NBTTagCompound> tiles = new ArrayList<>();
        public final List<Integer> saplingOffsets = new ArrayList<>();
        public final List<IBlockState> saplingStates = new ArrayList<>();
        public boolean overBounds;
        public boolean placed;
    }

    public static boolean recording() { return RECORDING.get(); }

    public static boolean outOfBounds(int dx, int dy, int dz) { return dx < -LIMIT_XZ || dx >= LIMIT_XZ || dz < -LIMIT_XZ || dz >= LIMIT_XZ || dy < -LIMIT_Y || dy >= LIMIT_Y * 3; }

    public static boolean write(World world, BlockPos pos, IBlockState state, int flags) {
        boolean prior = world.captureBlockSnapshots;
        world.captureBlockSnapshots = false;
        try { return world.setBlockState(pos, state, flags); }
        finally { world.captureBlockSnapshots = prior; }
    }

    public static void clear(World world, BlockPos pos) { write(world, pos, Blocks.AIR.getDefaultState(), BlockFlags.UPDATE_ALL); }

    @Nullable public static Result record(World world, BlockPos pos, @Nullable IBlockState sapling, long seed, String source, @Nullable Consumer<Random> own, Predicate<Result> whole) {
        int saved = world.capturedBlockSnapshots.size();
        boolean prior = world.captureBlockSnapshots;
        RECORDING.set(Boolean.TRUE);
        try {
            world.captureBlockSnapshots = true;
            grow(world, pos, sapling, new Random(seed), own, saved);
            Result result = read(world, pos, sapling, saved);
            if (result != null && whole.test(result)) {
                commitWhole(world, saved, prior);
                result.placed = true;
            }
            return result;
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Tree record {} at {} {} {} failed: {}", source, pos.getX(), pos.getY(), pos.getZ(), ex.toString());
            return null;
        }
        finally {
            restore(world, saved, prior);
            RECORDING.set(Boolean.FALSE);
        }
    }

    public static void commitWhole(World world, int saved, boolean prior) {
        List<BlockSnapshot> list = world.capturedBlockSnapshots;
        List<BlockSnapshot> tail = new ArrayList<>(list.subList(saved, list.size()));
        list.subList(saved, list.size()).clear();
        world.captureBlockSnapshots = false;
        try {
            for (BlockSnapshot snap : tail) {
                IBlockState oldState = snap.getReplacedBlock();
                IBlockState newState = world.getBlockState(snap.getPos());
                if (!newState.getBlock().hasTileEntity(newState)) { newState.getBlock().onBlockAdded(world, snap.getPos(), newState); }
                world.markAndNotifyBlock(snap.getPos(), null, oldState, newState, snap.getFlag());
            }
        }
        finally { world.captureBlockSnapshots = prior; }
    }

    private static void grow(World world, BlockPos pos, @Nullable IBlockState sapling, Random rand, @Nullable Consumer<Random> own, int saved) {
        if (own == null && sapling != null) { world.setBlockState(pos, sapling, BlockFlags.UPDATE_CLIENTS); }
        for (int call = 0; call < MAX_CALLS; call++) {
            if (own != null) { own.accept(rand); }
            else {
                IBlockState now = world.getBlockState(pos);
                Block block = now.getBlock();
                if (!(block instanceof IGrowable)) { return; }
                ((IGrowable) block).grow(world, rand, pos, now);
            }
            if (!onlyStart(world.capturedBlockSnapshots, saved, pos)) { return; }
        }
    }

    static boolean onlyStart(List<BlockSnapshot> list, int saved, BlockPos start) {
        if (list.size() <= saved) { return false; }
        for (int i = saved; i < list.size(); i++) {
            if (!list.get(i).getPos().equals(start)) { return false; }
        }
        return true;
    }

    @Nullable private static Result read(World world, BlockPos start, @Nullable IBlockState sapling, int saved) {
        List<BlockSnapshot> list = world.capturedBlockSnapshots;
        Map<BlockPos, IBlockState> first = new LinkedHashMap<>();
        for (int i = saved; i < list.size(); i++) { first.putIfAbsent(list.get(i).getPos(), list.get(i).getReplacedBlock()); }
        if (list.size() <= saved || onlyStart(list, saved, start)) { return null; }
        Result result = new Result();
        for (Map.Entry<BlockPos, IBlockState> entry : first.entrySet()) {
            BlockPos at = entry.getKey();
            IBlockState under = entry.getValue();
            IBlockState now = world.getBlockState(at);
            boolean isStart = at.equals(start);
            boolean isSapling = isStart || Plants.sapling(under.getBlock());
            if (!isSapling && now.getBlock() == under.getBlock()) { continue; }
            BlockPos offset = at.subtract(start);
            if (outOfBounds(offset.getX(), offset.getY(), offset.getZ()) || result.offsets.size() >= MAX_ENTRIES) {
                Result over = new Result();
                over.overBounds = true;
                return over;
            }
            int packed = TreeEntry.pack(offset);
            result.offsets.add(packed);
            result.states.add(now);
            result.unders.add(under);
            result.tiles.add(tileOf(world, at));
            if (isSapling) {
                result.saplingOffsets.add(packed);
                result.saplingStates.add(isStart && sapling != null ? sapling : under);
            }
        }
        return result.offsets.size() == result.saplingOffsets.size() ? null : result;
    }

    @Nullable private static NBTTagCompound tileOf(World world, BlockPos at) {
        TileEntity tile = world.getTileEntity(at);
        if (tile == null) { return null; }
        NBTTagCompound tag = tile.writeToNBT(new NBTTagCompound());
        tag.removeTag("x");
        tag.removeTag("y");
        tag.removeTag("z");
        return tag;
    }

    public static void restore(World world, int saved, boolean prior) {
        List<BlockSnapshot> list = world.capturedBlockSnapshots;
        int end = list.size();
        world.captureBlockSnapshots = true;
        try {
            for (int i = end - 1; i >= saved; i--) { world.setBlockState(list.get(i).getPos(), list.get(i).getReplacedBlock(), BlockFlags.UPDATE_CLIENTS_KNOWN_SHAPE); }
        }
        finally {
            if (list.size() > saved) { list.subList(saved, list.size()).clear(); }
            world.captureBlockSnapshots = prior;
        }
    }
}
