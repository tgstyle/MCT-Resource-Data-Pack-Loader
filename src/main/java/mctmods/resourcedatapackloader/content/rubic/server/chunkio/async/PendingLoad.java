package mctmods.resourcedatapackloader.content.rubic.server.chunkio.async;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;

abstract class PendingLoad<T> implements Runnable {
    private final List<Consumer<T>> waiting = new CopyOnWriteArrayList<>();
    private final AtomicBoolean readStarted = new AtomicBoolean();
    private final AtomicBoolean handedOver = new AtomicBoolean();
    private final CountDownLatch read = new CountDownLatch(1);
    @Nullable private Throwable failure;

    abstract void readOffThread() throws Exception;

    abstract void applyOnServerThread();

    @Nullable abstract T loaded();

    abstract String describe();

    void waitFor(Consumer<T> listener) { waiting.add(listener); }

    void stopWaiting(Consumer<T> listener) { waiting.remove(listener); }

    boolean nobodyWaiting() { return waiting.isEmpty(); }

    boolean claimRead() { return readStarted.compareAndSet(false, true); }

    @Override public void run() {
        if (!claimRead()) { return; }
        readNow();
    }

    void readNow() {
        try { readOffThread(); }
        catch (Throwable thrown) { failure = thrown; }
        finally { read.countDown(); }
    }

    void awaitRead() {
        try { read.await(); }
        catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for " + describe(), interrupted);
        }
    }

    boolean hasBeenHandedOver() { return handedOver.get(); }

    void handOver() {
        if (!handedOver.compareAndSet(false, true)) { return; }
        if (failure != null) { throw new RuntimeException("Could not read " + describe(), failure); }
        applyOnServerThread();
        T value = loaded();
        for (Consumer<T> listener : waiting) { listener.accept(value); }
        waiting.clear();
    }
}
