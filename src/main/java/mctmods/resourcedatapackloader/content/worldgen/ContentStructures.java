package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.compat.IForgettingStarts;

import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentStructures {
    public static final Set<String> MAP_GENS = mapGens();
    private static final Map<String, String> STRUCTURE_NAMES = structureNames();
    public static final Map<String, PopulateChunkEvent.Populate.EventType> POPULATES = populates();
    private static final Map<Integer, Map<String, Boolean>> BY_DIMENSION = new LinkedHashMap<>();

    private ContentStructures() {}

    public static void load() {
        BY_DIMENSION.clear();
        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template != null && !template.structures.isEmpty()) { remember(template.structures, template.dimensions); }
        if (!BY_DIMENSION.isEmpty()) { Summary.info("structures", "Controlling structure generation in " + BY_DIMENSION.keySet()); }
    }

    public static void remember(Map<String, Boolean> settings, List<Integer> dimensions) {
        if (settings.isEmpty()) { return; }
        if (dimensions.isEmpty()) {
            BY_DIMENSION.computeIfAbsent(Integer.MIN_VALUE, key -> new LinkedHashMap<>()).putAll(settings);
            return;
        }
        for (int dimension : dimensions) { BY_DIMENSION.computeIfAbsent(dimension, key -> new LinkedHashMap<>()).putAll(settings); }
    }

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.STRUCTURES)) { return false; }
        return !BY_DIMENSION.isEmpty();
    }

    public static void watchStarts(World world, MapGenStructure generator) {
        STARTS_WATCHED.computeIfAbsent(world, held -> Collections.newSetFromMap(new WeakHashMap<>())).add(generator);
    }

    public static void forgetFarStarts(World world, int chunkX, int chunkZ) {
        Set<MapGenStructure> watched = STARTS_WATCHED.get(world);
        if (watched == null) { return; }
        for (MapGenStructure generator : watched) { ((IForgettingStarts) generator).rdpl$forgetFarStarts(chunkX, chunkZ); }
    }

    private static final Map<World, Set<MapGenStructure>> STARTS_WATCHED = new WeakHashMap<>();

    public static boolean blocks(World world, MapGenBase generator) {
        if (BY_DIMENSION.isEmpty() || world == null || world.provider == null) { return false; }
        String key = keyFor(generator);
        return key != null && !allows(world.provider.getDimension(), key);
    }

    public static boolean blocks(World world, String structure) {
        if (BY_DIMENSION.isEmpty() || world == null || world.provider == null) { return false; }
        String key = STRUCTURE_NAMES.get(structure);
        return key != null && !allows(world.provider.getDimension(), key);
    }

    @Nullable private static String keyFor(MapGenBase generator) {
        if (generator instanceof MapGenStructure) { return STRUCTURE_NAMES.get(((MapGenStructure) generator).getStructureName()); }
        if (generator instanceof MapGenCaves) { return "caves"; }
        if (generator instanceof MapGenRavine) { return "ravines"; }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onPopulate(PopulateChunkEvent.Populate event) {
        World world = event.getWorld();
        if (world == null || world.provider == null) { return; }
        String key = keyFor(event.getType());
        if (key == null || allows(world.provider.getDimension(), key)) { return; }
        event.setResult(Event.Result.DENY);
    }

    private static boolean allows(int dimension, String key) {
        Boolean scoped = lookup(BY_DIMENSION.get(dimension), key);
        if (scoped != null) { return scoped; }
        Boolean global = lookup(BY_DIMENSION.get(Integer.MIN_VALUE), key);
        return global == null || global;
    }

    @Nullable private static Boolean lookup(@Nullable Map<String, Boolean> settings, String key) { return settings == null ? null : settings.get(key); }

    @Nullable private static String keyFor(PopulateChunkEvent.Populate.EventType type) {
        for (Map.Entry<String, PopulateChunkEvent.Populate.EventType> entry : POPULATES.entrySet()) {
            if (entry.getValue() == type) { return entry.getKey(); }
        }
        return null;
    }

    public static boolean known(String key) { return MAP_GENS.contains(key) || POPULATES.containsKey(key); }

    public static String describe() {
        StringBuilder text = new StringBuilder();
        for (String key : MAP_GENS) { text.append(text.length() == 0 ? "" : ", ").append(key); }
        for (String key : POPULATES.keySet()) { text.append(", ").append(key); }
        return text.toString();
    }

    public static String normalise(String key) { return key.trim().toLowerCase(Locale.ROOT); }

    private static Map<String, String> structureNames() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Village", "villages");
        map.put("Mineshaft", "mineshafts");
        map.put("Stronghold", "strongholds");
        map.put("Temple", "temples");
        map.put("Monument", "monuments");
        map.put("Mansion", "mansions");
        map.put("Fortress", "netherbridges");
        map.put("EndCity", "endcities");
        map.put("reccomplex", "reccomplex");
        return Collections.unmodifiableMap(map);
    }

    private static Set<String> mapGens() {
        Set<String> keys = new LinkedHashSet<>();
        keys.add("caves");
        keys.add("ravines");
        keys.add("mineshafts");
        keys.add("villages");
        keys.add("strongholds");
        keys.add("temples");
        keys.add("monuments");
        keys.add("mansions");
        keys.add("netherbridges");
        keys.add("endcities");
        keys.add("reccomplex");
        return Collections.unmodifiableSet(keys);
    }

    private static Map<String, PopulateChunkEvent.Populate.EventType> populates() {
        Map<String, PopulateChunkEvent.Populate.EventType> map = new LinkedHashMap<>();
        map.put("dungeons", PopulateChunkEvent.Populate.EventType.DUNGEON);
        map.put("waterlakes", PopulateChunkEvent.Populate.EventType.LAKE);
        map.put("lavalakes", PopulateChunkEvent.Populate.EventType.LAVA);
        map.put("netherlava", PopulateChunkEvent.Populate.EventType.NETHER_LAVA);
        map.put("fire", PopulateChunkEvent.Populate.EventType.FIRE);
        map.put("glowstone", PopulateChunkEvent.Populate.EventType.GLOWSTONE);
        map.put("ice", PopulateChunkEvent.Populate.EventType.ICE);
        map.put("animals", PopulateChunkEvent.Populate.EventType.ANIMALS);
        return Collections.unmodifiableMap(map);
    }
}
