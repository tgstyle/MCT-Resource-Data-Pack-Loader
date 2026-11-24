package mctmods.resourcedatapackloader.common.ownership.claims;

import mctmods.resourcedatapackloader.common.environment.ContentDressLight;
import mctmods.resourcedatapackloader.common.pregen.ContentPristine;
import mctmods.resourcedatapackloader.common.rubic.lighting.interfaces.ILightingManager;
import mctmods.resourcedatapackloader.common.rubic.regionlib.impl.RegionNames;
import mctmods.resourcedatapackloader.common.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.common.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.common.rubic.server.chunkio.IONbtReader;
import mctmods.resourcedatapackloader.common.rubic.server.chunkio.RegionCubeStorage;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.CubePos;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.util.Constants;

public final class ClaimRestore {
    private static final String LEVEL = "Level";
    private static final String TILE_ENTITIES = "TileEntities";
    private static final String SECTIONS = "Sections";
    private static String lastKey = "";
    @Nullable private static NBTTagCompound lastLevel;

    private ClaimRestore() {}

    private static void unreadable(Object source, String kind, String coords, Exception cause) { ContentLog.LOGGER.warn("The pristine copy of {} could not be read at {} {}", source, kind, coords, cause); }

    public static boolean covers(WorldServer world, int chunkX, int chunkZ) {
        Path folder = folderOf(world);
        if (folder == null) { return false; }
        if (!GenHeights.rubic(world)) { return levelOf(folder, chunkX, chunkZ) != null; }
        if (!Files.isDirectory(folder.resolve(RegionNames.COLUMN_FOLDER))) { return false; }
        try (RegionCubeStorage storage = new RegionCubeStorage(holdingOf(world), folder)) { return storage.columnExists(new ChunkPos(chunkX, chunkZ)); }
        catch (IOException | RuntimeException unreadable) {
            unreadable(folder, "column", chunkX + ", " + chunkZ, unreadable);
            return false;
        }
    }

    public static boolean section(WorldServer world, int chunkX, int chunkZ, int sectionY) {
        Path folder = folderOf(world);
        if (folder == null) { return false; }
        return GenHeights.rubic(world) ? cube(world, folder, chunkX, sectionY, chunkZ) : vanilla(world, folder, chunkX, chunkZ, sectionY);
    }

    public static void finish(WorldServer world, int chunkX, int chunkZ) {
        lastKey = "";
        lastLevel = null;
        if (GenHeights.rubic(world)) {
            finishCubes(world, chunkX, chunkZ);
            return;
        }
        Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
        if (chunk == null) { return; }
        chunk.generateSkylightMap();
        chunk.setLightPopulated(false);
        chunk.resetRelightChecks();
        ContentDressLight.relit(chunk);
        chunk.markDirty();
        PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(chunkX, chunkZ);
        if (entry == null) { return; }
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP && world.getPlayerChunkMap().isPlayerWatchingChunk((EntityPlayerMP) player, chunkX, chunkZ)) { entry.sendToPlayer((EntityPlayerMP) player); }
        }
    }

    @Nullable private static Path folderOf(WorldServer world) {
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) { return null; }
        Path kept = ContentPristine.holdingFor(server);
        String saveFolder = world.provider.getSaveFolder();
        Path folder = saveFolder == null ? kept : kept.resolve(saveFolder);
        return Files.isDirectory(folder) ? folder : null;
    }

    private static Path holdingOf(WorldServer world) {
        MinecraftServer server = world.getMinecraftServer();
        return ContentPristine.holdingFor(server);
    }

    @Nullable private static NBTTagCompound levelOf(Path folder, int chunkX, int chunkZ) {
        String key = folder + ":" + chunkX + ":" + chunkZ;
        if (key.equals(lastKey)) { return lastLevel; }
        NBTTagCompound level = readLevel(folder, chunkX, chunkZ);
        lastKey = key;
        lastLevel = level;
        return level;
    }

    @Nullable private static NBTTagCompound readLevel(Path folder, int chunkX, int chunkZ) {
        File file = RegionNames.chunkFolder(folder).resolve(RegionNames.chunkRegion(Coords.chunkToRegion(chunkX), Coords.chunkToRegion(chunkZ))).toFile();
        if (!file.isFile()) { return null; }
        RegionFile region = new RegionFile(file);
        try {
            if (!region.isChunkSaved(chunkX & 31, chunkZ & 31)) { return null; }
            DataInputStream stream = region.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
            if (stream == null) { return null; }
            try { return CompressedStreamTools.read(stream).getCompoundTag(LEVEL); }
            finally { stream.close(); }
        }
        catch (IOException | RuntimeException unreadable) {
            unreadable(file, "chunk", chunkX + ", " + chunkZ, unreadable);
            return null;
        }
        finally {
            try { region.close(); }
            catch (IOException ignored) { }
        }
    }

    private static boolean vanilla(WorldServer world, Path folder, int chunkX, int chunkZ, int sectionY) {
        ChunkProviderServer provider = world.getChunkProvider();
        Chunk chunk = provider.getLoadedChunk(chunkX, chunkZ);
        NBTTagCompound level = levelOf(folder, chunkX, chunkZ);
        if (level == null || chunk == null || sectionY < 0 || sectionY >= chunk.getBlockStorageArray().length) { return false; }
        for (BlockPos at : new ArrayList<>(chunk.getTileEntityMap().keySet())) {
            if (at.getY() >> 4 == sectionY) { chunk.removeTileEntity(at); }
        }
        NBTTagCompound saved = sectionOf(level, sectionY);
        ExtendedBlockStorage storage = null;
        if (saved != null) {
            storage = new ExtendedBlockStorage(sectionY << 4, world.provider.hasSkyLight());
            NibbleArray add = saved.hasKey("Add", Constants.NBT.TAG_BYTE_ARRAY) ? new NibbleArray(saved.getByteArray("Add")) : null;
            storage.getData().setDataFromNBT(saved.getByteArray("Blocks"), new NibbleArray(saved.getByteArray("Data")), add);
            storage.setBlockLight(new NibbleArray(saved.getByteArray("BlockLight")));
            if (world.provider.hasSkyLight()) { storage.setSkyLight(new NibbleArray(saved.getByteArray("SkyLight"))); }
            storage.recalculateRefCounts();
        }
        chunk.getBlockStorageArray()[sectionY] = storage;
        restoreTiles(world, level, tag -> Coords.blockToCube(tag.getInteger("y")) == sectionY, chunk::addTileEntity);
        chunk.markDirty();
        return true;
    }

    private static void restoreTiles(WorldServer world, NBTTagCompound level, Predicate<NBTTagCompound> keep, Consumer<TileEntity> add) {
        NBTTagList held = level.getTagList(TILE_ENTITIES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < held.tagCount(); i++) {
            NBTTagCompound tag = held.getCompoundTagAt(i);
            if (!keep.test(tag)) { continue; }
            TileEntity entity = TileEntity.create(world, tag);
            if (entity != null) { add.accept(entity); }
        }
    }

    @Nullable private static NBTTagCompound sectionOf(NBTTagCompound level, int sectionY) {
        NBTTagList sections = level.getTagList(SECTIONS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < sections.tagCount(); i++) {
            NBTTagCompound section = sections.getCompoundTagAt(i);
            if (section.getByte("Y") == sectionY) { return section; }
        }
        return null;
    }

    private static boolean cube(WorldServer world, Path folder, int cubeX, int cubeY, int cubeZ) {
        Cube live = ((CubeProviderServer) world.getChunkProvider()).getCube(cubeX, cubeY, cubeZ, ICubeProviderServer.Requirement.LOAD);
        if (live == null) { return false; }
        NBTTagCompound level;
        try (RegionCubeStorage storage = new RegionCubeStorage(holdingOf(world), folder)) {
            NBTTagCompound nbt = storage.cubeExists(new CubePos(cubeX, cubeY, cubeZ)) ? storage.readCube(new CubePos(cubeX, cubeY, cubeZ)) : null;
            if (nbt == null) { return false; }
            level = nbt.getCompoundTag(LEVEL);
        }
        catch (IOException | RuntimeException unreadable) {
            unreadable(folder, "cube", cubeX + ", " + cubeY + ", " + cubeZ, unreadable);
            return false;
        }
        for (TileEntity old : new ArrayList<>(live.getTileEntityMap().values())) {
            old.invalidate();
            world.markTileEntityForRemoval(old);
        }
        live.getTileEntityMap().clear();
        NBTTagCompound saved = !level.hasKey(SECTIONS) ? null : level.getTagList(SECTIONS, Constants.NBT.TAG_COMPOUND).getCompoundTagAt(0);
        ExtendedBlockStorage storage = saved == null ? null : IONbtReader.decodeStorage(saved, world, cubeY);
        live.setStorage(storage);
        live.pushOpacity(storage);
        restoreTiles(world, level, tag -> true, entity -> { if (live.getCoords().containsBlock(entity.getPos())) { live.addTileEntity(entity); } });
        live.markDirty();
        return true;
    }

    private static void finishCubes(WorldServer world, int chunkX, int chunkZ) {
        Chunk column = ((CubeProviderServer) world.getChunkProvider()).getLoadedColumn(chunkX, chunkZ);
        if (column == null) { return; }
        ILightingManager lighting = ((IRubicWorldInternal) world).rdpl$getLightingManager();
        PlayerCubeMap watchers = (PlayerCubeMap) world.getPlayerChunkMap();
        List<EntityPlayerMP> players = new ArrayList<>();
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP) { players.add((EntityPlayerMP) player); }
        }
        for (Cube cube : new ArrayList<>(((IColumn) column).loadedCubes())) {
            ExtendedBlockStorage storage = cube.getStorage();
            if (storage != null) {
                if (world.provider.hasSkyLight()) { Arrays.fill(storage.getSkyLight().getData(), (byte) 0); }
                Arrays.fill(storage.getBlockLight().getData(), (byte) 0);
            }
            lighting.doFirstLight(cube);
            cube.markDirty();
            for (EntityPlayerMP player : players) {
                if (watchers.isPlayerWatchingCube(player, cube.getX(), cube.getY(), cube.getZ())) { watchers.scheduleSendCubeToPlayer(cube, player); }
            }
        }
        column.markDirty();
        for (int x = 0; x < ICube.SIZE; x++) {
            for (int z = 0; z < ICube.SIZE; z++) { watchers.heightUpdated(Coords.localToBlock(chunkX, x), Coords.localToBlock(chunkZ, z)); }
        }
    }
}
