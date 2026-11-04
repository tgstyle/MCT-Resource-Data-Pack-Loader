package mctmods.resourcedatapackloader.common.ownership;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;

public final class OwnedBlockLookup {
    private OwnedBlockLookup() {}

    public static void tag(ServerLevel level, BlockPos pos, String blockName, int handle) {
        Store store = store(level, pos, true);
        if (store == null) { return; }
        store.set(key(pos), blockName, handle);
    }

    public static int clear(ServerLevel level, BlockPos pos) {
        Store store = store(level, pos, false);
        return store == null ? 0 : store.drop(key(pos));
    }

    private static int key(BlockPos pos) { return OwnedBlockKey.pack(pos.getX(), pos.getY(), pos.getZ()); }

    static String nameOf(Block block) { return BuiltInRegistries.BLOCK.getKey(block).toString(); }

    @Nullable static ServerLevel level(MinecraftServer server, String dimension) {
        Identifier id = Identifier.tryParse(dimension);
        return id == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
    }

    @Nullable private static Store store(ServerLevel level, BlockPos pos, boolean create) { return at(level, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), create); }

    @Nullable static Store at(ServerLevel level, int cx, int cz, boolean create) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(cx, cz);
        if (chunk == null) { return null; }
        OwnedBlocks blocks = create ? OwnedBlocksStore.of(chunk) : OwnedBlocksStore.existing(chunk);
        return blocks == null ? null : new Store(level, blocks, chunk);
    }

    static final class Store {
        final OwnedBlocks blocks;
        final LevelChunk chunk;
        private final ServerLevel level;

        Store(ServerLevel level, OwnedBlocks blocks, LevelChunk chunk) {
            this.level = level;
            this.blocks = blocks;
            this.chunk = chunk;
        }

        void markDirty() { chunk.markUnsaved(); }

        private void held(int handle, int delta) {
            OwnerTable.get(level.getServer()).held(handle, delta);
        }

        int drop(int key) {
            OwnedBlocks.Entry old = blocks.entries().get(key);
            if (old == null) { return 0; }
            int oldHandle = old.handle();
            blocks.remove(key);
            held(oldHandle, -1);
            markDirty();
            return oldHandle;
        }

        void set(int key, String blockName, int handle) {
            OwnedBlocks.Entry old = blocks.entries().get(key);
            int oldHandle = old == null ? 0 : old.handle();
            blocks.put(key, blockName, handle);
            if (oldHandle != 0) { held(oldHandle, -1); }
            held(handle, 1);
            markDirty();
        }
    }
}
