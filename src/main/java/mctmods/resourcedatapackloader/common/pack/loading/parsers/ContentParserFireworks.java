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

final class ContentParserFireworks {
    private static final String FIREWORK = "firework";
    private static final int WHITE = 0xFFFFFF;

    private ContentParserFireworks() {}

    @Nullable static FireworkDef firework(ResourceLocation key, JsonObject json) {
        if (!json.has(FIREWORK)) { return null; }
        JsonObject firework = Json.getJsonObject(json, FIREWORK, new JsonObject());
        String owner = "Item " + key + " " + FIREWORK;
        List<FireworkDef.Explosion> explosions = new ArrayList<>();
        JsonArray list = Json.getJsonArray(firework, "explosions", new JsonArray());
        for (int index = 0; index < list.size(); index++) {
            JsonElement entry = list.get(index);
            if (!entry.isJsonObject()) {
                ContentLog.LOGGER.error("{} explosion {} is not an object, so it is skipped", owner, index);
                continue;
            }
            explosions.add(explosion(entry.getAsJsonObject(), owner + " explosion " + index));
        }
        return new FireworkDef(Json.getBoolean(firework, "replaceUse", false),
                Json.choice(firework, "mode", FireworkDef.MODE_LAUNCH, owner, List.of(FireworkDef.MODE_LAUNCH, FireworkDef.MODE_BURST)),
                Json.choice(firework, "on", FireworkDef.ON_BLOCK, owner, List.of(FireworkDef.ON_BLOCK, FireworkDef.ON_AIR, FireworkDef.ON_BOTH)),
                bounded(firework, "flight", FireworkDef.DEFAULT_FLIGHT, FireworkDef.MIN_FLIGHT, FireworkDef.MAX_FLIGHT, owner),
                Json.getBoolean(firework, "consume", true), Json.getBoolean(firework, "dispense", true), Collections.unmodifiableList(explosions), bursts(firework, owner));
    }

    private static FireworkDef.Explosion explosion(JsonObject json, String owner) {
        String shape = Json.choice(json, "shape", FireworkDef.SHAPE_SMALL_BALL, owner, List.of(FireworkDef.SHAPE_SMALL_BALL, FireworkDef.SHAPE_LARGE_BALL, FireworkDef.SHAPE_STAR, FireworkDef.SHAPE_CREEPER, FireworkDef.SHAPE_BURST));
        int[] colors = colors(json, "colors", owner);
        if (colors.length == 0) { colors = new int[] {WHITE}; }
        return new FireworkDef.Explosion(shape, colors, colors(json, "fade", owner), Json.getBoolean(json, "trail", false), Json.getBoolean(json, "twinkle", false));
    }

    private static int[] colors(JsonObject json, String member, String owner) {
        if (!json.has(member)) { return new int[0]; }
        JsonArray list = Json.getJsonArray(json, member, new JsonArray());
        List<Integer> found = new ArrayList<>();
        for (JsonElement element : list) {
            String text = Json.asString(element, member, "").trim();
            if (text.startsWith("#")) { text = text.substring(1); }
            try {
                found.add(Integer.parseInt(text, 16) & WHITE);
            } catch (NumberFormatException failure) {
                ContentLog.LOGGER.error("{} lists '{}' in {}, which is not a hex color, so it is skipped", owner, text, member);
            }
        }
        return found.stream().mapToInt(Integer::intValue).toArray();
    }

    private static List<FireworkDef.Burst> bursts(JsonObject firework, String owner) {
        List<FireworkDef.Burst> found = new ArrayList<>();
        if (!firework.has("burst")) { return Collections.unmodifiableList(found); }
        JsonElement raw = firework.get("burst");
        List<JsonElement> entries = new ArrayList<>();
        if (raw.isJsonArray()) { raw.getAsJsonArray().forEach(entries::add); } else { entries.add(raw); }
        for (int index = 0; index < entries.size(); index++) {
            String where = owner + " burst " + index;
            if (!entries.get(index).isJsonObject()) {
                ContentLog.LOGGER.error("{} is not an object, so it is skipped", where);
                continue;
            }
            FireworkDef.Burst burst = burst(entries.get(index).getAsJsonObject(), where);
            if (burst != null) { found.add(burst); }
        }
        return Collections.unmodifiableList(found);
    }

    private static List<float[]> points(JsonObject json, String where) {
        List<float[]> found = new ArrayList<>();
        JsonArray list = Json.getJsonArray(json, "points", new JsonArray());
        for (JsonElement entry : list) {
            if (!entry.isJsonArray() || entry.getAsJsonArray().size() != 3) {
                ContentLog.LOGGER.error("{} has a point that is not an [x, y, z] triple, so it is skipped", where);
                continue;
            }
            JsonArray triple = entry.getAsJsonArray();
            found.add(new float[] {Json.asFloat(triple.get(0), "points", 0.0F), Json.asFloat(triple.get(1), "points", 0.0F), Json.asFloat(triple.get(2), "points", 0.0F)});
        }
        return found;
    }

    @Nullable private static FireworkDef.Burst burst(JsonObject json, String where) {
        ResourceLocation particle = ResourceLocation.tryParse(Json.getString(json, "particle", "").trim());
        if (particle == null) {
            ContentLog.LOGGER.error("{} has no valid particle item id, so the burst is ignored", where);
            return null;
        }
        String soundText = Json.getString(json, "sound", "").trim();
        ResourceLocation sound = soundText.isEmpty() ? null : ResourceLocation.tryParse(soundText);
        if (!soundText.isEmpty() && sound == null) { ContentLog.LOGGER.error("{} sound '{}' is not a valid id, so there is no sound", where, soundText); }
        String pattern = Json.choice(json, "pattern", FireworkDef.PATTERN_SPHERE, where, List.of(FireworkDef.PATTERN_SPHERE, FireworkDef.PATTERN_RING, FireworkDef.PATTERN_FOUNTAIN, FireworkDef.PATTERN_POINTS));
        List<float[]> points = FireworkDef.PATTERN_POINTS.equals(pattern) ? points(json, where) : List.of();
        if (FireworkDef.PATTERN_POINTS.equals(pattern) && points.isEmpty()) {
            ContentLog.LOGGER.error("{} uses pattern points without a non-empty points list, so the burst is ignored", where);
            return null;
        }
        return new FireworkDef.Burst(particle, bounded(json, "count", FireworkDef.DEFAULT_COUNT, FireworkDef.MIN_COUNT, FireworkDef.MAX_COUNT, where),
                Json.getFloat(json, "speed", FireworkDef.DEFAULT_SPEED), pattern, sound,
                bounded(json, "delay", FireworkDef.MIN_DELAY, FireworkDef.MIN_DELAY, FireworkDef.MAX_DELAY, where),
                Json.ranged(json, "spread", FireworkDef.MIN_SPREAD, FireworkDef.MIN_SPREAD, FireworkDef.MAX_SPREAD, where), Collections.unmodifiableList(points));
    }

    private static int bounded(JsonObject json, String member, int fallback, int low, int high, String owner) {
        int value = Json.getInt(json, member, fallback);
        if (value >= low && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside {} to {}, so {} is used instead", owner, member, value, low, high, fallback);
        return fallback;
    }
}
