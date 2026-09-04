package mctmods.resourcedatapackloader.content.rubic.regionlib.lib;


public class RegionEntryLocation {
	private final int offset;
	private final int size;

	public RegionEntryLocation(int offset, int size) {
		this.offset = offset;
		this.size = size;
	}

	public int getOffset() { return offset; }

	public int getSize() { return size; }

	@Override public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		RegionEntryLocation that = (RegionEntryLocation) o;
		if (offset != that.offset) { return false; }
		return size == that.size;
	}

	@Override public int hashCode() {
		int result = offset;
		result = 31*result + size;
		return result;
	}
}
