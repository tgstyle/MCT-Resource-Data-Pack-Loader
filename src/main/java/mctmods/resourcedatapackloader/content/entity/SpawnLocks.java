package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.SpawnGateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpawnLocks {
    private static final List<Lock> LOCKS = new ArrayList<>();

    private SpawnLocks() {}

    public static void load() {
        LOCKS.clear();
        Json.eachObject(PackManager.SPAWN_LOCKS, "spawn lock file", SpawnLocks::read);
        if (!LOCKS.isEmpty()) { Summary.info("entities.locks", "Loaded " + LOCKS.size() + " spawn lock(s)"); }
    }

    static List<SpawnGateDef> gates(Entity entity) {
        if (LOCKS.isEmpty()) { return Collections.emptyList(); }
        List<SpawnGateDef> found = null;
        for (Lock lock : LOCKS) {
            if (!MobStats.targets(lock.entities, entity)) { continue; }
            if (found == null) { found = new ArrayList<>(); }
            found.add(lock.gate);
        }
        return found == null ? Collections.emptyList() : found;
    }

    private static void read(ResourceLocation key, JsonObject json) {
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

    private static final class Lock {
        final List<String> entities;
        final SpawnGateDef gate;

        Lock(List<String> entities, SpawnGateDef gate) {
            this.entities = entities;
            this.gate = gate;
        }
    }
}
