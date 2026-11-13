package mctmods.resourcedatapackloader.common.worldgen.surface;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.util.worldgen.WorldgenJson;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;

public final class ContentCaveWorld {
    public static final int HEIGHT = 256;
    private static final int NETHER_SLIDE_FROM = 104;
    private static final int NETHER_SLIDE_TO = 128;
    private static final int SLIDE_FROM = 232;
    private static final int SLIDE_TO = 256;
    private static final String STONE = "minecraft:stone";
    private static final String GRADIENT = "minecraft:y_clamped_gradient";
    private static final String NOODLE = "minecraft:overworld/caves/noodle";
    private static final String SPAGHETTI = "minecraft:overworld/caves/spaghetti_2d";
    private static final String ROUGHNESS = "minecraft:overworld/caves/spaghetti_roughness_function";

    private ContentCaveWorld() {}

    public static void shape(JsonObject settings, DimensionDef def) {
        JsonObject noise = Json.getJsonObject(settings, "noise");
        noise.addProperty("min_y", 0);
        noise.addProperty("height", HEIGHT);
        JsonObject fill = new JsonObject();
        fill.addProperty("Name", fillBlock(def));
        settings.add("default_block", fill);
        JsonObject water = new JsonObject();
        water.addProperty("Name", "minecraft:water");
        JsonObject level = new JsonObject();
        level.addProperty("level", "0");
        water.add("Properties", level);
        settings.add("default_fluid", water);
        settings.addProperty("aquifers_enabled", false);
        settings.addProperty("ore_veins_enabled", false);
        settings.add("surface_rule", WorldgenJson.sequence(bedrock(settings)));
        JsonObject router = Json.getJsonObject(settings, "noise_router");
        JsonObject density = Json.getJsonObject(router, "final_density");
        slideTop(density);
        JsonObject carved = min(density, min(named(NOODLE), add(named(SPAGHETTI), named(ROUGHNESS))));
        router.add("final_density", pair("minecraft:max", carved, pair("minecraft:max", band(HEIGHT - 6, -1.0, HEIGHT - 4, 1.0), band(3, 1.0, 5, -1.0))));
    }

    private static String fillBlock(DimensionDef def) {
        Block block = Registered.block(def.fillBlock());
        if (block != null && block.defaultBlockState().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) { return BuiltInRegistries.BLOCK.getKey(block).toString(); }
        ContentLog.LOGGER.error("Dimension {} terrain fillBlock '{}' is not a full solid block, using minecraft:stone; this is said once", def.key(), def.fillBlock());
        return STONE;
    }

    private static List<JsonObject> bedrock(JsonObject settings) {
        List<JsonObject> kept = new ArrayList<>();
        for (JsonElement element : WorldgenJson.sequenceOf(settings)) {
            if (!element.isJsonObject()) { continue; }
            String gradient = WorldgenJson.gradientName(element.getAsJsonObject());
            if (gradient != null && (gradient.endsWith(ContentBedrock.BEDROCK_FLOOR) || gradient.endsWith(ContentBedrock.BEDROCK_ROOF))) { kept.add(element.getAsJsonObject()); }
        }
        return kept;
    }

    private static void slideTop(JsonElement node) {
        if (node.isJsonArray()) {
            for (JsonElement child : node.getAsJsonArray()) { slideTop(child); }
        }
        else if (node.isJsonObject()) {
            JsonObject object = node.getAsJsonObject();
            if (GRADIENT.equals(Json.getString(object, "type", "")) && Json.getInt(object, "from_y", 0) == NETHER_SLIDE_FROM && Json.getInt(object, "to_y", 0) == NETHER_SLIDE_TO) {
                object.addProperty("from_y", SLIDE_FROM);
                object.addProperty("to_y", SLIDE_TO);
            }
            for (String member : object.keySet()) { slideTop(object.get(member)); }
        }
    }

    private static JsonElement named(String function) { return new JsonPrimitive(function); }

    private static JsonObject min(JsonElement first, JsonElement second) { return pair("minecraft:min", first, second); }

    private static JsonObject add(JsonElement first, JsonElement second) { return pair("minecraft:add", first, second); }

    private static JsonObject pair(String type, JsonElement first, JsonElement second) {
        JsonObject out = new JsonObject();
        out.addProperty("type", type);
        out.add("argument1", first);
        out.add("argument2", second);
        return out;
    }

    private static JsonObject band(int fromY, double fromValue, int toY, double toValue) {
        JsonObject out = new JsonObject();
        out.addProperty("type", GRADIENT);
        out.addProperty("from_y", fromY);
        out.addProperty("to_y", toY);
        out.addProperty("from_value", fromValue);
        out.addProperty("to_value", toValue);
        return out;
    }
}
