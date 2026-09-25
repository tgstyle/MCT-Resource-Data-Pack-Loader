package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentPixelMaps;
import mctmods.resourcedatapackloader.pack.interfaces.IPackConsumer;
import mctmods.resourcedatapackloader.pack.port.Port;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class PackManager {
    public static final String ROOT_DIRECTORY = "rdploader";
    public static final String PACK_META = "pack.mcmeta";
    public static final String PACK_ICON = "pack.png";
    public static final String ROOT_PACK = "<loose files>";
    private static final Set<String> EXTRA_DATA = ConcurrentHashMap.newKeySet();
    private static final String DISABLED_SUFFIX = ".disabled";
    public static final String ADVANCEMENTS = "advancements";
    public static final String LOOT_TABLES = "loot_tables";
    public static final String RECIPES = "recipes";
    public static final String REGISTRY_REMAP = "registry_remap";
    public static final String BLOCKS = "blocks";
    public static final String ITEMS = "items";
    public static final String FLUIDS = "fluids";
    public static final String FURNACE = "furnace";
    public static final String WORLDGEN = "worldgen";
    public static final String EXPOSURES = "exposures";
    public static final String CAVEREGIONS = "caveregions";
    public static final String GATES = "gates";
    public static final String CARDS = "cards";
    public static final String WORLDTEMPLATES = "worldtemplates";
    public static final String PATHINTERSECTS = "pathintersects";
    public static final String STRUCTUREMAPS = "structuremaps";
    public static final String CITYMAPS = "citymaps";
    public static final String PORTALFRAMES = "portalframes";
    public static final String BLASTPLASTER = "blastplaster";
    public static final String WORLDINTRO = "worldintro";
    public static final String DIMENSIONS = "dimensions";
    public static final String GAMERULES = "gamerules";
    public static final String TEAMS = "teams";
    public static final String SCORING = "scoring";
    public static final String RAIDS = "raids";
    public static final String FUELS = "fuels";
    public static final String OREDICT = "oredict";
    public static final String SOUNDS = "sounds";
    public static final String RECIPE_REMOVALS = "recipe_removals";
    public static final String DISABLED = "disabled";
    public static final String MATERIALS = "materials";
    public static final String LOOT_INJECTIONS = "loot_injections";
    public static final String BLOCK_DROPS = "block_drops";
    public static final String ANVILS = "anvils";
    public static final String PLAYER_LOOT = "player_loot";
    public static final String TABS = "tabs";
    public static final String FUNCTIONS = "functions";
    public static final String STRUCTURES = "structures";
    public static final String POTIONS = "potions";
    public static final String POTION_TYPES = "potion_types";
    public static final String BREWING = "brewing";
    public static final String VILLAGERS = "villagers";
    public static final String TRADES = "trades";
    public static final String BIOMES = "biomes";
    public static final String VILLAGES = "villages";
    public static final String ENTITIES = "entities";
    public static final String HARDNESS = "hardness";
    public static final String OVERRIDES = "overrides";
    public static final String JSON = "json";
    public static final String MCFUNCTION = "mcfunction";
    private static final Pattern PRIORITY = Pattern.compile("^[Rr][Dd][Pp][Ll](\\d+)?(?:([OoNn])(?=[ _-]|$))?[ _-]?");
    private static final PackManager INSTANCE = new PackManager();
    private final List<RDPLPack> packs = new CopyOnWriteArrayList<>();
    private final Map<String, Map<String, Entry>> mergedNormal = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Entry>> mergedOverride = new ConcurrentHashMap<>();
    private final Set<String> warned = ConcurrentHashMap.newKeySet();
    private final Set<String> served = ConcurrentHashMap.newKeySet();
    private volatile Path root;
    @Nullable private volatile Set<String> namespacesNormal;
    @Nullable private volatile Set<String> namespacesOverride;
    private final AtomicInteger generation = new AtomicInteger();
    private static final Gson GSON = new GsonBuilder().create();
    @Nullable private volatile String packMeta;

    private PackManager() {}

    public static PackManager get() { return INSTANCE; }

    public boolean isEmpty() { return packs.isEmpty(); }

    public List<RDPLPack> getPacks() { return Collections.unmodifiableList(packs); }

    public boolean provides(String namespace) {
        for (RDPLPack pack : packs) {
            if (pack.getNamespaces().contains(namespace)) { return true; }
        }
        return false;
    }

    @Nullable public Path getRoot() { return root; }

    public int getGeneration() { return generation.get(); }

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
                switch (fileName) {
                    case RDPLPack.ASSETS:
                    case Port.DATA:
                    case PackReadme.README:
                    case "config": continue;
                }
                if (Files.isDirectory(entry)) {
                    if (!ContentPixelMaps.CACHE_DIRECTORY.equals(fileName)) { ContentLog.LOGGER.warn("Skipping the folder '{}': a pack is a zip file. Loose files go under {}/{}/<namespace>, and a pack in a folder is zipped up", fileName, packRoot, RDPLPack.ASSETS); }
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
        namespacesNormal = null;
        namespacesOverride = null;
        packMeta = resolvePackMeta();
        PackOptions.reload(packRoot, packs);
        ContentPixelMaps.tidy();
    }

    private void buildIndex() {
        for (RDPLPack pack : packs) {
            Map<String, Map<String, Entry>> target = pack.isOverriding() ? mergedOverride : mergedNormal;
            for (String namespace : pack.getNamespaces()) {
                Map<String, Entry> paths = target.computeIfAbsent(namespace, k -> new ConcurrentHashMap<>());
                for (String path : pack.getPaths(namespace)) {
                    String lowered = isLowerCase(path) ? path : path.toLowerCase(Locale.ROOT);
                    Entry prev = paths.get(lowered);
                    if (prev == null) {
                        paths.put(lowered, new Entry(pack, path, null));
                        continue;
                    }
                    Map<String, RDPLPack> variants = prev.variants;
                    if (variants == null && !prev.actual.equals(path)) {
                        variants = new HashMap<>(4);
                        variants.put(prev.actual, prev.pack);
                    }
                    if (variants != null) { variants.put(path, pack); }
                    paths.put(lowered, new Entry(pack, path, variants));
                }
            }
        }
    }

    private void prepare(Path packRoot) {
        try {
            Files.createDirectories(packRoot.resolve(RDPLPack.ASSETS));
            Files.createDirectories(packRoot.resolve("config"));
            Path readme = packRoot.resolve(PackReadme.README);
            String text = PackReadme.readmeText();
            if (text == null) { ContentLog.LOGGER.error("The readme is missing from the jar, so {} is left as it is", readme); }
            else {
                boolean missing = !Files.exists(readme);
                if (missing || !text.equals(new String(Files.readAllBytes(readme), StandardCharsets.UTF_8))) {
                    Files.write(readme, text.getBytes(StandardCharsets.UTF_8));
                    ContentLog.LOGGER.info("{} {}", missing ? "Wrote" : "Brought up to date", readme);
                }
            }
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not prepare {}", packRoot, ex);
        }
    }

    @Nullable private RDPLPack loadRoot(Path packRoot) {
        if (!Files.isDirectory(packRoot.resolve(RDPLPack.ASSETS)) && !Files.isDirectory(packRoot.resolve(Port.DATA))) { return null; }
        RDPLPack pack = new RDPLPack(ROOT_PACK, -1, Config.packs.overrideResourcePacks, packRoot, null, null);
        return pack.getNamespaces().isEmpty() ? null : pack;
    }

    @Nullable private RDPLPack load(Path entry) {
        String fileName = entry.getFileName().toString();
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".zip")) { return null; }
        FileSystem zip = null;
        try {
            zip = FileSystems.newFileSystem(entry, null);
            RDPLPack pack = create(stripExtension(fileName), zip.getPath("/"), zip, entry);
            if (pack.getNamespaces().isEmpty()) {
                ContentLog.LOGGER.warn("Skipping '{}': no '{}' directory inside the zip", fileName, RDPLPack.ASSETS);
                zip.close();
                return null;
            }
            if (pack.ported() == null) { return pack; }
            Path written = PackVersions.write(entry, pack.ported());
            if (written == null) { return pack; }
            IOUtils.closeQuietly(pack::close);
            if (!PackVersions.swap(written, entry)) { return null; }
            ContentLog.LOGGER.info("Pack '{}': what 1.12.2 reads differently was written into its '{}' folder, and the modern files at the root are left as they were, so the same zip still loads on 1.20.1 and 1.21.1. Read the port's notes above and the parsers' lines below for what to finish by hand", fileName, PackVersions.PREFIX);
            return load(entry);
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not open zip pack '{}'", fileName, ex);
            IOUtils.closeQuietly(zip);
            return null;
        }
        catch (RuntimeException ex) {
            IOUtils.closeQuietly(zip);
            throw ex;
        }
    }

    private static RDPLPack create(String raw, Path root, @Nullable FileSystem owned, @Nullable Path archiveFile) {
        boolean fallback = Config.packs.overrideResourcePacks;
        Matcher matcher = PRIORITY.matcher(raw);
        if (!matcher.find() || (matcher.group(1) == null && matcher.group(2) == null)) { return new RDPLPack(raw, -1, fallback, root, owned, archiveFile); }
        String clean = raw.substring(matcher.end());
        if (clean.isEmpty()) { clean = raw; }
        boolean overriding = tier(matcher.group(2), fallback);
        if (matcher.group(1) == null) { return new RDPLPack(clean, -1, overriding, root, owned, archiveFile); }
        try { return new RDPLPack(clean, Integer.parseInt(matcher.group(1)), overriding, root, owned, archiveFile); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.warn("Pack '{}': priority number is too large, treating the pack as unprioritised", raw);
            return new RDPLPack(clean, -1, overriding, root, owned, archiveFile);
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

    @Nullable private Entry lookup(String namespace, String path) {
        Entry entry = lookup(namespace, path, true);
        if (entry != null) { return entry; }
        return lookup(namespace, path, false);
    }

    @Nullable private Entry lookup(String namespace, String path, boolean overriding) {
        Map<String, Entry> paths = (overriding ? mergedOverride : mergedNormal).get(namespace);
        if (paths == null) { return null; }
        Entry entry = paths.get(isLowerCase(path) ? path : path.toLowerCase(Locale.ROOT));
        if (entry == null) { return null; }
        if (entry.actual.equals(path)) { return entry; }
        if (entry.variants != null && entry.variants.get(path) == entry.pack) { return new Entry(entry.pack, path, null); }
        reportCaseMismatch(namespace, path, entry);
        return entry;
    }

    @Nullable private Entry resolve(String namespace, String path) {
        Entry entry = lookup(namespace, path);
        if (entry != null) { served.add(namespace + ":" + entry.actual); }
        return entry;
    }

    @Nullable private Entry resolve(String namespace, String path, boolean overriding) {
        Entry entry = lookup(namespace, path, overriding);
        if (entry != null) { served.add(namespace + ":" + entry.actual); }
        return entry;
    }

    private static boolean isLowerCase(String path) {
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c != Character.toLowerCase(c)) { return false; }
        }
        return true;
    }

    private void reportCaseMismatch(String namespace, String requested, Entry entry) {
        if (!Config.packs.warnOnCaseMismatch) { return; }
        if (!warned.add(namespace + ":" + requested)) { return; }
        ContentLog.LOGGER.warn("Pack '{}': loading {}:{} from '{}', the filename case does not match. Rename it to '{}' so it also works outside this mod.", entry.pack.getName(), namespace, requested, entry.actual, requested);
    }

    public boolean existsRaw(String namespace, String path, boolean overriding) {
        if (resolve(namespace, path, overriding) != null) { return true; }
        return ContentPixelMaps.couldBeDrawn(path) && ContentPixelMaps.exists(namespace, path, overriding);
    }

    @Nullable public InputStream openRaw(String namespace, String path) throws IOException {
        Entry entry = resolve(namespace, path);
        if (entry == null) { return null; }
        return entry.pack.open(namespace, entry.actual);
    }

    @Nullable public InputStream openRaw(String namespace, String path, boolean overriding) throws IOException {
        Entry entry = resolve(namespace, path, overriding);
        if (entry != null) { return entry.pack.open(namespace, entry.actual); }
        if (!ContentPixelMaps.couldBeDrawn(path)) { return null; }
        byte[] drawn = ContentPixelMaps.made(namespace, path, overriding);
        return drawn == null ? null : new ByteArrayInputStream(drawn);
    }

    public List<String> rawPaths(boolean overriding) {
        List<String> paths = new ArrayList<>();
        for (Map.Entry<String, Map<String, Entry>> namespace : (overriding ? mergedOverride : mergedNormal).entrySet()) {
            for (Entry entry : namespace.getValue().values()) {
                String path = "assets/" + namespace.getKey() + "/" + entry.actual;
                paths.add(path);
                if (path.endsWith(ContentPixelMaps.PNG + ContentPixelMaps.SUFFIX)) { paths.add(path.substring(0, path.length() - ContentPixelMaps.SUFFIX.length())); }
            }
        }
        return paths;
    }

    public List<String> findUnused() {
        List<String> unused = new ArrayList<>();
        for (RDPLPack pack : packs) {
            for (String namespace : pack.getNamespaces()) {
                for (String path : pack.getPaths(namespace)) {
                    if (isData(path)) { continue; }
                    if (served.contains(namespace + ":" + path)) { continue; }
                    unused.add(pack.getName() + " -> " + namespace + ":" + path);
                }
            }
        }
        Collections.sort(unused);
        return unused;
    }

    @SuppressWarnings("unused") public static void registerDataFolders(String... folders) { Collections.addAll(EXTRA_DATA, folders); }

    private static boolean extraData(String path) {
        for (String folder : EXTRA_DATA) {
            if (path.startsWith(folder + "/")) { return true; }
        }
        return false;
    }

    private static boolean isData(String path) {
        return extraData(path) || path.startsWith(ADVANCEMENTS + "/") || path.startsWith(LOOT_TABLES + "/") || path.startsWith(RECIPES + "/")
                || path.startsWith(FUNCTIONS + "/") || path.startsWith(REGISTRY_REMAP + "/") || path.startsWith(STRUCTURES + "/")
                || path.startsWith(GATES + "/") || path.startsWith(CARDS + "/") || path.startsWith(WORLDTEMPLATES + "/") || path.startsWith(PATHINTERSECTS + "/") || path.startsWith(STRUCTUREMAPS + "/") || path.startsWith(CITYMAPS + "/") || path.startsWith(PORTALFRAMES + "/")
                || path.startsWith(BLASTPLASTER + "/") || path.startsWith(WORLDINTRO + "/") || path.startsWith(DIMENSIONS + "/") || path.startsWith(GAMERULES + "/")
                || path.startsWith(BLOCKS + "/") || path.startsWith(ITEMS + "/") || path.startsWith(FLUIDS + "/") || path.startsWith(FURNACE + "/") || path.startsWith(WORLDGEN + "/") || path.startsWith(FUELS + "/") || path.startsWith(OREDICT + "/") || path.startsWith(SOUNDS + "/") || path.startsWith(RECIPE_REMOVALS + "/") || path.startsWith(DISABLED + "/") || path.startsWith(MATERIALS + "/") || path.startsWith(LOOT_INJECTIONS + "/") || path.startsWith(BLOCK_DROPS + "/") || path.startsWith(ANVILS + "/") || path.startsWith(PLAYER_LOOT + "/") || path.startsWith(TABS + "/") || path.startsWith(POTIONS + "/") || path.startsWith(POTION_TYPES + "/") || path.startsWith(BREWING + "/") || path.startsWith(VILLAGERS + "/") || path.startsWith(TRADES + "/") || path.startsWith(BIOMES + "/") || path.startsWith(VILLAGES + "/") || path.startsWith(ENTITIES + "/") || path.startsWith(HARDNESS + "/");
    }

    @Nullable public String getPackName(String namespace, String path) {
        Entry entry = lookup(namespace, path);
        return entry == null ? null : entry.pack.getName();
    }

    public List<RDPLPack> holders(String namespace, String path) {
        List<RDPLPack> result = new ArrayList<>();
        Entry entry = lookup(namespace, path);
        if (entry == null) { return result; }
        String lowered = isLowerCase(entry.actual) ? entry.actual : entry.actual.toLowerCase(Locale.ROOT);
        for (RDPLPack pack : packs) {
            for (String held : pack.getPaths(namespace)) {
                if (!lowered.equals(isLowerCase(held) ? held : held.toLowerCase(Locale.ROOT))) { continue; }
                result.add(pack);
                break;
            }
        }
        return result;
    }

    @Nullable public InputStream openPackFile(String name) {
        for (int i = packs.size() - 1; i >= 0; i--) {
            RDPLPack pack = packs.get(i);
            try {
                InputStream stream = pack.openPackFile(name);
                if (stream != null) { return stream; }
            }
            catch (IOException ex) {
                ContentLog.LOGGER.error("Pack '{}': could not read {}", pack.getName(), name, ex);
            }
        }
        Path file = rootFile(name);
        if (file == null) { return null; }
        try { return Files.newInputStream(file); }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not read {}", file, ex); }
        return null;
    }

    @Nullable private Path rootFile(String name) {
        Path base = root;
        if (base == null) { return null; }
        Path file = base.resolve(name);
        return Files.isRegularFile(file) ? file : null;
    }

    @Nullable public String packMeta() { return packMeta; }

    @Nullable private String resolvePackMeta() {
        for (int i = packs.size() - 1; i >= 0; i--) {
            RDPLPack pack = packs.get(i);
            String contents;
            try { contents = pack.readPackFile(PACK_META); }
            catch (IOException ex) {
                ContentLog.LOGGER.error("Pack '{}': could not read {}", pack.getName(), PACK_META, ex);
                continue;
            }
            if (contents == null) { continue; }
            if (validMeta(contents)) { return contents; }
            ContentLog.LOGGER.warn("Pack '{}': {} is not valid JSON with a 'pack' section, so it is being ignored", pack.getName(), PACK_META);
        }
        Path file = rootFile(PACK_META);
        if (file == null) { return null; }
        String contents;
        try { contents = new String(Files.readAllBytes(file), StandardCharsets.UTF_8); }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {}", file, ex);
            return null;
        }
        if (validMeta(contents)) { return contents; }
        ContentLog.LOGGER.warn("{} is not valid JSON with a 'pack' section, so it is being ignored", file);
        return null;
    }

    private static boolean validMeta(String contents) {
        try {
            JsonObject json = GSON.fromJson(contents, JsonObject.class);
            return json != null && json.has("pack") && json.get("pack").isJsonObject();
        }
        catch (RuntimeException malformed) { return false; }
    }

    @Nullable public String read(String namespace, String path, String type, String ext) {
        Entry entry = resolve(namespace, type + "/" + path + "." + ext);
        if (entry == null) { return null; }
        try {
            String contents = entry.pack.read(namespace, entry.actual);
            ContentLog.LOGGER.debug("Serving {} {}:{} from pack '{}'", type, namespace, path, entry.pack.getName());
            return contents;
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {} {}:{} from pack '{}'", type, namespace, path, entry.pack.getName(), ex);
            return null;
        }
    }

    public void forEach(String type, String ext, IPackConsumer consumer) {
        for (RDPLPack pack : packs) { pack.forEach(type, ext, consumer); }
    }

    public Set<String> getNamespaces(boolean overriding) {
        Set<String> cached = overriding ? namespacesOverride : namespacesNormal;
        if (cached != null) { return cached; }
        Set<String> all = new LinkedHashSet<>();
        for (RDPLPack pack : packs) {
            if (pack.isOverriding() == overriding) { all.addAll(pack.getNamespaces()); }
        }
        Set<String> built = Collections.unmodifiableSet(all);
        if (overriding) { namespacesOverride = built; }
        else { namespacesNormal = built; }
        return built;
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
        mergedNormal.clear();
        mergedOverride.clear();
        warned.clear();
        served.clear();
        ContentPixelMaps.forget();
        namespacesNormal = null;
        namespacesOverride = null;
        packMeta = null;
        generation.incrementAndGet();
    }

    private static final class Entry {
        final RDPLPack pack;
        final String actual;
        @Nullable final Map<String, RDPLPack> variants;

        Entry(RDPLPack pack, String actual, @Nullable Map<String, RDPLPack> variants) {
            this.pack = pack;
            this.actual = actual;
            this.variants = variants;
        }
    }
}
