package mctmods.resourcedatapackloader.core.util;

public final class Heap {
    private Heap() {}

    public static long heapUsed() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
