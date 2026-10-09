package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.DimensionValues;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

final class ContentDimensionSetting<T> {
    private final String key;
    private final Supplier<String[]> configured;
    private final DimensionValues<T> values;
    private final TemplateMemo<String[]> memo = new TemplateMemo<>();

    ContentDimensionSetting(String key, Supplier<String[]> configured, Function<String, T> parse, String wrong) {
        this.key = key;
        this.configured = configured;
        this.values = new DimensionValues<>(key, parse, wrong);
    }

    String[] asked() { return memo.get(() -> ContentControl.list(ContentControl.TERRAIN, key, configured.get())); }

    @Nullable T at(int dimension, @Nullable T own) { return ContentControl.off(ContentControl.TERRAIN) ? own : values.at(dimension, asked(), own); }
}
