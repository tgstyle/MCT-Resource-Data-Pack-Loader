package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentPixelMaps;
import mctmods.resourcedatapackloader.pack.interfaces.IPackConsumer;
import mctmods.resourcedatapackloader.pack.port.PackPort;
import mctmods.resourcedatapackloader.pack.port.Port;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.PackType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class PackManager {
    public static final String ROOT_DIRECTORY = "rdploader";
    public static final String PACK_META = "pack.mcmeta";
    public static final String ROOT_PACK = "<loose files>";
    public static final String CONFIG = "config";
    public static final String JSON = "json";
    public static final PackType CONTENT = PackType.SERVER_DATA;
    public static final String BLOCKS = "blocks";
    public static final String ITEMS = "items";
    public static final String FLUIDS = "fluids";
    public static final String BIOMES = "biomes";
    public static final String CAVEREGIONS = "caveregions";
    public static final String GAMERULES = "gamerules";
    public static final String BLASTPLASTER = "blastplaster";
    public static final String WORLDINTRO = "worldintro";
    public static final String WORLDGEN = "worldgen";
    public static final String DIMENSIONS = "dimensions";
    public static final String WORLDTEMPLATES = "worldtemplates";
    public static final String PATHINTERSECTS = "pathintersects";
    public static final String CITYMAPS = "citymaps";
    public static final String STRUCTUREMAPS = "structuremaps";
    public static final String PORTALFRAMES = "portalframes";
    public static final String GATES = "gates";
    public static final String CARDS = "cards";
    public static final String POTIONS = "potions";
    public static final String POTION_TYPES = "potion_types";
    public static final String BREWING = "brewing";
    public static final String VILLAGERS = "villagers";
    public static final String TRADES = "trades";
    public static final String VILLAGES = "villages";
    public static final String ENTITIES = "entities";
    public static final String RECIPE_REMOVALS = "recipe_removals";
    public static final String DISABLED = "disabled";
    public static final String FURNACE = "furnace";
    public static final String LOOT_INJECTIONS = "loot_injections";
    public static final String PLAYER_LOOT = "player_loot";
    public static final String BLOCK_DROPS = "block_drops";
    public static final String REGISTRY_REMAP = "registry_remap";
    public static final String SOUNDS = "sounds";
    public static final String FUELS = "fuels";
    public static final String EXPOSURES = "exposures";
    public static final String MATERIALS = "materials";
    public static final String FUNCTIONS = "function";
    public static final String MCFUNCTION = "mcfunction";
    public static final String TABS = "tabs";
    public static final String TEAMS = "teams";
    public static final String SCORING = "scoring";
    public static final String RAIDS = "raids";
    public static final String OVERRIDES = "overrides";
    public static final String HARDNESS = "hardness";
    public static final String ANVILS = "anvils";
    private static final String DISABLED_SUFFIX = ".disabled";
    private static final Pattern PRIORITY = Pattern.compile("^[Rr][Dd][Pp][Ll](\\d+)?(?:([OoNn])(?=[ _-]|$))?[ _-]?");
    private static final PackManager INSTANCE = new PackManager();
    private static final Gson GSON = new GsonBuilder().create();
    private final List<RDPLPack> packs = new CopyOnWriteArrayList<>();
    private final Map<PackType, Map<String, Map<String, Entry>>> mergedNormal = new EnumMap<>(PackType.class);
    private final Map<PackType, Map<String, Map<String, Entry>>> mergedOverride = new EnumMap<>(PackType.class);
    private final Set<String> warned = ConcurrentHashMap.newKeySet();
    private final Set<String> served = ConcurrentHashMap.newKeySet();
    private final AtomicInteger generation = new AtomicInteger();
    @Nullable private volatile Path root;
    @Nullable private volatile JsonObject meta;

    private PackManager() {
        for (PackType type : PackType.values()) {
            mergedNormal.put(type, new ConcurrentHashMap<>());
            mergedOverride.put(type, new ConcurrentHashMap<>());
        }
    }

    public static PackManager get() { return INSTANCE; }

    public boolean isEmpty() { return packs.isEmpty(); }

    public List<RDPLPack> getPacks() { return Collections.unmodifiableList(packs); }

    @Nullable public Path getRoot() { return root; }

    public int getGeneration() { return generation.get(); }

    public boolean provides(String namespace) {
        for (RDPLPack pack : packs) {
            for (PackType type : PackType.values()) {
                if (pack.getNamespaces(type).contains(namespace)) { return true; }
            }
        }
        return false;
    }

    public void forEach(String folder, String ext, IPackConsumer consumer) {
        for (RDPLPack pack : packs) { pack.forEach(CONTENT, folder, ext, consumer); }
    }

    public void scan(Path packRoot) {
        if (packRoot.getNameCount() == 0 || packRoot.equals(packRoot.getRoot())) {
            ContentLog.LOGGER.error("rootDirectory resolves to '{}', which would treat the whole folder as the pack root. Set it to a folder name such as '{}'. No packs loaded.", packRoot, ROOT_DIRECTORY);
            close();
            return;
        }
        this.root = packRoot;
        close();
        prepare(packRoot);
        if (!Files.isDirectory(packRoot)) { return; }
        List<RDPLPack> named = new ArrayList<>();
        for (RDPLPack mod : ModPacks.load(packRoot)) {
            if (mod.getPriority() < 0) { packs.add(mod); }
            else { named.add(mod); }
        }
        RDPLPack loose = loadRoot(packRoot);
        if (loose != null) { packs.add(loose); }
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(packRoot)) {
            for (Path entry : entries) {
                String fileName = entry.getFileName().toString();
                if (RDPLPack.ASSETS.equals(fileName) || RDPLPack.DATA.equals(fileName) || PackReadme.README.equals(fileName) || CONFIG.equals(fileName)) { continue; }
                if (Files.isDirectory(entry)) {
                    if (ContentPixelMaps.CACHE_DIRECTORY.equals(fileName)) { continue; }
                    ContentLog.LOGGER.warn("Skipping the folder '{}': a pack is a zip file. Loose files go under {}/{}/<namespace> or {}/{}/<namespace>, and a pack in a folder is zipped up", fileName, packRoot, RDPLPack.ASSETS, packRoot, RDPLPack.DATA);
                    continue;
                }
                if (fileName.toLowerCase(Locale.ROOT).endsWith(DISABLED_SUFFIX)) {
                    ContentLog.LOGGER.info("Skipping disabled pack '{}'", fileName);
                    continue;
                }
                RDPLPack pack = load(entry);
                if (pack != null) { named.add(pack); }
            }
        }
        catch (IOException | UncheckedIOException ex) {
            ContentLog.LOGGER.error("Could not scan {}", packRoot, ex);
        }
        named.sort(Comparator.comparingInt(RDPLPack::getPriority).thenComparing(RDPLPack::getName, String.CASE_INSENSITIVE_ORDER));
        packs.addAll(named);
        buildIndex();
        meta = resolveMeta();
        PackOptions.reload(packRoot, packs);
        ContentPixelMaps.tidy();
        generation.incrementAndGet();
    }

    private void buildIndex() {
        for (PackType type : PackType.values()) {
            for (RDPLPack pack : packs) {
                Map<String, Map<String, Entry>> target = (pack.isOverriding() ? mergedOverride : mergedNormal).get(type);
                for (String namespace : pack.getNamespaces(type)) {
                    Map<String, Entry> paths = target.computeIfAbsent(namespace, k -> new ConcurrentHashMap<>());
                    for (String path : pack.getPaths(type, namespace)) {
                        String lowered = lower(path);
                        Entry prev = paths.get(lowered);
                        if (prev == null) {
                            paths.put(lowered, new Entry(pack, path, null));
                            String singular = type == PackType.SERVER_DATA ? Port.singular(lowered) : null;
                            if (singular != null && !paths.containsKey(singular)) { paths.put(singular, new Entry(pack, path, null)); }
                            continue;
                        }
                        Map<String, RDPLPack> variants = prev.variants();
                        if (variants == null && !prev.actual().equals(path)) {
                            variants = new HashMap<>(4);
                            variants.put(prev.actual(), prev.pack());
                        }
                        if (variants != null) { variants.put(path, pack); }
                        paths.put(lowered, new Entry(pack, path, variants));
                    }
                }
            }
        }
    }

    private void prepare(Path packRoot) {
        try {
            Files.createDirectories(packRoot.resolve(RDPLPack.ASSETS));
            Files.createDirectories(packRoot.resolve(RDPLPack.DATA));
            Files.createDirectories(packRoot.resolve(CONFIG));
            Path readme = packRoot.resolve(PackReadme.README);
            String text = PackReadme.readmeText();
            if (text == null) { ContentLog.LOGGER.error("The readme is missing from the jar, so {} is left as it is", readme); }
            else {
                boolean missing = !Files.exists(readme);
                if (missing || !text.equals(Files.readString(readme))) {
                    Files.writeString(readme, text);
                    ContentLog.LOGGER.info("{} {}", missing ? "Wrote" : "Brought up to date", readme);
                }
            }
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not prepare {}", packRoot, ex);
        }
    }

    @Nullable private RDPLPack loadRoot(Path packRoot) {
        if (RDPLPack.lacksContent(packRoot)) { return null; }
        RDPLPack pack = new RDPLPack(ROOT_PACK, -1, Config.packs.overrideResourcePacks(), packRoot, null);
        return pack.isEmpty() ? null : pack;
    }

    @Nullable private RDPLPack load(Path entry) {
        String fileName = entry.getFileName().toString();
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".zip")) { return null; }
        FileSystem zip = null;
        try {
            zip = FileSystems.newFileSystem(entry);
            RDPLPack pack = create(stripExtension(fileName), zip.getPath("/"), zip);
            if (pack.isEmpty()) {
                ContentLog.LOGGER.warn("Skipping '{}': no '{}' or '{}' directory inside the zip", fileName, RDPLPack.ASSETS, RDPLPack.DATA);
                zip.close();
                return null;
            }
            PackPort port = pack.ported();
            if (port == null) { return pack; }
            Path written = PackVersions.write(entry, port);
            if (written == null) { return pack; }
            closeQuietly(zip);
            if (!PackVersions.swap(written, entry)) { return null; }
            ContentLog.LOGGER.info("Pack '{}': what this version reads differently was written into its '{}' folder, and the {} files at the root are left as they were, so the same zip still loads on {}. Read the port's notes above and the parsers' lines below for what to finish by hand", fileName, PackVersions.prefix(), port.origin(), port.origin());
            return load(entry);
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not open zip pack '{}'", fileName, ex);
            closeQuietly(zip);
            return null;
        }
        catch (RuntimeException ex) {
            closeQuietly(zip);
            throw ex;
        }
    }

    private static void closeQuietly(@Nullable FileSystem owned) {
        if (owned == null) { return; }
        try { owned.close(); }
        catch (IOException ignored) { }
    }

    private static RDPLPack create(String raw, Path root, @Nullable FileSystem owned) {
        boolean fallback = Config.packs.overrideResourcePacks();
        Matcher matcher = PRIORITY.matcher(raw);
        if (!matcher.find() || (matcher.group(1) == null && matcher.group(2) == null)) { return new RDPLPack(raw, -1, fallback, root, owned); }
        String clean = raw.substring(matcher.end());
        if (clean.isEmpty()) { clean = raw; }
        boolean overriding = tier(matcher.group(2), fallback);
        if (matcher.group(1) == null) { return new RDPLPack(clean, -1, overriding, root, owned); }
        try { return new RDPLPack(clean, Integer.parseInt(matcher.group(1)), overriding, root, owned); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.warn("Pack '{}': priority number is too large, treating the pack as unprioritized", raw);
            return new RDPLPack(clean, -1, overriding, root, owned);
        }
    }

    private static boolean tier(@Nullable String marker, boolean fallback) {
        if (marker == null) { return fallback; }
        return marker.equalsIgnoreCase("O");
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }

    public void warnAboutDisabledFeatures() { PackReport.warnAboutDisabledFeatures(packs); }

    public void report() { PackReport.report(packs, root); }

    @Nullable private Entry lookup(PackType type, String namespace, String path) {
        Entry entry = lookup(type, namespace, path, true);
        if (entry != null) { return entry; }
        return lookup(type, namespace, path, false);
    }

    @Nullable private Entry lookup(PackType type, String namespace, String path, boolean overriding) {
        Map<String, Entry> paths = (overriding ? mergedOverride : mergedNormal).get(type).get(namespace);
        if (paths == null) { return null; }
        Entry entry = paths.get(lower(path));
        if (entry == null) { return null; }
        if (entry.actual().equals(path)) { return entry; }
        if (entry.variants() != null && entry.variants().get(path) == entry.pack()) { return new Entry(entry.pack(), path, null); }
        reportCaseMismatch(type, namespace, path, entry);
        return entry;
    }

    @Nullable private Entry resolve(PackType type, String namespace, String path, boolean overriding) {
        Entry entry = lookup(type, namespace, path, overriding);
        if (entry != null) { served.add(key(type, namespace, entry.actual())); }
        return entry;
    }

    private static String key(PackType type, String namespace, String path) { return type.getDirectory() + ":" + namespace + ":" + path; }

    private static String lower(String path) { return isLowerCase(path) ? path : path.toLowerCase(Locale.ROOT); }

    private static boolean isLowerCase(String path) {
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c != Character.toLowerCase(c)) { return false; }
        }
        return true;
    }

    private void reportCaseMismatch(PackType type, String namespace, String requested, Entry entry) {
        if (!Config.packs.warnOnCaseMismatch()) { return; }
        if (!warned.add(key(type, namespace, requested))) { return; }
        if (lower(requested).equals(Port.singular(lower(entry.actual())))) {
            ContentLog.LOGGER.warn("Pack '{}': loading {}/{}/{} from '{}', a 1.20.1 plural folder name. Rename it to '{}', the 1.21.1 name, so it also works outside this mod.", entry.pack().getName(), type.getDirectory(), namespace, requested, entry.actual(), requested);
            return;
        }
        ContentLog.LOGGER.warn("Pack '{}': loading {}/{}/{} from '{}', the filename case does not match. Rename it to '{}' so it also works outside this mod.", entry.pack().getName(), type.getDirectory(), namespace, requested, entry.actual(), requested);
    }

    public boolean existsRaw(PackType type, String namespace, String path, boolean overriding) {
        if (resolve(type, namespace, path, overriding) != null) { return true; }
        return drawable(type, path) && ContentPixelMaps.exists(namespace, path, overriding);
    }

    @Nullable public InputStream openRaw(PackType type, String namespace, String path, boolean overriding) throws IOException {
        Entry entry = resolve(type, namespace, path, overriding);
        if (entry != null) { return entry.pack().open(type, namespace, entry.actual()); }
        if (!drawable(type, path)) { return null; }
        byte[] drawn = ContentPixelMaps.made(namespace, path, overriding);
        return drawn == null ? null : new ByteArrayInputStream(drawn);
    }

    @Nullable public byte[] bytes(PackType type, String namespace, String path) {
        for (boolean overriding : new boolean[] {false, true}) {
            try (InputStream stream = openRaw(type, namespace, path, overriding)) {
                if (stream != null) { return stream.readAllBytes(); }
            }
            catch (IOException ex) { ContentLog.LOGGER.error("Could not read {}:{} from the packs", namespace, path, ex); }
        }
        return null;
    }

    private static boolean drawable(PackType type, String path) { return type == PackType.CLIENT_RESOURCES && ContentPixelMaps.couldBeDrawn(path); }

    public void list(PackType type, String namespace, boolean overriding, String prefix, Consumer<String> out) {
        Map<String, Entry> paths = (overriding ? mergedOverride : mergedNormal).get(type).get(namespace);
        if (paths == null) { return; }
        String head = prefix.isEmpty() || prefix.endsWith("/") ? prefix : prefix + "/";
        for (Map.Entry<String, Entry> held : paths.entrySet()) {
            if (!held.getKey().startsWith(head)) { continue; }
            served.add(key(type, namespace, held.getValue().actual()));
            out.accept(held.getKey());
            if (type == PackType.CLIENT_RESOURCES && held.getKey().endsWith(ContentPixelMaps.PNG + ContentPixelMaps.SUFFIX)) { out.accept(held.getKey().substring(0, held.getKey().length() - ContentPixelMaps.SUFFIX.length())); }
        }
    }

    public List<String> findUnused() {
        List<String> unused = new ArrayList<>();
        for (RDPLPack pack : packs) {
            for (String namespace : pack.getNamespaces(PackType.CLIENT_RESOURCES)) {
                for (String path : pack.getPaths(PackType.CLIENT_RESOURCES, namespace)) {
                    if (served.contains(key(PackType.CLIENT_RESOURCES, namespace, path))) { continue; }
                    unused.add(pack.getName() + " -> " + namespace + ":" + path);
                }
            }
        }
        Collections.sort(unused);
        return unused;
    }

    public boolean provides(PackType type, String namespace, String path) {
        if (lookup(type, namespace, path) != null) { return true; }
        return drawable(type, path) && lookup(type, namespace, path + ContentPixelMaps.SUFFIX) != null;
    }

    public List<RDPLPack> holders(PackType type, String namespace, String path) {
        List<RDPLPack> result = new ArrayList<>();
        Entry entry = lookup(type, namespace, path);
        if (entry == null) { return result; }
        String lowered = lower(entry.actual());
        for (RDPLPack pack : packs) {
            for (String held : pack.getPaths(type, namespace)) {
                if (!lowered.equals(lower(held))) { continue; }
                result.add(pack);
                break;
            }
        }
        return result;
    }

    @Nullable public Path packFile(String name) {
        for (int i = packs.size() - 1; i >= 0; i--) {
            Path file = packs.get(i).packFile(name);
            if (file != null) { return file; }
        }
        Path base = root;
        if (base == null) { return null; }
        Path file = base.resolve(name);
        return Files.isRegularFile(file) ? file : null;
    }

    @Nullable public String description() {
        JsonObject held = meta;
        return held == null ? null : descriptionOf(held.getAsJsonObject("pack"));
    }

    @Nullable public JsonObject language() {
        JsonObject held = meta;
        return held != null && held.has("language") && held.get("language").isJsonObject() ? held.getAsJsonObject("language") : null;
    }

    @Nullable private JsonObject resolveMeta() {
        for (int i = packs.size() - 1; i >= 0; i--) {
            RDPLPack pack = packs.get(i);
            String contents;
            try { contents = pack.readPackFile(PACK_META); }
            catch (IOException ex) {
                ContentLog.LOGGER.error("Pack '{}': could not read {}", pack.getName(), PACK_META, ex);
                continue;
            }
            if (contents == null) { continue; }
            JsonObject found = validMeta(contents);
            if (found != null) { return found; }
            ContentLog.LOGGER.warn("Pack '{}': {} is not valid JSON with a 'pack' section, so it is being ignored", pack.getName(), PACK_META);
        }
        Path base = root;
        Path file = base == null ? null : base.resolve(PACK_META);
        if (file == null || !Files.isRegularFile(file)) { return null; }
        String contents;
        try { contents = Files.readString(file); }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {}", file, ex);
            return null;
        }
        JsonObject found = validMeta(contents);
        if (found != null) { return found; }
        ContentLog.LOGGER.warn("{} is not valid JSON with a 'pack' section, so it is being ignored", file);
        return null;
    }

    @Nullable private static JsonObject validMeta(String contents) {
        try {
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
            return json != null && json.has("pack") && json.get("pack").isJsonObject() ? json : null;
        }
        catch (RuntimeException malformed) { return null; }
    }

    @Nullable private static String descriptionOf(JsonObject pack) {
        JsonElement held = pack.get("description");
        return held != null && held.isJsonPrimitive() ? held.getAsString() : null;
    }

    public Set<String> getNamespaces(PackType type, boolean overriding) {
        Set<String> all = new LinkedHashSet<>();
        for (RDPLPack pack : packs) {
            if (pack.isOverriding() == overriding) { all.addAll(pack.getNamespaces(type)); }
        }
        return Collections.unmodifiableSet(all);
    }

    public boolean hasTier(boolean overriding) {
        for (RDPLPack pack : packs) {
            if (pack.isOverriding() == overriding) { return true; }
        }
        return false;
    }

    public void close() {
        for (RDPLPack pack : packs) {
            try { pack.close(); }
            catch (IOException ex) { ContentLog.LOGGER.error("Could not close pack '{}'", pack.getName(), ex); }
        }
        packs.clear();
        for (PackType type : PackType.values()) {
            mergedNormal.get(type).clear();
            mergedOverride.get(type).clear();
        }
        warned.clear();
        served.clear();
        ContentPixelMaps.forget();
        meta = null;
    }

    private record Entry(RDPLPack pack, String actual, @Nullable Map<String, RDPLPack> variants) {}
}
