package mctmods.resourcedatapackloader.util.compat;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

final class GcDimensionTypes {
    private GcDimensionTypes() {}

    static DimensionType of(int dimension, DimensionType fallback) { return DimensionManager.isDimensionRegistered(dimension) ? DimensionManager.getProviderType(dimension) : fallback; }
}
