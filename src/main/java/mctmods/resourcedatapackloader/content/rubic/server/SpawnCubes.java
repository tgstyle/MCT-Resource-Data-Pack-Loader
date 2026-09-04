package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMinecraftServerMessage;
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

    public void update(World world) { update(world, spawnGenerateDistanceXZ, spawnGenerateDistanceY, spawnLoadDistanceXZ, spawnLoadDistanceY); }

    public void update(World world, int newRadiusXZGenerate, int newRadiusYGenerate, int newRadiusXZForce, int newRadiusYForce) {
        if (!world.getSpawnPoint().equals(spawnPoint) ||
                radiusXZGenerate != newRadiusXZGenerate ||
                radiusYGenerate != newRadiusYGenerate ||
                radiusXZForce != newRadiusXZForce ||
                radiusYForce != newRadiusYForce) {
            removeTickets(world);
            spawnPoint = world.getSpawnPoint();
            radiusXZGenerate = newRadiusXZGenerate;
            radiusYGenerate = newRadiusYGenerate;
            radiusXZForce = newRadiusXZForce;
            radiusYForce = newRadiusYForce;
            addTickets(world);
        }
    }

    private void removeTickets(World world) {
        if (radiusYForce < 0 || radiusXZForce < 0 || spawnPoint == null) { return; }
        ICubeProviderInternal serverCubeCache = (ICubeProviderInternal) world.getChunkProvider();
        int spawnCubeX = Coords.blockToCube(spawnPoint.getX());
        int spawnCubeY = Coords.blockToCube(spawnPoint.getY());
        int spawnCubeZ = Coords.blockToCube(spawnPoint.getZ());
        for (int cubeX = spawnCubeX - radiusXZForce; cubeX <= spawnCubeX + radiusXZForce; cubeX++) {
            for (int cubeZ = spawnCubeZ - radiusXZForce; cubeZ <= spawnCubeZ + radiusXZForce; cubeZ++) {
                for (int cubeY = spawnCubeY + radiusYForce; cubeY >= spawnCubeY - radiusYForce; cubeY--) {
                    serverCubeCache.getCube(cubeX, cubeY, cubeZ).getTickets().remove(this);
                }
            }
        }
    }

    private void addTickets(World world) {
        if (radiusXZGenerate < 0 || radiusYGenerate < 0) { return; }
        CubeProviderServer serverCubeCache = (CubeProviderServer) world.getChunkProvider();
        Rubic.LOGGER.info("Loading cubes for spawn...");
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
            Cube cube = serverCubeCache.getCubeNow(cubeX, cubeY, cubeZ, req);
            assert cube != null;
            if (dx <= radiusXZForce && dz <= radiusXZForce) { cube.getTickets().add(this); }
            generated.incrementAndGet();
            tellScreen(world, generated.get() * 100 / totalToGenerate);
            if (System.currentTimeMillis() >= lastTime.get() + progressReportInterval) {
                lastTime.set(System.currentTimeMillis());
                Rubic.LOGGER.info("Preparing spawn area: {}%", generated.get() * 100 / totalToGenerate);
            }
        });
        tellScreen(world, 100);
        Rubic.LOGGER.info("Preparing spawn area: 100%");
    }

    private static void tellScreen(World world, int percent) {
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) { return; }
        IMinecraftServerMessage progress = (IMinecraftServerMessage) server;
        progress.rdpl$setCurrentTask("Preparing spawn area");
        progress.rdpl$setPercentDone(percent);
        progress.rdpl$setUserMessage("menu.generatingTerrain");
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
