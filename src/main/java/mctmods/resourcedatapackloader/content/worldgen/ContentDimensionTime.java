package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.GameData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.clock.WorldClock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

final class ContentDimensionTime {
    private static final String OVERWORLD_CLOCK = "minecraft:overworld";
    private static final String IN_OVERWORLD = "#minecraft:in_overworld";
    private static final String TRACKS = "tracks";
    private static final String PERIOD = "period_ticks";
    private static final String KEYFRAMES = "keyframes";
    private static final String TICKS = "ticks";
    private static final String SKY_LIGHT_LEVEL = "minecraft:gameplay/sky_light_level";
    private static final Set<String> DAYLIGHT_CHECKS = Set.of("minecraft:gameplay/monsters_burn", "minecraft:gameplay/bees_stay_in_hive", "minecraft:gameplay/creaking_active", "minecraft:gameplay/eyeblossom_open", "minecraft:audio/firefly_bush_sounds");
    private static final Map<Identifier, Long> FIXED = new ConcurrentHashMap<>();

    private ContentDimensionTime() {}

    static void reset() { FIXED.clear(); }

    static long fixedTime(Holder<WorldClock> clock) {
        if (FIXED.isEmpty()) { return -1L; }
        return clock.unwrapKey().map(key -> FIXED.getOrDefault(key.identifier(), -1L)).orElse(-1L);
    }

    static void time(DimensionDef def, JsonObject type) {
        Identifier key = def.key();
        boolean fixed = def.fixedTime() >= 0;
        type.addProperty("has_fixed_time", fixed);
        type.addProperty("default_clock", fixed ? key.toString() : OVERWORLD_CLOCK);
        if (type.has("attributes")) { type.getAsJsonObject("attributes").remove(SKY_LIGHT_LEVEL); }
        type.addProperty("timelines", IN_OVERWORLD);
        long length = def.traits().dayLength();
        if (!fixed && def.fogColor() < 0 && def.skyColor() < 0 && def.sunriseColors() && length == DimensionTraitsDef.VANILLA_DAY) { return; }
        String clock = fixed ? key.toString() : OVERWORLD_CLOCK;
        JsonObject day = timeline("day", clock);
        JsonObject moon = fixed ? timeline("moon", clock) : null;
        if (day == null || (fixed && moon == null)) { return; }
        JsonObject tracks = day.getAsJsonObject(TRACKS);
        if (length != DimensionTraitsDef.VANILLA_DAY) { scale(day, tracks, length); }
        if (fixed) { DAYLIGHT_CHECKS.forEach(tracks::remove); }
        if (!def.sunriseColors()) { tracks.add("minecraft:visual/sunrise_sunset_color", constant("#00000000")); }
        String namespace = key.getNamespace();
        String path = key.getPath();
        GeneratedResources.put(PackType.SERVER_DATA, namespace, "timeline/" + path + "/day.json", day.toString());
        JsonArray values = new JsonArray();
        values.add("#minecraft:universal");
        values.add("minecraft:early_game");
        if (moon != null) {
            GeneratedResources.put(PackType.SERVER_DATA, namespace, "timeline/" + path + "/moon.json", moon.toString());
            GeneratedResources.put(PackType.SERVER_DATA, namespace, "world_clock/" + path + ".json", "{}");
            FIXED.put(key, def.fixedTime());
            values.add(namespace + ":" + path + "/moon");
        }
        else { values.add("minecraft:moon"); }
        JsonObject colors = colors(def, clock);
        if (colors != null) {
            GeneratedResources.put(PackType.SERVER_DATA, namespace, "timeline/" + path + "/colors.json", colors.toString());
            values.add(namespace + ":" + path + "/colors");
        }
        values.add(namespace + ":" + path + "/day");
        JsonObject tag = new JsonObject();
        tag.add("values", values);
        GeneratedResources.put(PackType.SERVER_DATA, namespace, "tags/timeline/" + path + ".json", tag.toString());
        type.addProperty("timelines", "#" + key);
    }

    private static void scale(JsonObject day, JsonObject tracks, long length) {
        if (!day.has(PERIOD)) { return; }
        long period = day.get(PERIOD).getAsLong();
        day.addProperty(PERIOD, length);
        for (Map.Entry<String, JsonElement> track : tracks.entrySet()) {
            if (!track.getValue().isJsonObject() || !track.getValue().getAsJsonObject().has(KEYFRAMES)) { continue; }
            JsonArray scaled = new JsonArray();
            for (JsonElement element : track.getValue().getAsJsonObject().getAsJsonArray(KEYFRAMES)) {
                JsonObject keyframe = element.getAsJsonObject();
                long ticks = keyframe.get(TICKS).getAsLong() * length / period;
                keyframe.addProperty(TICKS, ticks);
                int held = scaled.size();
                if (held >= 2 && ticks(scaled, held - 1) == ticks && ticks(scaled, held - 2) == ticks) { scaled.set(held - 1, keyframe); }
                else { scaled.add(keyframe); }
            }
            track.getValue().getAsJsonObject().add(KEYFRAMES, scaled);
        }
    }

    private static long ticks(JsonArray keyframes, int index) { return keyframes.get(index).getAsJsonObject().get(TICKS).getAsLong(); }

    static void clockItem(List<String> natural) {
        JsonArray when = new JsonArray();
        when.add(OVERWORLD_CLOCK);
        natural.stream().filter(key -> !OVERWORLD_CLOCK.equals(key)).forEach(when::add);
        if (when.size() == 1) { return; }
        JsonObject shown = new JsonObject();
        shown.add("when", when);
        shown.add("model", dial("daytime"));
        JsonArray cases = new JsonArray();
        cases.add(shown);
        JsonObject select = new JsonObject();
        select.addProperty("type", "minecraft:select");
        select.addProperty("property", "minecraft:context_dimension");
        select.add("cases", cases);
        select.add("fallback", dial("random"));
        JsonObject item = new JsonObject();
        item.add("model", select);
        GeneratedResources.put(PackType.CLIENT_RESOURCES, "minecraft", "items/clock.json", item.toString());
    }

    @Nullable private static JsonObject timeline(String name, String clock) {
        JsonObject timeline = GameData.json(Identifier.fromNamespaceAndPath("minecraft", "timeline/" + name + ".json"));
        if (timeline == null) { return null; }
        timeline.addProperty("clock", clock);
        timeline.remove("time_markers");
        if (!timeline.has(TRACKS)) { timeline.add(TRACKS, new JsonObject()); }
        return timeline;
    }

    @Nullable private static JsonObject colors(DimensionDef def, String clock) {
        JsonObject tracks = new JsonObject();
        if (def.skyColor() >= 0) { tracks.add("minecraft:visual/sky_color", constant(String.format("#%06x", def.skyColor() & 0xFFFFFF))); }
        if (def.fogColor() >= 0) { tracks.add("minecraft:visual/fog_color", constant(String.format("#%06x", def.fogColor() & 0xFFFFFF))); }
        if (tracks.isEmpty()) { return null; }
        JsonObject timeline = new JsonObject();
        timeline.addProperty("clock", clock);
        timeline.add(TRACKS, tracks);
        return timeline;
    }

    private static JsonObject constant(String value) {
        JsonObject keyframe = new JsonObject();
        keyframe.addProperty("ticks", 0);
        keyframe.addProperty("value", value);
        JsonArray keyframes = new JsonArray();
        keyframes.add(keyframe);
        JsonObject track = new JsonObject();
        track.add("keyframes", keyframes);
        return track;
    }

    private static JsonObject dial(String source) {
        JsonArray entries = new JsonArray();
        for (int face = 0; face <= 64; face++) {
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", String.format("minecraft:item/clock_%02d", face % 64));
            JsonObject entry = new JsonObject();
            entry.addProperty("threshold", face == 0 ? 0.0F : face - 0.5F);
            entry.add("model", model);
            entries.add(entry);
        }
        JsonObject dial = new JsonObject();
        dial.addProperty("type", "minecraft:range_dispatch");
        dial.addProperty("property", "minecraft:time");
        dial.addProperty("scale", 64.0F);
        dial.addProperty("source", source);
        dial.add("entries", entries);
        return dial;
    }
}
