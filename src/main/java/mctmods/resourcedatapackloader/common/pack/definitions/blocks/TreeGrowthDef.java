package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.Collections;
import java.util.List;
import net.minecraft.util.RandomSource;

public record TreeGrowthDef(List<TreePickDef> trees, int steps, Blocked blocked, GrowBy growBy, int stepTicks, CatchUp catchUp, int catchUpLimit, Bonemeal bonemeal, int bonemealTicks, boolean bonemealChecks, List<String> biomes, boolean needsSky, boolean needsWater, int waterRange, double chance, int light) {
    public enum Blocked { WAIT, RESET, DROP }

    public enum GrowBy { CHANCE, TIME }

    public enum CatchUp { WORLD, PAUSE }

    public enum Bonemeal { UNSET, STEP, TIME, OFF }

    public static final TreeGrowthDef NONE = new TreeGrowthDef(Collections.emptyList(), 0, Blocked.WAIT, GrowBy.CHANCE, 0, CatchUp.WORLD, 0, Bonemeal.UNSET, 0, false, Collections.emptyList(), false, false, 1, -1.0, -1);

    public boolean growing() { return !trees.isEmpty(); }

    public boolean timed() { return growBy == GrowBy.TIME; }

    public boolean decidesRandomTick() { return chance >= 0.0 || light >= 0; }

    public boolean rolls(RandomSource random) { return chance < 0.0 || random.nextDouble() < chance; }
}
