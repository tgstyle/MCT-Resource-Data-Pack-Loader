package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentLocate extends SavedData {
    public static final String NAME = "rdpl_locate";
    private static final String PLACED = "Placed";
    private CompoundTag placed = new CompoundTag();

    private static final SavedDataType<ContentLocate> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, NAME), ContentLocate::new, CompoundTag.CODEC.xmap(ContentLocate::read, ContentLocate::write));

    private static ContentLocate of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(TYPE); }

    private static ContentLocate read(CompoundTag tag) {
        ContentLocate held = new ContentLocate();
        held.placed = tag.getCompoundOrEmpty(PLACED).copy();
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        synchronized (this) { tag.put(PLACED, placed.copy()); }
        return tag;
    }

    public static void record(ServerLevel level, String name, BlockPos at) {
        ContentLocate held = of(level);
        synchronized (held) {
            long[] known = held.placed.getLongArray(name).orElse(new long[0]);
            long[] grown = new long[known.length + 1];
            System.arraycopy(known, 0, grown, 0, known.length);
            grown[known.length] = at.asLong();
            held.placed.putLongArray(name, grown);
        }
        held.setDirty();
    }

    public static List<String> names(ServerLevel level) {
        ContentLocate held = of(level);
        synchronized (held) { return new ArrayList<>(held.placed.keySet()); }
    }

    @Nullable public static BlockPos nearest(ServerLevel level, String name, BlockPos from) { return nearest(level, name, from, 0.0D); }

    @Nullable public static BlockPos nearest(ServerLevel level, String name, BlockPos from, double beyond) {
        ContentLocate held = of(level);
        long[] known;
        synchronized (held) { known = held.placed.getLongArray(name).orElse(new long[0]); }
        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        for (long packed : known) {
            BlockPos at = BlockPos.of(packed);
            double away = at.distSqr(from);
            if (away < beyond * beyond) { continue; }
            if (away < closest) {
                closest = away;
                best = at;
            }
        }
        return best;
    }
}
