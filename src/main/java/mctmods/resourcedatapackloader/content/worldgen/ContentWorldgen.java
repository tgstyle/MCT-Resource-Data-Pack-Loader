package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.BlockWeightDef;
import mctmods.resourcedatapackloader.content.def.FollowDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentWorldgen {
    public static final String SHAPE_FEATURE = "shape";
    public static final String SPREAD_PLACEMENT = "spread";
    public static final String RETROGEN_TOKENS = "retrogen";
    private static final String UNDERGROUND = "underground_ores";
    private static final String VEGETAL = "vegetal_decoration";
    private static final String SURFACE = "surface_structures";
    private static final Map<ResourceLocation, WorldgenDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Entry> ENTRIES = new LinkedHashMap<>();
    private static boolean loaded;
    private static final Set<String> CHAIN_WARNED = new LinkedHashSet<>();
    private static final int EDGE_REACH = 16;

    private ContentWorldgen() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.worldgen.load()) { return; }
        Json.eachFile(PackManager.WORLDGEN, "worldgen definition", (key, contents) -> {
            WorldgenDef def = ContentWorldgenParser.parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("worldgen", "Loaded " + DEFS.size() + " worldgen entries"); }
    }

    public static void generate() {
        ENTRIES.clear();
        CHAIN_WARNED.clear();
        for (WorldgenDef def : DEFS.values()) {
            Entry entry = resolve(def);
            if (entry == null) { continue; }
            ENTRIES.put(def.key(), entry);
            write(entry);
        }
        if (!ENTRIES.isEmpty()) { Summary.info("worldgen.features", "Generated " + ENTRIES.size() + " feature(s) from worldgen entries"); }
        ContentRetrogen.setup(ENTRIES.values());
    }

    @Nullable public static Entry entry(ResourceLocation key) { return ENTRIES.get(key); }

    @Nullable public static Entry byName(String name) {
        ResourceLocation key = ResourceLocation.tryParse(name);
        if (key != null && ENTRIES.containsKey(key)) { return ENTRIES.get(key); }
        if (name.indexOf(':') >= 0) { return null; }
        for (Map.Entry<ResourceLocation, Entry> held : ENTRIES.entrySet()) {
            if (held.getKey().getPath().equals(name)) { return held.getValue(); }
        }
        return null;
    }

    public static List<String> veinNames() {
        List<String> names = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Entry> held : ENTRIES.entrySet()) {
            if (held.getValue().shape() instanceof ContentOreVein) { names.add(held.getKey().toString()); }
        }
        Collections.sort(names);
        return names;
    }

    public static void after(Entry entry, ContentPlacer placer, RandomSource random, BlockPos origin, List<WorldgenDef> chain) {
        WorldgenDef def = entry.def();
        ContentOreSigns.place(placer, def, random, origin);
        if (def.then().isEmpty()) { return; }
        chain.add(def);
        int rolls = def.thenCount().pick(random);
        List<FollowDef> left = new ArrayList<>(def.then());
        for (int roll = 0; roll < rolls && !left.isEmpty(); roll++) {
            FollowDef chosen = FollowDef.pick(left, random);
            if (chosen == null) { break; }
            left.remove(chosen);
            if (FollowDef.EMPTY.equals(chosen.name())) { continue; }
            Entry next = byName(chosen.name());
            if (next == null) {
                ContentLog.LOGGER.error("Worldgen entry {} queues {}, which no pack registers as worldgen, so nothing follows it", def.key(), chosen.name());
                continue;
            }
            if (chain.contains(next.def())) {
                if (CHAIN_WARNED.add(def.key() + ">" + chosen.name())) { ContentLog.LOGGER.error("Worldgen entry {} queues {}, which already generated earlier in this chain, so the chain stops here rather than running forever", def.key(), chosen.name()); }
                continue;
            }
            if (next.shape() instanceof IContentChunkShape) {
                ContentLog.LOGGER.error("Worldgen entry {} queues {}, whose shape cannot follow another entry, so nothing follows it", def.key(), chosen.name());
                continue;
            }
            int spread = chosen.spreadOr(def.thenSpread() >= 0 ? def.thenSpread() : Math.max(0, def.shape().radius().most()));
            int dx = random.nextInt(2 * spread + 1) - spread;
            int dz = random.nextInt(2 * spread + 1) - spread;
            int dy = chosen.depthOr(def.thenDepth(), random);
            if (dx == 0 && dy == 0 && dz == 0) { dy = -1; }
            BlockPos pos = edgeOf(placer, entry, origin, dx, dy, dz);
            if (pos.getY() < placer.floorY() || pos.getY() >= placer.ceilingY() || !ContentPlacer.loaded(placer.level(), pos)) { continue; }
            ContentLog.LOGGER.debug("The {} at {}, {}, {} queues {} at {}, {}, {}, off its edge", def.key(), origin.getX(), origin.getY(), origin.getZ(), chosen.name(), pos.getX(), pos.getY(), pos.getZ());
            ContentPlacer follower = placer.rebound(next.palette());
            if (next.shape().generate(follower, random, pos)) { after(next, follower, random, pos, chain); }
        }
        chain.remove(chain.size() - 1);
    }

    private static BlockPos edgeOf(ContentPlacer placer, Entry entry, BlockPos origin, int dx, int dy, int dz) {
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double ux = dx / length;
        double uy = dy / length;
        double uz = dz / length;
        int edge = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int step = 1; step <= EDGE_REACH; step++) {
            at.set(origin.getX() + (int) Math.round(ux * step), origin.getY() + (int) Math.round(uy * step), origin.getZ() + (int) Math.round(uz * step));
            if (ContentPlacer.loaded(placer.level(), at) && entry.palette().places(placer.level().getBlockState(at))) { edge = step; }
        }
        int out = edge + 1;
        return new BlockPos(Mth.clamp(origin.getX() + (int) Math.round(ux * out), placer.lowX(), placer.highX()), origin.getY() + (int) Math.round(uy * out), Mth.clamp(origin.getZ() + (int) Math.round(uz * out), placer.lowZ(), placer.highZ()));
    }

    public static boolean dimensionAllows(Entry entry, WorldGenLevel level) {
        if (entry.dimensions().isEmpty()) { return true; }
        return entry.dimensions().contains(level.getLevel().dimension().location()) != entry.def().dimensionsAreBlacklist();
    }

    public static boolean allows(Entry entry, WorldGenLevel level, BlockPos pos) {
        if (!entry.def().needsBiome()) { return true; }
        if (!ContentPlacer.loaded(level, pos)) { return false; }
        Holder<Biome> biome = level.getBiome(pos);
        if (!entry.def().caveRegions().isEmpty() && !inRegion(entry, biome)) { return false; }
        if (entry.def().hasBiomeFilter() && matches(entry, biome) == entry.def().biomesAreBlacklist()) { return false; }
        return entry.def().climateAllows(biome.value().getBaseTemperature(), biome.value().getModifiedClimateSettings().downfall());
    }

    private static boolean inRegion(Entry entry, Holder<Biome> biome) {
        for (ResourceLocation region : entry.def().caveRegions()) {
            if (biome.is(region)) { return true; }
        }
        return false;
    }

    private static boolean matches(Entry entry, Holder<Biome> biome) {
        for (ResourceLocation named : entry.biomes()) {
            if (biome.is(named)) { return true; }
        }
        for (TagKey<Biome> tag : entry.biomeTags()) {
            if (biome.is(tag)) { return true; }
        }
        return false;
    }

    @Nullable private static Entry resolve(WorldgenDef def) {
        ResourceLocation key = def.key();
        if (!ContentRegistry.available(def.requires(), key)) { return null; }
        Set<Block> targets = new LinkedHashSet<>();
        Set<BlockState> exact = new LinkedHashSet<>();
        bind(def, def.replaces(), "replace", targets, exact);
        if (targets.isEmpty() && exact.isEmpty()) {
            ContentLog.LOGGER.error("Worldgen {} has no registered block to replace, skipping it", key);
            return null;
        }
        Set<Block> nearby = new LinkedHashSet<>();
        Set<BlockState> nearbyExact = new LinkedHashSet<>();
        bind(def, def.adjacent(), "adjacent", nearby, nearbyExact);
        if (!def.adjacent().isEmpty() && nearby.isEmpty() && nearbyExact.isEmpty()) {
            ContentLog.LOGGER.error("Worldgen {} has no registered adjacent block, skipping it so it does not generate everywhere", key);
            return null;
        }
        Set<Block> surface = new LinkedHashSet<>();
        if (ShapeDef.DECORATION.equals(def.shape().type()) || ShapeDef.TREE.equals(def.shape().type())) {
            List<String> unknown = new ArrayList<>();
            for (String name : def.shape().surface()) {
                Block found = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(name.trim()));
                if (found == null) { unknown.add(name); }
                else { surface.add(found); }
            }
            if (!unknown.isEmpty() && !surface.isEmpty()) { ContentLog.LOGGER.error("Worldgen {} names surface block(s) {}, which are not registered, leaving them out", key, unknown); }
            else if (!unknown.isEmpty()) {
                ContentLog.LOGGER.error("Worldgen {} has no registered surface block, skipping it", key);
                return null;
            }
        }
        List<BlockState> states = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        if (def.blocks().isEmpty()) {
            BlockState state = state(def.block(), Map.of(), key);
            if (state == null) {
                ContentLog.LOGGER.error("Worldgen {} names block {}, which is not registered, skipping it", key, def.block());
                return null;
            }
            states.add(state);
            weights.add(1);
        }
        else {
            List<String> missing = new ArrayList<>();
            for (BlockWeightDef weighted : def.blocks()) {
                BlockState state = state(weighted.block(), weighted.properties(), key);
                if (state == null) { missing.add(weighted.block().toString()); }
                else {
                    states.add(state);
                    weights.add(weighted.weight());
                }
            }
            if (!missing.isEmpty() && !states.isEmpty()) { ContentLog.LOGGER.error("Worldgen {} names {} in its blocks list, which are not registered, leaving them out", key, missing); }
            else if (!missing.isEmpty()) {
                ContentLog.LOGGER.error("Worldgen {} names only {} in its blocks list, none of which are registered, skipping it. Add \"requires\" naming the mod so this is expected rather than an error", key, missing);
                return null;
            }
        }
        ContentPalette palette = new ContentPalette(states, weights, targets, exact, nearby, nearbyExact, surface);
        IContentShape shape = build(def, state(def.shape().outline(), key), state(def.shape().fill(), key), state(def.shape().log(), key), state(def.shape().leaves(), key));
        return new Entry(def, palette, shape, biomeNames(def), biomeTags(def), dimensions(def));
    }

    private static IContentShape build(WorldgenDef def, @Nullable BlockState outline, @Nullable BlockState fill, @Nullable BlockState log, @Nullable BlockState leaves) {
        ShapeDef shape = def.shape();
        switch (shape.type()) {
            case ShapeDef.PLATE: return new ContentPlate(shape);
            case ShapeDef.LARGEVEIN: return new ContentLargeVein(def.size(), def.sparse(), shape.slim());
            case ShapeDef.DECORATION: return new ContentDecoration(def.size(), shape);
            case ShapeDef.TREE: return new ContentTree(def.size(), shape, log, leaves, def.key());
            case ShapeDef.VINES: return new ContentVines(def.size(), shape);
            case ShapeDef.BASIN: return new ContentBasin(shape);
            case ShapeDef.SPIRE: return new ContentSpire(shape);
            case ShapeDef.NODULE: return new ContentNodule(shape);
            case ShapeDef.VENT: return new ContentVent(shape);
            case ShapeDef.IMPRINT: return new ContentImprint(shape, def.key(), def.replacesGiven());
            case ShapeDef.BELT: return new ContentBelt(shape, def.minHeight(), def.maxHeight(), def.key());
            case ShapeDef.VEIN: return new ContentOreVein(shape, def.size(), def.attempts(), def.minHeight(), def.maxHeight(), def.key(), tier(def, shape.rich(), "rich"), tier(def, shape.poor(), "poor"));
            case ShapeDef.GEODE:
                if (outline != null) { return new ContentGeode(shape, outline, fill); }
                ContentLog.LOGGER.error("Worldgen {} makes a geode but names no registered outline block, so it generates as a cluster", def.key());
                return new ContentVein(def.size(), def.sparse());
            case ShapeDef.FIELD:
                if (shape.field() != null) { return new ContentFieldShape(shape.field(), shape, def.minHeight(), def.maxHeight(), def.key()); }
                ContentLog.LOGGER.error("Worldgen {} asks for a field but describes none, so it generates as a cluster", def.key());
                return new ContentVein(def.size(), def.sparse());
            default: return new ContentVein(def.size(), def.sparse());
        }
    }

    @Nullable private static BlockState tier(WorldgenDef def, String named, String which) {
        if (named.isEmpty()) { return null; }
        BlockState state = state(named, def.key());
        if (state == null) { ContentLog.LOGGER.error("Worldgen {} names {} as its {} block, which is not a registered block, so that tier places the normal block", def.key(), named, which); }
        return state;
    }

    private static void bind(WorldgenDef def, List<BlockMatchDef> names, String where, Set<Block> whole, Set<BlockState> exact) {
        for (BlockMatchDef name : names) {
            Block block = Registered.find(ForgeRegistries.BLOCKS, name.block());
            if (block == null) {
                ContentLog.LOGGER.error("Worldgen {} names {} block {}, which is not registered, leaving it out", def.key(), where, name.block());
                continue;
            }
            if (name.properties().isEmpty()) { whole.add(block); }
            else { exact.addAll(ContentStates.matching(block, name.properties(), def.key() + " " + where)); }
        }
    }

    @Nullable private static BlockState state(ResourceLocation name, Map<String, String> properties, ResourceLocation key) {
        Block block = Registered.find(ForgeRegistries.BLOCKS, name);
        if (block == null) { return null; }
        if (properties.isEmpty()) { return block.defaultBlockState(); }
        List<BlockState> found = ContentStates.matching(block, properties, key);
        return found.isEmpty() ? block.defaultBlockState() : found.get(0);
    }

    @Nullable private static BlockState state(String name, ResourceLocation key) {
        return name.isEmpty() ? null : state(ResourceLocation.tryParse(name), Map.of(), key);
    }

    private static Set<ResourceLocation> biomeNames(WorldgenDef def) {
        Set<ResourceLocation> named = new LinkedHashSet<>();
        for (String name : def.biomes()) {
            ResourceLocation biome = ResourceLocation.tryParse(name.trim());
            if (biome == null) { ContentLog.LOGGER.error("Worldgen {} names biome '{}', which is not a valid id, leaving it out", def.key(), name); }
            else { named.add(biome); }
        }
        return Set.copyOf(named);
    }

    private static List<TagKey<Biome>> biomeTags(WorldgenDef def) {
        List<TagKey<Biome>> tags = new ArrayList<>();
        for (String type : def.biomeTypes()) {
            String tag = ContentFormats.biomeTag(type);
            if (tag == null) {
                ContentLog.LOGGER.error("Worldgen {} names biome type '{}', which no biome tag on this line answers to", def.key(), type);
                continue;
            }
            ResourceLocation named = ResourceLocation.tryParse(tag);
            if (named != null) { tags.add(TagKey.create(Registries.BIOME, named)); }
        }
        return List.copyOf(tags);
    }

    private static Set<ResourceLocation> dimensions(WorldgenDef def) {
        Set<ResourceLocation> named = new LinkedHashSet<>();
        for (String entry : def.dimensions()) {
            ResourceLocation dimension = ResourceLocation.tryParse(ContentFormats.dimensionId(entry));
            if (dimension == null) { ContentLog.LOGGER.error("Worldgen {} names dimension '{}', which is not a valid id, leaving it out", def.key(), entry); }
            else { named.add(dimension); }
        }
        return Set.copyOf(named);
    }

    private static void write(Entry entry) {
        ResourceLocation key = entry.def().key();
        String namespace = key.getNamespace();
        JsonObject config = new JsonObject();
        config.addProperty("entry", key.toString());
        JsonObject configured = new JsonObject();
        configured.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + SHAPE_FEATURE);
        configured.add("config", config);
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.CONFIGURED_FEATURES + "/" + key.getPath() + ".json", configured.toString());
        JsonObject spread = new JsonObject();
        spread.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + SPREAD_PLACEMENT);
        spread.addProperty("entry", key.toString());
        JsonArray placement = new JsonArray();
        placement.add(spread);
        JsonObject placed = new JsonObject();
        placed.addProperty("feature", key.toString());
        placed.add("placement", placement);
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.PLACED_FEATURES + "/" + key.getPath() + ".json", placed.toString());
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", ContentFormats.ADD_FEATURES);
        modifier.add("biomes", ContentFormats.anyBiomes());
        modifier.addProperty("features", key.toString());
        modifier.addProperty("step", step(entry.def().shape()));
        GeneratedResources.put(PackType.SERVER_DATA, namespace, ContentFormats.BIOME_MODIFIERS + "/" + key.getPath() + "_features.json", modifier.toString());
        ContentLog.LOGGER.debug("Worldgen {} generates as a {} shape on the {} step", key, entry.def().shape().type(), step(entry.def().shape()));
    }

    private static String step(ShapeDef shape) {
        return switch (shape.type()) {
            case ShapeDef.DECORATION, ShapeDef.TREE, ShapeDef.VINES -> VEGETAL;
            case ShapeDef.IMPRINT -> SURFACE;
            default -> UNDERGROUND;
        };
    }

    public record Entry(WorldgenDef def, ContentPalette palette, IContentShape shape, Set<ResourceLocation> biomes, List<TagKey<Biome>> biomeTags, Set<ResourceLocation> dimensions) {}
}
