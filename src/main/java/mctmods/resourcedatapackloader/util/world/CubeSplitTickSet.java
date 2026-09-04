package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.world.NextTickListEntry;
import javax.annotation.Nonnull;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CubeSplitTickSet implements Set<NextTickListEntry> {
    private final Map<CubePos, NextTickListEntryHashSet> byCube = new HashMap<>();
    private final NextTickListEntryHashSet all = new NextTickListEntryHashSet();

    public Set<NextTickListEntry> getForCube(CubePos pos) {
        Set<NextTickListEntry> val = byCube.get(pos);
        return val == null ? Collections.emptySet() : val;
    }

    @Override public int size() { return all.size(); }

    @Override public boolean isEmpty() { return all.isEmpty(); }

    @Override public boolean contains(Object o) { return all.contains(o); }

    @Override @Nonnull public Iterator<NextTickListEntry> iterator() { return new CubeSplitIterator(all.iterator(), this::removeByCube); }

    private void removeByCube(NextTickListEntry e) {
        CubePos pos = CubePos.fromBlockCoords(e.position);
        Set<NextTickListEntry> set = byCube.get(pos);
        set.remove(e);
        if (set.isEmpty()) { byCube.remove(pos); }
    }

    @Override @Nonnull public Object[] toArray() { return all.toArray(); }

    @Override @Nonnull public <T> T[] toArray(@Nonnull T[] a) { return all.toArray(a); }

    @Override public boolean add(NextTickListEntry e) {
        boolean ret = all.add(e);
        byCube.computeIfAbsent(CubePos.fromBlockCoords(e.position), x -> new NextTickListEntryHashSet()).add(e);
        return ret;
    }

    @Override public boolean remove(Object o) {
        boolean ret = all.remove(o);
        if (ret) { removeByCube((NextTickListEntry) o); }
        return ret;
    }

    @Override public boolean containsAll(@Nonnull Collection<?> c) { return all.containsAll(c); }

    @Override public boolean addAll(Collection<? extends NextTickListEntry> c) {
        boolean ret = false;
        for (NextTickListEntry entry : c) {
            if (add(entry)) { ret = true; }
        }
        return ret;
    }

    @Override public boolean retainAll(@Nonnull Collection<?> c) {
        Iterator<NextTickListEntry> it = this.iterator();
        boolean changed = false;
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }

    @Override public boolean removeAll(Collection<?> c) {
        boolean ret = false;
        for (Object entry : c) {
            if (remove(entry)) { ret = true; }
        }
        return ret;
    }

    @Override public void clear() {
        all.clear();
        byCube.clear();
    }

    public static final class EqualsHashCodeWrapper<T extends Comparable<T>> implements Comparable<EqualsHashCodeWrapper<T>> {
        final T entry;

        public EqualsHashCodeWrapper(T entry) { this.entry = entry; }

        @Override public int hashCode() { return entry.hashCode(); }

        @Override public boolean equals(Object entry) {
            if (!(entry instanceof EqualsHashCodeWrapper)) { return false; }
            return this.entry.equals(((EqualsHashCodeWrapper<?>) entry).entry);
        }

        @Override public int compareTo(@Nonnull EqualsHashCodeWrapper<T> other) {
            if (this.equals(other)) { return 0; }
            return this.entry.compareTo(other.entry);
        }
    }

    public static final class NextTickListEntryHashSet extends AbstractSet<NextTickListEntry> {
        private final Set<EqualsHashCodeWrapper<NextTickListEntry>> backingSet = new HashSet<>();

        @Override @Nonnull public Iterator<NextTickListEntry> iterator() {
            return new Iterator<NextTickListEntry>() {
                final Iterator<EqualsHashCodeWrapper<NextTickListEntry>> it = backingSet.iterator();
                @Override public boolean hasNext() { return it.hasNext(); }
                @Override public NextTickListEntry next() { return it.next().entry; }
                @Override public void remove() { it.remove(); }
            };
        }

        @Override public int size() { return backingSet.size(); }

        @Override public boolean contains(Object entry) {
            if (!(entry instanceof NextTickListEntry)) { return false; }
            return backingSet.contains(new EqualsHashCodeWrapper<>((NextTickListEntry) entry));
        }

        @Override public boolean add(NextTickListEntry entry) { return backingSet.add(new EqualsHashCodeWrapper<>(entry)); }

        @Override public boolean remove(Object entry) {
            if (!(entry instanceof NextTickListEntry)) { return false; }
            return backingSet.remove(new EqualsHashCodeWrapper<>((NextTickListEntry) entry));
        }
    }
}
