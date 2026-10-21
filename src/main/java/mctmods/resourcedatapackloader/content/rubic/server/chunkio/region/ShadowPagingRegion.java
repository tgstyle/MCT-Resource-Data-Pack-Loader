package mctmods.resourcedatapackloader.content.rubic.server.chunkio.region;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.content.rubic.regionlib.MultiUnsupportedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.UnsupportedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IHeaderDataEntryProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.RegionEntryLocation;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.IKeyIdToSectorMap;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.header.IntPackedSectorMap;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.ICorruptedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static java.nio.file.StandardOpenOption.*;

public class ShadowPagingRegion<K extends IKey> implements IRegion<K> {
    private static final int ZERO_BYTEBUFFER_CAPACITY = 4096;
    private static final ByteBuffer ZERO_BYTEBUFFER = ByteBuffer.allocateDirect(ZERO_BYTEBUFFER_CAPACITY).asReadOnlyBuffer();
    private final FileChannel file;
    private final IHeaderDataEntryProvider<?, K> headerEntryProvider;
    private final RegionKey regionKey;
    private final int sectorSize;
    private final SectorTracker<K> sectorTracker;
    private final Lock startWriteLock = new ReentrantLock();
    private final ReadView<K> idle = new ReadView<>(Collections.emptyMap(), Collections.emptyMap());
    private volatile ReadView<K> ongoingWrite;

    private ShadowPagingRegion(FileChannel file, SectorTracker<K> sectorTracker, IHeaderDataEntryProvider<?, K> headerEntryProvider, RegionKey regionKey, int sectorSize) {
        this.file = file;
        this.headerEntryProvider = headerEntryProvider;
        this.regionKey = regionKey;
        this.sectorSize = sectorSize;
        this.sectorTracker = sectorTracker;
    }

    private static ByteBuffer[] zeroes(int length) {
        List<ByteBuffer> target = new ArrayList<>();
        zeroes(length, target);
        return target.toArray(new ByteBuffer[0]);
    }

    private static void zeroes(int length, List<ByteBuffer> target) {
        for (int i = 0, count = ceilDiv(length, ZERO_BYTEBUFFER_CAPACITY); i < count; i++) {
            int remaining = length - i * ZERO_BYTEBUFFER_CAPACITY;
            target.add((ByteBuffer) ZERO_BYTEBUFFER.duplicate().clear().limit(Math.min(remaining, ZERO_BYTEBUFFER_CAPACITY)));
        }
    }

    @Override public void writeValue(K key, ByteBuffer value) throws IOException {
        ContentLog.LOGGER.warn("Using slow non-batch write at {} in {}", key, this.regionKey.getName());
        this.writeValues(Collections.singletonMap(key, value));
    }

    @Override public void writeValues(Map<K, ByteBuffer> entries) throws IOException {
        if (entries.isEmpty()) { return; }
        List<UnsupportedDataException.WithKey> exceptions = new ArrayList<>();
        Map<K, HeaderUpdate> pendingHeaderUpdates = new HashMap<>(entries.size());
        ReadView<K> view = this.startWrite(entries, null);
        try {
            this.reserveAndWriteData(view, entries, exceptions, pendingHeaderUpdates);
            this.doPendingHeaderUpdatesPass(pendingHeaderUpdates);
        }
        finally { this.finishWrite(view); }
        for (K key : pendingHeaderUpdates.keySet()) {
            ByteBuffer value = entries.get(key);
            if (value != null) { value.position(value.limit()); }
        }
        if (!exceptions.isEmpty()) { throw new MultiUnsupportedDataException(exceptions); }
    }

    private void reserveAndWriteData(ReadView<K> view, Map<K, ByteBuffer> entries, List<UnsupportedDataException.WithKey> exceptions, Map<K, HeaderUpdate> pendingHeaderUpdates) throws IOException {
        try {
            this.reserveHeaderEntriesPass(entries, exceptions, pendingHeaderUpdates);
            Map<K, ByteBuffer> accepted = new HashMap<>(pendingHeaderUpdates.size());
            for (K key : pendingHeaderUpdates.keySet()) { accepted.put(key, entries.get(key)); }
            view.accepted = accepted;
            this.writeDataPass(entries, pendingHeaderUpdates);
        }
        catch (RuntimeException | Error | IOException e) {
            this.rollBack(pendingHeaderUpdates, e, false);
            throw e;
        }
    }

    private void reserveHeaderEntriesPass(Map<K, ByteBuffer> entries, List<UnsupportedDataException.WithKey> exceptions, Map<K, HeaderUpdate> pendingHeaderUpdates) throws IOException {
        for (Map.Entry<K, ByteBuffer> entry : entries.entrySet()) {
            K key = entry.getKey();
            ByteBuffer value = entry.getValue();
            if (value == null) {
                pendingHeaderUpdates.put(key, this.sectorTracker.reserveDeletion(key));
                continue;
            }
            try { pendingHeaderUpdates.put(key, this.sectorTracker.reserve(key, this.getSectorNumber(value.remaining() + Integer.BYTES))); }
            catch (UnsupportedDataException e) { exceptions.add(new UnsupportedDataException.WithKey(e, key)); }
        }
    }

    private void writeDataPass(Map<K, ByteBuffer> entries, Map<K, HeaderUpdate> pendingHeaderUpdates) throws IOException {
        boolean shouldFlush = false;
        List<ByteBuffer> tempBuffers = new ArrayList<>();
        ByteBuffer lengthPrefixBuffer = ByteBuffer.allocate(Integer.BYTES);
        for (Map.Entry<K, HeaderUpdate> entry : pendingHeaderUpdates.entrySet()) {
            RegionEntryLocation location = entry.getValue().next;
            if (location == null) { continue; }
            ByteBuffer value = entries.get(entry.getKey()).slice();
            int size = value.remaining();
            int bytesOffset = location.getOffset() * this.sectorSize;
            tempBuffers.clear();
            tempBuffers.add(((ByteBuffer) lengthPrefixBuffer.clear()).putInt(0, size));
            tempBuffers.add(value);
            if ((lengthPrefixBuffer.capacity() + value.remaining()) % this.sectorSize != 0) {
                zeroes(this.sectorSize - (lengthPrefixBuffer.capacity() + value.remaining()) % this.sectorSize, tempBuffers);
            }
            assert tempBuffers.stream().mapToInt(ByteBuffer::remaining).sum() == location.getSize() * this.sectorSize;
            Utils.writeFully(this.file.position(bytesOffset), tempBuffers.toArray(new ByteBuffer[0]));
            shouldFlush = true;
        }
        if (shouldFlush) { this.file.force(true); }
    }

    private void doPendingHeaderUpdatesPass(Map<K, HeaderUpdate> pendingHeaderUpdates) throws IOException {
        if (pendingHeaderUpdates.isEmpty()) { return; }
        try {
            pendingHeaderUpdates.values().forEach(this.sectorTracker::commit);
            for (K key : pendingHeaderUpdates.keySet()) { this.updateHeaders(key); }
            this.file.force(true);
        }
        catch (RuntimeException | Error | IOException e) {
            this.rollBack(pendingHeaderUpdates, e, true);
            throw e;
        }
    }

    private void rollBack(Map<K, HeaderUpdate> pendingHeaderUpdates, Throwable cause, boolean rewriteHeaders) {
        for (Map.Entry<K, HeaderUpdate> entry : pendingHeaderUpdates.entrySet()) {
            try {
                this.sectorTracker.rollBack(entry.getKey(), entry.getValue());
                if (rewriteHeaders) { this.updateHeaders(entry.getKey()); }
            }
            catch (RuntimeException | Error | IOException failed) { cause.addSuppressed(failed); }
        }
    }

    private void updateHeaders(K key) throws IOException {
        int entryByteCount = headerEntryProvider.getEntryByteCount();
        ByteBuffer buf = ByteBuffer.allocate(entryByteCount);
        headerEntryProvider.apply(key).write(buf);
        buf.flip();
        Utils.writeFully(file.position((long) key.getId() * entryByteCount), buf);
    }

    private ReadView<K> startWrite(Map<K, ByteBuffer> pending, @Nullable Map<K, ByteBuffer> accepted) {
        this.startWriteLock.lock();
        try {
            ReadView<K> view = new ReadView<>(pending, accepted);
            this.ongoingWrite = view;
            this.idle.lock.writeLock().lock();
            return view;
        }
        catch (RuntimeException | Error e) {
            this.ongoingWrite = null;
            this.startWriteLock.unlock();
            throw e;
        }
    }

    private void finishWrite(ReadView<K> view) {
        this.idle.lock.writeLock().unlock();
        this.ongoingWrite = null;
        view.lock.writeLock().lock();
        this.startWriteLock.unlock();
    }

    private ReadView<K> startRead(K key) {
        while (true) {
            ReadView<K> write = this.ongoingWrite;
            ReadView<K> view = write == null ? this.idle : write;
            if (!view.lock.readLock().tryLock()) { continue; }
            if (view.accepted != null || !view.pending.containsKey(key)) { return view; }
            view.lock.readLock().unlock();
            Thread.yield();
        }
    }

    @Override public Optional<ByteBuffer> readValue(K key) throws IOException {
        ReadView<K> view = this.startRead(key);
        try {
            if (!view.holds(key)) { return this.readFromDisk(key); }
            ByteBuffer written = view.value(key);
            if (written == null) { return Optional.empty(); }
            ByteBuffer copy = ByteBuffer.allocate(written.remaining());
            copy.put(written.slice()).flip();
            return Optional.of(copy);
        }
        finally { view.lock.readLock().unlock(); }
    }

    private Optional<ByteBuffer> readFromDisk(K key) throws IOException {
        Optional<RegionEntryLocation> entryLocation = this.sectorTracker.getEntryLocation(key);
        if (!entryLocation.isPresent()) { return Optional.empty(); }
        RegionEntryLocation loc = entryLocation.get();
        int capacity = loc.getSize() * this.sectorSize;
        long position = (long) loc.getOffset() * this.sectorSize;
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        long at = position;
        while (buffer.hasRemaining()) {
            int read = this.file.read(buffer, at);
            if (read < 0) { break; }
            at += read;
        }
        buffer.flip();
        if (buffer.remaining() < Integer.BYTES) { throw new ICorruptedDataException("Expected " + Integer.BYTES + " byte(s) at " + position + " but the file ends there"); }
        int dataLength = buffer.getInt();
        if (dataLength < 0 || dataLength > capacity) { throw new ICorruptedDataException("Expected data size max " + capacity + " but found " + dataLength); }
        if (dataLength > buffer.remaining()) { throw new ICorruptedDataException("Expected " + dataLength + " byte(s) at " + (position + Integer.BYTES) + " but only " + buffer.remaining() + " are there"); }
        buffer.limit(buffer.position() + dataLength);
        return Optional.of(buffer.slice());
    }

    @Override public boolean hasValue(K key) {
        ReadView<K> view = this.startRead(key);
        try { return view.holds(key) ? view.value(key) != null : this.sectorTracker.getEntryLocation(key).isPresent(); }
        finally { view.lock.readLock().unlock(); }
    }

    private int getSectorNumber(int bytes) { return ceilDiv(bytes, sectorSize); }

    @Override public void flush() throws IOException {
        ReadView<K> view = this.startWrite(Collections.emptyMap(), Collections.emptyMap());
        try {
            boolean fileLengthChanged = false;
            fileLengthChanged |= this.ensureSectorSizeAligned();
            fileLengthChanged |= this.erasePendingSectors();
            this.file.force(fileLengthChanged);
        }
        finally { this.finishWrite(view); }
    }

    @Override public void close() throws IOException {
        ReadView<K> view = this.startWrite(Collections.emptyMap(), Collections.emptyMap());
        try {
            this.ensureSectorSizeAligned();
            this.erasePendingSectors();
        }
        finally {
            try { this.file.close(); }
            finally { this.finishWrite(view); }
        }
    }

    private boolean ensureSectorSizeAligned() throws IOException {
        long fileSize = this.file.size();
        if (fileSize % sectorSize != 0) {
            this.file.position(fileSize);
            int extra = (int) (sectorSize - (fileSize % sectorSize));
            Utils.writeFully(this.file, zeroes(extra));
            assert this.file.size() % sectorSize == 0;
            return true;
        }
        return false;
    }

    private boolean erasePendingSectors() throws IOException {
        for (RegionEntryLocation range : this.sectorTracker.getAllSectorsPendingErasure()) {
            this.file.position(range.getOffset() * (long) this.sectorSize);
            Utils.writeFully(this.file, zeroes(Math.multiplyExact(range.getSize(), this.sectorSize)));
            this.sectorTracker.markSectorsErased(range);
        }
        long expectedFileSize = this.sectorTracker.getSectorsLength() * (long) this.sectorSize;
        long actualFileSize = this.file.size();
        assert expectedFileSize <= actualFileSize : "region file is too short???";
        if (actualFileSize > expectedFileSize) {
            this.file.truncate(expectedFileSize);
            return true;
        }
        return false;
    }

    private static int ceilDiv(int x, int y) { return -Math.floorDiv(-x, y); }

    public static <L extends IKey> ShadowPagingRegion.Builder<L> builder() { return new ShadowPagingRegion.Builder<>(); }


    public static class Builder<K extends IKey> {
        private Path directory;
        private int sectorSize = 512;
        private RegionKey regionKey;
        private IKeyProvider keyProvider;

        public Builder<K> setDirectory(Path path) {
            this.directory = path;
            return this;
        }

        public Builder<K> setRegionKey(RegionKey key) {
            this.regionKey = key;
            return this;
        }

        public Builder<K> setKeyProvider(IKeyProvider keyProvider) {
            this.keyProvider = keyProvider;
            return this;
        }

        public Builder<K> setSectorSize(int sectorSize) {
            this.sectorSize = sectorSize;
            return this;
        }

        public ShadowPagingRegion<K> build() throws IOException {
            FileChannel file = FileChannel.open(directory.resolve(regionKey.getName()), CREATE, READ, WRITE);
            try {
                int entryMapSectors = ceilDiv(keyProvider.getKeyCount(regionKey) * Integer.BYTES, sectorSize);
                IntPackedSectorMap<K> sectorMap = IntPackedSectorMap.readOrCreate(file, keyProvider.getKeyCount(regionKey));
                SectorTracker<K> regionSectorTracker = SectorTracker.fromFile(file, sectorMap, entryMapSectors, sectorSize);
                return new ShadowPagingRegion<>(file, regionSectorTracker, sectorMap.headerEntryProvider(), this.regionKey, this.sectorSize);
            }
            catch (IOException | RuntimeException e) {
                try { file.close(); }
                catch (IOException closing) { e.addSuppressed(closing); }
                throw e;
            }
        }
    }

    private static final class ReadView<K> {
        private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
        private final Map<K, ByteBuffer> pending;
        @Nullable private volatile Map<K, ByteBuffer> accepted;

        private ReadView(Map<K, ByteBuffer> pending, @Nullable Map<K, ByteBuffer> accepted) {
            this.pending = pending;
            this.accepted = accepted;
        }

        private boolean holds(K key) {
            Map<K, ByteBuffer> found = this.accepted;
            return found != null && found.containsKey(key);
        }

        @Nullable private ByteBuffer value(K key) {
            Map<K, ByteBuffer> found = this.accepted;
            return found == null ? null : found.get(key);
        }
    }

    private static final class HeaderUpdate {
        @Nullable private final RegionEntryLocation prev;
        @Nullable private final RegionEntryLocation next;

        private HeaderUpdate(@Nullable RegionEntryLocation prev, @Nullable RegionEntryLocation next) {
            this.prev = prev;
            this.next = next;
        }
    }

    private static class SectorTracker<K extends IKey> {
        private final BitSet usedSectors;
        private final IKeyIdToSectorMap<K> sectorMap;
        private final BitSet sectorsPendingErasure = new BitSet();

        private SectorTracker(BitSet usedSectors, IKeyIdToSectorMap<K> sectorMap) {
            this.usedSectors = usedSectors;
            this.sectorMap = sectorMap;
        }

        public Optional<RegionEntryLocation> getEntryLocation(K key) { return sectorMap.getEntryLocation(key); }

        public HeaderUpdate reserve(K key, int requestedSize) throws IOException {
            RegionEntryLocation existing = sectorMap.getEntryLocation(key).orElse(null);
            RegionEntryLocation found = findFree(requestedSize);
            this.sectorMap.setOffsetAndSize(key, found);
            this.updateUsedSectorsFor(null, found);
            return new HeaderUpdate(existing, found);
        }

        public HeaderUpdate reserveDeletion(K key) throws IOException {
            RegionEntryLocation existing = sectorMap.getEntryLocation(key).orElse(null);
            this.sectorMap.setOffsetAndSize(key, new RegionEntryLocation(0, 0));
            return new HeaderUpdate(existing, null);
        }

        public void commit(HeaderUpdate update) { this.updateUsedSectorsFor(update.prev, null); }

        public void rollBack(K key, HeaderUpdate update) throws IOException {
            this.sectorMap.setOffsetAndSize(key, update.prev == null ? new RegionEntryLocation(0, 0) : update.prev);
            this.updateUsedSectorsFor(update.next, update.prev);
        }

        private RegionEntryLocation findFree(int requestedSize) { return Utils.findFreeSectors(usedSectors, requestedSize); }

        private void updateUsedSectorsFor(@Nullable RegionEntryLocation oldSectorLocation, @Nullable RegionEntryLocation newSectorLocation) {
            if (oldSectorLocation != null) {
                int oldOffset = oldSectorLocation.getOffset();
                usedSectors.set(oldOffset, oldOffset + oldSectorLocation.getSize(), false);
                this.sectorsPendingErasure.set(oldOffset, oldOffset + oldSectorLocation.getSize(), true);
            }
            if (newSectorLocation != null) {
                int newOffset = newSectorLocation.getOffset();
                usedSectors.set(newOffset, newOffset + newSectorLocation.getSize(), true);
                this.sectorsPendingErasure.set(newOffset, newOffset + newSectorLocation.getSize(), false);
            }
        }

        public List<RegionEntryLocation> getAllSectorsPendingErasure() {
            List<RegionEntryLocation> out = new ArrayList<>();
            for (int next = 0; (next = this.sectorsPendingErasure.nextSetBit(next)) >= 0; ) {
                int rangeStart = next;
                int rangeEnd = this.sectorsPendingErasure.nextClearBit(rangeStart);
                out.add(new RegionEntryLocation(rangeStart, rangeEnd - rangeStart));
                next = rangeEnd;
            }
            return out;
        }

        public void markSectorsErased(RegionEntryLocation range) {
            assert this.sectorsPendingErasure.get(range.getOffset()) && this.sectorsPendingErasure.nextClearBit(range.getOffset()) == range.getSize() + range.getOffset() : "the given range isn't pending erasure";
            this.sectorsPendingErasure.clear(range.getOffset(), range.getOffset() + range.getSize());
        }

        public int getSectorsLength() { return this.usedSectors.length(); }

        public static <L extends IKey> SectorTracker<L> fromFile(SeekableByteChannel file, IKeyIdToSectorMap<L> sectorMap, int reservedSectors, int sectorSize) throws IOException {
            BitSet usedSectors = new BitSet(Math.max((int) (file.size() / sectorSize), reservedSectors));
            for (int i = 0; i < reservedSectors; i++) { usedSectors.set(i, true); }
            for (RegionEntryLocation loc : sectorMap) {
                int offset = loc.getOffset();
                int size = loc.getSize();
                for (int i = 0; i < size; i++) { usedSectors.set(offset + i); }
            }
            return new SectorTracker<>(usedSectors, sectorMap);
        }
    }
}
