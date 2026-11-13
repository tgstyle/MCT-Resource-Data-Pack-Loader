package mctmods.resourcedatapackloader.common.worldgen.surface;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.util.worldgen.WorldgenJson;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class ContentCaveWorld {
    private static final int HEIGHT = 256;
    private static final int SLIDE_FROM = 104;
    private static final int SLIDE_TO = 128;
    private static final String STONE = "minecraft:stone";
    private static final String WATER = "minecraft:water";
    private static final String TYPE = "type";
    private static final String NOODLE = "minecraft:overworld/caves/noodle";
    private static final String SPAGHETTI = "minecraft:overworld/caves/spaghetti_2d";
    private static final String ROUGHNESS = "minecraft:overworld/caves/spaghetti_roughness_function";
    private static final Set<String> GRADIENTS = Set.of("minecraft:y_clamped_gradient", "minecraft:gradient");
    private static final List<String> BEDROCK = List.of("bedrock_floor", "bedrock_roof");

    private ContentCaveWorld() {}

    public static void retype(JsonObject type) {
        type.addProperty("min_y", 0);
        type.addProperty("height", HEIGHT);
        type.addProperty("logical_height", HEIGHT);
        type.addProperty("has_ceiling", true);
    }

    public static void shape(JsonObject settings, DimensionDef def, String owner) {
        JsonObject noise = Json.getJsonObject(settings, "noise");
        noise.addProperty("min_y", 0);
        noise.addProperty("height", HEIGHT);
        JsonObject fill = new JsonObject();
        fill.addProperty("Name", fill(def.fillBlock(), owner));
        settings.add("default_block", fill);
        JsonObject water = OceanFluid.state(WATER, false, owner);
        if (water != null) { settings.add("default_fluid", water); }
        settings.addProperty("aquifers_enabled", false);
        settings.addProperty("ore_veins_enabled", false);
        settings.add("surface_rule", WorldgenJson.sequence(bedrockRules(settings)));
        JsonObject router = Json.getJsonObject(settings, "noise_router");
        JsonElement density = router.get("final_density");
        List<JsonObject> gradients = new ArrayList<>();
        collect(density, gradients);
        boolean modern = gradients.stream().anyMatch(gradient -> gradient.has("from_coordinate"));
        for (JsonObject gradient : gradients) { raiseSlide(gradient); }
        JsonObject spaghetti = binary("minecraft:add", new JsonPrimitive(SPAGHETTI), new JsonPrimitive(ROUGHNESS), modern);
        JsonObject tunnels = binary("minecraft:min", new JsonPrimitive(NOODLE), spaghetti, modern);
        JsonObject carved = binary("minecraft:min", density, tunnels, modern);
        JsonObject top = band(gradients.getFirst(), HEIGHT - 6, -1.0, HEIGHT - 4, 1.0);
        JsonObject bottom = band(gradients.getFirst(), 3, 1.0, 5, -1.0);
        router.add("final_density", binary("minecraft:max", carved, binary("minecraft:max", top, bottom, modern), modern));
    }

    private static String fill(String name, String owner) {
        Block block = Registered.block(name);
        BlockState state = block == null ? null : block.defaultBlockState();
        if (state != null && !state.hasBlockEntity() && state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) { return BuiltInRegistries.BLOCK.getKey(block).toString(); }
        ContentLog.LOGGER.error("{} terrain fillBlock '{}' is not a full solid block, using minecraft:stone; this is said once", owner, name);
        return STONE;
    }

    private static List<JsonObject> bedrockRules(JsonObject settings) {
        List<JsonObject> rules = new ArrayList<>();
        for (JsonElement element : WorldgenJson.sequenceOf(settings)) {
            if (!element.isJsonObject()) { continue; }
            String gradient = WorldgenJson.gradientName(element.getAsJsonObject());
            if (gradient != null && BEDROCK.stream().anyMatch(gradient::endsWith)) { rules.add(element.getAsJsonObject()); }
        }
        return rules;
    }

    private static void collect(JsonElement node, List<JsonObject> into) {
        if (node.isJsonArray()) {
            for (JsonElement item : (JsonArray) node) { collect(item, into); }
            return;
        }
        if (!node.isJsonObject()) { return; }
        JsonObject object = node.getAsJsonObject();
        if (GRADIENTS.contains(Json.getString(object, TYPE, ""))) { into.add(object); }
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) { collect(entry.getValue(), into); }
    }

    private static void raiseSlide(JsonObject gradient) {
        String from = gradient.has("from_coordinate") ? "from_coordinate" : "from_y";
        String to = gradient.has("from_coordinate") ? "to_coordinate" : "to_y";
        if (gradient.get(from).getAsInt() != SLIDE_FROM || gradient.get(to).getAsInt() != SLIDE_TO) { return; }
        gradient.addProperty(from, SLIDE_FROM + HEIGHT - SLIDE_TO);
        gradient.addProperty(to, HEIGHT);
    }

    private static JsonObject binary(String type, JsonElement first, JsonElement second, boolean modern) {
        JsonObject out = new JsonObject();
        out.addProperty(TYPE, type);
        out.add(modern ? "left" : "argument1", first);
        out.add(modern ? "right" : "argument2", second);
        return out;
    }

    private static JsonObject band(JsonObject template, int fromY, double fromValue, int toY, double toValue) {
        JsonObject out = template.deepCopy();
        String from = out.has("from_coordinate") ? "from_coordinate" : "from_y";
        String to = out.has("from_coordinate") ? "to_coordinate" : "to_y";
        out.addProperty(from, fromY);
        out.addProperty(to, toY);
        out.addProperty("from_value", fromValue);
        out.addProperty("to_value", toValue);
        return out;
    }
}
