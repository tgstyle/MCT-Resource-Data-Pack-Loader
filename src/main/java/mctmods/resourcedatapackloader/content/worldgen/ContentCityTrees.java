package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.blastplaster.util.BlastPlasterUtil;
import mctmods.blastplaster.util.TreeCollector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class ContentCityTrees {
    private static final int UP = 16;
    private static final int SUSTAIN = 6;

    private ContentCityTrees() {}

    public static int fellAround(WorldGenLevel level, BoundingBox held, BoundingBox box, int floor, int top, int reach) {
        List<BlockPos> seeds = new ArrayList<>();
        List<BlockPos> canopy = new ArrayList<>();
        int felled = 0;
        int leastX = Math.max(held.minX() - reach, box.minX());
        int mostX = Math.min(held.maxX() + reach, box.maxX());
        int leastZ = Math.max(held.minZ() - reach, box.minZ());
        int mostZ = Math.min(held.maxZ() + reach, box.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = leastX; x <= mostX; x++) {
            for (int z = leastZ; z <= mostZ; z++) {
                boolean over = x >= held.minX() && x <= held.maxX() && z >= held.minZ() && z <= held.maxZ();
                int from = over ? Math.max(floor, top + 1) : floor;
                for (int y = from; y <= held.maxY() + UP; y++) {
                    at.set(x, y, z);
                    if (!box.isInside(at)) { continue; }
                    BlockState state = level.getBlockState(at);
                    if (state.isAir()) { continue; }
                    if (BlastPlasterUtil.isTreeWood(state)) { seeds.add(at.immutable()); }
                    else if (state.getBlock() instanceof LeavesBlock) { canopy.add(at.immutable()); }
                    else if (state.is(Blocks.VINE)) { felled += clear(level, at); }
                }
            }
        }
        Predicate<BlockPos> within = box::isInside;
        felled += fellTrees(level, seeds, within);
        for (BlockPos leaf : canopy) {
            BlockState state = level.getBlockState(leaf);
            if (!(state.getBlock() instanceof LeavesBlock)) { continue; }
            if (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) { continue; }
            if (sustained(level, leaf, box)) { continue; }
            at.set(leaf.getX(), leaf.getY(), leaf.getZ());
            felled += clear(level, at);
        }
        return felled;
    }

    private static int fellTrees(WorldGenLevel level, List<BlockPos> seeds, Predicate<BlockPos> within) {
        int felled = 0;
        int most = mctmods.blastplaster.Config.view(level).getMaxTreeSize();
        Set<BlockPos> done = new HashSet<>();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (BlockPos seed : seeds) {
            if (done.contains(seed)) { continue; }
            TreeCollector.Tree tree = TreeCollector.collect(level, seed, most, within);
            for (BlockPos log : tree.logs) {
                done.add(log);
                at.set(log.getX(), log.getY(), log.getZ());
                felled += clear(level, at);
            }
            for (BlockPos leaf : tree.leaves) {
                at.set(leaf.getX(), leaf.getY(), leaf.getZ());
                felled += clear(level, at);
            }
        }
        return felled;
    }

    private static boolean sustained(WorldGenLevel level, BlockPos leaf, BoundingBox box) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int dx = -SUSTAIN; dx <= SUSTAIN; dx++) {
            for (int dy = -SUSTAIN; dy <= SUSTAIN; dy++) {
                for (int dz = -SUSTAIN; dz <= SUSTAIN; dz++) {
                    probe.set(leaf.getX() + dx, leaf.getY() + dy, leaf.getZ() + dz);
                    if (!box.isInside(probe)) { continue; }
                    if (BlastPlasterUtil.isTreeWood(level.getBlockState(probe))) { return true; }
                }
            }
        }
        return false;
    }

    private static int clear(WorldGenLevel level, BlockPos.MutableBlockPos at) {
        if (level.getBlockState(at).isAir()) { return 0; }
        level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
        return 1;
    }
}
