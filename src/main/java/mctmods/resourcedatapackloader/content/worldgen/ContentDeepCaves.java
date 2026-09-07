package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public final class ContentDeepCaves {
    public static final String OFF = "off";
    public static final String DEEP = "deep";
    public static final String WORLD = "world";
    private static final int LAVA_ABOVE_FLOOR = 10;
    private static final int SLIDE = 24;
    private static final int NOODLES_ABOVE_FLOOR = 4;
    private static final int SPAGHETTI_SPAN = 384;
    private static final int FAR_ABOVE = 1000000;
    private static final String FOLDER = "worldgen/density_function";
    private static final String VANILLA_NOODLE = "minecraft:overworld/caves/noodle";
    private static final String VANILLA_SPAGHETTI = "minecraft:overworld/caves/spaghetti_2d";
    private static final String GRADIENT = "minecraft:y_clamped_gradient";
    private static final String RANGE = "minecraft:range_choice";
    private static final String HEIGHT = "minecraft:y";

    private ContentDeepCaves() {}

    public static boolean carves(int floor, String where) {
        if (floor >= ContentWorldShape.VANILLA_MIN) { return false; }
        String asked = ContentTerrain.noiseCaves().toLowerCase(Locale.ROOT);
        if (asked.isEmpty() || OFF.equals(asked)) {
            ContentLog.LOGGER.info("{} goes down to {} with noiseCaves off, so the world under {} is solid deep stone apart from what the worldgen entries and the game's carvers cut", where, floor, ContentWorldShape.VANILLA_MIN);
            return false;
        }
        if (!DEEP.equals(asked) && !WORLD.equals(asked)) {
            ContentLog.LOGGER.error("noiseCaves is '{}', not off, deep or world, so the world under {} in {} stays solid", asked, ContentWorldShape.VANILLA_MIN, where);
            return false;
        }
        return true;
    }

    public static void deepen(JsonObject settings, ResourceLocation owner, int floor) {
        JsonObject router = GsonHelper.getAsJsonObject(settings, "noise_router", null);
        JsonObject noodle = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", FOLDER + "/overworld/caves/noodle.json"));
        JsonObject spaghetti = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", FOLDER + "/overworld/caves/spaghetti_2d.json"));
        if (router == null || !router.has("final_density") || noodle == null || spaghetti == null) {
            ContentLog.LOGGER.error("The game's overworld noise could not be read for {}, so the world under {} stays solid", owner, ContentWorldShape.VANILLA_MIN);
            return;
        }
        rewrite(noodle, element -> noodleFloor(element, floor));
        rewrite(spaghetti, element -> spaghettiFloor(element, floor));
        String noodleId = put(owner, "noodle", noodle);
        String spaghettiId = put(owner, "spaghetti_2d", spaghetti);
        router.add("final_density", rewrite(router.get("final_density"), element -> deepFloor(element, floor, noodleId, spaghettiId)));
        ContentLog.LOGGER.debug("The caves, tunnels and noodles of {} carry on down to {}, closing {} blocks above the floor, with the lava lakes {} blocks above it", owner, floor, SLIDE, LAVA_ABOVE_FLOOR);
    }

    @Nullable public static Aquifer.FluidPicker fluidPicker(NoiseGeneratorSettings settings) {
        int floor = settings.noiseSettings().minY();
        if (floor >= ContentWorldShape.VANILLA_MIN) { return null; }
        int lavaLevel = floor + LAVA_ABOVE_FLOOR;
        Aquifer.FluidStatus lava = new Aquifer.FluidStatus(lavaLevel, Blocks.LAVA.defaultBlockState());
        Aquifer.FluidStatus sea = new Aquifer.FluidStatus(settings.seaLevel(), settings.defaultFluid());
        int lavaBelow = Math.min(lavaLevel, settings.seaLevel());
        return (x, y, z) -> y < lavaBelow ? lava : sea;
    }

    private static String put(ResourceLocation owner, String suffix, JsonObject json) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(owner.getNamespace(), owner.getPath() + "_" + suffix);
        GeneratedResources.put(PackType.SERVER_DATA, id.getNamespace(), FOLDER + "/" + id.getPath() + ".json", json.toString());
        return id.toString();
    }

    private static JsonElement rewrite(JsonElement element, UnaryOperator<JsonElement> rule) {
        JsonElement swapped = rule.apply(element);
        if (swapped != element) { return swapped; }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : List.copyOf(object.keySet())) { object.add(key, rewrite(object.get(key), rule)); }
        }
        else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int index = 0; index < array.size(); index++) { array.set(index, rewrite(array.get(index), rule)); }
        }
        return element;
    }

    private static JsonElement noodleFloor(JsonElement element, int floor) {
        JsonObject range = typed(element, RANGE);
        if (range != null && names(range.get("input"), HEIGHT) && GsonHelper.getAsDouble(range, "min_inclusive", 0.0D) == ContentWorldShape.VANILLA_MIN + NOODLES_ABOVE_FLOOR) { range.addProperty("min_inclusive", floor + NOODLES_ABOVE_FLOOR); }
        return element;
    }

    private static JsonElement spaghettiFloor(JsonElement element, int floor) {
        JsonObject gradient = gradient(element, ContentWorldShape.VANILLA_MAX);
        if (gradient != null) {
            gradient.addProperty("from_y", floor);
            gradient.addProperty("to_y", floor + SPAGHETTI_SPAN);
        }
        return element;
    }

    private static JsonElement deepFloor(JsonElement element, int floor, String noodleId, String spaghettiId) {
        JsonObject slide = gradient(element, ContentWorldShape.VANILLA_MIN + SLIDE);
        if (slide != null) {
            slide.addProperty("from_y", floor);
            slide.addProperty("to_y", floor + SLIDE);
            return element;
        }
        if (names(element, VANILLA_NOODLE)) { return new JsonPrimitive(noodleId); }
        if (!names(element, VANILLA_SPAGHETTI)) { return element; }
        JsonObject split = new JsonObject();
        split.addProperty("type", RANGE);
        split.addProperty("input", HEIGHT);
        split.addProperty("min_inclusive", ContentWorldShape.VANILLA_MIN);
        split.addProperty("max_exclusive", FAR_ABOVE);
        split.addProperty("when_in_range", VANILLA_SPAGHETTI);
        split.addProperty("when_out_of_range", spaghettiId);
        return split;
    }

    private static boolean names(@Nullable JsonElement element, String id) { return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() && id.equals(element.getAsString()); }

    @Nullable private static JsonObject typed(JsonElement element, String type) {
        if (!element.isJsonObject()) { return null; }
        JsonObject object = element.getAsJsonObject();
        return names(object.get("type"), type) ? object : null;
    }

    @Nullable private static JsonObject gradient(JsonElement element, int toY) {
        JsonObject gradient = typed(element, GRADIENT);
        if (gradient == null || GsonHelper.getAsInt(gradient, "from_y", 0) != ContentWorldShape.VANILLA_MIN || GsonHelper.getAsInt(gradient, "to_y", 0) != toY) { return null; }
        return gradient;
    }
}
