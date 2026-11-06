package mctmods.resourcedatapackloader.common.entities.behavior;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class OpenedContainers extends SavedData {
    private static final String NAME = "rdpl_opened";
    private static final String OPENED = "Opened";
    private final Set<Long> opened = new HashSet<>();

    private static OpenedContainers of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(OpenedContainers::read, OpenedContainers::new, NAME); }

    @Nullable private static OpenedContainers stored(ServerLevel level) { return level.getDataStorage().get(OpenedContainers::read, NAME); }

    private static OpenedContainers read(CompoundTag tag) {
        OpenedContainers held = new OpenedContainers();
        for (long packed : tag.getLongArray(OPENED)) { held.opened.add(packed); }
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.putLongArray(OPENED, opened.stream().mapToLong(Long::longValue).toArray());
        return tag;
    }

    public static void mark(ServerLevel level, BlockPos pos) {
        OpenedContainers held = of(level);
        if (held.opened.add(pos.asLong())) { held.setDirty(); }
    }

    public static boolean has(ServerLevel level, BlockPos pos) {
        OpenedContainers held = stored(level);
        return held != null && held.opened.contains(pos.asLong());
    }

    public static void forget(ServerLevel level, BlockPos pos) {
        OpenedContainers held = stored(level);
        if (held != null && held.opened.remove(pos.asLong())) { held.setDirty(); }
    }
}
