package mctmods.resourcedatapackloader.content.rubic.regionlib.impl;

import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.key.RegionKey;


public class EntryLocation3D implements IKey {
	private static final int LOC_BITS = 4;
	private static final int LOC_BITMASK = (1 << LOC_BITS) - 1;
	public static final int ENTRIES_PER_REGION = (1 << LOC_BITS)*(1 << LOC_BITS)*(1 << LOC_BITS);
	private final int entryX;
	private final int entryY;
	private final int entryZ;

	public EntryLocation3D(int entryX, int entryY, int entryZ) {
		this.entryX = entryX;
		this.entryY = entryY;
		this.entryZ = entryZ;
	}

	@Override public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EntryLocation3D that = (EntryLocation3D) o;
		if (entryX != that.entryX) return false;
		if (entryY != that.entryY) return false;
		return entryZ == that.entryZ;
	}

	@Override public int hashCode() {
		int result = entryX;
		result = 31*result + entryY;
		result = 31*result + entryZ;
		return result;
	}

	@Override public RegionKey getRegionKey() {
		int regX = entryX >> LOC_BITS;
		int regY = entryY >> LOC_BITS;
		int regZ = entryZ >> LOC_BITS;
		return new RegionKey(regX + "." + regY + "." + regZ + ".3dr");
	}

	@Override public int getId() { return ((entryX & LOC_BITMASK) << LOC_BITS*2) | ((entryY & LOC_BITMASK) << LOC_BITS) | (entryZ & LOC_BITMASK); }

	@Override public String toString() {
		return "EntryLocation3D{" +
			"entryX=" + entryX +
			", entryY=" + entryY +
			", entryZ=" + entryZ +
			'}';
	}

	public static class Provider implements IKeyProvider {
		@Override public int getKeyCount(RegionKey key) { return ENTRIES_PER_REGION; }

		@Override public boolean isValid(RegionKey key) { return key.getName().matches("-?\\d+\\.-?\\d+\\.-?\\d+\\.3dr"); }
	}
}
