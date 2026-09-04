package mctmods.resourcedatapackloader.content.rubic.server.chunkio.region;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedConsumer;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.ICheckedFunction;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class CachedRegionProvider<K extends IKey> implements IRegionProvider<K> {
    private final IRegionProvider<K> reader;
    private boolean closed;

    public CachedRegionProvider(IRegionProvider<K> reader) { this.reader = reader; }

    @Override public <R> Optional<R> fromExistingRegion(K key, ICheckedFunction<? super IRegion<K>, R, IOException> work) throws IOException {
        IRegion<K> region = openIfThere(key);
        return region == null ? Optional.empty() : Optional.of(work.apply(region));
    }

    @Override public <R> R fromRegion(K key, ICheckedFunction<? super IRegion<K>, R, IOException> work) throws IOException { return work.apply(getRegion(key)); }

    @Override public void forRegion(K key, ICheckedConsumer<? super IRegion<K>, IOException> work) throws IOException { work.accept(getRegion(key)); }

    @Override public IRegion<K> getRegion(K key) throws IOException {
        awake();
        Held held = new Held(key.getRegionKey(), reader);
        IRegion<K> region = RegionCache.held(held);
        if (region != null) { return region; }
        region = reader.getRegion(key);
        RegionCache.hold(held, region);
        return region;
    }

    @Override public Optional<IRegion<K>> getExistingRegion(K key) throws IOException {
        IRegion<K> region = openIfThere(key);
        return Optional.ofNullable(region);
    }


    @Override public void flush() throws IOException {
        awake();
        RegionCache.flushAll();
        reader.flush();
    }

    @Override public void close() throws IOException {
        awake();
        RegionCache.closeWhere(key -> key instanceof Held && ((Held) key).reader == reader);
        reader.close();
        closed = true;
    }

    @Nullable private IRegion<K> openIfThere(K key) throws IOException {
        awake();
        Held held = new Held(key.getRegionKey(), reader);
        IRegion<K> region = RegionCache.held(held);
        if (region != null) { return region; }
        region = reader.getExistingRegion(key).orElse(null);
        if (region != null) { RegionCache.hold(held, region); }
        return region;
    }

    private void awake() {
        if (closed) { throw new IllegalStateException("This region provider is closed"); }
    }

    private static final class Held {
        private final RegionKey region;
        private final IRegionProvider<?> reader;

        Held(RegionKey region, IRegionProvider<?> reader) {
            this.region = region;
            this.reader = reader;
        }

        @Override public boolean equals(Object other) {
            if (other == this) { return true; }
            if (!(other instanceof Held)) { return false; }
            Held held = (Held) other;
            return region.equals(held.region) && reader == held.reader;
        }

        @Override public int hashCode() { return region.hashCode() * 31 + Objects.hashCode(reader); }

        @Override public String toString() { return region.getName(); }
    }
}
