package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.card.CardPlace;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public record SpawnGateDef(List<String> dimensions, List<String> structures, int radius, int timeFrom, int timeTo, long dayAtLeast, String advancement, String objective, @Nullable Integer scoreAtLeast) {
    public static final SpawnGateDef NONE = new SpawnGateDef(List.of(), List.of(), 0, -1, -1, -1L, "", "", null);

    public static SpawnGateDef read(Identifier key, JsonObject json, String what) {
        String objective = Json.getString(json, "objective", "").trim();
        Integer least = json.has("scoreAtLeast") ? Json.getInt(json, "scoreAtLeast") : null;
        if (objective.isEmpty() != (least == null)) {
            ContentLog.LOGGER.error("{} {} sets only one of objective and scoreAtLeast, so it leaves the score condition out", what, key);
            objective = "";
            least = null;
        }
        List<String> dimensions = new ArrayList<>();
        for (String named : Json.strings(json, "dimensions")) { dimensions.add(CardPlace.dimensionId(named)); }
        SpawnGateDef gate = new SpawnGateDef(dimensions, Json.strings(json, "structures"), Math.max(1, Json.getInt(json, "radius", CardPlace.NEAR)),
                CardPlace.tickOf(json, "timeFrom"), CardPlace.tickOf(json, "timeTo"), json.has("dayAtLeast") ? Math.max(0L, Json.getInt(json, "dayAtLeast")) : -1L,
                Json.getString(json, "advancement", "").trim(), objective, least);
        return gate.any() ? gate : NONE;
    }

    public boolean timed() { return timeFrom >= 0 && timeTo >= 0; }

    public boolean needsPlayer() { return !advancement.isEmpty() || scoreAtLeast != null; }

    public boolean any() { return !dimensions.isEmpty() || !structures.isEmpty() || timed() || dayAtLeast >= 0L || needsPlayer(); }
}
