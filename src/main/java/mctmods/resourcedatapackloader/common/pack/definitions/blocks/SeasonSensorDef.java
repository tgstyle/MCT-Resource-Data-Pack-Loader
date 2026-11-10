package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record SeasonSensorDef(@Nullable ResourceLocation dimension, Map<String, Integer> output) {
    public static final SeasonSensorDef PLAIN = new SeasonSensorDef(null, Map.of());

    public int power(String season, int index, int count) {
        Integer named = output.get(season);
        return named != null ? named : (index + 1) * 15 / count;
    }
}
