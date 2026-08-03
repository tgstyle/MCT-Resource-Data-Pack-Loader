package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class ContentWorldTemplates {
    private static final Map<String, BiomeDictionary.Type> ROLES = roles();
    private static final List<String> ROLE_ORDER = Collections.unmodifiableList(Arrays.asList(
            "ocean", "river", "beach", "mushroom", "swamp", "hills", "mountain", "jungle",
            "forest", "savanna", "sandy", "mesa", "snowy", "wasteland", "plains", "water"));
    private static final Map<ResourceLocation, WorldTemplateDef> DEFS = new LinkedHashMap<>();
    private static final Map<Biome, Biome> RESOLVED = new LinkedHashMap<>();
    @Nullable private static WorldTemplateDef active;
    private static boolean loaded;

    private ContentWorldTemplates() {}

    public static Map<String, BiomeDictionary.Type> knownRoles() { return ROLES; }

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.load) { return; }

        for (WorldTemplateDef def : builtins()) { DEFS.put(def.registryName, def); }

        PackManager.get().forEach(PackManager.WORLDTEMPLATES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try {
                WorldTemplateDef def = ContentParser.worldTemplate(key, contents);
                if (def != null) { DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in world template {}, ignoring it: {}", key, ex.getMessage()); }
        });

        active = select();
        ContentControl.check(active);
        ContentStructures.load();
        if (active != null) { Summary.info("worldtemplate", "Using world template " + active.getKey() + " (" + active.name + ") to fill biomes that blocking removes"); }
    }

    @Nullable public static WorldTemplateDef active() { return active; }

    public static Map<ResourceLocation, WorldTemplateDef> all() { return Collections.unmodifiableMap(DEFS); }

    public static boolean isVoid() { return active != null && WorldTemplateDef.VOID.equals(active.fallback) && active.roles.isEmpty(); }

    public static void resolve(Iterable<Biome> blocked) {
        RESOLVED.clear();
        if (active == null) { return; }

        List<String> missing = new ArrayList<>();
        for (Biome biome : blocked) {
            Biome target = replacementFor(biome, missing);
            if (target != null && target != biome) { RESOLVED.put(biome, target); }
        }
        if (!missing.isEmpty()) { ContentLog.LOGGER.error("World template {} names biome(s) {}, which are not registered, so those roles fall through", active.getKey(), missing); }
    }

    @Nullable public static Biome replacement(Biome blocked) { return RESOLVED.get(blocked); }

    public static boolean appliesTo(int dimension) {
        if (active == null) { return false; }
        if (active.dimensions.isEmpty()) { return true; }

        return active.dimensions.contains(dimension);
    }

    @Nullable private static Biome replacementFor(Biome blocked, List<String> missing) {
        if (active == null) { return null; }

        for (String role : ROLE_ORDER) {
            String named = active.roles.get(role);
            if (named == null || named.isEmpty()) { continue; }

            BiomeDictionary.Type type = ROLES.get(role);
            if (type == null || !BiomeDictionary.hasType(blocked, type)) { continue; }

            Biome found = biome(named, missing);
            if (found != null) { return found; }
        }
        return biome(active.fallback, missing);
    }

    @Nullable private static Biome biome(String name, List<String> missing) {
        if (name.isEmpty() || WorldTemplateDef.VOID.equals(name)) { return null; }

        ResourceLocation key = new ResourceLocation(name);
        Biome found = ForgeRegistries.BIOMES.containsKey(key) ? ForgeRegistries.BIOMES.getValue(key) : null;
        if (found == null && !missing.contains(name)) { missing.add(name); }

        return found;
    }

    @Nullable private static WorldTemplateDef select() {
        String wanted = Config.worldgen.worldTemplate.trim();
        if (wanted.isEmpty()) { return null; }

        List<WorldTemplateDef> usable = new ArrayList<>();
        for (Map.Entry<ResourceLocation, WorldTemplateDef> entry : DEFS.entrySet()) {
            if (ContentRegistry.available(entry.getValue().requires, entry.getKey())) { usable.add(entry.getValue()); }
        }

        for (WorldTemplateDef def : usable) {
            if (def.getKey().equalsIgnoreCase(wanted) || def.registryName.getPath().equalsIgnoreCase(wanted)) { return def; }
        }

        if ("auto".equalsIgnoreCase(wanted)) {
            WorldTemplateDef chosen = null;
            for (WorldTemplateDef def : usable) {
                if (!"rdpl".equals(def.registryName.getNamespace())) { chosen = def; }
            }
            return chosen;
        }

        ContentLog.LOGGER.error("worldTemplate is '{}' but nothing defines it. Known templates: {}", wanted, DEFS.keySet());
        return null;
    }

    private static List<WorldTemplateDef> builtins() {
        List<WorldTemplateDef> list = new ArrayList<>();
        list.add(built("void", "Void", WorldTemplateDef.VOID, Collections.emptyMap()));

        Map<String, String> vanilla = new LinkedHashMap<>();
        vanilla.put("ocean", "minecraft:ocean");
        vanilla.put("river", "minecraft:river");
        vanilla.put("beach", "minecraft:beach");
        vanilla.put("mushroom", "minecraft:mushroom_island");
        vanilla.put("swamp", "minecraft:swampland");
        vanilla.put("hills", "minecraft:extreme_hills");
        vanilla.put("mountain", "minecraft:extreme_hills");
        list.add(built("vanilla", "Vanilla shoreline", "minecraft:plains", vanilla));

        Map<String, String> water = new LinkedHashMap<>();
        water.put("river", "minecraft:river");
        water.put("beach", "minecraft:beach");
        list.add(built("ocean", "Ocean world", "minecraft:ocean", water));

        list.add(built("plains", "Plains world", "minecraft:plains", Collections.emptyMap()));
        list.add(built("desert", "Desert world", "minecraft:desert", Collections.emptyMap()));
        return list;
    }

    private static WorldTemplateDef built(String path, String name, String fallback, Map<String, String> roles) {
        return new WorldTemplateDef(new ResourceLocation("rdpl", path), name, fallback, Collections.unmodifiableMap(roles), Collections.emptyMap(), null, Collections.emptyList(), Collections.emptyList());
    }

    private static Map<String, BiomeDictionary.Type> roles() {
        Map<String, BiomeDictionary.Type> map = new LinkedHashMap<>();
        map.put("ocean", BiomeDictionary.Type.OCEAN);
        map.put("river", BiomeDictionary.Type.RIVER);
        map.put("water", BiomeDictionary.Type.WATER);
        map.put("beach", BiomeDictionary.Type.BEACH);
        map.put("mushroom", BiomeDictionary.Type.MUSHROOM);
        map.put("swamp", BiomeDictionary.Type.SWAMP);
        map.put("hills", BiomeDictionary.Type.HILLS);
        map.put("mountain", BiomeDictionary.Type.MOUNTAIN);
        map.put("jungle", BiomeDictionary.Type.JUNGLE);
        map.put("forest", BiomeDictionary.Type.FOREST);
        map.put("savanna", BiomeDictionary.Type.SAVANNA);
        map.put("sandy", BiomeDictionary.Type.SANDY);
        map.put("mesa", BiomeDictionary.Type.MESA);
        map.put("snowy", BiomeDictionary.Type.SNOWY);
        map.put("wasteland", BiomeDictionary.Type.WASTELAND);
        map.put("plains", BiomeDictionary.Type.PLAINS);
        map.put("nether", BiomeDictionary.Type.NETHER);
        map.put("end", BiomeDictionary.Type.END);
        return Collections.unmodifiableMap(map);
    }

    public static String describeRoles() { return String.join(", ", ROLES.keySet()); }
}
