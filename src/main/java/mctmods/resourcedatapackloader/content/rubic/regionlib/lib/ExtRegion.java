package mctmods.resourcedatapackloader.content.rubic.regionlib.lib;

import mctmods.resourcedatapackloader.content.rubic.regionlib.UnsupportedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IHeaderDataEntry;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IHeaderDataEntryProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static java.nio.file.StandardCopyOption.*;
import static java.nio.file.StandardOpenOption.*;

public class ExtRegion<K extends IKey> implements IRegion<K> {
    private final Path directory;
    private final List<IHeaderDataEntryProvider<?, K>> headerData;
    private final int totalHeaderSize;
    private final BitSet exists;
    private boolean initialized = false;

    public ExtRegion(Path saveDirectory, List<IHeaderDataEntryProvider<?, K>> headerData, IKeyProvider keyProvider, RegionKey regionKey)
            throws IOException {
        this.directory = saveDirectory.resolve(regionKey.getName() + ".ext");
        this.headerData = headerData;
        int headerSize = 0;
        for (IHeaderDataEntryProvider<?, ?> p : headerData) { headerSize += p.getEntryByteCount(); }
        this.totalHeaderSize = headerSize;
        this.exists = new BitSet(keyProvider.getKeyCount(regionKey));
        if (!Files.exists(this.directory)) { return; }
        this.initialized = true;
        try(Stream<Path> stream = Files.list(this.directory)) {
            stream.forEach(p -> {
                String name = p.getFileName().toString();
                if (!name.matches("\\d{1,9}")) {
                    return;
                }
                int i = Integer.parseInt(name);
                if (i < keyProvider.getKeyCount(regionKey)) { exists.set(i); }
            });
        }
    }

    @Override public void writeValue(K key, ByteBuffer value) throws IOException {
        if (value == null && (!initialized || exists.isEmpty() || !exists.get(key.getId()))) { return; }
        if (!initialized) {
            Utils.createDirectories(this.directory);
            initialized = true;
        }
        String fileName = String.valueOf(key.getId());
        Path file = directory.resolve(fileName);
        if (!Files.exists(file)) {
            if (value == null) {
                exists.clear(key.getId());
                return;
            }
        }
        else if (value == null) {
            Files.delete(file);
            exists.clear(key.getId());
            return;
        }
        Path tmpFile = directory.resolve(fileName + ".tmp");
        List<ByteBuffer> buffers = new ArrayList<>(this.headerData.size() + 1);
        for (IHeaderDataEntryProvider<?, K> h : headerData) {
            IHeaderDataEntry entry = h.apply(key);
            ByteBuffer buf = ByteBuffer.allocate(h.getEntryByteCount());
            entry.write(buf);
            buf.flip();
            buffers.add(buf);
        }
        buffers.add(value);
        try (GatheringByteChannel channel = FileChannel.open(tmpFile, WRITE, CREATE, TRUNCATE_EXISTING, DSYNC)) {
            Utils.writeFully(channel, buffers.toArray(new ByteBuffer[0]));
        }
        Files.move(tmpFile, file, ATOMIC_MOVE, REPLACE_EXISTING);
        exists.set(key.getId());
    }

    @Override public Optional<ByteBuffer> readValue(K key) throws IOException {
        if (!initialized || !exists.get(key.getId())) { return Optional.empty(); }
        Path file = directory.resolve(String.valueOf(key.getId()));
        if (!Files.exists(file)) {
            exists.set(key.getId(), false);
            return Optional.empty();
        }
        try (SeekableByteChannel channel = Files.newByteChannel(file)) {
            long size = channel.size();
            if (size > Integer.MAX_VALUE) { throw new UnsupportedDataException("Size " + size + " is too big"); }
            ByteBuffer buf = ByteBuffer.wrap(new byte[(int) (size - totalHeaderSize)]);
            Utils.readFully(channel.position(totalHeaderSize), buf);
            buf.rewind();
            return Optional.of(buf);
        }
    }

    @Override public boolean hasValue(K key) { return exists.get(key.getId()); }

    @Override public void flush() throws IOException {
    }

    @Override public void close() throws IOException {
    }
}
