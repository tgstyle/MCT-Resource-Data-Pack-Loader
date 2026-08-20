package mctmods.resourcedatapackloader.content.rubic.regionlib.lib;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.IKeyIdToSectorMap;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.Utils;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.util.BitSet;
import java.util.Optional;

public class RegionSectorTracker<K extends IKey> {
	private final BitSet usedSectors;
	private final IKeyIdToSectorMap<?, ?, K> sectorMap;

	public RegionSectorTracker(BitSet usedSectors, IKeyIdToSectorMap<?, ?, K> sectorMap) {
		this.usedSectors = usedSectors;
		this.sectorMap = sectorMap;
	}

	public void removeKey(K key) throws IOException {
		Optional<RegionEntryLocation> existing = sectorMap.getEntryLocation(key);
		RegionEntryLocation loc = new RegionEntryLocation(0, 0);
		this.sectorMap.setOffsetAndSize(key, loc);
		this.updateUsedSectorsFor(existing.orElse(null), loc);
	}

	public RegionEntryLocation reserveForKey(K key, int requestedSize) throws IOException {
		Optional<RegionEntryLocation> existing = sectorMap.getEntryLocation(key);
		RegionEntryLocation found = findSectorFor(existing.orElse(null), requestedSize);
		this.sectorMap.setOffsetAndSize(key, found);
		this.updateUsedSectorsFor(existing.orElse(null), found);
		return found;
	}

	private RegionEntryLocation findSectorFor(RegionEntryLocation oldSector, int requestedSize) {
		if (oldSector == null) { return findNextFree(requestedSize); }
		if (requestedSize <= oldSector.getSize()) { return oldSector.withSize(requestedSize); }
		for (int i = oldSector.getOffset() + oldSector.getSize(); i < oldSector.getOffset() + requestedSize; i++) {
			if (!isSectorFree(i)) { return findNextFree(requestedSize); }
		}
		return oldSector.withSize(requestedSize);
	}

	private RegionEntryLocation findNextFree(int requestedSize) { return Utils.findFreeSectors(usedSectors, requestedSize); }

	private void updateUsedSectorsFor(RegionEntryLocation oldSectorLocation, RegionEntryLocation newSectorLocation) {
		if (oldSectorLocation != null) {
			int oldOffset = oldSectorLocation.getOffset();
			int oldSize = oldSectorLocation.getSize();
			for (int i = 0; i < oldSize; i++) { usedSectors.set(oldOffset + i, false); }
		}
		if (newSectorLocation != null) {
			int newOffset = newSectorLocation.getOffset();
			int newSize = newSectorLocation.getSize();
			for (int i = 0; i < newSize; i++) { usedSectors.set(newOffset + i, true); }
		}
	}

	private boolean isSectorFree(int sector) { return !usedSectors.get(sector); }

	public static <L extends IKey> RegionSectorTracker<L> fromFile(
			SeekableByteChannel file, IKeyIdToSectorMap<?, ?, L> sectorMap, int reservedSectors, int sectorSize) throws IOException {
		BitSet usedSectors = new BitSet(Math.max((int) (file.size()/sectorSize), reservedSectors));
		for (int i = 0; i < reservedSectors; i++) { usedSectors.set(i, true); }
		for (RegionEntryLocation loc : sectorMap) {
			int offset = loc.getOffset();
			int size = loc.getSize();
			for (int i = 0; i < size; i++) { usedSectors.set(offset + i); }
		}
		return new RegionSectorTracker<>(usedSectors, sectorMap);
	}
}
