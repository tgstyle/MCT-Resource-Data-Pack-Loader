package mctmods.resourcedatapackloader.pack.port;

import static mctmods.resourcedatapackloader.pack.RDPLPack.key;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackMeta;
import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.LineNote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.packs.PackType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

public final class Crossed extends PortBase<Crossed.Source> {
    private static final String JSON = ".json";
    private static final String TAGS = "tags/";
    private static final String BIOME_MODIFIERS = "/biome_modifier/";
    private static final String LOOT_MODIFIERS = "loot_modifiers/";
    private final Port.Line from;
    @Nullable private final Port.Line to;
    private final boolean down;
    private final boolean lined;
    private final Map<String, Definition> definitions = new HashMap<>();
    private int moved;

    public Crossed(String name, Path root, Port.Line from) {
        super(name, root);
        this.from = from;
        this.to = step(from);
        this.down = from == Port.Line.V26 || from == Port.Line.V26_3;
        this.lined = from == Port.Line.V26_3;
    }

    record Source(Path real, String file, Port.Kind kind, boolean same) implements Served {}

    private record Target(String namespace, String path) {}

    private record Definition(String namespace, String modelPath, Path real) {}

    Port.Line to() { return to == null ? Port.Line.running() : to; }

    @Override public String origin() { return from.title(); }

    @Override public boolean reads(PackType type) { return type == PackType.SERVER_DATA || down && type == PackType.CLIENT_RESOURCES; }

    @Nullable private static Port.Line step(Port.Line from) {
        Port.Line running = Port.Line.running();
        if (from == Port.Line.V1_20) { return running == Port.Line.V1_20 ? null : Port.Line.V1_21; }
        return running == Port.Line.V1_20 ? Port.Line.V1_20 : null;
    }

    public static boolean keepsPlace(String namespace, String path, Port.Line from) {
        boolean stepped = step(from) != null;
        boolean lined = from == Port.Line.V26_3;
        if (!stepped && !lined) { return true; }
        String renamed = lined ? LinePort.path(path) : path;
        Target target = stepped ? target(namespace, renamed, from, null) : new Target(namespace, renamed);
        return target.namespace().equals(namespace) && target.path().equals(path);
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
        String singular = layout(path);
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
            String renamed = lined ? LinePort.path(path) : path;
            Target target = to == null ? new Target(namespace, renamed) : target(namespace, renamed, from, this);
            boolean same = target.namespace().equals(namespace) && target.path().equals(path);
            if (!same) { moved++; }
            expose(type, target.namespace(), target.path(), new Source(home.resolve(path), RDPLPack.DATA + "/" + namespace + "/" + path, kind(target.path()), same));
        }
    }

    private void assets(Path home, String namespace, List<String> realPaths) {
        String base = RDPLPack.ASSETS + "/" + namespace + "/";
        Set<String> present = new HashSet<>(realPaths);
        for (String written : realPaths) {
            Path real = home.resolve(written);
            String path = lined ? LinePort.assetPath(written) : written;
            boolean model = path.startsWith("models/") && path.endsWith(JSON);
            boolean same = path.equals(written);
            if (!same) { moved++; }
            Port.Kind kind = model ? Port.Kind.MODEL : lined && path.endsWith(JSON) ? Port.Kind.DEFINITION : Port.Kind.RAW;
            expose(PackType.CLIENT_RESOURCES, namespace, path, new Source(real, base + written, kind, same));
            String alias = DownAssets.moved(path);
            if (alias != null && !present.contains(alias)) {
                expose(PackType.CLIENT_RESOURCES, namespace, alias, new Source(real, base + path, Port.Kind.RAW, false));
                moved++;
            }
            String modelPath = DownAssets.modelPath(path);
            if (modelPath == null) { continue; }
            if (present.contains(modelPath)) {
                definitions.put(base + modelPath, new Definition(namespace, modelPath, real));
                continue;
            }
            JsonObject made;
            try { made = DownAssets.made(namespace, modelPath, readJson(real), base + path, this::note); }
            catch (RuntimeException failed) {
                note("'" + base + path + "' could not be ported and is left out: " + failed);
                continue;
            }
            if (made == null) { continue; }
            cache.put(key(PackType.CLIENT_RESOURCES, namespace, modelPath), Ported.GSON.toJson(made).getBytes(StandardCharsets.UTF_8));
            expose(PackType.CLIENT_RESOURCES, namespace, modelPath, new Source(real, base + path, Port.Kind.DEFINITION, false));
            note("'" + base + path + "' is an item definition, which " + Port.Line.running().title() + " does not read, so it became the item model " + namespace + ":" + modelPath);
        }
    }

    @Override byte[] convert(Source source, String namespace, String path) throws IOException {
        byte[] original = Files.readAllBytes(source.real());
        String contents = new String(original, StandardCharsets.UTF_8);
        try {
            if (source.kind() == Port.Kind.FUNCTION) {
                String out = lined ? LinePort.function(contents, said(source)) : contents;
                if (down) { out = Down.function(out, source.file(), this::note); }
                if (to != null) { out = CrossCommands.function(out, source.file(), this); }
                return out.equals(contents) ? original : out.getBytes(StandardCharsets.UTF_8);
            }
            JsonElement before = JsonParser.parseString(contents);
            boolean asset = source.file().startsWith(RDPLPack.ASSETS + "/");
            JsonElement json = !lined ? before.deepCopy() : asset ? LinePort.asset(before.deepCopy(), path, said(source)) : LinePort.data(before.deepCopy(), singular(path), said(source));
            if (json.isJsonObject() && (!asset || source.kind() == Port.Kind.MODEL)) { ported(json.getAsJsonObject(), source, path); }
            if (json.equals(before)) { return original; }
            rewritten++;
            return Ported.GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
        }
        catch (RuntimeException failed) {
            note("'" + source.file() + "' could not be ported and is served as written: " + failed);
            return original;
        }
    }

    private void ported(JsonObject held, Source source, String path) {
        if (down) { downgrade(held, source, path); }
        if (to == null || source.kind() == Port.Kind.MODEL) { return; }
        switch (source.kind()) {
            case RECIPE -> CrossJson.recipe(held, source.file(), this);
            case LOOT -> CrossLoot.loot(held, source.file(), this);
            case ADVANCEMENT -> CrossJson.advancement(held, source.file(), this);
            default -> CrossJson.definition(held, CrossIds.registry(layout(path)), source.file(), this);
        }
    }

    private LineNote said(Source source) {
        return new LineNote() {
            @Override public void accept(String said) { note("'" + source.file() + "' " + said); }

            @Override @Nullable public JsonElement carried(String folder, String id) { return Crossed.this.carried(folder, id); }
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

    private void downgrade(JsonObject held, Source source, String path) {
        Down.data(held, source.kind(), layout(path), source.file(), this::note);
        Definition definition = source.kind() == Port.Kind.MODEL ? definitions.get(source.file()) : null;
        if (definition != null) { DownAssets.attach(held, definition.namespace(), definition.modelPath(), readJson(definition.real()), source.file(), this::note); }
    }

    private String layout(String path) { return to == Port.Line.V1_20 ? singular(path) : path; }

    private static String singular(String path) {
        String folder = Port.singular(path);
        return folder == null ? path : folder;
    }

    @Override public void report() {
        reported = true;
        ContentLog.LOGGER.info("Pack '{}' is written for {} and is read through the port to {}: {} file(s) moved to this version's names, ids and keys rewritten as they are read", name, from.title(), Port.Line.running().title(), moved);
        listNotes();
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
        Path metaFile = root.resolve(PackManager.PACK_META);
        JsonObject old = null;
        if (Files.isRegularFile(metaFile)) {
            old = readJson(metaFile);
            if (old == null) { note("its pack.mcmeta could not be read, so the one written here carries the pack format alone"); }
        }
        JsonObject pack = packOf(old);
        PackMeta.formats(pack, PackType.SERVER_DATA);
        pack.remove("supported_formats");
        pack.remove("min_format");
        pack.remove("max_format");
        writeMeta(out, prefix, pack);
        note("written into " + prefix + ", " + written + " file(s) " + Port.Line.running().title() + " reads differently; the rest is read from the root as it is");
    }

    @Override public void closing() { if (rewritten > 0) { ContentLog.LOGGER.info("Pack '{}': the port from {} rewrote {} file(s) while the pack was read", name, from.title(), rewritten); } }
}
