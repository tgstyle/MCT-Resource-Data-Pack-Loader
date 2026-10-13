package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ContentOptionFeatures() implements LineCompat.BiomeEdit {
    public static final String ID = "option_features";
    public static final MapCodec<ContentOptionFeatures> CODEC = MapCodec.unit(new ContentOptionFeatures());
    private static volatile Map<String, Holder<PlacedFeature>> copies = Map.of();
    private static volatile Set<String> dropped = Set.of();

    static boolean active() { return !copies.isEmpty() || !dropped.isEmpty(); }

    static void publish(Map<String, Holder<PlacedFeature>> made, Set<String> silenced) {
        copies = Map.copyOf(made);
        dropped = Set.copyOf(silenced);
        if (made.isEmpty()) { return; }
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + ID);
        GeneratedResources.put(PackType.SERVER_DATA, ResourceDataPackLoader.MOD_ID, ContentFormats.BIOME_MODIFIERS + "/" + ID + PackManager.JSON_SUFFIX, modifier.toString());
    }

    @Nullable static JsonObject inline(String path, JsonObject placed) {
        JsonElement feature = placed.get("feature");
        if (feature != null && feature.isJsonObject()) { return feature.getAsJsonObject(); }
        Identifier id = feature != null && feature.isJsonPrimitive() ? Identifier.tryParse(feature.getAsString()) : null;
        Identifier file = id == null ? null : WorldgenFiles.id(LineCompat.FEATURES, id);
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
        if (level.dimension() == Level.OVERWORLD && ContentWorldShape.madeHere(level)) {
            Registry<PlacedFeature> placed = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
            for (String path : copies.keySet()) { placed.getOptional(Identifier.withDefaultNamespace(path)).ifPresent(out::add); }
            for (String path : dropped) { placed.getOptional(Identifier.withDefaultNamespace(path)).ifPresent(out::add); }
            return out;
        }
        for (Holder<PlacedFeature> copy : copies.values()) { out.add(copy.value()); }
        return out;
    }

    @Override public void edit(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        Map<String, Holder<PlacedFeature>> held = copies;
        if (phase != Phase.ADD || held.isEmpty()) { return; }
        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            List<Holder<PlacedFeature>> features = builder.getGenerationSettings().getFeatures(step);
            for (Holder<PlacedFeature> feature : List.copyOf(features)) {
                Identifier id = feature.unwrapKey().map(ResourceKey::identifier).orElse(null);
                Holder<PlacedFeature> copy = id == null || !Identifier.DEFAULT_NAMESPACE.equals(id.getNamespace()) ? null : held.get(id.getPath());
                if (copy != null) { features.add(copy); }
            }
        }
    }

    @Override @Nonnull public MapCodec<? extends BiomeModifier> codec() { return CODEC; }
}
