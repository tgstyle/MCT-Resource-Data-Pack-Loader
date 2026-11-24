package mctmods.resourcedatapackloader.common.rubic.server;

import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.common.util.ticks.ITicket;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.SpawnScreen;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnCubes implements ITicket {
    private static final int spawnGenerateDistanceXZ = 12;
    private static final int spawnGenerateDistanceY = 8;
    private static final int spawnLoadDistanceXZ = 8;
    private static final int spawnLoadDistanceY = 8;
    @Nullable private BlockPos spawnPoint = null;

    public void update(World world) {
        if (!world.getSpawnPoint().equals(spawnPoint)) {
            removeTickets(world);
            spawnPoint = world.getSpawnPoint();
            addTickets(world);
        }
    }

    private void removeTickets(World world) {
        if (spawnPoint == null) { return; }
        ICubeProviderInternal serverCubeCache = (ICubeProviderInternal) world.getChunkProvider();
        int spawnCubeX = Coords.blockToCube(spawnPoint.getX());
        int spawnCubeY = Coords.blockToCube(spawnPoint.getY());
        int spawnCubeZ = Coords.blockToCube(spawnPoint.getZ());
        forEachCube(spawnCubeX, spawnCubeY, spawnCubeZ, spawnLoadDistanceXZ, spawnLoadDistanceY, (cubeX, cubeY, cubeZ) -> serverCubeCache.getCube(cubeX, cubeY, cubeZ).getTickets().remove(this));
    }

    private void addTickets(World world) {
        CubeProviderServer serverCubeCache = (CubeProviderServer) world.getChunkProvider();
        ContentLog.LOGGER.info("Loading cubes for spawn...");
        BlockPos spawnPoint = Objects.requireNonNull(this.spawnPoint);
        int spawnCubeX = Coords.blockToCube(spawnPoint.getX());
        int spawnCubeY = Coords.blockToCube(spawnPoint.getY());
        int spawnCubeZ = Coords.blockToCube(spawnPoint.getZ());
        AtomicLong lastTime = new AtomicLong(System.currentTimeMillis());
        final int progressReportInterval = 1000;
        int totalToGenerate = (spawnGenerateDistanceXZ * 2 + 1) * (spawnGenerateDistanceXZ * 2 + 1) * (spawnGenerateDistanceY * 2 + 1);
        AtomicInteger generated = new AtomicInteger();
        int r = Math.max(spawnGenerateDistanceXZ, spawnLoadDistanceXZ);
        int ry = Math.max(spawnGenerateDistanceY, spawnLoadDistanceY);
        forEachCube(spawnCubeX, spawnCubeY, spawnCubeZ, r, ry, (cubeX, cubeY, cubeZ) -> serverCubeCache.asyncGetCube(cubeX, cubeY, cubeZ, ICubeProviderServer.Requirement.LOAD, c -> {}));
        forEachCube(spawnCubeX, spawnCubeY, spawnCubeZ, r, ry, (cubeX, cubeY, cubeZ) -> {
            ICubeProviderServer.Requirement req;
            int dx = Math.abs(cubeX - spawnCubeX);
            int dy = Math.abs(cubeY - spawnCubeY);
            int dz = Math.abs(cubeZ - spawnCubeZ);
            if (dx >= spawnGenerateDistanceXZ || dz >= spawnGenerateDistanceXZ || dy >= spawnGenerateDistanceY) { req = ICubeProviderServer.Requirement.GENERATE; }
            else { req = ICubeProviderServer.Requirement.LIGHT; }
            Cube cube = serverCubeCache.getCube(cubeX, cubeY, cubeZ, req);
            assert cube != null;
            if (dx <= spawnLoadDistanceXZ && dz <= spawnLoadDistanceXZ) { cube.getTickets().add(this); }
            int percent = generated.incrementAndGet() * 100 / totalToGenerate;
            SpawnScreen.tell(world.getMinecraftServer(), percent);
            if (System.currentTimeMillis() >= lastTime.get() + progressReportInterval) {
                lastTime.set(System.currentTimeMillis());
                ContentLog.LOGGER.info(SpawnScreen.LOG, percent);
            }
        });
        SpawnScreen.tell(world.getMinecraftServer(), 100);
        ContentLog.LOGGER.info(SpawnScreen.LOG, 100);
    }

    private void forEachCube(int spawnCubeX, int spawnCubeY, int spawnCubeZ, int r, int ry, IXYZConsumer action) {
        for (int cubeX = spawnCubeX - r; cubeX <= spawnCubeX + r; cubeX++) {
            for (int cubeZ = spawnCubeZ - r; cubeZ <= spawnCubeZ + r; cubeZ++) {
                for (int cubeY = spawnCubeY + ry; cubeY >= spawnCubeY - ry; cubeY--) { action.accept(cubeX, cubeY, cubeZ); }
            }
        }
    }

    @Override public boolean shouldTick() { return false; }

    @FunctionalInterface private interface IXYZConsumer { void accept(int x, int y, int z); }
}
