package mctmods.resourcedatapackloader.util.world;

import net.minecraft.world.NextTickListEntry;
import java.util.Iterator;
import java.util.function.Consumer;

final class CubeSplitIterator implements Iterator<NextTickListEntry> {
    private final Iterator<NextTickListEntry> it;
    private final Consumer<NextTickListEntry> onRemove;
    private NextTickListEntry lastEntry;

    CubeSplitIterator(Iterator<NextTickListEntry> it, Consumer<NextTickListEntry> onRemove) {
        this.it = it;
        this.onRemove = onRemove;
    }

    @Override public boolean hasNext() { return it.hasNext(); }

    @Override public NextTickListEntry next() { return lastEntry = it.next(); }

    @Override public void remove() {
        it.remove();
        onRemove.accept(lastEntry);
    }
}
