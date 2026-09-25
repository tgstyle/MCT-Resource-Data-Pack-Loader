package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public record ContentOreControl() implements BiomeModifier {
    public static final String ID = "ore_control";
    public static final Codec<ContentOreControl> CODEC = Codec.unit(new ContentOreControl());
    private static final String CUSTOM = "CUSTOM";
    private static final List<Map.Entry<String, String>> TYPES = List.of(
            Map.entry("COAL", "coal"), Map.entry("IRON", "iron"), Map.entry("COPPER", "copper"), Map.entry("GOLD", "gold"), Map.entry("REDSTONE", "redstone"),
            Map.entry("DIAMOND", "diamond"), Map.entry("LAPIS", "lapis"), Map.entry("EMERALD", "emerald"), Map.entry("QUARTZ", "quartz"), Map.entry("DIRT", "dirt"),
            Map.entry("GRAVEL", "gravel"), Map.entry("DIORITE", "diorite"), Map.entry("GRANITE", "granite"), Map.entry("ANDESITE", "andesite"), Map.entry("TUFF", "tuff"),
            Map.entry("CLAY", "clay"), Map.entry("SILVERFISH", "infested"));
    private static final Map<String, Integer> BLOCKED = new LinkedHashMap<>();
    private static volatile Map<ChunkGenerator, Set<PlacedFeature>> scoped = Map.of();
    private static boolean reported;

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.ORES)) { return false; }
        return ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres()) || !ContentControl.list(ContentControl.ORES, "oreTypes", Config.worldgen.oreTypes()).isEmpty();
    }

    public static void generate() {
        BLOCKED.clear();
        reported = false;
        if (!enabled()) { return; }
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + ID);
        GeneratedResources.put(PackType.SERVER_DATA, ResourceDataPackLoader.MOD_ID, ContentFormats.BIOME_MODIFIERS + "/" + ID + ".json", modifier.toString());
        Set<String> whitelist = whitelist();
        List<String> types = types();
        if (ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres())) { Summary.info("oregen", "Blocking ore generation except from " + (whitelist.isEmpty() ? "nothing" : whitelist)); }
        if (!types.isEmpty()) { Summary.info("oregen.types", (blacklist() ? "Blocking these ore types outright: " : "Allowing only these ore types to generate: ") + types); }
        if (!ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres()) && !whitelist.contains("minecraft")) { ContentLog.LOGGER.warn("oreWhitelist leaves out minecraft, but blockOres is off, so nothing is being blocked by mod. Turn blockOres on for the whitelist to mean anything"); }
    }

    @Override public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.REMOVE || !enabled() || !dimensions().isEmpty()) { return; }
        Set<String> whitelist = whitelist();
        List<String> types = types();
        boolean byMod = ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres());
        boolean blacklist = blacklist();
        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            builder.getGenerationSettings().getFeatures(step).removeIf(feature -> blocked(feature.unwrapKey().map(ResourceKey::location).orElse(null), feature.value(), whitelist, types, byMod, blacklist));
        }
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        Map<ChunkGenerator, Set<PlacedFeature>> next = level.dimension() == Level.OVERWORLD ? new IdentityHashMap<>() : new IdentityHashMap<>(scoped);
        List<String> dimensions = dimensions();
        if (enabled() && !dimensions.isEmpty() && inScope(level.dimension().location().toString(), dimensions)) {
            Set<String> whitelist = whitelist();
            List<String> types = types();
            boolean byMod = ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres());
            boolean blacklist = blacklist();
            Set<PlacedFeature> features = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Map.Entry<ResourceKey<PlacedFeature>, PlacedFeature> entry : level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).entrySet()) {
                if (blocked(entry.getKey().location(), entry.getValue(), whitelist, types, byMod, blacklist)) { features.add(entry.getValue()); }
            }
            next.put(level.getChunkSource().getGenerator(), features);
            ContentLog.LOGGER.debug("Ore blocking refuses {} ore feature(s) in {}", features.size(), level.dimension().location());
        }
        scoped = next;
    }

    private static boolean ore(PlacedFeature feature) {
        Feature<?> kind = feature.feature().value().feature();
        return kind instanceof OreFeature || kind instanceof ScatteredOreFeature;
    }

    public static boolean refuses(ChunkGenerator generator, PlacedFeature feature) {
        Set<PlacedFeature> held = scoped.get(generator);
        return held != null && held.contains(feature);
    }

    @Override public Codec<? extends BiomeModifier> codec() { return CODEC; }

    private static boolean blocked(@Nullable ResourceLocation id, PlacedFeature feature, Set<String> whitelist, List<String> types, boolean byMod, boolean blacklist) {
        if (id == null || !ore(feature) || ContentWorldgen.entry(id) != null) { return false; }
        String type = typeOf(id.getPath());
        boolean denied = !types.isEmpty() && types.contains(type) == blacklist;
        if (!denied && byMod && !whitelist.contains(id.getNamespace())) { denied = true; }
        if (!denied) { return false; }
        synchronized (BLOCKED) { BLOCKED.merge(id.getNamespace() + " " + type, 1, Integer::sum); }
        ContentLog.LOGGER.debug("Blocking ore feature {} ({})", id, type);
        return true;
    }

    private static String typeOf(String path) {
        for (Map.Entry<String, String> type : TYPES) {
            if (path.contains(type.getValue())) { return type.getKey(); }
        }
        return CUSTOM;
    }

    private static List<String> dimensions() { return ContentControl.list(ContentControl.ORES, "blockOreDimensions", Config.worldgen.blockOreDimensions()); }

    private static boolean inScope(String dimension, List<String> dimensions) {
        boolean listed = false;
        for (String named : dimensions) {
            if (ContentFormats.dimensionId(named).equals(dimension)) {
                listed = true;
                break;
            }
        }
        return listed != ContentControl.flag(ContentControl.ORES, "blockOreDimensionsAreBlacklist", Config.worldgen.blockOreDimensionsAreBlacklist());
    }

    private static Set<String> whitelist() { return Settings.lower(ContentControl.list(ContentControl.ORES, "oreWhitelist", Config.worldgen.oreWhitelist())); }

    private static List<String> types() {
        List<String> out = new ArrayList<>();
        for (String type : ContentControl.list(ContentControl.ORES, "oreTypes", Config.worldgen.oreTypes())) { out.add(type.trim().toUpperCase(Locale.ROOT)); }
        return out;
    }

    private static boolean blacklist() { return ContentControl.flag(ContentControl.ORES, "oreTypesAreBlacklist", Config.worldgen.oreTypesAreBlacklist()); }

    public static Map<String, Integer> blocked() {
        synchronized (BLOCKED) { return Map.copyOf(BLOCKED); }
    }

    public static boolean veinsBlocked(String dimension) {
        if (!enabled()) { return false; }
        List<String> dimensions = dimensions();
        if (!dimensions.isEmpty() && !inScope(dimension, dimensions)) { return false; }
        List<String> types = types();
        boolean blacklist = blacklist();
        boolean byType = !types.isEmpty() && (types.contains("IRON") == blacklist || types.contains("COPPER") == blacklist);
        boolean byMod = ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres()) && !whitelist().contains("minecraft");
        return byType || byMod;
    }

    public static void onServerStarted(ServerStartedEvent ignored) {
        if (reported || BLOCKED.isEmpty()) { return; }
        reported = true;
        int total = 0;
        for (int count : BLOCKED.values()) { total += count; }
        if (!ContentControl.flag(ContentControl.ORES, "logBlockedOres", Config.worldgen.logBlockedOres())) { return; }
        Summary.info("oregen.blocked", "Blocked " + total + " ore feature placement(s) across the biomes: " + BLOCKED);
    }
}
