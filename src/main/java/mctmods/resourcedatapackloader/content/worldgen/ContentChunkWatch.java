package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.util.math.MathHelper;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ContentChunkWatch {
    private static final int SNAPSHOT = 100;
    private static final long SAMPLE = 63L;
    private static long made;
    private static long read;
    private static long readFromDisk;
    private static long opened;
    private static long emptied;
    private static long released;
    private static long written;
    private static long writeNanos;
    private static long fileNanos;
    private static long readied;
    private static long readyNanos;
    private static long lastLook;
    private static long lit;
    private static long litNanos;
    private static long unlit;
    private static long retried;
    private static long deferred;
    private static long heldOff;
    private static long relit;
    private static long relitNanos;
    private static long spread;
    private static long spreadNanos;
    private static long spreadSky;
    private static long skyNanos;
    private static long sampled;
    private static long sampledSky;
    private static long settled;
    private static long warm;
    private static long chunkLookups;
    private static long materialLookups;
    private static long blockIdLookups;
    private static long stoneSpared;
    private static long primerLookups;
    private static long collections;
    private static long collectMillis;
    private static long allocated;
    private static long lastHeap;
    private static final Map<String, long[]> SWEEPERS = new HashMap<>();
    private static long terrainNanos;
    private static long decorNanos;
    private static long modNanos;
    private static final Map<String, Long> BY_MOD = new HashMap<>();
    private static int mostWaiting;
    private static int mostHeld;
    private static boolean seen;

    private ContentChunkWatch() {}

    public static boolean watching() { return ContentLog.LOGGER.debugEnabled(); }

    public static void made() { made++; seen = true; }

    public static void read() { read++; seen = true; }

    public static void readFromDisk() { readFromDisk++; }

    public static void opened() { opened++; }

    public static void emptied() { emptied++; }

    public static void released() { released++; }

    public static void written(long nanos) { written++; writeNanos += nanos; seen = true; }

    public static void toFile(long nanos) { fileNanos += nanos; }

    public static void readied(long nanos) { readied++; readyNanos += nanos; seen = true; }

    public static void lit(long nanos, boolean finished) {
        lit++;
        litNanos += nanos;
        if (!finished) { unlit++; }
        seen = true;
    }

    public static void retried() { retried++; }

    public static void lightDeferred() { deferred++; }

    public static void dressingHeldOff() { heldOff++; }

    public static void skySettled() { settled++; }

    public static void warmChunk() { warm++; }

    public static void chunkLookup() { chunkLookups++; }

    public static void materialLookup() { materialLookups++; }

    public static void blockIdLookup() { blockIdLookups++; }

    public static void stoneSpared() { stoneSpared++; }

    public static void primerLookup() { primerLookups++; }

    public static void relit(long nanos) { relit++; relitNanos += nanos; }

    public static boolean timingThisOne() { return watching() && (spread & SAMPLE) == 0L; }

    public static void spread(long began, boolean sky) {
        if (!watching()) { return; }
        spread++;
        if (sky) { spreadSky++; }
        if (began == 0L) { return; }
        long took = System.nanoTime() - began;
        sampled++;
        spreadNanos += took;
        if (!sky) { return; }
        sampledSky++;
        skyNanos += took;
    }

    public static void terrain(long nanos) { terrainNanos += nanos; }

    public static void decorated(long nanos) { decorNanos += nanos; }

    public static void byMod(String mod, long nanos) {
        modNanos += nanos;
        BY_MOD.compute(mod, (k, had) -> (had == null ? 0L : had) + nanos);
    }

    private static String worst() {
        if (BY_MOD.isEmpty()) { return ""; }
        StringBuilder out = new StringBuilder(", of which");
        List<Map.Entry<String, Long>> order = new ArrayList<>(BY_MOD.entrySet());
        order.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        int shown = 0;
        for (Map.Entry<String, Long> entry : order) {
            if (shown++ >= 5) { break; }
            out.append(String.format(" %s %.1f ms", entry.getKey(), entry.getValue() / 1.0E6D));
        }
        return out.toString();
    }

    private static void takeStock() {
        long runs = 0L;
        long spent = 0L;
        Runtime memory = Runtime.getRuntime();
        long held = memory.totalMemory() - memory.freeMemory();
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = bean.getCollectionCount();
            long time = bean.getCollectionTime();
            if (count > 0L) { runs += count; }
            if (time > 0L) { spent += time; }
            note(bean.getName(), count, time, held);
        }
        if (collections == 0L && collectMillis == 0L) {
            collections = runs;
            collectMillis = spent;
        }
        if (lastHeap > 0L && held > lastHeap) { allocated += held - lastHeap; }
        lastHeap = held;
    }

    private static void note(String sweeper, long count, long time, long held) {
        long[] was = SWEEPERS.get(sweeper);
        if (was == null) {
            SWEEPERS.put(sweeper, new long[]{count, time, held, Runtime.getRuntime().totalMemory()});
            return;
        }
        if (count <= was[0]) {
            was[2] = held;
            was[3] = Runtime.getRuntime().totalMemory();
            return;
        }
        long room = Runtime.getRuntime().totalMemory();
        ContentLog.LOGGER.debug(String.format(
                "Tidying: %s ran %d time(s) taking %d ms, holding %d MB before and %d MB after, with room for %d MB%s. %d chunk(s) made since the last look",
                sweeper, count - was[0], time - was[1], was[2] >> 20, held >> 20, room >> 20,
                room == was[3] ? "" : " (room changed from " + (was[3] >> 20) + " MB)", made));
        was[0] = count;
        was[1] = time;
        was[2] = held;
        was[3] = room;
    }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        takeStock();
        int waiting = 0;
        int held = 0;
        for (WorldServer world : server.worlds) {
            ChunkProviderServer provider = world.getChunkProvider();
            held += provider.getLoadedChunkCount();
            if (provider.chunkLoader instanceof AnvilChunkLoader) { waiting += ((AnvilChunkLoader) provider.chunkLoader).getPendingSaveCount(); }
        }
        if (waiting > mostWaiting) { mostWaiting = waiting; }
        if (held > mostHeld) { mostHeld = held; }
        if (server.getTickCounter() % SNAPSHOT != 0 || !seen) { return; }
        double mean = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        long touched = made + read;
        double useful = touched == 0L ? 100.0D : 100.0D * made / touched;
        long now = System.nanoTime();
        double seconds = lastLook == 0L ? 0.0D : (now - lastLook) / 1.0E9D;
        lastLook = now;
        double rate = seconds <= 0.0D ? 0.0D : written / seconds;
        ContentLog.LOGGER.debug(String.format(
                "Since the last look the world made %d chunk(s) and had to fetch %d again, %d of those from the disk, so %.1f%% of the work was new ground. It opened %d region file(s) and let go of them %d time(s), let %d chunk(s) go, and at its busiest held %d chunk(s) with %d still waiting to be written. A round took %.1f ms",
                made, read, readFromDisk, useful, opened, emptied, released, mostHeld, mostWaiting, mean));
        ContentLog.LOGGER.debug(String.format(
                "In the same time it got %d chunk(s) ready to write in %.1f ms of the round's own time, then wrote %d of them away, %.1f a second, spending %.1f ms apart from the round, of which %.1f ms went into the files themselves and %.1f ms into packing them down",
                readied, readyNanos / 1.0E6D, written, rate, writeNanos / 1.0E6D, fileNanos / 1.0E6D, (writeNanos - fileNanos) / 1.0E6D));
        ContentLog.LOGGER.debug(String.format(
                "It also lit %d chunk(s) in %.1f ms, of which %d were left dark for want of their neighbors %d were the same chunks being tried again and %d were held back until the dressing was on and %d chunk(s) were left undressed while only the light was being seen to, went back over %d chunk(s) in %.1f ms to finish the job, and spread light from %d place(s), %d about the sky and %d about lamps and fire, taking about %.1f ms in all of which about %.1f ms was the sky, timed one in every %d. %d more were not asked at all, the sky about them being settled already",
                lit, litNanos / 1.0E6D, unlit, retried, deferred, heldOff, relit, relitNanos / 1.0E6D, spread, spreadSky, spread - spreadSky,
                sampled == 0L ? 0.0D : spreadNanos / 1.0E6D * spread / sampled,
                sampledSky == 0L ? 0.0D : skyNanos / 1.0E6D * spreadSky / sampledSky,
                SAMPLE + 1L, settled));
        ContentLog.LOGGER.debug(String.format(
                "It had to look a chunk up in earnest %d time(s), ask what a block was made of %d time(s), name a block %d time(s) while making the ground and %d time(s) for the writing, and spared Quark's stone generator %d reading(s) of the world for ground it was never going to reach",
                chunkLookups, materialLookups, primerLookups, blockIdLookups, stoneSpared));
        long runs = 0L;
        long spent = 0L;
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (bean.getCollectionCount() > 0L) { runs += bean.getCollectionCount(); }
            if (bean.getCollectionTime() > 0L) { spent += bean.getCollectionTime(); }
        }
        Runtime runtime = Runtime.getRuntime();
        ContentLog.LOGGER.debug(String.format(
                "It threw away about %d MB of working scraps, which the tidying took %d ms across %d sweep(s), and it is holding %d MB of the %d MB it may have",
                allocated >> 20, spent - collectMillis, runs - collections, (runtime.totalMemory() - runtime.freeMemory()) >> 20, runtime.maxMemory() >> 20));
        collections = runs;
        collectMillis = spent;
        allocated = 0L;
        ContentLog.LOGGER.debug(String.format(
                "Making the ground took %.1f ms, dressing it as the game does took %.1f ms, and the mods that dress it took %.1f ms%s. %d chunk(s) were too warm anywhere in them for ice or snow, so were not walked for it",
                terrainNanos / 1.0E6D, decorNanos / 1.0E6D, modNanos / 1.0E6D, worst(), warm));
        made = 0L;
        read = 0L;
        readFromDisk = 0L;
        opened = 0L;
        emptied = 0L;
        released = 0L;
        written = 0L;
        writeNanos = 0L;
        fileNanos = 0L;
        readied = 0L;
        readyNanos = 0L;
        lit = 0L;
        litNanos = 0L;
        unlit = 0L;
        retried = 0L;
        deferred = 0L;
        heldOff = 0L;
        relit = 0L;
        relitNanos = 0L;
        spread = 0L;
        spreadNanos = 0L;
        spreadSky = 0L;
        skyNanos = 0L;
        sampled = 0L;
        sampledSky = 0L;
        settled = 0L;
        warm = 0L;
        chunkLookups = 0L;
        materialLookups = 0L;
        blockIdLookups = 0L;
        stoneSpared = 0L;
        primerLookups = 0L;
        terrainNanos = 0L;
        decorNanos = 0L;
        modNanos = 0L;
        BY_MOD.clear();
        mostWaiting = 0;
        mostHeld = 0;
        seen = false;
    }
}
