package mctmods.resourcedatapackloader.core.integration.galacticraftcore;

import javax.annotation.Nullable;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

final class GcDimensionTypes {
    private GcDimensionTypes() {}

    static DimensionType of(int dimension, @Nullable DimensionType fallback) { return DimensionManager.isDimensionRegistered(dimension) ? DimensionManager.getProviderType(dimension) : fallback; }
}
