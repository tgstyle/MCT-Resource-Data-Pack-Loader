package mctmods.resourcedatapackloader.common.rubic.world;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.world.Coords;

public final class ColumnState {
    private ColumnState() {}

    public static boolean cubeLoadedAt(Object column, int blockY) {
        ICube cube = ((IColumn) column).getLoadedCube(Coords.blockToCube(blockY));
        return cube != null && cube.isCubeLoaded();
    }

    public static boolean compatGenerating(Object column) { return ((IColumnInternal) column).getCompatGenerationPrimer() != null; }
}
