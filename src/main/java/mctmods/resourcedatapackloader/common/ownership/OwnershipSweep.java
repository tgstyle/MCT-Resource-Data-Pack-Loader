package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;

public final class OwnershipSweep {
    private static final int PROBES = 4096;
    private static final int SAVE_EVERY = 256;
    private static final int CHUNK_SLOTS = 1024;
    private static final String CHUNK_PREFIX = "r.";
    private static final String CHUNK_SUFFIX = ".mca";
    private static State state;

    private OwnershipSweep() {}

    public record Progress(String dimension, int done, int total, int position, int dimensions, boolean all) {}

    private static final class State {
        private final OwnerTable table;
        private final List<String> dimensions;
        private final boolean all;
        private final Map<Integer, Integer> counts = new HashMap<>();
        private int index;
        private String dimension = "";
        private int[] regions = new int[0];
        private int total;
        private int region;
        private int slot;
        private int done;

        private State(OwnerTable table, List<String> dimensions, boolean all) {
            this.table = table;
            this.dimensions = dimensions;
            this.all = all;
        }

        private boolean enter(MinecraftServer server) {
            for (; index < dimensions.size(); index++) {
                dimension = dimensions.get(index);
                ServerLevel level = OwnedBlockLookup.level(server, dimension);
                if (level == null) { continue; }
                regions = regionsOf(server, level);
                region = 0;
                slot = 0;
                done = 0;
                total = 0;
                for (; !finished(); advance()) {
                    if (saved(this, level)) { total++; }
                }
                region = 0;
                slot = 0;
                if (total > 0) { return true; }
            }
            return false;
        }

        private boolean finished() { return region * 2 >= regions.length; }

        private void advance() {
            if (++slot < CHUNK_SLOTS) { return; }
            slot = 0;
            region++;
        }

        private int x() { return regions[region * 2] * 32 + (slot & 31); }

        private int z() { return regions[region * 2 + 1] * 32 + (slot >> 5); }

        private void next() {
            done++;
            advance();
            if (done % SAVE_EVERY == 0) { persist(); }
        }

        private void persist() { table.setSweepCursor(write()); }

        private CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            ListTag names = new ListTag();
            for (String name : dimensions) { names.add(StringTag.valueOf(name)); }
            tag.put("dims", names);
            tag.putBoolean("all", all);
            tag.putInt("index", index);
            tag.putString("dim", dimension);
            tag.putIntArray("regions", regions);
            tag.putInt("total", total);
            tag.putInt("region", region);
            tag.putInt("slot", slot);
            tag.putInt("done", done);
            int[] pairs = new int[counts.size() * 2];
            int at = 0;
            for (Map.Entry<Integer, Integer> each : counts.entrySet()) {
                pairs[at++] = each.getKey();
                pairs[at++] = each.getValue();
            }
            tag.putIntArray("counts", pairs);
            return tag;
        }
    }

    @Nullable private static State read(OwnerTable table) {
        CompoundTag tag = table.sweepCursor();
        if (!tag.contains("dim")) { return null; }
        List<String> names = new ArrayList<>();
        ListTag listed = tag.getListOrEmpty("dims");
        for (int at = 0; at < listed.size(); at++) { names.add(listed.getStringOr(at, "")); }
        State read = new State(table, names, tag.getBooleanOr("all", false));
        read.index = tag.getIntOr("index", 0);
        read.dimension = tag.getStringOr("dim", "");
        read.regions = tag.getIntArray("regions").orElseGet(() -> new int[0]);
        read.total = tag.getIntOr("total", 0);
        read.region = tag.getIntOr("region", 0);
        read.slot = tag.getIntOr("slot", 0);
        read.done = tag.getIntOr("done", 0);
        int[] pairs = tag.getIntArray("counts").orElseGet(() -> new int[0]);
        for (int at = 0; at + 1 < pairs.length; at += 2) { read.counts.put(pairs[at], pairs[at + 1]); }
        return read;
    }

    @Nullable private static State current(MinecraftServer server) {
        OwnerTable table = OwnerTable.get(server);
        if (state == null || state.table != table) { state = read(table); }
        return state;
    }

    @Nullable public static Progress progress(MinecraftServer server) {
        State held = current(server);
        return held == null ? null : new Progress(held.dimension, held.done, held.total, held.index + 1, held.dimensions.size(), held.all);
    }

    @Nullable public static Progress start(MinecraftServer server, boolean all, String dimension) {
        State begun = new State(OwnerTable.get(server), all ? allDimensions(server) : List.of(dimension), all);
        if (!begun.enter(server)) { return null; }
        state = begun;
        begun.persist();
        return progress(server);
    }

    private static List<String> allDimensions(MinecraftServer server) {
        String overworld = Level.OVERWORLD.identifier().toString();
        List<String> listed = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) { listed.add(level.dimension().identifier().toString()); }
        listed.sort(null);
        listed.remove(overworld);
        listed.addFirst(overworld);
        return listed;
    }

    public static void cancel(MinecraftServer server) {
        state = null;
        OwnerTable.get(server).setSweepCursor(new CompoundTag());
    }

    public static void tick(MinecraftServer server) {
        State held = current(server);
        if (held == null || !DissolvePass.idle()) { return; }
        ServerLevel level = OwnedBlockLookup.level(server, held.dimension);
        if (level != null) {
            for (int probes = 0; probes < PROBES && !held.finished() && !saved(held, level); probes++) { held.advance(); }
        }
        if (level == null || held.finished()) {
            held.index++;
            if (held.enter(server)) { held.persist(); }
            else { finish(held); }
        }
        else if (saved(held, level)) { process(held, level); }
    }

    private static void process(State held, ServerLevel level) {
        if (absent(held, level)) {
            level.getChunk(held.x(), held.z(), ChunkStatus.FULL, true);
            if (absent(held, level)) {
                held.next();
                return;
            }
        }
        if (!DissolvePass.idle()) { return; }
        OwnedBlockLookup.Store store = OwnedBlockLookup.at(level, held.x(), held.z(), false);
        if (store != null) {
            DissolvePass.onLoad(level, store.blocks, store.chunk);
            if (!DissolvePass.idle()) { return; }
            if (held.all) {
                for (OwnedBlocks.Entry entry : store.blocks.entries().values()) { held.counts.merge(entry.handle(), 1, Integer::sum); }
            }
        }
        held.next();
    }

    private static void finish(State held) {
        if (held.all) {
            held.table.recount(held.counts);
            held.table.purge();
            ContentLog.LOGGER.info("Ownership sweep of {} dimension(s) finished, held counts recounted and dissolved owners purged", held.dimensions.size());
        } else {
            ContentLog.LOGGER.info("Ownership sweep of dimension {} finished, held counts not recounted and nothing purged because only one dimension was swept", held.dimension);
        }
        held.table.setSweepCursor(new CompoundTag());
        state = null;
    }

    private static boolean saved(State held, ServerLevel level) {
        Optional<CompoundTag> data = level.getChunkSource().chunkMap.read(new ChunkPos(held.x(), held.z())).join();
        return data.isPresent() && SerializableChunkData.getChunkStatusFromTag(data.get()) == ChunkStatus.FULL;
    }

    private static boolean absent(State held, ServerLevel level) { return level.getChunkSource().getChunkNow(held.x(), held.z()) == null; }

    private static int[] regionsOf(MinecraftServer server, ServerLevel level) {
        File folder = DimensionType.getStorageFolder(level.dimension(), server.getWorldPath(LevelResource.ROOT)).resolve("region").toFile();
        String[] names = folder.list();
        if (names == null) { return new int[0]; }
        Arrays.sort(names);
        List<Integer> found = new ArrayList<>();
        for (String name : names) {
            int[] parsed = parse(name);
            if (parsed == null) { continue; }
            for (int value : parsed) { found.add(value); }
        }
        return found.stream().mapToInt(Integer::intValue).toArray();
    }

    @Nullable private static int[] parse(String name) {
        if (!name.startsWith(CHUNK_PREFIX) || !name.endsWith(CHUNK_SUFFIX) || name.length() < CHUNK_PREFIX.length() + CHUNK_SUFFIX.length()) { return null; }
        String[] parts = name.substring(CHUNK_PREFIX.length(), name.length() - CHUNK_SUFFIX.length()).split("\\.");
        if (parts.length != 2) { return null; }
        int[] out = new int[2];
        try {
            for (int index = 0; index < 2; index++) { out[index] = Integer.parseInt(parts[index]); }
        } catch (NumberFormatException bad) {
            return null;
        }
        return out;
    }
}
