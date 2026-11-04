package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProvider;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public final class OwnedBlockLookup {
    private OwnedBlockLookup() {}

    public static void tag(World world, BlockPos pos, String blockName, int handle) {
        Store store = store(world, pos);
        if (store == null) { return; }
        store.set(key(store, pos), blockName, handle);
    }

    public static int clear(World world, BlockPos pos) {
        Store store = store(world, pos);
        return store == null ? 0 : store.drop(key(store, pos));
    }

    private static int key(Store store, BlockPos pos) {
        return OwnedBlockKey.pack(Coords.blockToLocal(pos.getX()), store.cube == null ? pos.getY() : Coords.blockToLocal(pos.getY()), Coords.blockToLocal(pos.getZ()));
    }

    static String nameOf(Block block) { return String.valueOf(block.getRegistryName()); }

    @Nullable private static Store store(World world, BlockPos pos) {
        return at(world, Coords.blockToCube(pos.getX()), Coords.blockToCube(pos.getY()), Coords.blockToCube(pos.getZ()));
    }

    @Nullable static Store at(World world, int cx, int cy, int cz) {
        if (world.isRemote) { return null; }
        if (GenHeights.rubic(world)) {
            ICube cube = ((ICubeProvider) world.getChunkProvider()).getLoadedCube(cx, cy, cz);
            OwnedBlocks blocks = OwnedBlocksProvider.of(cube);
            return blocks == null ? null : new Store(world, blocks, null, cube);
        }
        Chunk chunk = world.getChunkProvider().getLoadedChunk(cx, cz);
        OwnedBlocks blocks = OwnedBlocksProvider.of(chunk);
        return blocks == null ? null : new Store(world, blocks, chunk, null);
    }

    static final class Store {
        final OwnedBlocks blocks;
        final Chunk chunk;
        final ICube cube;
        private final World world;

        Store(World world, OwnedBlocks blocks, @Nullable Chunk chunk, @Nullable ICube cube) {
            this.world = world;
            this.blocks = blocks;
            this.chunk = chunk;
            this.cube = cube;
        }

        void markDirty() {
            if (chunk != null) { chunk.markDirty(); }
            else if (cube instanceof Cube) { ((Cube) cube).markDirty(); }
        }

        private void held(int handle, int delta) {
            OwnerTable table = OwnerTable.get(world.getMinecraftServer());
            if (table != null) { table.held(handle, delta); }
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
