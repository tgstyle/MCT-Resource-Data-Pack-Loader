package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.MessageHush;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class BlastHush {
    private static final Set<MessageHush> HUSHED = ConcurrentHashMap.newKeySet();

    private BlastHush() {}

    public static void mark(MessageHush blast) { HUSHED.add(blast); }

    public static boolean hushed(double x, double y, double z) { return HUSHED.remove(new MessageHush(x, y, z)); }
}
