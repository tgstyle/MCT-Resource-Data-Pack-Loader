package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.WeaponDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ContentParserWeapons {
    private static final int DEFAULT_WINDOW = 30;

    private ContentParserWeapons() {}

    @Nullable static WeaponDef weapon(ResourceLocation key, JsonObject json) {
        float reach = Float.NaN;
        if (json.has("attackReach")) {
            float asked = Json.getFloat(json, "attackReach", Float.NaN);
            if (asked > 0.0F && asked <= WeaponDef.MOST_REACH) { reach = asked; }
            else { ContentLog.LOGGER.error("The item {} sets attackReach to {}, which is not above 0 and at most {}, so the key is ignored", key, asked, WeaponDef.MOST_REACH); }
        }
        WeaponDef.Sweep sweep = sweep(key, json);
        WeaponDef.Combo combo = combo(key, json);
        if (Float.isNaN(reach) && sweep == null && combo == null) { return null; }
        return new WeaponDef(reach, sweep, combo);
    }

    @Nullable private static WeaponDef.Sweep sweep(ResourceLocation key, JsonObject parent) {
        if (!parent.has("sweep")) { return null; }
        if (!parent.get("sweep").isJsonObject()) {
            ContentLog.LOGGER.error("The sweep on {} is not an object, so the key is ignored", key);
            return null;
        }
        JsonObject json = parent.getAsJsonObject("sweep");
        String named = Json.getString(json, "shape", "box").trim().toLowerCase(Locale.ROOT);
        WeaponDef.Shape shape = null;
        for (WeaponDef.Shape each : WeaponDef.Shape.values()) {
            if (each.name().toLowerCase(Locale.ROOT).equals(named)) { shape = each; }
        }
        if (shape == null) {
            ContentLog.LOGGER.error("The sweep on {} has the unknown shape '{}', so the key is ignored. The shapes are box, arc, line and circle", key, named);
            return null;
        }
        String owner = "The sweep on " + key;
        return new WeaponDef.Sweep(shape,
                Json.ranged(json, "reach", 3.0F, 0.1F, WeaponDef.MOST_REACH, owner),
                Json.ranged(json, "angle", 90.0F, 1.0F, 360.0F, owner),
                Json.ranged(json, "width", 1.0F, 0.1F, WeaponDef.MOST_REACH, owner),
                Json.ranged(json, "damage", 0.5F, 0.0F, 10.0F, owner));
    }

    @Nullable private static WeaponDef.Combo combo(ResourceLocation key, JsonObject parent) {
        if (!parent.has("combo")) { return null; }
        if (!parent.get("combo").isJsonObject()) {
            ContentLog.LOGGER.error("The combo on {} is not an object, so the key is ignored", key);
            return null;
        }
        JsonObject json = parent.getAsJsonObject("combo");
        int window = Json.getInt(json, "window", DEFAULT_WINDOW);
        if (window < 1) {
            ContentLog.LOGGER.error("The combo on {} sets window to {}, which is below 1, so {} is used instead", key, window, DEFAULT_WINDOW);
            window = DEFAULT_WINDOW;
        }
        JsonArray array = Json.getJsonArray(json, "steps", new JsonArray());
        List<WeaponDef.Step> steps = new ArrayList<>();
        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A step of the combo on {} is not an object, skipping it", key);
                continue;
            }
            JsonObject step = element.getAsJsonObject();
            steps.add(new WeaponDef.Step(Json.ranged(step, "damage", 1.0F, 0.0F, 100.0F, "A step of the combo on " + key), sweep(key, step)));
        }
        if (steps.isEmpty()) {
            ContentLog.LOGGER.error("The combo on {} has no steps, so the key is ignored", key);
            return null;
        }
        return new WeaponDef.Combo(window, Collections.unmodifiableList(steps));
    }
}
