package mctmods.resourcedatapackloader.common.rubic.server.chunkio;

import mctmods.resourcedatapackloader.common.rubic.lighting.interfaces.ILightingManager;
import mctmods.resourcedatapackloader.common.rubic.world.ServerHeightMap;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.common.util.world.CubePos;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

class IONbtWriter {
    static NBTTagCompound write(Chunk column) {
        NBTTagCompound columnNbt = new NBTTagCompound();
        NBTTagCompound level = new NBTTagCompound();
        columnNbt.setTag(ChunkNbtKeys.LEVEL, level);
        stampVersion(columnNbt);
        writeBaseColumn(column, level);
        writeBiomes(column, level);
        writeOpacityIndex(column, level);
        MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Save(column, columnNbt));
        return columnNbt;
    }

    static NBTTagCompound write(final Cube cube) {
        NBTTagCompound cubeNbt = new NBTTagCompound();
        NBTTagCompound level = new NBTTagCompound();
        cubeNbt.setTag(ChunkNbtKeys.LEVEL, level);
        stampVersion(cubeNbt);
        writeBaseCube(cube, level);
        writeBlocks(cube, level);
        writeEntities(cube, level);
        writeTileEntities(cube, level);
        writeScheduledTicks(cube, level);
        writeLightingInfo(cube, level);
        writeBiomes(cube, level);
        return cubeNbt;
    }

    private static void stampVersion(NBTTagCompound nbt) {
        nbt.setInteger("DataVersion", FMLCommonHandler.instance().getDataFixer().version);
        FMLCommonHandler.instance().getDataFixer().writeVersionData(nbt);
    }

    private static void writeCaps(@Nullable CapabilityDispatcher caps, NBTTagCompound nbt) {
        if (caps == null) { return; }
        try { nbt.setTag(ChunkNbtKeys.FORGE_CAPS, caps.serializeNBT()); }
        catch (Exception exception) { ContentLog.LOGGER.error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception); }
    }

    private static void writeBaseColumn(Chunk column, NBTTagCompound nbt) {
        nbt.setInteger(ChunkNbtKeys.X, column.x);
        nbt.setInteger(ChunkNbtKeys.Z, column.z);
        nbt.setByte(ChunkNbtKeys.VERSION, (byte) 1);
        nbt.setLong(ChunkNbtKeys.INHABITED_TIME, column.getInhabitedTime());
        if (((IColumnInternal) column).pregenDone()) { nbt.setBoolean(ChunkNbtKeys.PREGEN_DONE, true); }
        writeCaps(column.getCapabilities(), nbt);
    }

    private static void writeBiomes(Chunk column, NBTTagCompound nbt) { nbt.setByteArray(ChunkNbtKeys.BIOMES, column.getBiomeArray()); }

    private static void writeOpacityIndex(Chunk column, NBTTagCompound nbt) { nbt.setByteArray(ChunkNbtKeys.OPACITY_INDEX, ((ServerHeightMap) ((IColumn) column).getOpacityIndex()).getData()); }

    private static void writeBaseCube(Cube cube, NBTTagCompound cubeNbt) {
        cubeNbt.setByte(ChunkNbtKeys.VERSION, (byte) 1);
        cubeNbt.setInteger(ChunkNbtKeys.X, cube.getX());
        cubeNbt.setInteger(ChunkNbtKeys.Y, cube.getY());
        cubeNbt.setInteger(ChunkNbtKeys.Z, cube.getZ());
        cubeNbt.setBoolean(ChunkNbtKeys.POPULATED, cube.isPopulated());
        cubeNbt.setBoolean(ChunkNbtKeys.SURFACE_TRACKED, cube.isSurfaceTracked());
        cubeNbt.setBoolean(ChunkNbtKeys.FULLY_POPULATED, cube.isFullyPopulated());
        cubeNbt.setBoolean(ChunkNbtKeys.INIT_LIGHT_DONE, cube.isInitialLightingDone());
        writeCaps(cube.getCapabilities(), cubeNbt);
    }

    private static void writeBlocks(Cube cube, NBTTagCompound cubeNbt) {
        ExtendedBlockStorage ebs = cube.getStorage();
        if (ebs == null) { return; }
        NBTTagList sectionList = new NBTTagList();
        NBTTagCompound section = new NBTTagCompound();
        sectionList.appendTag(section);
        cubeNbt.setTag(ChunkNbtKeys.SECTIONS, sectionList);
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
            add = BlockIdNibbles.putNibble(add, data.length, i, (id >> 12) & 0xF);
            add2neid = BlockIdNibbles.putNibble(add2neid, data.length, i, (id >> 16) & 0xF);
            abyte[i] = (byte) (id >> 4 & 255);
            data[i >> 1] |= (byte) ((id & 15) << ((i & 1) << 2));
        }
        section.setByteArray(ChunkNbtKeys.BLOCKS, abyte);
        section.setByteArray(ChunkNbtKeys.DATA, data);
        if (add != null) { section.setByteArray(ChunkNbtKeys.ADD, add); }
        if (add2neid != null) { section.setByteArray(ChunkNbtKeys.ADD2, add2neid); }
        section.setByteArray(ChunkNbtKeys.BLOCK_LIGHT, ebs.getBlockLight().getData());
        if (cube.getWorld().provider.hasSkyLight()) { section.setByteArray(ChunkNbtKeys.SKY_LIGHT, ebs.getSkyLight().getData()); }
    }

    private static void writeEntities(Cube cube, NBTTagCompound cubeNbt) {
        cube.getEntityContainer().writeToNbt(cubeNbt, ChunkNbtKeys.ENTITIES, entity -> {
            CubePos entityCube = CubePos.fromEntity(entity);
            if (!entityCube.equals(cube.getCoords())) { ContentLog.LOGGER.warn("Saved entity {} in cube ({},{},{}) to cube ({},{},{})! Entity thinks its in ({},{},{})", entity.getClass().getName(), entityCube.getX(), entityCube.getY(), entityCube.getZ(), cube.getX(), cube.getY(), cube.getZ(), entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ); }
        });
    }

    private static void writeTileEntities(Cube cube, NBTTagCompound cubeNbt) {
        NBTTagList nbtTileEntities = new NBTTagList();
        cubeNbt.setTag(ChunkNbtKeys.TILE_ENTITIES, nbtTileEntities);
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
        cubeNbt.setTag(ChunkNbtKeys.TILE_TICKS, nbtTicks);
        for (NextTickListEntry scheduledTick : scheduledTicks) {
            NBTTagCompound nbtScheduledTick = new NBTTagCompound();
            ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(scheduledTick.getBlock());
            nbtScheduledTick.setString(ChunkNbtKeys.TICK_BLOCK, resourcelocation.toString());
            nbtScheduledTick.setInteger(ChunkNbtKeys.X, scheduledTick.position.getX());
            nbtScheduledTick.setInteger(ChunkNbtKeys.Y, scheduledTick.position.getY());
            nbtScheduledTick.setInteger(ChunkNbtKeys.Z, scheduledTick.position.getZ());
            nbtScheduledTick.setInteger(ChunkNbtKeys.TICK_TIME, (int) (scheduledTick.scheduledTime - time));
            nbtScheduledTick.setInteger(ChunkNbtKeys.TICK_PRIORITY, scheduledTick.priority);
            nbtTicks.appendTag(nbtScheduledTick);
        }
    }

    private static void writeLightingInfo(Cube cube, NBTTagCompound cubeNbt) {
        ILightingManager lightingManager = ((IRubicWorldInternal) cube.getWorld()).rdpl$getLightingManager();
        cubeNbt.setString(ChunkNbtKeys.LIGHTING_INFO_TYPE, lightingManager.getId());
        NBTTagCompound lightingInfo = new NBTTagCompound();
        cubeNbt.setTag(ChunkNbtKeys.LIGHTING_INFO, lightingInfo);
        lightingManager.writeToNbt(cube, lightingInfo);
    }

    private static void writeBiomes(Cube cube, NBTTagCompound nbt) {
        byte[] biomes = cube.getBiomeArray();
        if (biomes != null) { nbt.setByteArray(ChunkNbtKeys.BIOMES_3D, biomes); }
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
