package mctmods.resourcedatapackloader.content.rubic.server.chunkio.region;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
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
import java.io.UncheckedIOException;
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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static java.nio.file.StandardOpenOption.*;

public class ShadowPagingRegion<K extends IKey> implements IRegion<K> {
	private static final int ZERO_BYTEBUFFER_CAPACITY = 4096;
	private static final ByteBuffer ZERO_BYTEBUFFER = ByteBuffer.allocateDirect(ZERO_BYTEBUFFER_CAPACITY).asReadOnlyBuffer();

	private static ByteBuffer[] zeroes(int length) {
		ByteBuffer[] arr = new ByteBuffer[Math.floorDiv(length - 1, ZERO_BYTEBUFFER_CAPACITY) + 1];
		for (int i = 0; i < arr.length; i++) {
			int remaining = length - i * ZERO_BYTEBUFFER_CAPACITY;
			arr[i] = (ByteBuffer) ZERO_BYTEBUFFER.duplicate().clear().limit(Math.min(remaining, ZERO_BYTEBUFFER_CAPACITY));
		}
		return arr;
	}

	private static void zeroes(int length, List<ByteBuffer> target) {
		for (int i = 0, count = Math.floorDiv(length - 1, ZERO_BYTEBUFFER_CAPACITY) + 1; i < count; i++) {
			int remaining = length - i * ZERO_BYTEBUFFER_CAPACITY;
			target.add((ByteBuffer) ZERO_BYTEBUFFER.duplicate().clear().limit(Math.min(remaining, ZERO_BYTEBUFFER_CAPACITY)));
		}
	}

	private final FileChannel file;
	private final IHeaderDataEntryProvider<?, K> headerEntryProvider;
	private final RegionKey regionKey;
    private final int sectorSize;
	private final SectorTracker<K> sectorTracker;
	private final ReadWriteLock dataLock = new ReentrantReadWriteLock();
	private final ReadWriteLock reserveSectorsLock = new ReentrantReadWriteLock();
	private ShadowPagingRegion(FileChannel file, SectorTracker<K> sectorTracker, IHeaderDataEntryProvider<?, K> headerEntryProvider, RegionKey regionKey, int sectorSize) {
		this.file = file;
		this.headerEntryProvider = headerEntryProvider;
		this.regionKey = regionKey;
        this.sectorSize = sectorSize;
		this.sectorTracker = sectorTracker;
	}

	@Override public void writeValue(K key, ByteBuffer value) throws IOException {
		Rubic.LOGGER.warn("Using slow non-batch write at {} in {}", key, this.regionKey.getName());
		this.writeValues(Collections.singletonMap(key, value));
	}

	@Override public void writeValues(Map<K, ByteBuffer> entries) throws IOException {
		if (entries.isEmpty()) { return; }
		List<UnsupportedDataException.WithKey> exceptions = new ArrayList<>();
		List<K> pendingRemovals = new ArrayList<>();
		Map<K, RegionEntryLocation> pendingPrevLocations = new HashMap<>(entries.size());
		Map<K, RegionEntryLocation> entryLocationsToUse = new HashMap<>(entries.size());
		Lock sectorLock = reserveSectorsLock.writeLock();
		Lock mainLock = dataLock.writeLock();
		sectorLock.lock();
		mainLock.lock();
		try {
			reserveHeaderEntriesPass(entries, exceptions, pendingRemovals, pendingPrevLocations, entryLocationsToUse);
		} finally {
			sectorLock.unlock();
		}
		try {
			boolean shouldFlush = writeDataPass(entries, exceptions, entryLocationsToUse);
			if (shouldFlush) { this.file.force(true); }
			doPendingHeaderUpdatesPass(pendingRemovals, pendingPrevLocations);
			if (!exceptions.isEmpty()) { throw new MultiUnsupportedDataException(exceptions); }
		} finally {
			mainLock.unlock();
		}
	}

	private void reserveHeaderEntriesPass(Map<K, ByteBuffer> entries, List<UnsupportedDataException.WithKey> exceptions,
	                                      List<K> pendingRemovals, Map<K, RegionEntryLocation> pendingPrevLocations, Map<K, RegionEntryLocation> entryLocationsToUse) throws IOException {
        for (Map.Entry<K, ByteBuffer> entry : entries.entrySet()) {
            K key = entry.getKey();
            ByteBuffer value = entry.getValue();
            try {
                if (value == null) { pendingRemovals.add(key); }
                else {
                    int size = value.remaining();
                    int sizeWithSizeInfo = size + Integer.BYTES;
                    int numSectors = this.getSectorNumber(sizeWithSizeInfo);
                    pendingPrevLocations.put(key, this.sectorTracker.getEntryLocation(key).orElse(null));
                    entryLocationsToUse.put(key, this.sectorTracker.reserveForKey(key, numSectors));
                }
            } catch (UnsupportedDataException e) {
                exceptions.add(new UnsupportedDataException.WithKey(e, key));
            }
        }
	}

	private boolean writeDataPass(Map<K, ByteBuffer> entries, List<UnsupportedDataException.WithKey> exceptions,
	                              Map<K, RegionEntryLocation> entryLocationsToUse) throws IOException {
		boolean shouldFlush = false;
		List<ByteBuffer> tempBuffers = new ArrayList<>();
		ByteBuffer lengthPrefixBuffer = ByteBuffer.allocate(Integer.BYTES);
        for (Map.Entry<K, ByteBuffer> entry : entries.entrySet()) {
            K key = entry.getKey();
            ByteBuffer value = entry.getValue();
            try {
                if (value != null) {
                    int size = value.remaining();
                    int bytesOffset = entryLocationsToUse.get(key).getOffset() * this.sectorSize;
                    tempBuffers.clear();
                    tempBuffers.add(((ByteBuffer) lengthPrefixBuffer.clear()).putInt(0, size));
                    tempBuffers.add(value);
                    if ((lengthPrefixBuffer.capacity() + value.remaining()) % this.sectorSize != 0) {
                        zeroes(this.sectorSize - (lengthPrefixBuffer.capacity() + value.remaining()) % this.sectorSize, tempBuffers);
                    }
                    assert tempBuffers.stream().mapToInt(ByteBuffer::remaining).sum() == entryLocationsToUse.get(key).getSize() * this.sectorSize;
                    Utils.writeFully(this.file.position(bytesOffset), tempBuffers.toArray(new ByteBuffer[0]));
                    shouldFlush = true;
                }
            } catch (UnsupportedDataException e) {
                exceptions.add(new UnsupportedDataException.WithKey(e, key));
            }
        }
		return shouldFlush;
	}

	private void doPendingHeaderUpdatesPass(List<K> pendingRemovals, Map<K, RegionEntryLocation> pendingPrevLocations) throws IOException {
		if (pendingRemovals.isEmpty() && pendingPrevLocations.isEmpty()) { return; }
		for (K key : pendingRemovals) {
			this.sectorTracker.removeKey(key);
			this.updateHeaders(key);
		}
		for (Map.Entry<K, RegionEntryLocation> entry : pendingPrevLocations.entrySet()) {
			this.sectorTracker.updateUsedSectorsFor(entry.getValue(), null);
			this.updateHeaders(entry.getKey());
		}
		this.file.force(true);
	}

	private void updateHeaders(K key) throws IOException {
		int entryByteCount = headerEntryProvider.getEntryByteCount();
		ByteBuffer buf = ByteBuffer.allocate(entryByteCount);
		headerEntryProvider.apply(key).write(buf);
		buf.flip();
		Utils.writeFully(file.position((long) key.getId() * entryByteCount), buf);
	}

	@Override public Optional<ByteBuffer> readValue(K key) throws IOException {
		Lock sectorLock = reserveSectorsLock.readLock();
		Lock mainLock = dataLock.readLock();
		boolean mainLocked = false;
		try {
			sectorLock.lock();
			Optional<RegionEntryLocation> entryLocation = sectorTracker.getEntryLocation(key);
			if (!entryLocation.isPresent()) { return Optional.empty(); }
			mainLock.lock();
			mainLocked = true;
			return doReadKey(key);
		} catch (UncheckedIOException e) {
			throw e.getCause();
		} finally {
			sectorLock.unlock();
			if (mainLocked) { mainLock.unlock(); }
		}
	}

	private Optional<ByteBuffer> doReadKey(K key) {
		try {
			Optional<RegionEntryLocation> entryLocation = sectorTracker.getEntryLocation(key);
			if (!entryLocation.isPresent()) { return Optional.empty(); }
			RegionEntryLocation loc = entryLocation.get();
			int sectorOffset = loc.getOffset();
			int sectorCount = loc.getSize();
			ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
			long position = (long) sectorOffset * sectorSize;
			readFully(file, buf, position);
			int dataLength = buf.getInt(0);
			if (dataLength > sectorCount * sectorSize) {
				throw new ICorruptedDataException(
						"Expected data size max " + sectorCount * sectorSize + " but found " + dataLength);
			}
			ByteBuffer bytes = ByteBuffer.allocate(dataLength);
			readFully(file, bytes, position + Integer.BYTES);
			bytes.flip();
			return Optional.of(bytes);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override public boolean hasValue(K key) {
		reserveSectorsLock.readLock().lock();
		try {
			return sectorTracker.getEntryLocation(key).isPresent();
		} finally {
			reserveSectorsLock.readLock().unlock();
		}
	}

	private int getSectorNumber(int bytes) { return ceilDiv(bytes, sectorSize); }

	@Override public void flush() throws IOException {
		reserveSectorsLock.writeLock().lock();
		dataLock.writeLock().lock();
		try {
			boolean fileLengthChanged = false;
			fileLengthChanged |= this.ensureSectorSizeAligned();
			fileLengthChanged |= this.erasePendingSectors();
			this.file.force(fileLengthChanged);
		} finally {
			dataLock.writeLock().unlock();
			reserveSectorsLock.writeLock().unlock();
		}
	}

	@Override public void close() throws IOException {
		reserveSectorsLock.writeLock().lock();
		dataLock.writeLock().lock();
		try {
			this.ensureSectorSizeAligned();
			this.erasePendingSectors();
		} finally {
			try {
				this.file.close();
			} finally {
				dataLock.writeLock().unlock();
				reserveSectorsLock.writeLock().unlock();
			}
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

	public static void readFully(FileChannel src, ByteBuffer data, long position) throws IOException {
		while (data.hasRemaining()) { src.read(data, position); }
	}

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
			int entryMapBytes = Integer.BYTES;
			int entryMapSectors = ceilDiv(keyProvider.getKeyCount(regionKey) * entryMapBytes, sectorSize);
			IntPackedSectorMap<K> sectorMap = IntPackedSectorMap.readOrCreate(file, keyProvider.getKeyCount(regionKey));
			SectorTracker<K> regionSectorTracker = SectorTracker.fromFile(file, sectorMap, entryMapSectors, sectorSize);
			return new ShadowPagingRegion<>(file, regionSectorTracker, sectorMap.headerEntryProvider(), this.regionKey, this.sectorSize);
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

		public void removeKey(K key) throws IOException {
			Optional<RegionEntryLocation> existing = sectorMap.getEntryLocation(key);
			RegionEntryLocation loc = new RegionEntryLocation(0, 0);
			this.sectorMap.setOffsetAndSize(key, loc);
			this.updateUsedSectorsFor(existing.orElse(null), loc);
		}

		public RegionEntryLocation reserveForKey(K key, int requestedSize) throws IOException {
			RegionEntryLocation found = findFree(requestedSize);
			this.sectorMap.setOffsetAndSize(key, found);
			this.updateUsedSectorsFor(null, found);
			return found;
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
			assert this.sectorsPendingErasure.get(range.getOffset()) && this.sectorsPendingErasure.nextClearBit(range.getOffset()) == range.getSize() + range.getOffset()
					: "the given range isn't pending erasure";
			this.sectorsPendingErasure.clear(range.getOffset(), range.getOffset() + range.getSize());
		}

		public int getSectorsLength() { return this.usedSectors.length(); }

		public static <L extends IKey> SectorTracker<L> fromFile(
				SeekableByteChannel file, IKeyIdToSectorMap<L> sectorMap, int reservedSectors, int sectorSize) throws IOException {
			BitSet usedSectors = new BitSet(Math.max((int) (file.size()/sectorSize), reservedSectors));
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
