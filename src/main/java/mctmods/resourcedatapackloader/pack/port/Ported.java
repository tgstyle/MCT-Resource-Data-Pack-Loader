package mctmods.resourcedatapackloader.pack.port;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;

public final class Ported implements PackPort {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final List<String> PRIMARY_TEXTURES = List.of("all", "cross", "texture", "side", "pane", "torch", "crop", "particle", "layer0", "wall", "top", "end");
    private static final String WALL = "_wall";
    private static final List<String> ROTATIONS = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
    private static final List<String> FACINGS = List.of("north", "south", "west", "east");
    private final String name;
    private final Path root;
    private final Set<String> namespaces = new LinkedHashSet<>();
    private final Map<String, Variants> blockFiles = new LinkedHashMap<>();
    private final Map<String, Variants> itemFiles = new LinkedHashMap<>();
    private final Map<String, String> blockVariants = new LinkedHashMap<>();
    private final Map<String, String> itemVariants = new LinkedHashMap<>();
    private final Map<String, String> banners = new LinkedHashMap<>();
    private final Map<PackType, Map<String, Map<String, Source>>> exposed = new EnumMap<>(PackType.class);
    private final Map<String, byte[]> cache = new ConcurrentHashMap<>();
    private final Set<String> notes = new LinkedHashSet<>();
    private final Set<String> taken = new LinkedHashSet<>();
    private final Map<String, Map<String, String>> renamed = new LinkedHashMap<>();
    private final Set<String> ticking = new LinkedHashSet<>();
    private final Map<String, String> dimensionIds = new LinkedHashMap<>();
    private final Map<String, Integer> dimensionFloors = new LinkedHashMap<>();
    private final List<Integer> templateFloors = new ArrayList<>();
    private final Set<String> plots = new LinkedHashSet<>();
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

    private record Banner(String name, String property, List<String> values, String layer) {}

    @Override public String origin() { return "1.12.2"; }

    @Override public PackType reads() { return PackType.CLIENT_RESOURCES; }

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

    public List<String> ownItems(String id) { return variantNames(itemFiles.containsKey(id) ? itemFiles.get(id) : blockFiles.get(id), id); }

    public List<String> ownBlocks(String id) { return variantNames(blockFiles.containsKey(id) ? blockFiles.get(id) : itemFiles.get(id), id); }

    private static List<String> variantNames(@Nullable Variants held, String id) {
        if (held == null) { return List.of(id); }
        Set<String> names = new LinkedHashSet<>();
        for (String named : held.byMeta().values()) { names.add(id.substring(0, id.indexOf(':')) + ":" + named); }
        return List.copyOf(names);
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

    @Override public void index(String namespace, List<String> realPaths) {
        namespaces.add(namespace);
        Path home = root.resolve("assets").resolve(namespace);
        for (String path : realPaths) {
            if (path.startsWith("blocks/") && path.endsWith(".json")) { learn(home, namespace, path, "blocks", blockFiles, blockVariants); }
            else if (path.startsWith("items/") && path.endsWith(".json")) { learn(home, namespace, path, "items", itemFiles, itemVariants); }
            else if (path.startsWith("dimensions/") && path.endsWith(".json")) {
                dimensionId(home, namespace, path);
                dimensionFloor(home, namespace, path);
            }
            else if (path.startsWith("worldtemplates/") && path.endsWith(".json")) { templateFloor(home, path); }
            else if (path.startsWith("villages/") && path.endsWith(".json")) { plots.add(namespace + ":" + path.substring("villages/".length(), path.length() - ".json".length())); }
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
                case BLOCKSTATE -> {
                    Banner banner = banner(namespace, path);
                    if (banner != null) { bannerState(namespace, real, path, banner); }
                    else { aliases(home, namespace, real, path, realPaths); }
                }
                default -> {
                    if (generatedModel(namespace, path, realPaths)) {
                        dropped++;
                        note("'" + path + "' shares its name with a block whose models are generated, so it is left out and the generated model is used");
                        continue;
                    }
                    if (path.startsWith("gamerules/") && path.endsWith(".json")) { gameLoop(real, path); }
                    if (mapped.type() == PackType.SERVER_DATA) { moved++; }
                    for (String exposedPath : overrideTargets(mapped.path())) { expose(mapped.type(), namespace, exposedPath, new Source(real, mapped.kind(), null)); }
                }
            }
        }
    }

    private boolean generatedModel(String namespace, String path, List<String> realPaths) {
        if (!path.startsWith("models/block/") || !path.endsWith(".json")) { return false; }
        String name = path.substring("models/block/".length(), path.length() - ".json".length());
        String file = blockVariants.get(namespace + ":" + name);
        if (file == null) { return false; }
        return !banners.containsKey(namespace + ":" + file) || !realPaths.contains("blockstates/" + file + ".json");
    }

    @Nullable private Banner banner(String namespace, String path) {
        String file = path.substring("blockstates/".length(), path.length() - ".json".length());
        boolean wall = file.endsWith(WALL) && !banners.containsKey(namespace + ":" + file);
        String standing = wall ? file.substring(0, file.length() - WALL.length()) : file;
        String layer = banners.get(namespace + ":" + standing);
        Variants held = blockFiles.get(namespace + ":" + standing);
        if (layer == null || held == null) { return null; }
        return wall ? new Banner(held.main() + WALL, "facing", FACINGS, layer) : new Banner(held.main(), "rotation", ROTATIONS, layer);
    }

    private void bannerState(String namespace, Path real, String path, Banner banner) {
        JsonObject json = readJson(real);
        if (json == null) { return; }
        JsonObject defaults = json.has("defaults") && json.get("defaults").isJsonObject() ? json.getAsJsonObject("defaults") : new JsonObject();
        JsonObject variants = json.has("variants") && json.get("variants").isJsonObject() ? json.getAsJsonObject("variants") : new JsonObject();
        JsonObject byValue = variants.has(banner.property()) && variants.get(banner.property()).isJsonObject() ? variants.getAsJsonObject(banner.property()) : null;
        JsonObject states = new JsonObject();
        for (String value : banner.values()) {
            JsonObject entry = merged(defaults, firstEntry(byValue != null ? byValue.get(value) : variants.get(banner.property() + "=" + value)));
            if (!entry.has("model") || !entry.get("model").isJsonPrimitive()) { continue; }
            String model = entry.get("model").getAsString();
            JsonObject child = new JsonObject();
            child.addProperty("parent", (model.indexOf(':') < 0 ? "minecraft" : model.substring(0, model.indexOf(':'))) + ":block/" + model.substring(model.indexOf(':') + 1));
            if (entry.has("textures") && entry.get("textures").isJsonObject()) {
                JsonObject textures = new JsonObject();
                for (Map.Entry<String, JsonElement> texture : entry.getAsJsonObject("textures").entrySet()) {
                    String held = texture.getValue().isJsonPrimitive() ? texture.getValue().getAsString() : null;
                    if (held != null) { textures.addProperty(texture.getKey(), held.startsWith("#") ? held : ConvertAssets.texturePath(held)); }
                }
                child.add("textures", textures);
            }
            if (entry.has("transform") && entry.get("transform").isJsonObject()) {
                JsonObject transform = entry.getAsJsonObject("transform").deepCopy();
                if (!transform.has("origin")) { transform.addProperty("origin", "center"); }
                child.add("transform", transform);
            }
            if (!banner.layer().isEmpty() && !"solid".equals(banner.layer())) { child.addProperty("render_type", "minecraft:" + banner.layer()); }
            String childName = banner.name() + "_" + banner.property() + "_" + value;
            serve(namespace, "models/block/" + childName + ".json", real, child);
            JsonObject state = new JsonObject();
            state.addProperty("model", namespace + ":block/" + childName);
            for (String turn : new String[] {"x", "y", "uvlock"}) {
                if (entry.has(turn)) { state.add(turn, entry.get(turn)); }
            }
            states.add(banner.property() + "=" + value, state);
        }
        if (states.size() == 0) {
            dropped++;
            note("'" + path + "' names no model for any " + banner.property() + ", so it is left out and the banner is drawn from its texture");
            return;
        }
        JsonObject out = new JsonObject();
        out.add("variants", states);
        serve(namespace, "blockstates/" + banner.name() + ".json", real, out);
        note("'" + path + "' is a 1.12.2 banner blockstate: it became blockstates/" + banner.name() + ".json and " + states.size() + " model(s) turning the pack's own banner model");
    }

    private static JsonObject merged(JsonObject defaults, @Nullable JsonObject variant) {
        JsonObject out = defaults.deepCopy();
        if (variant == null) { return out; }
        for (Map.Entry<String, JsonElement> field : variant.entrySet()) {
            if ("textures".equals(field.getKey()) && field.getValue().isJsonObject() && out.has("textures") && out.get("textures").isJsonObject()) {
                for (Map.Entry<String, JsonElement> texture : field.getValue().getAsJsonObject().entrySet()) { out.getAsJsonObject("textures").add(texture.getKey(), texture.getValue()); }
            }
            else { out.add(field.getKey(), field.getValue().deepCopy()); }
        }
        return out;
    }

    @Nullable private static JsonObject firstEntry(@Nullable JsonElement held) {
        if (held == null) { return null; }
        if (held.isJsonArray()) { return !held.getAsJsonArray().isEmpty() && held.getAsJsonArray().get(0).isJsonObject() ? held.getAsJsonArray().get(0).getAsJsonObject() : null; }
        return held.isJsonObject() ? held.getAsJsonObject() : null;
    }

    private void serve(String namespace, String path, Path real, JsonObject json) {
        cache.put(key(PackType.CLIENT_RESOURCES, namespace, path), GSON.toJson(json).getBytes(StandardCharsets.UTF_8));
        expose(PackType.CLIENT_RESOURCES, namespace, path, new Source(real, Port.Kind.BLOCKSTATE, null));
    }

    private List<String> overrideTargets(String path) {
        if (!path.startsWith("overrides/") || !path.endsWith(".json")) { return List.of(path); }
        String[] parts = path.substring("overrides/".length(), path.length() - ".json".length()).split("/", 2);
        if (parts.length != 2 || !"minecraft".equals(parts[0])) { return List.of(path); }
        String target = "minecraft:" + parts[1];
        List<String> blocks = Ids.blocks(target);
        List<String> mapped = blocks.equals(List.of(target)) ? Ids.items(target) : blocks;
        if (mapped.equals(List.of(target))) { return List.of(path); }
        List<String> paths = new ArrayList<>();
        for (String name : mapped) { paths.add("overrides/" + name.replace(':', '/') + ".json"); }
        note("'" + path + "' changes " + target + ", which is " + String.join(", ", mapped) + " now, so it is read as the override of each");
        return paths;
    }

    private void dimensionId(Path home, String namespace, String path) {
        JsonObject json = readJson(home.resolve(path));
        if (json == null || !json.has("id") || !json.get("id").isJsonPrimitive() || !json.getAsJsonPrimitive("id").isNumber()) { return; }
        String named = namespace + ":" + path.substring("dimensions/".length(), path.length() - ".json".length());
        dimensionIds.put(String.valueOf(json.get("id").getAsInt()), named);
        note("'" + path + "' was dimension " + json.get("id").getAsInt() + ", so that number is read as " + named + " wherever the pack names it");
    }

    private void dimensionFloor(Path home, String namespace, String path) {
        JsonObject json = readJson(home.resolve(path));
        JsonObject terrain = json != null && json.has("terrain") && json.get("terrain").isJsonObject() ? json.getAsJsonObject("terrain") : null;
        if (terrain == null || !terrain.has("type") || !terrain.get("type").isJsonPrimitive() || !Convert.FLAT.equalsIgnoreCase(terrain.get("type").getAsString().trim())) { return; }
        boolean floored = terrain.has("minHeight") && terrain.get("minHeight").isJsonPrimitive() && terrain.getAsJsonPrimitive("minHeight").isNumber();
        dimensionFloors.put(namespace + ":" + path.substring("dimensions/".length(), path.length() - ".json".length()), floored ? terrain.get("minHeight").getAsInt() : Convert.FLAT_FLOOR);
    }

    private void templateFloor(Path home, String path) {
        JsonObject json = readJson(home.resolve(path));
        templateFloors.add(json != null && json.has("settings") && json.get("settings").isJsonObject() ? Convert.flatShift(json.getAsJsonObject("settings")) : 0);
    }

    public List<String> plots() { return List.copyOf(plots); }

    public int overworldShift() {
        if (templateFloors.isEmpty()) { return 0; }
        int first = templateFloors.get(0);
        for (int floor : templateFloors) {
            if (floor != first) { return 0; }
        }
        return first;
    }

    public int shiftIn(String named) {
        String id = dimension(named);
        return Convert.OVERWORLD.equals(id) ? overworldShift() : dimensionFloors.getOrDefault(id, 0);
    }

    public String dimension(String named) {
        String own = dimensionIds.get(named.trim());
        return own != null ? own : Ids.dimension(named);
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
        if (main != null && "blocks".equals(folder) && json.has("type") && json.get("type").isJsonPrimitive() && "banner".equals(json.get("type").getAsString())) {
            String layer = json.has("renderLayer") && json.get("renderLayer").isJsonPrimitive() ? json.get("renderLayer").getAsString().trim().toLowerCase(Locale.ROOT) : "";
            banners.put(namespace + ":" + file, layer);
        }
    }

    private void expose(PackType type, String namespace, String path, Source source) {
        exposed.computeIfAbsent(type, k -> new LinkedHashMap<>()).computeIfAbsent(namespace, k -> new LinkedHashMap<>()).putIfAbsent(path, source);
    }

    private void gameLoop(Path real, String path) {
        JsonObject json = readJson(real);
        if (json == null) { return; }
        int before = ticking.size();
        for (Map.Entry<String, JsonElement> dimension : json.entrySet()) {
            if (!dimension.getValue().isJsonObject() || !dimension.getValue().getAsJsonObject().has(Convert.GAME_LOOP)) { continue; }
            String function = dimension.getValue().getAsJsonObject().get(Convert.GAME_LOOP).getAsString().trim();
            if (!function.isEmpty()) { ticking.add(function.indexOf(':') < 0 ? "minecraft:" + function : function); }
        }
        if (ticking.size() == before) { return; }
        JsonObject tag = new JsonObject();
        JsonArray values = new JsonArray();
        ticking.forEach(values::add);
        tag.add("values", values);
        String tagPath = ContentFormats.FUNCTION_TAGS + "/tick.json";
        cache.put(key(PackType.SERVER_DATA, "minecraft", tagPath), GSON.toJson(tag).getBytes(StandardCharsets.UTF_8));
        expose(PackType.SERVER_DATA, "minecraft", tagPath, new Source(real, Port.Kind.DEFINITION, null));
        note("'" + path + "' runs a gameLoopFunction, which is the #minecraft:tick function tag now, so it became " + tagPath + " under minecraft");
    }

    private void expandOreDict(Path real, String path) {
        JsonObject json = readJson(real);
        if (json == null) { return; }
        for (Map.Entry<String, JsonObject> tag : ConvertRecipes.oreDict(json, this).entrySet()) {
            String id = tag.getKey();
            String namespace = id.substring(0, id.indexOf(':'));
            String tagPath = ContentFormats.ITEM_TAGS + "/" + id.substring(id.indexOf(':') + 1) + ".json";
            cache.put(key(PackType.SERVER_DATA, namespace, tagPath), GSON.toJson(tag.getValue()).getBytes(StandardCharsets.UTF_8));
            expose(PackType.SERVER_DATA, namespace, tagPath, new Source(real, Port.Kind.OREDICT, id));
            moved++;
        }
        note("'" + path + "' became " + ConvertRecipes.oreDict(json, this).size() + " item tag file(s)");
    }

    private void aliases(Path home, String namespace, Path real, String path, List<String> realPaths) {
        JsonObject json = readJson(real);
        String file = path.substring("blockstates/".length(), path.length() - ".json".length());
        Variants held = blockFiles.get(namespace + ":" + file);
        if (json == null || held == null) { return; }
        JsonObject defaults = json.has("defaults") && json.get("defaults").isJsonObject() ? json.getAsJsonObject("defaults") : new JsonObject();
        JsonObject variants = json.has("variants") && json.get("variants").isJsonObject() ? json.getAsJsonObject("variants") : new JsonObject();
        JsonObject blocks = variants.has("blocks") && variants.get("blocks").isJsonObject() ? variants.getAsJsonObject("blocks") : new JsonObject();
        Map<String, String> original = new LinkedHashMap<>();
        for (Map.Entry<String, String> rename : renamedIn(namespace, "blocks", file).entrySet()) { original.put(rename.getValue(), rename.getKey()); }
        Map<String, String> modeled = new LinkedHashMap<>();
        Map<Integer, String> stages = new LinkedHashMap<>();
        if (!json.has("forge_marker")) { gatherModels(home, json, modeled, stages); }
        int made = 0;
        for (String variant : new LinkedHashSet<>(held.byMeta().values())) {
            String key = original.getOrDefault(variant, variant);
            Map<String, String> textures = new LinkedHashMap<>();
            gather(defaults, textures);
            if (blocks.has(key) && blocks.get(key).isJsonObject()) { gather(blocks.getAsJsonObject(key), textures); }
            textures.putAll(modeled);
            for (Map.Entry<Integer, String> stage : stages.entrySet()) {
                if (alias(namespace, realPaths, home, "textures/block/" + variant + "_stage" + stage.getKey(), stage.getValue())) { made++; }
            }
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

    private void gatherModels(Path home, JsonObject json, Map<String, String> textures, Map<Integer, String> stages) {
        if (json.has("variants") && json.get("variants").isJsonObject()) {
            for (Map.Entry<String, JsonElement> variant : json.getAsJsonObject("variants").entrySet()) {
                Map<String, String> found = new LinkedHashMap<>();
                gatherModel(home, variant.getValue(), found);
                textures.putAll(found);
                if (variant.getKey().startsWith("age=") && found.containsKey("crop")) {
                    try { stages.put(Integer.parseInt(variant.getKey().substring("age=".length())), found.get("crop")); }
                    catch (NumberFormatException ignored) { note("A crop blockstate names the variant '" + variant.getKey() + "', whose age is not a number"); }
                }
            }
        }
        if (json.has("multipart") && json.get("multipart").isJsonArray()) {
            for (JsonElement part : json.getAsJsonArray("multipart")) {
                if (part.isJsonObject() && part.getAsJsonObject().has("apply")) { gatherModel(home, part.getAsJsonObject().get("apply"), textures); }
            }
        }
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

    @Override public Map<PackType, Map<String, Set<String>>> exposed() {
        Map<PackType, Map<String, Set<String>>> out = new EnumMap<>(PackType.class);
        for (Map.Entry<PackType, Map<String, Map<String, Source>>> type : exposed.entrySet()) {
            Map<String, Set<String>> namespacesOut = new LinkedHashMap<>();
            for (Map.Entry<String, Map<String, Source>> namespace : type.getValue().entrySet()) { namespacesOut.put(namespace.getKey(), new LinkedHashSet<>(namespace.getValue().keySet())); }
            out.put(type.getKey(), namespacesOut);
        }
        return out;
    }

    @Override @Nullable public InputStream open(PackType type, String namespace, String path) throws IOException {
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
                case LANG -> ConvertAssets.lang(contents, this);
                case FUNCTION -> path.endsWith(".mcfunction") ? Commands.function(contents, path, this) : contents;
                case DEFINITION -> Convert.definition(JsonParser.parseString(contents).getAsJsonObject(), path.substring(0, path.indexOf('/')), namespace, path.substring(path.indexOf('/') + 1, path.length() - ".json".length()), this);
                case MODEL -> ConvertAssets.model(JsonParser.parseString(contents).getAsJsonObject(), this);
                case PIXELMAP -> ConvertAssets.pixelMap(JsonParser.parseString(contents).getAsJsonObject(), this);
                case RECIPE -> ConvertRecipes.recipe(JsonParser.parseString(contents).getAsJsonObject(), namespace, this);
                case LOOT -> ConvertLoot.loot(JsonParser.parseString(contents).getAsJsonObject(), this);
                case ADVANCEMENT -> ConvertAdvancements.advancement(JsonParser.parseString(contents).getAsJsonObject(), this);
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

    @Override public void report() {
        reported = true;
        ContentLog.LOGGER.info("Pack '{}' is written for 1.12.2 and is read through the forward port: {} file(s) moved under data, {} left out, ids and keys rewritten as they are read", name, moved, dropped);
        List<String> said = new ArrayList<>(notes);
        for (int i = 0; i < Math.min(said.size(), 40); i++) { ContentLog.LOGGER.info("  {}", said.get(i)); }
        if (said.size() > 40) { ContentLog.LOGGER.info("  ... and {} more", said.size() - 40); }
    }

    @Override public void writeVersion(ZipOutputStream out, String prefix) throws IOException {
        int written = 0;
        for (Map.Entry<PackType, Map<String, Map<String, Source>>> type : exposed.entrySet()) {
            for (Map.Entry<String, Map<String, Source>> namespace : type.getValue().entrySet()) {
                for (Map.Entry<String, Source> path : namespace.getValue().entrySet()) {
                    if (carried(type.getKey(), namespace.getKey(), path.getKey(), path.getValue())) { continue; }
                    try (InputStream in = open(type.getKey(), namespace.getKey(), path.getKey())) {
                        if (in == null) { continue; }
                        out.putNextEntry(new ZipEntry(prefix + type.getKey().getDirectory() + "/" + namespace.getKey() + "/" + path.getKey()));
                        in.transferTo(out);
                        out.closeEntry();
                        written++;
                    }
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
        out.putNextEntry(new ZipEntry(prefix + "pack.mcmeta"));
        out.write(GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        note("written into " + prefix + ", " + written + " file(s) this version reads differently; the rest is read from the root as it is");
    }

    private boolean carried(PackType type, String namespace, String path, Source source) {
        return type == PackType.CLIENT_RESOURCES && source.kind() == Port.Kind.RAW && Port.unchanged(path) && source.real().equals(root.resolve("assets").resolve(namespace).resolve(path));
    }

    @Override public void closing() {
        if (rewritten > 0) { ContentLog.LOGGER.info("Pack '{}': the forward port rewrote {} id(s) and key(s) while the pack was read", name, rewritten); }
    }
}
