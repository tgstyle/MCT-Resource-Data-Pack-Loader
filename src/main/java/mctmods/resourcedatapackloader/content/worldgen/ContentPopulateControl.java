package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentPopulateControl {
    private static final String ANIMALS = "animals";
    private static final Map<String, List<String>> FEATURES = Map.of(
            "dungeons", List.of("monster_room", "monster_room_deep"), "waterlakes", List.of(), "lavalakes", List.of("lake_lava_underground", "lake_lava_surface"),
            "netherlava", List.of("spring_open", "spring_delta"), "fire", List.of("patch_fire", "patch_soul_fire"), "glowstone", List.of("glowstone", "glowstone_extra"),
            "ice", List.of("freeze_top_layer"));
    private static final Map<String, List<String>> CARVERS = Map.of("caves", List.of("cave", "cave_extra_underground", "nether_cave"), "ravines", List.of("canyon"));
    private static final Map<String, String> OPTIONS = Map.ofEntries(Map.entry("useCaves", "caves"), Map.entry("useRavines", "ravines"), Map.entry("useDungeons", "dungeons"), Map.entry("useWaterLakes", "waterlakes"),
            Map.entry("useLavaLakes", "lavalakes"), Map.entry("useStrongholds", "strongholds"), Map.entry("useVillages", "villages"), Map.entry("useMineShafts", "mineshafts"), Map.entry("useTemples", "temples"),
            Map.entry("useMonuments", "monuments"), Map.entry("useMansions", "mansions"));
    private static final Set<String> STRUCTURE_SWITCHES = Set.of("strongholds", "villages", "mineshafts", "temples", "monuments", "mansions");
    private static volatile Off off = Off.NONE;
    private static volatile Map<ChunkGenerator, Bound> bound = Map.of();

    private ContentPopulateControl() {}

    private record Off(Set<String> features, Set<String> carvers, boolean animals, Set<String> structures, Set<String> dimensions) {
        static final Off NONE = new Off(Set.of(), Set.of(), false, Set.of(), Set.of());

        boolean any() { return animals || !features.isEmpty() || !carvers.isEmpty() || !structures.isEmpty(); }
    }

    private record Bound(Set<PlacedFeature> features, Set<ConfiguredWorldCarver<?>> carvers, boolean animals, Set<Structure> structures) {}

    public static boolean readsOption(String key) { return OPTIONS.containsKey(key); }

    static List<String> names() {
        List<String> out = new ArrayList<>(FEATURES.keySet());
        out.addAll(CARVERS.keySet());
        out.add(ANIMALS);
        Collections.sort(out);
        return out;
    }

    public static boolean populates(String key) {
        String wanted = key.trim().toLowerCase(Locale.ROOT);
        return FEATURES.containsKey(wanted) || CARVERS.containsKey(wanted) || ANIMALS.equals(wanted);
    }

    public static void load(@Nullable WorldTemplateDef template, List<String> structuresOff, List<String> touched) {
        off = Off.NONE;
        bound = Map.of();
        if (template == null) { return; }
        Set<String> features = new LinkedHashSet<>();
        Set<String> carvers = new LinkedHashSet<>();
        boolean animals = false;
        for (Map.Entry<String, Boolean> entry : template.structures().entrySet()) {
            String key = entry.getKey().trim().toLowerCase(Locale.ROOT);
            if (entry.getValue() || !populates(key)) { continue; }
            features.addAll(FEATURES.getOrDefault(key, List.of()));
            carvers.addAll(CARVERS.getOrDefault(key, List.of()));
            animals |= ANIMALS.equals(key);
            touched.add(key + " off");
        }
        for (String key : structuresOff) { touched.add(key + " off"); }
        Set<String> dimensions = new LinkedHashSet<>();
        for (String named : template.dimensions()) { dimensions.add(ContentFormats.dimensionId(named)); }
        off = new Off(Set.copyOf(features), Set.copyOf(carvers), animals, Set.copyOf(structuresOff), Set.copyOf(dimensions));
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        if (level.dimension() == Level.OVERWORLD) { ContentStructureControl.checkBiomes(level); }
        Off held = off;
        Map<ChunkGenerator, Bound> next = level.dimension() == Level.OVERWORLD ? new IdentityHashMap<>() : new IdentityHashMap<>(bound);
        boolean templated = held.any() && (held.dimensions().isEmpty() || held.dimensions().contains(level.dimension().location().toString()));
        Set<String> options = optionsOff(level);
        if (templated || !options.isEmpty()) { next.put(level.getChunkSource().getGenerator(), resolve(level, templated ? held : Off.NONE, options)); }
        bound = next;
    }

    private static Set<String> optionsOff(ServerLevel level) {
        DimensionDef def = ContentDimensions.def(level);
        JsonObject options = level.dimension() == Level.OVERWORLD ? ContentTerrain.customizedOptions() : def == null ? null : def.options();
        Set<String> out = new LinkedHashSet<>();
        if (options == null) { return out; }
        for (Map.Entry<String, String> option : OPTIONS.entrySet()) {
            JsonElement value = options.get(option.getKey());
            if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean() && !value.getAsBoolean()) { out.add(option.getValue()); }
        }
        if (!out.isEmpty()) { ContentLog.LOGGER.debug("The generatorOptions of {} turn off {}", level.dimension().location(), out); }
        return out;
    }

    private static Bound resolve(ServerLevel level, Off held, Set<String> options) {
        Set<String> featureNames = new LinkedHashSet<>(held.features());
        Set<String> carverNames = new LinkedHashSet<>(held.carvers());
        Set<String> structureNames = new LinkedHashSet<>(held.structures());
        for (String key : options) {
            featureNames.addAll(FEATURES.getOrDefault(key, List.of()));
            carverNames.addAll(CARVERS.getOrDefault(key, List.of()));
            if (STRUCTURE_SWITCHES.contains(key)) { structureNames.add(key); }
        }
        Set<PlacedFeature> features = Collections.newSetFromMap(new IdentityHashMap<>());
        Registry<PlacedFeature> placed = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
        for (String path : featureNames) { placed.getOptional(ResourceLocation.fromNamespaceAndPath("minecraft", path)).ifPresent(features::add); }
        Set<ConfiguredWorldCarver<?>> carvers = Collections.newSetFromMap(new IdentityHashMap<>());
        Registry<ConfiguredWorldCarver<?>> configured = level.registryAccess().registryOrThrow(Registries.CONFIGURED_CARVER);
        for (String path : carverNames) { configured.getOptional(ResourceLocation.fromNamespaceAndPath("minecraft", path)).ifPresent(carvers::add); }
        Set<Structure> structures = Collections.newSetFromMap(new IdentityHashMap<>());
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (String key : structureNames) {
            for (ResourceLocation id : ContentStructureControl.structures(key)) { registry.getOptional(id).ifPresent(structures::add); }
        }
        return new Bound(features, carvers, held.animals(), structures);
    }

    public static boolean refusesStructure(ChunkGenerator generator, Structure structure) {
        Bound held = bound.get(generator);
        return held != null && held.structures().contains(structure);
    }

    public static boolean refuses(ChunkGenerator generator, PlacedFeature feature) {
        Bound held = bound.get(generator);
        return held != null && held.features().contains(feature);
    }

    public static boolean carves(ChunkGenerator generator, ConfiguredWorldCarver<?> carver) {
        Bound held = bound.get(generator);
        return held == null || !held.carvers().contains(carver);
    }

    public static boolean refusesAnimals(ChunkGenerator generator) {
        Bound held = bound.get(generator);
        return held != null && held.animals();
    }
}
