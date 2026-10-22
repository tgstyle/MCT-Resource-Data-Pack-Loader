package mctmods.resourcedatapackloader.content.def;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class SpawnGateDef {
    public static final SpawnGateDef NONE = new SpawnGateDef(Collections.emptyList(), Collections.emptyList(), 0, -1, -1, -1L, "", "", null);
    public final List<String> dimensions;
    public final List<String> structures;
    public final int radius;
    public final int timeFrom;
    public final int timeTo;
    public final long dayAtLeast;
    public final String advancement;
    public final String objective;
    @Nullable public final Integer scoreAtLeast;

    public SpawnGateDef(List<String> dimensions, List<String> structures, int radius, int timeFrom, int timeTo, long dayAtLeast, String advancement, String objective, @Nullable Integer scoreAtLeast) {
        this.dimensions = dimensions;
        this.structures = structures;
        this.radius = radius;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.dayAtLeast = dayAtLeast;
        this.advancement = advancement;
        this.objective = objective;
        this.scoreAtLeast = scoreAtLeast;
    }

    public boolean timed() { return timeFrom >= 0 && timeTo >= 0; }

    public boolean needsPlayer() { return !advancement.isEmpty() || scoreAtLeast != null; }

    public boolean any() { return !dimensions.isEmpty() || !structures.isEmpty() || timed() || dayAtLeast >= 0L || needsPlayer(); }
}
