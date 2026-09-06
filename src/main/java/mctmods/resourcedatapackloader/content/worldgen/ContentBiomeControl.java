package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentBiomeControl {
    public static final String VOID = "minecraft:the_void";
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

    public static void reset() {
        BLOCKED.clear();
        TAG_MEMBERS.clear();
    }

    public static String place(ResourceLocation biome) {
        if (!blocked(biome)) { return biome.toString(); }
        String replacement = replacement(biome);
        BLOCKED.merge(biome.toString(), 1, Integer::sum);
        return replacement;
    }

    public static void report(String dimension) {
        if (BLOCKED.isEmpty()) { return; }
        int points = 0;
        for (int count : BLOCKED.values()) { points += count; }
        WorldTemplateDef template = ContentWorldTemplates.active();
        Summary.info("biomes.blocked." + dimension, "Blocked " + BLOCKED.size() + " biome(s) out of " + dimension + ", " + points + " climate point(s), replaced " + (template == null || (template.fallback().isEmpty() && template.roles().isEmpty()) ? "with " + VOID : "by world template " + template.key() + "'s roles and fallback") + ": " + BLOCKED.keySet());
        BLOCKED.clear();
    }

    private static boolean blocked(ResourceLocation biome) {
        Set<String> names = lower(ContentControl.list(ContentControl.BIOMES, "biomeNames", Config.worldgen.biomeNames()));
        boolean blacklist = ContentControl.flag(ContentControl.BIOMES, "biomeNamesAreBlacklist", Config.worldgen.biomeNamesAreBlacklist());
        if (!names.isEmpty()) {
            if (names.contains(biome.toString())) { return blacklist; }
            if (!blacklist) { return true; }
        }
        if (!ContentControl.flag(ContentControl.BIOMES, "blockBiomes", Config.worldgen.blockBiomes())) { return false; }
        return !lower(ContentControl.list(ContentControl.BIOMES, "biomeWhitelist", Config.worldgen.biomeWhitelist())).contains(biome.getNamespace());
    }

    private static String replacement(ResourceLocation blocked) {
        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template == null) { return VOID; }
        for (Map.Entry<String, String> role : template.roles().entrySet()) {
            String tag = ContentFormats.biomeTag(role.getKey());
            if (tag == null) {
                if (WARNED.add(role.getKey())) { ContentLog.LOGGER.error("World template {} names the role '{}', which no biome tag on this line answers to", template.key(), role.getKey()); }
                continue;
            }
            ResourceLocation named = ResourceLocation.tryParse(tag);
            if (named != null && members(named).contains(blocked.toString())) { return role.getValue(); }
        }
        return template.fallback().isEmpty() ? VOID : template.fallback();
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

    private static Set<String> lower(List<String> values) {
        Set<String> out = new LinkedHashSet<>();
        for (String value : values) { out.add(value.trim().toLowerCase(Locale.ROOT)); }
        return out;
    }
}
