package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.content.ContentInherits;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class SkippedContent {
    static final List<String> FOLDERS = List.of(PackManager.BLOCKS, PackManager.ITEMS, PackManager.FLUIDS, PackManager.MATERIALS, PackManager.TABS, PackManager.SOUNDS, PackManager.POTIONS, PackManager.POTION_TYPES, PackManager.VILLAGERS, PackManager.ENTITIES, PackManager.EXPOSURES, PackManager.TOOLTIPS);
    static final List<String> WITHHELD_FOLDERS = List.of("advancement", "recipe", "loot_table");
    private static final Map<String, Boolean> VERDICTS = new ConcurrentHashMap<>();
    private static volatile int generation = -1;
    private static volatile Set<String> ids = Set.of();

    private SkippedContent() {}

    private static synchronized Set<String> ids() {
        int current = PackManager.get().getGeneration();
        if (current == generation) { return ids; }
        Set<String> found = new HashSet<>();
        for (RDPLPack pack : PackManager.get().getPacks()) { found.addAll(packIds(pack)); }
        VERDICTS.clear();
        ids = found;
        generation = current;
        return found;
    }

    private static Set<String> packIds(RDPLPack pack) {
        Set<String> found = new HashSet<>();
        for (String folder : FOLDERS) { found.addAll(pack.files(PackManager.CONTENT, folder, PackManager.JSON)); }
        for (String folder : List.of(PackManager.BLOCKS, PackManager.ITEMS, PackManager.FLUIDS)) {
            pack.forEach(PackManager.CONTENT, folder, PackManager.JSON, (namespace, path, text) -> addEntries(found, folder, Identifier.fromNamespaceAndPath(namespace, path), text));
        }
        return found;
    }

    private static void addEntries(Set<String> found, String folder, Identifier file, String text) {
        try {
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            if (PackManager.FLUIDS.equals(folder)) {
                Identifier id = Identifier.tryBuild(file.getNamespace(), ContentParser.fluidName(file, json));
                if (id == null) { return; }
                found.add(id.toString());
                found.add(FluidDef.flowingOf(id).toString());
                found.add(FluidDef.bucketOf(id).toString());
                return;
            }
            for (Identifier id : ContentInherits.variantIds(file, json)) { found.add(id.toString()); }
        }
        catch (RuntimeException ex) { ContentLog.LOGGER.debug("Could not read {} to list its entries: {}", file, ex.getMessage()); }
    }

    private static String named(String text, Set<String> ids) {
        List<String> hits = new ArrayList<>();
        for (String id : ids) {
            if (text.contains("\"" + id + "\"")) { hits.add(id); }
        }
        hits.sort(null);
        return String.join(", ", hits);
    }

    static boolean withheld(PackType type, String namespace, String path, boolean overriding) {
        if (type != PackType.SERVER_DATA || !Config.content.vanillaClients() || WITHHELD_FOLDERS.stream().noneMatch(folder -> path.startsWith(folder + "/")) || !path.endsWith(PackManager.JSON_SUFFIX)) { return false; }
        Set<String> skipped = ids();
        if (skipped.isEmpty()) { return false; }
        String key = overriding + ":" + namespace + ":" + path;
        Boolean known = VERDICTS.get(key);
        if (known == null) {
            known = namesSkipped(type, namespace, path, overriding, skipped);
            VERDICTS.put(key, known);
        }
        return known;
    }

    private static boolean namesSkipped(PackType type, String namespace, String path, boolean overriding, Set<String> skipped) {
        try (InputStream stream = PackManager.get().openRaw(type, namespace, path, overriding)) { return stream != null && !named(new String(stream.readAllBytes(), StandardCharsets.UTF_8), skipped).isEmpty(); }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read {}:{} to check it for skipped content", namespace, path, ex);
            return false;
        }
    }

    static void report(RDPLPack pack) {
        Set<String> skipped = ids();
        if (skipped.isEmpty()) { return; }
        List<String> files = new ArrayList<>();
        Set<String> names = new HashSet<>();
        for (String folder : WITHHELD_FOLDERS) {
            pack.forEach(PackManager.CONTENT, folder, PackManager.JSON, (namespace, id, text) -> {
                String hit = named(text, skipped);
                if (hit.isEmpty()) { return; }
                files.add(folder + " " + namespace + ":" + id);
                names.add(hit);
            });
        }
        if (files.isEmpty()) { return; }
        files.sort(null);
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(null);
        ContentLog.LOGGER.warn("Pack '{}' provides {} advancement, recipe or loot table file(s) naming skipped content, withheld because content.vanillaClients is on: {} (names {})", pack.getName(), files.size(), String.join(", ", files), String.join(", ", sorted));
    }
}
