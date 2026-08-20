package mctmods.resourcedatapackloader.content.rubic.regionlib.lib;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IHeaderDataEntryProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.IKeyIdToSectorMap;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.header.IntPackedSectorMap;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.ICorruptedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.Utils;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Region<K extends IKey> implements IRegion<K> {
	private final IKeyIdToSectorMap<?, ?, K> sectorMap;
	private final RegionSectorTracker<K> regionSectorTracker;
	private final FileChannel file;
	private final List<IHeaderDataEntryProvider<?, K>> headerEntryProviders;
	private final int sectorSize;
    private final int keyCount;

	private Region(FileChannel file,
	               IntPackedSectorMap<K> sectorMap,
	               RegionSectorTracker<K> sectorTracker,
	               List<IHeaderDataEntryProvider<?, K>> headerEntryProviders,
	               RegionKey regionKey,
	               IKeyProvider keyProvider,
	               int sectorSize) {
        this.keyCount = keyProvider.getKeyCount(regionKey);
		this.file = file;
		this.headerEntryProviders = headerEntryProviders;
		this.sectorSize = sectorSize;
		this.sectorMap = sectorMap;
		this.regionSectorTracker = sectorTracker;
	}

	@Override public synchronized void writeValue(K key, ByteBuffer value) throws IOException {
		if (value == null) {
			this.regionSectorTracker.removeKey(key);
			updateHeaders(key);
			return;
		}
		int size = value.remaining();
		int sizeWithSizeInfo = size + Integer.BYTES;
		int numSectors = getSectorNumber(sizeWithSizeInfo);
		RegionEntryLocation location = this.regionSectorTracker.reserveForKey(key, numSectors);
		int bytesOffset = location.getOffset()*sectorSize;
		Utils.writeFully(file.position(bytesOffset), ByteBuffer.allocate(Integer.BYTES).putInt(0, size));
		Utils.writeFully(file, value);
		updateHeaders(key);
	}

	private void updateHeaders(K key) throws IOException {
		int id = key.getId();
		int currentHeaderBytes = 0;
		for (IHeaderDataEntryProvider<?, K> prov : headerEntryProviders) {
			ByteBuffer buf = ByteBuffer.allocate(prov.getEntryByteCount());
			prov.apply(key).write(buf);
			buf.flip();
			Utils.writeFully(file.position((long) currentHeaderBytes * keyCount + (long) id * prov.getEntryByteCount()), buf);
			currentHeaderBytes += prov.getEntryByteCount();
		}
	}

	@Override public synchronized Optional<ByteBuffer> readValue(K key) throws IOException {
		try {
			return doReadKey(key);
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	private Optional<ByteBuffer> doReadKey(K key) {
		return sectorMap.getEntryLocation(key).flatMap(loc -> {
			try {
				int sectorOffset = loc.getOffset();
				int sectorCount = loc.getSize();
				ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
				Utils.readFully(file.position((long) sectorOffset * sectorSize), buf);
				int dataLength = buf.getInt(0);
				if (dataLength > sectorCount * sectorSize) {
					throw new ICorruptedDataException(
							"Expected data size max" + sectorCount * sectorSize + " but found " + dataLength);
				}
				ByteBuffer bytes = ByteBuffer.allocate(dataLength);
				Utils.readFully(file, bytes);
				bytes.flip();
				return Optional.of(bytes);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	@Override public synchronized boolean hasValue(K key) { return sectorMap.getEntryLocation(key).isPresent(); }

    private int getSectorNumber(int bytes) { return ceilDiv(bytes, sectorSize); }

	@Override public void flush() throws IOException {
		this.ensureSectorSizeAligned();
		this.file.force(false);
	}

	@Override public void close() throws IOException {
		this.flush();
		this.file.close();
	}

	private void ensureSectorSizeAligned() throws IOException {
		if (file.size() % sectorSize != 0) {
			int extra = (int) (sectorSize - (file.size() % sectorSize));
			ByteBuffer buffer = ByteBuffer.allocateDirect(extra);
			this.file.position(this.file.size());
			Utils.writeFully(this.file, buffer);
			assert this.file.size() % sectorSize == 0;
		}
	}

	private static int ceilDiv(int x, int y) { return -Math.floorDiv(-x, y); }

	public static <L extends IKey> Builder<L> builder() { return new Builder<>(); }

	public static class Builder<K extends IKey> {
		private Path directory;
		private int sectorSize = 512;
		private final List<IHeaderDataEntryProvider<?, K>> headerEntryProviders = new ArrayList<>();
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

		public Region<K> build() throws IOException {
			FileChannel file = FileChannel.open(directory.resolve(regionKey.getName()), CREATE, READ, WRITE);
			int entryMapBytes = Integer.BYTES;
			for (IHeaderDataEntryProvider<?, ?> prov : headerEntryProviders) { entryMapBytes += prov.getEntryByteCount(); }
			int entryMapSectors = ceilDiv(keyProvider.getKeyCount(regionKey) * entryMapBytes, sectorSize);
			IntPackedSectorMap<K> sectorMap = IntPackedSectorMap.readOrCreate(file, keyProvider.getKeyCount(regionKey));
			RegionSectorTracker<K> regionSectorTracker = RegionSectorTracker.fromFile(file, sectorMap, entryMapSectors, sectorSize);
			this.headerEntryProviders.add(0, sectorMap.headerEntryProvider());
			return new Region<>(file, sectorMap, regionSectorTracker,
					this.headerEntryProviders, this.regionKey, keyProvider, this.sectorSize);
		}
	}
}
