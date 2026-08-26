package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.ModJars;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;

public final class ModPacks {
    static final String IN_JAR = PackManager.ROOT_DIRECTORY;
    private static final String ASSETS_PREFIX = IN_JAR + "/" + RDPLPack.ASSETS + "/";
    private static final String MCMOD_INFO = "mcmod.info";
    private static final String CONTROL_FILE = "mods.json";
    private static final String ENABLED = "enabled";
    private static final String PRIORITY = "priority";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ModPacks() {}

    public static List<RDPLPack> load(Path packRoot) {
        List<Found> found = new ArrayList<>();
        for (File jar : ModJars.list()) { collect(jar, found); }
        if (found.isEmpty()) { return Collections.emptyList(); }
        JsonObject control = readControl(packRoot);
        List<RDPLPack> packs = new ArrayList<>();
        for (Found entry : found) {
            JsonObject settings = settingsFor(control, entry.modId);
            if (!settings.get(ENABLED).getAsBoolean()) {
                ContentLog.LOGGER.info("Mod pack '{}' is turned off in {}/{}", entry.modId, "config", CONTROL_FILE);
                continue;
            }
            RDPLPack pack = open(entry, settings.get(PRIORITY).getAsInt());
            if (pack != null) { packs.add(pack); }
        }
        writeControl(packRoot, control);
        return packs;
    }

    private static void collect(File jar, List<Found> found) {
        try (ZipFile zip = new ZipFile(jar)) {
            if (!carriesPack(zip)) { return; }
            Set<String> declared = declaredModIds(zip);
            if (declared.isEmpty()) {
                ContentLog.LOGGER.warn("'{}' carries a '{}' folder but declares no mod id in {}, so its content cannot be attributed to a namespace and is ignored", jar.getName(), IN_JAR, MCMOD_INFO);
                return;
            }
            found.add(new Found(jar, declared.iterator().next(), declared));
        }
        catch (IOException | RuntimeException unreadable) {
            ContentLog.LOGGER.debug("Could not read '{}' while looking for a '{}' folder", jar.getName(), IN_JAR, unreadable);
        }
    }

    private static boolean carriesPack(ZipFile zip) {
        java.util.Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            if (entries.nextElement().getName().startsWith(ASSETS_PREFIX)) { return true; }
        }
        return false;
    }

    private static Set<String> declaredModIds(ZipFile zip) {
        ZipEntry entry = zip.getEntry(MCMOD_INFO);
        if (entry == null) { return Collections.emptySet(); }
        Set<String> ids = new LinkedHashSet<>();
        try (InputStream in = zip.getInputStream(entry); Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonElement parsed = new JsonParser().parse(reader);
            JsonArray list = null;
            if (parsed.isJsonArray()) { list = parsed.getAsJsonArray(); }
            else if (parsed.isJsonObject() && parsed.getAsJsonObject().has("modList")) { list = parsed.getAsJsonObject().getAsJsonArray("modList"); }
            if (list == null) { return ids; }
            for (JsonElement element : list) {
                if (!element.isJsonObject()) { continue; }
                JsonElement id = element.getAsJsonObject().get("modid");
                if (id != null && id.isJsonPrimitive()) { ids.add(id.getAsString()); }
            }
        }
        catch (IOException | RuntimeException unreadable) { ContentLog.LOGGER.debug("Could not read {} from '{}'", MCMOD_INFO, zip.getName(), unreadable); }
        return ids;
    }

    @Nullable private static RDPLPack open(Found entry, int priority) {
        try {
            FileSystem jar = FileSystems.newFileSystem(entry.jar.toPath(), null);
            Path root = jar.getPath("/" + IN_JAR);
            RDPLPack pack = new RDPLPack(entry.modId, priority, false, root, jar, entry.namespaces);
            if (pack.getNamespaces().isEmpty()) {
                ContentLog.LOGGER.warn("Mod pack '{}' has a '{}' folder but nothing usable under '{}/{}'", entry.modId, IN_JAR, IN_JAR, RDPLPack.ASSETS);
                jar.close();
                return null;
            }
            return pack;
        }
        catch (IOException | RuntimeException failed) {
            ContentLog.LOGGER.error("Could not open the '{}' folder inside '{}'", IN_JAR, entry.jar.getName(), failed);
            return null;
        }
    }

    private static JsonObject settingsFor(JsonObject control, String modId) {
        JsonElement held = control.get(modId);
        JsonObject settings = held != null && held.isJsonObject() ? held.getAsJsonObject() : new JsonObject();
        if (!settings.has(ENABLED) || !settings.get(ENABLED).isJsonPrimitive()) { settings.addProperty(ENABLED, true); }
        if (!settings.has(PRIORITY) || !settings.get(PRIORITY).isJsonPrimitive()) { settings.addProperty(PRIORITY, -1); }
        control.add(modId, settings);
        return settings;
    }

    private static JsonObject readControl(Path packRoot) {
        Path file = packRoot.resolve("config").resolve(CONTROL_FILE);
        if (!Files.isRegularFile(file)) { return new JsonObject(); }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement parsed = new JsonParser().parse(reader);
            return parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();
        }
        catch (IOException | RuntimeException ex) {
            ContentLog.LOGGER.error("Could not read {}, treating every mod pack as on", file, ex);
            return new JsonObject();
        }
    }

    private static void writeControl(Path packRoot, JsonObject control) {
        Path file = packRoot.resolve("config").resolve(CONTROL_FILE);
        String text = GSON.toJson(control) + System.lineSeparator();
        try {
            if (Files.isRegularFile(file) && text.equals(new String(Files.readAllBytes(file), StandardCharsets.UTF_8))) { return; }
            Files.createDirectories(file.getParent());
            Files.write(file, text.getBytes(StandardCharsets.UTF_8));
            ContentLog.LOGGER.info("Wrote {}, where each mod that ships a '{}' folder can be turned off or given a priority", file, IN_JAR);
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not write {}", file, ex); }
    }

    private static final class Found {
        private final File jar;
        private final String modId;
        private final Set<String> namespaces;

        private Found(File jar, String modId, Set<String> namespaces) {
            this.jar = jar;
            this.modId = modId;
            this.namespaces = namespaces;
        }
    }
}
