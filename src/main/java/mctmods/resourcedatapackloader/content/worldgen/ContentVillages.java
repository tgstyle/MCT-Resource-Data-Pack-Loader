package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentVillages {
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, VillageDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentVillages() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff() || !Config.content.villages()) { return; }
        Json.eachFile(PackManager.VILLAGES, "village plot", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            VillageDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("villages", "Loaded " + DEFS.size() + " village plot definition(s) from packs"); }
    }

    @Nullable public static VillageDef byKey(String named) {
        ResourceLocation key = ResourceLocation.tryParse(named);
        return key == null ? null : DEFS.get(key);
    }

    public static List<VillageDef> allowed() {
        List<String> named = ContentControl.list(ContentControl.STRUCTURES, "villagePieces", Config.worldgen.villagePieces());
        boolean blacklist = ContentControl.flag(ContentControl.STRUCTURES, "villagePiecesAreBlacklist", Config.worldgen.villagePiecesAreBlacklist());
        List<VillageDef> found = new ArrayList<>();
        for (VillageDef def : DEFS.values()) {
            if (!named.isEmpty() && named.contains(def.key().toString()) == !blacklist) { found.add(def); }
            else if (named.isEmpty()) { found.add(def); }
        }
        return found;
    }

    private static int chance(VillageDef def) { return Math.max(1, def.weight()) * Math.max(1, def.mostCount() - def.leastCount() + 1); }

    public static int largestPlot() {
        int largest = 0;
        for (VillageDef def : allowed()) { largest = Math.max(largest, Math.max(def.width(), def.depth())); }
        return largest;
    }

    @Nullable public static VillageDef pick(List<VillageDef> choices, RandomSource random, int widest, int deepest) {
        int total = 0;
        for (VillageDef def : choices) {
            if (def.width() <= widest && def.depth() <= deepest) { total += chance(def); }
        }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (VillageDef def : choices) {
            if (def.width() > widest || def.depth() > deepest) { continue; }
            roll -= chance(def);
            if (roll < 0) { return def; }
        }
        return null;
    }

    @Nullable private static VillageDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Village plot {} is empty, so it is dropped", key);
            return null;
        }
        String type = GsonHelper.getAsString(json, "type", VillageDef.FARM).trim().toLowerCase(Locale.ROOT);
        if (!VillageDef.FARM.equals(type) && !VillageDef.TEMPLATE.equals(type)) {
            ContentLog.LOGGER.error("Village plot {} asks for type '{}', which is not {} or {}, so {} stands in", key, type, VillageDef.FARM, VillageDef.TEMPLATE, VillageDef.FARM);
            type = VillageDef.FARM;
        }
        String structure = GsonHelper.getAsString(json, "structure", "");
        if (VillageDef.TEMPLATE.equals(type) && structure.isEmpty()) {
            ContentLog.LOGGER.error("Village plot {} is a template but names no structure, so it is dropped", key);
            return null;
        }
        return new VillageDef(key, type,
                Math.max(1, GsonHelper.getAsInt(json, "weight", 3)),
                Math.max(0, GsonHelper.getAsInt(json, "leastCount", 1)),
                Math.max(0, GsonHelper.getAsInt(json, "mostCount", 4)),
                Math.max(3, GsonHelper.getAsInt(json, "width", 7)),
                Math.max(1, GsonHelper.getAsInt(json, "height", 4)),
                Math.max(3, GsonHelper.getAsInt(json, "depth", 9)),
                Math.max(0, GsonHelper.getAsInt(json, "apron", 2)),
                Json.strings(json, "crops"),
                GsonHelper.getAsString(json, "edge", "minecraft:oak_log"),
                GsonHelper.getAsString(json, "soil", "minecraft:farmland"),
                GsonHelper.getAsBoolean(json, "water", true),
                Math.max(1, GsonHelper.getAsInt(json, "rowWidth", 2)),
                structure,
                GsonHelper.getAsString(json, "ground", "minecraft:dirt"),
                Mth.clamp(GsonHelper.getAsInt(json, "integrity", 100), 1, 100),
                GsonHelper.getAsString(json, "lootTable", ""),
                Math.max(0, GsonHelper.getAsInt(json, "villagers", 0)),
                GsonHelper.getAsString(json, "villagerEntity", ""),
                GsonHelper.getAsInt(json, "villagerX", 1),
                GsonHelper.getAsInt(json, "villagerY", 1),
                GsonHelper.getAsInt(json, "villagerZ", 1),
                Json.strings(json, "requires"));
    }
}
