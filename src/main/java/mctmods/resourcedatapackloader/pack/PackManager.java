package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.Config;
import mctmods.resourcedatapackloader.core.MCTMixin;

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
    public static final String ROOT_PACK = "<loose files>";
    private static final String README = "readme.txt";
    private static final String DISABLED = ".disabled";
    public static final String ADVANCEMENTS = "advancements";
    public static final String LOOT_TABLES = "loot_tables";
    public static final String RECIPES = "recipes";
    public static final String JSON = "json";
    private static final Pattern PRIORITY = Pattern.compile("^[Rr][Dd][Pp][Ll](\\d+)[ _-]?");
    private static final PackManager INSTANCE = new PackManager();
    private final List<RDPLPack> packs = new CopyOnWriteArrayList<>();
    private final Map<String, Map<String, Entry>> merged = new ConcurrentHashMap<>();
    private final Set<String> warned = ConcurrentHashMap.newKeySet();
    private volatile Path root;
    @Nullable private volatile Set<String> namespaces;
    private final AtomicInteger generation = new AtomicInteger();

    private PackManager() {}

    public static PackManager get() { return INSTANCE; }

    public boolean isEmpty() { return packs.isEmpty(); }

    public List<RDPLPack> getPacks() { return Collections.unmodifiableList(packs); }

    @Nullable public Path getRoot() { return root; }

    public int getGeneration() { return generation.get(); }

    public void scan(Path packRoot) {
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
                if (RDPLPack.ASSETS.equals(fileName)) { continue; }
                if (README.equals(fileName)) { continue; }
                if (fileName.toLowerCase(Locale.ROOT).endsWith(DISABLED)) {
                    MCTMixin.LOGGER.info("Skipping disabled pack '{}'", fileName);
                    continue;
                }
                RDPLPack pack = load(entry);
                if (pack != null) { named.add(pack); }
            }
        }
        catch (IOException | UncheckedIOException ex) {
            MCTMixin.LOGGER.error("Could not scan {}", packRoot, ex);
        }
        named.sort(Comparator.comparingInt(RDPLPack::getPriority).thenComparing(RDPLPack::getName, String.CASE_INSENSITIVE_ORDER));
        packs.addAll(named);
        buildIndex();
        namespaces = null;
    }

    private void buildIndex() {
        for (RDPLPack pack : packs) {
            for (String namespace : pack.getNamespaces()) {
                Map<String, Entry> paths = merged.computeIfAbsent(namespace, k -> new ConcurrentHashMap<>());
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
            Path readme = packRoot.resolve(README);
            if (!Files.exists(readme)) {
                Files.write(readme, readmeText().getBytes(StandardCharsets.UTF_8));
                MCTMixin.LOGGER.info("Wrote {}", readme);
            }
        }
        catch (IOException ex) {
            MCTMixin.LOGGER.error("Could not prepare {}", packRoot, ex);
        }
    }

    @Nullable private RDPLPack loadRoot(Path packRoot) {
        if (!Files.isDirectory(packRoot.resolve(RDPLPack.ASSETS))) { return null; }
        RDPLPack pack = new RDPLPack(ROOT_PACK, -1, packRoot, null);
        return pack.getNamespaces().isEmpty() ? null : pack;
    }

    @Nullable private RDPLPack load(Path entry) {
        String fileName = entry.getFileName().toString();
        if (Files.isDirectory(entry)) {
            if (!Files.isDirectory(entry.resolve(RDPLPack.ASSETS))) {
                MCTMixin.LOGGER.warn("Skipping '{}': a pack folder must contain an '{}' directory", fileName, RDPLPack.ASSETS);
                return null;
            }
            return create(fileName, entry, null);
        }
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".zip")) { return null; }
        try {
            FileSystem zip = FileSystems.newFileSystem(entry, null);
            RDPLPack pack = create(stripExtension(fileName), zip.getPath("/"), zip);
            if (pack.getNamespaces().isEmpty()) {
                MCTMixin.LOGGER.warn("Skipping '{}': no '{}' directory inside the zip", fileName, RDPLPack.ASSETS);
                zip.close();
                return null;
            }
            return pack;
        }
        catch (IOException ex) {
            MCTMixin.LOGGER.error("Could not open zip pack '{}'", fileName, ex);
            return null;
        }
    }

    private static RDPLPack create(String raw, Path root, @Nullable FileSystem owned) {
        Matcher matcher = PRIORITY.matcher(raw);
        if (!matcher.find()) { return new RDPLPack(raw, -1, root, owned); }
        String clean = raw.substring(matcher.end());
        if (clean.isEmpty()) { clean = raw; }
        try { return new RDPLPack(clean, Integer.parseInt(matcher.group(1)), root, owned); }
        catch (NumberFormatException ex) {
            MCTMixin.LOGGER.warn("Pack '{}': priority number is too large, treating the pack as unprioritised", raw);
            return new RDPLPack(raw, -1, root, owned);
        }
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }

    public void report() {
        if (packs.isEmpty()) {
            MCTMixin.LOGGER.info("No packs found in {}", root);
            return;
        }
        MCTMixin.LOGGER.info("Loaded {} pack(s) from {}, lowest priority first", packs.size(), root);
        if (!Config.settings.logPackContents) { return; }
        for (RDPLPack pack : packs) {
            String priority = pack.getPriority() >= 0 ? " priority=" + pack.getPriority() : "";
            MCTMixin.LOGGER.info("  '{}'{}: files={} namespaces={} advancements={} loot_tables={} recipes={}",
                    pack.getName(), priority, pack.getFileCount(), pack.getNamespaces(), pack.count(ADVANCEMENTS, JSON), pack.count(LOOT_TABLES, JSON), pack.count(RECIPES, JSON));
        }
    }

    @Nullable private Entry resolve(String namespace, String path) {
        Map<String, Entry> paths = merged.get(namespace);
        if (paths == null) { return null; }
        Entry entry = paths.get(isLowerCase(path) ? path : path.toLowerCase(Locale.ROOT));
        if (entry == null) { return null; }
        if (entry.actual.equals(path)) { return entry; }
        if (entry.variants != null && entry.variants.get(path) == entry.pack) { return new Entry(entry.pack, path, null); }
        reportCaseMismatch(namespace, path, entry);
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
        if (!Config.settings.warnOnCaseMismatch) { return; }
        if (!warned.add(namespace + ":" + requested)) { return; }
        MCTMixin.LOGGER.warn("Pack '{}': loading {}:{} from '{}', the filename case does not match. Rename it to '{}' so it also works outside this mod.", entry.pack.getName(), namespace, requested, entry.actual, requested);
    }

    public boolean existsRaw(String namespace, String path) { return resolve(namespace, path) != null; }

    @Nullable public InputStream openRaw(String namespace, String path) throws IOException {
        Entry entry = resolve(namespace, path);
        if (entry == null) { return null; }
        return entry.pack.open(namespace, entry.actual);
    }

    @Nullable public String getPackName(String namespace, String path) {
        Entry entry = resolve(namespace, path);
        return entry == null ? null : entry.pack.getName();
    }

    public List<RDPLPack> holders(String namespace, String path) {
        List<RDPLPack> result = new ArrayList<>();
        Entry entry = resolve(namespace, path);
        if (entry == null) { return result; }
        for (RDPLPack pack : packs) {
            if (pack.getPaths(namespace).contains(entry.actual)) { result.add(pack); }
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
                "CraftTweaker and GroovyScript still work exactly as before. They run after this",
                "mod, so anything your scripts remove or change wins over a file here.",
                "",
                "Bear in mind a file here replaces the original completely. If you only want to",
                "drop one item from a loot table or tweak one ingredient, CraftTweaker is the",
                "better tool.",
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
                "/rdpl list shows every pack loaded and what is in it.",
                "",
                "/rdpl which minecraft:textures/blocks/stone.png shows which pack serves a file",
                "and which packs are shadowed underneath it.",
                "",
                "",
                "IF SOMETHING DOES NOT WORK",
                "--------------------------",
                "",
                "Check the log first. Every file that gets used is logged along with the pack it",
                "came from, and anything wrong is logged as a warning saying why.",
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
                "Delete this file and a fresh copy appears the next time the game starts.",
                "");
    }

    @Nullable public String readPackFile(String name) {
        for (int i = packs.size() - 1; i >= 0; i--) {
            RDPLPack pack = packs.get(i);
            try {
                String contents = pack.readPackFile(name);
                if (contents != null) { return contents; }
            }
            catch (IOException ex) {
                MCTMixin.LOGGER.error("Pack '{}': could not read {}", pack.getName(), name, ex);
            }
        }
        return null;
    }

    @Nullable public String read(String namespace, String path, String type, String ext) {
        Entry entry = resolve(namespace, type + "/" + path + "." + ext);
        if (entry == null) { return null; }
        try {
            String contents = entry.pack.read(namespace, entry.actual);
            MCTMixin.LOGGER.info("Serving {} {}:{} from pack '{}'", type, namespace, path, entry.pack.getName());
            return contents;
        }
        catch (IOException ex) {
            MCTMixin.LOGGER.error("Could not read {} {}:{} from pack '{}'", type, namespace, path, entry.pack.getName(), ex);
            return null;
        }
    }

    public void forEach(String type, String ext, PackConsumer consumer) {
        for (RDPLPack pack : packs) { pack.forEach(type, ext, consumer); }
    }

    public Set<String> getNamespaces() {
        Set<String> cached = namespaces;
        if (cached != null) { return cached; }
        Set<String> all = new LinkedHashSet<>();
        for (RDPLPack pack : packs) { all.addAll(pack.getNamespaces()); }
        Set<String> built = Collections.unmodifiableSet(all);
        namespaces = built;
        return built;
    }

    public void close() {
        for (RDPLPack pack : packs) {
            try { pack.close(); }
            catch (IOException ex) { MCTMixin.LOGGER.error("Could not close pack '{}'", pack.getName(), ex); }
        }
        packs.clear();
        merged.clear();
        warned.clear();
        namespaces = null;
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
