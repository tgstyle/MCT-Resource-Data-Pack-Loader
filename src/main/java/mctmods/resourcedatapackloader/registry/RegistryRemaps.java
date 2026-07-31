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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import java.util.HashMap;
import java.util.Map;

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
        if (!Config.data.registryRemaps) { return; }
        int[] count = new int[1];
        PackManager.get().forEach(PackManager.REGISTRY_REMAP, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { count[0] += read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in registry remap {}, ignoring it", key, ex); }
        });
        if (count[0] > 0) { Summary.info("remaps", "Loaded " + count[0] + " registry rename(s) across " + REMAPS.size() + " registry/registries"); }
    }

    private static int read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Registry remap {} is empty, ignoring it", key);
            return 0;
        }
        ResourceLocation registry = new ResourceLocation(JsonUtils.getString(json, REGISTRY));
        JsonObject mapping = JsonUtils.getJsonObject(json, MAPPING);
        Map<ResourceLocation, ResourceLocation> target = REMAPS.computeIfAbsent(registry, k -> new HashMap<>());
        int count = 0;
        for (Map.Entry<String, JsonElement> entry : mapping.entrySet()) {
            put(target, new ResourceLocation(entry.getKey()), new ResourceLocation(entry.getValue().getAsString()));
            count++;
        }
        return count;
    }

    private static void put(Map<ResourceLocation, ResourceLocation> target, ResourceLocation from, ResourceLocation to) {
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : target.entrySet()) {
            if (entry.getValue().equals(from)) { entry.setValue(to); }
        }
        target.put(from, to);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent public static void onMissingMappings(RegistryEvent.MissingMappings event) {
        if (!Config.data.registryRemaps) { return; }
        if (generation != PackManager.get().getGeneration()) { reload(); }
        Map<ResourceLocation, ResourceLocation> target = REMAPS.get(event.getName());
        if (target == null) { return; }
        for (Object raw : event.getAllMappings()) {
            RegistryEvent.MissingMappings.Mapping mapping = (RegistryEvent.MissingMappings.Mapping) raw;
            ResourceLocation renamed = target.get(mapping.key);
            if (renamed == null) { continue; }
            IForgeRegistryEntry value = mapping.registry.getValue(renamed);
            if (value == null) {
                ContentLog.LOGGER.warn("Registry remap sends {} to {} in {}, but nothing is registered under that name", mapping.key, renamed, event.getName());
                continue;
            }
            mapping.remap(value);
            ContentLog.LOGGER.info("Remapped {} to {} in {}", mapping.key, renamed, event.getName());
        }
    }
}
