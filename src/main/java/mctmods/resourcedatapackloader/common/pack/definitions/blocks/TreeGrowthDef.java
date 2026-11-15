package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.Collections;
import java.util.List;

public final class TreeGrowthDef {
    public enum Blocked { WAIT, RESET, DROP }

    public enum GrowBy { CHANCE, TIME }

    public enum CatchUp { WORLD, PAUSE }

    public enum Bonemeal { UNSET, STEP, TIME, OFF }

    public static final TreeGrowthDef NONE = new TreeGrowthDef(Collections.emptyList(), 0, Blocked.WAIT, GrowBy.CHANCE, 0, CatchUp.WORLD, 0, Bonemeal.UNSET, 0, false, Collections.emptyList(), false, false, 1, -1.0, -1);

    public final List<TreePickDef> trees;
    public final int steps;
    public final Blocked blocked;
    public final GrowBy growBy;
    public final int stepTicks;
    public final CatchUp catchUp;
    public final int catchUpLimit;
    public final Bonemeal bonemeal;
    public final int bonemealTicks;
    public final boolean bonemealChecks;
    public final List<String> biomes;
    public final boolean needsSky;
    public final boolean needsWater;
    public final int waterRange;
    public final double chance;
    public final int light;

    public TreeGrowthDef(List<TreePickDef> trees, int steps, Blocked blocked, GrowBy growBy, int stepTicks, CatchUp catchUp, int catchUpLimit, Bonemeal bonemeal, int bonemealTicks, boolean bonemealChecks, List<String> biomes, boolean needsSky, boolean needsWater, int waterRange, double chance, int light) {
        this.trees = trees;
        this.steps = steps;
        this.blocked = blocked;
        this.growBy = growBy;
        this.stepTicks = stepTicks;
        this.catchUp = catchUp;
        this.catchUpLimit = catchUpLimit;
        this.bonemeal = bonemeal;
        this.bonemealTicks = bonemealTicks;
        this.bonemealChecks = bonemealChecks;
        this.biomes = biomes;
        this.needsSky = needsSky;
        this.needsWater = needsWater;
        this.waterRange = waterRange;
        this.chance = chance;
        this.light = light;
    }

    public boolean growing() { return !trees.isEmpty(); }

    public boolean timed() { return growBy == GrowBy.TIME; }

    public boolean decidesRandomTick() { return chance >= 0.0 || light >= 0; }
}
