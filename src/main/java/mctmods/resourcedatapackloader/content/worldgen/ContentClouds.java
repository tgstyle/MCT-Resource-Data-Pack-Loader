package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.DimensionHeights;

import javax.annotation.Nullable;

public final class ContentClouds {
    private static final String KEY = "cloudHeight";
    private static final DimensionHeights HEIGHTS = new DimensionHeights(KEY);

    private ContentClouds() {}

    @Nullable public static Integer heightFor(int dimension) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return HEIGHTS.at(dimension, ContentControl.lines(ContentControl.TERRAIN, KEY, Config.worldgen.cloudHeight));
    }
}
