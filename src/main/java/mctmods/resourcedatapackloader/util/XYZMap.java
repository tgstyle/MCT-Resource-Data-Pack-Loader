package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.util.interfaces.IXYZAddressable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class XYZMap<T extends IXYZAddressable> implements Iterable<T> {
    private static final int HASH_SEED = 1183822147;
    @Nonnull private IXYZAddressable[] bucketsByPointer;
    @Nonnull private IXYZAddressable[] bucketsByHash;
    @Nonnull private int[] pointers;
    private int size = 0;
    private final float loadFactor;
    private int loadThreshold;
    private int mask;
    private final Thread debugStartThreadRef = Thread.currentThread();

    public XYZMap(float loadFactor, int capacity) {
        if (loadFactor > 1.0) { throw new IllegalArgumentException("You really dont want to be using a " + loadFactor + " load loadFactor with this hash table!"); }
        this.loadFactor = loadFactor;
        int tCapacity = 1;
        while (tCapacity < capacity) { tCapacity <<= 1; }
        this.bucketsByPointer = new IXYZAddressable[tCapacity];
        this.bucketsByHash = new IXYZAddressable[tCapacity];
        this.pointers = new int[tCapacity];
        this.refreshFields();
    }

    public int getSize() { return this.size; }

    private static int hash(int x, int y, int z) {
        int hash = HASH_SEED;
        hash += x;
        hash *= HASH_SEED;
        hash += y;
        hash *= HASH_SEED;
        hash += z;
        hash *= HASH_SEED;
        return hash;
    }

    private int getPointerIndex(int x, int y, int z) { return hash(x, y, z) & this.mask; }

    private int getNextPointerIndex(int pointerIndex) { return ++pointerIndex & this.mask; }


    @SuppressWarnings("unchecked") @Nullable public T put(T value) {
        checkThreadedWrite();
        int x = value.getX();
        int y = value.getY();
        int z = value.getZ();
        int pointerIndex = this.getPointerIndex(x, y, z);
        int index = pointers[pointerIndex];
        while (index != 0) {
            IXYZAddressable bucket = this.bucketsByPointer[index];
            if (bucket.getX() == x && bucket.getY() == y && bucket.getZ() == z) {
                this.bucketsByPointer[index] = value;
                this.bucketsByHash[pointerIndex] = value;
                return (T) bucket;
            }
            pointerIndex = this.getNextPointerIndex(pointerIndex);
            index = pointers[pointerIndex];
        }
        this.bucketsByPointer[++size] = value;
        this.bucketsByHash[pointerIndex] = value;
        this.pointers[pointerIndex] = size;
        if (this.size > this.loadThreshold)
            grow();
        return null;
    }

    @SuppressWarnings("unchecked") @Nullable public T remove(int x, int y, int z) {
        checkThreadedWrite();
        int pointerIndex = this.getPointerIndex(x, y, z);
        int index = pointers[pointerIndex];
        while (index != 0) {
            IXYZAddressable bucket = this.bucketsByPointer[index];
            if (bucket.getX() == x && bucket.getY() == y && bucket.getZ() == z) {
                this.collapseBucket(pointerIndex, index);
                return (T) bucket;
            }
            pointerIndex = this.getNextPointerIndex(pointerIndex);
            index = pointers[pointerIndex];
        }
        return null;
    }

    @Nullable public T remove(T value) { return this.remove(value.getX(), value.getY(), value.getZ()); }

    @SuppressWarnings("unchecked") @Nullable public T get(int x, int y, int z) {
        IXYZAddressable[] buckets = this.bucketsByHash;
        int slots = this.mask;
        int index = hash(x, y, z) & slots;
        for (IXYZAddressable bucket = buckets[index]; bucket != null; bucket = buckets[index]) {
            if (bucket.getX() == x && bucket.getY() == y && bucket.getZ() == z) { return (T) bucket; }
            index = ++index & slots;
        }
        return null;
    }


    private void grow() {
        int newLength = this.bucketsByPointer.length * 2;
        int newMask = newLength - 1;
        IXYZAddressable[] newBucketsByPointer = new IXYZAddressable[newLength];
        IXYZAddressable[] newBucketsByHash = new IXYZAddressable[newLength];
        int[] newPointers = new int[newLength];
        for (int i = 1; i <= size; i++) {
            IXYZAddressable bucket = bucketsByPointer[i];
            newBucketsByPointer[i] = bucket;
            int pointerIndex = hash(bucket.getX(), bucket.getY(), bucket.getZ()) & newMask;
            while (newPointers[pointerIndex] != 0)
                pointerIndex = ++pointerIndex & newMask;
            newPointers[pointerIndex] = i;
            newBucketsByHash[pointerIndex] = bucket;
        }
        bucketsByPointer = newBucketsByPointer;
        bucketsByHash = newBucketsByHash;
        pointers = newPointers;
        mask=newMask;
        loadThreshold = (int) (newLength * this.loadFactor) - 2;
    }

    private void collapseBucket(final int holePointerIndex, final int holeIndex) {
        final int lastElement = size;
        final int oldLastPointerIndex = getElementPointerIndex(lastElement);
        List<IXYZAddressable> nextPointersBuckets = new ArrayList<>(10);
        List<Integer> nextBucketIndexes = new ArrayList<>(10);
        this.pointers[oldLastPointerIndex] = holeIndex;
        this.pointers[holePointerIndex] = 0;
        this.bucketsByPointer[holeIndex] = this.bucketsByPointer[lastElement];
        this.bucketsByPointer[lastElement] = null;
        this.bucketsByHash[holePointerIndex] = null;
        this.size--;
        int pointerIndex = this.getNextPointerIndex(holePointerIndex);
        int index = pointers[pointerIndex];
        while (index != 0) {
            IXYZAddressable bucket = this.bucketsByPointer[index];
            nextPointersBuckets.add(bucket);
            nextBucketIndexes.add(index);
            this.pointers[pointerIndex] = 0;
            this.bucketsByHash[pointerIndex] = null;
            pointerIndex = this.getNextPointerIndex(pointerIndex);
            index = pointers[pointerIndex];
        }
        for (int i = 0; i < nextPointersBuckets.size(); i++) {
            IXYZAddressable bucket = nextPointersBuckets.get(i);
            int x = bucket.getX();
            int y = bucket.getY();
            int z = bucket.getZ();
            int newBucketPointerIndex = this.getPointerIndex(x, y, z);
            int newIndex = pointers[newBucketPointerIndex];
            while (newIndex != 0) {
                newBucketPointerIndex = this.getNextPointerIndex(newBucketPointerIndex);
                newIndex = pointers[newBucketPointerIndex];
            }
            this.pointers[newBucketPointerIndex] = nextBucketIndexes.get(i);
            this.bucketsByHash[newBucketPointerIndex] = bucket;
        }
    }

    private int getElementPointerIndex(int index) {
        IXYZAddressable lastElement = this.bucketsByPointer[index];
        int pointerIndex = this.getPointerIndex(lastElement.getX(), lastElement.getY(), lastElement.getZ());
        while (pointers[pointerIndex] != index) { pointerIndex = this.getNextPointerIndex(pointerIndex); }
        return pointerIndex;
    }

    private void refreshFields() {
        this.loadThreshold = (int) (this.bucketsByPointer.length * this.loadFactor) - 2;
        this.mask = this.bucketsByPointer.length - 1;
    }

    private void checkThreadedWrite() {
        if (ContentLog.LOGGER.debugEnabled() && Thread.currentThread() != debugStartThreadRef) {
            ContentLog.LOGGER.error("A cube map was written to from {}, which is not the thread that made it", Thread.currentThread().getName());
        }
    }

    @Nonnull public Iterator<T> iterator() {
        return new Iterator<T>() {
            int at = 1;
            @Override public boolean hasNext() { return at <= size; }
            @SuppressWarnings("unchecked") @Override @Nullable public T next() { return (T) bucketsByPointer[at++]; }
            @Override public void remove() {
                checkThreadedWrite();
                int pointerIndex = getElementPointerIndex(--at);
                collapseBucket(pointerIndex, at);
            }
        };
    }
}
