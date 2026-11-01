package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;

public record SpawnGateDef(List<String> dimensions, List<String> structures, int radius, int timeFrom, int timeTo, long dayAtLeast, String advancement, Standing standing) {
    public static final SpawnGateDef NONE = new SpawnGateDef(List.of(), List.of(), 0, -1, -1, -1L, "", Standing.NONE);

    public static SpawnGateDef read(Identifier key, JsonObject json, String what) {
        Standing standing = Standing.parse(json);
        if (standing.halfScore()) {
            ContentLog.LOGGER.error("{} {} sets only one of objective and scoreAtLeast, so it leaves the score condition out", what, key);
            standing = standing.withoutScore();
        }
        List<String> dimensions = new ArrayList<>();
        for (String named : Json.strings(json, "dimensions")) { dimensions.add(CardPlace.dimensionId(named)); }
        SpawnGateDef gate = new SpawnGateDef(dimensions, Json.strings(json, "structures"), Math.max(1, Json.getInt(json, "radius", CardPlace.NEAR)),
                CardPlace.tickOf(json, "timeFrom"), CardPlace.tickOf(json, "timeTo"), json.has("dayAtLeast") ? Math.max(0L, Json.getInt(json, "dayAtLeast")) : -1L,
                Json.getString(json, "advancement", "").trim(), standing);
        return gate.any() ? gate : NONE;
    }

    public boolean timed() { return timeFrom >= 0 && timeTo >= 0; }

    public boolean needsPlayer() { return !advancement.isEmpty() || standing.any(); }

    public boolean any() { return !dimensions.isEmpty() || !structures.isEmpty() || timed() || dayAtLeast >= 0L || needsPlayer(); }
}
