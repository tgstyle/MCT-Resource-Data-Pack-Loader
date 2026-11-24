package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldSettings;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentTreeStore {
    private static final String TAG = "rdpl_trees";
    private static final Map<Integer, Map<Long, Map<Long, TreeEntry>>> CHUNKS = new HashMap<>();
    private static final Map<Integer, Map<Long, TreeEntry>> OWNED = new HashMap<>();
    public static final List<BiConsumer<WorldServer, ChunkPos>> UNLOAD_LISTENERS = new ArrayList<>();

    public static TreeEntry get(WorldServer world, long pos) {
        Map<Long, TreeEntry> entries = chunkMap(world, chunkOf(pos));
        return entries == null ? null : entries.get(pos);
    }

    public static void put(WorldServer world, TreeEntry entry) {
        int dimension = world.provider.getDimension();
        Map<Long, TreeEntry> entries = chunks(dimension).computeIfAbsent(chunkOf(entry.pos), k -> new LinkedHashMap<>());
        TreeEntry old = entries.put(entry.pos, entry);
        if (old != null) { unindex(dimension, old); }
        index(dimension, entry);
        dirty(world, entry);
        ContentTreeClock.track(world, entry);
    }

    public static TreeEntry remove(WorldServer world, long pos) {
        Map<Long, TreeEntry> entries = chunkMap(world, chunkOf(pos));
        TreeEntry removed = entries == null ? null : entries.remove(pos);
        if (removed == null) { return null; }
        unindex(world.provider.getDimension(), removed);
        dirty(world, removed);
        return removed;
    }

    public static Collection<TreeEntry> entries(WorldServer world, int chunkX, int chunkZ) {
        Map<Long, TreeEntry> entries = chunkMap(world, chunkKey(chunkX, chunkZ));
        return entries == null ? Collections.emptyList() : entries.values();
    }

    public static TreeEntry ownerOf(WorldServer world, long position) {
        Map<Long, TreeEntry> owners = OWNED.get(world.provider.getDimension());
        return owners == null ? null : owners.get(position);
    }

    public static void addOwned(WorldServer world, TreeEntry entry, int packed) {
        entry.owned.add(packed);
        ownedIndex(world.provider.getDimension()).put(worldPos(entry, packed), entry);
    }

    public static void removeOwned(WorldServer world, TreeEntry entry, int packed) {
        entry.owned.remove(packed);
        ownedIndex(world.provider.getDimension()).remove(worldPos(entry, packed), entry);
    }

    public static void dirty(WorldServer world, TreeEntry entry) {
        BlockPos start = BlockPos.fromLong(entry.pos);
        world.getChunk(start).markDirty();
    }

    @SubscribeEvent public static void onChunkLoad(ChunkDataEvent.Load event) {
        if (event.getWorld().isRemote) { return; }
        int dimension = event.getWorld().provider.getDimension();
        ChunkPos pos = event.getChunk().getPos();
        Map<Long, TreeEntry> entries = new LinkedHashMap<>();
        NBTTagList list = event.getData().getTagList(TAG, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            TreeEntry entry = TreeEntry.read(list.getCompoundTagAt(i));
            entries.put(entry.pos, entry);
            index(dimension, entry);
        }
        if (!entries.isEmpty()) { chunks(dimension).put(chunkKey(pos.x, pos.z), entries); }
    }

    @SubscribeEvent public static void onChunkSave(ChunkDataEvent.Save event) {
        if (event.getWorld().isRemote) { return; }
        int dimension = event.getWorld().provider.getDimension();
        Map<Long, Map<Long, TreeEntry>> chunks = CHUNKS.get(dimension);
        if (chunks == null) { return; }
        Chunk chunk = event.getChunk();
        long key = chunkKey(chunk.getPos().x, chunk.getPos().z);
        Map<Long, TreeEntry> entries = chunks.get(key);
        if (entries == null) { return; }
        if (!entries.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (TreeEntry entry : entries.values()) { list.appendTag(entry.write()); }
            event.getData().setTag(TAG, list);
        }
        if (!chunk.isLoaded()) { release(dimension, chunks, key); }
    }

    @SubscribeEvent public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld().isRemote) { return; }
        int dimension = event.getWorld().provider.getDimension();
        ChunkPos pos = event.getChunk().getPos();
        long key = chunkKey(pos.x, pos.z);
        Map<Long, Map<Long, TreeEntry>> chunks = CHUNKS.get(dimension);
        Map<Long, TreeEntry> entries = chunks == null ? null : chunks.get(key);
        if (entries != null && !entries.isEmpty()) {
            ContentLog.LOGGER.info("Tree store {}: chunk {} {} unloaded, {} entries", dimension, pos.x, pos.z, entries.size());
        }
        if (entries != null && ((IRubicWorldSettings) event.getWorld().getWorldInfo()).rdpl$isRubic() && !event.getChunk().needsSaving(true)) { release(dimension, chunks, key); }
        for (BiConsumer<WorldServer, ChunkPos> listener : UNLOAD_LISTENERS) { listener.accept((WorldServer) event.getWorld(), pos); }
    }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) { return; }
        int dimension = event.getWorld().provider.getDimension();
        CHUNKS.remove(dimension);
        OWNED.remove(dimension);
        ContentTreeClock.forget(dimension);
    }

    private static Map<Long, TreeEntry> chunkMap(WorldServer world, long key) {
        Map<Long, Map<Long, TreeEntry>> chunks = CHUNKS.get(world.provider.getDimension());
        return chunks == null ? null : chunks.get(key);
    }

    private static void release(int dimension, Map<Long, Map<Long, TreeEntry>> chunks, long key) {
        for (TreeEntry entry : chunks.remove(key).values()) { unindex(dimension, entry); }
    }

    private static Map<Long, Map<Long, TreeEntry>> chunks(int dimension) {
        return CHUNKS.computeIfAbsent(dimension, k -> new HashMap<>());
    }

    private static Map<Long, TreeEntry> ownedIndex(int dimension) {
        return OWNED.computeIfAbsent(dimension, k -> new HashMap<>());
    }

    private static void index(int dimension, TreeEntry entry) {
        Map<Long, TreeEntry> owners = ownedIndex(dimension);
        for (int packed : entry.owned.toIntArray()) { owners.put(worldPos(entry, packed), entry); }
    }

    private static void unindex(int dimension, TreeEntry entry) {
        Map<Long, TreeEntry> owners = ownedIndex(dimension);
        for (int packed : entry.owned.toIntArray()) { owners.remove(worldPos(entry, packed), entry); }
    }

    private static long worldPos(TreeEntry entry, int packed) {
        return TreeEntry.at(BlockPos.fromLong(entry.pos), packed).toLong();
    }

    private static long chunkOf(long pos) {
        BlockPos start = BlockPos.fromLong(pos);
        return chunkKey(Coords.blockToCube(start.getX()), Coords.blockToCube(start.getZ()));
    }

    private static long chunkKey(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
