package mctmods.resourcedatapackloader.common.entities.spawning;

import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnGateDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public final class SpawnLocks {
    private static final List<Lock> LOCKS = new ArrayList<>();

    private SpawnLocks() {}

    public static void load() {
        LOCKS.clear();
        Json.eachObject(PackManager.SPAWN_LOCKS, "spawn lock file", SpawnLocks::read);
        if (!LOCKS.isEmpty()) { Summary.info("entities.locks", "Loaded " + LOCKS.size() + " spawn lock(s)"); }
    }

    static List<SpawnGateDef> gates(Entity entity) {
        if (LOCKS.isEmpty()) { return List.of(); }
        List<SpawnGateDef> found = new ArrayList<>();
        for (Lock lock : LOCKS) {
            if (MobStats.targets(lock.ids(), lock.kinds(), entity)) { found.add(lock.gate()); }
        }
        return found;
    }

    private static void read(Identifier key, JsonObject json) {
        String owner = "Spawn lock file " + key;
        List<Identifier> ids = new ArrayList<>();
        List<String> kinds = new ArrayList<>();
        if (!json.has("entities") || !MobStats.entities(owner, "entities", json.get("entities"), ids, kinds) || ids.isEmpty() && kinds.isEmpty()) {
            ContentLog.LOGGER.error("{} names no entities, ignoring it", owner);
            return;
        }
        SpawnGateDef gate = SpawnGateDef.read(key, json, "Spawn lock file");
        if (gate == SpawnGateDef.NONE) {
            ContentLog.LOGGER.error("{} sets no spawn condition, ignoring it", owner);
            return;
        }
        LOCKS.add(new Lock(ids, kinds, gate));
    }

    private record Lock(List<Identifier> ids, List<String> kinds, SpawnGateDef gate) {}
}
