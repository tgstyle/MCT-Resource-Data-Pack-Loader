package mctmods.resourcedatapackloader.common.pack.definitions.games;

import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;

public record PickDef(String name, int weight) {
    public PickDef { weight = Math.max(1, weight); }

    @Nullable public static String pick(List<PickDef> choices, RandomSource random) {
        PickDef picked = WeightedPicks.pick(choices, PickDef::weight, random);
        return picked == null ? null : picked.name();
    }
}
