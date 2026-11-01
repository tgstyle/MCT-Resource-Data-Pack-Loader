package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;

public record FollowDef(String name, int weight, int spread, @Nullable AmountDef depth) {
    public static final String EMPTY = "empty";

    public FollowDef { weight = Math.max(1, weight); }

    public int spreadOr(int fallback) { return spread >= 0 ? spread : fallback; }

    public int depthOr(AmountDef fallback, RandomSource random) { return (depth != null ? depth : fallback).pick(random); }

    @Nullable public static FollowDef pick(List<FollowDef> choices, RandomSource random) { return WeightedPicks.pick(choices, FollowDef::weight, random); }
}
