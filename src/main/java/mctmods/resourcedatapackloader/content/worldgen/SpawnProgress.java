package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.Util;
import net.minecraft.util.Mth;
import java.util.concurrent.atomic.AtomicInteger;

public final class SpawnProgress {
    private static final int SITING_SHARE = 85;
    private static final AtomicInteger QUEUED = new AtomicInteger();
    private static final AtomicInteger SITED = new AtomicInteger();
    private static final AtomicInteger SHOWN = new AtomicInteger();
    private static volatile boolean spawning;
    private static volatile int queuedBeforeSpawn;
    private static volatile long startedAt;

    private SpawnProgress() {}

    public static void begin() {
        spawning = false;
        queuedBeforeSpawn = 0;
        QUEUED.set(0);
        SITED.set(0);
        SHOWN.set(0);
        startedAt = Util.getMillis();
    }

    static void queued(int count) { QUEUED.addAndGet(count); }

    static void sited() { SITED.incrementAndGet(); }

    public static void spawnPreparing() {
        int queued = QUEUED.get();
        queuedBeforeSpawn = queued;
        spawning = true;
        ContentLog.LOGGER.debug("The world took {} ms to load before its spawn area, siting {} of {} queued city region(s), so the loading screen hands over to the spawn chunks", Util.getMillis() - startedAt, SITED.get(), queued);
    }

    public static int shown(int game) {
        int held = Mth.clamp(game, 0, 100);
        int now;
        if (!spawning) {
            int queued = QUEUED.get();
            now = queued == 0 ? 0 : Math.min(SITING_SHARE, SITED.get() * SITING_SHARE / queued);
        }
        else if (queuedBeforeSpawn == 0) { now = held; }
        else { now = SITING_SHARE + held * (100 - SITING_SHARE) / 100; }
        return SHOWN.accumulateAndGet(now, Math::max);
    }
}
