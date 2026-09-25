package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.pack.RDPLPack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class Port {
    public static final String DATA = "data";
    public static final int LEGACY_FORMAT = 3;
    private static final Set<String> VANILLA_WORLDGEN = new HashSet<>(Arrays.asList("biome", "configured_carver", "configured_feature", "placed_feature", "noise", "noise_settings", "density_function", "structure", "structure_set", "template_pool", "processor_list", "world_preset", "flat_level_generator_preset", "multi_noise_biome_source_parameter_list"));
    private static final Set<String> NO_TWIN_DATA = new HashSet<>(Arrays.asList("dimension", "dimension_type", "damage_type", "chat_type", "trim_material", "trim_pattern", "banner_pattern", "painting_variant", "jukebox_song", "enchantment", "enchantment_provider", "wolf_variant", "predicates", "predicate", "item_modifiers", "item_modifier", "forge", "neoforge", "data_maps", "instrument", "trial_spawner"));
    private static final Set<String> NO_TWIN_ASSETS = new HashSet<>(Arrays.asList("atlases", "particles", "equipment", "items", "post_effect", "waypoint_style"));
    private static final Map<String, String> SINGULAR = new HashMap<>();

    static {
        SINGULAR.put("recipe", "recipes");
        SINGULAR.put("loot_table", "loot_tables");
        SINGULAR.put("advancement", "advancements");
        SINGULAR.put("function", "functions");
        SINGULAR.put("structure", "structures");
    }

    private Port() {}

    public enum Kind { RAW, DEFINITION, LANG, MODEL, BLOCKSTATE, PIXELMAP, RECIPE, LOOT, ADVANCEMENT, FUNCTION, STRUCTURE, ITEM_TAG, FUNCTION_TAG, DROPPED }

    public static final class Mapped {
        final String path;
        final Kind kind;
        final String why;

        Mapped(String path, Kind kind, String why) {
            this.path = path;
            this.kind = kind;
            this.why = why;
        }
    }

    public static boolean modern(Path root) {
        Path assets = root.resolve(RDPLPack.ASSETS);
        Path data = root.resolve(DATA);
        boolean hasAssets = Files.isDirectory(assets);
        boolean hasData = Files.isDirectory(data);
        if (!hasAssets && !hasData) { return false; }
        Integer format = packFormat(root);
        if (format != null) { return format > LEGACY_FORMAT; }
        if (hasData && hasNamespace(data)) { return true; }
        return hasAssets && modernAssets(assets);
    }

    @Nullable static Integer packFormat(Path root) {
        Path meta = root.resolve("pack.mcmeta");
        if (!Files.isRegularFile(meta)) { return null; }
        try {
            JsonElement parsed = new JsonParser().parse(new String(Files.readAllBytes(meta), StandardCharsets.UTF_8));
            JsonObject pack = parsed.isJsonObject() && parsed.getAsJsonObject().has("pack") && parsed.getAsJsonObject().get("pack").isJsonObject() ? parsed.getAsJsonObject().getAsJsonObject("pack") : null;
            if (pack == null || !pack.has("pack_format") || !pack.get("pack_format").isJsonPrimitive()) { return null; }
            return pack.get("pack_format").getAsInt();
        }
        catch (IOException | RuntimeException unreadable) { return null; }
    }

    private static boolean hasNamespace(Path data) {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(data)) {
            for (Path entry : entries) {
                if (Files.isDirectory(entry)) { return true; }
            }
        }
        catch (IOException ignored) { }
        return false;
    }

    private static boolean modernAssets(Path assets) {
        try (DirectoryStream<Path> namespaces = Files.newDirectoryStream(assets)) {
            for (Path namespace : namespaces) {
                if (!Files.isDirectory(namespace)) { continue; }
                if (Files.isDirectory(namespace.resolve("textures").resolve("block")) && !Files.isDirectory(namespace.resolve("textures").resolve("blocks"))) { return true; }
                if (jsonLangOnly(namespace.resolve("lang"))) { return true; }
            }
        }
        catch (IOException ignored) { }
        return false;
    }

    private static boolean jsonLangOnly(Path lang) throws IOException {
        if (!Files.isDirectory(lang)) { return false; }
        boolean json = false;
        try (DirectoryStream<Path> files = Files.newDirectoryStream(lang)) {
            for (Path file : files) {
                String name = trim(file.getFileName().toString());
                if (name.endsWith(".lang")) { return false; }
                if (name.endsWith(".json")) { json = true; }
            }
        }
        return json;
    }

    static String trim(String name) { return name.endsWith("/") ? name.substring(0, name.length() - 1) : name; }

    static Mapped assets(String path) {
        int slash = path.indexOf('/');
        String head = slash < 0 ? path : path.substring(0, slash);
        if (NO_TWIN_ASSETS.contains(head)) { return new Mapped(path, Kind.DROPPED, "the '" + head + "' folder has no twin on 1.12.2"); }
        if (path.startsWith("lang/") && path.endsWith(".json")) { return new Mapped(path.substring(0, path.length() - ".json".length()).toLowerCase(Locale.ROOT) + ".lang", Kind.LANG, ""); }
        if (path.startsWith("font/") && path.endsWith(".json")) { return new Mapped(path, Kind.DROPPED, "a font provider file has no twin on 1.12.2"); }
        if (path.startsWith("textures/")) {
            String moved = texturePath(path);
            return new Mapped(moved, path.endsWith(".json") ? Kind.PIXELMAP : Kind.RAW, "");
        }
        if (path.startsWith("models/") && path.endsWith(".json")) { return new Mapped(path, Kind.MODEL, ""); }
        if (path.startsWith("blockstates/") && path.endsWith(".json")) { return new Mapped(path, Kind.BLOCKSTATE, ""); }
        return new Mapped(path, Kind.RAW, "");
    }

    public static boolean unchanged(String path) {
        Mapped mapped = assets(path);
        return mapped.kind == Kind.RAW && mapped.path.equals(path);
    }

    static String texturePath(String path) {
        if (path.startsWith("textures/block/")) { return "textures/blocks/" + path.substring("textures/block/".length()); }
        if (path.startsWith("textures/item/")) { return "textures/items/" + path.substring("textures/item/".length()); }
        return path;
    }

    static Mapped data(String path) {
        int slash = path.indexOf('/');
        String head = slash < 0 ? path : path.substring(0, slash);
        String rest = slash < 0 ? "" : path.substring(slash + 1);
        String folder = SINGULAR.getOrDefault(head, head);
        String moved = slash < 0 ? path : folder + "/" + rest;
        if (NO_TWIN_DATA.contains(head)) { return new Mapped(path, Kind.DROPPED, "the '" + head + "' folder has no twin on 1.12.2"); }
        switch (folder) {
            case "recipes": return new Mapped(moved, path.endsWith(".json") ? Kind.RECIPE : Kind.RAW, "");
            case "loot_tables":
            case "loot_injections": return new Mapped(moved, path.endsWith(".json") ? Kind.LOOT : Kind.RAW, "");
            case "advancements": return new Mapped(moved, path.endsWith(".json") ? Kind.ADVANCEMENT : Kind.RAW, "");
            case "functions": return new Mapped(moved, path.endsWith(".mcfunction") ? Kind.FUNCTION : Kind.RAW, "");
            case "structures": return new Mapped(moved, path.endsWith(".nbt") ? Kind.STRUCTURE : Kind.RAW, "");
            case "tags": return tag(path, rest);
            case "worldgen": {
                int inner = rest.indexOf('/');
                if (inner > 0 && VANILLA_WORLDGEN.contains(rest.substring(0, inner))) { return new Mapped(path, Kind.DROPPED, "vanilla's data driven '" + rest.substring(0, inner) + "' worldgen has no twin on 1.12.2"); }
                return new Mapped(moved, path.endsWith(".json") ? Kind.DEFINITION : Kind.RAW, "");
            }
            default: return new Mapped(moved, path.endsWith(".json") ? Kind.DEFINITION : Kind.RAW, "");
        }
    }

    private static Mapped tag(String path, String rest) {
        int slash = rest.indexOf('/');
        String kind = slash < 0 ? rest : rest.substring(0, slash);
        if (("items".equals(kind) || "item".equals(kind)) && path.endsWith(".json")) { return new Mapped(path, Kind.ITEM_TAG, ""); }
        if (("functions".equals(kind) || "function".equals(kind)) && path.endsWith(".json")) { return new Mapped(path, Kind.FUNCTION_TAG, ""); }
        return new Mapped(path, Kind.DROPPED, "a '" + kind + "' tag has no twin on 1.12.2, where only items have an ore dictionary");
    }
}
