package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentOwners;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import static mctmods.resourcedatapackloader.util.Json.strings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentBiomes {
    private static final List<String> RATE_KEYS = Collections.unmodifiableList(Arrays.asList(
            "surfaceDay", "surfaceNight", "undergroundDay", "undergroundNight"));

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onCreateDecorator(BiomeEvent.CreateDecorator event) {
        if (!(event.getBiome() instanceof ContentBiome)) { return; }
        ((ContentBiome) event.getBiome()).applyDecoration(event.getNewBiomeDecorator());
    }

    public static void replaceStone(ChunkPrimer primer, Biome[] biomes) {
        if (biomes == null) { return; }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = biomes[z * 16 + x];
                if (!(biome instanceof ContentBiome)) { continue; }
                IBlockState stone = ((ContentBiome) biome).getStoneState();
                if (stone == null) { continue; }
                for (int y = 0; y < 256; y++) {
                    if (primer.getBlockState(x, y, z).getBlock() == Blocks.STONE) { primer.setBlockState(x, y, z, stone); }
                }
            }
        }
    }

    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ResourceLocation, BiomeDef> DEFS = new LinkedHashMap<>();

    static Collection<BiomeDef> defs() { return DEFS.values(); }
    private static final List<ContentBiome> REGISTERED = new ArrayList<>();
    private static boolean loaded;

    private ContentBiomes() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.biomes) { return false; }
        PackManager.get().forEach(PackManager.BIOMES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (ContentOwners.reserved(key)) { return; }
            try { read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in biome file {}, ignoring it", key, ex); }
        });
        if (!DEFS.isEmpty()) { Summary.info("biomes", "Loaded " + DEFS.size() + " biome definition(s) from packs"); }
        return !DEFS.isEmpty();
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Biome file {} is empty, ignoring it", key);
            return;
        }
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (json.has("spawns")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "spawns")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                String entity = JsonUtils.getString(entry, "entity", "");
                if (entity.isEmpty()) {
                    ContentLog.LOGGER.error("A spawn entry in {} names no entity, skipping it", key);
                    continue;
                }
                int min = Math.max(1, JsonUtils.getInt(entry, "min", 1));
                spawns.add(new SpawnEntryDef(JsonUtils.getString(entry, "type", "creature"), entity,
                        Math.max(1, JsonUtils.getInt(entry, "weight", 10)), min, Math.max(min, JsonUtils.getInt(entry, "max", min))));
            }
        }
        Map<String, Integer> decoration = new LinkedHashMap<>();
        JsonObject decorationJson = JsonUtils.getJsonObject(json, "decoration", new JsonObject());
        for (Map.Entry<String, JsonElement> entry : decorationJson.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) { continue; }
            decoration.put(entry.getKey(), entry.getValue().getAsInt());
        }
        for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "spawnRates", new JsonObject()).entrySet()) {
            if (RATE_KEYS.contains(entry.getKey())) { continue; }
            ContentLog.LOGGER.error("Biome {} sets the spawn rate '{}', which is not one of {}, so it does nothing. These are how often hostile mobs spawn, not creature types", key, entry.getKey(), RATE_KEYS);
        }
        JsonObject placement = JsonUtils.getJsonObject(json, "placement", new JsonObject());
        DEFS.put(key, new BiomeDef(key,
                JsonUtils.getString(json, "name", key.getPath()),
                JsonUtils.getInt(json, "id", BiomeDef.AUTO_ID),
                JsonUtils.getFloat(json, "temperature", 0.5F),
                JsonUtils.getFloat(json, "rainfall", 0.5F),
                JsonUtils.getFloat(json, "baseHeight", 0.1F),
                JsonUtils.getFloat(json, "heightVariation", 0.2F),
                JsonUtils.getBoolean(json, "snow", false),
                JsonUtils.getBoolean(json, "rain", true),
                ContentTypes.color(JsonUtils.getString(json, "waterColor", "FFFFFF"), key.toString()),
                JsonUtils.getString(json, "baseBiome", ""),
                JsonUtils.getString(json, "topBlock", ""),
                JsonUtils.getString(json, "fillerBlock", ""),
                JsonUtils.getString(json, "stoneBlock", ""),
                strings(json, "types"),
                JsonUtils.getString(placement, "climate", ""),
                Math.max(0, JsonUtils.getInt(placement, "weight", 10)),
                JsonUtils.getBoolean(placement, "playerSpawn", false),
                JsonUtils.getBoolean(placement, "villages", false),
                JsonUtils.getBoolean(placement, "villageSpawn", true),
                villageKind(JsonUtils.getString(json, "villageType", ""), key),
                JsonUtils.getBoolean(placement, "strongholds", false),
                Collections.unmodifiableMap(decoration),
                spawnChance(key, JsonUtils.getFloat(json, "spawnChance", 0.1F)),
                rate(json, "surfaceDay"),
                rate(json, "surfaceNight"),
                rate(json, "undergroundDay"),
                rate(json, "undergroundNight"),
                color(json, "grassColor", key),
                color(json, "foliageColor", key),
                json.has("minHeight") || json.has("maxHeight"),
                JsonUtils.getInt(json, "minHeight", Integer.MIN_VALUE),
                JsonUtils.getInt(json, "maxHeight", Integer.MAX_VALUE),
                strings(json, "replaces"),
                JsonUtils.getBoolean(json, "keepDefaultSpawns", false),
                Collections.unmodifiableList(spawns),
                strings(json, "requires")));
    }

    private static int villageKind(String written, ResourceLocation key) {
        if (written.isEmpty()) { return -1; }
        if ("oak".equalsIgnoreCase(written)) { return 0; }
        if ("sandstone".equalsIgnoreCase(written)) { return 1; }
        if ("acacia".equalsIgnoreCase(written)) { return 2; }
        if ("spruce".equalsIgnoreCase(written)) { return 3; }
        ContentLog.LOGGER.error("Biome {} sets villageType '{}', which is not oak, sandstone, acacia or spruce, so villages here build with oak as they would without it", key, written);
        return -1;
    }

    private static float spawnChance(ResourceLocation key, float wanted) {
        if (wanted < 0.99F) { return Math.max(0.0F, wanted); }
        ContentLog.LOGGER.error("Biome {} asks for a spawnChance of {}. The game keeps starting another herd for as long as that roll succeeds, so at 1 it never stops and the world fills until it runs out of room. Using 0.99 instead", key, wanted);
        return 0.99F;
    }

    private static float rate(JsonObject json, String name) {
        JsonObject rates = JsonUtils.getJsonObject(json, "spawnRates", new JsonObject());
        return rates.has(name) ? Math.max(0.0F, JsonUtils.getFloat(rates, name)) : -1.0F;
    }

    private static int color(JsonObject json, String name, ResourceLocation key) {
        if (!json.has(name)) { return ContentTypes.NO_COLOR; }
        return ContentTypes.color(JsonUtils.getString(json, name, "FFFFFF"), key.toString());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        for (BiomeDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            if (ForgeRegistries.BIOMES.containsKey(def.registryName)) {
                ContentLog.LOGGER.debug("Biome {} is already registered, leaving it alone", def.registryName);
                continue;
            }
            ModContainer previous = Loader.instance().activeModContainer();
            try {
                Loader.instance().setActiveModContainer(ContentOwners.of(def.registryName.getNamespace()));
                ContentBiome biome = ContentBiome.create(def);
                if (def.isAutoId() || !pin(event.getRegistry(), biome, def.id)) { event.getRegistry().register(biome); }
                REGISTERED.add(biome);
            }
            finally { Loader.instance().setActiveModContainer(previous); }
        }
        for (ContentBiome biome : REGISTERED) {
            ContentLog.LOGGER.debug("Biome {} registered with id {}{}", biome.getRegistryName(), Biome.getIdForBiome(biome), biome.getDef().isAutoId() ? "" : " (pinned by the pack)");
        }
        if (!REGISTERED.isEmpty()) { Summary.info("content_biomes", "Registered " + REGISTERED.size() + " biome(s) from packs"); }
    }

    @Nullable private static Method adder(IForgeRegistry<Biome> registry, ContentBiome biome) {
        for (Class<?> held = registry.getClass(); held != null; held = held.getSuperclass()) {
            for (Method one : held.getDeclaredMethods()) {
                if (!"add".equals(one.getName()) || one.getParameterCount() != 2) { continue; }
                if (one.getParameterTypes()[0] != int.class || !one.getParameterTypes()[1].isInstance(biome)) { continue; }
                return one;
            }
        }
        return null;
    }

    private static boolean pin(IForgeRegistry<Biome> registry, ContentBiome biome, int id) {
        try {
            Method add = adder(registry, biome);
            if (add == null) { throw new NoSuchMethodException("add(int, entry)"); }
            add.setAccessible(true);
            int given = (int) add.invoke(registry, id, biome);
            if (given != id) { ContentLog.LOGGER.error("Biome {} asks for id {}, which was already taken, so it has id {} instead", biome.getRegistryName(), id, given); }
            return true;
        }
        catch (ReflectiveOperationException | RuntimeException refused) {
            ContentLog.LOGGER.error("Biome {} asks for id {}, which this Forge build will not let a pack pin, so a free id is chosen instead", biome.getRegistryName(), id, refused);
            return false;
        }
    }

    public static void applyPlacement() {
        int placed = 0;
        for (ContentBiome biome : REGISTERED) {
            BiomeDef def = biome.getDef();
            biome.resolveSpawns();
            types(biome, def);
            if (def.spawnBiome) { BiomeManager.addSpawnBiome(biome); }
            if (def.villageBiome) { BiomeManager.addVillageBiome(biome, def.villageSpawn); }
            if (def.strongholdBiome) { BiomeManager.addStrongholdBiome(biome); }
            if (def.climate.isEmpty() || def.weight <= 0) { continue; }
            BiomeManager.BiomeType climate = BiomeManager.BiomeType.getType(def.climate);
            BiomeManager.addBiome(climate, new BiomeManager.BiomeEntry(biome, def.weight));
            placed++;
        }
        if (placed > 0) { Summary.info("content_biome_placement", "Placed " + placed + " biome(s) into world generation"); }
    }

    private static void types(ContentBiome biome, BiomeDef def) {
        if (def.types.isEmpty()) {
            BiomeDictionary.makeBestGuess(biome);
            ContentLog.LOGGER.debug("Biome {} lists no types, guessing them from its properties", def.registryName);
            return;
        }
        List<BiomeDictionary.Type> types = new ArrayList<>();
        for (String name : def.types) { types.add(BiomeDictionary.Type.getType(name)); }
        BiomeDictionary.addTypes(biome, types.toArray(new BiomeDictionary.Type[0]));
    }
}
