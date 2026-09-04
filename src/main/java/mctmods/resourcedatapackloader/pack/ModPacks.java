package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class ModPacks {
    static final String IN_JAR = PackManager.ROOT_DIRECTORY;
    static final String CONTROL_FILE = "mods.json";
    private static final String MODS_TOML = "META-INF/neoforge.mods.toml";
    private static final String ENABLED = "enabled";
    private static final String PRIORITY = "priority";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ModPacks() {}

    public static List<RDPLPack> load(Path packRoot) {
        List<Found> found = new ArrayList<>();
        for (IModFileInfo info : ModList.get().getModFiles()) { collect(info, found); }
        if (found.isEmpty()) { return Collections.emptyList(); }
        JsonObject control = readControl(packRoot);
        List<RDPLPack> packs = new ArrayList<>();
        for (Found entry : found) {
            JsonObject settings = settingsFor(control, entry.modId());
            if (!settings.get(ENABLED).getAsBoolean()) {
                ContentLog.LOGGER.info("Mod pack '{}' is turned off in {}/{}", entry.modId(), PackManager.CONFIG, CONTROL_FILE);
                continue;
            }
            RDPLPack pack = open(entry, settings.get(PRIORITY).getAsInt());
            if (pack != null) { packs.add(pack); }
        }
        writeControl(packRoot, control);
        return packs;
    }

    private static void collect(IModFileInfo info, List<Found> found) {
        String fileName = info.getFile().getFileName();
        try {
            Path home = info.getFile().findResource(IN_JAR);
            if (!Files.isDirectory(home)) { return; }
            Set<String> declared = new LinkedHashSet<>();
            for (IModInfo mod : info.getMods()) { declared.add(mod.getModId()); }
            if (declared.isEmpty()) {
                ContentLog.LOGGER.warn("'{}' carries a '{}' folder but declares no mod id in {}, so its content cannot be attributed to a namespace and is ignored", fileName, IN_JAR, MODS_TOML);
                return;
            }
            found.add(new Found(home, fileName, declared.iterator().next(), declared));
        }
        catch (RuntimeException unreadable) {
            ContentLog.LOGGER.debug("Could not look inside '{}' for a '{}' folder", fileName, IN_JAR, unreadable);
        }
    }

    @Nullable private static RDPLPack open(Found entry, int priority) {
        try {
            RDPLPack pack = new RDPLPack(entry.modId(), priority, false, entry.home(), null, entry.namespaces());
            if (pack.isEmpty()) {
                ContentLog.LOGGER.warn("Mod pack '{}' has a '{}' folder but nothing usable under '{}/{}' or '{}/{}'", entry.modId(), IN_JAR, IN_JAR, RDPLPack.ASSETS, IN_JAR, RDPLPack.DATA);
                return null;
            }
            return pack;
        }
        catch (RuntimeException failed) {
            ContentLog.LOGGER.error("Could not open the '{}' folder inside '{}'", IN_JAR, entry.fileName(), failed);
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
        Path file = packRoot.resolve(PackManager.CONFIG).resolve(CONTROL_FILE);
        if (!Files.isRegularFile(file)) { return new JsonObject(); }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            return parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();
        }
        catch (IOException | RuntimeException ex) {
            ContentLog.LOGGER.error("Could not read {}, treating every mod pack as on", file, ex);
            return new JsonObject();
        }
    }

    private static void writeControl(Path packRoot, JsonObject control) {
        Path file = packRoot.resolve(PackManager.CONFIG).resolve(CONTROL_FILE);
        String text = GSON.toJson(control) + System.lineSeparator();
        try {
            if (Files.isRegularFile(file) && text.equals(Files.readString(file))) { return; }
            Files.createDirectories(file.getParent());
            Files.writeString(file, text);
            ContentLog.LOGGER.info("Wrote {}, where each mod that ships a '{}' folder can be turned off or given a priority", file, IN_JAR);
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not write {}", file, ex); }
    }

    private record Found(Path home, String fileName, String modId, Set<String> namespaces) {}
}
