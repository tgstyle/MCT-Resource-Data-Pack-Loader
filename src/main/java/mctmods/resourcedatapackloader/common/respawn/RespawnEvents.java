package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.util.world.PlayerNear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class RespawnEvents {
    private static final int COARSE = 100;

    private RespawnEvents() {}

    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) { RespawnData.prune(level); }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        RespawnData data = RespawnData.of(level);
        for (RespawnInstance instance : data.unpark(ChunkPos.pack(event.getChunk().getPos().x(), event.getChunk().getPos().z()))) { data.requeue(instance, 0L); }
    }

    public static void onTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        long now = level.getGameTime();
        if (now % COARSE != 0) { return; }
        RespawnData data = RespawnData.of(level);
        if (data.size() == 0) { return; }
        RespawnInstance instance = data.pollDue(now);
        while (instance != null) {
            handle(level, data, instance, now);
            instance = data.pollDue(now);
        }
    }

    private static void handle(ServerLevel level, RespawnData data, RespawnInstance instance, long now) {
        RespawnDef def = RespawnRecorder.resolve(instance.kind, instance.def);
        if (def == null) {
            data.remove(instance);
            return;
        }
        int[] box = instance.box;
        if (level.getChunkSource().getChunkNow(((box[0] + box[3]) >> 1) >> 4, ((box[2] + box[5]) >> 1) >> 4) == null) {
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

    private static boolean generated(ServerLevel level, int[] box) {
        for (int chunkX = box[0] >> 4; chunkX <= box[3] >> 4; chunkX++) {
            for (int chunkZ = box[2] >> 4; chunkZ <= box[5] >> 4; chunkZ++) {
                if (level.getChunkSource().getChunkNow(chunkX, chunkZ) == null) { return false; }
            }
        }
        return true;
    }
}
