package mctmods.resourcedatapackloader.content.rubic.regionlib.impl;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;


public class EntryLocation2D implements IKey {
	public static final int LOC_BITS = 5;
	public static final int LOC_BITMASK = (1 << LOC_BITS) - 1;
	public static final int ENTRIES_PER_REGION = (1 << LOC_BITS)*(1 << LOC_BITS);
	private final int entryX;
	private final int entryZ;

	public EntryLocation2D(int entryX, int entryZ) {
		this.entryX = entryX;
		this.entryZ = entryZ;
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EntryLocation2D that = (EntryLocation2D) o;
		if (entryX != that.entryX) return false;
		return entryZ == that.entryZ;
	}

	@Override public int hashCode() {
		int result = entryX;
		result = 31*result + entryZ;
		return result;
	}

	@Override public RegionKey getRegionKey() {
		int regX = entryX >> LOC_BITS;
		int regZ = entryZ >> LOC_BITS;
		return new RegionKey(regX + "." + regZ + ".2dr");
	}

	@Override public int getId() { return ((entryX & LOC_BITMASK) << LOC_BITS) | (entryZ & LOC_BITMASK); }

	@Override public String toString() {
		return "EntryLocation2D{" +
			"entryX=" + entryX +
			", entryZ=" + entryZ +
			'}';
	}

	public static class Provider implements IKeyProvider {
		@Override public int getKeyCount(RegionKey key) { return EntryLocation2D.ENTRIES_PER_REGION; }

		@Override public boolean isValid(RegionKey key) { return key.getName().matches("-?\\d+\\.-?\\d+\\.2dr"); }
	}
}
