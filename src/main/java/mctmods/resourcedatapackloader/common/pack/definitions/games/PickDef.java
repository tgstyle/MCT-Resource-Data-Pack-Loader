package mctmods.resourcedatapackloader.common.pack.definitions.games;

import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;

public record PickDef(String name, int weight) {
    public PickDef { weight = Math.max(1, weight); }

    public static String pick(List<PickDef> choices, Random random, String fallback) {
        if (choices == null || choices.isEmpty()) { return fallback; }
        return WeightedPicks.pickOr(choices, PickDef::weight, random, choices.get(0)).name();
    }

    @Nullable public static String pick(List<PickDef> choices, RandomSource random) {
        PickDef picked = WeightedPicks.pick(choices, PickDef::weight, random);
        return picked == null ? null : picked.name();
    }
}
