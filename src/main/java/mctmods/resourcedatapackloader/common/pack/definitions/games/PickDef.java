package mctmods.resourcedatapackloader.common.pack.definitions.games;

import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;

import java.util.List;
import java.util.Random;

public final class PickDef {
    public final String name;
    public final int weight;

    public PickDef(String name, int weight) {
        this.name = name;
        this.weight = Math.max(1, weight);
    }

    public static String pick(List<PickDef> choices, Random random, String fallback) {
        if (choices == null || choices.isEmpty()) { return fallback; }
        return WeightedPicks.pickOr(choices, choice -> choice.weight, random, choices.get(0)).name;
    }
}
