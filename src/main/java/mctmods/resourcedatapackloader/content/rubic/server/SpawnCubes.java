package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.interfaces.ITicket;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class SpawnCubes implements ITicket {
    private static final int spawnGenerateDistanceXZ = 12;
    private static final int spawnGenerateDistanceY = 8;
    private static final int spawnLoadDistanceXZ = 8;
    private static final int spawnLoadDistanceY = 8;
    @Nullable private BlockPos spawnPoint = null;
    private int radiusXZGenerate = spawnGenerateDistanceXZ;
    private int radiusYGenerate = spawnGenerateDistanceY;
    private int radiusXZForce = spawnLoadDistanceXZ;
    private int radiusYForce = spawnLoadDistanceY;

    public void update(World world) {
        if (!world.getSpawnPoint().equals(spawnPoint) ||
                radiusXZGenerate != spawnGenerateDistanceXZ ||
                radiusYGenerate != spawnGenerateDistanceY ||
                radiusXZForce != spawnLoadDistanceXZ ||
                radiusYForce != spawnLoadDistanceY) {
            removeTickets(world);
            spawnPoint = world.getSpawnPoint();
            radiusXZGenerate = spawnGenerateDistanceXZ;
            radiusYGenerate = spawnGenerateDistanceY;
            radiusXZForce = spawnLoadDistanceXZ;
            radiusYForce = spawnLoadDistanceY;
            addTickets(world);
        }
    }

    private void removeTickets(World world) {
        if (radiusYForce < 0 || radiusXZForce < 0 || spawnPoint == null) { return; }
        ICubeProviderInternal serverCubeCache = (ICubeProviderInternal) world.getChunkProvider();
        int spawnCubeX = Coords.blockToCube(spawnPoint.getX());
        int spawnCubeY = Coords.blockToCube(spawnPoint.getY());
        int spawnCubeZ = Coords.blockToCube(spawnPoint.getZ());
        forEachCube(spawnCubeX, spawnCubeY, spawnCubeZ, radiusXZForce, radiusYForce, (cubeX, cubeY, cubeZ) -> serverCubeCache.getCube(cubeX, cubeY, cubeZ).getTickets().remove(this));
    }

    private void addTickets(World world) {
        if (radiusXZGenerate < 0 || radiusYGenerate < 0) { return; }
        CubeProviderServer serverCubeCache = (CubeProviderServer) world.getChunkProvider();
        ContentLog.LOGGER.info("Loading cubes for spawn...");
        BlockPos spawnPoint = Objects.requireNonNull(this.spawnPoint);
        int spawnCubeX = Coords.blockToCube(spawnPoint.getX());
        int spawnCubeY = Coords.blockToCube(spawnPoint.getY());
        int spawnCubeZ = Coords.blockToCube(spawnPoint.getZ());
        AtomicLong lastTime = new AtomicLong(System.currentTimeMillis());
        final int progressReportInterval = 1000;
        int totalToGenerate = (radiusXZGenerate * 2 + 1) * (radiusXZGenerate * 2 + 1) * (radiusYGenerate * 2 + 1);
        AtomicInteger generated = new AtomicInteger();
        int r = Math.max(radiusXZGenerate, radiusXZForce);
        int ry = Math.max(radiusYGenerate, radiusYForce);
        forEachCube(spawnCubeX, spawnCubeY, spawnCubeZ, r, ry, (cubeX, cubeY, cubeZ) -> serverCubeCache.asyncGetCube(cubeX, cubeY, cubeZ, ICubeProviderServer.Requirement.LOAD, c -> {}));
        forEachCube(spawnCubeX, spawnCubeY, spawnCubeZ, r, ry, (cubeX, cubeY, cubeZ) -> {
            ICubeProviderServer.Requirement req;
            int dx = Math.abs(cubeX - spawnCubeX);
            int dy = Math.abs(cubeY - spawnCubeY);
            int dz = Math.abs(cubeZ - spawnCubeZ);
            if (dx >= radiusXZGenerate || dz >= radiusXZGenerate || dy >= radiusYGenerate) { req = ICubeProviderServer.Requirement.GENERATE; }
            else { req = ICubeProviderServer.Requirement.LIGHT; }
            Cube cube = serverCubeCache.getCube(cubeX, cubeY, cubeZ, req);
            assert cube != null;
            if (dx <= radiusXZForce && dz <= radiusXZForce) { cube.getTickets().add(this); }
            generated.incrementAndGet();
            tellScreen(world, generated.get() * 100 / totalToGenerate);
            if (System.currentTimeMillis() >= lastTime.get() + progressReportInterval) {
                lastTime.set(System.currentTimeMillis());
                ContentLog.LOGGER.info("Preparing spawn area: {}%", generated.get() * 100 / totalToGenerate);
            }
        });
        tellScreen(world, 100);
        ContentLog.LOGGER.info("Preparing spawn area: 100%");
    }

    private static void tellScreen(World world, int percent) {
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) { return; }
        server.currentTask = "Preparing spawn area";
        server.percentDone = percent;
        server.setUserMessage("menu.generatingTerrain");
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
