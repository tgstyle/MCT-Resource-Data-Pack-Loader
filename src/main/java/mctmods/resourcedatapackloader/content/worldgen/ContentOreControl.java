package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public record ContentOreControl() implements BiomeModifier {
    public static final String ID = "ore_control";
    public static final MapCodec<ContentOreControl> CODEC = MapCodec.unit(new ContentOreControl());
    private static final String CUSTOM = "CUSTOM";
    private static final List<Map.Entry<String, String>> TYPES = List.of(
            Map.entry("COAL", "coal"), Map.entry("IRON", "iron"), Map.entry("COPPER", "copper"), Map.entry("GOLD", "gold"), Map.entry("REDSTONE", "redstone"),
            Map.entry("DIAMOND", "diamond"), Map.entry("LAPIS", "lapis"), Map.entry("EMERALD", "emerald"), Map.entry("QUARTZ", "quartz"), Map.entry("DIRT", "dirt"),
            Map.entry("GRAVEL", "gravel"), Map.entry("DIORITE", "diorite"), Map.entry("GRANITE", "granite"), Map.entry("ANDESITE", "andesite"), Map.entry("TUFF", "tuff"),
            Map.entry("CLAY", "clay"), Map.entry("SILVERFISH", "infested"));
    private static final Map<String, String> DIMENSION_TAGS = Map.of("minecraft:overworld", "minecraft:is_overworld", "minecraft:the_nether", "minecraft:is_nether", "minecraft:the_end", "minecraft:is_end");
    private static final Map<String, Integer> BLOCKED = new LinkedHashMap<>();
    private static final Set<String> WARNED = new LinkedHashSet<>();
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
    }

    @Override public void modify(@Nonnull Holder<Biome> biome, @Nonnull Phase phase, @Nonnull ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.REMOVE || !enabled() || !inScope(biome)) { return; }
        Set<String> whitelist = whitelist();
        List<String> types = types();
        boolean byMod = ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres());
        boolean blacklist = blacklist();
        for (GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
            builder.getGenerationSettings().getFeatures(step).removeIf(feature -> blocked(feature, whitelist, types, byMod, blacklist));
        }
    }

    @Override @Nonnull public MapCodec<? extends BiomeModifier> codec() { return CODEC; }

    private static boolean blocked(Holder<PlacedFeature> feature, Set<String> whitelist, List<String> types, boolean byMod, boolean blacklist) {
        ResourceLocation id = feature.unwrapKey().map(ResourceKey::location).orElse(null);
        if (id == null || !id.getPath().contains("ore")) { return false; }
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

    private static boolean inScope(Holder<Biome> biome) {
        List<String> dimensions = ContentControl.list(ContentControl.ORES, "blockOreDimensions", Config.worldgen.blockOreDimensions());
        if (dimensions.isEmpty()) { return true; }
        boolean listed = false;
        for (String named : dimensions) {
            String tag = DIMENSION_TAGS.get(ContentFormats.dimensionId(named));
            if (tag == null) {
                if (WARNED.add(named)) { ContentLog.LOGGER.error("blockOreDimensions names {}, which is not one of the three dimensions with a biome tag, so it cannot scope ore blocking", named); }
                continue;
            }
            if (biome.is(TagKey.create(Registries.BIOME, ResourceLocation.parse(tag)))) {
                listed = true;
                break;
            }
        }
        return listed != ContentControl.flag(ContentControl.ORES, "blockOreDimensionsAreBlacklist", Config.worldgen.blockOreDimensionsAreBlacklist());
    }

    private static Set<String> whitelist() {
        Set<String> out = new LinkedHashSet<>();
        for (String mod : ContentControl.list(ContentControl.ORES, "oreWhitelist", Config.worldgen.oreWhitelist())) { out.add(mod.trim().toLowerCase(Locale.ROOT)); }
        return out;
    }

    private static List<String> types() {
        List<String> out = new ArrayList<>();
        for (String type : ContentControl.list(ContentControl.ORES, "oreTypes", Config.worldgen.oreTypes())) { out.add(type.trim().toUpperCase(Locale.ROOT)); }
        return out;
    }

    private static boolean blacklist() { return ContentControl.flag(ContentControl.ORES, "oreTypesAreBlacklist", Config.worldgen.oreTypesAreBlacklist()); }

    public static boolean veinsBlocked() {
        if (!enabled()) { return false; }
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
