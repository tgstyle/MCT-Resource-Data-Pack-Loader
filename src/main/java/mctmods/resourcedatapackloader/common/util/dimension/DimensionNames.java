package mctmods.resourcedatapackloader.common.util.dimension;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public final class DimensionNames {
    private DimensionNames() {}

    public static String dimensionName(int dimension) {
        if (!DimensionManager.isDimensionRegistered(dimension)) { return Integer.toString(dimension); }
        DimensionType type = DimensionManager.getProviderType(dimension);
        return type == null ? Integer.toString(dimension) : type.getName();
    }
}
