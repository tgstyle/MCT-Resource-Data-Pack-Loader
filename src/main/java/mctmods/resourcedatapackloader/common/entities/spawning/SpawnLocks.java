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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public final class SpawnLocks {
    private static final List<String> FILE_KEYS = Collections.unmodifiableList(Arrays.asList("entities", "dimensions", "structures", "radius", "timeFrom", "timeTo", "dayAtLeast", "advancement", "team", "objective", "scoreAtLeast", "dietAtLeast", "dietCount"));
    private static final List<Lock> LOCKS = new ArrayList<>();

    private SpawnLocks() {}

    public static void load() {
        LOCKS.clear();
        Json.eachObject(PackManager.SPAWN_LOCKS, "spawn lock file", SpawnLocks::read);
        if (!LOCKS.isEmpty()) { Summary.info("entities.locks", "Loaded " + LOCKS.size() + " spawn lock(s)"); }
    }

    static List<SpawnGateDef> gates(Entity entity) {
        if (LOCKS.isEmpty()) { return Collections.emptyList(); }
        return Lists.collect(LOCKS, lock -> MobStats.targets(lock.entities, entity), lock -> lock.gate);
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", FILE_KEYS);
        List<String> entities = MobStats.required("Spawn lock file " + key, json, "entities", "entities");
        if (entities == null) {
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

    private static final class Lock {
        final List<String> entities;
        final SpawnGateDef gate;

        Lock(List<String> entities, SpawnGateDef gate) {
            this.entities = entities;
            this.gate = gate;
        }
    }
}
