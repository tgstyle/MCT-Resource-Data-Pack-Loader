package mctmods.resourcedatapackloader.common.dimension;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.dimension.DimensionValues;
import mctmods.resourcedatapackloader.common.util.worldgen.TemplateMemo;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public final class ContentDimensionSetting<T> {
    private final String key;
    private final Supplier<List<String>> configured;
    private final DimensionValues<T> values;
    private final TemplateMemo<List<String>> memo = new TemplateMemo<>();

    public ContentDimensionSetting(String key, Supplier<List<String>> configured, Function<String, T> parse, String wrong) {
        this.key = key;
        this.configured = configured;
        this.values = new DimensionValues<>(key, parse, wrong);
    }

    @Nullable public T at(String dimension, @Nullable T own) { return ContentControl.off(ContentControl.TERRAIN) ? own : values.at(dimension, memo.get(() -> ContentControl.list(ContentControl.TERRAIN, key, configured.get())), own); }
}
