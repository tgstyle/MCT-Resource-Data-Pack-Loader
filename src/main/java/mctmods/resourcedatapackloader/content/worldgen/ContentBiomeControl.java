package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentBiomeControl {
    public static final String VOID = "minecraft:the_void";
    private static final String END = "minecraft:the_end";
    private static final Map<Holder<Biome>, Holder<Biome>> END_BIOMES = new ConcurrentHashMap<>();
    private static final ResourceLocation VOID_ID = ResourceLocation.parse(VOID);
    private static final Map<ResourceLocation, Set<String>> TAG_MEMBERS = new HashMap<>();
    private static final Map<String, Integer> BLOCKED = new LinkedHashMap<>();
    private static final Set<String> WARNED = new LinkedHashSet<>();

    private ContentBiomeControl() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.BIOMES)) { return false; }
        return ContentControl.flag(ContentControl.BIOMES, "blockBiomes", Config.worldgen.blockBiomes()) || !ContentControl.list(ContentControl.BIOMES, "biomeNames", Config.worldgen.biomeNames()).isEmpty();
    }

    public static boolean appliesTo(String dimension) {
        List<String> listed = ContentControl.list(ContentControl.BIOMES, "blockBiomeDimensions", Config.worldgen.blockBiomeDimensions());
        if (listed.isEmpty()) { return true; }
        boolean found = false;
        for (String named : listed) {
            if (ContentFormats.dimensionId(named).equals(dimension)) {
                found = true;
                break;
            }
        }
        return found != ContentControl.flag(ContentControl.BIOMES, "blockBiomeDimensionsAreBlacklist", Config.worldgen.blockBiomeDimensionsAreBlacklist());
    }

    public static boolean everythingBlocked() {
        if (!enabled()) { return false; }
        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template == null || !template.voidOnly()) { return false; }
        for (ResourceLocation biome : ContentBiomes.known()) {
            if (!biome.equals(VOID_ID) && allowed(biome)) { return false; }
        }
        return true;
    }

    public static void reset() {
        BLOCKED.clear();
        TAG_MEMBERS.clear();
        END_BIOMES.clear();
    }

    public static Holder<Biome> inEnd(Holder<Biome> biome) {
        if (!enabled() || !appliesTo(END)) { return biome; }
        return END_BIOMES.computeIfAbsent(biome, ContentBiomeControl::endReplacement);
    }

    private static Holder<Biome> endReplacement(Holder<Biome> biome) {
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (id == null || server == null || allowed(id)) { return biome; }
        ResourceLocation replaced = ResourceLocation.tryParse(replacement(id, END));
        Holder<Biome> found = replaced == null ? null : server.registryAccess().registryOrThrow(Registries.BIOME).getHolder(ResourceKey.create(Registries.BIOME, replaced)).orElse(null);
        if (found == null) { return biome; }
        ContentLog.LOGGER.debug("Biome {} is blocked out of the end, which takes {} in its place", id, replaced);
        return found;
    }

    public static String place(ResourceLocation biome, String dimension) {
        if (allowed(biome)) { return biome.toString(); }
        String replacement = replacement(biome, dimension);
        BLOCKED.merge(biome.toString(), 1, Integer::sum);
        return replacement;
    }

    public static void report(String dimension) {
        if (BLOCKED.isEmpty()) { return; }
        int points = 0;
        for (int count : BLOCKED.values()) { points += count; }
        WorldTemplateDef template = ContentWorldTemplates.active();
        Summary.info("biomes.blocked." + dimension, "Blocked " + BLOCKED.size() + " biome(s) out of " + dimension + ", " + points + " climate point(s), replaced " + (template == null || template.voidOnly() ? "with " + VOID : "by world template " + template.key() + "'s roles and fallback"));
        if (ContentControl.flag(ContentControl.BIOMES, "logBlockedBiomes", Config.worldgen.logBlockedBiomes())) {
            Map<String, Integer> byMod = new LinkedHashMap<>();
            for (String biome : BLOCKED.keySet()) { byMod.merge(biome.substring(0, Math.max(0, biome.indexOf(':'))), 1, Integer::sum); }
            for (Map.Entry<String, Integer> mod : byMod.entrySet()) { ContentLog.LOGGER.debug("  blocked {} biome(s) from {}", mod.getValue(), mod.getKey()); }
        }
        BLOCKED.clear();
    }

    private static boolean allowed(ResourceLocation biome) {
        Set<String> names = Settings.lower(ContentControl.list(ContentControl.BIOMES, "biomeNames", Config.worldgen.biomeNames()));
        boolean blacklist = ContentControl.flag(ContentControl.BIOMES, "biomeNamesAreBlacklist", Config.worldgen.biomeNamesAreBlacklist());
        if (!names.isEmpty() && BiomeNames.named(biome, names) == blacklist) { return false; }
        if (!ContentControl.flag(ContentControl.BIOMES, "blockBiomes", Config.worldgen.blockBiomes())) { return true; }
        return Settings.lower(ContentControl.list(ContentControl.BIOMES, "biomeWhitelist", Config.worldgen.biomeWhitelist())).contains(biome.getNamespace());
    }

    private static String replacement(ResourceLocation blocked, String dimension) {
        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template == null || !templated(template, dimension)) { return VOID; }
        for (String role : ContentWorldTemplates.ROLE_ORDER) {
            String named = template.roles().get(role);
            if (named == null || named.isEmpty()) { continue; }
            String tag = ContentFormats.biomeTag(role);
            ResourceLocation members = tag == null ? null : ResourceLocation.tryParse(tag);
            if (members == null || !members(members).contains(blocked.toString())) { continue; }
            String found = biome(template, named);
            if (found != null) { return found; }
        }
        String fallback = biome(template, template.fallback());
        return fallback == null ? VOID : fallback;
    }

    private static boolean templated(WorldTemplateDef template, String dimension) {
        if (template.dimensions().isEmpty()) { return true; }
        for (String named : template.dimensions()) {
            if (ContentFormats.dimensionId(named).equals(dimension)) { return true; }
        }
        return false;
    }

    @Nullable private static String biome(WorldTemplateDef template, String name) {
        if (name.isEmpty() || WorldTemplateDef.VOID.equalsIgnoreCase(name)) { return null; }
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id != null && ContentBiomes.shipped(id)) { return id.toString(); }
        if (WARNED.add(template.key() + " " + name)) { ContentLog.LOGGER.error("World template {} names biome {}, which is not registered, so that role falls through", template.key(), name); }
        return null;
    }

    private static Set<String> members(ResourceLocation tag) {
        Set<String> known = TAG_MEMBERS.get(tag);
        if (known != null) { return known; }
        Set<String> found = new LinkedHashSet<>();
        TAG_MEMBERS.put(tag, found);
        JsonObject json = GameData.json(ResourceLocation.fromNamespaceAndPath(tag.getNamespace(), ContentFormats.BIOME_TAGS + "/" + tag.getPath() + ".json"));
        if (json == null) { return found; }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "values", new com.google.gson.JsonArray())) {
            String value = element.isJsonObject() ? GsonHelper.getAsString(element.getAsJsonObject(), "id", "") : element.getAsString();
            if (value.startsWith("#")) {
                ResourceLocation nested = ResourceLocation.tryParse(value.substring(1));
                if (nested != null) { found.addAll(members(nested)); }
            }
            else if (!value.isEmpty()) { found.add(value); }
        }
        return found;
    }
}
