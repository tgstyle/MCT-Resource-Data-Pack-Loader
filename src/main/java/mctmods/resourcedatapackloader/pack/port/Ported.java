package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

public final class Ported {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Set<String> PRIMARY_TEXTURES = Set.of("all", "cross", "texture", "side", "pane", "torch", "crop", "particle", "layer0", "wall", "top", "end");
    private final String name;
    private final Path root;
    private final Set<String> namespaces = new LinkedHashSet<>();
    private final Map<String, Variants> blockFiles = new LinkedHashMap<>();
    private final Map<String, Variants> itemFiles = new LinkedHashMap<>();
    private final Map<String, String> blockVariants = new LinkedHashMap<>();
    private final Map<String, String> itemVariants = new LinkedHashMap<>();
    private final Map<PackType, Map<String, Map<String, Source>>> exposed = new EnumMap<>(PackType.class);
    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    private final Set<String> notes = new LinkedHashSet<>();
    private final Set<String> taken = new LinkedHashSet<>();
    private final Map<String, Map<String, String>> renamed = new LinkedHashMap<>();
    private boolean reported;
    private int moved;
    private int rewritten;
    private int dropped;

    public Ported(String name, Path root) {
        this.name = name;
        this.root = root;
    }

    private record Variants(String main, Map<Integer, String> byMeta) {}

    private record Source(Path real, Port.Kind kind, @Nullable String extra) {}

    public String mainNamespace() { return namespaces.isEmpty() ? "minecraft" : namespaces.iterator().next(); }

    public boolean ownsNamespace(String namespace) { return !"minecraft".equals(namespace) && namespaces.contains(namespace); }

    public boolean owns(String id) {
        int colon = id.indexOf(':');
        return colon > 0 && ownsNamespace(id.substring(0, colon));
    }

    @Nullable public String ownBlock(String id, int meta) { return own(blockFiles, blockVariants, id, meta); }

    @Nullable public String ownItem(String id, int meta) { return own(itemFiles, itemVariants, id, meta); }

    @Nullable private static String own(Map<String, Variants> files, Map<String, String> variants, String id, int meta) {
        Variants held = files.get(id);
        if (held != null) {
            String named = held.byMeta().get(meta);
            if (named == null && held.byMeta().size() == 1) { named = held.byMeta().values().iterator().next(); }
            if (named == null) { named = held.main(); }
            return id.substring(0, id.indexOf(':')) + ":" + named;
        }
        return variants.containsKey(id) ? id : null;
    }

    @Nullable public String mainVariantOfBlockFile(String namespace, String file) {
        Variants held = blockFiles.get(namespace + ":" + file);
        return held == null ? null : held.main();
    }

    @Nullable public String mainVariantOfItemFile(String namespace, String file) {
        Variants held = itemFiles.get(namespace + ":" + file);
        return held == null ? null : held.main();
    }

    public void rewrote() { rewritten++; }

    public void note(String said) {
        if (notes.add(said) && reported) { ContentLog.LOGGER.info("Pack '{}': {}", name, said); }
    }

    public Map<String, String> renamedIn(String namespace, String folder, String file) { return renamed.getOrDefault(namespace + ":" + folder + "/" + file, Map.of()); }

    public void index(String namespace, List<String> realPaths) {
        namespaces.add(namespace);
        Path home = root.resolve("assets").resolve(namespace);
        for (String path : realPaths) {
            if (path.startsWith("blocks/") && path.endsWith(".json")) { learn(home, namespace, path, "blocks", blockFiles, blockVariants); }
            else if (path.startsWith("items/") && path.endsWith(".json")) { learn(home, namespace, path, "items", itemFiles, itemVariants); }
        }
        for (String path : realPaths) {
            Port.Mapped mapped = Port.map(path);
            Path real = home.resolve(path);
            switch (mapped.kind()) {
                case DROPPED -> {
                    dropped++;
                    note("'" + path + "' has no twin on this version and is left out");
                }
                case OREDICT -> expandOreDict(real, path);
                case BLOCKSTATE -> aliases(home, namespace, real, path, realPaths);
                default -> {
                    if (mapped.type() == PackType.SERVER_DATA) { moved++; }
                    expose(mapped.type(), namespace, overrideTarget(mapped.path()), new Source(real, mapped.kind(), null));
                }
            }
        }
    }

    private String overrideTarget(String path) {
        if (!path.startsWith("overrides/") || !path.endsWith(".json")) { return path; }
        String[] parts = path.substring("overrides/".length(), path.length() - ".json".length()).split("/", 2);
        if (parts.length != 2 || !"minecraft".equals(parts[0])) { return path; }
        String target = "minecraft:" + parts[1];
        String block = Ids.block(target, 0).name();
        String mapped = block.equals(target) ? Ids.item(target, 0) : block;
        if (mapped.equals(target)) { return path; }
        note("'" + path + "' changes " + target + ", which is " + mapped + " now, so it is read as overrides/" + mapped.replace(':', '/') + ".json");
        return "overrides/" + mapped.replace(':', '/') + ".json";
    }

    private void learn(Path home, String namespace, String path, String folder, Map<String, Variants> files, Map<String, String> variants) {
        String file = path.substring(folder.length() + 1, path.length() - ".json".length());
        JsonObject json = readJson(home.resolve(path));
        if (json == null || !json.has("variants") || !json.get("variants").isJsonObject()) { return; }
        Map<Integer, String> byMeta = new LinkedHashMap<>();
        String main = null;
        for (Map.Entry<String, JsonElement> variant : json.getAsJsonObject("variants").entrySet()) {
            int meta = variant.getValue().isJsonObject() && variant.getValue().getAsJsonObject().has("meta") ? variant.getValue().getAsJsonObject().get("meta").getAsInt() : byMeta.size();
            String named = variant.getKey();
            if (!taken.add(namespace + ":" + named)) {
                String plain = file.substring(file.lastIndexOf('/') + 1);
                named = plain + "_" + named;
                taken.add(namespace + ":" + named);
                renamed.computeIfAbsent(namespace + ":" + folder + "/" + file, k -> new LinkedHashMap<>()).put(variant.getKey(), named);
                note("'" + path + "' names the variant '" + variant.getKey() + "', which another file already claims, so it is registered as '" + named + "'");
            }
            byMeta.putIfAbsent(meta, named);
            if (main == null || meta == 0) { main = named; }
            variants.put(namespace + ":" + named, file);
        }
        if (main != null) { files.put(namespace + ":" + file, new Variants(main, byMeta)); }
    }

    private void expose(PackType type, String namespace, String path, Source source) {
        exposed.computeIfAbsent(type, k -> new LinkedHashMap<>()).computeIfAbsent(namespace, k -> new LinkedHashMap<>()).putIfAbsent(path, source);
    }

    private void expandOreDict(Path real, String path) {
        JsonObject json = readJson(real);
        if (json == null) { return; }
        for (Map.Entry<String, JsonObject> tag : Convert.oreDict(json, this).entrySet()) {
            String id = tag.getKey();
            String namespace = id.substring(0, id.indexOf(':'));
            String tagPath = ContentFormats.ITEM_TAGS + "/" + id.substring(id.indexOf(':') + 1) + ".json";
            cache.put(key(PackType.SERVER_DATA, namespace, tagPath), GSON.toJson(tag.getValue()).getBytes(StandardCharsets.UTF_8));
            expose(PackType.SERVER_DATA, namespace, tagPath, new Source(real, Port.Kind.OREDICT, id));
            moved++;
        }
        note("'" + path + "' became " + Convert.oreDict(json, this).size() + " item tag file(s)");
    }

    private void aliases(Path home, String namespace, Path real, String path, List<String> realPaths) {
        JsonObject json = readJson(real);
        String file = path.substring("blockstates/".length(), path.length() - ".json".length());
        Variants held = blockFiles.get(namespace + ":" + file);
        if (json == null || held == null) { return; }
        JsonObject defaults = json.has("defaults") && json.get("defaults").isJsonObject() ? json.getAsJsonObject("defaults") : new JsonObject();
        JsonObject variants = json.has("variants") && json.get("variants").isJsonObject() ? json.getAsJsonObject("variants") : new JsonObject();
        JsonObject blocks = variants.has("blocks") && variants.get("blocks").isJsonObject() ? variants.getAsJsonObject("blocks") : new JsonObject();
        int made = 0;
        for (String variant : new LinkedHashSet<>(held.byMeta().values())) {
            Map<String, String> textures = new LinkedHashMap<>();
            gather(defaults, textures);
            if (blocks.has(variant) && blocks.get(variant).isJsonObject()) { gather(blocks.getAsJsonObject(variant), textures); }
            if (!json.has("forge_marker") && variants.has("normal")) { gatherModel(home, variants.get("normal"), textures); }
            String primary = null;
            for (String candidate : PRIMARY_TEXTURES) {
                if (textures.containsKey(candidate)) {
                    primary = textures.get(candidate);
                    break;
                }
            }
            if (primary != null && alias(namespace, realPaths, home, "textures/block/" + variant, primary)) { made++; }
            String top = textures.getOrDefault("end", textures.get("top"));
            if (top != null && alias(namespace, realPaths, home, "textures/block/" + variant + "_top", top)) { made++; }
            if (textures.containsKey("bottom") && alias(namespace, realPaths, home, "textures/block/" + variant + "_bottom", textures.get("bottom"))) { made++; }
            JsonObject inventory = variants.has("inventory") && variants.get("inventory").isJsonArray() && !variants.getAsJsonArray("inventory").isEmpty() && variants.getAsJsonArray("inventory").get(0).isJsonObject() ? variants.getAsJsonArray("inventory").get(0).getAsJsonObject() : null;
            if (inventory != null && inventory.has("textures") && inventory.get("textures").isJsonObject() && inventory.getAsJsonObject("textures").has("layer0")) {
                if (alias(namespace, realPaths, home, "textures/item/" + variant, inventory.getAsJsonObject("textures").get("layer0").getAsString())) { made++; }
            }
        }
        dropped++;
        note("'" + path + "' is a 1.12.2 blockstate: it is not served, " + made + " texture name(s) were taken from it and the blockstate and models are generated");
    }

    private void gatherModel(Path home, JsonElement normal, Map<String, String> textures) {
        JsonObject entry = normal.isJsonArray() && !normal.getAsJsonArray().isEmpty() ? normal.getAsJsonArray().get(0).getAsJsonObject() : normal.isJsonObject() ? normal.getAsJsonObject() : null;
        if (entry == null || !entry.has("model")) { return; }
        String model = entry.get("model").getAsString();
        String rest = model.indexOf(':') < 0 ? model : model.substring(model.indexOf(':') + 1);
        JsonObject json = readJson(home.resolve("models/block/" + rest + ".json"));
        if (json != null) { gather(json, textures); }
    }

    private static void gather(JsonObject holder, Map<String, String> textures) {
        if (!holder.has("textures") || !holder.get("textures").isJsonObject()) { return; }
        for (Map.Entry<String, JsonElement> texture : holder.getAsJsonObject("textures").entrySet()) {
            if (texture.getValue().isJsonPrimitive() && !texture.getValue().getAsString().startsWith("#")) { textures.put(texture.getKey(), texture.getValue().getAsString()); }
        }
    }

    private boolean alias(String namespace, List<String> realPaths, Path home, String exposedPath, String texture) {
        String textureNamespace = texture.indexOf(':') < 0 ? "minecraft" : texture.substring(0, texture.indexOf(':'));
        if (!textureNamespace.equals(namespace)) { return false; }
        String rest = texture.substring(texture.indexOf(':') + 1);
        String legacy = "textures/" + rest;
        String png = legacy + ".png";
        String map = legacy + ".png.json";
        if (realPaths.contains(png)) {
            expose(PackType.CLIENT_RESOURCES, namespace, exposedPath + ".png", new Source(home.resolve(png), Port.Kind.RAW, null));
            return true;
        }
        if (realPaths.contains(map)) {
            expose(PackType.CLIENT_RESOURCES, namespace, exposedPath + ".png.json", new Source(home.resolve(map), Port.Kind.PIXELMAP, null));
            return true;
        }
        return false;
    }

    public Map<PackType, Map<String, Set<String>>> exposed() {
        Map<PackType, Map<String, Set<String>>> out = new EnumMap<>(PackType.class);
        for (Map.Entry<PackType, Map<String, Map<String, Source>>> type : exposed.entrySet()) {
            Map<String, Set<String>> namespacesOut = new LinkedHashMap<>();
            for (Map.Entry<String, Map<String, Source>> namespace : type.getValue().entrySet()) { namespacesOut.put(namespace.getKey(), new LinkedHashSet<>(namespace.getValue().keySet())); }
            out.put(type.getKey(), namespacesOut);
        }
        return out;
    }

    @Nullable public InputStream open(PackType type, String namespace, String path) throws IOException {
        Source source = exposed.getOrDefault(type, Map.of()).getOrDefault(namespace, Map.of()).get(path);
        if (source == null) { return null; }
        if (source.kind() == Port.Kind.RAW) { return Files.newInputStream(source.real()); }
        String key = key(type, namespace, path);
        byte[] held = cache.get(key);
        if (held == null) {
            held = convert(source, namespace, path);
            cache.put(key, held);
        }
        return new ByteArrayInputStream(held);
    }

    private byte[] convert(Source source, String namespace, String path) throws IOException {
        String contents = Files.readString(source.real(), StandardCharsets.UTF_8);
        try {
            String out = switch (source.kind()) {
                case LANG -> Convert.lang(contents, this);
                case FUNCTION -> {
                    note("'" + path + "' is a 1.12.2 function and is served as written; commands that changed since need rewriting by hand");
                    yield contents;
                }
                case DEFINITION -> Convert.definition(JsonParser.parseString(contents).getAsJsonObject(), path.substring(0, path.indexOf('/')), namespace, path.substring(path.indexOf('/') + 1, path.length() - ".json".length()), this);
                case MODEL -> Convert.model(JsonParser.parseString(contents).getAsJsonObject(), this);
                case PIXELMAP -> Convert.pixelMap(JsonParser.parseString(contents).getAsJsonObject(), this);
                case RECIPE -> Convert.recipe(JsonParser.parseString(contents).getAsJsonObject(), this);
                case LOOT -> Convert.loot(JsonParser.parseString(contents).getAsJsonObject(), this);
                case ADVANCEMENT -> Convert.advancement(JsonParser.parseString(contents).getAsJsonObject(), this);
                default -> contents;
            };
            return out.getBytes(StandardCharsets.UTF_8);
        }
        catch (RuntimeException failed) {
            note("'" + path + "' could not be ported and is served as written: " + failed);
            return contents.getBytes(StandardCharsets.UTF_8);
        }
    }

    private static String key(PackType type, String namespace, String path) { return type.getDirectory() + "/" + namespace + "/" + path; }

    @Nullable private static JsonObject readJson(Path file) {
        try {
            JsonElement held = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            return held.isJsonObject() ? held.getAsJsonObject() : null;
        }
        catch (IOException | RuntimeException failed) { return null; }
    }

    public void report() {
        reported = true;
        ContentLog.LOGGER.info("Pack '{}' is written for 1.12.2 and is read through the forward port: {} file(s) moved under data, {} left out, ids and keys rewritten as they are read. The pack is not changed on disk", name, moved, dropped);
        List<String> said = new ArrayList<>(notes);
        for (int i = 0; i < Math.min(said.size(), 40); i++) { ContentLog.LOGGER.info("  {}", said.get(i)); }
        if (said.size() > 40) { ContentLog.LOGGER.info("  ... and {} more", said.size() - 40); }
    }

    public void writeZip(Path target, Path root) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(target)))) {
            Set<String> written = new LinkedHashSet<>();
            for (Map.Entry<PackType, Map<String, Map<String, Source>>> type : exposed.entrySet()) {
                for (Map.Entry<String, Map<String, Source>> namespace : type.getValue().entrySet()) {
                    for (String path : namespace.getValue().keySet()) {
                        String entry = type.getKey().getDirectory() + "/" + namespace.getKey() + "/" + path;
                        if (!written.add(entry)) { continue; }
                        try (InputStream in = open(type.getKey(), namespace.getKey(), path)) {
                            if (in == null) { continue; }
                            out.putNextEntry(new ZipEntry(entry));
                            in.transferTo(out);
                            out.closeEntry();
                        }
                    }
                }
            }
            for (String rootFile : new String[] {"pack.png", "readme.txt", "readme.md", "README.md", "README.txt"}) {
                Path held = root.resolve(rootFile);
                if (Files.isRegularFile(held) && written.add(rootFile)) {
                    out.putNextEntry(new ZipEntry(rootFile));
                    Files.copy(held, out);
                    out.closeEntry();
                }
            }
            Path config = root.resolve("config");
            if (Files.isDirectory(config)) {
                try (Stream<Path> files = Files.walk(config)) {
                    for (Path held : (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                        String entry = root.relativize(held).toString().replace('\\', '/');
                        if (!written.add(entry)) { continue; }
                        out.putNextEntry(new ZipEntry(entry));
                        Files.copy(held, out);
                        out.closeEntry();
                    }
                }
            }
            JsonObject meta = new JsonObject();
            Path metaFile = root.resolve("pack.mcmeta");
            JsonObject old = Files.isRegularFile(metaFile) ? readJson(metaFile) : null;
            JsonObject pack = old != null && old.has("pack") && old.get("pack").isJsonObject() ? old.getAsJsonObject("pack") : new JsonObject();
            pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
            if (!pack.has("description")) { pack.addProperty("description", name); }
            meta.add("pack", pack);
            out.putNextEntry(new ZipEntry("pack.mcmeta"));
            out.write(GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
            note("written out as a pack of this version, " + written.size() + " file(s)");
        }
    }

    public void closing() {
        if (rewritten > 0) { ContentLog.LOGGER.info("Pack '{}': the forward port rewrote {} id(s) and key(s) while the pack was read", name, rewritten); }
    }
}
