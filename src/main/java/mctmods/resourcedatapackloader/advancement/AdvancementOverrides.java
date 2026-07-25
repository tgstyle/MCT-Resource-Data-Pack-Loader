package mctmods.resourcedatapackloader.advancement;

import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.JsonParseException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

public final class AdvancementOverrides {
    private static final Map<ResourceLocation, String> CACHE = new LinkedHashMap<>();
    private static int generation = -1;
    private static boolean quiet;

    private AdvancementOverrides() {}

    public static void apply(Map<ResourceLocation, Advancement.Builder> map) {
        PackManager manager = PackManager.get();
        if (manager.isEmpty()) { return; }
        if (generation != manager.getGeneration()) {
            generation = manager.getGeneration();
            quiet = false;
            CACHE.clear();
            manager.forEach(PackManager.ADVANCEMENTS, PackManager.JSON, (namespace, path, contents) -> CACHE.put(new ResourceLocation(namespace, path), contents));
        }
        int applied = 0;
        for (Map.Entry<ResourceLocation, String> entry : CACHE.entrySet()) {
            if (map.containsKey(entry.getKey())) { continue; }
            Advancement.Builder builder = parse(entry.getKey(), entry.getValue());
            if (builder == null) { continue; }
            map.put(entry.getKey(), builder);
            applied++;
        }
        if (!quiet && applied > 0) { MCTMixin.LOGGER.info("Applied {} advancement override(s)", applied); }
        quiet = true;
    }

    @Nullable private static Advancement.Builder parse(ResourceLocation id, String contents) {
        try {
            Advancement.Builder builder = JsonUtils.gsonDeserialize(AdvancementManager.GSON, contents, Advancement.Builder.class);
            if (builder == null && !quiet) { MCTMixin.LOGGER.error("Advancement {} is empty or null, leaving the built-in one in place", id); }
            return builder;
        }
        catch (IllegalArgumentException | JsonParseException ex) {
            if (!quiet) { MCTMixin.LOGGER.error("Parsing error in advancement {}, leaving the built-in one in place", id, ex); }
            return null;
        }
    }
}
