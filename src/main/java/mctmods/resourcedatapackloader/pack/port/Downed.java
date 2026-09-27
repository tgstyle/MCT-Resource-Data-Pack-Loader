package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.JsonTree;
import mctmods.resourcedatapackloader.util.LineNote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

final class Downed {
    private static final String JSON = ".json";
    private static final int LAST_1_21_DATA_FORMAT = 48;
    private static final int LAST_1_21_RESOURCE_FORMAT = 34;
    private static final int LAST_26_2_DATA_FORMAT = 107;
    private static final int LAST_26_2_RESOURCE_FORMAT = 88;
    private final Path root;
    private final boolean lined;
    private final Map<String, String[]> definitions = new HashMap<>();

    private Downed(Path root, boolean lined) {
        this.root = root;
        this.lined = lined;
    }

    @Nullable static Downed of(Path root) {
        JsonObject meta = Ported.readJson(root.resolve("pack.mcmeta"));
        JsonObject pack = meta != null && meta.has("pack") && meta.get("pack").isJsonObject() ? meta.getAsJsonObject("pack") : new JsonObject();
        boolean data = Files.isDirectory(root.resolve(Port.DATA));
        int format = pack.has("pack_format") ? major(pack.get("pack_format")) : 0;
        if (!pack.has("min_format") && !pack.has("max_format") && format <= (data ? LAST_1_21_DATA_FORMAT : LAST_1_21_RESOURCE_FORMAT)) { return null; }
        int max = pack.has("max_format") ? major(pack.get("max_format")) : format;
        return new Downed(root, max > (data ? LAST_26_2_DATA_FORMAT : LAST_26_2_RESOURCE_FORMAT));
    }

    private static int major(JsonElement value) {
        JsonElement held = value.isJsonArray() && value.getAsJsonArray().size() > 0 ? value.getAsJsonArray().get(0) : value;
        return held.isJsonPrimitive() && held.getAsJsonPrimitive().isNumber() ? held.getAsInt() : 0;
    }

    String title() { return lined ? "26.3" : "26.x"; }

    String path(String path) { return lined ? LinePort.path(path) : path; }

    @Nullable String asset(Ported pack, String namespace, String written) {
        String path = lined ? LinePort.assetPath(written) : written;
        Path home = root.resolve(RDPLPack.ASSETS).resolve(namespace);
        String base = RDPLPack.ASSETS + "/" + namespace + "/";
        String alias = DownAssets.moved(path);
        if (alias != null && !Files.exists(home.resolve(alias))) { pack.alias(namespace, alias, home.resolve(written), base + written, null); }
        String modelPath = DownAssets.modelPath(path);
        if (modelPath == null) { return path; }
        if (Files.isRegularFile(home.resolve(modelPath))) {
            definitions.put(base + modelPath, new String[] {namespace, modelPath, written});
            return null;
        }
        JsonObject made = DownAssets.made(namespace, modelPath, Ported.readJson(home.resolve(written)), base + written, pack::note);
        if (made == null) { return path; }
        pack.alias(namespace, modelPath, home.resolve(written), base + written, Ported.GSON.toJson(made).getBytes(StandardCharsets.UTF_8));
        pack.note("'" + base + written + "' is an item definition, which 1.12.2 does not read, so it became the item model " + namespace + ":" + modelPath);
        return null;
    }

    @Nullable JsonObject json(Path real, String from, Ported pack) {
        try {
            JsonElement held = new JsonParser().parse(new String(read(Files.readAllBytes(real), from, pack), StandardCharsets.UTF_8));
            return held.isJsonObject() ? held.getAsJsonObject() : null;
        }
        catch (IOException | RuntimeException unreadable) { return null; }
    }

    byte[] read(byte[] raw, String from, Ported pack) {
        boolean asset = from.startsWith(RDPLPack.ASSETS + "/");
        String written = from.substring(from.indexOf('/', from.indexOf('/') + 1) + 1);
        String path = asset ? lined ? LinePort.assetPath(written) : written : path(written);
        Port.Kind kind = kind(path, asset);
        String contents = new String(raw, StandardCharsets.UTF_8);
        LineNote said = said(from, pack);
        try {
            if (kind == Port.Kind.FUNCTION) {
                String out = Down.function(lined ? LinePort.function(contents, said) : contents);
                return out.equals(contents) ? raw : out.getBytes(StandardCharsets.UTF_8);
            }
            if (!path.endsWith(JSON)) { return raw; }
            JsonElement before = new JsonParser().parse(contents);
            JsonElement json = !lined ? JsonTree.copy(before) : asset ? LinePort.asset(JsonTree.copy(before), path, said) : LinePort.data(JsonTree.copy(before), path, said);
            if (json.isJsonObject() && (!asset || kind == Port.Kind.MODEL)) {
                Down.data(json.getAsJsonObject(), kind, from, pack::note);
                String[] definition = kind == Port.Kind.MODEL ? definitions.get(from) : null;
                if (definition != null) { DownAssets.attach(json.getAsJsonObject(), definition[0], definition[1], Ported.readJson(root.resolve(RDPLPack.ASSETS).resolve(definition[0]).resolve(definition[2])), from, pack::note); }
            }
            return json.equals(before) ? raw : Ported.GSON.toJson(json).getBytes(StandardCharsets.UTF_8);
        }
        catch (RuntimeException failed) {
            pack.note("'" + from + "' could not be ported and is served as written: " + failed);
            return raw;
        }
    }

    private static Port.Kind kind(String path, boolean asset) {
        if (asset) { return path.startsWith("models/") && path.endsWith(JSON) ? Port.Kind.MODEL : Port.Kind.DEFINITION; }
        if (path.startsWith("function/")) { return path.endsWith(".mcfunction") ? Port.Kind.FUNCTION : Port.Kind.RAW; }
        if (path.startsWith("recipe/")) { return Port.Kind.RECIPE; }
        if (path.startsWith("loot_table/") || path.startsWith("predicate/") || path.startsWith("item_modifier/") || path.startsWith("loot_modifiers/")) { return Port.Kind.LOOT; }
        return path.startsWith("advancement/") ? Port.Kind.ADVANCEMENT : Port.Kind.DEFINITION;
    }

    private LineNote said(String from, Ported pack) {
        return new LineNote() {
            @Override public void accept(String said) { pack.note("'" + from + "' " + said); }

            @Override @Nullable public JsonElement carried(String folder, String id) {
                int colon = id.indexOf(':');
                Path file = root.resolve(Port.DATA).resolve(colon < 0 ? "minecraft" : id.substring(0, colon)).resolve(folder).resolve(id.substring(colon + 1) + JSON);
                if (!Files.isRegularFile(file)) { return null; }
                try { return new JsonParser().parse(new String(Files.readAllBytes(file), StandardCharsets.UTF_8)); }
                catch (IOException | RuntimeException unreadable) { return null; }
            }
        };
    }
}
