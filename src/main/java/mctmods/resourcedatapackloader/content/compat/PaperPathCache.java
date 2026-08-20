package mctmods.resourcedatapackloader.content.compat;

import mctmods.resourcedatapackloader.util.ContentLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PaperPathCache {
    private static final String HOLDER = "net.caffeinemc.lithium.ai.pathing.PathNodeCache";
    private static Method opens;
    private static Method closes;
    private static Field switched;
    private static boolean looked;
    private static boolean ours;
    private static boolean told;

    private PaperPathCache() {}

    public static void open() {
        if (!looked) { look(); }
        if (opens == null) { return; }
        if (!ours && theirsAlready()) {
            stepAside();
            return;
        }
        try {
            closes.invoke(null);
            opens.invoke(null);
            ours = true;
        }
        catch (Exception ex) { give(ex); }
    }

    public static void close() {
        if (closes == null || !ours) { return; }
        try {
            closes.invoke(null);
            ours = false;
        }
        catch (Exception ex) { give(ex); }
    }

    private static boolean theirsAlready() {
        try { return switched != null && switched.getBoolean(null); }
        catch (Exception ex) { return false; }
    }

    private static void stepAside() {
        opens = null;
        closes = null;
        switched = null;
        ContentLog.LOGGER.info("PaperFixes now asks for its own note of which chunk sections are safe to walk beside, so this mod has stopped switching it on for it");
    }

    private static synchronized void look() {
        if (looked) { return; }
        looked = true;
        try {
            Class<?> held = Class.forName(HOLDER, false, PaperPathCache.class.getClassLoader());
            opens = held.getMethod("enableChunkCache");
            closes = held.getMethod("disableChunkCache");
            switched = held.getDeclaredField("dangerCacheEnabled");
            switched.setAccessible(true);
            ContentLog.LOGGER.info("PaperFixes keeps a note of which chunk sections are safe to walk beside, but never asks for it. It is switched on around each path search now, which is what it was written for");
        }
        catch (Throwable missing) {
            opens = null;
            closes = null;
            switched = null;
        }
    }

    private static void give(Exception ex) {
        opens = null;
        closes = null;
        switched = null;
        if (told) { return; }
        told = true;
        ContentLog.LOGGER.error("PaperFixes would not take its own note of safe chunk sections, so it is left switched off", ex);
    }
}
