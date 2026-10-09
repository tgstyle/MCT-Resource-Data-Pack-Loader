package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.world.NextTickListEntry;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
public class CubeSplitTickList extends AbstractList<NextTickListEntry> {
    private final CubeTickIndex<List<NextTickListEntry>> byCube = new CubeTickIndex<>(ArrayList::new);
    private final List<NextTickListEntry> all = new ArrayList<>();

    public List<NextTickListEntry> getForCube(CubePos pos) {
        List<NextTickListEntry> val = byCube.get(pos);
        return val == null ? Collections.emptyList() : val;
    }

    @Override public int size() { return all.size(); }

    @Override public boolean isEmpty() { return all.isEmpty(); }

    @Override public boolean contains(Object o) { return all.contains(o); }

    @Nonnull @Override public Iterator<NextTickListEntry> iterator() { return new CubeSplitIterator(all.iterator(), byCube::remove); }

    @Override @Nonnull public Object[] toArray() { return all.toArray(); }

    @Override @Nonnull public <T> T[] toArray(@Nonnull T[] a) { return all.toArray(a); }

    @Override public boolean add(NextTickListEntry e) {
        all.add(e);
        byCube.add(e);
        return true;
    }

    @Override public boolean remove(Object o) {
        boolean ret = all.remove(o);
        if (ret) { byCube.remove((NextTickListEntry) o); }
        return ret;
    }

    @Override public boolean containsAll(@Nonnull Collection<?> c) { return new HashSet<>(all).containsAll(c); }

    @Override public void clear() {
        all.clear();
        byCube.clear();
    }

    @Override public NextTickListEntry get(int index) { return all.get(index); }

    @Override public NextTickListEntry set(int index, NextTickListEntry e) {
        NextTickListEntry old = all.set(index, e);
        byCube.remove(old);
        byCube.add(e);
        return old;
    }

    @Override public void add(int index, NextTickListEntry element) {
        all.add(index, element);
        byCube.add(element);
    }

    @Override public NextTickListEntry remove(int index) {
        NextTickListEntry old = all.remove(index);
        byCube.remove(old);
        return old;
    }

    @Override public int indexOf(Object o) { return all.indexOf(o); }

    @Override public int lastIndexOf(Object o) { return all.lastIndexOf(o); }
}
