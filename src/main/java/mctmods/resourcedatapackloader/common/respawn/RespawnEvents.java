package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProvider;
import mctmods.resourcedatapackloader.common.util.world.ChunkSpan;
import mctmods.resourcedatapackloader.common.util.world.PlayerNear;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mctmods.resourcedatapackloader.core.util.SideUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class RespawnEvents {
    private static final int COARSE = 100;

    private RespawnEvents() {}

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) { RespawnData.prune(event.getWorld()); }
    }

    @SubscribeEvent public static void onChunkLoad(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || GenHeights.rubic(world)) { return; }
        wake(world, ChunkPos.asLong(event.getChunk().x, event.getChunk().z));
    }

    public static void onCubeLoad(World world, Cube cube) { wake(world, ChunkPos.asLong(cube.getX(), cube.getZ())); }

    @SubscribeEvent public static void onTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!SideUtils.serverEnd(event) || world.getTotalWorldTime() % COARSE != 0) { return; }
        RespawnData data = RespawnData.of(world);
        if (data.size() == 0) { return; }
        long now = world.getTotalWorldTime();
        RespawnInstance instance = data.pollDue(now);
        while (instance != null) {
            handle((WorldServer) world, data, instance, now);
            instance = data.pollDue(now);
        }
    }

    private static void wake(World world, long column) {
        RespawnData data = RespawnData.of(world);
        for (RespawnInstance instance : data.unpark(column)) { data.requeue(instance, 0L); }
    }

    private static void handle(WorldServer world, RespawnData data, RespawnInstance instance, long now) {
        RespawnDef def = RespawnRecorder.resolve(instance.kind, instance.def);
        if (def == null) {
            data.remove(instance);
            return;
        }
        int[] box = instance.box;
        if (!world.isBlockLoaded(new BlockPos((box[0] + box[3]) >> 1, (box[1] + box[4]) >> 1, (box[2] + box[5]) >> 1))) {
            data.park(instance);
            return;
        }
        if (PlayerNear.within(world, instance.bounds(), def.playerDistance) || !generated(world, box)) {
            data.requeue(instance, now + COARSE * 3L);
            return;
        }
        RespawnReset.reset(world, instance, def);
        instance.last = now;
        data.requeue(instance, now + def.after * 20L);
        data.markDirty();
    }

    private static boolean generated(WorldServer world, int[] box) {
        if (!GenHeights.rubic(world)) {
            return ChunkSpan.allChunks(box[0], box[2], box[3], box[5], world::isChunkGeneratedAt);
        }
        ICubeProvider cubes = (ICubeProvider) world.getChunkProvider();
        int low = Math.max(box[1], GenHeights.floor(world, 0)) >> 4;
        int high = Math.min(box[4], GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT) - 1) >> 4;
        return ChunkSpan.allChunks(box[0], box[2], box[3], box[5], (cubeX, cubeZ) -> {
            for (int cubeY = low; cubeY <= high; cubeY++) {
                if (cubes.getLoadedCube(cubeX, cubeY, cubeZ) == null && !RubicWorldControl.cubeSaved(world.getChunkProvider(), cubeX, cubeY, cubeZ)) { return false; }
            }
            return true;
        });
    }
}
