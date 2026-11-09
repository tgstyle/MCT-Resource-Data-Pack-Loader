package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class ContentParserFireworks {
    private static final List<String> MODES = List.of(FireworkDef.MODE_LAUNCH, FireworkDef.MODE_BURST);
    private static final List<String> TARGETS = List.of(FireworkDef.ON_BLOCK, FireworkDef.ON_AIR, FireworkDef.ON_BOTH);
    private static final List<String> PATTERNS = List.of(FireworkDef.PATTERN_SPHERE, FireworkDef.PATTERN_RING, FireworkDef.PATTERN_FOUNTAIN, FireworkDef.PATTERN_POINTS);
    private static final List<String> SHAPES = List.of(FireworkDef.SHAPE_SMALL_BALL, FireworkDef.SHAPE_LARGE_BALL, FireworkDef.SHAPE_STAR, FireworkDef.SHAPE_CREEPER, FireworkDef.SHAPE_BURST);
    private static final int[] WHITE = {0xFFFFFF};

    private ContentParserFireworks() {}

    @Nullable static FireworkDef firework(ResourceLocation key, JsonObject json) {
        if (!json.has("firework")) { return null; }
        if (!json.get("firework").isJsonObject()) {
            ContentLog.LOGGER.error("Item {} has a 'firework' that is not an object, ignoring it", key);
            return null;
        }
        JsonObject held = json.getAsJsonObject("firework");
        String owner = "Item " + key + " firework";
        return new FireworkDef(
                Json.getBoolean(held, "replaceUse", false),
                Json.choice(held, "mode", FireworkDef.MODE_LAUNCH, owner, MODES),
                Json.choice(held, "on", FireworkDef.ON_BLOCK, owner, TARGETS),
                inRange(held, "flight", FireworkDef.DEFAULT_FLIGHT, FireworkDef.MIN_FLIGHT, FireworkDef.MAX_FLIGHT, owner),
                Json.getBoolean(held, "consume", true),
                Json.getBoolean(held, "dispense", true),
                explosions(held, key),
                bursts(held, key));
    }

    private static int inRange(JsonObject json, String member, int fallback, int low, int high, String owner) {
        if (!json.has(member)) { return fallback; }
        int value = Json.getInt(json, member, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so {} is used instead", owner, member, value, low, high, fallback);
        return fallback;
    }

    private static List<FireworkDef.Explosion> explosions(JsonObject held, ResourceLocation key) {
        if (!held.has("explosions")) { return Collections.emptyList(); }
        if (!held.get("explosions").isJsonArray()) {
            ContentLog.LOGGER.error("Item {} has a firework 'explosions' that is not a list, using none", key);
            return Collections.emptyList();
        }
        List<FireworkDef.Explosion> found = new ArrayList<>();
        int index = 0;
        for (JsonElement element : held.getAsJsonArray("explosions")) {
            String owner = "Item " + key + " firework explosion " + index++;
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("{} is not an object, skipping it", owner);
                continue;
            }
            JsonObject json = element.getAsJsonObject();
            int[] colors = colors(json, "colors", owner);
            found.add(new FireworkDef.Explosion(
                    Json.choice(json, "shape", FireworkDef.SHAPE_SMALL_BALL, owner, SHAPES),
                    colors.length == 0 ? WHITE : colors,
                    colors(json, "fade", owner),
                    Json.getBoolean(json, "trail", false),
                    Json.getBoolean(json, "twinkle", false)));
        }
        return Collections.unmodifiableList(found);
    }

    private static int[] colors(JsonObject json, String member, String owner) {
        List<String> names = Json.strings(json, member);
        int[] colors = new int[names.size()];
        for (int index = 0; index < colors.length; index++) { colors[index] = ContentParser.color(names.get(index), owner + " " + member); }
        return colors;
    }

    private static List<FireworkDef.Burst> bursts(JsonObject held, ResourceLocation key) {
        if (!held.has("burst")) { return Collections.emptyList(); }
        JsonElement member = held.get("burst");
        List<JsonElement> entries = new ArrayList<>();
        if (member.isJsonObject()) { entries.add(member); }
        else if (member.isJsonArray()) { member.getAsJsonArray().forEach(entries::add); }
        else {
            ContentLog.LOGGER.error("Item {} has a firework 'burst' that is not an object or a list, ignoring it", key);
            return Collections.emptyList();
        }
        List<FireworkDef.Burst> found = new ArrayList<>();
        int index = 0;
        for (JsonElement entry : entries) {
            String owner = "Item " + key + " firework burst " + index++;
            if (!entry.isJsonObject()) {
                ContentLog.LOGGER.error("{} is not an object, skipping it", owner);
                continue;
            }
            FireworkDef.Burst burst = burst(entry.getAsJsonObject(), owner);
            if (burst != null) { found.add(burst); }
        }
        return Collections.unmodifiableList(found);
    }

    private static float spread(JsonObject json, String owner) {
        if (!json.has("spread")) { return 0.0F; }
        float value = Json.getFloat(json, "spread", 0.0F);
        if (value >= 0.0F && value <= 1.0F) { return value; }
        ContentLog.LOGGER.error("{} sets spread to {}, which is outside 0 to 1, so 0 is used instead", owner, value);
        return 0.0F;
    }

    private static List<float[]> points(JsonObject json, String owner) {
        List<float[]> found = new ArrayList<>();
        if (!json.has("points")) { return found; }
        if (!json.get("points").isJsonArray()) {
            ContentLog.LOGGER.error("{} has 'points' that is not a list, using none", owner);
            return found;
        }
        for (JsonElement element : json.getAsJsonArray("points")) {
            if (!element.isJsonArray() || element.getAsJsonArray().size() != 3 || !element.getAsJsonArray().asList().stream().allMatch(part -> part.isJsonPrimitive() && part.getAsJsonPrimitive().isNumber())) {
                ContentLog.LOGGER.error("{} has a point that is not three numbers, skipping it", owner);
                continue;
            }
            JsonArray triple = element.getAsJsonArray();
            found.add(new float[] {triple.get(0).getAsFloat(), triple.get(1).getAsFloat(), triple.get(2).getAsFloat()});
        }
        return found;
    }

    @Nullable private static FireworkDef.Burst burst(JsonObject json, String owner) {
        ResourceLocation particle = ContentParser.location(Json.getString(json, "particle", ""));
        if (particle == null) {
            ContentLog.LOGGER.error("{} has no valid 'particle' item, ignoring the burst", owner);
            return null;
        }
        ResourceLocation sound = ContentParser.location(Json.getString(json, "sound", ""));
        if (sound == null && json.has("sound") && !Json.getString(json, "sound", "").isBlank()) {
            ContentLog.LOGGER.error("{} sets a 'sound' that is not a valid id, so none is played", owner);
        }
        String pattern = Json.choice(json, "pattern", FireworkDef.PATTERN_SPHERE, owner, PATTERNS);
        List<float[]> points = points(json, owner);
        if (FireworkDef.PATTERN_POINTS.equals(pattern) && points.isEmpty()) {
            ContentLog.LOGGER.error("{} uses pattern 'points' without a non-empty 'points' list, ignoring the burst", owner);
            return null;
        }
        return new FireworkDef.Burst(
                particle,
                inRange(json, "count", FireworkDef.DEFAULT_COUNT, FireworkDef.MIN_COUNT, FireworkDef.MAX_COUNT, owner),
                Json.getFloat(json, "speed", FireworkDef.DEFAULT_SPEED),
                pattern,
                sound,
                inRange(json, "delay", 0, 0, FireworkDef.MAX_DELAY, owner),
                spread(json, owner),
                Collections.unmodifiableList(points));
    }
}
