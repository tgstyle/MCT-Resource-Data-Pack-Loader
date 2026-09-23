package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.pack.RDPLPack;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

public final class Ported {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    static final String BLOCKS = "blocks";
    static final String ITEMS = "items";
    static final int MODERN_FLAT_FLOOR = -64;
    private static final String CONVERTED = "converted";
    private static final String[] ROOT_FILES = {"pack.png", "readme.txt", "readme.md", "README.md", "README.txt"};
    private static final List<String> VANILLA_DIMENSIONS = Collections.unmodifiableList(Arrays.asList("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
    private static final Set<String> SINGLE_STATE_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("ladder", "torch", "cane", "crop", "sapling", "door", "banner", "trapdoor", "fence_gate", "stairs", "vine")));
    private static final int FIRST_DIMENSION = 1000;
    private static final int DIMENSION_SPAN = 9000;
    private final String name;
    private final Path root;
    private final Set<String> namespaces = new LinkedHashSet<>();
    private final Map<String, Own> ownBlocks = new HashMap<>();
    private final Map<String, Own> ownItems = new HashMap<>();
    private final Map<String, Own> ownFiles = new HashMap<>();
    private final Set<String> ownFluids = new HashSet<>();
    private final Map<String, JsonObject> blockDefs = new LinkedHashMap<>();
    private final Map<String, JsonObject> itemDefs = new LinkedHashMap<>();
    private final Map<String, Integer> dimensionNumbers = new LinkedHashMap<>();
    private final Map<String, Integer> dimensionShifts = new HashMap<>();
    private final List<Integer> templateShifts = new ArrayList<>();
    private final Map<String, Map<String, Source>> exposed = new LinkedHashMap<>();
    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    private final Set<String> notes = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Set<String> missing = ConcurrentHashMap.newKeySet();
    private final Set<String> textures = new LinkedHashSet<>();
    private final Map<String, JsonObject> furnace = new LinkedHashMap<>();
    private volatile boolean reported;
    private int moved;
    private int converted;
    private int dropped;
    private int failed;

    public Ported(String name, Path root) {
        this.name = name;
        this.root = root;
    }

    static final class Own {
        final String namespace;
        final String folder;
        final String file;
        final String variant;
        final int meta;
        final String type;
        final int variants;

        Own(String namespace, String folder, String file, String variant, int meta, String type, int variants) {
            this.namespace = namespace;
            this.folder = folder;
            this.file = file;
            this.variant = variant;
            this.meta = meta;
            this.type = type;
            this.variants = variants;
        }

        String id() { return namespace + ":" + file; }

        boolean variantProperty() { return BLOCKS.equals(folder) && !SINGLE_STATE_TYPES.contains(type); }
    }

    private static final class Source {
        @Nullable final Path real;
        final Port.Kind kind;
        final String from;
        @Nullable final byte[] made;

        Source(@Nullable Path real, Port.Kind kind, String from, @Nullable byte[] made) {
            this.real = real;
            this.kind = kind;
            this.from = from;
            this.made = made;
        }
    }

    public Map<String, Set<String>> build(Map<String, List<String>> assets, Map<String, List<String>> data) {
        for (String namespace : data.keySet()) { learn(namespace, data.get(namespace)); }
        for (String namespace : data.keySet()) {
            if (!"minecraft".equals(namespace) && !"forge".equals(namespace) && !"c".equals(namespace)) { namespaces.add(namespace); }
        }
        for (String namespace : assets.keySet()) {
            if (!"minecraft".equals(namespace)) { namespaces.add(namespace); }
            for (String path : assets.get(namespace)) {
                if (path.startsWith("textures/")) {
                    textures.add(namespace + ":" + Port.texturePath(path));
                }
            }
        }
        List<JsonObject> itemTags = new ArrayList<>();
        List<String> tagIds = new ArrayList<>();
        Map<String, List<String>> functionTags = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> namespace : assets.entrySet()) {
            for (String path : namespace.getValue()) { exposeAsset(namespace.getKey(), path); }
        }
        for (Map.Entry<String, List<String>> namespace : data.entrySet()) {
            String ns = namespace.getKey();
            Path home = root.resolve(Port.DATA).resolve(ns);
            for (String path : namespace.getValue()) {
                Port.Mapped mapped = Port.data(path);
                String from = "data/" + ns + "/" + path;
                switch (mapped.kind) {
                    case DROPPED:
                        drop(from, mapped.why);
                        break;
                    case ITEM_TAG: {
                        JsonObject json = readJson(home.resolve(path));
                        if (json == null) {
                            fail(from, "it is not a JSON object");
                            break;
                        }
                        itemTags.add(json);
                        tagIds.add(ns + ":" + path.substring(path.indexOf('/', "tags/".length()) + 1, path.length() - ".json".length()));
                        break;
                    }
                    case FUNCTION_TAG: {
                        JsonObject json = readJson(home.resolve(path));
                        String tag = path.substring(path.lastIndexOf('/') + 1, path.length() - ".json".length());
                        if (json != null && json.has("values") && json.get("values").isJsonArray()) {
                            for (JsonElement value : json.getAsJsonArray("values")) {
                                String id = value.isJsonObject() && value.getAsJsonObject().has("id") ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();
                                functionTags.computeIfAbsent(ns + ":" + tag, k -> new ArrayList<>()).add(id);
                            }
                        }
                        break;
                    }
                    case RECIPE:
                        recipe(ns, path, mapped, home.resolve(path), from);
                        break;
                    default: {
                        String target = overrideTarget(mapped.path, from);
                        expose(ns, target, new Source(home.resolve(path), mapped.kind, from, null));
                        movedLine(from, ns, target);
                    }
                }
            }
        }
        oreDictionary(itemTags, tagIds);
        functionTags(functionTags);
        furnaceFile();
        Blockstates.generate(this);
        Map<String, Set<String>> out = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Source>> namespace : exposed.entrySet()) { out.put(namespace.getKey(), new LinkedHashSet<>(namespace.getValue().keySet())); }
        return out;
    }

    private void exposeAsset(String namespace, String path) {
        Port.Mapped mapped = Port.assets(path);
        Path real = root.resolve(RDPLPack.ASSETS).resolve(namespace).resolve(path);
        String from = "assets/" + namespace + "/" + path;
        switch (mapped.kind) {
            case DROPPED:
                drop(from, mapped.why);
                return;
            case BLOCKSTATE: {
                String file = path.substring("blockstates/".length(), path.length() - ".json".length());
                Own held = ownBlocks.get(namespace + ":" + file);
                if (held != null) {
                    drop(from, "it is the modern blockstate of the variant '" + file + "', and 1.12.2 reads one blockstate per file, so " + held.namespace + ":blockstates/" + held.file + ".json is generated in its place");
                    return;
                }
                break;
            }
            case MODEL: {
                if (path.startsWith("models/item/") && path.indexOf('/', "models/item/".length()) < 0) {
                    String variant = path.substring("models/item/".length(), path.length() - ".json".length());
                    Own held = ownItems.containsKey(namespace + ":" + variant) ? ownItems.get(namespace + ":" + variant) : ownBlocks.get(namespace + ":" + variant);
                    if (held != null) {
                        String nested = "models/item/" + held.file + "/" + variant + ".json";
                        expose(namespace, nested, new Source(real, Port.Kind.MODEL, from, null));
                        if (held.variants == 1) { expose(namespace, "models/item/" + held.file + ".json", new Source(real, Port.Kind.MODEL, from, null)); }
                        movedLine(from, namespace, nested);
                        return;
                    }
                }
                break;
            }
            default:
                break;
        }
        expose(namespace, mapped.path, new Source(real, mapped.kind, from, null));
        if (!mapped.path.equals(path)) { movedLine(from, namespace, mapped.path); }
    }

    private String overrideTarget(String path, String from) {
        String prefix = "overrides/minecraft/";
        if (!path.startsWith(prefix) || !path.endsWith(".json")) { return path; }
        String modern = "minecraft:" + path.substring(prefix.length(), path.length() - ".json".length());
        Ids.Legacy found = Ids.block(modern, Collections.emptyMap());
        if (found == null) { found = Ids.item(modern); }
        if (found == null || found.id.equals(modern)) { return path; }
        note("'" + from + "' overrides " + modern + ", which 1.12.2 holds as " + found.id + ":" + found.meta + ", and an override changes every variant of " + found.id);
        return prefix + found.id.substring(found.id.indexOf(':') + 1) + ".json";
    }

    private void recipe(String namespace, String path, Port.Mapped mapped, Path real, String from) {
        JsonObject json = readJson(real);
        String type = json != null && json.has("type") && json.get("type").isJsonPrimitive() ? Ids.namespaced(json.get("type").getAsString()) : "";
        if (json != null && ("minecraft:smelting".equals(type))) {
            JsonObject entry = ConvertRecipes.smelting(json, this);
            if (entry == null) { return; }
            furnace.put(namespace + ":" + path, entry);
            converted++;
            note("'" + from + "' is a smelting recipe, which 1.12.2 reads from the furnace folder, so it became an entry of " + mainNamespace() + ":furnace/" + CONVERTED + "_smelting.json");
            return;
        }
        if (json != null && ConvertRecipes.noTwinRecipe(type)) {
            drop(from, "the recipe type '" + type + "' has no twin on 1.12.2");
            return;
        }
        expose(namespace, mapped.path, new Source(real, Port.Kind.RECIPE, from, null));
        movedLine(from, namespace, mapped.path);
    }

    private void learn(String namespace, List<String> paths) {
        Path home = root.resolve(Port.DATA).resolve(namespace);
        for (String path : paths) {
            boolean blocks = path.startsWith(BLOCKS + "/") && path.endsWith(".json");
            boolean items = path.startsWith(ITEMS + "/") && path.endsWith(".json");
            if (blocks || items) {
                namespaces.add(namespace);
                JsonObject json = readJson(home.resolve(path));
                if (json == null) { continue; }
                String folder = blocks ? BLOCKS : ITEMS;
                String file = path.substring(folder.length() + 1, path.length() - ".json".length());
                String type = json.has("type") && json.get("type").isJsonPrimitive() ? json.get("type").getAsString().toLowerCase(Locale.ROOT) : "";
                (blocks ? blockDefs : itemDefs).put(namespace + ":" + file, json);
                JsonObject variants = json.has("variants") && json.get("variants").isJsonObject() ? json.getAsJsonObject("variants") : new JsonObject();
                int count = variants.size();
                int meta = 0;
                for (Map.Entry<String, JsonElement> variant : variants.entrySet()) {
                    int given = variant.getValue().isJsonObject() && variant.getValue().getAsJsonObject().has("meta") ? variant.getValue().getAsJsonObject().get("meta").getAsInt() : meta;
                    Own held = new Own(namespace, folder, file, variant.getKey(), given, type, count);
                    (blocks ? ownBlocks : ownItems).putIfAbsent(namespace + ":" + variant.getKey(), held);
                    if (meta == 0) { ownFiles.putIfAbsent(folder + ":" + namespace + ":" + file, held); }
                    meta++;
                }
            }
            else if (path.startsWith("fluids/") && path.endsWith(".json")) { ownFluids.add(namespace + ":" + path.substring("fluids/".length(), path.length() - ".json".length())); }
            else if (path.startsWith("dimensions/") && path.endsWith(".json")) { dimension(namespace, path, readJson(home.resolve(path))); }
            else if (path.startsWith("worldtemplates/") && path.endsWith(".json")) {
                JsonObject json = readJson(home.resolve(path));
                templateShifts.add(json != null && json.has("settings") && json.get("settings").isJsonObject() ? Convert.flatShift(json.getAsJsonObject("settings")) : 0);
            }
        }
    }

    private void dimension(String namespace, String path, @Nullable JsonObject json) {
        String id = namespace + ":" + path.substring("dimensions/".length(), path.length() - ".json".length());
        int number;
        if (json != null && json.has("id") && json.get("id").isJsonPrimitive() && json.getAsJsonPrimitive("id").isNumber()) { number = json.get("id").getAsInt(); }
        else {
            number = FIRST_DIMENSION + Math.floorMod(id.hashCode(), DIMENSION_SPAN);
            while (dimensionNumbers.containsValue(number)) { number = FIRST_DIMENSION + Math.floorMod(number - FIRST_DIMENSION + 1, DIMENSION_SPAN); }
            note("Dimension " + id + " has no number, and 1.12.2 names a dimension by one, so it is dimension " + number + " wherever the pack names it");
        }
        dimensionNumbers.put(id, number);
        JsonObject terrain = json != null && json.has("terrain") && json.get("terrain").isJsonObject() ? json.getAsJsonObject("terrain") : null;
        if (terrain != null && terrain.has("type") && terrain.get("type").isJsonPrimitive() && "flat".equalsIgnoreCase(terrain.get("type").getAsString().trim())) {
            int floor = terrain.has("minHeight") && terrain.get("minHeight").isJsonPrimitive() && terrain.getAsJsonPrimitive("minHeight").isNumber() ? terrain.get("minHeight").getAsInt() : MODERN_FLAT_FLOOR;
            dimensionShifts.put(id, -floor);
        }
    }

    private void oreDictionary(List<JsonObject> tags, List<String> ids) {
        if (tags.isEmpty()) { return; }
        Map<String, JsonObject> byId = new LinkedHashMap<>();
        for (int i = 0; i < tags.size(); i++) { byId.put(ids.get(i), tags.get(i)); }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonObject> tag : byId.entrySet()) {
            String ore = Ids.oreName(tag.getKey());
            if (ore == null) { ore = Ids.camel(tag.getKey().replace(':', '_').replace('/', '_'), false); }
            Set<String> values = new LinkedHashSet<>();
            expandTag(tag.getKey(), byId, values, new LinkedHashSet<>());
            JsonObject json = tag.getValue();
            if (json.has("replace") && json.get("replace").isJsonPrimitive() && json.get("replace").getAsBoolean()) {
                JsonArray all = new JsonArray();
                all.add("*");
                out.add("-" + ore, all);
            }
            if (json.has("remove") && json.get("remove").isJsonArray()) {
                JsonArray removed = new JsonArray();
                for (JsonElement value : json.getAsJsonArray("remove")) {
                    if (value.isJsonPrimitive() && !value.getAsString().startsWith("#")) { removed.add(Convert.itemString(value.getAsString(), this)); }
                }
                if (removed.size() > 0) { out.add("-" + ore, removed); }
            }
            JsonArray added = new JsonArray();
            for (String value : values) { added.add(value); }
            if (added.size() > 0) { out.add(ore, added); }
            note("The item tag " + tag.getKey() + " became the ore dictionary name '" + ore + "' in " + mainNamespace() + ":oredict/" + CONVERTED + "_tags.json");
        }
        if (out.size() == 0) { return; }
        made(mainNamespace(), "oredict/" + CONVERTED + "_tags.json", GSON.toJson(out), "data/*/tags/items");
        converted++;
    }

    private void expandTag(String id, Map<String, JsonObject> byId, Set<String> values, Set<String> seen) {
        if (!seen.add(id)) { return; }
        JsonObject json = byId.get(id);
        if (json == null || !json.has("values") || !json.get("values").isJsonArray()) { return; }
        for (JsonElement value : json.getAsJsonArray("values")) {
            String named = value.isJsonObject() && value.getAsJsonObject().has("id") ? value.getAsJsonObject().get("id").getAsString() : value.isJsonPrimitive() ? value.getAsString() : "";
            if (named.isEmpty()) { continue; }
            if (named.startsWith("#")) {
                String inner = Ids.namespaced(named.substring(1));
                if (byId.containsKey(inner)) { expandTag(inner, byId, values, seen); }
                else { note("The item tag " + id + " includes the tag " + inner + ", which this pack does not define, so its items are not carried into the ore dictionary"); }
                continue;
            }
            values.add(Convert.itemString(named, this));
        }
    }

    private void functionTags(Map<String, List<String>> tags) {
        for (Map.Entry<String, List<String>> tag : tags.entrySet()) {
            if ("minecraft:tick".equals(tag.getKey())) { tick(tag.getValue()); }
            else { note("The function tag " + tag.getKey() + " has no twin on 1.12.2, which only runs one gameLoopFunction, so " + String.join(", ", tag.getValue()) + " must be run another way"); }
        }
    }

    private void tick(List<String> functions) {
        String main = mainNamespace();
        String function = functions.get(0);
        if (functions.size() > 1) {
            StringBuilder lines = new StringBuilder();
            for (String each : functions) { lines.append("function ").append(each).append('\n'); }
            made(main, "functions/" + CONVERTED + "_tick.mcfunction", lines.toString(), "data/minecraft/tags/functions/tick.json");
            function = main + ":" + CONVERTED + "_tick";
        }
        JsonObject rules = new JsonObject();
        JsonObject overworld = new JsonObject();
        overworld.addProperty("gameLoopFunction", function);
        rules.add("0", overworld);
        made(main, "gamerules/" + CONVERTED + "_tick.json", GSON.toJson(rules), "data/minecraft/tags/functions/tick.json");
        converted++;
        note("The #minecraft:tick function tag became the overworld gameLoopFunction '" + function + "' in " + main + ":gamerules/" + CONVERTED + "_tick.json");
    }

    private void furnaceFile() {
        if (furnace.isEmpty()) { return; }
        JsonObject json = new JsonObject();
        JsonArray add = new JsonArray();
        for (JsonObject entry : furnace.values()) { add.add(entry); }
        json.add("add", add);
        made(mainNamespace(), "furnace/" + CONVERTED + "_smelting.json", GSON.toJson(json), "data/*/recipes");
    }

    void made(String namespace, String path, String contents, String from) { made(namespace, path, contents.getBytes(StandardCharsets.UTF_8), from); }

    void made(String namespace, String path, byte[] contents, String from) { exposed.computeIfAbsent(namespace, k -> new LinkedHashMap<>()).put(path, new Source(null, Port.Kind.RAW, from, contents)); }

    private void expose(String namespace, String path, Source source) { exposed.computeIfAbsent(namespace, k -> new LinkedHashMap<>()).putIfAbsent(path, source); }

    boolean exposes(String namespace, String path) { return exposed.containsKey(namespace) && exposed.get(namespace).containsKey(path); }

    boolean hasTexture(String namespace, String legacyPath) { return textures.contains(namespace + ":" + legacyPath); }

    Map<String, JsonObject> blockDefs() { return blockDefs; }

    Map<String, JsonObject> itemDefs() { return itemDefs; }

    private void movedLine(String from, String namespace, String path) {
        moved++;
        note("moved '" + from + "' to 'assets/" + namespace + "/" + path + "'");
    }

    private void drop(String from, String why) {
        dropped++;
        note("left out '" + from + "': " + why);
    }

    private void fail(String from, String why) {
        failed++;
        note("could not carry '" + from + "': " + why);
    }

    void note(String said) {
        if (notes.add(said) && reported) { ContentLog.LOGGER.info("Pack '{}': {}", name, said); }
    }

    void noTwin(String id, String where) {
        if (missing.add(id)) { note(where + " names " + id + ", which has no twin on 1.12.2, so it is left as written"); }
    }

    String mainNamespace() {
        for (String namespace : namespaces) {
            if (!"minecraft".equals(namespace)) { return namespace; }
        }
        return "minecraft";
    }

    boolean ownsNamespace(String namespace) { return namespaces.contains(namespace) && !"minecraft".equals(namespace); }

    @Nullable Own ownBlock(String id) {
        Own held = ownBlocks.get(id);
        return held != null ? held : ownFiles.get(BLOCKS + ":" + id);
    }

    boolean ownFluid(String id) { return ownFluids.contains(id); }

    @Nullable Own ownItem(String id) {
        Own held = ownItems.get(id);
        if (held != null) { return held; }
        held = ownFiles.get(ITEMS + ":" + id);
        return held != null ? held : ownBlock(id);
    }

    @Nullable Integer dimensionNumber(String named) {
        String id = Ids.namespaced(named.trim().toLowerCase(Locale.ROOT));
        int vanilla = VANILLA_DIMENSIONS.indexOf(id);
        if (vanilla >= 0) { return vanilla == 0 ? 0 : vanilla == 1 ? -1 : 1; }
        return dimensionNumbers.get(id);
    }

    int overworldShift() {
        if (templateShifts.isEmpty()) { return 0; }
        int first = templateShifts.get(0);
        for (int shift : templateShifts) {
            if (shift != first) { return 0; }
        }
        return first;
    }

    int shiftIn(String dimension) {
        String id = Ids.namespaced(dimension.trim());
        if ("minecraft:overworld".equals(id) || "0".equals(dimension.trim())) { return overworldShift(); }
        Integer held = dimensionShifts.get(id);
        return held == null ? 0 : held;
    }

    int dimensionShift(String id) {
        Integer held = dimensionShifts.get(id);
        return held == null ? 0 : held;
    }

    @Nullable public InputStream open(String namespace, String path) throws IOException {
        Map<String, Source> paths = exposed.get(namespace);
        Source source = paths == null ? null : paths.get(path);
        if (source == null) { return null; }
        if (source.made != null || source.real == null) { return new ByteArrayInputStream(source.made == null ? new byte[0] : source.made); }
        if (source.kind == Port.Kind.RAW) { return Files.newInputStream(source.real); }
        String key = namespace + "/" + path;
        byte[] held = cache.get(key);
        if (held == null) {
            held = convert(source, source.real, namespace, path);
            cache.put(key, held);
        }
        return new ByteArrayInputStream(held);
    }

    private byte[] convert(Source source, Path real, String namespace, String path) throws IOException {
        byte[] raw = Files.readAllBytes(real);
        String folder = path.indexOf('/') < 0 ? "" : path.substring(0, path.indexOf('/'));
        try {
            String out;
            switch (source.kind) {
                case STRUCTURE: return Structures.convert(raw, source.from, this);
                case LANG: out = ConvertAssets.lang(text(raw), this); break;
                case FUNCTION: out = Commands.function(text(raw), source.from, this); break;
                case MODEL: out = ConvertAssets.model(object(raw), this); break;
                case BLOCKSTATE: out = ConvertAssets.blockstate(object(raw)); break;
                case PIXELMAP: out = ConvertAssets.pixelMap(object(raw)); break;
                case RECIPE: out = ConvertRecipes.recipe(object(raw), source.from, this); break;
                case LOOT: out = ConvertLoot.loot(object(raw), source.from, this); break;
                case ADVANCEMENT: out = ConvertAdvancements.advancement(object(raw), source.from, this); break;
                case DEFINITION: out = Convert.definition(object(raw), folder, namespace, path.substring(folder.length() + 1, path.length() - ".json".length()), this); break;
                default: return raw;
            }
            converted++;
            return out.getBytes(StandardCharsets.UTF_8);
        }
        catch (RuntimeException broken) {
            fail(source.from, "it is served as written, since " + broken);
            return raw;
        }
    }

    private static String text(byte[] raw) { return new String(raw, StandardCharsets.UTF_8); }

    private static JsonObject object(byte[] raw) { return new JsonParser().parse(text(raw)).getAsJsonObject(); }

    @Nullable static JsonObject readJson(Path file) {
        try {
            JsonElement held = new JsonParser().parse(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
            return held.isJsonObject() ? held.getAsJsonObject() : null;
        }
        catch (IOException | RuntimeException unreadable) { return null; }
    }

    public void report() {
        reported = true;
        ContentLog.LOGGER.info("Pack '{}' is written for a modern Minecraft (1.20.1 or 1.21.1) and is read through the port to 1.12.2: {} file(s) moved, {} converted into new files, {} left out", name, moved, converted, dropped);
        List<String> said;
        synchronized (notes) { said = new ArrayList<>(notes); }
        for (String line : said) { ContentLog.LOGGER.info("  {}", line); }
    }

    public void closing() {
        if (failed > 0 || converted > 0) { ContentLog.LOGGER.info("Pack '{}': the port converted {} file(s) and could not carry {} while the pack was read", name, converted, failed); }
    }

    public void writeZip(Path target) throws IOException {
        int written = 0;
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(target)))) {
            Set<String> names = new LinkedHashSet<>();
            for (Map.Entry<String, Map<String, Source>> namespace : exposed.entrySet()) {
                for (String path : new TreeMap<>(namespace.getValue()).keySet()) {
                    String entry = RDPLPack.ASSETS + "/" + namespace.getKey() + "/" + path;
                    if (!names.add(entry)) { continue; }
                    try (InputStream in = open(namespace.getKey(), path)) {
                        if (in == null) { continue; }
                        out.putNextEntry(new ZipEntry(entry));
                        copy(in, out);
                        out.closeEntry();
                        written++;
                    }
                }
            }
            for (String rootFile : ROOT_FILES) {
                Path held = root.resolve(rootFile);
                if (Files.isRegularFile(held) && names.add(rootFile)) {
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
                        if (!names.add(entry)) { continue; }
                        out.putNextEntry(new ZipEntry(entry));
                        Files.copy(held, out);
                        out.closeEntry();
                    }
                }
            }
            JsonObject old = readJson(root.resolve("pack.mcmeta"));
            JsonObject pack = old != null && old.has("pack") && old.get("pack").isJsonObject() ? old.getAsJsonObject("pack") : new JsonObject();
            JsonObject meta = new JsonObject();
            JsonObject kept = new JsonObject();
            kept.addProperty("pack_format", Port.LEGACY_FORMAT);
            kept.add("description", pack.has("description") ? pack.get("description") : new JsonPrimitive(name));
            meta.add("pack", kept);
            out.putNextEntry(new ZipEntry("pack.mcmeta"));
            out.write(GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }
        note("written out as a 1.12.2 pack of " + written + " file(s)");
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        for (int read = in.read(buffer); read > 0; read = in.read(buffer)) { out.write(buffer, 0, read); }
    }
}
