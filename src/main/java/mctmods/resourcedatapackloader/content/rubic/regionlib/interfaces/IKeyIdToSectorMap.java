package mctmods.resourcedatapackloader.content.rubic.regionlib.interfaces;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IHeaderDataEntry;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IHeaderDataEntryProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.RegionEntryLocation;

import java.io.IOException;
import java.util.Optional;

public interface IKeyIdToSectorMap<
	H extends IHeaderDataEntry,
	P extends IHeaderDataEntryProvider<H, K>,
	K extends IKey> extends Iterable<RegionEntryLocation> {
	default Optional<RegionEntryLocation> getEntryLocation(K key) { return getEntryLocation(key.getId()); }
	Optional<RegionEntryLocation> getEntryLocation(int id);
	void setOffsetAndSize(K key, RegionEntryLocation location) throws IOException;
	P headerEntryProvider();
}
