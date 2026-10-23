package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.SpawnGateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpawnLocks {
    private static final Map<ResourceLocation, List<SpawnGateDef>> GATES = new HashMap<>();

    private SpawnLocks() {}

    public static void load() {
        GATES.clear();
        Json.eachObject(PackManager.SPAWN_LOCKS, "spawn lock file", SpawnLocks::read);
        if (!GATES.isEmpty()) { Summary.info("entities.locks", "Loaded spawn locks for " + GATES.size() + " entity type(s)"); }
    }

    static List<SpawnGateDef> gates(Entity entity) {
        if (GATES.isEmpty()) { return List.of(); }
        return GATES.getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()), List.of());
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
        for (String entity : entities) {
            ResourceLocation name = ResourceLocation.tryParse(entity);
            if (name == null) {
                ContentLog.LOGGER.error("Spawn lock file {} names '{}', which is not an entity name, ignoring it", key, entity);
                continue;
            }
            GATES.computeIfAbsent(name, ignored -> new ArrayList<>()).add(gate);
        }
    }
}
