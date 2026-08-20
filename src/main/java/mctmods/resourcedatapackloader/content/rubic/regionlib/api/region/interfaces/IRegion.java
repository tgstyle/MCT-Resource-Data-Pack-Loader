package mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces;

import mctmods.resourcedatapackloader.content.rubic.regionlib.MultiUnsupportedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.UnsupportedDataException;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IRegion<K extends IKey> extends Flushable, Closeable {
    void writeValue(K key, ByteBuffer value) throws IOException;

    default void writeValues(Map<K, ByteBuffer> entries) throws IOException {
        List<UnsupportedDataException.WithKey> exceptions = new ArrayList<>();
        for (Map.Entry<K, ByteBuffer> entry : entries.entrySet()) {
            try {
                this.writeValue(entry.getKey(), entry.getValue());
            } catch (UnsupportedDataException.WithKey e) {
                exceptions.add(e);
            } catch (UnsupportedDataException e) {
                exceptions.add(new UnsupportedDataException.WithKey(e, entry.getKey()));
            }
        }
        if (!exceptions.isEmpty()) { throw new MultiUnsupportedDataException(exceptions); }
    }

    Optional<ByteBuffer> readValue(K key) throws IOException;

    boolean hasValue(K key);
}
