package mctmods.resourcedatapackloader.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mctmods.resourcedatapackloader.util.interfaces.IBucketSorterEntry;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

@SuppressWarnings({"unchecked"}) public abstract class WatchersSortingList<T extends IBucketSorterEntry> implements Iterable<T> {
    protected final int bucketCount;
    protected final ObjectArrayList<T>[] buckets;
    protected final int[] bucketSizes;
    protected final int intrusiveCollectionId;
    protected final Supplier<Collection<EntityPlayer>> playersSupplier;
    protected long[] playerPositions = new long[0];
    private int distributingBucket = 0;

    protected WatchersSortingList(int bucketCount, int intrusiveCollectionId, Supplier<Collection<EntityPlayer>> playersSupplier) {
        this.bucketCount = bucketCount;
        this.buckets = new ObjectArrayList[bucketCount];
        this.bucketSizes = new int[bucketCount];
        this.intrusiveCollectionId = intrusiveCollectionId;
        this.playersSupplier = playersSupplier;
        for (int i = 0; i < buckets.length; i++) { buckets[i] = new ObjectArrayList<>(); }
        updatePlayerPositions();
    }

    protected abstract int coordinatesPerPlayer();

    protected abstract void storePlayerPosition(int index, EntityPlayer player);

    protected abstract int computeBucketIdx(T element);

    public void tick() {
        updatePlayerPositions();
        for (int i = 0, j = 0; i < 10 && j < bucketCount; j++) {
            if (bucketSizes[distributingBucket] == 0) {
                distributingBucket++;
                if (distributingBucket >= bucketCount) { distributingBucket = 0; }
                continue;
            }
            redistributeBucket(distributingBucket++);
            if (distributingBucket >= bucketCount) { distributingBucket = 0; }
            i++;
        }
    }

    protected void updatePlayerPositions() {
        Collection<EntityPlayer> players = playersSupplier.get();
        int newSize = players.size() * coordinatesPerPlayer();
        if (playerPositions.length != newSize) { playerPositions = new long[newSize]; }
        int i = 0;
        for (EntityPlayer player : players) {
            storePlayerPosition(i, player);
            i += coordinatesPerPlayer();
        }
    }

    protected int bucketFromDistanceSq(long distSqMin) {
        int log2dist = 64 - Long.numberOfLeadingZeros(distSqMin);
        int bitsToCutOff = log2dist >> 1;
        long approxDist = distSqMin >> bitsToCutOff;
        return (int) Math.min(approxDist, bucketCount - 1);
    }

    public void redistributeBucket(int bucket) {
        ObjectArrayList<T> list = buckets[bucket];
        for (int i = 0; i < list.size();) {
            T element = list.get(i);
            int newBucket = computeBucketIdx(element);
            if (newBucket == bucket) {
                i++;
                continue;
            }
            long oldElementNewData = 1 | ((long) newBucket << 1);
            bucketSizes[bucket]--;
            if (i != list.size() - 1) {
                T replacement = list.pop();
                list.set(i, replacement);
                long replacementElementData = replacement.getSorterStorage(intrusiveCollectionId) & 0x00000000FFFFFFFFL;
                replacement.setSorterStorage(intrusiveCollectionId, replacementElementData | ((long) i << 32));
            }
            else { list.pop(); }
            ObjectArrayList<T> newList = buckets[newBucket];
            bucketSizes[newBucket]++;
            element.setSorterStorage(intrusiveCollectionId, oldElementNewData | ((long) newList.size() << 32));
            newList.add(element);
        }
    }

    public boolean isEmpty() { return false; }

    @Override @Nonnull public Iterator<T> iterator() { return iteratorUpToDistance(bucketCount - 1); }

    public Iterator<T> iteratorUpToDistance(int maxDistance) {
        return new Iterator<T>() {
            int bucket = 0;
            int idx = 0;
            T next;
            private T peekNext() {
                if (next != null) { return next; }
                if (bucket > maxDistance) { return null; }
                while (idx >= bucketSizes[bucket]) {
                    bucket++;
                    idx = 0;
                    if (bucket > maxDistance) { return null; }
                }
                return next = buckets[bucket].get(idx++);
            }
            @Override public boolean hasNext() { return peekNext() != null; }
            @Override public T next() {
                T ret = peekNext();
                next = null;
                return ret;
            }
            @Override public void remove() {
                next = null;
                idx--;
                if (idx < 0) {
                    bucket--;
                    idx = bucketSizes[bucket] - 1;
                }
                WatchersSortingList.this.remove(buckets[bucket].get(idx));
                if (idx >= bucketSizes[bucket]) {
                    bucket++;
                    idx = 0;
                }
            }
        };
    }

    public void removeIf(Predicate<T> predicate) {
        for (Iterator<T> iterator = this.iterator(); iterator.hasNext();) {
            T t = iterator.next();
            if (predicate.test(t)) { iterator.remove(); }
        }
    }

    public void remove(T entry) {
        long sorterStorage = entry.getSorterStorage(intrusiveCollectionId);
        if (sorterStorage == 0) { return; }
        entry.setSorterStorage(intrusiveCollectionId, 0);
        int bucket = ((int) sorterStorage) >> 1;
        bucketSizes[bucket]--;
        int index = (int) (sorterStorage >>> 32);
        ObjectArrayList<T> list = buckets[bucket];
        if (index == list.size() - 1) {
            list.pop();
            return;
        }
        T replacementElement = list.pop();
        list.set(index, replacementElement);
        long replacementData = replacementElement.getSorterStorage(intrusiveCollectionId) & 0x00000000FFFFFFFFL;
        replacementElement.setSorterStorage(intrusiveCollectionId, replacementData | ((long) index << 32));
    }

    public void add(T element) {
        if (this.contains(element)) { return; }
        int bucket = computeBucketIdx(element);
        ObjectArrayList<T> list = buckets[bucket];
        bucketSizes[bucket]++;
        int indexInBucket = list.size();
        list.add(element);
        element.setSorterStorage(intrusiveCollectionId, (long) indexInBucket << 32 | (long) bucket << 1 | 1L);
    }

    public boolean contains(T element) { return element.getSorterStorage(intrusiveCollectionId) != 0; }
}
