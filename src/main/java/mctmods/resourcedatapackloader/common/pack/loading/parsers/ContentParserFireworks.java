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
import net.minecraft.resources.Identifier;

final class ContentParserFireworks {
    private static final String FIREWORK = "firework";
    private static final String COLORS = "colors";
    private static final String FADE = "fade";
    private static final String BURST = "burst";
    private static final int WHITE = 0xFFFFFF;
    private static final List<String> MODES = List.of(FireworkDef.MODE_LAUNCH, FireworkDef.MODE_BURST);
    private static final List<String> TARGETS = List.of(FireworkDef.ON_BLOCK, FireworkDef.ON_AIR, FireworkDef.ON_BOTH);
    private static final List<String> SHAPES = List.of(FireworkDef.SHAPE_SMALL_BALL, FireworkDef.SHAPE_LARGE_BALL, FireworkDef.SHAPE_STAR, FireworkDef.SHAPE_CREEPER, FireworkDef.SHAPE_BURST);
    private static final List<String> PATTERNS = List.of(FireworkDef.PATTERN_SPHERE, FireworkDef.PATTERN_RING, FireworkDef.PATTERN_FOUNTAIN, FireworkDef.PATTERN_POINTS);

    private ContentParserFireworks() {}

    @Nullable static FireworkDef firework(Identifier key, JsonObject json) {
        if (!json.has(FIREWORK)) { return null; }
        JsonObject firework = Json.getJsonObject(json, FIREWORK, new JsonObject());
        String owner = "Item " + key + " " + FIREWORK;
        int flight = Json.getInt(firework, "flight", FireworkDef.DEFAULT_FLIGHT);
        if (flight < FireworkDef.MIN_FLIGHT || flight > FireworkDef.MAX_FLIGHT) {
            ContentLog.LOGGER.error("{} sets flight to {}, which is outside {} to {}, so {} is used instead", owner, flight, FireworkDef.MIN_FLIGHT, FireworkDef.MAX_FLIGHT, FireworkDef.DEFAULT_FLIGHT);
            flight = FireworkDef.DEFAULT_FLIGHT;
        }
        return new FireworkDef(Json.getBoolean(firework, "replaceUse", false), Json.choice(firework, "mode", FireworkDef.MODE_LAUNCH, owner, MODES),
                Json.choice(firework, "on", FireworkDef.ON_BLOCK, owner, TARGETS), flight, Json.getBoolean(firework, "consume", true),
                Json.getBoolean(firework, "dispense", true), explosions(firework, owner), bursts(firework, owner));
    }

    private static List<FireworkDef.Explosion> explosions(JsonObject firework, String owner) {
        List<FireworkDef.Explosion> explosions = new ArrayList<>();
        JsonArray list = Json.getJsonArray(firework, "explosions", new JsonArray());
        for (JsonElement entry : list) {
            if (!entry.isJsonObject()) {
                ContentLog.LOGGER.error("{} has an explosions entry {} that is not an object, so it is skipped", owner, entry);
                continue;
            }
            JsonObject explosion = entry.getAsJsonObject();
            int[] colors = colors(explosion, COLORS, owner);
            explosions.add(new FireworkDef.Explosion(Json.choice(explosion, "shape", FireworkDef.SHAPE_SMALL_BALL, owner, SHAPES),
                    colors.length == 0 ? new int[]{WHITE} : colors, colors(explosion, FADE, owner),
                    Json.getBoolean(explosion, "trail", false), Json.getBoolean(explosion, "twinkle", false)));
        }
        return Collections.unmodifiableList(explosions);
    }

    private static int[] colors(JsonObject explosion, String member, String owner) {
        List<Integer> colors = new ArrayList<>();
        for (JsonElement entry : Json.getJsonArray(explosion, member, new JsonArray())) {
            String text = entry.isJsonPrimitive() ? entry.getAsString().trim() : "";
            if (text.startsWith("#")) { text = text.substring(1); }
            try {
                int color = Integer.parseInt(text, 16);
                if (text.length() > 6 || color < 0) { throw new NumberFormatException(text); }
                colors.add(color);
            } catch (NumberFormatException bad) {
                ContentLog.LOGGER.error("{} gives {} as a {} entry, which is not a hex color, so it is skipped", owner, entry, member);
            }
        }
        return colors.stream().mapToInt(Integer::intValue).toArray();
    }

    private static List<FireworkDef.Burst> bursts(JsonObject firework, String owner) {
        List<FireworkDef.Burst> bursts = new ArrayList<>();
        if (!firework.has(BURST)) { return bursts; }
        JsonElement raw = firework.get(BURST);
        if (raw.isJsonObject()) {
            addBurst(bursts, raw.getAsJsonObject(), owner);
        } else if (raw.isJsonArray()) {
            for (JsonElement entry : raw.getAsJsonArray()) {
                if (entry.isJsonObject()) { addBurst(bursts, entry.getAsJsonObject(), owner); }
                else { ContentLog.LOGGER.error("{} has a burst entry {} that is not an object, so it is skipped", owner, entry); }
            }
        } else {
            ContentLog.LOGGER.error("{} has a burst that is neither an object nor a list, so it is ignored", owner);
        }
        return Collections.unmodifiableList(bursts);
    }

    private static void addBurst(List<FireworkDef.Burst> bursts, JsonObject burst, String owner) {
        FireworkDef.Burst parsed = burst(burst, owner);
        if (parsed != null) { bursts.add(parsed); }
    }

    private static List<float[]> points(JsonObject burst, String owner) {
        List<float[]> points = new ArrayList<>();
        for (JsonElement entry : Json.getJsonArray(burst, FireworkDef.PATTERN_POINTS, new JsonArray())) {
            if (!entry.isJsonArray() || entry.getAsJsonArray().size() != 3) {
                ContentLog.LOGGER.error("{} gives {} as a points entry, which is not an [x, y, z] triple, so it is skipped", owner, entry);
                continue;
            }
            JsonArray triple = entry.getAsJsonArray();
            points.add(new float[]{Json.asFloat(triple.get(0), "points", 0.0F), Json.asFloat(triple.get(1), "points", 0.0F), Json.asFloat(triple.get(2), "points", 0.0F)});
        }
        return points;
    }

    @Nullable private static FireworkDef.Burst burst(JsonObject burst, String owner) {
        Identifier particle = id(burst, "particle", owner);
        if (particle == null) {
            ContentLog.LOGGER.error("{} has a burst without a usable particle, so the burst is ignored", owner);
            return null;
        }
        int count = Json.getInt(burst, "count", FireworkDef.DEFAULT_COUNT);
        if (count < FireworkDef.MIN_COUNT || count > FireworkDef.MAX_COUNT) {
            ContentLog.LOGGER.error("{} sets burst count to {}, which is outside {} to {}, so {} is used instead", owner, count, FireworkDef.MIN_COUNT, FireworkDef.MAX_COUNT, FireworkDef.DEFAULT_COUNT);
            count = FireworkDef.DEFAULT_COUNT;
        }
        String pattern = Json.choice(burst, "pattern", FireworkDef.PATTERN_SPHERE, owner, PATTERNS);
        List<float[]> points = points(burst, owner);
        if (FireworkDef.PATTERN_POINTS.equals(pattern) && points.isEmpty()) {
            ContentLog.LOGGER.error("{} has a points burst without a non-empty points list, so the burst is ignored", owner);
            return null;
        }
        return new FireworkDef.Burst(particle, count, Json.getFloat(burst, "speed", FireworkDef.DEFAULT_SPEED), pattern, id(burst, "sound", owner),
                (int) Json.ranged(burst, "delay", 0, FireworkDef.MIN_DELAY, FireworkDef.MAX_DELAY, owner),
                Json.ranged(burst, "spread", 0.0F, FireworkDef.MIN_SPREAD, FireworkDef.MAX_SPREAD, owner), Collections.unmodifiableList(points));
    }

    @Nullable private static Identifier id(JsonObject json, String member, String owner) {
        String text = Json.getString(json, member, "").trim();
        if (text.isEmpty()) { return null; }
        Identifier id = Identifier.tryParse(text);
        if (id == null) { ContentLog.LOGGER.error("{} sets {} to '{}', which is not an id", owner, member, text); }
        return id;
    }
}
