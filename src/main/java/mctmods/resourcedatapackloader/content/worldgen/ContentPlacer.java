package mctmods.resourcedatapackloader.content.worldgen;

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
    private static final int FLAGS = 2 | 16;
    private final List<IBlockState> states;
    private final int[] ladder;
    private final int weight;
    private final Predicate<IBlockState> replaceable;
    private final Set<Block> nearby;
    private final Set<IBlockState> nearbyExact;
    private final boolean wantsNearby;

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

    public boolean place(World world, Random random, int x, int y, int z) {
        if (occupied(world, x, y, z)) { return false; }

        return world.setBlockState(new BlockPos(x, y, z), choose(random), FLAGS);
    }

    public boolean placeExactly(World world, IBlockState state, int x, int y, int z) {
        if (y < 1 || y >= world.getHeight()) { return false; }

        return world.setBlockState(new BlockPos(x, y, z), state, FLAGS);
    }

    public boolean occupied(World world, int x, int y, int z) {
        if (y < 1 || y >= world.getHeight()) { return true; }

        BlockPos pos = new BlockPos(x, y, z);
        IBlockState found = world.getBlockState(pos);
        if (!found.getBlock().isReplaceableOreGen(found, world, pos, replaceable)) { return true; }

        return wantsNearby && !beside(world, x, y, z);
    }

    private boolean beside(World world, int x, int y, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int offX = -1; offX <= 1; offX++) {
            for (int offY = -1; offY <= 1; offY++) {
                int nearY = y + offY;
                if (nearY < 0 || nearY >= world.getHeight()) { continue; }

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

    public IBlockState choose(Random random) {
        if (states.size() < 2) { return states.get(0); }

        int roll = random.nextInt(weight);
        for (int index = 0; index < ladder.length; index++) {
            if (roll < ladder[index]) { return states.get(index); }
        }
        return states.get(states.size() - 1);
    }
}
