package mctmods.resourcedatapackloader.common.entities.spawning;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnGateDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.Lists;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public final class SpawnLocks {
    private static final List<String> FILE_KEYS = List.of("entities", "dimensions", "structures", "radius", "timeFrom", "timeTo", "dayAtLeast", "advancement", "team", "objective", "scoreAtLeast", "dietAtLeast", "dietCount");
    private static final List<Lock> LOCKS = new ArrayList<>();

    private SpawnLocks() {}

    public static void load() {
        LOCKS.clear();
        Json.eachObject(PackManager.SPAWN_LOCKS, "spawn lock file", SpawnLocks::read);
        if (!LOCKS.isEmpty()) { Summary.info("entities.locks", "Loaded " + LOCKS.size() + " spawn lock(s)"); }
    }

    static List<SpawnGateDef> gates(Entity entity) {
        if (LOCKS.isEmpty()) { return List.of(); }
        return Lists.collect(LOCKS, lock -> MobStats.targets(lock.entities(), entity), Lock::gate);
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        List<String> entities = json.has("entities") ? MobStats.entities("Spawn lock file " + key, "entities", json.get("entities")) : null;
        if (entities == null || entities.isEmpty()) {
            ContentLog.LOGGER.error("Spawn lock file {} names no entities, ignoring it", key);
            return;
        }
        SpawnGateDef gate = SpawnGateDef.read(key, json, "Spawn lock file");
        if (gate == SpawnGateDef.NONE) {
            ContentLog.LOGGER.error("Spawn lock file {} sets no spawn condition, ignoring it", key);
            return;
        }
        LOCKS.add(new Lock(entities, gate));
    }

    private record Lock(List<String> entities, SpawnGateDef gate) {}
}
