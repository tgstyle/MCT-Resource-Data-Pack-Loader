package mctmods.resourcedatapackloader.util.interfaces;

import net.minecraft.world.level.pathfinder.PathType;
import javax.annotation.Nullable;

public interface IPathKindMemo {
    @Nullable PathType rdpl$known(int x, int y, int z);

    void rdpl$remember(int x, int y, int z, PathType kind);
}
