package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

    @Nullable public static CityMapDef byName(String named) { return named == null || named.isEmpty() ? null : DEFS.get(named.trim()); }

    @Nullable private static CityMapDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) { return null; }
        List<String> rows = rows(json);
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
                palette.put(symbol.charAt(0), cell(mark.getValue()));
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
        return CityMapDef.of(key, cell, palette, rows);
    }

    private static CityMapDef.Cell cell(JsonElement value) {
        List<String> names = new ArrayList<>();
        if (value.isJsonArray()) {
            for (JsonElement choice : value.getAsJsonArray()) { names.add(choice.getAsString().trim()); }
        }
        else { names.add(value.getAsString().trim()); }
        if (names.size() == 1) {
            CityMapDef.Kind kind = kind(names.get(0));
            if (kind != null) { return new CityMapDef.Cell(kind, List.of()); }
        }
        List<PickDef> picks = new ArrayList<>();
        for (String name : names) { picks.add(weighted(name)); }
        return new CityMapDef.Cell(CityMapDef.Kind.PLOT, picks);
    }

    @Nullable private static CityMapDef.Kind kind(String named) {
        return switch (named.toLowerCase(Locale.ROOT)) {
            case "street" -> CityMapDef.Kind.STREET;
            case "plaza" -> CityMapDef.Kind.PLAZA;
            case "alley" -> CityMapDef.Kind.ALLEY;
            case "open" -> CityMapDef.Kind.OPEN;
            case "grow" -> CityMapDef.Kind.GROW;
            default -> null;
        };
    }

    private static PickDef weighted(String entry) {
        int split = entry.lastIndexOf('=');
        if (split <= 0) { return new PickDef(entry, 1); }
        try { return new PickDef(entry.substring(0, split).trim(), Math.max(1, Integer.parseInt(entry.substring(split + 1).trim()))); }
        catch (NumberFormatException ignored) { return new PickDef(entry, 1); }
    }

    private static List<String> rows(JsonObject json) {
        List<String> found = new ArrayList<>();
        if (!json.has("map")) { return found; }
        JsonArray held = GsonHelper.getAsJsonArray(json, "map");
        for (JsonElement row : held) { found.add(row.getAsString()); }
        return found;
    }
}
