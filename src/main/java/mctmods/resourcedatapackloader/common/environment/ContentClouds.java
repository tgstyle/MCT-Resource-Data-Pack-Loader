package mctmods.resourcedatapackloader.common.environment;

import mctmods.resourcedatapackloader.common.util.dimension.DimensionHeights;
import mctmods.resourcedatapackloader.core.config.Config;

import javax.annotation.Nullable;

public final class ContentClouds {
    private static final DimensionHeights HEIGHTS = new DimensionHeights("cloudHeight");

    private ContentClouds() {}

    @Nullable public static Integer heightFor(int dimension) { return HEIGHTS.terrain(dimension, () -> Config.worldgen.cloudHeight); }
}
