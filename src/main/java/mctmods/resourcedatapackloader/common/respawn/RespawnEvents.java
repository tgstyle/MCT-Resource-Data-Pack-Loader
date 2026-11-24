package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.util.world.ChunkSpan;
import mctmods.resourcedatapackloader.common.util.world.PlayerNear;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;

public final class RespawnEvents {
    private static final int COARSE = 100;

    private RespawnEvents() {}

    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) { RespawnData.prune(level); }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        RespawnData data = RespawnData.of(level);
        for (RespawnInstance instance : data.unpark(event.getChunk().getPos().toLong())) { data.requeue(instance, 0L); }
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level) || level.getGameTime() % COARSE != 0) { return; }
        RespawnData data = RespawnData.of(level);
        if (data.size() == 0) { return; }
        long now = level.getGameTime();
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
        if (unloaded(level, instance.center().getX() >> 4, instance.center().getZ() >> 4)) {
            data.park(instance);
            return;
        }
        if (PlayerNear.within(level, instance.bounds(), def.playerDistance()) || !loaded(level, instance.box)) {
            data.requeue(instance, now + COARSE * 3L);
            return;
        }
        RespawnReset.reset(level, instance, def);
        instance.last = now;
        data.requeue(instance, now + def.after() * 20L);
        data.setDirty();
    }

    private static boolean unloaded(ServerLevel level, int chunkX, int chunkZ) { return level.getChunkSource().getChunkNow(chunkX, chunkZ) == null; }

    private static boolean loaded(ServerLevel level, int[] box) { return ChunkSpan.allChunks(box[0], box[2], box[3], box[5], (chunkX, chunkZ) -> !unloaded(level, chunkX, chunkZ)); }
}
