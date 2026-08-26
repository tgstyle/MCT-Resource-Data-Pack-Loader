package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentPixelMaps;
import mctmods.resourcedatapackloader.core.util.ConfigCore;
import mctmods.resourcedatapackloader.pack.interfaces.IPackConsumer;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static final String README = "readme.txt";
    private static final String README_BASE = "/assets/resourcedatapackloader/readme";
    private static final String README_FALLBACK = README_BASE + "_en_us.txt";
    private static final String DISABLED = ".disabled";
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
    public static final String WORLDTEMPLATES = "worldtemplates";
    public static final String PATHINTERSECTS = "pathintersects";
    public static final String BLASTPLASTER = "blastplaster";
    public static final String WORLDINTRO = "worldintro";
    public static final String DIMENSIONS = "dimensions";
    public static final String GAMERULES = "gamerules";
    public static final String FUELS = "fuels";
    public static final String OREDICT = "oredict";
    public static final String SOUNDS = "sounds";
    public static final String RECIPE_REMOVALS = "recipe_removals";
    public static final String MATERIALS = "materials";
    public static final String LOOT_INJECTIONS = "loot_injections";
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
                    case README:
                    case "config": continue;
                }
                if (ROOT_DIRECTORY.equalsIgnoreCase(fileName)) {
                    ContentLog.LOGGER.warn("Skipping '{}': a folder named '{}' inside the pack folder is never loaded, since that is the pack folder's own name. Put its contents straight into {} or rename the pack", fileName, ROOT_DIRECTORY, packRoot);
                    continue;
                }
                if (fileName.toLowerCase(Locale.ROOT).endsWith(DISABLED)) {
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
            Path readme = packRoot.resolve(README);
            String text = readmeText();
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
        if (!Files.isDirectory(packRoot.resolve(RDPLPack.ASSETS))) { return null; }
        RDPLPack pack = new RDPLPack(ROOT_PACK, -1, Config.packs.overrideResourcePacks, packRoot, null);
        return pack.getNamespaces().isEmpty() ? null : pack;
    }

    @Nullable private RDPLPack load(Path entry) {
        String fileName = entry.getFileName().toString();
        if (ContentPixelMaps.CACHE_DIRECTORY.equals(fileName)) { return null; }
        if (Files.isDirectory(entry)) {
            if (!Files.isDirectory(entry.resolve(RDPLPack.ASSETS))) {
                ContentLog.LOGGER.warn("Skipping '{}': a pack folder must contain an '{}' directory", fileName, RDPLPack.ASSETS);
                return null;
            }
            return create(fileName, entry, null);
        }
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".zip")) { return null; }
        try {
            FileSystem zip = FileSystems.newFileSystem(entry, null);
            RDPLPack pack = create(stripExtension(fileName), zip.getPath("/"), zip);
            if (pack.getNamespaces().isEmpty()) {
                ContentLog.LOGGER.warn("Skipping '{}': no '{}' directory inside the zip", fileName, RDPLPack.ASSETS);
                zip.close();
                return null;
            }
            return pack;
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not open zip pack '{}'", fileName, ex);
            return null;
        }
    }

    private static RDPLPack create(String raw, Path root, @Nullable FileSystem owned) {
        boolean fallback = Config.packs.overrideResourcePacks;
        Matcher matcher = PRIORITY.matcher(raw);
        if (!matcher.find() || (matcher.group(1) == null && matcher.group(2) == null)) { return new RDPLPack(raw, -1, fallback, root, owned); }
        String clean = raw.substring(matcher.end());
        if (clean.isEmpty()) { clean = raw; }
        boolean overriding = tier(matcher.group(2), fallback);
        if (matcher.group(1) == null) { return new RDPLPack(clean, -1, overriding, root, owned); }
        try { return new RDPLPack(clean, Integer.parseInt(matcher.group(1)), overriding, root, owned); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.warn("Pack '{}': priority number is too large, treating the pack as unprioritised", raw);
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

    public void warnAboutDisabledFeatures() {
        if (packs.isEmpty()) { return; }
        List<String[]> off = new ArrayList<>();
        clientSide(off, Config.content.load, "content.load", JSON, BLOCKS, ITEMS, FLUIDS, MATERIALS);
        clientSide(off, Config.content.sounds, "content.sounds", JSON, SOUNDS);
        collect(off, Config.content.fuels, "content.fuels", JSON, FUELS);
        collect(off, Config.content.oreDictionary, "content.oreDictionary", JSON, OREDICT);
        clientSide(off, Config.content.potions, "content.potions", JSON, POTIONS, POTION_TYPES);
        collect(off, Config.content.brewing, "content.brewing", JSON, BREWING);
        clientSide(off, Config.content.villagers, "content.villagers", JSON, VILLAGERS, TRADES);
        clientSide(off, Config.content.biomes, "content.biomes", JSON, BIOMES);
        clientSide(off, Config.content.dimensions, "content.dimensions", JSON, DIMENSIONS);
        collect(off, Config.content.villages, "content.villages", JSON, VILLAGES);
        collect(off, Config.content.entities, "content.entities", JSON, ENTITIES);
        collect(off, Config.content.hardness, "content.hardness", JSON, HARDNESS);
        collect(off, Config.recipes.furnace, "recipes.furnace", JSON, FURNACE);
        collect(off, Config.recipes.removals, "recipes.removals", JSON, RECIPE_REMOVALS);
        collect(off, Config.data.lootInjections, "data.lootInjections", JSON, LOOT_INJECTIONS);
        collect(off, Config.data.playerLoot, "data.playerLoot", JSON, PLAYER_LOOT);
        collect(off, Config.data.registryRemaps, "data.registryRemaps", JSON, REGISTRY_REMAP);
        collect(off, Config.worldgen.load, "worldgen.load", JSON, WORLDGEN);
        collect(off, Config.data.functions, "data.functions", MCFUNCTION, FUNCTIONS);
        if (off.isEmpty()) { return; }
        for (RDPLPack pack : packs) {
            for (String[] entry : off) {
                int count = pack.count(entry[0], entry[1]);
                if (count == 0) { continue; }
                ContentLog.LOGGER.warn("Pack '{}' provides {} {} file(s), but {}, so they do nothing", pack.getName(), count, entry[0], entry[2]);
            }
        }
    }

    private static void collect(List<String[]> off, boolean enabled, String setting, String ext, String... types) {
        because(off, enabled, setting + " is off in the config", ext, types);
    }

    /** For content a client must also know about, which vanillaClients stops no matter what its own setting says. */
    private static void clientSide(List<String[]> off, boolean enabled, String setting, String ext, String... types) {
        if (Config.content.vanillaClients) {
            because(off, false, "content.vanillaClients is on and they are the sort a client would need too", ext, types);
            return;
        }
        collect(off, enabled, setting, ext, types);
    }

    private static void because(List<String[]> off, boolean enabled, String reason, String ext, String... types) {
        if (enabled) { return; }
        for (String type : types) { off.add(new String[] { type, ext, reason }); }
    }

    public void report() {
        if (packs.isEmpty()) {
            ContentLog.LOGGER.info("No packs found in {}", root);
            return;
        }
        int fromMods = 0;
        for (RDPLPack pack : packs) {
            if (pack.isFromMod()) { fromMods++; }
        }
        ContentLog.LOGGER.info("Loaded {} pack(s) from {}, lowest priority first{}", packs.size(), root,
                fromMods == 0 ? "" : ", " + fromMods + " of them shipped inside a mod jar and listed in config/mods.json");
        if (!Config.packs.logContents) { return; }
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " priority=" + pack.getPriority() : "";
            String tier = (pack.isOverriding() ? " overriding" : "") + (pack.isFromMod() ? " from a mod jar" : "");
            ContentLog.LOGGER.debug("  '{}'{}{}: files={} namespaces={} advancements={} loot_tables={} recipes={} functions={} remaps={} blocks={} items={} fluids={} furnace={} worldgen={} fuels={} oredict={} sounds={} recipe_removals={} materials={} loot_injections={} player_loot={} tabs={} potions={} potion_types={} brewing={} villagers={} trades={} biomes={} villages={} entities={} hardness={}",
                    pack.getName(), priority, tier, pack.getFileCount(), pack.getNamespaces(), pack.count(ADVANCEMENTS, JSON), pack.count(LOOT_TABLES, JSON), pack.count(RECIPES, JSON), pack.count(FUNCTIONS, MCFUNCTION), pack.count(REGISTRY_REMAP, JSON), pack.count(BLOCKS, JSON), pack.count(ITEMS, JSON), pack.count(FLUIDS, JSON), pack.count(FURNACE, JSON), pack.count(WORLDGEN, JSON), pack.count(FUELS, JSON), pack.count(OREDICT, JSON), pack.count(SOUNDS, JSON), pack.count(RECIPE_REMOVALS, JSON), pack.count(MATERIALS, JSON), pack.count(LOOT_INJECTIONS, JSON), pack.count(PLAYER_LOOT, JSON), pack.count(TABS, JSON), pack.count(POTIONS, JSON), pack.count(POTION_TYPES, JSON), pack.count(BREWING, JSON), pack.count(VILLAGERS, JSON), pack.count(TRADES, JSON), pack.count(BIOMES, JSON), pack.count(VILLAGES, JSON), pack.count(ENTITIES, JSON), pack.count(HARDNESS, JSON));
        }
    }

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

    private static boolean isData(String path) {
        return path.startsWith(ADVANCEMENTS + "/") || path.startsWith(LOOT_TABLES + "/") || path.startsWith(RECIPES + "/")
                || path.startsWith(FUNCTIONS + "/") || path.startsWith(REGISTRY_REMAP + "/") || path.startsWith(STRUCTURES + "/")
                || path.startsWith(GATES + "/") || path.startsWith(WORLDTEMPLATES + "/") || path.startsWith(PATHINTERSECTS + "/")
                || path.startsWith(BLASTPLASTER + "/") || path.startsWith(WORLDINTRO + "/") || path.startsWith(DIMENSIONS + "/") || path.startsWith(GAMERULES + "/")
                || path.startsWith(BLOCKS + "/") || path.startsWith(ITEMS + "/") || path.startsWith(FLUIDS + "/") || path.startsWith(FURNACE + "/") || path.startsWith(WORLDGEN + "/") || path.startsWith(FUELS + "/") || path.startsWith(OREDICT + "/") || path.startsWith(SOUNDS + "/") || path.startsWith(RECIPE_REMOVALS + "/") || path.startsWith(MATERIALS + "/") || path.startsWith(LOOT_INJECTIONS + "/") || path.startsWith(PLAYER_LOOT + "/") || path.startsWith(TABS + "/") || path.startsWith(POTIONS + "/") || path.startsWith(POTION_TYPES + "/") || path.startsWith(BREWING + "/") || path.startsWith(VILLAGERS + "/") || path.startsWith(TRADES + "/") || path.startsWith(BIOMES + "/") || path.startsWith(VILLAGES + "/") || path.startsWith(ENTITIES + "/") || path.startsWith(HARDNESS + "/");
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

    private static String readmeSource() {
        String language = readmeLanguage();
        if (!language.isEmpty()) {
            String scoped = README_BASE + "_" + language + ".txt";
            if (PackManager.class.getResource(scoped) != null) { return scoped; }
        }
        return README_FALLBACK;
    }

    private static String readmeLanguage() {
        File options = new File(ConfigCore.gameDir(), "options.txt");
        if (!options.isFile()) { return ""; }
        try {
            for (String line : Files.readAllLines(options.toPath(), StandardCharsets.UTF_8)) {
                if (line.startsWith("lang:")) { return line.substring(5).trim().toLowerCase(Locale.ROOT); }
            }
        }
        catch (IOException | RuntimeException unreadable) { ContentLog.LOGGER.warn("Could not read the chosen language from {}, writing the readme in English", options); }
        return "";
    }

    @Nullable private static String readmeText() {
        String source = readmeSource();
        try (InputStream stream = PackManager.class.getResourceAsStream(source)) {
            if (stream == null) { return null; }
            ByteArrayOutputStream held = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            for (int read = stream.read(buffer); read > 0; read = stream.read(buffer)) { held.write(buffer, 0, read); }
            return new String(held.toByteArray(), StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {} out of the jar", source, ex);
            return null;
        }
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
