package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentTree implements IContentShape {
    private static final int FLAGS = 2 | 16;
    private final AmountDef count;
    private final AmountDef height;
    private final int scatterX;
    private final int scatterZ;
    private final int drift;
    @Nullable private final BlockState log;
    @Nullable private final BlockState leaves;

    public ContentTree(AmountDef count, ShapeDef shape, @Nullable BlockState log, @Nullable BlockState leaves, ResourceLocation key) {
        this.count = count;
        this.height = shape.height();
        this.scatterX = shape.scatterX();
        this.scatterZ = shape.scatterZ();
        this.drift = Math.max(1, shape.scatterY());
        this.log = log;
        this.leaves = leaves;
        if (log == null || leaves == null) { ContentLog.LOGGER.error("Worldgen {} grows a tree but its log '{}' or leaves '{}' are not registered, so nothing generates", key, shape.log(), shape.leaves()); }
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        if (log == null || leaves == null) { return false; }
        WorldGenLevel level = placer.level();
        Set<Block> surface = placer.palette().surface();
        boolean placed = false;
        int attempts = count.pick(random);
        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = origin.getX() + ContentPlacer.scatter(random, scatterX);
            int z = origin.getZ() + ContentPlacer.scatter(random, scatterZ);
            BlockPos top = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);
            if (placer.unreadable(top)) { continue; }
            if (Math.abs(top.getY() - origin.getY()) > drift) { continue; }
            if (!surface.isEmpty() && !surface.contains(level.getBlockState(top.below()).getBlock())) { continue; }
            if (!level.isEmptyBlock(top)) { continue; }
            placed |= grow(placer, random, top, Math.max(1, height.pick(random)), surface);
        }
        return placed;
    }

    private boolean grow(ContentPlacer placer, RandomSource random, BlockPos position, int least, Set<Block> surface) {
        WorldGenLevel level = placer.level();
        int tall = random.nextInt(3) + least;
        if (position.getY() < placer.floorY() || position.getY() >= placer.ceilingY() - tall - 1) { return false; }
        if (!rooted(level, position.below(), surface)) { return false; }
        if (!clear(placer, position, tall)) { return false; }
        BlockPos below = position.below();
        if (level.getBlockState(below).is(Blocks.GRASS_BLOCK)) { level.setBlock(below, Blocks.DIRT.defaultBlockState(), FLAGS); }
        Set<BlockPos> canopy = canopy(placer, random, position, tall);
        Set<BlockPos> logs = trunk(placer, position, tall);
        canopy.removeAll(logs);
        settle(placer, canopy, logs);
        return true;
    }

    private static boolean rooted(WorldGenLevel level, BlockPos below, Set<Block> surface) {
        BlockState state = level.getBlockState(below);
        if (!surface.isEmpty()) { return surface.contains(state.getBlock()); }
        return state.canSustainPlant(level, below, Direction.UP, (SaplingBlock) Blocks.OAK_SAPLING);
    }

    private static boolean clear(ContentPlacer placer, BlockPos position, int tall) {
        for (int y = position.getY(); y <= position.getY() + 1 + tall; y++) {
            int reach = 1;
            if (y == position.getY()) { reach = 0; }
            if (y >= position.getY() + 1 + tall - 2) { reach = 2; }
            for (int x = position.getX() - reach; x <= position.getX() + reach; x++) {
                for (int z = position.getZ() - reach; z <= position.getZ() + reach; z++) {
                    BlockPos at = new BlockPos(x, y, z);
                    if (y < placer.floorY() || y >= placer.ceilingY() || placer.unreadable(at)) { return false; }
                    if (!replaceable(placer.level(), at)) { return false; }
                }
            }
        }
        return true;
    }

    private static boolean replaceable(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || state.is(BlockTags.REPLACEABLE_BY_TREES) || state.is(Blocks.VINE);
    }

    private Set<BlockPos> canopy(ContentPlacer placer, RandomSource random, BlockPos position, int tall) {
        Set<BlockPos> placed = new HashSet<>();
        for (int y = position.getY() - 3 + tall; y <= position.getY() + tall; y++) {
            int depth = y - (position.getY() + tall);
            int reach = 1 - depth / 2;
            for (int x = position.getX() - reach; x <= position.getX() + reach; x++) {
                int offX = x - position.getX();
                for (int z = position.getZ() - reach; z <= position.getZ() + reach; z++) {
                    int offZ = z - position.getZ();
                    if (Math.abs(offX) == reach && Math.abs(offZ) == reach && (random.nextInt(2) == 0 || depth == 0)) { continue; }
                    if (blocked(placer.level(), new BlockPos(x, y, z))) { continue; }
                    if (placer.placeExactly(leaves, x, y, z)) { placed.add(new BlockPos(x, y, z)); }
                }
            }
        }
        return placed;
    }

    private Set<BlockPos> trunk(ContentPlacer placer, BlockPos position, int tall) {
        Set<BlockPos> placed = new HashSet<>();
        for (int y = 0; y < tall; y++) {
            BlockPos at = position.above(y);
            if (blocked(placer.level(), at)) { continue; }
            if (placer.placeExactly(log, at.getX(), at.getY(), at.getZ())) { placed.add(at); }
        }
        return placed;
    }

    private void settle(ContentPlacer placer, Set<BlockPos> canopy, Set<BlockPos> logs) {
        if (leaves == null || !leaves.hasProperty(LeavesBlock.DISTANCE)) { return; }
        Map<BlockPos, Integer> reached = new HashMap<>();
        ArrayDeque<BlockPos> open = new ArrayDeque<>();
        for (BlockPos wood : logs) {
            reached.put(wood, 0);
            open.add(wood);
        }
        while (!open.isEmpty()) {
            BlockPos at = open.poll();
            int step = reached.get(at) + 1;
            if (step >= LeavesBlock.DECAY_DISTANCE) { continue; }
            for (Direction side : Direction.values()) {
                BlockPos next = at.relative(side);
                if (!canopy.contains(next) || reached.containsKey(next)) { continue; }
                reached.put(next, step);
                open.add(next);
            }
        }
        for (BlockPos leaf : canopy) {
            int distance = reached.getOrDefault(leaf, LeavesBlock.DECAY_DISTANCE);
            placer.placeExactly(leaves.setValue(LeavesBlock.DISTANCE, distance), leaf.getX(), leaf.getY(), leaf.getZ());
        }
    }

    private static boolean blocked(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && !state.is(BlockTags.LEAVES) && !state.is(Blocks.VINE);
    }
}
