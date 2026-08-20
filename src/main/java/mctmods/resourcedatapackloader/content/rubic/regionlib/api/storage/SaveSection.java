package mctmods.resourcedatapackloader.content.rubic.regionlib.api.storage;

import mctmods.resourcedatapackloader.content.rubic.regionlib.MultiUnsupportedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.UnsupportedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.SaveSectionException;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class SaveSection<K extends IKey> implements Flushable, Closeable {
    private static final ByteBuffer DUMMY_EMPTY = ByteBuffer.allocate(0);
    private final List<IRegionProvider<K>> regionProviders;

    public SaveSection(List<IRegionProvider<K>> regionProviders) { this.regionProviders = regionProviders; }

    public void save(K key, ByteBuffer value) throws IOException {
        ByteBuffer toWrite = value;
        List<UnsupportedDataException> exceptions = new ArrayList<>();
        for (IRegionProvider<K> prov : regionProviders) {
            ByteBuffer toWriteFinal = toWrite;
            prov.forRegion(key, r -> {
                try {
                    r.writeValue(key, toWriteFinal);
                    exceptions.clear();
                } catch (UnsupportedDataException ex) {
                    exceptions.add(ex);
                    r.writeValue(key, null);
                }
            });
            if (exceptions.isEmpty()) { toWrite = null; }
        }
        if (!exceptions.isEmpty())
            throw new SaveSectionException("No region provider supporting key " + key + " with data size " + value.capacity(), exceptions);
    }

    public void save(Map<K, ByteBuffer> entries) throws IOException {
        Map<K, ByteBuffer> pendingEntries = new HashMap<>(entries);
        Map<K, List<UnsupportedDataException>> exceptions = new HashMap<>();
        Map<RegionKey, List<K>> positionsByRegion = pendingEntries.keySet().stream().collect(Collectors.groupingBy(IKey::getRegionKey, Collectors.toList()));
        for (List<K> positionsIn : positionsByRegion.values()) {
            for (IRegionProvider<K> prov : regionProviders) {
                prov.forRegion(positionsIn.get(0), r -> {
                    List<K> positions = positionsIn;
                    try {
                        Map<K, ByteBuffer> regionEntries = new HashMap<>(positions.size());
                        positions.forEach(k -> regionEntries.put(k, pendingEntries.get(k)));
                        r.writeValues(regionEntries);
                    } catch (MultiUnsupportedDataException ex) {
                        Map<K, UnsupportedDataException> children = ex.getChildren();
                        positions = positions.stream().filter(((Predicate<K>) children::containsKey).negate()).collect(Collectors.toList());
                        children.forEach((k, e) -> exceptions.computeIfAbsent(k, unused -> new ArrayList<>()).add(e));
                        Map<K, ByteBuffer> toNulls = new HashMap<>(positions.size());
                        children.forEach((k, v) -> toNulls.put(k, null));
                        r.writeValues(toNulls);
                    }
                    positions.forEach(k -> {
                        exceptions.remove(k);
                        pendingEntries.put(k, null);
                    });
                });
            }
        }
        if (!exceptions.isEmpty()) {
            throw new SaveSectionException("multiple write errors", exceptions.entrySet().stream()
                    .map(e -> new SaveSectionException("No region provider supporting key " + e.getKey() + " with data size " + entries.get(e.getKey()), e.getValue()))
                    .collect(Collectors.toList()));
        }
    }

    public Optional<ByteBuffer> load(K key, boolean createRegion) throws IOException {
        for (IRegionProvider<K> prov : regionProviders) {
            ByteBuffer buf =
                    createRegion ? prov.fromRegion(key, r -> r.readValue(key)).orElse(null)
                            : prov.fromExistingRegion(key, r -> r.readValue(key)).orElse(Optional.of(DUMMY_EMPTY)).orElse(null);
            if (buf != null) { return buf == DUMMY_EMPTY ? Optional.empty() : Optional.of(buf); }
        }
        return Optional.empty();
    }

    public boolean hasEntry(K key) throws IOException {
        for (IRegionProvider<K> prov : this.regionProviders) {
            if (prov.fromExistingRegion(key, r -> r.hasValue(key)).orElse(false)) { return true; }
        }
        return false;
    }

    @Override public void flush() throws IOException {
        for (IRegionProvider<K> prov : this.regionProviders) { prov.flush(); }
    }

    @Override public void close() throws IOException {
        for (IRegionProvider<K> prov : this.regionProviders) { prov.close(); }
    }
}
