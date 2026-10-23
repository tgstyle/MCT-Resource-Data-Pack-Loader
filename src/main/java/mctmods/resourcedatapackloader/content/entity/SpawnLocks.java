package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.SpawnGateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpawnLocks {
    private static final Map<ResourceLocation, List<SpawnGateDef>> GATES = new HashMap<>();
    private static boolean loaded;

    private SpawnLocks() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        Json.eachObject(PackManager.SPAWN_LOCKS, "spawn lock file", SpawnLocks::read);
        if (!GATES.isEmpty()) { Summary.info("entities.locks", "Loaded spawn locks for " + GATES.size() + " entity type(s)"); }
    }

    static List<SpawnGateDef> gates(Entity entity) {
        if (GATES.isEmpty()) { return Collections.emptyList(); }
        ResourceLocation name = EntityList.getKey(entity);
        List<SpawnGateDef> found = name == null ? null : GATES.get(name);
        return found == null ? Collections.emptyList() : found;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        List<String> entities = Json.strings(json, "entities");
        if (entities.isEmpty()) {
            ContentLog.LOGGER.error("Spawn lock file {} names no entities, ignoring it", key);
            return;
        }
        SpawnGateDef gate = SpawnGateDef.read(key, json, "Spawn lock file");
        if (gate == SpawnGateDef.NONE) {
            ContentLog.LOGGER.error("Spawn lock file {} sets no spawn condition, ignoring it", key);
            return;
        }
        for (String entity : entities) { GATES.computeIfAbsent(new ResourceLocation(entity), name -> new ArrayList<>()).add(gate); }
    }
}
