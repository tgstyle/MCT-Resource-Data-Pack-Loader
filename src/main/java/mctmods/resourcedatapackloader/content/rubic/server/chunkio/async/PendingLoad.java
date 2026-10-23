package mctmods.resourcedatapackloader.content.rubic.server.chunkio.async;

import mctmods.resourcedatapackloader.util.ContentLog;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;

abstract class PendingLoad<T> {
    private final List<Consumer<T>> waiting = new CopyOnWriteArrayList<>();
    private final AtomicBoolean readStarted = new AtomicBoolean();
    private final AtomicBoolean handedOver = new AtomicBoolean();
    private final AtomicBoolean applied = new AtomicBoolean();
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

    void abandon() { handedOver.set(true); }

    void apply() {
        if (!applied.compareAndSet(false, true)) { return; }
        if (failure != null) { ContentLog.LOGGER.error("Could not read {}, so it is handed over as missing", describe(), failure); }
        else { applyOnServerThread(); }
    }

    void handOver() {
        if (!handedOver.compareAndSet(false, true)) { return; }
        apply();
        T value = failure == null ? loaded() : null;
        for (Consumer<T> listener : waiting) { listener.accept(value); }
        waiting.clear();
    }
}
