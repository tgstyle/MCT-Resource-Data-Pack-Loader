package mctmods.resourcedatapackloader.core.util.address;

abstract class AbstractAddressMap {
    static final int HASH_SEED = 1183822147;
    final float loadFactor;
    final int tableLength;
    int size;
    int loadThreshold;
    int mask;

    AbstractAddressMap(float loadFactor, int capacity) {
        if (loadFactor > 1.0) { throw new IllegalArgumentException("You really dont want to be using a " + loadFactor + " load loadFactor with this hash table!"); }
        this.loadFactor = loadFactor;
        int tCapacity = 1;
        while (tCapacity < capacity) { tCapacity <<= 1; }
        this.tableLength = tCapacity;
    }

    static int next(int index, int mask) { return (index + 1) & mask; }

    final int getNextIndex(int index) { return next(index, this.mask); }
}
