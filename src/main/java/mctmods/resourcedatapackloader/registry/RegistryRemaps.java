package mctmods.resourcedatapackloader.registry;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.registries.MissingMappingsEvent;
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
    private static final PackGeneration GENERATION = new PackGeneration();

    private RegistryRemaps() {}

    public static void reload() {
        REMAPS.clear();
        GENERATION.stale();
        if (Config.data.registryRemapsOff()) { return; }
        int[] count = new int[1];
        Json.eachFile(PackManager.REGISTRY_REMAP, "registry remap", (key, contents) -> count[0] += read(key, contents));
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

    @SuppressWarnings("unchecked") public static void onMissingMappings(MissingMappingsEvent event) {
        if (Config.data.registryRemapsOff()) { return; }
        if (GENERATION.stale()) { reload(); }
        Map<ResourceLocation, ResourceLocation> target = REMAPS.get(event.getKey().location());
        if (target == null) { return; }
        remap(event, (ResourceKey<? extends Registry<Object>>) event.getKey(), target);
    }

    private static <T> void remap(MissingMappingsEvent event, ResourceKey<? extends Registry<T>> key, Map<ResourceLocation, ResourceLocation> target) {
        for (MissingMappingsEvent.Mapping<T> mapping : event.getAllMappings(key)) {
            ResourceLocation renamed = follow(target, mapping.getKey());
            if (renamed == null) { continue; }
            if (!mapping.getRegistry().containsKey(renamed)) {
                ContentLog.LOGGER.warn("Registry remap sends {} to {} in {}, but nothing is registered under that name", mapping.getKey(), renamed, key.location());
                continue;
            }
            mapping.remap(mapping.getRegistry().getValue(renamed));
            ContentLog.LOGGER.debug("Remapped {} to {} in {}", mapping.getKey(), renamed, key.location());
        }
    }
}
