package mctmods.resourcedatapackloader.common.ownership;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;

public final class DissolvePass {
    private static final Deque<Target> QUEUE = new ArrayDeque<>();

    private DissolvePass() {}

    private record Target(ResourceKey<Level> dimension, int x, int z) {}

    public static void onLoad(ServerLevel level, OwnedBlocks store, LevelChunk chunk) {
        OwnerTable table = OwnerTable.get(level.getServer());
        OwnedBlockLookup.Store holder = new OwnedBlockLookup.Store(level, store, chunk);
        rewriteMoved(table, holder);
        if (OwnershipSettings.keepsEverything() || store.epoch() >= table.epoch()) { return; }
        if (hasDissolved(table, store)) {
            Target target = new Target(level.dimension(), chunk.getPos().x, chunk.getPos().z);
            if (!QUEUE.contains(target)) { QUEUE.add(target); }
        }
        else {
            store.setEpoch(table.epoch());
            if (!store.entries().isEmpty()) { holder.markDirty(); }
        }
    }

    public static void run(MinecraftServer server) {
        if (QUEUE.isEmpty()) { return; }
        OwnerTable table = OwnerTable.get(server);
        int budget = OwnershipSettings.perTick();
        while (budget > 0 && !QUEUE.isEmpty()) {
            Target target = QUEUE.peek();
            ServerLevel level = server.getLevel(target.dimension());
            OwnedBlockLookup.Store holder = level == null ? null : OwnedBlockLookup.at(level, target.x(), target.z());
            if (holder == null) {
                QUEUE.poll();
                continue;
            }
            List<Integer> keys = dissolvedKeys(table, holder.blocks);
            int handled = 0;
            while (handled < keys.size() && budget > 0) {
                int key = keys.get(handled);
                OwnershipSettings.Dissolve mode = OwnershipSettings.dissolveMode(table.sourceOf(holder.blocks.entries().get(key).handle()));
                if (mode != OwnershipSettings.Dissolve.KEEP) { dissolve(level, holder, target, key, mode == OwnershipSettings.Dissolve.REMOVE); }
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

    public static int removeSection(ServerLevel level, int chunkX, int chunkZ, int sectionY, int budget) {
        OwnerTable table = OwnerTable.get(level.getServer());
        OwnedBlockLookup.Store holder = OwnedBlockLookup.at(level, chunkX, chunkZ);
        if (holder == null) { return 0; }
        Target target = new Target(level.dimension(), chunkX, chunkZ);
        List<Integer> keys = inSection(dissolvedKeys(table, holder.blocks), sectionY);
        int handled = Math.min(keys.size(), budget);
        for (int index = 0; index < handled; index++) { dissolve(level, holder, target, keys.get(index), true); }
        return handled;
    }

    public static void forget(ServerLevel level, int chunkX, int chunkZ, int sectionY) {
        OwnedBlockLookup.Store holder = OwnedBlockLookup.at(level, chunkX, chunkZ);
        if (holder == null) { return; }
        for (int key : inSection(new ArrayList<>(holder.blocks.entries().keySet()), sectionY)) { holder.drop(key); }
    }

    private static List<Integer> inSection(List<Integer> keys, int sectionY) {
        List<Integer> kept = new ArrayList<>();
        for (int key : keys) {
            if (OwnedBlockKey.y(key) >> 4 == sectionY) { kept.add(key); }
        }
        return kept;
    }

    private static void dissolve(ServerLevel level, OwnedBlockLookup.Store holder, Target target, int key, boolean remove) {
        OwnedBlocks.Entry entry = holder.blocks.entries().get(key);
        if (entry == null) { return; }
        String name = holder.blocks.blockName(entry);
        holder.drop(key);
        if (!remove) { return; }
        BlockPos pos = new BlockPos(SectionPos.sectionToBlockCoord(target.x(), OwnedBlockKey.x(key)), OwnedBlockKey.y(key), SectionPos.sectionToBlockCoord(target.z(), OwnedBlockKey.z(key)));
        if (!name.equals(OwnedBlockLookup.nameOf(level.getBlockState(pos).getBlock()))) { return; }
        if (OwnershipSettings.removeVanish()) { level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3); }
        else { level.destroyBlock(pos, true); }
    }
}
