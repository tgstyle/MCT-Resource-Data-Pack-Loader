package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import javax.annotation.Nullable;

public record CraterDef(float chance, int minRadius, int maxRadius, int depth) {
    public static final int WIDEST = 64;

    @Nullable public static CraterDef read(JsonObject json, Identifier key) {
        if (!json.has("craters")) { return null; }
        if (!json.get("craters").isJsonObject()) {
            ContentLog.LOGGER.error("Biome {} writes craters as something other than an object, so it has no craters", key);
            return null;
        }
        JsonObject craters = json.getAsJsonObject("craters");
        String owner = "Biome " + key + " craters";
        float chance = Json.ranged(craters, "chance", 0.1F, 0.0F, 1.0F, owner);
        int smallest = (int) Json.ranged(craters, "minRadius", 8.0F, 2.0F, WIDEST, owner);
        int largest = (int) Json.ranged(craters, "maxRadius", Math.max(16, smallest), 2.0F, WIDEST, owner);
        if (largest < smallest) {
            ContentLog.LOGGER.error("{} sets maxRadius {} below minRadius {}, so every crater there has a radius of {}", owner, largest, smallest, smallest);
            largest = smallest;
        }
        int depth = (int) Json.ranged(craters, "depth", 6.0F, 1.0F, 32.0F, owner);
        return chance > 0.0F ? new CraterDef(chance, smallest, largest, depth) : null;
    }
}
