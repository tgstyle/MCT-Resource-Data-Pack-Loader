package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.RandomSource;
import java.util.List;
import javax.annotation.Nullable;

public record FollowDef(String name, int weight, int spread, @Nullable AmountDef depth) {
    public static final String EMPTY = "empty";

    public int spreadOr(int fallback) { return spread >= 0 ? spread : fallback; }

    public int depthOr(AmountDef fallback, RandomSource random) { return (depth != null ? depth : fallback).pick(random); }

    @Nullable public static FollowDef pick(List<FollowDef> choices, RandomSource random) {
        int total = 0;
        for (FollowDef choice : choices) { total += Math.max(1, choice.weight()); }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (FollowDef choice : choices) {
            roll -= Math.max(1, choice.weight());
            if (roll < 0) { return choice; }
        }
        return null;
    }
}
