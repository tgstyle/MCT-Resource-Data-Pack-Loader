package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.level.LevelEvent;
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
    private static volatile Off off = Off.NONE;
    private static volatile Map<ChunkGenerator, Bound> bound = Map.of();

    private ContentPopulateControl() {}

    private record Off(Set<String> features, Set<String> carvers, boolean animals, Set<String> dimensions) {
        static final Off NONE = new Off(Set.of(), Set.of(), false, Set.of());

        boolean any() { return animals || !features.isEmpty() || !carvers.isEmpty(); }
    }

    private record Bound(Set<PlacedFeature> features, Set<ConfiguredWorldCarver<?>> carvers, boolean animals) {}

    public static boolean populates(String key) {
        String wanted = key.trim().toLowerCase(Locale.ROOT);
        return FEATURES.containsKey(wanted) || CARVERS.containsKey(wanted) || ANIMALS.equals(wanted);
    }

    public static void load(@Nullable WorldTemplateDef template, List<String> touched) {
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
        Set<String> dimensions = new LinkedHashSet<>();
        for (String named : template.dimensions()) { dimensions.add(ContentFormats.dimensionId(named)); }
        off = new Off(Set.copyOf(features), Set.copyOf(carvers), animals, Set.copyOf(dimensions));
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        Off held = off;
        if (!held.any() || !(event.getLevel() instanceof ServerLevel level)) { return; }
        Map<ChunkGenerator, Bound> next = level.dimension() == Level.OVERWORLD ? new IdentityHashMap<>() : new IdentityHashMap<>(bound);
        if (held.dimensions().isEmpty() || held.dimensions().contains(level.dimension().location().toString())) { next.put(level.getChunkSource().getGenerator(), resolve(level, held)); }
        bound = next;
    }

    private static Bound resolve(ServerLevel level, Off held) {
        Set<PlacedFeature> features = Collections.newSetFromMap(new IdentityHashMap<>());
        Registry<PlacedFeature> placed = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
        for (String path : held.features()) { placed.getOptional(ResourceLocation.fromNamespaceAndPath("minecraft", path)).ifPresent(features::add); }
        Set<ConfiguredWorldCarver<?>> carvers = Collections.newSetFromMap(new IdentityHashMap<>());
        Registry<ConfiguredWorldCarver<?>> configured = level.registryAccess().registryOrThrow(Registries.CONFIGURED_CARVER);
        for (String path : held.carvers()) { configured.getOptional(ResourceLocation.fromNamespaceAndPath("minecraft", path)).ifPresent(carvers::add); }
        return new Bound(features, carvers, held.animals());
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
