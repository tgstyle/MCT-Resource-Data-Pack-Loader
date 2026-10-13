package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public record ContentOptionFeatures() implements BiomeModifier {
    public static final String ID = "option_features";
    public static final Codec<ContentOptionFeatures> CODEC = Codec.unit(new ContentOptionFeatures());
    static final String ANCHOR = "lake_lava_surface";
    private static final ResourceLocation WORLD = Level.OVERWORLD.location();
    private static volatile Map<ResourceLocation, Tuned> tuned = Map.of();

    private record Tuned(Map<String, Holder<PlacedFeature>> copies, Set<String> dropped, List<Holder<PlacedFeature>> added) {
        boolean any() { return !copies.isEmpty() || !dropped.isEmpty() || !added.isEmpty(); }
    }

    static boolean active() { return tuned.containsKey(WORLD); }

    static void publish(ResourceLocation owner, Map<String, Holder<PlacedFeature>> made, Set<String> silenced, Map<String, Holder<PlacedFeature>> extra) {
        Map<ResourceLocation, Tuned> next = WORLD.equals(owner) ? new LinkedHashMap<>() : new LinkedHashMap<>(tuned);
        Tuned set = new Tuned(Map.copyOf(made), Set.copyOf(silenced), List.copyOf(extra.values()));
        if (set.any()) { next.put(owner, set); }
        tuned = Map.copyOf(next);
        if (made.isEmpty() && extra.isEmpty()) { return; }
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + ID);
        GeneratedResources.put(PackType.SERVER_DATA, ResourceDataPackLoader.MOD_ID, ContentFormats.BIOME_MODIFIERS + "/" + ID + PackManager.JSON_SUFFIX, modifier.toString());
    }

    @Nullable static JsonObject inline(String path, JsonObject placed) {
        JsonElement feature = placed.get("feature");
        if (feature != null && feature.isJsonObject()) { return feature.getAsJsonObject(); }
        ResourceLocation id = feature != null && feature.isJsonPrimitive() ? ResourceLocation.tryParse(feature.getAsString()) : null;
        ResourceLocation file = id == null ? null : WorldgenFiles.id(ContentFormats.CONFIGURED_FEATURES, id);
        JsonObject configured = file != null && GameData.has(file) ? GameData.json(file) : null;
        if (configured == null) {
            ContentLog.LOGGER.error("generatorOptions could not read the game's feature {} for {}, so that placement stays as the game has it", feature, path);
            return null;
        }
        placed.add("feature", configured);
        return configured;
    }

    @Nullable static Holder<PlacedFeature> build(String path, JsonObject placed, RegistryOps<JsonElement> ops) {
        if (inline(path, placed) == null) { return null; }
        return PlacedFeature.DIRECT_CODEC.parse(ops, placed)
                .resultOrPartial(error -> ContentLog.LOGGER.error("generatorOptions could not build the placement of {}: {}", path, error))
                .map(Holder::direct)
                .orElse(null);
    }

    static Set<PlacedFeature> refused(ServerLevel level) {
        Set<PlacedFeature> out = Collections.newSetFromMap(new IdentityHashMap<>());
        ResourceLocation here = level.dimension() != Level.OVERWORLD ? level.dimension().location() : ContentWorldShape.madeHere(level) ? WORLD : null;
        Registry<PlacedFeature> placed = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
        for (Map.Entry<ResourceLocation, Tuned> entry : tuned.entrySet()) {
            Tuned set = entry.getValue();
            if (here != null && here.equals(entry.getKey())) {
                for (String path : set.copies().keySet()) { placed.getOptional(ResourceLocation.withDefaultNamespace(path)).ifPresent(out::add); }
                for (String path : set.dropped()) { placed.getOptional(ResourceLocation.withDefaultNamespace(path)).ifPresent(out::add); }
            }
            else {
                for (Holder<PlacedFeature> copy : set.copies().values()) { out.add(copy.value()); }
                for (Holder<PlacedFeature> extra : set.added()) { out.add(extra.value()); }
            }
        }
        return out;
    }

    @Override public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        Map<ResourceLocation, Tuned> held = tuned;
        if (phase != Phase.ADD || held.isEmpty()) { return; }
        boolean desert = biome.is(Biomes.DESERT);
        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            List<Holder<PlacedFeature>> features = builder.getGenerationSettings().getFeatures(step);
            for (Holder<PlacedFeature> feature : List.copyOf(features)) {
                ResourceLocation id = feature.unwrapKey().map(ResourceKey::location).orElse(null);
                if (id == null || !ResourceLocation.DEFAULT_NAMESPACE.equals(id.getNamespace())) { continue; }
                for (Tuned set : held.values()) {
                    Holder<PlacedFeature> copy = set.copies().get(id.getPath());
                    if (copy != null) { features.add(copy); }
                    if (!desert && ANCHOR.equals(id.getPath())) { features.addAll(set.added()); }
                }
            }
        }
    }

    @Override public Codec<? extends BiomeModifier> codec() { return CODEC; }
}
