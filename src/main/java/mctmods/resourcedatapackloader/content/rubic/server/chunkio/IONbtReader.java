package mctmods.resourcedatapackloader.content.rubic.server.chunkio;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.lighting.ILightingManager;
import mctmods.resourcedatapackloader.content.rubic.world.ServerHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.AddressTools;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.Constants;
import java.util.Arrays;
import javax.annotation.Nullable;

public class IONbtReader {
    @Nullable static Chunk readColumn(World world, int x, int z, NBTTagCompound nbt) {
        NBTTagCompound level = nbt.getCompoundTag("Level");
        Chunk column = readBaseColumn(world, x, z, level);
        if (column == null) { return null; }
        readBiomes(level, column);
        readOpacityIndex(level, column);

        column.setModified(false);
        return column;
    }

    @Nullable private static Chunk readBaseColumn(World world, int x, int z, NBTTagCompound nbt) {
        byte version = nbt.getByte("v");
        if (version != 1) { throw new IllegalArgumentException(String.format("Column has wrong version: %d", version)); }
        int xCheck = nbt.getInteger("x");
        int zCheck = nbt.getInteger("z");
        if (xCheck != x || zCheck != z) {
            Rubic.LOGGER
                    .warn("Column is corrupted! Expected ({},{}) but got ({},{}). Column will be regenerated.", x, z, xCheck, zCheck);
            return null;
        }
        Chunk column = new Chunk(world, x, z);
        column.setInhabitedTime(nbt.getLong("InhabitedTime"));
        if (column.getCapabilities() != null && nbt.hasKey("ForgeCaps")) { column.getCapabilities().deserializeNBT(nbt.getCompoundTag("ForgeCaps")); }
        return column;
    }

    private static void readBiomes(NBTTagCompound nbt, Chunk column) {
        System.arraycopy(nbt.getByteArray("Biomes"), 0, column.getBiomeArray(), 0, Cube.SIZE * Cube.SIZE);
    }

    private static void readOpacityIndex(NBTTagCompound nbt, Chunk chunk) {
        IHeightMap hmap = ((IColumn) chunk).getOpacityIndex();
        ((ServerHeightMap) hmap).readData(nbt.getByteArray("OpacityIndex"));
    }

    @Nullable static Cube readCubeAsyncPart(Chunk column, final int cubeX, final int cubeY, final int cubeZ, NBTTagCompound nbt) {
        if (column.x != cubeX || column.z != cubeZ) {
            throw new IllegalArgumentException(String.format("Invalid column (%d, %d) for cube at (%d, %d, %d)",
                    column.x, column.z, cubeX, cubeY, cubeZ));
        }
        World world = column.getWorld();
        NBTTagCompound level = nbt.getCompoundTag("Level");
        Cube cube = readBaseCube(column, cubeX, cubeY, cubeZ, level);
        if (cube == null) { return null; }
        readBiomes(cube, level);
        readBlocks(level, world, cube);
        return cube;
    }

    static void readCubeSyncPart(Cube cube, World world, NBTTagCompound nbt) {
        cube.getColumn().preCacheCube(cube);
        NBTTagCompound level = nbt.getCompoundTag("Level");
        readEntities(level, world, cube);
        readTileEntities(level, world, cube);
        readScheduledBlockTicks(level, world);
        readLightingInfo(cube, level);
        cube.markSaved();
    }

    @Nullable private static Cube readBaseCube(Chunk column, int cubeX, int cubeY, int cubeZ, NBTTagCompound nbt) {
        byte version = nbt.getByte("v");
        if (version != 1) { throw new IllegalArgumentException(String.format("Cube at CubePos:(%d, %d, %d), has wrong version! %d", cubeX, cubeY, cubeZ, version)); }
        int xCheck = nbt.getInteger("x");
        int yCheck = nbt.getInteger("y");
        int zCheck = nbt.getInteger("z");
        if (xCheck != cubeX || yCheck != cubeY || zCheck != cubeZ) {
            Rubic.LOGGER.error("Cube is corrupted! Expected ({},{},{}) but got ({},{},{}). Cube will be regenerated.", cubeX, cubeY, cubeZ, xCheck, yCheck, zCheck);
            return null;
        }
        assert cubeX == column.x && cubeZ == column.z :
                String.format("Cube is corrupted! Cube (%d,%d,%d) does not match column (%d,%d).", cubeX, cubeY, cubeZ, column.z,
                        column.z);
        final Cube cube = new Cube(column, cubeY);
        cube.setPopulated(nbt.getBoolean("populated"));
        cube.setSurfaceTracked(nbt.getBoolean("isSurfaceTracked"));
        cube.setFullyPopulated(nbt.getBoolean("fullyPopulated"));
        cube.setInitialLightingDone(nbt.getBoolean("initLightDone"));
        if (cube.getCapabilities() != null && nbt.hasKey("ForgeCaps")) { cube.getCapabilities().deserializeNBT(nbt.getCompoundTag("ForgeCaps")); }
        return cube;
    }

    @SuppressWarnings("deprecation") private static void readBlocks(NBTTagCompound nbt, World world, Cube cube) {
        boolean isEmpty = !nbt.hasKey("Sections");
        if (!isEmpty) {
            NBTTagList sectionList = nbt.getTagList("Sections", 10);
            nbt = sectionList.getCompoundTagAt(0);
            ExtendedBlockStorage ebs = new ExtendedBlockStorage(Coords.cubeToMinBlock(cube.getY()), cube.getWorld().provider.hasSkyLight());
            byte[] abyte = nbt.getByteArray("Blocks");
            NibbleArray data = new NibbleArray(nbt.getByteArray("Data"));
            NibbleArray add = nbt.hasKey("Add", Constants.NBT.TAG_BYTE_ARRAY) ? new NibbleArray(nbt.getByteArray("Add")) : null;
            NibbleArray add2neid = nbt.hasKey("Add2", Constants.NBT.TAG_BYTE_ARRAY) ? new NibbleArray(nbt.getByteArray("Add2")) : null;
            for (int i = 0; i < 4096; i++) {
                int x = i & 15;
                int y = i >> 8 & 15;
                int z = i >> 4 & 15;
                int toAdd = add == null ? 0 : add.getFromIndex(i);
                toAdd = (toAdd & 0xF) | (add2neid == null ? 0 : add2neid.getFromIndex(i) << 4);
                int id = (toAdd << 12) | ((abyte[i] & 0xFF) << 4) | data.getFromIndex(i);
                IBlockState state = Block.BLOCK_STATE_IDS.getByValue(id);
                ebs.getData().set(x, y, z, state == null ? Blocks.AIR.getDefaultState() : state);
            }
            ebs.setBlockLight(new NibbleArray(nbt.getByteArray("BlockLight")));
            if (world.provider.hasSkyLight()) { ebs.setSkyLight(new NibbleArray(nbt.getByteArray("SkyLight"))); }
            ebs.recalculateRefCounts();
            cube.setStorageFromSave(ebs);
        }
    }

    private static void readEntities(NBTTagCompound nbt, World world, Cube cube) {
        cube.getEntityContainer().readFromNbt(nbt, "Entities", world, entity -> {
            int entityCubeX = Coords.getCubeXForEntity(entity);
            int entityCubeY = Coords.getCubeYForEntity(entity);
            int entityCubeZ = Coords.getCubeZForEntity(entity);
            if (entityCubeX != cube.getX() || entityCubeY != cube.getY() || entityCubeZ != cube.getZ()) {
                Rubic.LOGGER.warn("Loaded entity {} in cube ({},{},{}) to cube ({},{},{})!", entity.getClass()
                        .getName(), entityCubeX, entityCubeY, entityCubeZ, cube.getX(), cube.getY(), cube.getZ());
            }
            entity.addedToChunk = true;
            entity.chunkCoordX = cube.getX();
            entity.chunkCoordY = cube.getY();
            entity.chunkCoordZ = cube.getZ();
        });
    }

    private static void readTileEntities(NBTTagCompound nbt, World world, Cube cube) {
        NBTTagList nbtTileEntities = nbt.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nbtTileEntities.tagCount(); i++) {
            NBTTagCompound nbtTileEntity = nbtTileEntities.getCompoundTagAt(i);
            TileEntity blockEntity = TileEntity.create(world, nbtTileEntity);
            if (blockEntity != null) {
                if (!cube.getCoords().containsBlock(blockEntity.getPos())) {
                    Rubic.LOGGER.warn("TileEntity {} is not in cube at {}, tile entity will be skipped", blockEntity, cube.getCoords());
                    continue;
                }
                cube.addTileEntity(blockEntity);
            }
        }
    }

    private static void readScheduledBlockTicks(NBTTagCompound nbt, World world) {
        if (!(world instanceof WorldServer)) { return; }
        NBTTagList nbtScheduledTicks = nbt.getTagList("TileTicks", 10);
        for (int i = 0; i < nbtScheduledTicks.tagCount(); i++) {
            NBTTagCompound nbtScheduledTick = nbtScheduledTicks.getCompoundTagAt(i);
            Block block;
            if (nbtScheduledTick.hasKey("i", Constants.NBT.TAG_STRING)) { block = Block.getBlockFromName(nbtScheduledTick.getString("i")); }
            else { block = Block.getBlockById(nbtScheduledTick.getInteger("i")); }
            if (block == null) { continue; }
            world.scheduleBlockUpdate(
                    new BlockPos(
                            nbtScheduledTick.getInteger("x"),
                            nbtScheduledTick.getInteger("y"),
                            nbtScheduledTick.getInteger("z")
                    ),
                    block,
                    nbtScheduledTick.getInteger("t"),
                    nbtScheduledTick.getInteger("p")
            );
        }
    }

    private static void readLightingInfo(Cube cube, NBTTagCompound nbt) {
        ILightingManager lightingManager = ((IRubicWorldInternal) cube.getWorld()).rdpl$getLightingManager();
        String id = lightingManager.getId();
        String savedId = nbt.getString("LightingInfoType");
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
        NBTTagCompound lightingInfo = nbt.getCompoundTag("LightingInfo");
        lightingManager.readFromNbt(cube, lightingInfo);
    }

    private static void readBiomes(Cube cube, NBTTagCompound nbt) {
        if (nbt.hasKey("Biomes3D")) { cube.setBiomeArray(nbt.getByteArray("Biomes3D")); }
        if (nbt.hasKey("Biomes")) { cube.setBiomeArray(convertFromOldCubeBiomes(nbt.getByteArray("Biomes"))); }
    }

    private static byte[] convertFromOldCubeBiomes(byte[] biomes) {
        byte[] newBiomes = new byte[4 * 4 * 4];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) {
                    newBiomes[AddressTools.getBiomeAddress3d(x, y, z)] = biomes[getOldBiomeAddress(x << 1 | (y & 1), z << 1 | ((y >> 1) & 1))];
                }
            }
        }
        return newBiomes;
    }

    public static int getOldBiomeAddress(int biomeX, int biomeZ) { return biomeX << 3 | biomeZ; }
}
