package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.util.interfaces.IXZAddressable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

public class XZMap<T extends IXZAddressable> extends AbstractAddressMap implements Iterable<T> {
    private IXZAddressable[] buckets;

    public XZMap(float loadFactor, int capacity) {
        super(loadFactor, capacity);
        this.buckets = new IXZAddressable[this.tableLength];
        this.refreshFields();
    }

    private static int hash(int x, int z) {
        int hash = HASH_SEED;
        hash += x;
        hash *= HASH_SEED;
        hash += z;
        hash *= HASH_SEED;
        return hash;
    }

    private int getIndex(int x, int z) { return hash(x, z) & this.mask; }

    public void put(T value) {
        int x = value.getX();
        int z = value.getZ();
        int index = getIndex(x, z);
        IXZAddressable bucket = this.buckets[index];
        while (bucket != null) {
            if (bucket.getX() == x && bucket.getZ() == z) {
                this.buckets[index] = value;
                return;
            }
            index = getNextIndex(index);
            bucket = this.buckets[index];
        }
        this.buckets[index] = value;
        ++this.size;
        if (this.size > this.loadThreshold) { grow(); }
    }

    public void remove(int x, int z) {
        int index = getIndex(x, z);
        IXZAddressable bucket = this.buckets[index];
        while (bucket != null) {
            if (bucket.getX() == x && bucket.getZ() == z) {
                this.collapseBucket(index);
                return;
            }
            index = getNextIndex(index);
            bucket = this.buckets[index];
        }
    }

    public void remove(T value) { this.remove(value.getX(), value.getZ()); }

    @SuppressWarnings("unchecked") @Nullable public T get(int x, int z) {
        IXZAddressable[] slots = this.buckets;
        int wrap = slots.length - 1;
        int index = hash(x, z) & wrap;
        for (IXZAddressable bucket = slots[index]; bucket != null; bucket = slots[index]) {
            if (bucket.getX() == x && bucket.getZ() == z) { return (T) bucket; }
            index = ++index & wrap;
        }
        return null;
    }

    private void grow() {
        IXZAddressable[] oldBuckets = this.buckets;
        this.buckets = new IXZAddressable[this.buckets.length * 2];
        this.refreshFields();
        for (IXZAddressable oldBucket : oldBuckets) {
            if (oldBucket == null) { continue; }
            int index = getIndex(oldBucket.getX(), oldBucket.getZ());
            IXZAddressable bucket = this.buckets[index];
            while (bucket != null) { bucket = this.buckets[index = getNextIndex(index)]; }
            this.buckets[index] = oldBucket;
        }
    }

    private void collapseBucket(int hole) {
        assert this.buckets[hole] != null;
        --this.size;
        int currentIndex = hole;
        while (true) {
            currentIndex = getNextIndex(currentIndex);
            IXZAddressable bucket = this.buckets[currentIndex];
            if (bucket == null) {
                this.buckets[hole] = null;
                return;
            }
            int targetIndex = getIndex(bucket.getX(), bucket.getZ());
            if (hole < currentIndex) {
                if (targetIndex <= hole || currentIndex < targetIndex) {
                    this.buckets[hole] = bucket;
                    hole = currentIndex;
                }
            }
            else {
                if (hole >= targetIndex && targetIndex > currentIndex) {
                    this.buckets[hole] = bucket;
                    hole = currentIndex;
                }
            }
        }
    }

    private void refreshFields() {
        this.loadThreshold = Math.min(this.buckets.length - 1, (int) (this.buckets.length * this.loadFactor));
        this.mask = this.buckets.length - 1;
    }

    @Nonnull public Iterator<T> iterator() {
        return new Iterator<T>() {
            int at = -1;
            int next = -1;
            @Override public boolean hasNext() {
                if (next > at) { return true; }
                for (next++; next < buckets.length; next++) {
                    if (buckets[next] != null) { return true; }
                }
                return false;
            }
            @SuppressWarnings("unchecked") @Nullable @Override public T next() {
                if (next > at) {
                    at = next;
                    return (T) buckets[at];
                }
                for (next++; next < buckets.length; next++) {
                    if (buckets[next] != null) {
                        at = next;
                        return (T) buckets[at];
                    }
                }
                return null;
            }
            @Override public void remove() {
                collapseBucket(at);
                next = at = at - 1;
            }
        };
    }
}
