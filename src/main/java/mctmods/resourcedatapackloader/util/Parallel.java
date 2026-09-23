package mctmods.resourcedatapackloader.util;

import net.minecraft.Util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public final class Parallel {
    private Parallel() {}

    public static void each(int count, IntConsumer task) {
        if (count <= 0) { return; }
        AtomicInteger next = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(count);
        Runnable work = () -> {
            for (int at = next.getAndIncrement(); at < count; at = next.getAndIncrement()) {
                try { task.accept(at); }
                finally { done.countDown(); }
            }
        };
        int helpers = Math.min(count - 1, Runtime.getRuntime().availableProcessors() - 1);
        for (int helper = 0; helper < helpers; helper++) { CompletableFuture.runAsync(work, Util.backgroundExecutor()); }
        work.run();
        try { done.await(); }
        catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); }
    }
}
