package mctmods.resourcedatapackloader.util.interfaces;

import net.minecraft.world.level.pathfinder.BlockPathTypes;
import javax.annotation.Nullable;

public interface IPathKindMemo {
    @Nullable BlockPathTypes rdpl$known(int x, int y, int z);

    void rdpl$remember(int x, int y, int z, BlockPathTypes kind);
}
