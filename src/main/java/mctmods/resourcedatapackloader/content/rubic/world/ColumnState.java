package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.util.Coords;

public final class ColumnState {
    private ColumnState() {}

    public static boolean cubeLoadedAt(Object column, int blockY) {
        ICube cube = ((IColumn) column).getLoadedCube(Coords.blockToCube(blockY));
        return cube != null && cube.isCubeLoaded();
    }

    public static boolean compatGenerating(Object column) { return ((IColumnInternal) column).getCompatGenerationPrimer() != null; }
}
