package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.SkyTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentEnvironmentLayers {
    private static final JsonElement NO_SUNRISE = new JsonPrimitive("#00000000");
    private static final String FOG_COLOR = "minecraft:visual/fog_color";
    private static final Map<Identifier, Long> VANILLA_FIXED_TIME = Map.of(Level.NETHER.identifier(), 18000L, Level.END.identifier(), 6000L);

    private ContentEnvironmentLayers() {}

    public static void beforeBiomes(EnvironmentAttributeSystem.Builder builder, Level level) {
        Identifier key = level.dimension().identifier();
        DimensionDef base = ContentDimensions.base(key);
        if (base == null || ContentEnvironments.names(key).isEmpty()) { return; }
        JsonObject vanilla = ContentDimensions.vanillaAttributes(key);
        Set<String> written = ContentDimensions.visuals(base).keySet();
        Map<String, Map<DimensionDef, JsonElement>> dropped = new LinkedHashMap<>();
        for (DimensionDef set : ContentEnvironments.sets(key)) {
            JsonObject own = ContentDimensions.visuals(set);
            for (String name : written) {
                if (!own.has(name)) { dropped.computeIfAbsent(name, _ -> new IdentityHashMap<>()).put(set, vanilla.has(name) ? vanilla.get(name) : JsonNull.INSTANCE); }
            }
        }
        layer(builder, key, dropped);
    }

    public static void afterBiomes(EnvironmentAttributeSystem.Builder builder, Level level) {
        Identifier key = level.dimension().identifier();
        DimensionDef base = ContentDimensions.base(key);
        if (base == null) { return; }
        long time = unlitTime(level, base);
        Map<String, Map<DimensionDef, JsonElement>> given = new LinkedHashMap<>();
        for (DimensionDef def : defs(key, base)) {
            for (Map.Entry<String, JsonElement> entry : ContentDimensions.visuals(def).entrySet()) {
                JsonElement value = time >= 0 && FOG_COLOR.equals(entry.getKey()) ? lit(entry.getValue(), time) : entry.getValue();
                given.computeIfAbsent(entry.getKey(), _ -> new IdentityHashMap<>()).put(def, value);
            }
        }
        layer(builder, key, given);
    }

    private static long unlitTime(Level level, DimensionDef base) {
        if (level.dimensionType().timelines().stream().anyMatch(timeline -> timeline.value().attributes().contains(EnvironmentAttributes.FOG_COLOR))) { return -1L; }
        if (base.fixedTime() >= 0) { return base.fixedTime(); }
        return VANILLA_FIXED_TIME.getOrDefault(level.dimension().identifier(), -1L);
    }

    private static JsonElement lit(JsonElement color, long time) {
        float daylight = (float) SkyTime.daylight(time);
        float shade = daylight * 0.94F + 0.06F;
        int rgb = ARGB.scaleRGB(Integer.parseInt(color.getAsString().substring(1), 16), shade, shade, daylight * 0.91F + 0.09F);
        return new JsonPrimitive(String.format("#%06x", rgb & 0xFFFFFF));
    }

    public static void afterTimelines(EnvironmentAttributeSystem.Builder builder, Level level) {
        Identifier key = level.dimension().identifier();
        DimensionDef base = ContentDimensions.base(key);
        if (base == null) { return; }
        Map<DimensionDef, JsonElement> hidden = new IdentityHashMap<>();
        for (DimensionDef def : defs(key, base)) {
            if (!def.sunriseColors()) { hidden.put(def, NO_SUNRISE); }
        }
        if (!hidden.isEmpty()) { add(builder, EnvironmentAttributes.SUNRISE_SUNSET_COLOR, key, hidden); }
    }

    private static List<DimensionDef> defs(Identifier key, DimensionDef base) {
        List<DimensionDef> defs = new ArrayList<>();
        defs.add(base);
        defs.addAll(ContentEnvironments.sets(key));
        return defs;
    }

    private static void layer(EnvironmentAttributeSystem.Builder builder, Identifier key, Map<String, Map<DimensionDef, JsonElement>> changes) {
        for (Map.Entry<String, Map<DimensionDef, JsonElement>> change : changes.entrySet()) {
            EnvironmentAttribute<?> attribute = EnvironmentAttributes.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(change.getKey())).result().orElse(null);
            if (attribute == null) { ContentLog.LOGGER.error("Dimension {} changes {}, which is not an environment attribute, so it stays as it is", key, change.getKey()); }
            else { add(builder, attribute, key, change.getValue()); }
        }
    }

    private static <V> void add(EnvironmentAttributeSystem.Builder builder, EnvironmentAttribute<V> attribute, Identifier key, Map<DimensionDef, JsonElement> changes) {
        Map<DimensionDef, V> values = new IdentityHashMap<>();
        for (Map.Entry<DimensionDef, JsonElement> change : changes.entrySet()) {
            if (change.getValue().isJsonNull()) { values.put(change.getKey(), attribute.defaultValue()); }
            else { attribute.valueCodec().parse(JsonOps.INSTANCE, change.getValue()).resultOrPartial(error -> ContentLog.LOGGER.error("Dimension {} has a {} that could not be read: {}", key, attribute, error)).ifPresent(value -> values.put(change.getKey(), value)); }
        }
        if (!values.isEmpty()) { builder.addTimeBasedLayer(attribute, (value, _) -> values.getOrDefault(ContentDimensions.def(key), value)); }
    }
}
