package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidBehaviorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidReactionDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public final class ContentParserFluids {
    public static final int MAX_SPREAD = 15;
    public static final int MODERN_SPREAD = 7;
    public static final int MIN_INTERVAL = 20;
    private static final int DAMAGE_EVERY = 20;
    private static final float LARGE = 1.0E7F;
    private static boolean clamped;

    private ContentParserFluids() {}

    public static FluidBehaviorDef behavior(ResourceLocation key, JsonObject block) {
        FluidBehaviorDef.Creatures creatures = creatures(key, Json.object(block, "creatures"));
        FluidBehaviorDef.Items items = items(key, Json.object(block, "items"));
        List<FluidReactionDef> reactions = reactions(key, block, false);
        List<FluidBehaviorDef.Row> rows = rows(key, block);
        int flowTicks = whole(block, "flowTicks", 0, key);
        int spread = spread(block, key);
        boolean infinite = Json.getBoolean(block, "infinite", false);
        if (flowTicks == 0 && spread == 0 && !infinite && rows.isEmpty() && reactions.isEmpty() && creatures == FluidBehaviorDef.Creatures.NONE && items == FluidBehaviorDef.Items.NONE) { return FluidBehaviorDef.NONE; }
        return new FluidBehaviorDef(flowTicks, spread, infinite, rows, creatures, items, reactions);
    }

    public static List<FluidReactionDef> reactions(Object key, JsonObject owner, boolean vanilla) {
        if (!owner.has("reactions")) { return Collections.emptyList(); }
        JsonArray array = Json.getJsonArray(owner, "reactions", new JsonArray());
        List<FluidReactionDef> found = new ArrayList<>();
        int index = 0;
        for (JsonElement element : array) {
            index++;
            String label = key + " reaction " + index;
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("{} is not an object, so it is dropped", label);
                continue;
            }
            FluidReactionDef rule = reaction(label, element.getAsJsonObject(), vanilla);
            if (rule != null) { found.add(rule); }
        }
        return Collections.unmodifiableList(found);
    }

    @Nullable private static FluidReactionDef reaction(String label, JsonObject json, boolean vanilla) {
        List<String> touching = touching(json);
        if (touching.isEmpty()) {
            ContentLog.LOGGER.error("{} names nothing under touching, so it is dropped", label);
            return null;
        }
        int interval = interval(json, label, vanilla);
        FluidReactionDef.Explosion explosion = explosion(json, label);
        String becomes = Json.text(json, "becomes", "");
        String source = Json.text(json, "source", "");
        String flowing = Json.text(json, "flowing", "");
        String sound = Json.text(json, "sound", "");
        String particles = Json.text(json, "particles", "");
        String function = Json.text(json, "function", "");
        if (becomes.isEmpty() && source.isEmpty() && flowing.isEmpty() && explosion == null && sound.isEmpty() && particles.isEmpty() && function.isEmpty()) {
            ContentLog.LOGGER.error("{} has no becomes, source, flowing, explosion, sound, particles or function, so it is dropped", label);
            return null;
        }
        return new FluidReactionDef(Collections.unmodifiableList(touching), faces(json, label), Json.ranged(json, "chance", 1.0F, 0.0F, 1.0F, label), interval,
                becomes, Json.getBoolean(json, "drops", false), source, flowing, explosion, sound, particles, function);
    }

    private static List<String> touching(JsonObject json) {
        if (!json.has("touching")) { return new ArrayList<>(); }
        JsonElement element = json.get("touching");
        if (element.isJsonArray()) { return Json.names(json, "touching"); }
        List<String> one = new ArrayList<>();
        String named = Json.text(json, "touching", "");
        if (!named.isEmpty()) { one.add(named); }
        return one;
    }

    private static int interval(JsonObject json, String label, boolean vanilla) {
        if (!json.has("interval")) { return 0; }
        if (vanilla) {
            ContentLog.LOGGER.error("{} sets interval on a vanilla fluid, which cannot be checked on a timer, so the interval is ignored", label);
            return 0;
        }
        int asked = Json.getInt(json, "interval", 0);
        if (asked == 0 || asked >= MIN_INTERVAL) { return asked; }
        ContentLog.LOGGER.error("{} sets interval to {}, which is below {}, so it is off", label, asked, MIN_INTERVAL);
        return 0;
    }

    private static List<Direction> faces(JsonObject json, String label) {
        List<Direction> found = new ArrayList<>();
        for (String name : Json.names(json, "faces")) {
            Direction face = Direction.byName(name.toLowerCase(Locale.ROOT));
            if (face == null) { ContentLog.LOGGER.error("{} names the face '{}', which is not down, up, north, south, west or east, so it is skipped", label, name); }
            else if (!found.contains(face)) { found.add(face); }
        }
        if (found.isEmpty()) { Collections.addAll(found, Direction.values()); }
        return Collections.unmodifiableList(found);
    }

    @Nullable private static FluidReactionDef.Explosion explosion(JsonObject json, String label) {
        if (!json.has("explosion")) { return null; }
        JsonObject held = Json.object(json, "explosion");
        float power = Json.getFloat(held, "power", 0.0F);
        if (power <= 0.0F) {
            ContentLog.LOGGER.error("{} has an explosion with no power above zero, so the explosion is dropped", label);
            return null;
        }
        String at = Json.choice(held, "at", "touching", label + " explosion", Arrays.asList("touching", "self"));
        return new FluidReactionDef.Explosion(power, Json.getBoolean(held, "fire", false), Json.getBoolean(held, "grief", true), "self".equals(at));
    }

    private static List<FluidBehaviorDef.Row> rows(ResourceLocation key, JsonObject block) {
        if (!block.has("byDimension")) { return Collections.emptyList(); }
        List<FluidBehaviorDef.Row> found = new ArrayList<>();
        int index = 0;
        for (JsonElement element : Json.getJsonArray(block, "byDimension", new JsonArray())) {
            index++;
            String label = key + " byDimension row " + index;
            JsonObject row = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
            List<String> dimensions = Json.names(row, "dimensions");
            if (dimensions.isEmpty()) {
                ContentLog.LOGGER.error("{} names no dimensions, so it is dropped", label);
                continue;
            }
            found.add(new FluidBehaviorDef.Row(Collections.unmodifiableList(dimensions), whole(row, "flowTicks", 0, label), spread(row, label),
                    row.has("infinite") ? Json.getBoolean(row, "infinite", false) : null));
        }
        return Collections.unmodifiableList(found);
    }

    private static FluidBehaviorDef.Creatures creatures(ResourceLocation key, JsonObject json) {
        if (json.entrySet().isEmpty()) { return FluidBehaviorDef.Creatures.NONE; }
        String label = key + " creatures";
        return new FluidBehaviorDef.Creatures(decimal(json, "damage", 0.0F, LARGE, label), whole(json, "damageEvery", DAMAGE_EVERY, label),
                Json.getString(json, "damageType", "generic").trim(), json.has("fireSeconds") ? Math.max(0, Json.getInt(json, "fireSeconds", 0)) : null,
                flag(json, "drown"), flag(json, "swim"), decimal(json, "push", 0.0F, LARGE, label), decimal(json, "drag", 0.0F, 1.0F, label));
    }

    private static FluidBehaviorDef.Items items(ResourceLocation key, JsonObject json) {
        if (json.entrySet().isEmpty()) { return FluidBehaviorDef.Items.NONE; }
        String label = key + " items";
        return new FluidBehaviorDef.Items(flag(json, "burn"), Json.ranged(json, "dissolve", 0.0F, 0.0F, 1.0F, label), Json.names(json, "immune"),
                decimal(json, "drift", -LARGE, LARGE, label), decimal(json, "buoyancy", -LARGE, LARGE, label));
    }

    @Nullable private static Boolean flag(JsonObject json, String member) { return json.has(member) ? Json.getBoolean(json, member, false) : null; }

    @Nullable private static Float decimal(JsonObject json, String member, float low, float high, Object owner) {
        float value = Json.bounded(json, member, low, high, owner);
        return Float.isNaN(value) ? null : value;
    }

    private static int spread(JsonObject json, Object owner) {
        int value = whole(json, "spread", 0, owner, MAX_SPREAD);
        if (value <= MODERN_SPREAD) { return value; }
        if (!clamped) {
            clamped = true;
            ContentLog.LOGGER.warn("{} sets spread to {}, but this version holds at most {} blocks of reach, so spread is {} here (and for any other fluid set higher)", owner, value, MODERN_SPREAD, MODERN_SPREAD);
        }
        return MODERN_SPREAD;
    }

    private static int whole(JsonObject json, String member, int fallback, Object owner) { return whole(json, member, fallback, owner, Integer.MAX_VALUE); }

    private static int whole(JsonObject json, String member, int fallback, Object owner, int high) {
        if (!json.has(member)) { return fallback; }
        int value = Json.getInt(json, member, fallback);
        if (value >= 1 && value <= high) { return value; }
        ContentLog.LOGGER.error("{} sets {} to {}, which is outside 1 to {}, so the default is used instead", owner, member, value, high);
        return fallback;
    }
}
