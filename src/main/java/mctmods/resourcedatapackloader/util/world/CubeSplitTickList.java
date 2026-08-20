package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.world.NextTickListEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CubeSplitTickList extends AbstractList<NextTickListEntry> {
    private final Map<CubePos, List<NextTickListEntry>> byCube = new HashMap<>();
    private final List<NextTickListEntry> all = new ArrayList<>();

    public List<NextTickListEntry> getForCube(CubePos pos) {
        List<NextTickListEntry> val = byCube.get(pos);
        return val == null ? Collections.emptyList() : val;
    }

    @Override public int size() { return all.size(); }

    @Override public boolean isEmpty() { return all.isEmpty(); }

    @Override public boolean contains(Object o) { return all.contains(o); }

    @NotNull
    @SuppressWarnings("Duplicates") @Override public Iterator<NextTickListEntry> iterator() {
        return new Iterator<NextTickListEntry>() {
            private final Iterator<NextTickListEntry> it = all.iterator();
            private NextTickListEntry lastEntry = null;
            @Override public boolean hasNext() { return it.hasNext(); }
            @Override public NextTickListEntry next() { return lastEntry = it.next(); }
            @Override public void remove() {
                it.remove();
                removeByCube(lastEntry);
            }
        };
    }

    private void removeByCube(NextTickListEntry e) {
        CubePos pos = CubePos.fromBlockCoords(e.position);
        List<NextTickListEntry> list = byCube.get(pos);
        list.remove(e);
        if (list.isEmpty()) { byCube.remove(pos); }
    }

    private void addByCube(NextTickListEntry e) { byCube.computeIfAbsent(CubePos.fromBlockCoords(e.position), x -> new ArrayList<>()).add(e); }

    @Override @NotNull public Object[] toArray() { return all.toArray(); }

    @Override @NotNull public <T> T[] toArray(@NotNull T[] a) { return all.toArray(a); }

    @Override public boolean add(NextTickListEntry e) {
        all.add(e);
        addByCube(e);
        return true;
    }

    @Override public boolean remove(Object o) {
        boolean ret = all.remove(o);
        if (ret) { removeByCube((NextTickListEntry) o); }
        return ret;
    }

    @Override public boolean containsAll(@NotNull Collection<?> c) { return new HashSet<>(all).containsAll(c); }

    @Override public void clear() {
        all.clear();
        byCube.clear();
    }

    @Override public NextTickListEntry get(int index) { return all.get(index); }

    @Override public NextTickListEntry set(int index, NextTickListEntry e) {
        NextTickListEntry old = all.set(index, e);
        removeByCube(old);
        addByCube(e);
        return null;
    }

    @Override public void add(int index, NextTickListEntry element) {
        all.add(index, element);
        addByCube(element);
    }

    @Override public NextTickListEntry remove(int index) {
        NextTickListEntry old = all.remove(index);
        removeByCube(old);
        return old;
    }

    @Override public int indexOf(Object o) { return all.indexOf(o); }

    @Override public int lastIndexOf(Object o) { return all.lastIndexOf(o); }
}
