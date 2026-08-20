package mctmods.resourcedatapackloader.content.rubic.lighting;

import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.Coords;
import static mctmods.resourcedatapackloader.util.Coords.blockToLocal;
import static mctmods.resourcedatapackloader.util.Coords.localToBlock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import java.util.Arrays;

public class LightingManager implements ILightingManager {
    public static final int MAX_CLIENT_LIGHT_SCAN_DEPTH = 64;
    private final World world;
    private final ICubeLightEngine lightEngine;

    public LightingManager(World world) {
        this.world = world;
        this.lightEngine = new RubicLightEngine(world);
    }

    private CubeLightData getLightData(ICube cube) { return (CubeLightData) ((Cube) cube).getCubeLightData(); }

    @Override public void updateLightBetween(Chunk column, int localX, int y1, int y2, int localZ) { lightEngine.updateBetween(column, localX, y1, y2, localZ); }

    @Override public void onCubeLoad(ICube cube) {
        lightEngine.cubeLoaded(cube);
        tryScheduleOnLoadHeightChangeRelight(cube);
    }

    @Override public void onCreateCubeStorage(ICube cube, ExtendedBlockStorage storage) { lightEngine.cubeStorageMade(cube, storage); }

    @Override public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
        lightEngine.scheduleLightUpdate(lightType, pos);
        return true;
    }

    @Override public void processUpdates() { lightEngine.processLightUpdates(); }

    @Override public void processUpdatesOnAccess() {
        if (!world.isRemote) { processUpdates(); }
    }

    @Override public String getId() { return lightEngine.getId(); }

    @Override public void writeToNbt(ICube cube, NBTTagCompound lightingInfo) {
        int[] lastHeightmap = cube.getColumn().getHeightMap();
        lightingInfo.setIntArray("LastHeightMap", lastHeightmap);
    }

    @Override public void readFromNbt(ICube cube, NBTTagCompound lightingInfo) {
        CubeLightData lightData = getLightData(cube);
        lightData.lastHeightMap = lightingInfo.hasKey("LastHeightMap") ? lightingInfo.getIntArray("LastHeightMap") : null;
        if (lightData.lastHeightMap != null) {
            Arrays.fill(lightData.lastSaveHeightMapInfo, 0L);
            for (int i = 0; i < lightData.lastHeightMap.length; i++) {
                int cy = Coords.blockToCube(lightData.lastHeightMap[i] - 1);
                int flags = 0;
                if (cy >= cube.getY()) { flags |= 1; }
                if (cy <= cube.getY()) { flags |= 2; }
                int idx = i >> 5;
                int bit = (i & 31) << 1;
                long v = lightData.lastSaveHeightMapInfo[idx];
                v |= ((long) flags) << bit;
                lightData.lastSaveHeightMapInfo[idx] = v;
            }
        }
    }

    @Override public Cube.ICubeLightTrackingInfo createLightData() { return new CubeLightData(); }

    @Override public void onHeightUpdate(BlockPos pos) {
        if (!world.isRemote) { ((PlayerCubeMap) ((WorldServer) world).getPlayerChunkMap()).heightUpdated(pos.getX(), pos.getZ()); }
    }

    @Override public void onTrackCubeSurface(ICube cube) {
        if (!world.isRemote) {
            BlockPos min = cube.getCoords().getMinBlockPos();
            BlockPos max = cube.getCoords().getMaxBlockPos();
            for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(min, max.add(1, 1, 1))) {
                ((PlayerCubeMap) ((WorldServer) world).getPlayerChunkMap()).heightUpdated(pos.getX(), pos.getZ());
            }
            tryScheduleOnLoadHeightChangeRelight(cube);
        }
    }

    @Override public void doFirstLight(ICube cube) { lightEngine.firstLight(cube); }

    private void tryScheduleOnLoadHeightChangeRelight(ICube cube) {
        CubeLightData data = (CubeLightData) ((Cube) cube).getCubeLightData();
        if(data.lastHeightMap == null || !cube.isSurfaceTracked()) { return; }
        IColumnInternal column = cube.getColumn();
        LightingManager lightManager = (LightingManager) ((IRubicWorldInternal) cube.getWorld()).rdpl$getLightingManager();
        for (int i = 0; i < data.lastHeightMap.length; i++) {
            int localX = i & 0xF;
            int localZ = i >> 4;
            int currentY = column.getTopYWithStaging(localX, localZ) + 1;
            int lastY = data.lastHeightMap[i];
            if (currentY == lastY) { continue; }
            int minUpdateY = Math.min(currentY, lastY);
            int maxUpdateY = Math.max(currentY, lastY) - 1;
            int maxCubeY = Coords.blockToCube(maxUpdateY);
            int minCubeY = Coords.blockToCube(minUpdateY);
            int cubeY = cube.getY();
            if (minCubeY > cubeY || maxCubeY < cubeY) { continue; }
            int minLocal = 0;
            int maxLocal = 15;
            if (maxCubeY == cubeY) { maxLocal = blockToLocal(maxUpdateY); }
            if (minCubeY == cubeY) { minLocal = blockToLocal(minUpdateY); }
            lightManager.updateLightBetween(cube.getColumn(), localX, localToBlock(cubeY, minLocal), localToBlock(cubeY, maxLocal), localZ);
        }
        data.lastHeightMap = null;
    }

    public static class CubeLightData implements Cube.ICubeLightTrackingInfo {
        public int[] lastHeightMap = null;
        public long[] lastSaveHeightMapInfo = new long[8];

        @Override public boolean needsSaving(ICube cube) {
            int[] heightmap = cube.getColumn().getHeightMap();
            for (int i = 0; i < heightmap.length; i++) {
                int cy = Coords.blockToCube(heightmap[i] - 1);
                int idx = i >> 5;
                int bit = (i & 31) << 1;
                int flags = (int) ((lastSaveHeightMapInfo[idx] >>> bit) & 3);
                if (flags == 0) { return true; }
                int newFlags = 0;
                if (cy >= cube.getY()) { newFlags |= 1; }
                if (cy <= cube.getY()) { newFlags |= 2; }
                if (flags != newFlags) { return true; }
            }
            return false;
        }

        @Override public void markSaved(ICube cube) {
            Arrays.fill(lastSaveHeightMapInfo, 0L);
            int[] heightmap = cube.getColumn().getHeightMap();
            for (int i = 0; i < heightmap.length; i++) {
                int cy = Coords.blockToCube(heightmap[i] - 1);
                int flags = 0;
                if (cy >= cube.getY()) { flags |= 1; }
                if (cy <= cube.getY()) { flags |= 2; }
                int idx = i >> 5;
                int bit = (i & 31) << 1;
                long v = lastSaveHeightMapInfo[idx];
                v |= ((long) flags) << bit;
                lastSaveHeightMapInfo[idx] = v;
            }
        }
    }
}
