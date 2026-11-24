package mctmods.resourcedatapackloader.common.util.worldgen;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;

public final class PackGeneration {
    private int seen = -1;

    public static <R> void reload(List<R> rules, String folder, String label, String summaryKey, String noun, BiConsumer<ResourceLocation, JsonObject> reader) {
        rules.clear();
        Json.eachObject(folder, label, reader);
        if (!rules.isEmpty()) { Summary.info(summaryKey, "Loaded " + rules.size() + " " + noun); }
    }

    public boolean stale() {
        int now = PackManager.get().getGeneration();
        if (now == seen) { return false; }
        seen = now;
        return true;
    }

    public void load(boolean enabled, Runnable clear, String folder, String kind, BiConsumer<ResourceLocation, JsonObject> read, Runnable summary) {
        clear.run();
        stale();
        if (!enabled) { return; }
        Json.eachObject(folder, kind, read);
        summary.run();
    }
}
