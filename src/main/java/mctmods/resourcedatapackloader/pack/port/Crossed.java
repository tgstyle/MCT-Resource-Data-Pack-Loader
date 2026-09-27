package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.compat.LinePort;
import mctmods.resourcedatapackloader.pack.PackMeta;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.LineNote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public final class Crossed implements IPackPort {
    private static final String JSON = ".json";
    private static final String TAGS = "tags/";
    private static final String BIOME_MODIFIERS = "/biome_modifier/";
    private static final String LOOT_MODIFIERS = "loot_modifiers/";
    private final String name;
    private final Path root;
    private final Port.Line from;
    @Nullable private final Port.Line to;
    private final boolean modern;
    private final boolean lined;
    private final Map<PackType, Map<String, Map<String, Source>>> exposed = new EnumMap<>(PackType.class);
    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    private final Set<String> notes = new LinkedHashSet<>();
    private boolean reported;
    private int moved;
    private int rewritten;

    public Crossed(String name, Path root, Port.Line from) {
        this.name = name;
        this.root = root;
        this.from = from;
        this.to = step(from);
        this.modern = Modern.active() && from.ordinal() < Port.Line.V26.ordinal();
        this.lined = from.acrossSplit();
    }

    private record Source(Path real, String file, Port.Kind kind, boolean same, String before) {}

    private record Target(String namespace, String path) {}

    Port.Line to() { return to == null ? from : to; }

    void note(String said) {
        if (notes.add(said) && reported) { ContentLog.LOGGER.info("Pack '{}': {}", name, said); }
    }

    @Override public String origin() { return from.title(); }

    @Override public boolean reads(PackType type) { return type == PackType.SERVER_DATA || (modern || lined) && type == PackType.CLIENT_RESOURCES; }

    @Nullable private static Port.Line step(Port.Line from) {
        Port.Line running = Port.Line.running();
        if (from == Port.Line.V1_20) { return running == Port.Line.V1_20 ? null : Port.Line.V1_21; }
        return running == Port.Line.V1_20 ? Port.Line.V1_20 : null;
    }

    public static boolean moves(String namespace, String path, Port.Line from) {
        boolean stepped = step(from) != null;
        if (!stepped && !from.acrossSplit()) { return false; }
        Target target = stepped ? target(namespace, path, from, null) : new Target(namespace, path);
        String exposed = from.acrossSplit() ? LinePort.path(target.path()) : target.path();
        return !target.namespace().equals(namespace) || !exposed.equals(path);
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
        String layout = to == Port.Line.V1_20 ? Port.singular(path) : path;
        String singular = layout == null ? path : layout;
        if (singular.startsWith("function/")) { return singular.endsWith(".mcfunction") ? Port.Kind.FUNCTION : Port.Kind.RAW; }
        if (!singular.endsWith(JSON)) { return Port.Kind.RAW; }
        if (singular.startsWith("recipe/")) { return Port.Kind.RECIPE; }
        if (singular.startsWith("loot_table/") || singular.startsWith("predicate/") || singular.startsWith("item_modifier/") || singular.startsWith(LOOT_MODIFIERS)) { return Port.Kind.LOOT; }
        return singular.startsWith("advancement/") ? Port.Kind.ADVANCEMENT : Port.Kind.DEFINITION;
    }

    @Override public void index(PackType type, String namespace, List<String> realPaths) {
        Path home = root.resolve(type.getDirectory()).resolve(namespace);
        if (type == PackType.CLIENT_RESOURCES) {
            assets(home, namespace, realPaths);
            return;
        }
        for (String path : realPaths) {
            Target target = to == null ? new Target(namespace, path) : target(namespace, path, from, this);
            String exposed = lined ? LinePort.path(target.path()) : target.path();
            boolean same = target.namespace().equals(namespace) && exposed.equals(path);
            if (!same) { moved++; }
            expose(type, target.namespace(), exposed, new Source(home.resolve(path), RDPLPack.DATA + "/" + namespace + "/" + path, kind(target.path()), same, target.path()));
        }
    }

    private void assets(Path home, String namespace, List<String> realPaths) {
        String base = RDPLPack.ASSETS + "/" + namespace + "/";
        for (String path : realPaths) {
            Path real = home.resolve(path);
            boolean model = path.startsWith("models/") && path.endsWith(JSON);
            String exposedPath = lined ? LinePort.assetPath(path) : path;
            if (!exposedPath.equals(path)) { moved++; }
            Port.Kind kind = model && modern ? Port.Kind.MODEL : lined && path.endsWith(JSON) ? Port.Kind.DEFINITION : Port.Kind.RAW;
            expose(PackType.CLIENT_RESOURCES, namespace, exposedPath, new Source(real, base + path, kind, exposedPath.equals(path), path));
            if (!modern) { continue; }
            String alias = ModernAssets.moved(path);
            if (alias != null && !realPaths.contains(alias)) {
                expose(PackType.CLIENT_RESOURCES, namespace, alias, new Source(real, base + path, Port.Kind.RAW, false, alias));
                moved++;
            }
            String definition = model ? ModernAssets.definitionPath(path) : null;
            if (definition == null || realPaths.contains(definition)) { continue; }
            JsonObject made;
            try { made = ModernAssets.definition(namespace, path, readJson(real), base + path, this::note); }
            catch (RuntimeException failed) {
                note("'" + base + path + "' could not be ported and is served as written: " + failed);
                continue;
            }
            if (made == null) { continue; }
            cache.put(key(PackType.CLIENT_RESOURCES, namespace, definition), Ported.GSON.toJson(made).getBytes(StandardCharsets.UTF_8));
            expose(PackType.CLIENT_RESOURCES, namespace, definition, new Source(real, base + path, Port.Kind.DEFINITION, false, definition));
            note("'" + base + path + "' switches models with overrides, which item models no longer read, so they became the item definition " + namespace + ":" + definition);
        }
    }

    private void expose(PackType type, String namespace, String path, Source source) {
        exposed.computeIfAbsent(type, _ -> new LinkedHashMap<>()).computeIfAbsent(namespace, _ -> new LinkedHashMap<>()).putIfAbsent(path, source);
    }

    @Nullable private static JsonObject readJson(Path file) {
        try {
            JsonElement held = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            return held.isJsonObject() ? held.getAsJsonObject() : null;
        }
        catch (IOException | RuntimeException failed) { return null; }
    }

    private static String key(PackType type, String namespace, String path) { return type.getDirectory() + "/" + namespace + "/" + path; }

    @Override public Map<PackType, Map<String, Set<String>>> exposed() {
        Map<PackType, Map<String, Set<String>>> out = new EnumMap<>(PackType.class);
        exposed.forEach((type, namespaces) -> {
            Map<String, Set<String>> paths = new LinkedHashMap<>();
            namespaces.forEach((namespace, held) -> paths.put(namespace, new LinkedHashSet<>(held.keySet())));
            out.put(type, paths);
        });
        return out;
    }

    @Override @Nullable public InputStream open(PackType type, String namespace, String path) throws IOException {
        Source source = exposed.getOrDefault(type, Map.of()).getOrDefault(namespace, Map.of()).get(path);
        if (source == null) { return null; }
        if (source.kind() == Port.Kind.RAW) { return Files.newInputStream(source.real()); }
        String key = key(type, namespace, path);
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
                String out = to == null ? contents : CrossCommands.function(contents, source.file(), this);
                if (modern) { out = Modern.function(out, source.file(), this::note); }
                if (lined) { out = LinePort.function(out, said(source)); }
                return out.equals(contents) ? original : out.getBytes(StandardCharsets.UTF_8);
            }
            JsonElement json = JsonParser.parseString(contents);
            if (!json.isJsonObject() && !lined) { return original; }
            JsonElement before = json.deepCopy();
            boolean asset = source.file().startsWith(RDPLPack.ASSETS + "/");
            if (json.isJsonObject()) { earlier(json.getAsJsonObject(), source, asset); }
            JsonElement held = !lined ? json : asset ? LinePort.asset(json, source.before(), said(source)) : LinePort.data(json, path, said(source));
            if (held.equals(before)) { return original; }
            rewritten++;
            return Ported.GSON.toJson(held).getBytes(StandardCharsets.UTF_8);
        }
        catch (RuntimeException failed) {
            note("'" + source.file() + "' could not be ported and is served as written: " + failed);
            return original;
        }
    }

    private void earlier(JsonObject held, Source source, boolean asset) {
        String path = source.before();
        if (asset) {
            if (source.kind() == Port.Kind.MODEL) { ModernAssets.model(held); }
            return;
        }
        if (to != null) {
            switch (source.kind()) {
                case RECIPE -> CrossJson.recipe(held, source.file(), this);
                case LOOT -> CrossLoot.loot(held, source.file(), this);
                case ADVANCEMENT -> CrossJson.advancement(held, source.file(), this);
                default -> CrossJson.definition(held, CrossIds.registry(to == Port.Line.V1_21 ? path : singular(path)), source.file(), this);
            }
        }
        if (modern) { Modern.data(held, source.kind(), path, source.file(), from == Port.Line.V1_21, this::note); }
    }

    private LineNote said(Source source) {
        String[] parts = source.file().split("/", 3);
        return new LineNote() {
            @Override public void accept(String said) { note("'" + source.file() + "' " + said); }

            @Override @Nullable public JsonElement carried(String folder, String id) { return Crossed.this.carried(folder, id); }

            @Override public String namespace() { return parts.length > 1 ? parts[1] : LineNote.super.namespace(); }
        };
    }

    @Nullable private JsonElement carried(String folder, String id) {
        int colon = id.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : id.substring(0, colon);
        Source source = exposed.getOrDefault(PackType.SERVER_DATA, Map.of()).getOrDefault(namespace, Map.of()).get(folder + "/" + id.substring(colon + 1) + JSON);
        if (source == null) { return null; }
        try { return JsonParser.parseString(Files.readString(source.real(), StandardCharsets.UTF_8)); }
        catch (IOException | RuntimeException failed) { return null; }
    }

    private static String singular(String path) {
        String folder = Port.singular(path);
        return folder == null ? path : folder;
    }

    @Override public void report() {
        reported = true;
        ContentLog.LOGGER.info("Pack '{}' is written for {} and is read through the port to {}: {} file(s) moved to this version's names, ids and keys rewritten as they are read", name, from.title(), Port.Line.running().title(), moved);
        List<String> said = new ArrayList<>(notes);
        for (int i = 0; i < Math.min(said.size(), 40); i++) { ContentLog.LOGGER.info("  {}", said.get(i)); }
        if (said.size() > 40) { ContentLog.LOGGER.info("  ... and {} more", said.size() - 40); }
    }

    @Override public void writeVersion(ZipOutputStream out, String prefix) throws IOException {
        int written = 0;
        for (Map.Entry<PackType, Map<String, Map<String, Source>>> type : exposed.entrySet()) {
            for (Map.Entry<String, Map<String, Source>> namespace : type.getValue().entrySet()) {
                for (Map.Entry<String, Source> path : namespace.getValue().entrySet()) {
                    byte[] bytes;
                    try (InputStream in = open(type.getKey(), namespace.getKey(), path.getKey())) {
                        if (in == null) { continue; }
                        bytes = in.readAllBytes();
                    }
                    if (path.getValue().same() && Arrays.equals(bytes, Files.readAllBytes(path.getValue().real()))) { continue; }
                    out.putNextEntry(new ZipEntry(prefix + type.getKey().getDirectory() + "/" + namespace.getKey() + "/" + path.getKey()));
                    out.write(bytes);
                    out.closeEntry();
                    written++;
                }
            }
        }
        Path metaFile = root.resolve("pack.mcmeta");
        JsonObject old = null;
        if (Files.isRegularFile(metaFile)) {
            old = readJson(metaFile);
            if (old == null) { note("its pack.mcmeta could not be read, so the one written here carries the pack format alone"); }
        }
        JsonObject pack = old != null && old.has("pack") && old.get("pack").isJsonObject() ? old.getAsJsonObject("pack") : new JsonObject();
        PackMeta.formats(pack, PackType.SERVER_DATA);
        if (!pack.has("description")) { pack.addProperty("description", name); }
        JsonObject meta = new JsonObject();
        meta.add("pack", pack);
        out.putNextEntry(new ZipEntry(prefix + "pack.mcmeta"));
        out.write(Ported.GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        note("written into " + prefix + ", " + written + " file(s) " + Port.Line.running().title() + " reads differently; the rest is read from the root as it is");
    }

    @Override public void closing() {
        if (rewritten > 0) { ContentLog.LOGGER.info("Pack '{}': the port from {} rewrote {} file(s) while the pack was read", name, from.title(), rewritten); }
    }
}
