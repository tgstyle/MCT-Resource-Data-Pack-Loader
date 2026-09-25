package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.pack.RDPLPack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import javax.annotation.Nullable;

public final class Port {
    public static final String LANG_SUFFIX = ".lang";
    private static final String SOUNDS = "sounds";
    private static final Set<String> CLIENT_FOLDERS = Set.of("models", "blockstates", "textures", "lang", "texts", "font", "shaders", "particles", "sounds.json", "gui", "logo.png");
    private static final String[][] FOLDERS = {{"loot_tables/", "loot_table/"}, {"recipes/", "recipe/"}, {"advancements/", "advancement/"}, {"functions/", "function/"}, {"structures/", "structure/"}, {"predicates/", "predicate/"}, {"item_modifiers/", "item_modifier/"}, {"tags/items/", "tags/item/"}, {"tags/blocks/", "tags/block/"}, {"tags/fluids/", "tags/fluid/"}, {"tags/functions/", "tags/function/"}, {"tags/entity_types/", "tags/entity_type/"}, {"tags/game_events/", "tags/game_event/"}};
    private static final int LAST_1_20_FORMAT = 26;
    private static final int FIRST_1_21_FORMAT = 41;
    public static final Set<String> DEFINITION_FOLDERS = Set.of("blocks", "items", "fluids", "materials", "tabs", "biomes", "worldgen", "dimensions", "worldtemplates", "gates", "gamerules", "entities", "potions", "potion_types", "villagers", "trades", "villages", "structuremaps", "citymaps", "caveregions", "hardness", "anvils", "exposures", "overrides", "teams", "scoring", "raids", "worldintro", "cards", "portalframes", "blastplaster", "pathintersects", "player_loot", "registry_remap", "oredict", "block_drops", "brewing", "fuels", "furnace", "recipe_removals", "loot_injections");

    private Port() {}

    public enum Kind { RAW, DEFINITION, LANG, MODEL, BLOCKSTATE, RECIPE, LOOT, ADVANCEMENT, FUNCTION, OREDICT, PIXELMAP, DROPPED }

    public record Mapped(PackType type, String path, Kind kind) {}

    public enum Line {
        V1_20("1.20.1"), V1_21("1.21.1");

        private final String name;

        Line(String name) { this.name = name; }

        public String title() { return name; }

        public static Line running() { return SharedConstants.getCurrentVersion().getName().startsWith("1.21") ? V1_21 : V1_20; }
    }

    @Nullable public static String singular(String path) { return swapped(path, 0); }

    @Nullable public static String plural(String path) { return swapped(path, 1); }

    @Nullable private static String swapped(String path, int from) {
        for (String[] pair : FOLDERS) {
            if (path.startsWith(pair[from])) { return pair[1 - from] + path.substring(pair[from].length()); }
        }
        return null;
    }

    @Nullable public static Line foreign(Path root) {
        Line line = line(root);
        return line == null || line == Line.running() ? null : line;
    }

    @Nullable private static Line line(Path root) {
        Path data = root.resolve(RDPLPack.DATA);
        int plural = 0;
        int singular = 0;
        if (Files.isDirectory(data)) {
            try (DirectoryStream<Path> namespaces = Files.newDirectoryStream(data)) {
                for (Path namespace : namespaces) {
                    for (String[] pair : FOLDERS) {
                        if (Files.isDirectory(namespace.resolve(folder(pair[0])))) { plural++; }
                        if (Files.isDirectory(namespace.resolve(folder(pair[1])))) { singular++; }
                    }
                }
            }
            catch (IOException ignored) { }
        }
        if (plural > 0 && singular == 0) { return Line.V1_20; }
        if (singular > 0 && plural == 0) { return Line.V1_21; }
        int format = packFormat(root);
        if (format > 0 && format <= LAST_1_20_FORMAT) { return Line.V1_20; }
        return format >= FIRST_1_21_FORMAT ? Line.V1_21 : null;
    }

    private static String folder(String prefix) { return prefix.substring(0, prefix.length() - 1); }

    private static int packFormat(Path root) {
        Path meta = root.resolve("pack.mcmeta");
        if (!Files.isRegularFile(meta)) { return 0; }
        try {
            JsonObject json = JsonParser.parseString(Files.readString(meta, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject pack = json.has("pack") && json.get("pack").isJsonObject() ? json.getAsJsonObject("pack") : null;
            return pack != null && pack.has("pack_format") ? pack.get("pack_format").getAsInt() : 0;
        }
        catch (IOException | RuntimeException ignored) { return 0; }
    }

    public static boolean legacy(Path root) {
        Path assets = root.resolve(RDPLPack.ASSETS);
        if (!Files.isDirectory(assets)) { return false; }
        Path meta = root.resolve("pack.mcmeta");
        if (Files.isRegularFile(meta)) {
            try {
                JsonObject json = JsonParser.parseString(Files.readString(meta, StandardCharsets.UTF_8)).getAsJsonObject();
                JsonObject pack = json.has("pack") && json.get("pack").isJsonObject() ? json.getAsJsonObject("pack") : null;
                if (pack != null && pack.has("pack_format") && pack.get("pack_format").getAsInt() <= 3) { return true; }
            }
            catch (IOException | RuntimeException ignored) { }
        }
        try (DirectoryStream<Path> namespaces = Files.newDirectoryStream(assets)) {
            for (Path namespace : namespaces) {
                if (!Files.isDirectory(namespace)) { continue; }
                try (DirectoryStream<Path> folders = Files.newDirectoryStream(namespace)) {
                    for (Path folder : folders) {
                        String name = folder.getFileName().toString().replace("/", "");
                        if (Files.isDirectory(folder) && DEFINITION_FOLDERS.contains(name) && !Files.isDirectory(root.resolve(RDPLPack.DATA))) { return true; }
                        if (Files.isDirectory(folder) && "lang".equals(name) && hasLangFile(folder)) { return true; }
                    }
                }
            }
        }
        catch (IOException ignored) { }
        return false;
    }

    private static boolean hasLangFile(Path lang) throws IOException {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(lang)) {
            for (Path file : files) { if (file.getFileName().toString().replace("/", "").endsWith(LANG_SUFFIX)) { return true; } }
        }
        return false;
    }

    public static Mapped map(String path) {
        int slash = path.indexOf('/');
        String head = slash < 0 ? path : path.substring(0, slash);
        if (slash > 0 && SOUNDS.equals(head) && !path.endsWith(".json")) { return new Mapped(PackType.CLIENT_RESOURCES, path, Kind.RAW); }
        if (slash < 0 || CLIENT_FOLDERS.contains(head)) { return client(path); }
        return data(path, head);
    }

    public static boolean unchanged(String path) {
        Mapped mapped = map(path);
        return mapped.kind() == Kind.RAW && mapped.type() == PackType.CLIENT_RESOURCES && mapped.path().equals(path);
    }

    private static Mapped client(String path) {
        if (path.startsWith("lang/") && path.endsWith(LANG_SUFFIX)) { return new Mapped(PackType.CLIENT_RESOURCES, path.substring(0, path.length() - LANG_SUFFIX.length()) + ".json", Kind.LANG); }
        if (path.startsWith("textures/blocks/") || path.startsWith("textures/items/")) {
            String moved = path.startsWith("textures/blocks/") ? "textures/block/" + path.substring("textures/blocks/".length()) : "textures/item/" + path.substring("textures/items/".length());
            return new Mapped(PackType.CLIENT_RESOURCES, moved, path.endsWith(".json") ? Kind.PIXELMAP : Kind.RAW);
        }
        if (path.startsWith("textures/") && path.endsWith(".json")) { return new Mapped(PackType.CLIENT_RESOURCES, path, Kind.PIXELMAP); }
        if (path.startsWith("models/") && path.endsWith(".json")) {
            String[] parts = path.split("/");
            if (parts.length == 4 && "item".equals(parts[1])) { return new Mapped(PackType.CLIENT_RESOURCES, "models/item/" + parts[3], Kind.MODEL); }
            return new Mapped(PackType.CLIENT_RESOURCES, path, Kind.MODEL);
        }
        if (path.startsWith("blockstates/") && path.endsWith(".json")) { return new Mapped(PackType.CLIENT_RESOURCES, path, Kind.BLOCKSTATE); }
        return new Mapped(PackType.CLIENT_RESOURCES, path, Kind.RAW);
    }

    private static Mapped data(String path, String head) {
        return switch (head) {
            case "recipes" -> new Mapped(PackType.SERVER_DATA, path, path.endsWith(".json") ? Kind.RECIPE : Kind.RAW);
            case "loot_tables", "loot_injections" -> new Mapped(PackType.SERVER_DATA, path, path.endsWith(".json") ? Kind.LOOT : Kind.RAW);
            case "advancements" -> new Mapped(PackType.SERVER_DATA, path, path.endsWith(".json") ? Kind.ADVANCEMENT : Kind.RAW);
            case "functions" -> new Mapped(PackType.SERVER_DATA, path, Kind.FUNCTION);
            case "oredict" -> new Mapped(PackType.SERVER_DATA, path, path.endsWith(".json") ? Kind.OREDICT : Kind.DROPPED);
            case "structures", "tags" -> new Mapped(PackType.SERVER_DATA, path, Kind.RAW);
            default -> new Mapped(PackType.SERVER_DATA, path, path.endsWith(".json") ? Kind.DEFINITION : Kind.RAW);
        };
    }
}
