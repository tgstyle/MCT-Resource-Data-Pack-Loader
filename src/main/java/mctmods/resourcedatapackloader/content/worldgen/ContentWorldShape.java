package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.SectionPos;

import net.neoforged.neoforge.event.server.ServerStartedEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentWorldShape {
    public static final int VANILLA_MIN = -64;
    public static final int VANILLA_MAX = 320;
    public static final int LOWEST = -2032;
    public static final int HIGHEST = 2032;
    private static final int BLEND = 8;
    private static final String OVERWORLD = "minecraft:overworld";
    private static final String NETHER = "minecraft:the_nether";
    private static final String END = "minecraft:the_end";
    private static final List<String> DIMENSIONS = List.of(OVERWORLD, NETHER, END);
    private static final Map<String, String[]> BASES = Map.of(
            "", new String[] { "normal", "overworld" }, "default", new String[] { "normal", "overworld" }, "normal", new String[] { "normal", "overworld" },
            "largebiomes", new String[] { "large_biomes", "large_biomes" }, "large_biomes", new String[] { "large_biomes", "large_biomes" },
            "amplified", new String[] { "amplified", "amplified" });
    private static final String BEDROCK_FLOOR = "bedrock_floor";
    private static final String BEDROCK_ROOF = "bedrock_roof";
    private static final Gson GSON = new GsonBuilder().create();
    private static final Set<String> WARNED = new HashSet<>();
    private static final Set<ResourceLocation> MADE = new LinkedHashSet<>();
    @Nullable private static ResourceLocation presetId;
    @Nullable private static String presetName;
    @Nullable private static VanillaPackResources vanilla;

    private ContentWorldShape() {}

    @Nullable public static ResourceLocation presetId() { return presetId; }

    private record Shape(ResourceLocation id, String name, String[] base, int minY, int maxY, @Nullable String deepStone, int seaLevel, boolean lavaOceans) {
        boolean tall() { return minY != VANILLA_MIN || maxY != VANILLA_MAX; }

        boolean shapesOverworld() { return tall() || deepStone != null || seaLevel >= 0 || lavaOceans; }
    }

    public static void generate() {
        presetId = null;
        presetName = null;
        MADE.clear();
        Shape shape = shape();
        boolean anything = shape.shapesOverworld() || !"normal".equals(shape.base()[0]);
        JsonObject dimensions = new JsonObject();
        for (String dimension : DIMENSIONS) {
            boolean flatBedrock = bedrockApplies(dimension);
            boolean isVoid = voidApplies(dimension);
            anything |= flatBedrock || isVoid;
            dimensions.add(dimension, dimension(shape, dimension, flatBedrock, isVoid));
        }
        vanilla = null;
        if (!anything) {
            MADE.clear();
            return;
        }
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
                + (shape.deepStone() == null ? "" : ", deep stone " + shape.deepStone()) + (shape.seaLevel() >= 0 ? ", sea level " + shape.seaLevel() : "") + (shape.lavaOceans() ? ", lava oceans" : "")
                + (bedrockAsked() ? ", flat bedrock " + layers() + " layer(s)" : "") + (voidAsked() ? ", void " + voidDimensions() : ""));
    }

    private static Shape shape() {
        WorldTemplateDef template = ContentWorldTemplates.active();
        ResourceLocation id = template == null ? ResourceLocation.fromNamespaceAndPath("rdpl", "settings") : template.key();
        String name = template == null ? "RDPL settings" : template.name();
        String type = ContentTerrain.worldType().trim().toLowerCase(Locale.ROOT);
        String[] base = BASES.get(type);
        if (base == null) {
            ContentLog.LOGGER.error("worldType '{}' is not one of default, largebiomes or amplified on this version, building the shaped world on default", type);
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
        JsonObject options = ContentTerrain.generatorOptions();
        if (options != null) {
            seaLevel = GsonHelper.getAsInt(options, "seaLevel", -1);
            lavaOceans = GsonHelper.getAsBoolean(options, "useLavaOceans", false);
            for (String key : options.keySet()) {
                if (!key.equals("seaLevel") && !key.equals("useLavaOceans") && WARNED.add("generatorOptions." + key)) { ContentLog.LOGGER.info("generatorOptions sets '{}', which this version does not read; seaLevel and useLavaOceans are the keys read here", key); }
            }
        }
        return new Shape(id, name, base, minY, maxY, deepStone.isEmpty() ? null : deepStone, seaLevel, lavaOceans);
    }

    private static JsonObject dimension(Shape shape, String dimension, boolean flatBedrock, boolean isVoid) {
        JsonObject out = new JsonObject();
        String path = dimension.substring(dimension.indexOf(':') + 1);
        boolean overworld = OVERWORLD.equals(dimension);
        String typeId = dimension;
        if (overworld && shape.tall()) {
            JsonObject type = vanilla("dimension_type/overworld.json");
            if (type != null) {
                type.addProperty("min_y", shape.minY());
                type.addProperty("height", shape.maxY() - shape.minY());
                type.addProperty("logical_height", shape.maxY() - shape.minY());
                typeId = made(shape, path + "_type", "dimension_type", type);
            }
        }
        out.addProperty("type", typeId);
        JsonObject generator = new JsonObject();
        if (isVoid) {
            generator.addProperty("type", "minecraft:flat");
            JsonObject settings = new JsonObject();
            settings.addProperty("biome", "minecraft:the_void");
            settings.addProperty("features", false);
            settings.addProperty("lakes", false);
            settings.add("layers", new JsonArray());
            generator.add("settings", settings);
            MADE.add(ResourceLocation.fromNamespaceAndPath(shape.id().getNamespace(), shape.id().getPath() + "_" + path + "_void"));
            out.add("generator", generator);
            return out;
        }
        generator.addProperty("type", "minecraft:noise");
        JsonObject source = new JsonObject();
        if (END.equals(dimension)) { source.addProperty("type", "minecraft:the_end"); }
        else {
            source.addProperty("type", "minecraft:multi_noise");
            source.addProperty("preset", overworld ? OVERWORLD : "minecraft:nether");
        }
        generator.add("biome_source", source);
        String vanillaSettings = overworld ? shape.base()[1] : NETHER.equals(dimension) ? "nether" : "end";
        String settingsId = "minecraft:" + vanillaSettings;
        if ((overworld && shape.shapesOverworld()) || flatBedrock) {
            JsonObject settings = vanilla("worldgen/noise_settings/" + vanillaSettings + ".json");
            if (settings != null) {
                if (overworld) { shapeNoise(settings, shape); }
                if (flatBedrock) { flattenBedrock(settings, dimension); }
                settingsId = made(shape, path + "_noise", "worldgen/noise_settings", settings);
            }
        }
        generator.addProperty("settings", settingsId);
        out.add("generator", generator);
        return out;
    }

    private static String made(Shape shape, String suffix, String folder, JsonObject json) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(shape.id().getNamespace(), shape.id().getPath() + "_" + suffix);
        GeneratedResources.put(PackType.SERVER_DATA, id.getNamespace(), folder + "/" + id.getPath() + ".json", json.toString());
        MADE.add(id);
        return id.toString();
    }

    private static void shapeNoise(JsonObject settings, Shape shape) {
        JsonObject noise = GsonHelper.getAsJsonObject(settings, "noise");
        noise.addProperty("min_y", shape.minY());
        noise.addProperty("height", shape.maxY() - shape.minY());
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
        gradient.add("true_at_and_below", anchor("absolute", VANILLA_MIN));
        gradient.add("false_at_and_above", anchor("absolute", VANILLA_MIN + BLEND));
        JsonArray sequence = sequence(settings);
        sequence.asList().add(1, condition(gradient, block(shape.deepStone())));
    }

    private static void flattenBedrock(JsonObject settings, String dimension) {
        int layers = layers();
        JsonArray sequence = sequence(settings);
        for (int index = 0; index < sequence.size(); index++) {
            JsonElement element = sequence.get(index);
            if (!element.isJsonObject()) { continue; }
            JsonObject entry = element.getAsJsonObject();
            String gradient = gradientName(entry);
            if (gradient == null) { continue; }
            boolean roof = gradient.endsWith(BEDROCK_ROOF);
            if (roof && !roofWanted()) { continue; }
            if (!roof && !gradient.endsWith(BEDROCK_FLOOR)) { continue; }
            JsonObject bedrock = block("minecraft:bedrock");
            JsonObject flat;
            if (roof) { flat = condition(yAbove(anchor("below_top", layers - 1)), bedrock); }
            else {
                JsonObject not = new JsonObject();
                not.addProperty("type", "minecraft:not");
                not.add("invert", yAbove(anchor("above_bottom", layers)));
                flat = condition(not, bedrock);
            }
            List<String> biomes = bedrockBiomes();
            if (biomes.isEmpty()) {
                sequence.set(index, flat);
                continue;
            }
            JsonObject inBiomes = new JsonObject();
            inBiomes.addProperty("type", "minecraft:biome");
            JsonArray names = new JsonArray();
            for (String biome : biomes) { names.add(biome); }
            inBiomes.add("biome_is", names);
            JsonObject outside = new JsonObject();
            outside.addProperty("type", "minecraft:not");
            outside.add("invert", inBiomes);
            boolean blacklist = ContentControl.flag(ContentControl.BEDROCK, "flatBedrockBiomesAreBlacklist", Config.worldgen.flatBedrockBiomesAreBlacklist());
            JsonObject both = new JsonObject();
            both.addProperty("type", "minecraft:sequence");
            JsonArray pair = new JsonArray();
            pair.add(condition(blacklist ? outside : inBiomes, flat));
            pair.add(condition(blacklist ? inBiomes : outside, entry));
            both.add("sequence", pair);
            sequence.set(index, both);
        }
        ContentLog.LOGGER.debug("Flattened the bedrock of {} to {} layer(s){}", dimension, layers, roofWanted() ? " including the roof" : "");
    }

    @Nullable private static String gradientName(JsonObject entry) {
        if (!"minecraft:condition".equals(GsonHelper.getAsString(entry, "type", ""))) { return null; }
        JsonObject test = GsonHelper.getAsJsonObject(entry, "if_true", new JsonObject());
        if ("minecraft:not".equals(GsonHelper.getAsString(test, "type", ""))) { test = GsonHelper.getAsJsonObject(test, "invert", new JsonObject()); }
        if (!"minecraft:vertical_gradient".equals(GsonHelper.getAsString(test, "type", ""))) { return null; }
        return GsonHelper.getAsString(test, "random_name", "");
    }

    private static JsonArray sequence(JsonObject settings) {
        JsonObject rule = GsonHelper.getAsJsonObject(settings, "surface_rule");
        if ("minecraft:sequence".equals(GsonHelper.getAsString(rule, "type", ""))) { return GsonHelper.getAsJsonArray(rule, "sequence"); }
        JsonObject wrapped = new JsonObject();
        wrapped.addProperty("type", "minecraft:sequence");
        JsonArray sequence = new JsonArray();
        sequence.add(rule);
        wrapped.add("sequence", sequence);
        settings.add("surface_rule", wrapped);
        return sequence;
    }

    private static JsonObject anchor(String kind, int value) {
        JsonObject anchor = new JsonObject();
        anchor.addProperty(kind, value);
        return anchor;
    }

    private static JsonObject yAbove(JsonObject anchor) {
        JsonObject test = new JsonObject();
        test.addProperty("type", "minecraft:y_above");
        test.add("anchor", anchor);
        test.addProperty("surface_depth_multiplier", 0);
        test.addProperty("add_stone_depth", false);
        return test;
    }

    private static JsonObject condition(JsonObject test, JsonObject then) {
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:condition");
        out.add("if_true", test);
        out.add("then_run", then);
        return out;
    }

    private static JsonObject block(String name) {
        JsonObject state = new JsonObject();
        state.addProperty("Name", name);
        JsonObject out = new JsonObject();
        out.addProperty("type", "minecraft:block");
        out.add("result_state", state);
        return out;
    }

    @Nullable private static JsonObject vanilla(String path) {
        if (vanilla == null) { vanilla = ServerPacksSource.createVanillaPackSource(); }
        ResourceLocation at = ResourceLocation.fromNamespaceAndPath("minecraft", path);
        IoSupplier<InputStream> supplier = vanilla.getResource(PackType.SERVER_DATA, at);
        if (supplier == null) {
            ContentLog.LOGGER.error("The game's own {} could not be found, so that part of the world shape is left as the game makes it", at);
            return null;
        }
        try (InputStream in = supplier.get(); Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) { return GSON.fromJson(reader, JsonObject.class); }
        catch (IOException | JsonParseException ex) {
            ContentLog.LOGGER.error("The game's own {} could not be read, so that part of the world shape is left as the game makes it", at, ex);
            return null;
        }
    }

    private static boolean bedrockAsked() {
        if (ContentControl.off(ContentControl.BEDROCK)) { return false; }
        return ContentControl.flag(ContentControl.BEDROCK, "flatBedrock", Config.worldgen.flatBedrock());
    }

    private static boolean bedrockApplies(String dimension) {
        if (!bedrockAsked()) { return false; }
        return listed(dimension, ContentControl.list(ContentControl.BEDROCK, "flatBedrockDimensions", Config.worldgen.flatBedrockDimensions()),
                ContentControl.flag(ContentControl.BEDROCK, "flatBedrockDimensionsAreBlacklist", Config.worldgen.flatBedrockDimensionsAreBlacklist()));
    }

    private static int layers() { return Mth.clamp(ContentControl.number(ContentControl.BEDROCK, "bedrockLayers", Config.worldgen.bedrockLayers()), 1, 5); }

    private static boolean roofWanted() { return ContentControl.flag(ContentControl.BEDROCK, "flatBedrockRoof", Config.worldgen.flatBedrockRoof()); }

    private static List<String> bedrockBiomes() { return ContentControl.list(ContentControl.BEDROCK, "flatBedrockBiomes", Config.worldgen.flatBedrockBiomes()); }

    private static boolean voidAsked() {
        if (ContentControl.off(ContentControl.VOID)) { return false; }
        return ContentControl.flag(ContentControl.VOID, "voidWorld", Config.worldgen.voidWorld());
    }

    private static List<String> voidDimensions() { return ContentControl.list(ContentControl.VOID, "voidWorldDimensions", Config.worldgen.voidWorldDimensions()); }

    private static boolean voidApplies(String dimension) {
        if (!voidAsked()) { return false; }
        List<String> wanted = voidDimensions();
        return listed(dimension, wanted.isEmpty() ? List.of(OVERWORLD) : wanted, ContentControl.flag(ContentControl.VOID, "voidWorldDimensionsAreBlacklist", Config.worldgen.voidWorldDimensionsAreBlacklist()));
    }

    private static boolean voidApplies(ServerLevel level) { return voidApplies(level.dimension().location().toString()); }

    private static boolean listed(String dimension, List<String> entries, boolean blacklist) {
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

    @Nullable private static Block block(String name, String key) {
        Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(name.trim()));
        if (found == null && WARNED.add(key + ":" + name)) { ContentLog.LOGGER.error("{} names block '{}', which is not registered, so it does nothing", key, name); }
        return found;
    }

    public static void onCreateSpawn(LevelEvent.CreateSpawnPosition event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        if (voidApplies(level)) {
            BlockPos center = platformCenter(level);
            platform(level, center);
            event.getSettings().setSpawn(center.above(), 0.0F);
            level.getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, level.getServer());
            event.setCanceled(true);
            Summary.info("void", "Made a void world with a platform at " + center.getX() + ", " + center.getY() + ", " + center.getZ());
            return;
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
        level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
    }

    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel level = event.getServer().overworld();
        if (level.getLevelData().getGameTime() != 0L) { return; }
        int border = ContentTerrain.worldBorder();
        if (border <= 0) { return; }
        level.getWorldBorder().setSize(border);
        Summary.info("border", "Set the world border to " + border + " blocks across");
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) { return; }
        MinecraftServer server = level.getServer();
        int time = ContentTerrain.worldTime();
        if (time >= 0) {
            level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
            level.setDayTime(time);
        }
        Difficulty difficulty = ContentTerrain.worldDifficulty();
        if (difficulty != null) {
            server.setDifficulty(difficulty, true);
            server.setDifficultyLocked(true);
        }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        standOn(player);
        tell(player);
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { standOn(player); }
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { standOn(player); }
    }

    private static void tell(ServerPlayer player) {
        if (!Config.worldgen.tellWorldType() || presetName == null) { return; }
        ServerLevel level = player.serverLevel();
        if (level.dimension() != Level.OVERWORLD || !madeHere(level)) { return; }
        player.sendSystemMessage(Component.literal(Lang.tr(player, "rdpl.world.type", presetName)));
    }

    private static boolean madeHere(ServerLevel level) {
        ResourceKey<?> type = level.dimensionTypeRegistration().unwrapKey().orElse(null);
        if (type != null && MADE.contains(type.location())) { return true; }
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (generator instanceof NoiseBasedChunkGenerator noise) {
            ResourceKey<?> settings = noise.generatorSettings().unwrapKey().orElse(null);
            return settings != null && MADE.contains(settings.location());
        }
        return generator instanceof FlatLevelSource && voidApplies(level);
    }

    private static void standOn(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (!voidApplies(level)) { return; }
        BlockPos center = platformCenter(level);
        if (level.isEmptyBlock(center)) { platform(level, center); }
        if (player.getY() >= center.getY() + 1 && Math.abs(player.getX() - (center.getX() + 0.5D)) <= 0.5D && Math.abs(player.getZ() - (center.getZ() + 0.5D)) <= 0.5D) { return; }
        player.teleportTo(center.getX() + 0.5D, center.getY() + 1, center.getZ() + 0.5D);
        player.fallDistance = 0.0F;
    }

    private static BlockPos platformCenter(ServerLevel level) {
        int asked = ContentControl.number(ContentControl.VOID, "voidPlatformHeight", Config.worldgen.voidPlatformHeight());
        return new BlockPos(0, Mth.clamp(asked, level.getMinBuildHeight() + 1, level.getMaxBuildHeight() - 2), 0);
    }

    private static void platform(ServerLevel level, BlockPos center) {
        String name = ContentControl.text(ContentControl.VOID, "voidPlatformBlock", Config.worldgen.voidPlatformBlock());
        Block found = block(name, "voidPlatformBlock");
        BlockState state = found == null ? Blocks.STONE.defaultBlockState() : found.defaultBlockState();
        int reach = Math.max(0, (ContentControl.number(ContentControl.VOID, "voidPlatformSize", Config.worldgen.voidPlatformSize()) - 1) / 2);
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) { level.setBlock(center.offset(x, 0, z), state, 2); }
        }
    }
}
