package mctmods.resourcedatapackloader.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class FaceCache {
    private static final AtomicInteger EPOCH = new AtomicInteger(1);

    private FaceCache() {}

    public static int epoch() { return EPOCH.get(); }

    public static void rebaked() { EPOCH.incrementAndGet(); }
}
