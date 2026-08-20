package mctmods.resourcedatapackloader.util.interfaces;


public interface IBucketSorterEntry {
    default long getSorterStorage(int id) {
        long[] containerData = getBucketEntryData();
        if (containerData == null || id >= containerData.length) { return 0; }
        return containerData[id];
    }

    default void setSorterStorage(int id, long value) {
        long[] containerData = getBucketEntryData();
        if (containerData == null || id >= containerData.length) {
            long[] newData = new long[id + 1];
            if (containerData != null) { System.arraycopy(containerData, 0, newData, 0, containerData.length); }
            this.setBucketEntryData(containerData = newData);
        }
        containerData[id] = value;
    }

    long[] getBucketEntryData();

    void setBucketEntryData(long[] data);
}
