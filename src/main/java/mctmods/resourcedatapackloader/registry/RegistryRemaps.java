package mctmods.resourcedatapackloader.registry;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class RegistryRemaps {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String REGISTRY = "registry";
    private static final String MAPPING = "mapping";
    private static final Map<ResourceLocation, Map<ResourceLocation, ResourceLocation>> REMAPS = new HashMap<>();
    private static int generation = -1;

    private RegistryRemaps() {}

    public static void reload() {
        REMAPS.clear();
        generation = PackManager.get().getGeneration();
        if (!Config.data.registryRemaps()) { return; }
        int[] count = new int[1];
        PackManager.get().forEach(PackManager.REGISTRY_REMAP, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, path);
            try { count[0] += read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in registry remap {}, ignoring it", key, ex); }
        });
        if (count[0] > 0) { Summary.info("remaps", "Loaded " + count[0] + " registry rename(s) across " + REMAPS.size() + " registry/registries"); }
    }

    private static int read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Registry remap {} is empty, ignoring it", key);
            return 0;
        }
        ResourceLocation registry = ResourceLocation.parse(GsonHelper.getAsString(json, REGISTRY));
        JsonObject mapping = GsonHelper.getAsJsonObject(json, MAPPING);
        Map<ResourceLocation, ResourceLocation> target = REMAPS.computeIfAbsent(registry, k -> new HashMap<>());
        int count = 0;
        for (Map.Entry<String, JsonElement> entry : mapping.entrySet()) {
            target.put(ResourceLocation.parse(entry.getKey()), ResourceLocation.parse(entry.getValue().getAsString()));
            count++;
        }
        return count;
    }

    @Nullable private static ResourceLocation follow(Map<ResourceLocation, ResourceLocation> target, ResourceLocation from) {
        ResourceLocation current = target.get(from);
        if (current == null) { return null; }
        Set<ResourceLocation> seen = new HashSet<>();
        seen.add(from);
        while (seen.add(current)) {
            ResourceLocation next = target.get(current);
            if (next == null) { return current; }
            current = next;
        }
        ContentLog.LOGGER.error("Registry remap chain from {} loops back on itself, so it is ignored", from);
        return null;
    }

    public static void applyAliases() {
        if (!Config.data.registryRemaps()) { return; }
        if (generation != PackManager.get().getGeneration()) { reload(); }
        for (Map.Entry<ResourceLocation, Map<ResourceLocation, ResourceLocation>> entry : REMAPS.entrySet()) {
            Registry<?> registry = BuiltInRegistries.REGISTRY.get(entry.getKey());
            if (registry == null) {
                ContentLog.LOGGER.warn("Registry remaps name the registry {}, which does not exist, so they are ignored", entry.getKey());
                continue;
            }
            for (ResourceLocation from : entry.getValue().keySet()) { alias(registry, entry.getKey(), entry.getValue(), from); }
        }
    }

    private static void alias(Registry<?> registry, ResourceLocation name, Map<ResourceLocation, ResourceLocation> target, ResourceLocation from) {
        ResourceLocation renamed = follow(target, from);
        if (renamed == null) { return; }
        if (renamed.equals(registry.resolve(from))) { return; }
        if (!registry.containsKey(renamed)) {
            ContentLog.LOGGER.warn("Registry remap sends {} to {} in {}, but nothing is registered under that name", from, renamed, name);
            return;
        }
        try {
            registry.addAlias(from, renamed);
            ContentLog.LOGGER.debug("Remapped {} to {} in {}", from, renamed, name);
        }
        catch (IllegalStateException clash) { ContentLog.LOGGER.error("Could not remap {} to {} in {}: {}", from, renamed, name, clash.getMessage()); }
    }
}
