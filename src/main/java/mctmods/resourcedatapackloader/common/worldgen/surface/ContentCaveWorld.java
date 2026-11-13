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
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;

public final class ContentCaveWorld {
    public static final String DEFAULT_FILL = "minecraft:stone";
    public static final String DEFAULT_FLUID = "minecraft:water";
    public static final int DEFAULT_SEA = 32;
    public static final int HEIGHT = 256;
    private static final String GRADIENT = "minecraft:y_clamped_gradient";
    private static final int NETHER_TOP_FROM = 104;
    private static final int NETHER_TOP_TO = 128;
    private static final String NOODLE = "minecraft:overworld/caves/noodle";
    private static final String SPAGHETTI = "minecraft:overworld/caves/spaghetti_2d";
    private static final String ROUGHNESS = "minecraft:overworld/caves/spaghetti_roughness_function";

    private ContentCaveWorld() {}

    public static void shape(JsonObject settings, DimensionDef def) {
        String owner = "Dimension " + def.key();
        JsonObject noise = Json.getJsonObject(settings, "noise");
        noise.addProperty("min_y", 0);
        noise.addProperty("height", HEIGHT);
        settings.add("default_block", WorldgenJson.block(fill(def.fillBlock(), owner)).getAsJsonObject("result_state"));
        JsonObject water = new JsonObject();
        water.addProperty("Name", DEFAULT_FLUID);
        JsonObject level = new JsonObject();
        level.addProperty("level", "0");
        water.add("Properties", level);
        settings.add("default_fluid", water);
        settings.addProperty("aquifers_enabled", false);
        settings.addProperty("ore_veins_enabled", false);
        bedrockOnly(settings);
        JsonObject router = Json.getJsonObject(settings, "noise_router");
        JsonObject density = Json.getJsonObject(router, "final_density");
        raiseTopSlide(density);
        JsonObject spaghetti = binary("minecraft:add", new JsonPrimitive(SPAGHETTI), new JsonPrimitive(ROUGHNESS));
        JsonObject carved = binary("minecraft:min", density, binary("minecraft:min", new JsonPrimitive(NOODLE), spaghetti));
        router.add("final_density", binary("minecraft:max", carved, binary("minecraft:max", band(HEIGHT - 6, -1.0, HEIGHT - 4, 1.0), band(3, 1.0, 5, -1.0))));
    }

    private static String fill(String named, String owner) {
        Block block = Registered.block(named);
        if (block != null && block.defaultBlockState().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) { return named; }
        ContentLog.LOGGER.error("{} terrain fillBlock '{}' is not a full solid block, using minecraft:stone; this is said once", owner, named);
        return DEFAULT_FILL;
    }

    private static void bedrockOnly(JsonObject settings) {
        List<JsonObject> kept = new ArrayList<>();
        for (JsonElement element : WorldgenJson.sequenceOf(settings)) {
            if (!element.isJsonObject()) { continue; }
            String gradient = WorldgenJson.gradientName(element.getAsJsonObject());
            if (gradient != null && (gradient.endsWith("bedrock_floor") || gradient.endsWith("bedrock_roof"))) { kept.add(element.getAsJsonObject()); }
        }
        settings.add("surface_rule", WorldgenJson.sequence(kept));
    }

    private static void raiseTopSlide(JsonElement node) {
        if (node.isJsonArray()) {
            for (JsonElement child : node.getAsJsonArray()) { raiseTopSlide(child); }
            return;
        }
        if (!node.isJsonObject()) { return; }
        JsonObject object = node.getAsJsonObject();
        if (GRADIENT.equals(Json.getString(object, "type", "")) && Json.getInt(object, "from_y", 0) == NETHER_TOP_FROM && Json.getInt(object, "to_y", 0) == NETHER_TOP_TO) {
            object.addProperty("from_y", HEIGHT - 24);
            object.addProperty("to_y", HEIGHT);
        }
        for (String member : object.keySet()) { raiseTopSlide(object.get(member)); }
    }

    private static JsonObject binary(String type, JsonElement first, JsonElement second) {
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
