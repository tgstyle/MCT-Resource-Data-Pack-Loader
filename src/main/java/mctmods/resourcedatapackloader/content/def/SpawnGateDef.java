package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Standing;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import java.util.Collections;
import java.util.List;

public final class SpawnGateDef {
    public static final SpawnGateDef NONE = new SpawnGateDef(Collections.emptyList(), Collections.emptyList(), 0, -1, -1, -1L, "", Standing.NONE);
    public final List<String> dimensions;
    public final List<String> structures;
    public final int radius;
    public final int timeFrom;
    public final int timeTo;
    public final long dayAtLeast;
    public final String advancement;
    public final Standing standing;

    public SpawnGateDef(List<String> dimensions, List<String> structures, int radius, int timeFrom, int timeTo, long dayAtLeast, String advancement, Standing standing) {
        this.dimensions = dimensions;
        this.structures = structures;
        this.radius = radius;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.dayAtLeast = dayAtLeast;
        this.advancement = advancement;
        this.standing = standing;
    }

    public static SpawnGateDef read(ResourceLocation key, JsonObject json, String what) {
        Standing standing = Standing.parse(json);
        if (standing.halfScore()) {
            ContentLog.LOGGER.error("{} {} sets only one of objective and scoreAtLeast, so it leaves the score condition out", what, key);
            standing = standing.withoutScore();
        }
        SpawnGateDef gate = new SpawnGateDef(Json.strings(json, "dimensions"), Json.strings(json, "structures"), Math.max(1, Json.getInt(json, "radius", CardPlace.NEAR)),
                CardPlace.tickOf(json, "timeFrom"), CardPlace.tickOf(json, "timeTo"), json.has("dayAtLeast") ? Math.max(0L, Json.getInt(json, "dayAtLeast")) : -1L,
                Json.getString(json, "advancement", "").trim(), standing);
        return gate.any() ? gate : NONE;
    }

    public boolean timed() { return timeFrom >= 0 && timeTo >= 0; }

    public boolean needsPlayer() { return !advancement.isEmpty() || standing.any(); }

    public boolean any() { return !dimensions.isEmpty() || !structures.isEmpty() || timed() || dayAtLeast >= 0L || needsPlayer(); }
}
