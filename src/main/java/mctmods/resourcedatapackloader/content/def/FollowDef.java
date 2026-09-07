package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class FollowDef {
    public final String name;
    public final int weight;
    public final int spread;
    @Nullable public final AmountDef depth;

    public FollowDef(String name, int weight, int spread, @Nullable AmountDef depth) {
        this.name = name;
        this.weight = Math.max(1, weight);
        this.spread = spread;
        this.depth = depth;
    }

    public int spreadOr(int fallback) { return spread >= 0 ? spread : fallback; }

    public int depthOr(AmountDef fallback, Random random) { return (depth != null ? depth : fallback).pick(random); }

    @Nullable public static FollowDef pick(List<FollowDef> choices, Random random) {
        int total = 0;
        for (FollowDef choice : choices) { total += choice.weight; }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (FollowDef choice : choices) {
            roll -= choice.weight;
            if (roll < 0) { return choice; }
        }
        return null;
    }
}
