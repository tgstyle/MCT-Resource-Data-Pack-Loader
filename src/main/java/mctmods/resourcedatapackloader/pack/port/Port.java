package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.pack.RDPLPack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.packs.PackType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public final class Port {
    public static final String LANG_SUFFIX = ".lang";
    private static final Set<String> CLIENT_FOLDERS = Set.of("models", "blockstates", "textures", "lang", "texts", "font", "shaders", "particles", "sounds.json", "gui", "logo.png");
    private static final Set<String> DEFINITION_FOLDERS = Set.of("blocks", "items", "fluids", "materials", "tabs", "biomes", "worldgen", "dimensions", "worldtemplates", "gates", "gamerules", "entities", "potions", "potion_types", "villagers", "trades", "villages", "structuremaps", "citymaps", "caveregions", "hardness", "exposures", "overrides", "teams", "scoring", "worldintro", "portalframes", "blastplaster", "pathintersects", "player_loot", "registry_remap", "oredict", "block_drops", "brewing", "fuels", "furnace", "recipe_removals", "loot_injections");

    private Port() {}

    public enum Kind { RAW, DEFINITION, LANG, MODEL, BLOCKSTATE, RECIPE, LOOT, ADVANCEMENT, FUNCTION, OREDICT, PIXELMAP, DROPPED }

    public record Mapped(PackType type, String path, Kind kind) {}

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
        if (slash < 0 || CLIENT_FOLDERS.contains(head)) { return client(path); }
        return data(path, head);
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
            case "block_drops" -> new Mapped(PackType.SERVER_DATA, path, Kind.DROPPED);
            case "structures", "tags" -> new Mapped(PackType.SERVER_DATA, path, Kind.RAW);
            default -> new Mapped(PackType.SERVER_DATA, path, path.endsWith(".json") ? Kind.DEFINITION : Kind.RAW);
        };
    }
}
