package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.pregen.ContentPregenDimensions;
import mctmods.resourcedatapackloader.common.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.common.rubic.regionlib.impl.RegionNames;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProvider;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

public final class OwnershipSweep {
    private static final int PROBES = 4096;
    private static final int SAVE_EVERY = 256;
    private static final int CHUNK_SLOTS = 1024;
    private static final int CUBE_SLOTS = 4096;
    private static final String CHUNK_PREFIX = "r.";
    private static final String CHUNK_SUFFIX = ".mca";
    private static final String CUBE_FOLDER = "region3d";
    private static State state;

    private OwnershipSweep() {}

    private static final class State {
        private final OwnerTable table;
        private final int[] dimensions;
        private final boolean all;
        private final Map<Integer, Integer> counts = new HashMap<>();
        private int index;
        private int dimension;
        private boolean cube;
        private int[] regions = new int[0];
        private int total;
        private int region;
        private int slot;
        private int done;
        private boolean own;

        private State(OwnerTable table, int[] dimensions, boolean all) {
            this.table = table;
            this.dimensions = dimensions;
            this.all = all;
        }

        private boolean enter(MinecraftServer server) {
            for (; index < dimensions.length; index++) {
                dimension = dimensions[index];
                WorldServer world = server.getWorld(dimension);
                cube = GenHeights.rubic(world);
                regions = regionsOf(world, cube);
                region = 0;
                slot = 0;
                done = 0;
                total = 0;
                for (; !finished(); advance()) {
                    if (saved(this, world)) { total++; }
                }
                region = 0;
                slot = 0;
                if (total > 0) { return true; }
            }
            return false;
        }

        private int stride() { return cube ? 3 : 2; }

        private int slots() { return cube ? CUBE_SLOTS : CHUNK_SLOTS; }

        private boolean finished() { return region * stride() >= regions.length; }

        private void advance() {
            if (++slot < slots()) { return; }
            slot = 0;
            region++;
        }

        private int x() { return cube ? regions[region * 3] * 16 + (slot >> 8) : regions[region * 2] * 32 + (slot & 31); }

        private int y() { return cube ? regions[region * 3 + 1] * 16 + ((slot >> 4) & 15) : 0; }

        private int z() { return cube ? regions[region * 3 + 2] * 16 + (slot & 15) : regions[region * 2 + 1] * 32 + (slot >> 5); }

        private void next() {
            own = false;
            done++;
            advance();
            if (done % SAVE_EVERY == 0) { persist(); }
        }

        private void persist() { table.setSweepCursor(write()); }

        private NBTTagCompound write() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setIntArray("dims", dimensions);
            tag.setBoolean("all", all);
            tag.setInteger("index", index);
            tag.setInteger("dim", dimension);
            tag.setBoolean("cube", cube);
            tag.setIntArray("regions", regions);
            tag.setInteger("total", total);
            tag.setInteger("region", region);
            tag.setInteger("slot", slot);
            tag.setInteger("done", done);
            int[] pairs = new int[counts.size() * 2];
            int at = 0;
            for (Map.Entry<Integer, Integer> each : counts.entrySet()) {
                pairs[at++] = each.getKey();
                pairs[at++] = each.getValue();
            }
            tag.setIntArray("counts", pairs);
            return tag;
        }
    }

    @Nullable private static State read(OwnerTable table) {
        NBTTagCompound tag = table.sweepCursor();
        if (!tag.hasKey("dim", Constants.NBT.TAG_INT)) { return null; }
        State read = new State(table, tag.getIntArray("dims"), tag.getBoolean("all"));
        read.index = tag.getInteger("index");
        read.dimension = tag.getInteger("dim");
        read.cube = tag.getBoolean("cube");
        read.regions = tag.getIntArray("regions");
        read.total = tag.getInteger("total");
        read.region = tag.getInteger("region");
        read.slot = tag.getInteger("slot");
        read.done = tag.getInteger("done");
        int[] pairs = tag.getIntArray("counts");
        for (int at = 0; at + 1 < pairs.length; at += 2) { read.counts.put(pairs[at], pairs[at + 1]); }
        return read;
    }

    @Nullable private static State current(@Nullable MinecraftServer server) {
        OwnerTable table = OwnerTable.get(server);
        if (table == null) {
            state = null;
            return null;
        }
        if (state == null || state.table != table) { state = read(table); }
        return state;
    }

    @Nullable public static int[] progress(MinecraftServer server) {
        State held = current(server);
        return held == null ? null : new int[] {held.dimension, held.done, held.total, held.index + 1, held.dimensions.length, held.all ? 1 : 0};
    }

    @Nullable public static int[] start(MinecraftServer server, boolean all, int dimension) {
        OwnerTable table = OwnerTable.get(server);
        if (table == null) { return null; }
        State begun = new State(table, all ? allDimensions() : new int[] {dimension}, all);
        if (!begun.enter(server)) { return null; }
        state = begun;
        begun.persist();
        return progress(server);
    }

    private static int[] allDimensions() {
        List<Integer> listed = new ArrayList<>(Arrays.asList(DimensionManager.getStaticDimensionIDs()));
        listed.sort(null);
        listed.remove(Integer.valueOf(0));
        listed.add(0, 0);
        return listed.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void cancel(MinecraftServer server) {
        OwnerTable table = OwnerTable.get(server);
        state = null;
        if (table != null) { table.setSweepCursor(new NBTTagCompound()); }
    }

    public static void tick(MinecraftServer server) {
        State held = current(server);
        if (held == null || !DissolvePass.idle()) { return; }
        WorldServer world = server.getWorld(held.dimension);
        for (int probes = 0; probes < PROBES && !held.finished() && !saved(held, world); probes++) { held.advance(); }
        if (held.finished()) {
            held.index++;
            if (held.enter(server)) { held.persist(); }
            else { finish(held); }
        }
        else if (saved(held, world)) { process(held, world); }
    }

    private static void process(State held, WorldServer world) {
        if (absent(held, world)) {
            load(held, world);
            if (absent(held, world)) {
                held.next();
                return;
            }
            held.own = true;
        }
        if (!DissolvePass.idle()) { return; }
        OwnedBlockLookup.Store store = OwnedBlockLookup.at(world, held.x(), held.y(), held.z());
        if (store != null) {
            DissolvePass.onLoad(world, store.blocks, store.chunk, store.cube);
            if (!DissolvePass.idle()) { return; }
            if (held.all) {
                for (OwnedBlocks.Entry entry : store.blocks.entries().values()) { held.counts.merge(entry.handle(), 1, Integer::sum); }
            }
        }
        release(held, world);
        held.next();
    }

    private static void finish(State held) {
        if (held.all) {
            held.table.recount(held.counts);
            held.table.purge();
            ContentLog.LOGGER.info("Ownership sweep of {} dimension(s) finished, held counts recounted and dissolved owners purged", held.dimensions.length);
        } else {
            ContentLog.LOGGER.info("Ownership sweep of dimension {} finished, held counts not recounted and nothing purged because only one dimension was swept", held.dimension);
        }
        held.table.setSweepCursor(new NBTTagCompound());
        state = null;
    }

    private static boolean saved(State held, WorldServer world) {
        if (held.cube) { return RubicWorldControl.cubeSaved(world.getChunkProvider(), held.x(), held.y(), held.z()); }
        File folder = ContentPregenDimensions.regionFolder(world);
        return folder != null && RegionFileCache.createOrLoadRegionFile(folder.getParentFile(), held.x(), held.z()).isChunkSaved(held.x() & 31, held.z() & 31);
    }

    private static boolean absent(State held, WorldServer world) {
        if (held.cube) { return ((ICubeProvider) world.getChunkProvider()).getLoadedCube(held.x(), held.y(), held.z()) == null; }
        return world.getChunkProvider().getLoadedChunk(held.x(), held.z()) == null;
    }

    private static void load(State held, WorldServer world) {
        if (held.cube) { ((ICubeProviderServer) world.getChunkProvider()).getCube(held.x(), held.y(), held.z(), ICubeProviderServer.Requirement.LOAD); }
        else { world.getChunkProvider().loadChunk(held.x(), held.z()); }
    }

    private static void release(State held, WorldServer world) {
        if (!held.own || held.cube) { return; }
        ChunkProviderServer provider = world.getChunkProvider();
        Chunk chunk = provider.getLoadedChunk(held.x(), held.z());
        if (chunk != null && !world.getPlayerChunkMap().contains(held.x(), held.z())) { provider.queueUnload(chunk); }
    }

    private static int[] regionsOf(WorldServer world, boolean cube) {
        File folder = cube ? RubicWorldControl.storagePath(world).resolve(CUBE_FOLDER).toFile() : ContentPregenDimensions.regionFolder(world);
        String[] names = folder == null ? null : folder.list();
        if (names == null) { return new int[0]; }
        Arrays.sort(names);
        List<Integer> found = new ArrayList<>();
        for (String name : names) {
            int[] parsed = cube ? parse(name, "", RegionNames.CUBES, 3) : parse(name, CHUNK_PREFIX, CHUNK_SUFFIX, 2);
            if (parsed == null) { continue; }
            for (int value : parsed) { found.add(value); }
        }
        return found.stream().mapToInt(Integer::intValue).toArray();
    }

    @Nullable private static int[] parse(String name, String prefix, String suffix, int count) {
        if (!name.startsWith(prefix) || !name.endsWith(suffix) || name.length() < prefix.length() + suffix.length()) { return null; }
        String[] parts = name.substring(prefix.length(), name.length() - suffix.length()).split("\\.");
        if (parts.length != count) { return null; }
        int[] out = new int[count];
        try {
            for (int index = 0; index < count; index++) { out[index] = Integer.parseInt(parts[index]); }
        } catch (NumberFormatException bad) {
            return null;
        }
        return out;
    }
}
