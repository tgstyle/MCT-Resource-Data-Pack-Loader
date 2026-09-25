package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IDedicatedServer;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ISettings;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import net.neoforged.neoforge.event.server.ServerStartedEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentWorldShape {
    public static final int VANILLA_MIN = -64;
    public static final int VANILLA_MAX = 320;
    public static final int LOWEST = -2032;
    public static final int HIGHEST = 2032;
    private static final int BLEND = 8;
    static final int MAX_LAYERS = 5;
    private static final String OVERWORLD = "minecraft:overworld";
    private static final String NETHER = "minecraft:the_nether";
    private static final String END = "minecraft:the_end";
    private static final List<String> DIMENSIONS = List.of(OVERWORLD, NETHER, END);
    private static final Map<String, String[]> BASES = Map.of(
            "", new String[] { "normal", "overworld" }, "default", new String[] { "normal", "overworld" }, "normal", new String[] { "normal", "overworld" },
            "largebiomes", new String[] { "large_biomes", "large_biomes" }, "large_biomes", new String[] { "large_biomes", "large_biomes" },
            "amplified", new String[] { "amplified", "amplified" }, "flat", new String[] { "flat", "overworld" }, "superflat", new String[] { "flat", "overworld" },
            "customized", new String[] { "normal", "overworld" }, "default_1_1", new String[] { "normal", "overworld" });
    private static final Set<String> OPTION_KEYS = Set.of("seaLevel", "useLavaOceans", "fixedBiome");
    static final Set<String> WARNED = new HashSet<>();
    private static final Set<ResourceLocation> MADE = new LinkedHashSet<>();
    private static final Set<ResourceKey<NoiseGeneratorSettings>> OVERWORLD_NOISE = Set.of(NoiseGeneratorSettings.OVERWORLD, NoiseGeneratorSettings.LARGE_BIOMES, NoiseGeneratorSettings.AMPLIFIED);
    private static final Set<ResourceLocation> SHAPED_OVERWORLD_NOISE = new HashSet<>();
    private static final Map<ResourceLocation, ResourceKey<NoiseGeneratorSettings>> VOIDED_OVERWORLD = new HashMap<>();
    private static final Map<ResourceLocation, Unvoided> UNVOIDED = new ConcurrentHashMap<>();
    private static final int UNREAD_SURFACE = 62;
    @Nullable private static ResourceLocation presetId;
    @Nullable private static String presetName;
    @Nullable private static ResourceLocation overrode;
    @Nullable private static JsonObject flatSettings;
    static boolean blockedToVoid;

    private ContentWorldShape() {}

    @Nullable public static ResourceLocation presetId() { return presetId; }

    @Nullable public static ResourceLocation serverPreset(String levelType) {
        if (presetId == null) { return null; }
        String named = levelType.trim();
        if (named.equals(presetId.toString()) || kept(named)) { return null; }
        ContentLog.LOGGER.info("The server's level-type is '{}', but the packs ship the world preset {} ({}), so the world is made with that; name flat or debug_all_block_states as level-type to keep the game's own", named, presetId, presetName);
        overrode = presetId;
        return presetId;
    }

    static boolean kept(String levelType) {
        String named = levelType.trim();
        for (String exception : ContentTerrain.worldTypeExceptions()) {
            String kept = exception.trim();
            if (kept.equalsIgnoreCase(named) || ("minecraft:" + kept).equalsIgnoreCase(named)) { return true; }
        }
        return false;
    }

    @Nullable static String levelType() {
        String type = ContentTerrain.worldType().toLowerCase(Locale.ROOT);
        String[] base = BASES.get(type);
        if (type.isEmpty() || base == null) { return null; }
        return presetId != null ? presetId.toString() : "minecraft:" + base[0];
    }

    @Nullable static JsonObject flatSettings() { return flatSettings; }

    private record Unvoided(long seed, NoiseBasedChunkGenerator generator, RandomState random) {}

    private record Shape(ResourceLocation id, String name, String[] base, int minY, int maxY, @Nullable String deepStone, int seaLevel, boolean lavaOceans, boolean deepCaves, @Nullable String fixedBiome) {
        boolean tall() { return minY != VANILLA_MIN || maxY != VANILLA_MAX; }

        boolean shapesOverworld() { return tall() || deepStone != null || seaLevel >= 0 || lavaOceans || fixedBiome != null; }

        boolean flat() { return "flat".equals(base[0]); }
    }

    public static void generate() {
        presetId = null;
        presetName = null;
        flatSettings = null;
        MADE.clear();
        SHAPED_OVERWORLD_NOISE.clear();
        VOIDED_OVERWORLD.clear();
        UNVOIDED.clear();
        blockedToVoid = ContentBiomeControl.everythingBlocked();
        Shape shape = shape();
        if (blockedToVoid && !ContentControl.off(ContentControl.VOID)) { ContentLog.LOGGER.info("No biome survived blocking and the world template is void, so the void world dimensions are generated as a void world"); }
        boolean anything = shape.shapesOverworld() || !"normal".equals(shape.base()[0]) || ContentBiomes.placesBiomes(OVERWORLD, OVERWORLD) || ContentBiomes.placesBiomes(NETHER, NETHER) || ContentOreControl.veinsBlocked(OVERWORLD);
        JsonObject dimensions = new JsonObject();
        for (String dimension : DIMENSIONS) {
            boolean flatBedrock = ContentBedrock.bedrockApplies(dimension);
            boolean isVoid = ContentVoidWorld.voidApplies(dimension);
            anything |= flatBedrock || isVoid;
            dimensions.add(dimension, dimension(shape, dimension, flatBedrock, isVoid));
        }
        ContentBedrock.report();
        keepSaved(shape);
        GameData.release();
        if (!anything) { return; }
        JsonObject preset = new JsonObject();
        preset.add("dimensions", dimensions);
        String namespace = shape.id().getNamespace();
        GeneratedResources.put(PackType.SERVER_DATA, namespace, "worldgen/world_preset/" + shape.id().getPath() + ".json", preset.toString());
        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        JsonArray values = new JsonArray();
        values.add(shape.id().toString());
        tag.add("values", values);
        GeneratedResources.put(PackType.SERVER_DATA, "minecraft", "tags/worldgen/world_preset/normal.json", tag.toString());
        JsonObject lang = new JsonObject();
        lang.addProperty("generator." + namespace + "." + shape.id().getPath(), shape.name());
        GeneratedResources.put(PackType.CLIENT_RESOURCES, namespace, "lang/en_us.json", lang.toString());
        presetId = shape.id();
        presetName = shape.name();
        Summary.info("worldshape", "Generated world preset " + shape.id() + " (" + shape.name() + ") on " + shape.base()[0] + ": overworld " + shape.minY() + ".." + shape.maxY()
                + (shape.deepStone() == null ? "" : ", deep stone " + shape.deepStone()) + (shape.deepCaves() ? ", caves to the floor" : "") + (shape.seaLevel() >= 0 ? ", sea level " + shape.seaLevel() : "") + (shape.lavaOceans() ? ", lava oceans" : "")
                + (ContentBedrock.bedrockAsked() ? ", flat bedrock " + ContentBedrock.layers() + " layer(s)" : "") + (ContentVoidWorld.voidAsked() ? ", void " + ContentVoidWorld.voidDimensions() : ""));
    }

    private static Shape shape() {
        WorldTemplateDef template = ContentWorldTemplates.active();
        ResourceLocation id = template == null ? ResourceLocation.fromNamespaceAndPath("rdpl", "settings") : template.key();
        String name = template == null ? "RDPL settings" : template.name();
        String type = ContentTerrain.worldType().trim().toLowerCase(Locale.ROOT);
        String[] base = BASES.get(type);
        if (base == null) {
            ContentLog.LOGGER.error("worldType '{}' is not one of default, largebiomes, amplified or flat on this version, building the shaped world on default", type);
            base = BASES.get("");
        }
        int minY = ContentTerrain.worldMinHeight();
        int maxY = ContentTerrain.worldMaxHeight();
        if (minY >= maxY || (minY & 15) != 0 || (maxY & 15) != 0 || minY < LOWEST || maxY > HIGHEST) {
            ContentLog.LOGGER.error("worldMinHeight {} and worldMaxHeight {} must be multiples of 16 between {} and {}, with the min below the max; keeping the game's own {}..{}", minY, maxY, LOWEST, HIGHEST, VANILLA_MIN, VANILLA_MAX);
            minY = VANILLA_MIN;
            maxY = VANILLA_MAX;
        }
        String deepStone = ContentTerrain.deepStone().trim();
        if (!deepStone.isEmpty() && block(deepStone, "deepStone") == null) { deepStone = ""; }
        if (!deepStone.isEmpty() && minY >= VANILLA_MIN) {
            ContentLog.LOGGER.info("deepStone names {}, but worldMinHeight {} leaves no world under the vanilla terrain for it, so it does nothing", deepStone, minY);
            deepStone = "";
        }
        int seaLevel = -1;
        boolean lavaOceans = false;
        String fixedBiome = null;
        JsonObject options = "flat".equals(base[0]) ? null : ContentTerrain.customizedOptions();
        if (options != null) {
            seaLevel = GsonHelper.getAsInt(options, "seaLevel", -1);
            lavaOceans = GsonHelper.getAsBoolean(options, "useLavaOceans", false);
            fixedBiome = options.has("fixedBiome") ? ContentTerrain.shippedBiome(GsonHelper.getAsString(options, "fixedBiome", "-1").trim()) : null;
            for (String key : options.keySet()) {
                if (!OPTION_KEYS.contains(key) && !ContentPopulateControl.readsOption(key) && WARNED.add("generatorOptions." + key)) { ContentLog.LOGGER.info("generatorOptions sets '{}', which this version does not read", key); }
            }
        }
        boolean deepCaves = ContentDeepCaves.carves(minY, "The overworld");
        return new Shape(id, name, base, minY, maxY, deepStone.isEmpty() ? null : deepStone, seaLevel, lavaOceans, deepCaves, fixedBiome);
    }

    private static JsonObject dimension(Shape shape, String dimension, boolean flatBedrock, boolean isVoid) {
        JsonObject out = new JsonObject();
        String path = dimension.substring(dimension.indexOf(':') + 1);
        boolean overworld = OVERWORLD.equals(dimension);
        String typeId = dimension;
        if (overworld && shape.tall()) {
            JsonObject type = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", "dimension_type/overworld.json"));
            if (type != null) {
                type.addProperty("min_y", shape.minY());
                type.addProperty("height", shape.maxY() - shape.minY());
                type.addProperty("logical_height", shape.maxY() - shape.minY());
                typeId = made(shape, path + "_type", "dimension_type", type);
            }
        }
        out.addProperty("type", typeId);
        JsonObject generator = new JsonObject();
        if (overworld && shape.flat()) {
            out.add("generator", flat());
            return out;
        }
        generator.addProperty("type", "minecraft:noise");
        JsonObject source = new JsonObject();
        if (END.equals(dimension)) { source.addProperty("type", "minecraft:the_end"); }
        else if (overworld && shape.fixedBiome() != null) {
            source.addProperty("type", "minecraft:fixed");
            source.addProperty("biome", shape.fixedBiome());
        }
        else {
            source.addProperty("type", "minecraft:multi_noise");
            if (ContentBiomes.placesBiomes(dimension, dimension)) { source.add("biomes", ContentBiomes.biomes(dimension, dimension)); }
            else { source.addProperty("preset", overworld ? OVERWORLD : "minecraft:nether"); }
        }
        generator.add("biome_source", source);
        String vanillaSettings = vanillaSettings(shape, dimension);
        if (isVoid) {
            ResourceLocation id = ownId(shape, path + "_void");
            voided(id, overworld, vanillaSettings);
            out.add("generator", voidGenerator(source, vanillaSettings, id));
            return out;
        }
        String settingsId = noiseSettings(shape, dimension, flatBedrock, false);
        generator.addProperty("settings", settingsId == null ? "minecraft:" + vanillaSettings : settingsId);
        out.add("generator", generator);
        return out;
    }

    private static void keepSaved(Shape shape) {
        for (String dimension : DIMENSIONS) {
            String path = dimension.substring(dimension.indexOf(':') + 1);
            ResourceLocation voidId = ownId(shape, path + "_void");
            if (!MADE.contains(voidId)) {
                String vanillaSettings = vanillaSettings(shape, dimension);
                voided(voidId, OVERWORLD.equals(dimension), vanillaSettings);
                voidGenerator(new JsonObject(), vanillaSettings, voidId);
            }
            if (!MADE.contains(ownId(shape, path + "_noise"))) { noiseSettings(shape, dimension, ContentBedrock.bedrockApplies(dimension), true); }
        }
    }

    private static String vanillaSettings(Shape shape, String dimension) { return OVERWORLD.equals(dimension) ? shape.base()[1] : NETHER.equals(dimension) ? "nether" : "end"; }

    private static void voided(ResourceLocation id, boolean overworld, String vanillaSettings) {
        MADE.add(id);
        if (overworld) { VOIDED_OVERWORLD.put(id, ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath("minecraft", vanillaSettings))); }
    }

    @Nullable private static String noiseSettings(Shape shape, String dimension, boolean flatBedrock, boolean always) {
        boolean overworld = OVERWORLD.equals(dimension);
        boolean seamed = ContentSeams.opensFloor(dimension) || ContentSeams.opensCeiling(dimension);
        if (!always && !((overworld && (shape.shapesOverworld() || ContentBiomes.any() || ContentOreControl.veinsBlocked(dimension))) || flatBedrock || seamed)) { return null; }
        JsonObject settings = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", "worldgen/noise_settings/" + vanillaSettings(shape, dimension) + ".json"));
        if (settings == null) { return null; }
        if (overworld) { shapeNoise(settings, shape); }
        if (overworld && ContentOreControl.veinsBlocked(dimension)) {
            settings.addProperty("ore_veins_enabled", false);
            ContentLog.LOGGER.debug("The overworld's iron and copper ore veins are turned off by the ores group");
        }
        if (overworld && ContentBiomes.any()) {
            ContentBiomes.surface(settings);
            ContentBiomes.spawnTargets(settings);
        }
        if (seamed) { ContentSeams.openBedrock(settings, dimension); }
        if (flatBedrock) { ContentBedrock.flattenBedrock(settings, dimension); }
        String settingsId = made(shape, dimension.substring(dimension.indexOf(':') + 1) + "_noise", "worldgen/noise_settings", settings);
        if (overworld) { overworldNoise(ResourceLocation.parse(settingsId)); }
        return settingsId;
    }

    static JsonObject voidGenerator(JsonObject source, String vanillaSettings, ResourceLocation id) {
        JsonObject generator = new JsonObject();
        JsonObject settings = GameData.json(ResourceLocation.fromNamespaceAndPath("minecraft", "worldgen/noise_settings/" + vanillaSettings + ".json"));
        if (settings == null) {
            generator.addProperty("type", "minecraft:flat");
            JsonObject flat = new JsonObject();
            flat.addProperty("biome", "minecraft:the_void");
            flat.add("layers", new JsonArray());
            generator.add("settings", flat);
            return generator;
        }
        JsonObject air = new JsonObject();
        air.addProperty("Name", "minecraft:air");
        settings.add("default_block", air);
        settings.add("default_fluid", air.deepCopy());
        settings.addProperty("aquifers_enabled", false);
        settings.addProperty("ore_veins_enabled", false);
        settings.addProperty("disable_mob_generation", true);
        JsonObject surface = new JsonObject();
        surface.addProperty("type", "minecraft:sequence");
        surface.add("sequence", new JsonArray());
        settings.add("surface_rule", surface);
        GsonHelper.getAsJsonObject(settings, "noise_router").addProperty("final_density", 1.0D);
        GeneratedResources.put(PackType.SERVER_DATA, id.getNamespace(), "worldgen/noise_settings/" + id.getPath() + ".json", settings.toString());
        generator.addProperty("type", "minecraft:noise");
        generator.add("biome_source", source);
        generator.addProperty("settings", id.toString());
        return generator;
    }

    private static JsonObject flat() {
        JsonObject generator = new JsonObject();
        generator.addProperty("type", "minecraft:flat");
        JsonObject settings = new JsonObject();
        ContentTerrain.Flat asked = ContentTerrain.flat();
        ContentTerrain.leavesWaterLakes(asked);
        settings.addProperty("biome", asked.biome());
        settings.addProperty("features", asked.decorated());
        settings.addProperty("lakes", asked.lakes());
        JsonArray layers = new JsonArray();
        int height = 0;
        for (String written : asked.layers()) {
            int star = written.indexOf('*');
            int count = 1;
            String name = written;
            if (star >= 0) {
                try { count = Math.max(1, Integer.parseInt(written.substring(0, star).trim())); }
                catch (NumberFormatException notNumber) { ContentLog.LOGGER.error("generatorOptions flat layer '{}' does not start with a count, laying one", written); }
                name = written.substring(star + 1).trim();
            }
            if (block(name, "generatorOptions") == null) { continue; }
            JsonObject layer = new JsonObject();
            layer.addProperty("block", name);
            layer.addProperty("height", count);
            layers.add(layer);
            height += count;
        }
        settings.add("layers", layers);
        JsonArray structures = new JsonArray();
        structures.add(ResourceDataPackLoader.MOD_ID + ":" + ContentCity.STRUCTURE);
        for (String set : asked.structures()) { structures.add(set); }
        for (String set : ContentStructureMaps.sets()) { structures.add(set); }
        settings.add("structure_overrides", structures);
        generator.add("settings", settings);
        flatSettings = settings;
        ContentLog.LOGGER.info("The overworld is flat: {} layer(s) {} block(s) deep, the pack's cities on it{}{}", layers.size(), height, asked.structures().isEmpty() ? "" : " with " + String.join(", ", asked.structures()), asked.decorated() ? ", decorated with the biome's features" : ", undecorated");
        return generator;
    }

    private static String made(Shape shape, String suffix, String folder, JsonObject json) {
        ResourceLocation id = ownId(shape, suffix);
        GeneratedResources.put(PackType.SERVER_DATA, id.getNamespace(), folder + "/" + id.getPath() + ".json", json.toString());
        MADE.add(id);
        return id.toString();
    }

    private static ResourceLocation ownId(Shape shape, String suffix) { return ResourceLocation.fromNamespaceAndPath(shape.id().getNamespace(), shape.id().getPath() + "_" + suffix); }

    private static void shapeNoise(JsonObject settings, Shape shape) {
        JsonObject noise = GsonHelper.getAsJsonObject(settings, "noise");
        noise.addProperty("min_y", shape.minY());
        noise.addProperty("height", shape.maxY() - shape.minY());
        if (shape.deepCaves()) { ContentDeepCaves.deepen(settings, ResourceLocation.fromNamespaceAndPath(shape.id().getNamespace(), shape.id().getPath() + "_overworld"), shape.minY()); }
        if (shape.seaLevel() >= 0) { settings.addProperty("sea_level", shape.seaLevel()); }
        if (shape.lavaOceans()) {
            JsonObject lava = new JsonObject();
            lava.addProperty("Name", "minecraft:lava");
            JsonObject properties = new JsonObject();
            properties.addProperty("level", "0");
            lava.add("Properties", properties);
            settings.add("default_fluid", lava);
        }
        if (shape.deepStone() == null) { return; }
        JsonObject gradient = new JsonObject();
        gradient.addProperty("type", "minecraft:vertical_gradient");
        gradient.addProperty("random_name", shape.id().getNamespace() + ":deep_stone");
        gradient.add("true_at_and_below", WorldgenJson.anchor("absolute", VANILLA_MIN));
        gradient.add("false_at_and_above", WorldgenJson.anchor("absolute", VANILLA_MIN + BLEND));
        JsonArray sequence = WorldgenJson.sequenceOf(settings);
        sequence.asList().add(1, WorldgenJson.condition(gradient, WorldgenJson.block(shape.deepStone())));
    }

    static boolean listed(String dimension, List<String> entries, boolean blacklist) {
        if (entries.isEmpty()) { return true; }
        boolean found = false;
        for (String entry : entries) {
            String named = entry.trim();
            named = ContentFormats.dimensionId(named);
            if (named.equals(dimension)) {
                found = true;
                break;
            }
        }
        return found != blacklist;
    }

    @Nullable static Block block(String name, String key) {
        Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(name.trim()));
        if (found == null && WARNED.add(key + ":" + name)) { ContentLog.LOGGER.error("{} names block '{}', which is not registered, so it does nothing", key, name); }
        return found;
    }

    public static void onCreateSpawn(LevelEvent.CreateSpawnPosition event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        if (ContentVoidWorld.voidApplies(level)) {
            BlockPos center = ContentVoidWorld.platformCenter(level);
            ContentVoidWorld.platform(level, center);
            event.getSettings().setSpawn(center.above(), 0.0F);
            level.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, level.getServer());
            event.setCanceled(true);
            Summary.info("void", "Made a void world with a platform at " + center.getX() + ", " + center.getY() + ", " + center.getZ());
        }
        if (level.dimension() != Level.OVERWORLD) { return; }
        BlockPos spawn = spawnAsked(level);
        if (spawn == null) { return; }
        event.getSettings().setSpawn(spawn, 0.0F);
        event.setCanceled(true);
        Summary.info("spawn", "Spawned the world at " + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + " as asked");
    }

    @Nullable private static BlockPos spawnAsked(ServerLevel level) {
        String written = ContentTerrain.worldSpawn().trim();
        if (written.isEmpty()) { return null; }
        String[] parts = written.split(",");
        if (parts.length != 2 && parts.length != 3) {
            ContentLog.LOGGER.error("worldSpawn is '{}', which is not written as x,z or x,y,z, so the world is spawned the way the game chooses", written);
            return null;
        }
        try {
            int x = Integer.parseInt(parts[0].trim());
            int z = Integer.parseInt(parts[parts.length - 1].trim());
            int y = parts.length == 3 ? Integer.parseInt(parts[1].trim()) : ground(level, x, z);
            return new BlockPos(x, y, z);
        }
        catch (NumberFormatException notNumbers) {
            ContentLog.LOGGER.error("worldSpawn is '{}', which is not written as whole numbers, so the world is spawned the way the game chooses", written);
            return null;
        }
    }

    private static int ground(ServerLevel level, int x, int z) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (ContentFlatSource.flat(generator)) { return generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, level, level.getChunkSource().randomState()); }
        return level.getSeaLevel() + 1;
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) { return; }
        ContentVoidWorld.remember(level);
        VanillaWindow.keep(level.getMinBuildHeight() < VANILLA_MIN && madeHere(level) ? level.getChunkSource().getGenerator() : null);
    }

    public static void onServerStarted(ServerStartedEvent event) {
        writeBack(event.getServer());
        ServerLevel level = event.getServer().overworld();
        if (level.getLevelData().getGameTime() != 0L) { return; }
        int border = ContentTerrain.worldBorder();
        if (border <= 0) { return; }
        level.getWorldBorder().setSize(border);
        Summary.info("border", "Set the world border to " + border + " blocks across");
    }

    private static void writeBack(MinecraftServer server) {
        ResourceLocation wanted = overrode;
        if (wanted == null || !(server instanceof DedicatedServer dedicated)) { return; }
        overrode = null;
        DedicatedServerSettings settings = ((IDedicatedServer) dedicated).rdpl$settings();
        ((ISettings) settings.getProperties()).rdpl$properties().setProperty("level-type", wanted.toString());
        settings.forceSave();
        ContentLog.LOGGER.info("Wrote level-type={} back to server.properties, so the file names the preset the world was made with", wanted);
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        ContentVoidWorld.standOn(player);
        tell(player);
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { ContentVoidWorld.standOn(player); }
    }

    private static void tell(ServerPlayer player) {
        if (!Config.worldgen.tellWorldType() || presetName == null) { return; }
        ServerLevel level = player.serverLevel();
        if (level.dimension() != Level.OVERWORLD || !madeHere(level)) { return; }
        player.sendSystemMessage(Component.literal(Lang.tr(player, "rdpl.world.type", presetName)));
    }

    public static int surfaceAt(Structure.GenerationContext context, int x, int z) {
        ChunkGenerator generator = context.chunkGenerator();
        if (ContentFlatSource.empty(generator)) { return 0; }
        if (overworldShaped(generator)) { return generator.getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()); }
        Unvoided unvoided = unvoided(context);
        if (unvoided != null) { return unvoided.generator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), unvoided.random()); }
        return UNREAD_SURFACE;
    }

    @Nullable private static Unvoided unvoided(Structure.GenerationContext context) {
        if (!(context.chunkGenerator() instanceof NoiseBasedChunkGenerator noise)) { return null; }
        ResourceKey<NoiseGeneratorSettings> settings = noise.generatorSettings().unwrapKey().orElse(null);
        ResourceKey<NoiseGeneratorSettings> original = settings == null ? null : VOIDED_OVERWORLD.get(settings.location());
        if (original == null) { return null; }
        Unvoided held = UNVOIDED.get(settings.location());
        if (held != null && held.seed() == context.seed()) { return held; }
        Holder<NoiseGeneratorSettings> shape = context.registryAccess().registryOrThrow(Registries.NOISE_SETTINGS).getHolderOrThrow(original);
        Unvoided made = new Unvoided(context.seed(), new NoiseBasedChunkGenerator(noise.getBiomeSource(), shape), RandomState.create(shape.value(), context.registryAccess().lookupOrThrow(Registries.NOISE), context.seed()));
        UNVOIDED.put(settings.location(), made);
        return made;
    }

    private static boolean overworldShaped(ChunkGenerator generator) {
        if (ContentFlatSource.flat(generator)) { return true; }
        if (!(generator instanceof NoiseBasedChunkGenerator noise)) { return false; }
        ResourceKey<NoiseGeneratorSettings> settings = noise.generatorSettings().unwrapKey().orElse(null);
        return settings != null && (OVERWORLD_NOISE.contains(settings) || SHAPED_OVERWORLD_NOISE.contains(settings.location()));
    }

    public static void overworldNoise(ResourceLocation settings) { SHAPED_OVERWORLD_NOISE.add(settings); }

    private static boolean madeHere(ServerLevel level) {
        ResourceKey<?> type = level.dimensionTypeRegistration().unwrapKey().orElse(null);
        if (type != null && MADE.contains(type.location())) { return true; }
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (generator instanceof NoiseBasedChunkGenerator noise && !ContentFlatSource.flat(generator)) {
            ResourceKey<?> settings = noise.generatorSettings().unwrapKey().orElse(null);
            return settings != null && MADE.contains(settings.location());
        }
        return ContentFlatSource.flat(generator) && ContentVoidWorld.voidApplies(level);
    }
}
