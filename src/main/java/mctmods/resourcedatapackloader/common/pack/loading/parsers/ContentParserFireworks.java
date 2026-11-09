package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.FireworkDef;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentParserFireworks {
    private static final List<String> MODES = Arrays.asList(FireworkDef.MODE_LAUNCH, FireworkDef.MODE_BURST);
    private static final List<String> TARGETS = Arrays.asList(FireworkDef.ON_BLOCK, FireworkDef.ON_AIR, FireworkDef.ON_BOTH);
    private static final List<String> PATTERNS = Arrays.asList(FireworkDef.PATTERN_SPHERE, FireworkDef.PATTERN_RING, FireworkDef.PATTERN_FOUNTAIN, FireworkDef.PATTERN_POINTS);
    private static final List<String> SHAPES = Arrays.asList(FireworkDef.SHAPE_SMALL_BALL, FireworkDef.SHAPE_LARGE_BALL, FireworkDef.SHAPE_STAR, FireworkDef.SHAPE_CREEPER, FireworkDef.SHAPE_BURST);
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
        List<String> names = Json.names(json, member);
        int[] colors = new int[names.size()];
        for (int index = 0; index < colors.length; index++) { colors[index] = ContentTypes.color(names.get(index), owner + " " + member) & 0xFFFFFF; }
        return colors;
    }

    private static List<FireworkDef.Burst> bursts(JsonObject held, ResourceLocation key) {
        if (!held.has("burst")) { return Collections.emptyList(); }
        JsonElement element = held.get("burst");
        List<JsonElement> entries = new ArrayList<>();
        if (element.isJsonObject()) { entries.add(element); }
        else if (element.isJsonArray()) { element.getAsJsonArray().forEach(entries::add); }
        else {
            ContentLog.LOGGER.error("Item {} has a firework 'burst' that is not an object or a list, ignoring it", key);
            return Collections.emptyList();
        }
        List<FireworkDef.Burst> found = new ArrayList<>();
        for (int index = 0; index < entries.size(); index++) {
            String owner = "Item " + key + " firework burst " + index;
            if (!entries.get(index).isJsonObject()) {
                ContentLog.LOGGER.error("{} is not an object, skipping it", owner);
                continue;
            }
            FireworkDef.Burst burst = burst(entries.get(index).getAsJsonObject(), owner);
            if (burst != null) { found.add(burst); }
        }
        return Collections.unmodifiableList(found);
    }

    private static List<float[]> points(JsonObject json, String owner) {
        List<float[]> found = new ArrayList<>();
        if (!json.has("points") || !json.get("points").isJsonArray()) { return found; }
        for (JsonElement entry : json.getAsJsonArray("points")) {
            float[] point = new float[3];
            boolean usable = entry.isJsonArray() && entry.getAsJsonArray().size() == 3;
            for (int axis = 0; usable && axis < 3; axis++) {
                JsonElement value = entry.getAsJsonArray().get(axis);
                usable = value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber();
                if (usable) { point[axis] = value.getAsFloat(); }
            }
            if (usable) { found.add(point); }
            else { ContentLog.LOGGER.error("{} has a point {} that is not three numbers, skipping it", owner, entry); }
        }
        return found;
    }

    @Nullable private static FireworkDef.Burst burst(JsonObject json, String owner) {
        String particle = Json.getString(json, "particle", "").trim();
        if (particle.isEmpty()) {
            ContentLog.LOGGER.error("{} has no 'particle' item, ignoring the burst", owner);
            return null;
        }
        String pattern = Json.choice(json, "pattern", FireworkDef.PATTERN_SPHERE, owner, PATTERNS);
        List<float[]> points = points(json, owner);
        if (FireworkDef.PATTERN_POINTS.equals(pattern) && points.isEmpty()) {
            ContentLog.LOGGER.error("{} uses pattern 'points' without a non-empty 'points' list, ignoring the burst", owner);
            return null;
        }
        String sound = Json.getString(json, "sound", "").trim();
        return new FireworkDef.Burst(
                new ResourceLocation(particle),
                inRange(json, "count", FireworkDef.DEFAULT_COUNT, FireworkDef.MIN_COUNT, FireworkDef.MAX_COUNT, owner),
                Json.getFloat(json, "speed", FireworkDef.DEFAULT_SPEED),
                pattern,
                sound.isEmpty() ? null : new ResourceLocation(sound),
                inRange(json, "delay", 0, 0, FireworkDef.MAX_DELAY, owner),
                Json.ranged(json, "spread", 0.0F, 0.0F, 1.0F, owner),
                Collections.unmodifiableList(points));
    }
}
