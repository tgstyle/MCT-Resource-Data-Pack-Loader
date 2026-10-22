package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public record SpawnGateDef(List<String> dimensions, List<String> structures, int radius, int timeFrom, int timeTo, long dayAtLeast, String advancement, String objective, @Nullable Integer scoreAtLeast) {
    public static final SpawnGateDef NONE = new SpawnGateDef(List.of(), List.of(), 0, -1, -1, -1L, "", "", null);

    public boolean timed() { return timeFrom >= 0 && timeTo >= 0; }

    public boolean needsPlayer() { return !advancement.isEmpty() || scoreAtLeast != null; }

    public boolean any() { return !dimensions.isEmpty() || !structures.isEmpty() || timed() || dayAtLeast >= 0L || needsPlayer(); }
}
