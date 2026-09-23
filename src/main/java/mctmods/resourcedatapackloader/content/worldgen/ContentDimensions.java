package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.DimensionValues;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.entity.player.PlayerEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentDimensions {
    private static final ResourceLocation OVERWORLD = ResourceLocation.fromNamespaceAndPath("minecraft", "overworld");
    private static final Map<ResourceLocation, DimensionDef> DEFS = new LinkedHashMap<>();
    private static final Map<UUID, ResourceLocation> DIED_IN = new HashMap<>();
    private static final DimensionValues<Integer> CLOUDS = new DimensionValues<>("cloudHeight", ContentDimensions::height, "which is not a whole number");
    private static boolean loaded;

    private ContentDimensions() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff() || !Config.content.dimensions()) { return; }
        Json.eachFile(PackManager.DIMENSIONS, "dimension definition", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            DimensionDef def = ContentDimensionParser.parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("dimensions", "Loaded " + DEFS.size() + " dimension definition(s) from packs"); }
    }

    public static Collection<DimensionDef> all() { return Collections.unmodifiableCollection(DEFS.values()); }

    @Nullable public static DimensionDef def(ResourceLocation dimension) { return DEFS.get(dimension); }

    @Nullable public static DimensionDef def(Level level) { return DEFS.get(level.dimension().location()); }

    public static boolean spawns(Level level) {
        DimensionDef def = def(level);
        return def == null || def.spawning();
    }

    public static void generate() {
        List<String> made = new ArrayList<>();
        for (DimensionDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            JsonObject type = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", "dimension_type/" + def.base() + ".json"));
            if (type == null) { continue; }
            String namespace = def.key().getNamespace();
            String path = def.key().getPath();
            type.addProperty("has_skylight", def.hasSkyLight());
            type.addProperty("natural", def.surfaceWorld());
            type.addProperty("ultrawarm", def.nether() || def.waterVaporizes());
            type.addProperty("coordinate_scale", def.movementFactor());
            type.addProperty("bed_works", def.beds());
            type.addProperty("respawn_anchor_works", !def.beds());
            type.addProperty("ambient_light", def.ambientLight());
            if (def.fixedTime() >= 0) { type.addProperty("fixed_time", def.fixedTime()); }
            else { type.remove("fixed_time"); }
            if (def.shapesHeight()) {
                type.addProperty("min_y", def.minHeight());
                type.addProperty("height", def.maxHeight() - def.minHeight());
                type.addProperty("logical_height", def.maxHeight() - def.minHeight());
            }
            type.addProperty("effects", def.hasEffects() ? def.key().toString() : "minecraft:overworld");
            GeneratedResources.put(PackType.SERVER_DATA, namespace, "dimension_type/" + path + ".json", type.toString());
            JsonObject dimension = new JsonObject();
            dimension.addProperty("type", def.key().toString());
            dimension.add("generator", generator(def));
            GeneratedResources.put(PackType.SERVER_DATA, namespace, "dimension/" + path + ".json", dimension.toString());
            made.add(def.key().toString());
        }
        if (!made.isEmpty()) { Summary.info("dimensions.generated", "Generated " + made.size() + " dimension(s) with their types: " + String.join(", ", made)); }
    }

    private static JsonObject generator(DimensionDef def) {
        String dimension = def.key().toString();
        boolean isVoid = ContentVoidWorld.voidApplies(dimension);
        JsonObject generator = new JsonObject();
        if (DimensionDef.VOID.equals(def.terrain()) || DimensionDef.FLAT.equals(def.terrain())) {
            boolean empty = isVoid || DimensionDef.VOID.equals(def.terrain());
            ContentTerrain.Flat asked = ContentTerrain.flat(def.flatOptions());
            boolean single = DimensionDef.SINGLE.equals(def.biomeSource());
            generator.addProperty("type", single ? "minecraft:flat" : ResourceDataPackLoader.MOD_ID + ":" + ContentFlatSource.ID);
            if (!single) {
                generator.add("biome_source", biomeSource(def, dimension));
                generator.addProperty("noise_settings", OVERWORLD.toString());
                generator.addProperty("decorated", !empty && asked.decorated());
                generator.addProperty("lakes", !empty && asked.waterLakes());
                generator.addProperty("lava_lakes", !empty && asked.lakes());
            }
            else { ContentTerrain.leavesWaterLakes(asked); }
            JsonObject settings = new JsonObject();
            settings.addProperty("biome", single ? def.biome() : asked.biome());
            settings.addProperty("features", !empty && asked.decorated());
            settings.addProperty("lakes", !empty && asked.lakes());
            settings.add("layers", empty ? new JsonArray() : layers(def, asked.layers()));
            JsonArray structures = new JsonArray();
            if (!empty && def.structures()) {
                for (String set : asked.structures()) { structures.add(set); }
            }
            for (String set : ContentStructureMaps.sets()) { structures.add(set); }
            settings.add("structure_overrides", structures);
            generator.add("settings", settings);
            return generator;
        }
        generator.addProperty("type", "minecraft:noise");
        generator.add("biome_source", biomeSource(def, dimension));
        String vanillaSettings = DimensionDef.NETHER.equals(def.terrain()) ? "nether" : DimensionDef.END.equals(def.terrain()) ? "end" : "overworld";
        String settingsId = "minecraft:" + vanillaSettings;
        boolean seamed = ContentSeams.opensFloor(dimension) || ContentSeams.opensCeiling(dimension);
        boolean flatBedrock = ContentBedrock.bedrockApplies(dimension);
        boolean veinless = "overworld".equals(vanillaSettings) && ContentOreControl.veinsBlocked(dimension);
        boolean grounded = "overworld".equals(vanillaSettings) && ContentBiomes.any();
        if (def.shapesNoise() || seamed || flatBedrock || veinless || grounded) {
            JsonObject settings = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", "worldgen/noise_settings/" + vanillaSettings + ".json"));
            if (settings != null) {
                if (seamed) { ContentSeams.openBedrock(settings, dimension); }
                if (veinless) { settings.addProperty("ore_veins_enabled", false); }
                if (grounded) { ContentBiomes.surface(settings); }
                if (flatBedrock) { ContentBedrock.flattenBedrock(settings, dimension); }
                if (def.shapesHeight()) {
                    JsonObject noise = GsonHelper.getAsJsonObject(settings, "noise");
                    noise.addProperty("min_y", def.minHeight());
                    noise.addProperty("height", def.maxHeight() - def.minHeight());
                    if ("overworld".equals(vanillaSettings) && ContentDeepCaves.carves(def.minHeight(), "Dimension " + dimension)) { ContentDeepCaves.deepen(settings, def.key(), def.minHeight()); }
                }
                if (def.seaLevel() >= 0) { settings.addProperty("sea_level", def.seaLevel()); }
                if (def.lavaOceans()) {
                    JsonObject lava = new JsonObject();
                    lava.addProperty("Name", "minecraft:lava");
                    JsonObject properties = new JsonObject();
                    properties.addProperty("level", "0");
                    lava.add("Properties", properties);
                    settings.add("default_fluid", lava);
                }
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(def.key().getNamespace(), def.key().getPath() + "_noise");
                GeneratedResources.put(PackType.SERVER_DATA, id.getNamespace(), "worldgen/noise_settings/" + id.getPath() + ".json", settings.toString());
                if ("overworld".equals(vanillaSettings)) { ContentWorldShape.overworldNoise(id); }
                settingsId = id.toString();
            }
        }
        generator.addProperty("settings", settingsId);
        return generator;
    }

    private static JsonObject biomeSource(DimensionDef def, String dimension) {
        JsonObject source = new JsonObject();
        if (DimensionDef.SINGLE.equals(def.biomeSource())) {
            source.addProperty("type", "minecraft:fixed");
            source.addProperty("biome", def.biome());
        }
        else {
            source.addProperty("type", "minecraft:multi_noise");
            if (ContentBiomes.placesBiomes(ContentBiomes.OVERWORLD, dimension)) { source.add("biomes", ContentBiomes.biomes(ContentBiomes.OVERWORLD, dimension)); }
            else { source.addProperty("preset", ContentBiomes.OVERWORLD); }
        }
        return source;
    }

    private static JsonArray layers(DimensionDef def, List<String> asked) {
        JsonArray layers = new JsonArray();
        for (String written : asked) {
            String layer = written.trim();
            int star = layer.indexOf('*');
            int height = 1;
            String block = layer;
            if (star >= 0) {
                try { height = Math.max(1, Integer.parseInt(layer.substring(0, star).trim())); }
                catch (NumberFormatException wrong) { ContentLog.LOGGER.error("Dimension {} has flat layer '{}', whose count is not a number, taking one", def.key(), layer); }
                block = layer.substring(star + 1).trim();
            }
            JsonObject entry = new JsonObject();
            entry.addProperty("block", block);
            entry.addProperty("height", height);
            layers.add(entry);
        }
        return layers;
    }

    @Nullable public static Integer cloudHeight(String dimension) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return CLOUDS.at(dimension, ContentControl.lines(ContentControl.TERRAIN, "cloudHeight", Config.worldgen.cloudHeight()));
    }

    @Nullable static Integer height(String value) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException wrong) { return null; }
    }

    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) { return; }
        DIED_IN.put(event.getEntity().getUUID(), event.getOriginal().level().dimension().location());
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        ResourceLocation diedIn = DIED_IN.remove(player.getUUID());
        if (diedIn == null || event.isEndConquered()) { return; }
        DimensionDef def = DEFS.get(diedIn);
        if (def == null) { return; }
        ResourceLocation sentTo = def.respawnDimension() != null ? def.respawnDimension() : def.respawn() ? null : OVERWORLD;
        if (sentTo == null || sentTo.equals(player.level().dimension().location())) { return; }
        if (player.getRespawnPosition() != null && (def.respawn() || !player.getRespawnDimension().location().equals(diedIn))) { return; }
        MinecraftServer server = player.getServer();
        ServerLevel target = server == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, sentTo));
        if (target == null) {
            ContentLog.LOGGER.error("Dimension {} sends respawns to {}, which is not a loaded dimension, so the player respawns where the game put them", diedIn, sentTo);
            return;
        }
        BlockPos feet = landing(target, target.getSharedSpawnPos(), net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
        player.teleportTo(target, feet.getX() + 0.5D, feet.getY(), feet.getZ() + 0.5D, target.getSharedSpawnAngle(), 0.0F);
        ContentLog.LOGGER.debug("Player {} died in {}, which {}, and respawns in {} at {}", player.getName().getString(), diedIn, def.respawn() ? "sends respawns on" : "allows no respawn", sentTo, feet);
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { DIED_IN.remove(event.getEntity().getUUID()); }

    public static boolean structuresOff(ServerLevel level) {
        DimensionDef def = DEFS.get(level.dimension().location());
        return def != null && !def.structures();
    }

    public static boolean anyStructuresOff() {
        for (DimensionDef def : DEFS.values()) { if (!def.structures()) { return true; } }
        return false;
    }

    public static BlockPos landing(ServerLevel level, BlockPos column, net.minecraft.world.level.levelgen.Heightmap.Types type) {
        BlockPos top = top(level, column, type);
        if (top.getY() > level.getMinBuildHeight() + 1) { return top; }
        DimensionDef def = DEFS.get(level.dimension().location());
        if (def == null) { return top; }
        return new BlockPos(column.getX(), Mth.clamp(def.groundLevel(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight() - 2), column.getZ());
    }

    public static BlockPos top(ServerLevel level, BlockPos column, net.minecraft.world.level.levelgen.Heightmap.Types type) {
        LevelChunk held = level.getChunkAt(column);
        return new BlockPos(column.getX(), held.getHeight(type, column.getX() & 15, column.getZ() & 15) + 1, column.getZ());
    }
}
