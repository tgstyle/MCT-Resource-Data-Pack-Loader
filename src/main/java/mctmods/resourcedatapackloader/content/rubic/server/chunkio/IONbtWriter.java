package mctmods.resourcedatapackloader.content.rubic.server.chunkio;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.lighting.ILightingManager;
import mctmods.resourcedatapackloader.content.rubic.world.ClientHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.ServerHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import java.util.ArrayList;
import java.util.List;
import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

class IONbtWriter {
    static NBTTagCompound write(Chunk column) {
        NBTTagCompound columnNbt = new NBTTagCompound();
        NBTTagCompound level = new NBTTagCompound();
        columnNbt.setTag("Level", level);
        columnNbt.setInteger("DataVersion", FMLCommonHandler.instance().getDataFixer().version);
        FMLCommonHandler.instance().getDataFixer().writeVersionData(columnNbt);
        writeBaseColumn(column, level);
        writeBiomes(column, level);
        writeOpacityIndex(column, level);
        EVENT_BUS.post(new ChunkDataEvent.Save(column, columnNbt));
        return columnNbt;
    }

    static NBTTagCompound write(final Cube cube) {
        NBTTagCompound cubeNbt = new NBTTagCompound();
        NBTTagCompound level = new NBTTagCompound();
        cubeNbt.setTag("Level", level);
        cubeNbt.setInteger("DataVersion", FMLCommonHandler.instance().getDataFixer().version);
        FMLCommonHandler.instance().getDataFixer().writeVersionData(cubeNbt);
        writeBaseCube(cube, level);
        writeBlocks(cube, level);
        writeEntities(cube, level);
        writeTileEntities(cube, level);
        writeScheduledTicks(cube, level);
        writeLightingInfo(cube, level);
        writeBiomes(cube, level);
        return cubeNbt;
    }

    private static void writeBaseColumn(Chunk column, NBTTagCompound nbt) {
        nbt.setInteger("x", column.x);
        nbt.setInteger("z", column.z);
        nbt.setByte("v", (byte) 1);
        nbt.setLong("InhabitedTime", column.getInhabitedTime());
        if (((IColumnInternal) column).pregenDone()) { nbt.setBoolean("PregenDone", true); }
        if (column.getCapabilities() != null) {
            try {
                nbt.setTag("ForgeCaps", column.getCapabilities().serializeNBT());
            } catch (Exception exception) {
                Rubic.LOGGER.error("A capability provider has thrown an exception trying to write state. It will not persist. "
                        + "Report this to the mod author", exception);
            }
        }
    }

    private static void writeBiomes(Chunk column, NBTTagCompound nbt) { nbt.setByteArray("Biomes", column.getBiomeArray()); }

    private static void writeOpacityIndex(Chunk column, NBTTagCompound nbt) {
        IHeightMap hmap = ((IColumn) column).getOpacityIndex();
        if (hmap instanceof ServerHeightMap) { nbt.setByteArray("OpacityIndex", ((ServerHeightMap) hmap).getData()); }
        else { nbt.setByteArray("OpacityIndexClient", ((ClientHeightMap) hmap).getData()); }
    }

    private static void writeBaseCube(Cube cube, NBTTagCompound cubeNbt) {
        cubeNbt.setByte("v", (byte) 1);
        cubeNbt.setInteger("x", cube.getX());
        cubeNbt.setInteger("y", cube.getY());
        cubeNbt.setInteger("z", cube.getZ());
        cubeNbt.setBoolean("populated", cube.isPopulated());
        cubeNbt.setBoolean("isSurfaceTracked", cube.isSurfaceTracked());
        cubeNbt.setBoolean("fullyPopulated", cube.isFullyPopulated());
        cubeNbt.setBoolean("initLightDone", cube.isInitialLightingDone());
        if (cube.getCapabilities() != null) {
            try {
                cubeNbt.setTag("ForgeCaps", cube.getCapabilities().serializeNBT());
            } catch (Exception exception) {
                Rubic.LOGGER.error("A capability provider has thrown an exception trying to write state. It will not persist. "
                        + "Report this to the mod author", exception);
            }
        }
    }

    private static void writeBlocks(Cube cube, NBTTagCompound cubeNbt) {
        ExtendedBlockStorage ebs = cube.getStorage();
        if (ebs == null) { return; }
        NBTTagList sectionList = new NBTTagList();
        NBTTagCompound section = new NBTTagCompound();
        sectionList.appendTag(section);
        cubeNbt.setTag("Sections", sectionList);
        byte[] abyte = new byte[Cube.SIZE * Cube.SIZE * Cube.SIZE];
        byte[] data = new byte[abyte.length / 2];
        byte[] add = null;
        byte[] add2neid = null;
        BlockStateContainer states = ebs.getData();
        @SuppressWarnings("deprecation") ObjectIntIdentityMap<IBlockState> ids = Block.BLOCK_STATE_IDS;
        IBlockState airState = Blocks.AIR.getDefaultState();
        int airId = ids.get(airState);
        IBlockState lastState = airState;
        int lastId = airId;
        for (int i = 0; i < abyte.length; ++i) {
            IBlockState state = states.get(i & 15, i >> 8 & 15, i >> 4 & 15);
            int id;
            if (state == airState) { id = airId; }
            else {
                if (state != lastState) {
                    lastState = state;
                    lastId = ids.get(state);
                }
                id = lastId;
            }
            int in1 = (id >> 12) & 0xF;
            int in2 = (id >> 16) & 0xF;
            int nibble = i >> 1;
            int shift = (i & 1) << 2;
            if (in1 != 0) {
                if (add == null) { add = new byte[data.length]; }
                add[nibble] |= (byte) (in1 << shift);
            }
            if (in2 != 0) {
                if (add2neid == null) { add2neid = new byte[data.length]; }
                add2neid[nibble] |= (byte) (in2 << shift);
            }
            abyte[i] = (byte) (id >> 4 & 255);
            data[nibble] |= (byte) ((id & 15) << shift);
        }
        section.setByteArray("Blocks", abyte);
        section.setByteArray("Data", data);
        if (add != null) { section.setByteArray("Add", add); }
        if (add2neid != null) { section.setByteArray("Add2", add2neid); }
        section.setByteArray("BlockLight", ebs.getBlockLight().getData());
        if (cube.getWorld().provider.hasSkyLight()) { section.setByteArray("SkyLight", ebs.getSkyLight().getData()); }
    }

    private static void writeEntities(Cube cube, NBTTagCompound cubeNbt) {
        cube.getEntityContainer().writeToNbt(cubeNbt, "Entities", entity -> {
            int cubeX = Coords.getCubeXForEntity(entity);
            int cubeY = Coords.getCubeYForEntity(entity);
            int cubeZ = Coords.getCubeZForEntity(entity);
            if (cubeX != cube.getX() || cubeY != cube.getY() || cubeZ != cube.getZ()) {
                Rubic.LOGGER.warn("Saved entity {} in cube ({},{},{}) to cube ({},{},{})! Entity thinks its in ({},{},{})", entity.getClass().getName(), cubeX, cubeY, cubeZ, cube.getX(), cube.getY(), cube.getZ(), entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ);
            }
        });
    }

    private static void writeTileEntities(Cube cube, NBTTagCompound cubeNbt) {
        NBTTagList nbtTileEntities = new NBTTagList();
        cubeNbt.setTag("TileEntities", nbtTileEntities);
        for (TileEntity blockEntity : cube.getTileEntityMap().values()) {
            NBTTagCompound nbtTileEntity = new NBTTagCompound();
            blockEntity.writeToNBT(nbtTileEntity);
            nbtTileEntities.appendTag(nbtTileEntity);
        }
    }

    private static void writeScheduledTicks(Cube cube, NBTTagCompound cubeNbt) {
        Iterable<NextTickListEntry> scheduledTicks = rdpl$getScheduledTicks(cube);
        long time = cube.getWorld().getTotalWorldTime();
        NBTTagList nbtTicks = new NBTTagList();
        cubeNbt.setTag("TileTicks", nbtTicks);
        for (NextTickListEntry scheduledTick : scheduledTicks) {
            NBTTagCompound nbtScheduledTick = new NBTTagCompound();
            ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(scheduledTick.getBlock());
            nbtScheduledTick.setString("i", resourcelocation.toString());
            nbtScheduledTick.setInteger("x", scheduledTick.position.getX());
            nbtScheduledTick.setInteger("y", scheduledTick.position.getY());
            nbtScheduledTick.setInteger("z", scheduledTick.position.getZ());
            nbtScheduledTick.setInteger("t", (int) (scheduledTick.scheduledTime - time));
            nbtScheduledTick.setInteger("p", scheduledTick.priority);
            nbtTicks.appendTag(nbtScheduledTick);
        }
    }

    private static void writeLightingInfo(Cube cube, NBTTagCompound cubeNbt) {
        ILightingManager lightingManager = ((IRubicWorldInternal) cube.getWorld()).rdpl$getLightingManager();
        cubeNbt.setString("LightingInfoType", lightingManager.getId());
        NBTTagCompound lightingInfo = new NBTTagCompound();
        cubeNbt.setTag("LightingInfo", lightingInfo);
        lightingManager.writeToNbt(cube, lightingInfo);
    }

    private static void writeBiomes(Cube cube, NBTTagCompound nbt) {
        byte[] biomes = cube.getBiomeArray();
        if (biomes != null)
            nbt.setByteArray("Biomes3D", biomes);
    }

    private static List<NextTickListEntry> rdpl$getScheduledTicks(Cube cube) {
        ArrayList<NextTickListEntry> out = new ArrayList<>();
        if (!(cube.getWorld() instanceof WorldServer)) { return out; }
        WorldServer worldServer = cube.getWorld();
        out.addAll(((IRubicWorldInternal.IServer) worldServer).rdpl$getScheduledTicks().getForCube(cube.getCoords()));
        out.addAll(((IRubicWorldInternal.IServer) worldServer).rdpl$getThisTickScheduledTicks().getForCube(cube.getCoords()));
        return out;
    }
}
