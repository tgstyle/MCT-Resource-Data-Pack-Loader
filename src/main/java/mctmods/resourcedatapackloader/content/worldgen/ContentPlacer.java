package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.compat.CompatHandler;
import mctmods.resourcedatapackloader.util.world.BlockFlags;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class ContentPlacer {
    private final List<IBlockState> states;
    private final int[] ladder;
    private final int weight;
    private final Predicate<IBlockState> replaceable;
    private final Set<Block> nearby;
    private final Set<IBlockState> nearbyExact;
    private final boolean wantsNearby;
    private final BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();

    public ContentPlacer(List<IBlockState> states, List<Integer> weights, Set<Block> targets, Set<IBlockState> exact, Set<Block> nearby, Set<IBlockState> nearbyExact) {
        this.states = Collections.unmodifiableList(states);
        this.replaceable = state -> state != null && (targets.contains(state.getBlock()) || exact.contains(state));
        this.nearby = nearby;
        this.nearbyExact = nearbyExact;
        this.wantsNearby = !nearby.isEmpty() || !nearbyExact.isEmpty();
        int[] steps = new int[states.size()];
        int running = 0;
        for (int index = 0; index < states.size(); index++) {
            running += Math.max(1, weights.get(index));
            steps[index] = running;
        }
        this.ladder = steps;
        this.weight = Math.max(1, running);
    }

    public static int scatter(Random random, int bound) {
        if (bound <= 0) { return 0; }
        return random.nextInt(bound) - random.nextInt(bound);
    }

    public boolean place(World world, Random random, int x, int y, int z) {
        if (occupied(world, x, y, z)) { return false; }
        return world.setBlockState(new BlockPos(x, y, z), choose(random), BlockFlags.UPDATE_CLIENTS_KNOWN_SHAPE);
    }

    public boolean disc(World world, Random random, BlockPos origin, int radius, boolean round, int lowY, int highY) {
        int span = radius * radius;
        boolean placed = false;
        for (int offX = -radius; offX <= radius; offX++) {
            for (int offZ = -radius; offZ <= radius; offZ++) {
                if (round && offX * offX + offZ * offZ > span) { continue; }
                for (int y = lowY; y <= highY; y++) { placed |= place(world, random, origin.getX() + offX, y, origin.getZ() + offZ); }
            }
        }
        return placed;
    }

    public boolean placeExactly(World world, IBlockState state, int x, int y, int z) {
        if (y < floorY(world) || y >= ceilingY(world)) { return false; }
        return world.setBlockState(new BlockPos(x, y, z), state, BlockFlags.UPDATE_CLIENTS_KNOWN_SHAPE);
    }

    public boolean occupied(World world, int x, int y, int z) {
        if (!replaces(world, x, y, z)) { return true; }
        return wantsNearby && !beside(world, x, y, z);
    }

    public boolean replaces(World world, int x, int y, int z) {
        if (y < floorY(world) || y >= ceilingY(world)) { return false; }
        BlockPos pos = scratch.setPos(x, y, z);
        IBlockState found = world.getBlockState(pos);
        return found.getBlock().isReplaceableOreGen(found, world, pos, replaceable);
    }

    private boolean beside(World world, int x, int y, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int offX = -1; offX <= 1; offX++) {
            for (int offY = -1; offY <= 1; offY++) {
                int nearY = y + offY;
                if (nearY < floorY(world) - 1 || nearY >= ceilingY(world)) { continue; }
                for (int offZ = -1; offZ <= 1; offZ++) {
                    if (offX == 0 && offY == 0 && offZ == 0) { continue; }
                    pos.setPos(x + offX, nearY, z + offZ);
                    if (!ContentCascade.loaded(world, pos)) { continue; }
                    IBlockState state = world.getBlockState(pos);
                    if (nearby.contains(state.getBlock()) || nearbyExact.contains(state)) { return true; }
                }
            }
        }
        return false;
    }

    public static boolean areaLoaded(World world, int minX, int minZ, int maxX, int maxZ) {
        BlockPos.MutableBlockPos corner = new BlockPos.MutableBlockPos();
        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                if (!world.isBlockLoaded(corner.setPos(chunkX << 4, 64, chunkZ << 4))) { return false; }
            }
        }
        return true;
    }

    public static int floorY(World world) { return GenHeights.floor(world, 1); }

    public static int ceilingY(World world) { return GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT); }

    public boolean offers(IBlockState found) {
        for (IBlockState state : states) {
            if (ContentStates.alike(state, found)) { return true; }
        }
        return false;
    }

    public IBlockState choose(Random random) {
        if (states.size() < 2) { return states.get(0); }
        int roll = random.nextInt(weight);
        for (int index = 0; index < ladder.length; index++) {
            if (roll < ladder[index]) { return states.get(index); }
        }
        return states.get(states.size() - 1);
    }
}
