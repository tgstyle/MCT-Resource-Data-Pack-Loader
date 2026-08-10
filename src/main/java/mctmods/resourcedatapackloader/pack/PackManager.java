package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
        RDPLPack loose = loadRoot(packRoot);
        if (loose != null) { packs.add(loose); }
        List<RDPLPack> named = new ArrayList<>();
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(packRoot)) {
            for (Path entry : entries) {
                String fileName = entry.getFileName().toString();
                switch (fileName) {
                    case RDPLPack.ASSETS:
                    case README:
                    case "config": continue;
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
            boolean missing = !Files.exists(readme);
            if (missing || !text.equals(new String(Files.readAllBytes(readme), StandardCharsets.UTF_8))) {
                Files.write(readme, text.getBytes(StandardCharsets.UTF_8));
                ContentLog.LOGGER.info("{} {}", missing ? "Wrote" : "Brought up to date", readme);
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
        collect(off, Config.content.load, "content.load", JSON, BLOCKS, ITEMS, FLUIDS, MATERIALS);
        collect(off, Config.content.sounds, "content.sounds", JSON, SOUNDS);
        collect(off, Config.content.fuels, "content.fuels", JSON, FUELS);
        collect(off, Config.content.oreDictionary, "content.oreDictionary", JSON, OREDICT);
        collect(off, Config.content.potions, "content.potions", JSON, POTIONS, POTION_TYPES);
        collect(off, Config.content.brewing, "content.brewing", JSON, BREWING);
        collect(off, Config.content.villagers, "content.villagers", JSON, VILLAGERS, TRADES);
        collect(off, Config.content.biomes, "content.biomes", JSON, BIOMES);
        collect(off, Config.content.dimensions, "content.dimensions", JSON, DIMENSIONS);
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
                ContentLog.LOGGER.warn("Pack '{}' provides {} {} file(s), but {} is off in the config, so they do nothing", pack.getName(), count, entry[0], entry[2]);
            }
        }
    }

    private static void collect(List<String[]> off, boolean enabled, String setting, String ext, String... types) {
        if (enabled) { return; }
        for (String type : types) { off.add(new String[] { type, ext, setting }); }
    }

    public void report() {
        if (packs.isEmpty()) {
            ContentLog.LOGGER.info("No packs found in {}", root);
            return;
        }
        ContentLog.LOGGER.info("Loaded {} pack(s) from {}, lowest priority first", packs.size(), root);
        if (!Config.packs.logContents) { return; }
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " priority=" + pack.getPriority() : "";
            String tier = pack.isOverriding() ? " overriding" : "";
            ContentLog.LOGGER.info("  '{}'{}{}: files={} namespaces={} advancements={} loot_tables={} recipes={} functions={} remaps={} blocks={} items={} fluids={} furnace={} worldgen={} fuels={} oredict={} sounds={} recipe_removals={} materials={} loot_injections={} player_loot={} tabs={} potions={} potion_types={} brewing={} villagers={} trades={} biomes={} villages={} entities={} hardness={}",
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

    public boolean existsRaw(String namespace, String path, boolean overriding) { return resolve(namespace, path, overriding) != null; }

    @Nullable public InputStream openRaw(String namespace, String path) throws IOException {
        Entry entry = resolve(namespace, path);
        if (entry == null) { return null; }
        return entry.pack.open(namespace, entry.actual);
    }

    @Nullable public InputStream openRaw(String namespace, String path, boolean overriding) throws IOException {
        Entry entry = resolve(namespace, path, overriding);
        if (entry == null) { return null; }
        return entry.pack.open(namespace, entry.actual);
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

    private static String readmeText() {
        return String.join("\n",
                "Resource Data Pack Loader",
                "=========================",
                "",
                "Anything you put in this folder replaces what a mod or Minecraft itself provides.",
                "It applies to every world, in singleplayer and on servers, and there is nothing",
                "to switch on.",
                "",
                "",
                "MORE THAN OVERRIDES",
                "-------------------",
                "",
                "Packs here can also define new blocks, items, biomes and whole dimensions from",
                "JSON files, decide what generates and where, lock dimensions behind a key or a",
                "mob that must be slain, and make a world's land ahead of time so nobody ever",
                "waits on a chunk. HOWTO.md, shipped alongside the mod, covers all of it.",
                "",
                "",
                "HOW TO ADD A FILE",
                "-----------------",
                "",
                "Open the mod's jar, find the file you want to change, and copy its path from",
                "'assets' onwards.",
                "",
                "To replace the iron ore texture, the file inside the Minecraft jar is:",
                "",
                "    assets/minecraft/textures/blocks/iron_ore.png",
                "",
                "so your version goes here:",
                "",
                "    rdploader/assets/minecraft/textures/blocks/iron_ore.png",
                "",
                "That is the whole rule. The path after 'assets' is always the same as the path",
                "inside the jar, so nothing ever needs renaming or moving.",
                "",
                "",
                "KEEPING THINGS TIDY",
                "-------------------",
                "",
                "You can group files into a named pack instead, as a folder or a zip:",
                "",
                "    rdploader/MyTextures/assets/minecraft/textures/blocks/iron_ore.png",
                "    rdploader/MyTextures.zip        (with 'assets' at the top level of the zip)",
                "",
                "When zipping, select the contents and zip those, not the folder holding them.",
                "A zip whose top level is a single folder wrapping 'assets' is skipped, and the",
                "log says so.",
                "",
                "Folders are easier to edit while you work, zips are easier to hand to someone",
                "else. They behave the same.",
                "",
                "If the same file exists in two places, a named pack wins over loose files. The",
                "log names the pack every file came from, so you can always see which one won.",
                "",
                "",
                "PACK PRIORITY",
                "-------------",
                "",
                "If two named packs contain the same file, control which one wins by prepending",
                "RDPL and a number to the folder or zip name. RDPL0 loads first, higher numbers",
                "load later, and the pack loaded last wins:",
                "",
                "    rdploader/RDPL0 BaseTextures.zip",
                "    rdploader/RDPL1 SeasonalTextures",
                "",
                "Upper or lower case both work, and a space, dash or underscore after the number",
                "is optional. The prefix is stripped from the pack's name in the log and in",
                "/rdpl list, so RDPL1 SeasonalTextures shows up as SeasonalTextures.",
                "",
                "",
                "RESOURCE PACKS",
                "--------------",
                "",
                "By default the files here sit above the resource packs the player picks in the",
                "options screen, so a resource pack cannot override them. That is right for",
                "things like a modpack logo and wrong for textures you would like people to be",
                "able to reskin.",
                "",
                "Add O or N after the RDPL prefix to decide per pack:",
                "",
                "    rdploader/RDPLO Branding          always wins, resource packs cannot touch it",
                "    rdploader/RDPLN BaseTextures      a resource pack can override it",
                "    rdploader/RDPL1O Seasonal         priority and always wins, both together",
                "",
                "Packs with no letter follow the overrideResourcePacks option in the config, and",
                "/rdpl list marks the ones that override.",
                "",
                "Packs without a prefix load before all numbered packs, in alphabetical order,",
                "so a numbered pack always wins over an unnumbered one.",
                "",
                "To turn a pack off without deleting it, add .disabled to the end of its name:",
                "",
                "    rdploader/RDPL1 SeasonalTextures.zip.disabled",
                "",
                "The pack is skipped and the log says so. Remove the suffix to turn it back on.",
                "",
                "",
                "WHAT YOU CAN CHANGE",
                "-------------------",
                "",
                "Textures, models, blockstates, language files, sounds, fonts, splash texts, and",
                "anything else a mod keeps in its assets folder, such as guide books or manuals.",
                "",
                "Advancements and loot tables. These are server side, so they work on a dedicated",
                "server as well.",
                "",
                "Structure templates, the .nbt files under assets/<modid>/structures. A structure",
                "saved in the world's own structures folder still wins over a file here, and a",
                "structure that has already been placed stays loaded until you leave the world.",
                "",
                "Recipes, including replacing a mod's recipe or adding one of your own. Recipes",
                "only load when the game starts, so a change here needs a restart rather than a",
                "reload.",
                "",
                "Functions, the .mcfunction files under assets/<modid>/functions. Minecraft only",
                "reads these from the world's own data folder, so putting them here makes them",
                "work in every world. A function saved in the world still wins over a file here.",
                "",
                "Registry renames, so a world saved before a mod renamed one of its blocks keeps",
                "that block instead of losing it. Put a file in assets/<modid>/registry_remap:",
                "",
                "    {",
                "      \"registry\": \"minecraft:items\",",
                "      \"mapping\": { \"oldmod:old_name\": \"newmod:new_name\" }",
                "    }",
                "",
                "The registry is the one the entry belongs to, usually minecraft:items or",
                "minecraft:blocks. Renames chain, so mapping A to B and later B to C sends A to C.",
                "",
                "Player loot, a thing the game has no name for at all. Players drop their",
                "inventory and nothing else, so a file in assets/<modid>/player_loot gives them",
                "a loot table of their own:",
                "",
                "    {",
                "      \"table\": \"mypack:entities/player\",",
                "      \"mode\": \"add\",",
                "      \"rollOnKeepInventory\": false",
                "    }",
                "",
                "\"add\" drops what the table rolls alongside everything they were carrying,",
                "\"replace\" drops it instead of their inventory, and rollOnKeepInventory decides",
                "whether the table is rolled at all on a world where inventories are kept.",
                "",
                "CraftTweaker and GroovyScript still work exactly as before. They run after this",
                "mod, so anything your scripts remove or change wins over a file here.",
                "",
                "RDPL is good for replacing one or two recipes, and recipes for your own",
                "content belong in the pack alongside it. For full recipe control across a",
                "modpack, CraftTweaker and GroovyScript are better options. A file here",
                "replaces the original completely, so to change one ingredient or drop one",
                "loot entry, use those.",
                "",
                "",
                "ADDING NEW CONTENT",
                "------------------",
                "",
                "A pack can also add blocks, items and fluids of its own, described as JSON. You",
                "do not need to write or build a mod for this.",
                "",
                "The file's path is its name. A block at",
                "",
                "    rdploader/MyPack/assets/mypack/blocks/copper_ore.json",
                "",
                "registers as mypack:copper_ore. There is no name field to fill in or get wrong.",
                "If a real mod already registers that name, the mod wins and your file is skipped.",
                "",
                "The simplest block is a few lines:",
                "",
                "    {",
                "      \"type\": \"ore\",",
                "      \"material\": \"rock\",",
                "      \"harvestTool\": \"pickaxe\",",
                "      \"variants\": {",
                "        \"copper_ore\": { \"meta\": 0, \"hardness\": 3.0, \"harvestLevel\": 1 }",
                "      }",
                "    }",
                "",
                "You still supply the model, blockstate, texture and language entry the same way",
                "as any other file in this folder.",
                "",
                "Each of these is a folder under assets/<yourpack>:",
                "",
                "    blocks           items            fluids           materials",
                "    worldgen         furnace          fuels            oredict",
                "    sounds           tabs             recipes          recipe_removals",
                "    loot_tables      loot_injections  advancements     functions",
                "    structures       registry_remap   potions          potion_types",
                "    brewing          villagers        trades           biomes",
                "    villages         entities         gates            dimensions",
                "    gamerules        worldtemplates   worldintro       pathintersects",
                "    hardness         blastplaster     player_loot",
                "",
                "Blocks come in these shapes, set by the \"type\" field:",
                "",
                "    basic   ore     falling   slab    stairs   fence    door",
                "    pane    wall    ladder    torch   crop     flower   cane",
                "    log     leaves  sapling   vine    portal   trapdoor fence_gate",
                "",
                "and items in these:",
                "",
                "    basic   food    drink     potion  tool     armor    seed",
                "    potion_bottle",
                "",
                "A potion type always appears on the vanilla potion, splash potion, lingering",
                "potion and tipped arrow, which live in the Brewing and Combat tabs. The tab is",
                "a property of the item, not of the potion type, so there is no way to move them",
                "into your own tab. A potion_bottle item is your own container instead: it takes",
                "a creativeTab like any other item, lists the potion types you name in",
                "potionTypes, and the brewing stand accepts it wherever a glass bottle works.",
                "",
                "A villagers/<name>.json file defines a profession and the careers it offers.",
                "A trades/*.json file adds trades to any career, whether yours or one of",
                "Minecraft's, naming the profession, the career and the level the trade appears",
                "at. Name a career that does not exist and the log lists the ones that do.",
                "",
                "An entities/<name>.json file makes a new entity out of one that is already here.",
                "It names the entity to build on, and what is different about it: its name, its skin,",
                "how much health and damage it has, how fast it moves and how high it jumps, how big",
                "it is drawn, what it wears, what it hunts and what it ignores, and whether it",
                "still obeys the spawn rules of the entity it was built from. It is an entity of",
                "its own, with its own spawn egg and loot table, and the one it was built from is",
                "left alone. A village plot can be told to house one instead of a villager.",
                "",
                "A villages/<name>.json file adds a plot villages can build, either a farm you",
                "describe or one of your .nbt templates. The same settings choose which vanilla",
                "pieces still appear, how far apart villages are seeded, and which biomes they",
                "are allowed in.",
                "",
                "A worldintro/<name>.json file plays a run of pages when somebody enters the",
                "world, before they take control: scrolling text over a picture, a title card, a",
                "slideshow, with music behind it if you want. The words are plain .txt files",
                "under assets/<yourpack>/texts. It can play once per player or on every join.",
                "",
                "WHOLE WORLDS",
                "------------",
                "",
                "A pack is not limited to single things. dimensions/<name>.json registers a",
                "dimension with its own terrain, biomes and sky. gates/<name>.json puts a",
                "condition on reaching one, such as holding or spending an item. A block of",
                "type portal sends whoever walks in, and remembers who built it.",
                "",
                "A world template can also shape the overworld itself, setting sea level, lava",
                "oceans and the terrain noise. That is applied as a world is created and never",
                "afterwards, so a world that already exists is left as it was.",
                "",
                "worldtemplates/<name>.json gathers a world's settings into one file, so a pack",
                "can ship a whole world shape at once instead of asking for a dozen config",
                "edits. Every group it can set also answers to the control category in the",
                "config, which decides whether the pack decides, the config decides, or the",
                "group is off entirely and no pack can turn it on.",
                "",
                "worldgen is more than ore. An entry is a shape placed by a spread: blobs, long",
                "veins, plates, geodes, bowls, spires, nodules, vents, surface decoration, whole",
                "trees, vines, belts that span several chunks, or one of your own .nbt templates,",
                "spread evenly, around a height, fractally, along the terrain, on cave floors or",
                "ceilings, or under water.",
                "",
                "",
                "A biomes/<name>.json file defines a biome: its climate and colors, the blocks",
                "it is made of, what decorates it, what spawns in it, and where it generates.",
                "Its number is chosen for you and written into each world the first time that",
                "world loads, so it stays put afterwards no matter what else you install. Set",
                "\"id\" only when a biome has to keep a number something else already used, such",
                "as when a pack replaces a mod that is being retired. Renaming or deleting a",
                "biome a world already contains loses it, the same as renumbering a block, so",
                "use registry_remap for a rename instead.",
                "",
                "A villager's displayed name is the lang key entity.Villager.<career>, using the",
                "career name exactly as you wrote it and nothing else. That key space is shared",
                "with Minecraft and every other pack, so put your namespace in the career name,",
                "as in rdpltest.prospector. Only the name is affected: a villager stores its",
                "career as a number, so renaming one changes what existing villagers are",
                "called, and reordering the careers list changes which career they have.",
                "",
                "A potion type is named for its file the same way, and its displayed name comes",
                "from the lang key potion.effect.<namespace>.<name>, with splash_potion.effect.,",
                "lingering_potion.effect. and tipped_arrow.effect. for the other three forms.",
                "",
                "",
                "A WARNING ABOUT META",
                "--------------------",
                "",
                "Every variant has a meta number, and that number is what the world file stores.",
                "Renumbering a variant people already have in a world turns their blocks into",
                "something else. Add new variants on the end and never renumber an old one.",
                "",
                "A block holds 16 variants, because that is what four bits of metadata allows.",
                "Slabs get 8, since one bit says top or bottom, and stairs, ladders, torches and",
                "crops get 1, because facing or age uses the rest. Items are not as tight and",
                "can skip numbers.",
                "",
                "",
                "WHERE THIS STOPS",
                "----------------",
                "",
                "This describes what a thing is, not what it does over time. Anything needing a",
                "tile entity, a GUI, an inventory or code running every tick still needs a real",
                "mod. A machine is out of reach; an ore, a fence, a food or a fluid is not.",
                "",
                "",
                "SEEING YOUR CHANGES",
                "-------------------",
                "",
                "Press F3+T to reload textures, models, language files, advancements and loot",
                "tables. On a server, type /reload for the same thing. Recipes are the exception,",
                "as above: they only load at startup, so a recipe change needs a restart.",
                "",
                "If you add a new file or delete one, use /rdpl reload instead. Editing a file",
                "that was already there only needs F3+T.",
                "",
                "/rdpl reload textures reloads only textures, which is much faster than F3+T in a",
                "large pack. models, languages, sounds and shaders work the same way. Leave the",
                "name off to rescan the folder and reload everything.",
                "",
                "/rdpl list shows every pack loaded and what is in it. Click a pack to see it.",
                "",
                "/rdpl which minecraft:textures/blocks/stone.png shows which pack serves a file",
                "and which packs are shadowed underneath it.",
                "",
                "These work without being an operator, because they only read files on your own",
                "computer. On a dedicated server, /rdplserver reload rescans the server's copy.",
                "",
                "",
                "IF SOMETHING DOES NOT WORK",
                "--------------------------",
                "",
                "Check the log first. Advancements, loot tables, recipes, functions and",
                "structures are logged with the pack they came from, and anything wrong is logged",
                "as a warning saying why.",
                "",
                "For textures and other assets, /rdpl unused lists any file in your packs that",
                "nothing has asked for yet, which usually means a typo in the path. Run it after",
                "the game has finished loading, and bear in mind some files only load when they",
                "are needed, such as languages other than the one you play in.",
                "",
                "Capital letters matter. If your file is Stone.png and the game asked for",
                "stone.png, it still loads, but a warning tells you to rename it. Do rename it,",
                "because anywhere other than this mod the file will not be found at all.",
                "Language files trip people up most often: they are en_us.lang, not en_US.lang.",
                "",
                "Check your files sit inside an 'assets' folder. A pack folder or zip without one",
                "is skipped, and the log says so.",
                "",
                "",
                "",
                "ADVANCEMENTS AND RECIPES",
                "------------------------",
                "",
                "If your scripts remove a recipe, any advancement that unlocked it keeps working",
                "instead of breaking. It just has no recipe left to give you, and the log names it",
                "once.",
                "",
                "If you replaced that recipe with a new one and you want the advancement to unlock",
                "the new one, give the new recipe a name in your script:",
                "",
                "    recipes.addShaped(\"rail\", <minecraft:rail> * 16, [[...]]);",
                "",
                "That registers it as crafttweaker:rail. Then drop an advancement file in here",
                "pointing at that name, and the advancement works end to end again.",
                "",
                "Without a name it gets called something like crafttweaker:ct_shaped-1834729103,",
                "a hash of the recipe itself. That changes the moment you edit the recipe, and it",
                "can shift if another recipe is added before it, so it is not safe to point an",
                "advancement at.",
                "",
                "",
                "The rdploader folder itself can be moved or renamed with the rootDirectory option",
                "in config/mct_resourcedatapackloader_mixin.cfg. An absolute path works too, and",
                "a restart is required.",
                "",
                "Put a pack.png next to this file to give the pack an icon.",
                "",
                "This file is written by the mod and brought up to date whenever it changes,",
                "so anything you type into it is replaced the next time the game starts.",
                "");
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
            ContentLog.LOGGER.info("Serving {} {}:{} from pack '{}'", type, namespace, path, entry.pack.getName());
            return contents;
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {} {}:{} from pack '{}'", type, namespace, path, entry.pack.getName(), ex);
            return null;
        }
    }

    public void forEach(String type, String ext, PackConsumer consumer) {
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
