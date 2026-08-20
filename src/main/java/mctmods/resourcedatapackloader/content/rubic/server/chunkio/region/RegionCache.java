package mctmods.resourcedatapackloader.content.rubic.server.chunkio.region;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegion;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class RegionCache {
    private static final int LEAST_REGIONS = 64;
    private static final int MOST_REGIONS = 8192;
    private static final int DESCRIPTORS_ASSUMED = 1024;
    private static final int DESCRIPTOR_SHARE = 4;
    private static final long TUNE_EVERY_MILLIS = 5000L;
    private static final float HEAP_TIGHT = 0.85f;
    private static final float HEAP_EASY = 0.75f;
    private static final int STEP = 64;
    private static final Map<Object, IRegion<?>> OPEN = new LinkedHashMap<>(256, 0.75f, true);
    private static final int DESCRIPTOR_CEILING = descriptorCeiling();
    private static int wanted = LEAST_REGIONS;
    private static long tunedAt;
    private static long hits;
    private static long misses;
    private static long letGoThisWindow;
    private static long idleClosed;

    private RegionCache() {}

    @Nullable @SuppressWarnings("unchecked") public static synchronized <R extends IRegion<?>> R held(Object key) {
        R found = (R) OPEN.get(key);
        if (found == null) { misses++; }
        else { hits++; }
        return found;
    }

    public static synchronized void hold(Object key, IRegion<?> region) throws IOException {
        OPEN.put(key, region);
        tune();
        trim();
    }

    public static synchronized void flushAll() throws IOException {
        for (IRegion<?> region : OPEN.values()) { region.flush(); }
    }

    public static synchronized void closeAll() throws IOException {
        List<IRegion<?>> going = new ArrayList<>(OPEN.values());
        OPEN.clear();
        IOException failed = null;
        for (IRegion<?> region : going) {
            try { region.close(); }
            catch (IOException ex) { failed = ex; }
        }
        if (failed != null) { throw failed; }
    }

    public static synchronized void closeWhere(Predicate<Object> keyMatches) throws IOException {
        IOException failed = null;
        for (Iterator<Map.Entry<Object, IRegion<?>>> it = OPEN.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Object, IRegion<?>> entry = it.next();
            if (!keyMatches.test(entry.getKey())) { continue; }
            it.remove();
            try { entry.getValue().close(); }
            catch (IOException ex) { failed = ex; }
        }
        if (failed != null) { throw failed; }
    }

    private static void trim() throws IOException {
        while (OPEN.size() > wanted) {
            Iterator<Map.Entry<Object, IRegion<?>>> oldest = OPEN.entrySet().iterator();
            if (!oldest.hasNext()) { return; }
            IRegion<?> going = oldest.next().getValue();
            oldest.remove();
            idleClosed++;
            letGoThisWindow++;
            going.close();
        }
    }

    private static void tune() {
        long now = System.currentTimeMillis();
        if (now - tunedAt < TUNE_EVERY_MILLIS) { return; }
        tunedAt = now;
        int ceiling = Math.min(Config.chunks.regionCacheLimit > 0 ? Config.chunks.regionCacheLimit : MOST_REGIONS, DESCRIPTOR_CEILING);
        Runtime runtime = Runtime.getRuntime();
        float used = (runtime.totalMemory() - runtime.freeMemory()) / (float) runtime.maxMemory();
        int before = wanted;
        if (used > HEAP_TIGHT) { wanted = Math.max(LEAST_REGIONS, wanted - Math.max(STEP, wanted / 4)); }
        else if (used < HEAP_EASY && letGoThisWindow > 0L) { wanted = Math.min(ceiling, wanted + Math.max(STEP, wanted / 2)); }
        wanted = Math.max(LEAST_REGIONS, Math.min(wanted, ceiling));
        if (wanted != before) {
            ContentLog.LOGGER.debug("Region cache holds {} of {} file(s) it may, {}% of the heap in use, {} hit(s) to {} miss(es), {} file(s) let go so far",
                    OPEN.size(), wanted, Math.round(used * 100.0f), hits, misses, idleClosed);
        }
        hits = 0;
        misses = 0;
        letGoThisWindow = 0;
    }

    private static int descriptorCeiling() {
        try {
            for (String line : Files.readAllLines(Paths.get("/proc/self/limits"))) {
                if (!line.startsWith("Max open files")) { continue; }
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    if (!part.matches("\\d+")) { continue; }
                    int allowed = Math.max(LEAST_REGIONS, Math.min(MOST_REGIONS, Integer.parseInt(part) / DESCRIPTOR_SHARE));
                    ContentLog.LOGGER.info("This machine allows {} open file(s) at once, so a rubic world holds no more than {} region file(s) open", part, allowed);
                    return allowed;
                }
            }
        }
        catch (Exception unreadable) { ContentLog.LOGGER.warn("Could not read how many files this machine allows open at once, so no more than {} region file(s) will be held", DESCRIPTORS_ASSUMED / DESCRIPTOR_SHARE); }
        return DESCRIPTORS_ASSUMED / DESCRIPTOR_SHARE;
    }
}
