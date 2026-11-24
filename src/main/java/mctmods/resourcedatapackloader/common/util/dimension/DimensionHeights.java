package mctmods.resourcedatapackloader.common.util.dimension;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.core.config.Settings;

import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class DimensionHeights {
    private final String key;
    private final DimensionValues<Integer> heights;

    public DimensionHeights(String key) {
        this.key = key;
        this.heights = new DimensionValues<>(key, text -> Settings.clamped(text, Integer.MIN_VALUE, Integer.MAX_VALUE), "whose height is not a whole number");
    }

    @Nullable public Integer terrain(int dimension, Supplier<String[]> fallback) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        return heights.at(dimension, ContentControl.lines(ContentControl.TERRAIN, key, fallback.get()));
    }
}
