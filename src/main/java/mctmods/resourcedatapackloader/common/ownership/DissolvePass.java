package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public final class DissolvePass {
    private static final Deque<Target> QUEUE = new ArrayDeque<>();

    private DissolvePass() {}

    private static final class Target {
        private final int dimension;
        private final int x;
        private final int y;
        private final int z;
        private final boolean cube;

        private Target(int dimension, int x, int y, int z, boolean cube) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.cube = cube;
        }

        @Override public boolean equals(Object other) {
            if (!(other instanceof Target)) { return false; }
            Target target = (Target) other;
            return dimension == target.dimension && x == target.x && y == target.y && z == target.z && cube == target.cube;
        }

        @Override public int hashCode() { return Objects.hash(dimension, x, y, z, cube); }
    }

    public static void onLoad(World world, OwnedBlocks store, @Nullable Chunk chunk, @Nullable ICube cube) {
        OwnerTable table = OwnerTable.get(world.getMinecraftServer());
        if (table == null) { return; }
        OwnedBlockLookup.Store holder = new OwnedBlockLookup.Store(world, store, chunk, cube);
        rewriteMoved(table, holder);
        if (OwnershipSettings.keepsEverything() || store.epoch() >= table.epoch()) { return; }
        if (hasDissolved(table, store)) {
            Target target = target(world, chunk, cube);
            if (!QUEUE.contains(target)) { QUEUE.add(target); }
        } else {
            store.setEpoch(table.epoch());
            if (!store.entries().isEmpty()) { holder.markDirty(); }
        }
    }

    public static void run(MinecraftServer server) {
        OwnerTable table = OwnerTable.get(server);
        if (table == null) {
            clear();
            return;
        }
        int budget = OwnershipSettings.perTick();
        while (budget > 0 && !QUEUE.isEmpty()) {
            Target target = QUEUE.peek();
            WorldServer world = DimensionManager.getWorld(target.dimension);
            if (world == null) {
                QUEUE.poll();
                continue;
            }
            OwnedBlockLookup.Store holder = OwnedBlockLookup.at(world, target.x, target.y, target.z);
            if (holder == null) {
                QUEUE.poll();
                continue;
            }
            List<Integer> keys = dissolvedKeys(table, holder.blocks);
            int handled = 0;
            while (handled < keys.size() && budget > 0) {
                int key = keys.get(handled);
                OwnershipSettings.Dissolve mode = OwnershipSettings.dissolveMode(table.sourceOf(holder.blocks.entries().get(key).handle()));
                if (mode != OwnershipSettings.Dissolve.KEEP) { dissolve(world, holder, target, key, mode == OwnershipSettings.Dissolve.REMOVE); }
                handled++;
                budget--;
            }
            if (handled < keys.size()) { return; }
            holder.blocks.setEpoch(table.epoch());
            holder.markDirty();
            QUEUE.poll();
        }
    }

    public static boolean idle() { return QUEUE.isEmpty(); }

    public static void clear() { QUEUE.clear(); }

    private static void rewriteMoved(OwnerTable table, OwnedBlockLookup.Store holder) {
        boolean changed = false;
        for (Integer key : new ArrayList<>(holder.blocks.entries().keySet())) {
            OwnedBlocks.Entry entry = holder.blocks.entries().get(key);
            int old = entry.handle();
            int now = table.resolve(old);
            if (now == 0 || now == old) { continue; }
            holder.set(key, holder.blocks.blockName(entry), now);
            changed = true;
        }
        if (changed) { holder.markDirty(); }
    }

    private static boolean hasDissolved(OwnerTable table, OwnedBlocks store) {
        for (OwnedBlocks.Entry entry : store.entries().values()) {
            if (table.resolve(entry.handle()) == 0) { return true; }
        }
        return false;
    }

    private static List<Integer> dissolvedKeys(OwnerTable table, OwnedBlocks store) {
        List<Integer> keys = new ArrayList<>();
        for (Integer key : new ArrayList<>(store.entries().keySet())) {
            if (table.resolve(store.entries().get(key).handle()) == 0) { keys.add(key); }
        }
        return keys;
    }

    public static int removeSection(World world, int chunkX, int chunkZ, int sectionY, int budget) {
        OwnerTable table = OwnerTable.get(world.getMinecraftServer());
        Target target = sectionTarget(world, chunkX, chunkZ, sectionY);
        OwnedBlockLookup.Store holder = OwnedBlockLookup.at(world, target.x, target.y, target.z);
        if (table == null || holder == null) { return 0; }
        List<Integer> keys = inSection(dissolvedKeys(table, holder.blocks), target, sectionY);
        int handled = Math.min(keys.size(), budget);
        for (int index = 0; index < handled; index++) { dissolve(world, holder, target, keys.get(index), true); }
        return handled;
    }

    public static void forget(World world, int chunkX, int chunkZ, int sectionY) {
        Target target = sectionTarget(world, chunkX, chunkZ, sectionY);
        OwnedBlockLookup.Store holder = OwnedBlockLookup.at(world, target.x, target.y, target.z);
        if (holder == null) { return; }
        for (int key : inSection(new ArrayList<>(holder.blocks.entries().keySet()), target, sectionY)) { holder.drop(key); }
    }

    private static Target sectionTarget(World world, int chunkX, int chunkZ, int sectionY) {
        boolean cube = GenHeights.rubic(world);
        return new Target(world.provider.getDimension(), chunkX, cube ? sectionY : 0, chunkZ, cube);
    }

    private static List<Integer> inSection(List<Integer> keys, Target target, int sectionY) {
        if (target.cube) { return keys; }
        List<Integer> kept = new ArrayList<>();
        for (int key : keys) {
            if (OwnedBlockKey.y(key) >> 4 == sectionY) { kept.add(key); }
        }
        return kept;
    }

    private static void dissolve(World world, OwnedBlockLookup.Store holder, Target target, int key, boolean remove) {
        OwnedBlocks.Entry entry = holder.blocks.entries().get(key);
        if (entry == null) { return; }
        String name = holder.blocks.blockName(entry);
        holder.drop(key);
        if (!remove) { return; }
        BlockPos pos = position(target, key);
        if (!name.equals(OwnedBlockLookup.nameOf(world.getBlockState(pos).getBlock()))) { return; }
        if (OwnershipSettings.removeVanish()) { world.setBlockToAir(pos); }
        else { world.destroyBlock(pos, true); }
    }

    private static BlockPos position(Target target, int key) {
        int y = target.cube ? Coords.localToBlock(target.y, OwnedBlockKey.y(key)) : OwnedBlockKey.y(key);
        return new BlockPos(Coords.localToBlock(target.x, OwnedBlockKey.x(key)), y, Coords.localToBlock(target.z, OwnedBlockKey.z(key)));
    }

    private static Target target(World world, @Nullable Chunk chunk, @Nullable ICube cube) {
        int dimension = world.provider.getDimension();
        if (cube != null) { return new Target(dimension, cube.getCoords().getX(), cube.getCoords().getY(), cube.getCoords().getZ(), true); }
        return new Target(dimension, Objects.requireNonNull(chunk).x, 0, chunk.z, false);
    }
}
