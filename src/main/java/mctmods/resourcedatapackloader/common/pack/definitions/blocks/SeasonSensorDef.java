package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class SeasonSensorDef {
    public static final SeasonSensorDef PLAIN = new SeasonSensorDef(null, Collections.emptyMap());
    @Nullable public final ResourceLocation dimension;
    public final Map<String, Integer> output;

    public SeasonSensorDef(@Nullable ResourceLocation dimension, Map<String, Integer> output) {
        this.dimension = dimension;
        this.output = output;
    }

    public int power(String season, int index, int count) {
        Integer named = output.get(season);
        return named != null ? named : (index + 1) * 15 / count;
    }
}
