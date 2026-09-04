package mctmods.resourcedatapackloader.content.rubic.regionlib.lib.header;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IHeaderDataEntryProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces.IKeyIdToSectorMap;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.RegionEntryLocation;

import java.util.function.ToIntFunction;

public class EntryLocationHeaderEntryProvider<K extends IKey>
	implements IHeaderDataEntryProvider<IntHeaderEntry, K> {
	private final IKeyIdToSectorMap<K> sectorMap;
	private final ToIntFunction<RegionEntryLocation> pack;

	public EntryLocationHeaderEntryProvider(
		IKeyIdToSectorMap<K> sectorMap, ToIntFunction<RegionEntryLocation> pack) {
		this.sectorMap = sectorMap;
		this.pack = pack;
	}

	@Override public int getEntryByteCount() { return Integer.BYTES; }

	@Override public IntHeaderEntry apply(K key) {
		return new IntHeaderEntry(
			sectorMap.getEntryLocation(key)
				.map(pack::applyAsInt)
				.orElse(0)
		);
	}
}
