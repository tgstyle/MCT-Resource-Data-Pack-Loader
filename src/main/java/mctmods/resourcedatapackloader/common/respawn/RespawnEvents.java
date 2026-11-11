package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.util.world.PlayerNear;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class RespawnEvents {
    private static final int COARSE = 100;

    private RespawnEvents() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(RespawnEvents::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(RespawnEvents::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(RespawnEvents::onTick);
    }

    private static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) { RespawnData.prune(level); }
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) { wake(level, event.getChunk().getPos().toLong()); }
    }

    private static void onTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.getGameTime() % COARSE != 0) { return; }
        RespawnData data = RespawnData.of(level);
        if (data.size() == 0) { return; }
        long now = level.getGameTime();
        RespawnInstance instance = data.pollDue(now);
        while (instance != null) {
            handle(level, data, instance, now);
            instance = data.pollDue(now);
        }
    }

    private static void wake(ServerLevel level, long column) {
        RespawnData data = RespawnData.of(level);
        for (RespawnInstance instance : data.unpark(column)) { data.requeue(instance, 0L); }
    }

    private static void handle(ServerLevel level, RespawnData data, RespawnInstance instance, long now) {
        RespawnDef def = RespawnRecorder.resolve(instance.kind, instance.def);
        if (def == null) {
            data.remove(instance);
            return;
        }
        int[] box = instance.box;
        if (unloaded(level, ((box[0] + box[3]) >> 1) >> 4, ((box[2] + box[5]) >> 1) >> 4)) {
            data.park(instance);
            return;
        }
        if (PlayerNear.within(level, instance.bounds(), def.playerDistance()) || !generated(level, box)) {
            data.requeue(instance, now + COARSE * 3L);
            return;
        }
        RespawnReset.reset(level, instance, def);
        instance.last = now;
        data.requeue(instance, now + def.after() * 20L);
        data.setDirty();
    }

    private static boolean unloaded(ServerLevel level, int chunkX, int chunkZ) { return level.getChunkSource().getChunkNow(chunkX, chunkZ) == null; }

    private static boolean generated(ServerLevel level, int[] box) {
        for (int chunkX = box[0] >> 4; chunkX <= box[3] >> 4; chunkX++) {
            for (int chunkZ = box[2] >> 4; chunkZ <= box[5] >> 4; chunkZ++) {
                if (unloaded(level, chunkX, chunkZ)) { return false; }
            }
        }
        return true;
    }
}
