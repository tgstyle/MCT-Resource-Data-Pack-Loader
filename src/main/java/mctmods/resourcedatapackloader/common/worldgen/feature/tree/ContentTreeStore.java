package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentTreeStore {
    public static final Capability<Held> TREES = CapabilityManager.get(new CapabilityToken<>() {});
    private static final ResourceLocation ID = ResourceDataPackLoader.id("trees");
    private static final String TAG = "trees";
    private static final Map<ResourceKey<Level>, Map<Long, Map<Long, TreeEntry>>> CHUNKS = new HashMap<>();
    private static final Map<ResourceKey<Level>, Map<Long, TreeEntry>> OWNED = new HashMap<>();
    public static final List<BiConsumer<ServerLevel, ChunkPos>> UNLOAD_LISTENERS = new ArrayList<>();

    private ContentTreeStore() {}

    public static void register(RegisterCapabilitiesEvent event) { event.register(Held.class); }

    public static void onAttach(AttachCapabilitiesEvent<LevelChunk> event) { event.addCapability(ID, new Held()); }

    public static TreeEntry get(ServerLevel level, long pos) {
        Map<Long, TreeEntry> entries = chunkMap(level, chunkOf(pos));
        return entries == null ? null : entries.get(pos);
    }

    public static void put(ServerLevel level, TreeEntry entry) {
        ResourceKey<Level> dimension = level.dimension();
        Map<Long, TreeEntry> entries = CHUNKS.computeIfAbsent(dimension, k -> new HashMap<>()).computeIfAbsent(chunkOf(entry.pos), k -> new LinkedHashMap<>());
        TreeEntry old = entries.put(entry.pos, entry);
        if (old != null) { unindex(dimension, old); }
        index(dimension, entry);
        dirty(level, entry);
        ContentTreeClock.track(level, entry);
    }

    public static TreeEntry remove(ServerLevel level, long pos) {
        Map<Long, TreeEntry> entries = chunkMap(level, chunkOf(pos));
        TreeEntry removed = entries == null ? null : entries.remove(pos);
        if (removed == null) { return null; }
        unindex(level.dimension(), removed);
        dirty(level, removed);
        return removed;
    }

    public static Collection<TreeEntry> entries(ServerLevel level, int chunkX, int chunkZ) {
        Map<Long, TreeEntry> entries = chunkMap(level, ChunkPos.asLong(chunkX, chunkZ));
        return entries == null ? Collections.emptyList() : entries.values();
    }

    public static TreeEntry ownerOf(ServerLevel level, long position) {
        Map<Long, TreeEntry> owners = OWNED.get(level.dimension());
        return owners == null ? null : owners.get(position);
    }

    public static void addOwned(ServerLevel level, TreeEntry entry, int packed) {
        entry.owned.add(packed);
        ownedIndex(level.dimension()).put(worldPos(entry, packed), entry);
    }

    public static void removeOwned(ServerLevel level, TreeEntry entry, int packed) {
        entry.owned.remove(packed);
        ownedIndex(level.dimension()).remove(worldPos(entry, packed), entry);
    }

    public static void dirty(ServerLevel level, TreeEntry entry) {
        BlockPos start = BlockPos.of(entry.pos);
        LevelChunk chunk = level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(start.getX()), SectionPos.blockToSectionCoord(start.getZ()));
        if (chunk == null) { return; }
        Map<Long, TreeEntry> entries = chunkMap(level, chunk.getPos().toLong());
        ListTag list = new ListTag();
        if (entries != null) {
            for (TreeEntry each : entries.values()) { list.add(each.write()); }
        }
        chunk.getCapability(TREES).ifPresent(held -> held.keep(list));
        chunk.setUnsaved(true);
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) { return; }
        ListTag list = chunk.getCapability(TREES).map(Held::trees).orElseGet(ListTag::new);
        if (list.isEmpty()) { return; }
        ResourceKey<Level> dimension = level.dimension();
        HolderGetter<Block> blocks = level.holderLookup(Registries.BLOCK);
        Map<Long, TreeEntry> entries = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            TreeEntry entry = TreeEntry.read(blocks, list.getCompound(i));
            entries.put(entry.pos, entry);
            index(dimension, entry);
        }
        CHUNKS.computeIfAbsent(dimension, k -> new HashMap<>()).put(chunk.getPos().toLong(), entries);
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        ResourceKey<Level> dimension = level.dimension();
        ChunkPos pos = event.getChunk().getPos();
        long key = pos.toLong();
        Map<Long, Map<Long, TreeEntry>> chunks = CHUNKS.get(dimension);
        Map<Long, TreeEntry> entries = chunks == null ? null : chunks.get(key);
        if (entries != null && !entries.isEmpty()) {
            ContentLog.LOGGER.info("Tree store {}: chunk {} {} unloaded, {} entries", dimension.location(), pos.x, pos.z, entries.size());
        }
        if (entries != null) { release(dimension, chunks, key); }
        for (BiConsumer<ServerLevel, ChunkPos> listener : UNLOAD_LISTENERS) { listener.accept(level, pos); }
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        CHUNKS.remove(level.dimension());
        OWNED.remove(level.dimension());
        ContentTreeClock.forget(level.dimension());
    }

    public static void clear() {
        CHUNKS.clear();
        OWNED.clear();
    }

    private static Map<Long, TreeEntry> chunkMap(ServerLevel level, long key) {
        Map<Long, Map<Long, TreeEntry>> chunks = CHUNKS.get(level.dimension());
        return chunks == null ? null : chunks.get(key);
    }

    private static void release(ResourceKey<Level> dimension, Map<Long, Map<Long, TreeEntry>> chunks, long key) {
        for (TreeEntry entry : chunks.remove(key).values()) { unindex(dimension, entry); }
    }

    private static Map<Long, TreeEntry> ownedIndex(ResourceKey<Level> dimension) {
        return OWNED.computeIfAbsent(dimension, k -> new HashMap<>());
    }

    private static void index(ResourceKey<Level> dimension, TreeEntry entry) {
        Map<Long, TreeEntry> owners = ownedIndex(dimension);
        for (int packed : entry.owned.toIntArray()) { owners.put(worldPos(entry, packed), entry); }
    }

    private static void unindex(ResourceKey<Level> dimension, TreeEntry entry) {
        Map<Long, TreeEntry> owners = ownedIndex(dimension);
        for (int packed : entry.owned.toIntArray()) { owners.remove(worldPos(entry, packed), entry); }
    }

    private static long worldPos(TreeEntry entry, int packed) {
        return BlockPos.of(entry.pos).offset(TreeEntry.unpackX(packed), TreeEntry.unpackY(packed), TreeEntry.unpackZ(packed)).asLong();
    }

    private static long chunkOf(long pos) {
        BlockPos start = BlockPos.of(pos);
        return ChunkPos.asLong(SectionPos.blockToSectionCoord(start.getX()), SectionPos.blockToSectionCoord(start.getZ()));
    }

    public static final class Held implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<Held> self = LazyOptional.of(() -> this);
        private ListTag trees = new ListTag();

        public ListTag trees() { return trees; }

        private void keep(ListTag list) { trees = list; }

        @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) { return TREES.orEmpty(capability, self); }

        @Override public CompoundTag serializeNBT() {
            CompoundTag data = new CompoundTag();
            if (trees.isEmpty()) { return data; }
            data.put(TAG, trees);
            return data;
        }

        @Override public void deserializeNBT(CompoundTag data) { trees = data.getList(TAG, Tag.TAG_COMPOUND); }
    }
}
