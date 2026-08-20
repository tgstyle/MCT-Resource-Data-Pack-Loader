package mctmods.resourcedatapackloader.content.rubic.regionlib.lib.header;

import mctmods.resourcedatapackloader.content.rubic.regionlib.UnsupportedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.IKeyIdToSectorMap;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.RegionEntryLocation;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Iterator;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class IntPackedSectorMap<K extends IKey>
	implements IKeyIdToSectorMap<IntHeaderEntry, EntryLocationHeaderEntryProvider<K>, K> {
	private static final int SIZE_BITS = 8;
	private static final int OFFSET_BITS = Integer.SIZE - SIZE_BITS;
	private static final int SIZE_MASK = (1 << SIZE_BITS) - 1;
	private static final int MAX_SIZE = SIZE_MASK;
	private static final int OFFSET_MASK = (1 << OFFSET_BITS) - 1;
	private static final int MAX_OFFSET = OFFSET_MASK;
	private final int[] entrySectorOffsets;

	public IntPackedSectorMap(int[] data) { this.entrySectorOffsets = data; }

	@Override public Optional<RegionEntryLocation> getEntryLocation(int id) {
		int packed = entrySectorOffsets[id];
		if (packed == 0) { return Optional.empty(); }
		return Optional.of(new RegionEntryLocation(unpackOffset(packed), unpackSize(packed)));
	}

	@Override public void setOffsetAndSize(K key, RegionEntryLocation location) throws IOException {
		if (location.getSize() > MAX_SIZE) { throw new UnsupportedDataException("Max supported size " + MAX_SIZE + " but requested " + location.getSize()); }
		if (location.getOffset() > MAX_OFFSET) { throw new UnsupportedDataException("Max supported offset " + MAX_OFFSET + " but requested " + location.getOffset()); }
		entrySectorOffsets[key.getId()] = packed(location);
	}

	@Override @NotNull public Iterator<RegionEntryLocation> iterator() {
		int first = 0;
		while (first < entrySectorOffsets.length && entrySectorOffsets[first] == 0) { first++; }
		int firstIdx = first;
		return new Iterator<RegionEntryLocation>() {
			int idx = firstIdx;
			@Override public boolean hasNext() { return idx < entrySectorOffsets.length; }
			@Override public RegionEntryLocation next() {
				int packed = entrySectorOffsets[idx];
				RegionEntryLocation loc = new RegionEntryLocation(unpackOffset(packed), unpackSize(packed));
				do {
					idx++;
				} while (idx < entrySectorOffsets.length && entrySectorOffsets[idx] == 0);
				return loc;
			}
		};
	}

	@Override public EntryLocationHeaderEntryProvider<K> headerEntryProvider() { return new EntryLocationHeaderEntryProvider<>(this, IntPackedSectorMap::packed); }

	private static int unpackOffset(int sectorLocation) { return sectorLocation >>> SIZE_BITS; }

	private static int unpackSize(int sectorLocation) { return sectorLocation & SIZE_MASK; }

	private static int packed(RegionEntryLocation location) {
		if ((location.getSize() & SIZE_MASK) != location.getSize()) {
			throw new IllegalArgumentException("Supported entry size range is 0 to " + MAX_SIZE + ", but got " + location.getSize());
		}
		if ((location.getOffset() & OFFSET_MASK) != location.getOffset()) {
			throw new IllegalArgumentException("Supported entry offset range is 0 to " + MAX_OFFSET + ", but got " + location.getOffset());
		}
		return location.getSize() | (location.getOffset() << SIZE_BITS);
	}

	public static <K extends IKey> IntPackedSectorMap<K> readOrCreate(
			SeekableByteChannel file, int entriesPerRegion) throws IOException {
		int entryMappingBytes = Integer.BYTES*entriesPerRegion;
		if (file.size() < entryMappingBytes) {
			file.position(0);
			Utils.writeFully(file, ByteBuffer.allocate(entryMappingBytes));
		}
		file.position(0);
		int[] entrySectorOffsets = new int[entriesPerRegion];
		ByteBuffer buffer = ByteBuffer.allocate(entryMappingBytes);
		Utils.readFully(file, buffer);
		buffer.flip();
		buffer.asIntBuffer().get(entrySectorOffsets);
		return new IntPackedSectorMap<>(entrySectorOffsets);
	}
}
