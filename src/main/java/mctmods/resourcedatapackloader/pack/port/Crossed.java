package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

public final class Crossed implements PackPort {
    private static final String JSON = ".json";
    private static final String TAGS = "tags/";
    private static final String BIOME_MODIFIERS = "/biome_modifier/";
    private static final String LOOT_MODIFIERS = "loot_modifiers/";
    private final String name;
    private final Path root;
    private final Port.Line from;
    private final Port.Line to;
    private final Map<String, Map<String, Source>> exposed = new LinkedHashMap<>();
    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    private final Set<String> notes = new LinkedHashSet<>();
    private boolean reported;
    private int moved;
    private int rewritten;

    public Crossed(String name, Path root, Port.Line from) {
        this.name = name;
        this.root = root;
        this.from = from;
        this.to = from == Port.Line.V1_20 ? Port.Line.V1_21 : Port.Line.V1_20;
    }

    private record Source(Path real, String file, Port.Kind kind, boolean same) {}

    private record Target(String namespace, String path) {}

    Port.Line to() { return to; }

    void note(String said) {
        if (notes.add(said) && reported) { ContentLog.LOGGER.info("Pack '{}': {}", name, said); }
    }

    @Override public String origin() { return from.title(); }

    @Override public PackType reads() { return PackType.SERVER_DATA; }

    public static boolean moves(String namespace, String path, Port.Line from) {
        Target target = target(namespace, path, from, null);
        return !target.namespace().equals(namespace) || !target.path().equals(path);
    }

    private static Target target(String namespace, String path, Port.Line from, @Nullable Crossed pack) {
        String folder = from == Port.Line.V1_20 ? Port.singular(path) : Port.plural(path);
        String moved = folder == null ? path : folder;
        String loaderFrom = from == Port.Line.V1_20 ? CrossIds.FORGE : CrossIds.NEOFORGE;
        String loaderTo = from == Port.Line.V1_20 ? CrossIds.NEOFORGE : CrossIds.FORGE;
        if (moved.startsWith(loaderFrom + BIOME_MODIFIERS)) { return new Target(namespace, loaderTo + moved.substring(loaderFrom.length())); }
        if (loaderFrom.equals(namespace) && moved.startsWith(LOOT_MODIFIERS)) { return new Target(loaderTo, moved); }
        String singular = from == Port.Line.V1_20 ? moved : path;
        String registry = CrossIds.registry(singular);
        if (registry == null || !moved.endsWith(JSON)) { return new Target(namespace, moved); }
        String rest = singular.substring((TAGS + registry + "/").length(), singular.length() - JSON.length());
        String prefix = moved.substring(0, moved.length() - rest.length() - JSON.length());
        Port.Line to = from == Port.Line.V1_20 ? Port.Line.V1_21 : Port.Line.V1_20;
        String converted = CrossIds.tag(registry, namespace + ":" + rest, to);
        if (converted.contains(CrossIds.BOTH)) {
            if (pack != null) { pack.note("the tag file " + namespace + ":" + rest + " is " + converted.replace(CrossIds.BOTH, " and ") + " together on " + to.title() + ", so it is written as " + CrossIds.COMMON + ":" + rest + "; name the tags it belongs in by hand"); }
            converted = CrossIds.COMMON + ":" + rest;
        }
        int colon = converted.indexOf(':');
        return new Target(converted.substring(0, colon), prefix + converted.substring(colon + 1) + JSON);
    }

    private Port.Kind kind(String path) {
        String layout = to == Port.Line.V1_21 ? path : Port.singular(path);
        String singular = layout == null ? path : layout;
        if (singular.startsWith("function/")) { return singular.endsWith(".mcfunction") ? Port.Kind.FUNCTION : Port.Kind.RAW; }
        if (!singular.endsWith(JSON)) { return Port.Kind.RAW; }
        if (singular.startsWith("recipe/")) { return Port.Kind.RECIPE; }
        if (singular.startsWith("loot_table/") || singular.startsWith("predicate/") || singular.startsWith("item_modifier/") || singular.startsWith(LOOT_MODIFIERS)) { return Port.Kind.LOOT; }
        return singular.startsWith("advancement/") ? Port.Kind.ADVANCEMENT : Port.Kind.DEFINITION;
    }

    @Override public void index(String namespace, List<String> realPaths) {
        Path home = root.resolve(RDPLPack.DATA).resolve(namespace);
        for (String path : realPaths) {
            Target target = target(namespace, path, from, this);
            boolean same = target.namespace().equals(namespace) && target.path().equals(path);
            if (!same) { moved++; }
            Source source = new Source(home.resolve(path), RDPLPack.DATA + "/" + namespace + "/" + path, kind(target.path()), same);
            exposed.computeIfAbsent(target.namespace(), k -> new LinkedHashMap<>()).putIfAbsent(target.path(), source);
        }
    }

    @Override public Map<PackType, Map<String, Set<String>>> exposed() {
        Map<String, Set<String>> namespaces = new LinkedHashMap<>();
        exposed.forEach((namespace, paths) -> namespaces.put(namespace, new LinkedHashSet<>(paths.keySet())));
        Map<PackType, Map<String, Set<String>>> out = new EnumMap<>(PackType.class);
        out.put(PackType.SERVER_DATA, namespaces);
        return out;
    }

    @Override @Nullable public InputStream open(PackType type, String namespace, String path) throws IOException {
        if (type != PackType.SERVER_DATA) { return null; }
        Source source = exposed.getOrDefault(namespace, Map.of()).get(path);
        if (source == null) { return null; }
        if (source.kind() == Port.Kind.RAW) { return Files.newInputStream(source.real()); }
        String key = namespace + "/" + path;
        byte[] held = cache.get(key);
        if (held == null) {
            held = convert(source, path);
            cache.put(key, held);
        }
        return new ByteArrayInputStream(held);
    }

    private byte[] convert(Source source, String path) throws IOException {
        byte[] original = Files.readAllBytes(source.real());
        String contents = new String(original, StandardCharsets.UTF_8);
        try {
            if (source.kind() == Port.Kind.FUNCTION) {
                String out = CrossCommands.function(contents, source.file(), this);
                return out.equals(contents) ? original : out.getBytes(StandardCharsets.UTF_8);
            }
            JsonElement json = JsonParser.parseString(contents);
            if (!json.isJsonObject()) { return original; }
            JsonObject before = json.getAsJsonObject().deepCopy();
            JsonObject held = json.getAsJsonObject();
            switch (source.kind()) {
                case RECIPE -> CrossJson.recipe(held, source.file(), this);
                case LOOT -> CrossLoot.loot(held, source.file(), this);
                case ADVANCEMENT -> CrossJson.advancement(held, source.file(), this);
                default -> CrossJson.definition(held, CrossIds.registry(to == Port.Line.V1_21 ? path : singular(path)), source.file(), this);
            }
            if (held.equals(before)) { return original; }
            rewritten++;
            return Ported.GSON.toJson(held).getBytes(StandardCharsets.UTF_8);
        }
        catch (RuntimeException failed) {
            note("'" + source.file() + "' could not be ported and is served as written: " + failed);
            return original;
        }
    }

    private static String singular(String path) {
        String folder = Port.singular(path);
        return folder == null ? path : folder;
    }

    @Override public void report() {
        reported = true;
        ContentLog.LOGGER.info("Pack '{}' is written for {} and is read through the port to {}: {} file(s) moved to this version's folder names, ids and keys rewritten as they are read", name, from.title(), to.title(), moved);
        List<String> said = new ArrayList<>(notes);
        for (int i = 0; i < Math.min(said.size(), 40); i++) { ContentLog.LOGGER.info("  {}", said.get(i)); }
        if (said.size() > 40) { ContentLog.LOGGER.info("  ... and {} more", said.size() - 40); }
    }

    @Override public void writeVersion(ZipOutputStream out, String prefix) throws IOException {
        int written = 0;
        for (Map.Entry<String, Map<String, Source>> namespace : exposed.entrySet()) {
            for (Map.Entry<String, Source> path : namespace.getValue().entrySet()) {
                byte[] bytes;
                try (InputStream in = open(PackType.SERVER_DATA, namespace.getKey(), path.getKey())) {
                    if (in == null) { continue; }
                    bytes = in.readAllBytes();
                }
                if (path.getValue().same() && Arrays.equals(bytes, Files.readAllBytes(path.getValue().real()))) { continue; }
                out.putNextEntry(new ZipEntry(prefix + RDPLPack.DATA + "/" + namespace.getKey() + "/" + path.getKey()));
                out.write(bytes);
                out.closeEntry();
                written++;
            }
        }
        Path metaFile = root.resolve("pack.mcmeta");
        JsonObject old = null;
        if (Files.isRegularFile(metaFile)) {
            try { old = JsonParser.parseString(Files.readString(metaFile, StandardCharsets.UTF_8)).getAsJsonObject(); }
            catch (RuntimeException unreadable) { note("its pack.mcmeta could not be read, so the one written here carries the pack format alone"); }
        }
        JsonObject pack = old != null && old.has("pack") && old.get("pack").isJsonObject() ? old.getAsJsonObject("pack") : new JsonObject();
        pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
        pack.remove("supported_formats");
        if (!pack.has("description")) { pack.addProperty("description", name); }
        JsonObject meta = new JsonObject();
        meta.add("pack", pack);
        out.putNextEntry(new ZipEntry(prefix + "pack.mcmeta"));
        out.write(Ported.GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        note("written into " + prefix + ", " + written + " file(s) " + to.title() + " reads differently; the rest is read from the root as it is");
    }

    @Override public void closing() {
        if (rewritten > 0) { ContentLog.LOGGER.info("Pack '{}': the port from {} rewrote {} file(s) while the pack was read", name, from.title(), rewritten); }
    }
}
