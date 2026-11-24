package mctmods.resourcedatapackloader.common.rubic.server.chunkio;

import mctmods.resourcedatapackloader.common.rubic.lighting.interfaces.ILightingManager;
import mctmods.resourcedatapackloader.common.rubic.world.ServerHeightMap;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IHeightMap;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.CubePos;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.address.AddressTools;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.util.Constants;

public class IONbtReader {
    @Nullable static Chunk readColumn(World world, int x, int z, NBTTagCompound nbt) {
        NBTTagCompound level = nbt.getCompoundTag(ChunkNbtKeys.LEVEL);
        Chunk column = readBaseColumn(world, x, z, level);
        if (column == null) { return null; }
        try {
            readBiomes(level, column);
            readOpacityIndex(level, column);
        }
        catch (IndexOutOfBoundsException corrupt) {
            ContentLog.LOGGER.error("Column is corrupted! The biomes or the height map of ({},{}) are missing or cut short ({}). Column will be regenerated.", x, z, corrupt.toString());
            return null;
        }
        readPregenDone(level, column);

        column.setModified(false);
        return column;
    }

    private static void readPregenDone(NBTTagCompound nbt, Chunk column) { if (nbt.getBoolean(ChunkNbtKeys.PREGEN_DONE)) { ((IColumnInternal) column).markPregenDone(); } }

    @Nullable private static Chunk readBaseColumn(World world, int x, int z, NBTTagCompound nbt) {
        checkVersion(nbt, "Column has wrong version: %d");
        int xCheck = nbt.getInteger(ChunkNbtKeys.X);
        int zCheck = nbt.getInteger(ChunkNbtKeys.Z);
        if (xCheck != x || zCheck != z) {
            ContentLog.LOGGER.warn("Column is corrupted! Expected ({},{}) but got ({},{}). Column will be regenerated.", x, z, xCheck, zCheck);
            return null;
        }
        Chunk column = new Chunk(world, x, z);
        column.setInhabitedTime(nbt.getLong(ChunkNbtKeys.INHABITED_TIME));
        readCaps(column.getCapabilities(), nbt);
        return column;
    }

    private static void checkVersion(NBTTagCompound nbt, String message, Object... position) {
        byte version = nbt.getByte(ChunkNbtKeys.VERSION);
        if (version == 1) { return; }
        Object[] args = Arrays.copyOf(position, position.length + 1);
        args[position.length] = version;
        throw new IllegalArgumentException(String.format(message, args));
    }

    private static void readCaps(@Nullable CapabilityDispatcher caps, NBTTagCompound nbt) { if (caps != null && nbt.hasKey(ChunkNbtKeys.FORGE_CAPS)) { caps.deserializeNBT(nbt.getCompoundTag(ChunkNbtKeys.FORGE_CAPS)); } }

    private static void readBiomes(NBTTagCompound nbt, Chunk column) { System.arraycopy(nbt.getByteArray(ChunkNbtKeys.BIOMES), 0, column.getBiomeArray(), 0, Cube.SIZE * Cube.SIZE); }

    private static void readOpacityIndex(NBTTagCompound nbt, Chunk chunk) {
        IHeightMap hmap = ((IColumn) chunk).getOpacityIndex();
        ((ServerHeightMap) hmap).readData(nbt.getByteArray(ChunkNbtKeys.OPACITY_INDEX));
    }

    @Nullable static Cube readCubeAsyncPart(Chunk column, final int cubeX, final int cubeY, final int cubeZ, NBTTagCompound nbt) {
        if (column.x != cubeX || column.z != cubeZ) { throw new IllegalArgumentException(String.format("Invalid column (%d, %d) for cube at (%d, %d, %d)", column.x, column.z, cubeX, cubeY, cubeZ)); }
        World world = column.getWorld();
        NBTTagCompound level = nbt.getCompoundTag(ChunkNbtKeys.LEVEL);
        Cube cube = readBaseCube(column, cubeX, cubeY, cubeZ, level);
        if (cube == null) { return null; }
        readBiomes(cube, level);
        readBlocks(level, world, cube);
        return cube;
    }

    static void readCubeSyncPart(Cube cube, World world, NBTTagCompound nbt) {
        cube.getColumn().preCacheCube(cube);
        NBTTagCompound level = nbt.getCompoundTag(ChunkNbtKeys.LEVEL);
        readEntities(level, world, cube);
        readTileEntities(level, world, cube);
        readScheduledBlockTicks(level, world);
        readLightingInfo(cube, level);
        cube.markSaved();
    }

    @Nullable private static Cube readBaseCube(Chunk column, int cubeX, int cubeY, int cubeZ, NBTTagCompound nbt) {
        checkVersion(nbt, "Cube at CubePos:(%d, %d, %d), has wrong version! %d", cubeX, cubeY, cubeZ);
        int xCheck = nbt.getInteger(ChunkNbtKeys.X);
        int yCheck = nbt.getInteger(ChunkNbtKeys.Y);
        int zCheck = nbt.getInteger(ChunkNbtKeys.Z);
        if (xCheck != cubeX || yCheck != cubeY || zCheck != cubeZ) {
            ContentLog.LOGGER.error("Cube is corrupted! Expected ({},{},{}) but got ({},{},{}). Cube will be regenerated.", cubeX, cubeY, cubeZ, xCheck, yCheck, zCheck);
            return null;
        }
        assert cubeX == column.x && cubeZ == column.z :
                String.format("Cube is corrupted! Cube (%d,%d,%d) does not match column (%d,%d).", cubeX, cubeY, cubeZ, column.x,
                        column.z);
        final Cube cube = new Cube(column, cubeY);
        cube.setPopulated(nbt.getBoolean(ChunkNbtKeys.POPULATED));
        cube.setSurfaceTracked(nbt.getBoolean(ChunkNbtKeys.SURFACE_TRACKED));
        cube.setFullyPopulated(nbt.getBoolean(ChunkNbtKeys.FULLY_POPULATED));
        cube.setInitialLightingDone(nbt.getBoolean(ChunkNbtKeys.INIT_LIGHT_DONE));
        readCaps(cube.getCapabilities(), nbt);
        return cube;
    }

    private static void readBlocks(NBTTagCompound nbt, World world, Cube cube) {
        boolean isEmpty = !nbt.hasKey(ChunkNbtKeys.SECTIONS);
        if (!isEmpty) {
            NBTTagList sectionList = nbt.getTagList(ChunkNbtKeys.SECTIONS, Constants.NBT.TAG_COMPOUND);
            cube.setStorageFromSave(decodeStorage(sectionList.getCompoundTagAt(0), world, cube.getY()));
        }
    }

    @SuppressWarnings("deprecation") public static ExtendedBlockStorage decodeStorage(NBTTagCompound nbt, World world, int cubeY) {
        ExtendedBlockStorage ebs = new ExtendedBlockStorage(Coords.cubeToMinBlock(cubeY), world.provider.hasSkyLight());
        byte[] abyte = nbt.getByteArray(ChunkNbtKeys.BLOCKS);
        NibbleArray data = new NibbleArray(nbt.getByteArray(ChunkNbtKeys.DATA));
        NibbleArray add = nbt.hasKey(ChunkNbtKeys.ADD, Constants.NBT.TAG_BYTE_ARRAY) ? new NibbleArray(nbt.getByteArray(ChunkNbtKeys.ADD)) : null;
        NibbleArray add2neid = nbt.hasKey(ChunkNbtKeys.ADD2, Constants.NBT.TAG_BYTE_ARRAY) ? new NibbleArray(nbt.getByteArray(ChunkNbtKeys.ADD2)) : null;
        for (int i = 0; i < 4096; i++) {
            int x = i & 15;
            int y = i >> 8 & 15;
            int z = i >> 4 & 15;
            int id = BlockIdNibbles.decode(i, abyte, data, add, add2neid);
            IBlockState state = Block.BLOCK_STATE_IDS.getByValue(id);
            ebs.getData().set(x, y, z, state == null ? Blocks.AIR.getDefaultState() : state);
        }
        ebs.setBlockLight(new NibbleArray(nbt.getByteArray(ChunkNbtKeys.BLOCK_LIGHT)));
        if (world.provider.hasSkyLight()) { ebs.setSkyLight(new NibbleArray(nbt.getByteArray(ChunkNbtKeys.SKY_LIGHT))); }
        ebs.recalculateRefCounts();
        return ebs;
    }

    private static void readEntities(NBTTagCompound nbt, World world, Cube cube) {
        cube.getEntityContainer().readFromNbt(nbt, ChunkNbtKeys.ENTITIES, world, entity -> {
            CubePos entityCube = CubePos.fromEntity(entity);
            if (!entityCube.equals(cube.getCoords())) { ContentLog.LOGGER.warn("Loaded entity {} in cube ({},{},{}) to cube ({},{},{})!", entity.getClass() .getName(), entityCube.getX(), entityCube.getY(), entityCube.getZ(), cube.getX(), cube.getY(), cube.getZ()); }
            entity.addedToChunk = true;
            entity.chunkCoordX = cube.getX();
            entity.chunkCoordY = cube.getY();
            entity.chunkCoordZ = cube.getZ();
        });
    }

    private static void readTileEntities(NBTTagCompound nbt, World world, Cube cube) {
        NBTTagList nbtTileEntities = nbt.getTagList(ChunkNbtKeys.TILE_ENTITIES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtTileEntities.tagCount(); i++) {
            NBTTagCompound nbtTileEntity = nbtTileEntities.getCompoundTagAt(i);
            TileEntity blockEntity = TileEntity.create(world, nbtTileEntity);
            if (blockEntity != null) {
                if (!cube.getCoords().containsBlock(blockEntity.getPos())) {
                    ContentLog.LOGGER.warn("TileEntity {} is not in cube at {}, tile entity will be skipped", blockEntity, cube.getCoords());
                    continue;
                }
                cube.addTileEntity(blockEntity);
            }
        }
    }

    private static void readScheduledBlockTicks(NBTTagCompound nbt, World world) {
        if (!(world instanceof WorldServer)) { return; }
        NBTTagList nbtScheduledTicks = nbt.getTagList(ChunkNbtKeys.TILE_TICKS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtScheduledTicks.tagCount(); i++) {
            NBTTagCompound nbtScheduledTick = nbtScheduledTicks.getCompoundTagAt(i);
            Block block;
            if (nbtScheduledTick.hasKey(ChunkNbtKeys.TICK_BLOCK, Constants.NBT.TAG_STRING)) { block = Block.getBlockFromName(nbtScheduledTick.getString(ChunkNbtKeys.TICK_BLOCK)); }
            else { block = Block.getBlockById(nbtScheduledTick.getInteger(ChunkNbtKeys.TICK_BLOCK)); }
            if (block == null) { continue; }
            world.scheduleBlockUpdate(
                    Coords.readPos(nbtScheduledTick),
                    block,
                    nbtScheduledTick.getInteger(ChunkNbtKeys.TICK_TIME),
                    nbtScheduledTick.getInteger(ChunkNbtKeys.TICK_PRIORITY)
            );
        }
    }

    private static void readLightingInfo(Cube cube, NBTTagCompound nbt) {
        ILightingManager lightingManager = ((IRubicWorldInternal) cube.getWorld()).rdpl$getLightingManager();
        String id = lightingManager.getId();
        String savedId = nbt.getString(ChunkNbtKeys.LIGHTING_INFO_TYPE);
        if (!id.equals(savedId)) {
            cube.setInitialLightingDone(false);
            ExtendedBlockStorage storage = cube.getStorage();
            if (storage != null) {
                if (cube.getWorld().provider.hasSkyLight()) { Arrays.fill(storage.getSkyLight().getData(), (byte) 0); }
                Arrays.fill(storage.getBlockLight().getData(), (byte) 0);
            }
            cube.setSurfaceTracked(false);
            lightingManager.readFromNbt(cube, new NBTTagCompound());
            return;
        }
        NBTTagCompound lightingInfo = nbt.getCompoundTag(ChunkNbtKeys.LIGHTING_INFO);
        lightingManager.readFromNbt(cube, lightingInfo);
    }

    private static void readBiomes(Cube cube, NBTTagCompound nbt) {
        if (nbt.hasKey(ChunkNbtKeys.BIOMES_3D)) { cube.setBiomeArray(nbt.getByteArray(ChunkNbtKeys.BIOMES_3D)); }
        if (nbt.hasKey(ChunkNbtKeys.BIOMES)) { cube.setBiomeArray(convertFromOldCubeBiomes(nbt.getByteArray(ChunkNbtKeys.BIOMES))); }
    }

    private static byte[] convertFromOldCubeBiomes(byte[] biomes) {
        byte[] newBiomes = new byte[4 * 4 * 4];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) { newBiomes[AddressTools.getBiomeAddress3d(x, y, z)] = biomes[getOldBiomeAddress(x << 1 | (y & 1), z << 1 | ((y >> 1) & 1))]; }
            }
        }
        return newBiomes;
    }

    public static int getOldBiomeAddress(int biomeX, int biomeZ) { return biomeX << 3 | biomeZ; }
}
