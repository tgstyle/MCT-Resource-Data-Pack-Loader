package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.core.network.HushMessage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class BlastHush {
    private static final Set<HushMessage> HUSHED = ConcurrentHashMap.newKeySet();

    private BlastHush() {}

    public static void mark(HushMessage blast) { HUSHED.add(blast); }

    public static boolean audible(double x, double y, double z) { return !HUSHED.remove(new HushMessage(x, y, z)); }
}
