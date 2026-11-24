package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.items.WeaponDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class ContentParserWeapons {
    private static final int DEFAULT_WINDOW = 30;
    private static final float MOST_PUSH = 100.0F;
    private static final List<String> FINISHER_KEYS = List.of("targets", "push", "health", "damage", "damageType");
    private static final List<String> PUSH_KEYS = List.of("horizontal", "vertical");
    private static final List<String> SWEEP_KEYS = List.of("shape", "reach", "angle", "width", "damage");
    private static final List<String> COMBO_KEYS = List.of("window", "steps");
    private static final List<String> STEP_KEYS = List.of("damage", "sweep");

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
        int fire = Json.getInt(json, "fireSeconds", 0);
        if (fire < 0) {
            ContentLog.LOGGER.error("The item {} sets fireSeconds to {}, which is below 0, so the key is ignored", key, fire);
            fire = 0;
        }
        WeaponDef.Finisher finisher = finisher(key, json);
        if (Float.isNaN(reach) && sweep == null && combo == null && fire == 0 && finisher == null) { return null; }
        return new WeaponDef(reach, sweep, combo, fire, finisher);
    }

    @Nullable private static WeaponDef.Finisher finisher(ResourceLocation key, JsonObject parent) {
        if (!parent.has("finisher")) { return null; }
        if (!parent.get("finisher").isJsonObject()) {
            ContentLog.LOGGER.error("The finisher on {} is not an object, so the key is ignored", key);
            return null;
        }
        JsonObject json = parent.getAsJsonObject("finisher");
        Json.unknownKeys(json, "finisher", FINISHER_KEYS);
        String owner = "The finisher on " + key;
        List<String> targets = json.has("targets") ? MobStats.entities(owner, "targets", json.get("targets")) : null;
        if (targets == null || targets.isEmpty()) {
            ContentLog.LOGGER.error("{} lists no targets, so the key is ignored", owner);
            return null;
        }
        JsonObject push = Json.object(json, "push");
        Json.unknownKeys(push, "finisher.push", PUSH_KEYS);
        return new WeaponDef.Finisher(Collections.unmodifiableList(targets),
                Json.ranged(json, "health", Float.NaN, Float.MIN_VALUE, Float.MAX_VALUE, owner),
                Json.ranged(json, "damage", Float.NaN, Float.MIN_VALUE, Float.MAX_VALUE, owner),
                Json.getString(json, "damageType", "generic").trim(),
                Json.ranged(push, "horizontal", 0.0F, 0.0F, MOST_PUSH, owner + " push"),
                Json.ranged(push, "vertical", 0.0F, 0.0F, MOST_PUSH, owner + " push"));
    }

    @Nullable private static WeaponDef.Sweep sweep(ResourceLocation key, JsonObject parent) {
        if (!parent.has("sweep")) { return null; }
        if (!parent.get("sweep").isJsonObject()) {
            ContentLog.LOGGER.error("The sweep on {} is not an object, so the key is ignored", key);
            return null;
        }
        JsonObject json = parent.getAsJsonObject("sweep");
        Json.unknownKeys(json, "sweep", SWEEP_KEYS);
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
        Json.unknownKeys(json, "combo", COMBO_KEYS);
        int window = Json.getInt(json, "window", DEFAULT_WINDOW);
        if (window < 1) {
            ContentLog.LOGGER.error("The combo on {} sets window to {}, which is below 1, so {} is used instead", key, window, DEFAULT_WINDOW);
            window = DEFAULT_WINDOW;
        }
        JsonArray array = Json.getJsonArray(json, "steps", new JsonArray());
        List<WeaponDef.Step> steps = new ArrayList<>();
        for (JsonObject step : Json.objectsOrSkip(array, "A step of the combo on {} is not an object, skipping it", key)) {
            Json.unknownKeys(step, "combo.steps", STEP_KEYS);
            steps.add(new WeaponDef.Step(Json.ranged(step, "damage", 1.0F, 0.0F, 100.0F, "A step of the combo on " + key), sweep(key, step)));
        }
        if (steps.isEmpty()) {
            ContentLog.LOGGER.error("The combo on {} has no steps, so the key is ignored", key);
            return null;
        }
        return new WeaponDef.Combo(window, Collections.unmodifiableList(steps));
    }
}
