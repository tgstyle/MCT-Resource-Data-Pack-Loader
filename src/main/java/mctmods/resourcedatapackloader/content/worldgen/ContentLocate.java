package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentLocate extends SavedData {
    private static final String NAME = "rdpl_locate";
    private static final String PLACED = "Placed";
    private CompoundTag placed = new CompoundTag();

    private static final Factory<ContentLocate> FACTORY = new Factory<>(ContentLocate::new, (tag, lookup) -> read(tag));

    private static ContentLocate of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static ContentLocate read(CompoundTag tag) {
        ContentLocate held = new ContentLocate();
        held.placed = tag.getCompound(PLACED).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        synchronized (this) { tag.put(PLACED, placed.copy()); }
        return tag;
    }

    public static void record(ServerLevel level, String name, BlockPos at) {
        ContentLocate held = of(level);
        synchronized (held) {
            long[] known = held.placed.getLongArray(name);
            long[] grown = new long[known.length + 1];
            System.arraycopy(known, 0, grown, 0, known.length);
            grown[known.length] = at.asLong();
            held.placed.putLongArray(name, grown);
        }
        held.setDirty();
    }

    public static List<String> names(ServerLevel level) {
        ContentLocate held = of(level);
        synchronized (held) { return new ArrayList<>(held.placed.getAllKeys()); }
    }

    @Nullable public static BlockPos nearestBeyond(ServerLevel level, String name, BlockPos from, List<BlockPos> skip) {
        ContentLocate held = of(level);
        long[] known;
        synchronized (held) { known = held.placed.getLongArray(name); }
        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        for (long packed : known) {
            BlockPos at = BlockPos.of(packed);
            if (ContentStructureSearch.beenNear(skip, at)) { continue; }
            double away = at.distSqr(from);
            if (away < closest) {
                closest = away;
                best = at;
            }
        }
        return best;
    }

    @Nullable public static BlockPos nearest(ServerLevel level, String name, BlockPos from) {
        ContentLocate held = of(level);
        long[] known;
        synchronized (held) { known = held.placed.getLongArray(name); }
        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        for (long packed : known) {
            BlockPos at = BlockPos.of(packed);
            double away = at.distSqr(from);
            if (away < closest) {
                closest = away;
                best = at;
            }
        }
        return best;
    }
}
