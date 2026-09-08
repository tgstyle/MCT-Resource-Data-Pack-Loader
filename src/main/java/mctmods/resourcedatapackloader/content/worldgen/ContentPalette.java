package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.Set;

public final class ContentPalette {
    private final List<BlockState> states;
    private final int[] ladder;
    private final int weight;
    private final Set<Block> targets;
    private final Set<BlockState> exact;
    private final Set<Block> nearby;
    private final Set<BlockState> nearbyExact;
    private final Set<Block> surface;
    private final boolean wantsNearby;

    public ContentPalette(List<BlockState> states, List<Integer> weights, Set<Block> targets, Set<BlockState> exact, Set<Block> nearby, Set<BlockState> nearbyExact, Set<Block> surface) {
        this.states = List.copyOf(states);
        this.targets = Set.copyOf(targets);
        this.exact = Set.copyOf(exact);
        this.nearby = Set.copyOf(nearby);
        this.nearbyExact = Set.copyOf(nearbyExact);
        this.surface = Set.copyOf(surface);
        this.wantsNearby = !nearby.isEmpty() || !nearbyExact.isEmpty();
        int[] steps = new int[this.states.size()];
        int running = 0;
        for (int index = 0; index < steps.length; index++) {
            running += Math.max(1, weights.get(index));
            steps[index] = running;
        }
        this.ladder = steps;
        this.weight = Math.max(1, running);
    }

    public BlockState choose(RandomSource random) {
        if (states.size() < 2) { return states.get(0); }
        int roll = random.nextInt(weight);
        for (int index = 0; index < ladder.length; index++) {
            if (roll < ladder[index]) { return states.get(index); }
        }
        return states.get(states.size() - 1);
    }

    public boolean places(BlockState state) { return states.contains(state); }

    public boolean replaceable(BlockState state) { return targets.contains(state.getBlock()) || exact.contains(state); }

    public boolean wantsNearby() { return wantsNearby; }

    public boolean isNearby(BlockState state) { return nearby.contains(state.getBlock()) || nearbyExact.contains(state); }

    public Set<Block> surface() { return surface; }
}
