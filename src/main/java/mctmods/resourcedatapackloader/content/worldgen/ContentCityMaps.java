package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentCityMaps {
    private static final Gson GSON = new Gson();
    private static final Map<String, CityMapDef> DEFS = new LinkedHashMap<>();
    private static final int CELL_LEAST = 8;
    private static final int CELL_MOST = 128;
    private static final int CELL_USUAL = 48;
    private static final int LIFT_LEAST = 2;
    private static final int LIFT_MOST = 64;
    private static boolean loaded;

    private ContentCityMaps() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff()) { return; }
        Json.eachFile(PackManager.CITYMAPS, "city map", (key, contents) -> {
            CityMapDef def = parse(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { Summary.info("citymaps", "Loaded " + DEFS.size() + " city map(s) from packs"); }
    }

    @Nullable public static JsonElement setting(String key) {
        if (DEFS.isEmpty()) { return null; }
        CityMapDef def = ContentCity.layout();
        if (def == null || def.settings() == null || !def.settings().has(key)) { return null; }
        return def.settings().get(key);
    }

    @Nullable public static CityMapDef byName(String named) { return named == null || named.isEmpty() ? null : DEFS.get(named.trim().toLowerCase(Locale.ROOT)); }

    @Nullable private static CityMapDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) { return null; }
        List<String> rows = Json.strings(json, "map");
        if (rows.isEmpty() || rows.size() > CityMapDef.LIMIT) {
            ContentLog.LOGGER.error("City map {} holds {} map row(s), the most being {}, so it is dropped", key, rows.size(), CityMapDef.LIMIT);
            return null;
        }
        Map<Character, CityMapDef.Cell> palette = new LinkedHashMap<>();
        if (json.has("palette")) {
            for (Map.Entry<String, JsonElement> mark : GsonHelper.getAsJsonObject(json, "palette").entrySet()) {
                String symbol = mark.getKey().trim();
                if (symbol.length() != 1 || symbol.charAt(0) == CityMapDef.OPEN_MARK) {
                    ContentLog.LOGGER.error("City map {} palette symbol '{}' must be a single character other than '{}', so the map is dropped", key, mark.getKey(), CityMapDef.OPEN_MARK);
                    return null;
                }
                palette.put(symbol.charAt(0), cell(key, symbol, mark.getValue()));
            }
        }
        for (String row : rows) {
            if (row.length() > CityMapDef.LIMIT) {
                ContentLog.LOGGER.error("City map {} row '{}' is {} cell(s) long, the most being {}, so the map is dropped", key, row, row.length(), CityMapDef.LIMIT);
                return null;
            }
            for (char mark : row.toCharArray()) {
                if (mark != CityMapDef.OPEN_MARK && !palette.containsKey(mark)) {
                    ContentLog.LOGGER.error("City map {} uses '{}', which is not in its palette, so the map is dropped", key, mark);
                    return null;
                }
            }
        }
        int cell = Mth.clamp(GsonHelper.getAsInt(json, "cell", CELL_USUAL), CELL_LEAST, CELL_MOST);
        JsonObject settings = null;
        if (json.has("settings")) {
            if (json.get("settings").isJsonObject()) {
                settings = json.getAsJsonObject("settings");
                for (String named : settings.keySet()) {
                    if (ContentControl.ignores(named)) { ContentLog.LOGGER.error("City map {} sets '{}', which is not a setting anything reads, so it does nothing", key, named); }
                }
            }
            else { ContentLog.LOGGER.error("City map {} has settings that are not an object of setting names, so it keeps the world template's", key); }
        }
        return CityMapDef.of(key, cell, palette, rows, settings);
    }

    private static CityMapDef.Cell cell(ResourceLocation key, String symbol, JsonElement value) {
        List<String> names = new ArrayList<>();
        int height = CityMapDef.LIFT;
        JsonObject roadKeys = null;
        if (value.isJsonArray()) {
            for (JsonElement choice : value.getAsJsonArray()) { names.add(choice.getAsString().trim()); }
        }
        else if (value.isJsonObject()) {
            JsonObject described = value.getAsJsonObject();
            names.add(GsonHelper.getAsString(described, "kind", "").trim());
            height = Mth.clamp(GsonHelper.getAsInt(described, "height", CityMapDef.LIFT), LIFT_LEAST, LIFT_MOST);
            if (described.has("settings") && described.get("settings").isJsonObject()) {
                roadKeys = described.getAsJsonObject("settings");
                for (String named : roadKeys.keySet()) {
                    if (ContentControl.ignores(named)) { ContentLog.LOGGER.error("City map {} mark '{}' sets '{}', which is not a setting anything reads, so it does nothing", key, symbol, named); }
                }
            }
        }
        else { names.add(value.getAsString().trim()); }
        if (names.size() == 1) {
            CityMapDef.Kind kind = kind(names.get(0));
            if (kind != null) { return new CityMapDef.Cell(kind, List.of(), height, roadKeys); }
        }
        List<PickDef> picks = new ArrayList<>();
        for (String name : names) { picks.add(ContentStructureMaps.weighted(name)); }
        return new CityMapDef.Cell(CityMapDef.Kind.PLOT, picks, height, roadKeys);
    }

    @Nullable private static CityMapDef.Kind kind(String named) {
        return switch (named.toLowerCase(Locale.ROOT)) {
            case "street" -> CityMapDef.Kind.STREET;
            case "plaza" -> CityMapDef.Kind.PLAZA;
            case "alley" -> CityMapDef.Kind.ALLEY;
            case "open" -> CityMapDef.Kind.OPEN;
            case "grow" -> CityMapDef.Kind.GROW;
            case "junction" -> CityMapDef.Kind.JUNCTION;
            case "bulb" -> CityMapDef.Kind.BULB;
            case "elevated" -> CityMapDef.Kind.ELEVATED;
            default -> null;
        };
    }
}
