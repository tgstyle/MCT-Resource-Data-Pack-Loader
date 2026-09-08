package mctmods.resourcedatapackloader.content.rubic.worldgen;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.util.compat.CompatHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WorldgenHangWatchdog {
    private static final int worldgenWatchdogTimeLimit = 60000;
    private static final int SAMPLES_KEPT = 40;
    public static final boolean ENABLED = "true".equalsIgnoreCase(System.getProperty("rdpl.rubic.wgen_hang_watchdog", "true"));
    private static final WorldgenHangWatchdog INSTANCE = new WorldgenHangWatchdog();

    static { init(); }

    private final WeakHashMap<Thread, Entry> entries = new WeakHashMap<>();
    private static volatile String crashInfo = null;

    private WorldgenHangWatchdog() {
        if (INSTANCE != null) { throw new IllegalStateException("Already initialized"); }
    }

    public static String getCrashInfo() { return crashInfo; }

    public static void startWorldGen() {
        synchronized (INSTANCE.entries) {
            INSTANCE.entries.compute(Thread.currentThread(), (t, old) -> {
                if (old == null) { return new Entry(); }
                old.count++;
                return old;
            });
        }
    }

    public static void endWorldGen() {
        synchronized (INSTANCE.entries) {
            Entry e = INSTANCE.entries.get(Thread.currentThread());
            if (e != null) {
                if (e.count <= 0) { INSTANCE.entries.remove(Thread.currentThread()); }
                else { e.count--; }
            }
        }
    }

    private static void init() {
        Thread t = new Thread(INSTANCE::run);
        t.setName("WorldGen hang watchdog thread");
        t.setDaemon(true);
        t.start();
    }

    @SuppressWarnings("BusyWait") private void run() {
        if (!ENABLED) { return; }
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Rubic.LOGGER.error("WorldGen hang watchdog interrupted", e);
            }
            try { watch(); }
            catch (Throwable oops) { Rubic.LOGGER.error("WorldGen hang watchdog failed to report a hang and would have died; it keeps watching instead", oops); }
        }
    }

    private void watch() {
            synchronized (entries) {
                for (Iterator<Map.Entry<Thread, Entry>> iterator = entries.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<Thread, Entry> entry = iterator.next();
                    Thread t = entry.getKey();
                    Entry e = entry.getValue();
                    if (e.samples.size() < SAMPLES_KEPT) { e.samples.add(t.getStackTrace()); }
                    long currentTime = System.nanoTime();
                    long dt = currentTime - e.startTime;
                    if (dt > TimeUnit.MILLISECONDS.toNanos(worldgenWatchdogTimeLimit)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("World generation taking ").append(dt / (double) TimeUnit.SECONDS.toNanos(1))
                                .append(" seconds, should be less than 50ms.\n");
                        sb.append("Samples collected during world generation:\n");
                        int i = 1;
                        for (StackTraceElement[] stacktrace : e.samples) {
                            sb.append("--------------------------------------------\n");
                            Set<String> likelyModsInvolved = CompatHandler.getModsForStacktrace(stacktrace);
                            sb.append("SAMPLE #").append(i).append(", likely mods involved: ").append(String.join(", ", likelyModsInvolved))
                                    .append('\n');
                            for (StackTraceElement traceElement : stacktrace) {
                                String modid = CompatHandler.getModForStacktraceElement(traceElement);
                                sb.append("\tat ").append(traceElement).append(" [Likely mod: ").append(modid).append("]\n");
                            }
                            i++;
                        }
                        String msg = sb.toString();
                        crashInfo = msg;
                        iterator.remove();
                        Rubic.LOGGER.fatal(msg);
                        stop(t);
                    }
                }
            }
    }

    @SuppressWarnings("deprecation") private static void stop(Thread t) {
        try { t.stop(); }
        catch (UnsupportedOperationException | LinkageError gone) {
            Rubic.LOGGER.fatal("This Java cannot stop a thread, so the hung world generation above is left to run and the watchdog stops watching it. Close the world; the samples are the report");
        }
    }

    private static class Entry {
        long startTime;
        int count;
        List<StackTraceElement[]> samples = new ArrayList<>();

        Entry() { startTime = System.nanoTime(); }
    }
}
