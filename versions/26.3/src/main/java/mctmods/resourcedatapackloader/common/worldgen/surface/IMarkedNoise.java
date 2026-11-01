package mctmods.resourcedatapackloader.common.worldgen.surface;

import mctmods.resourcedatapackloader.common.worldgen.structure.city.CityDeckBeard;

import javax.annotation.Nullable;
import net.minecraft.world.level.WorldGenLevel;

public interface IMarkedNoise {
    void rdpl$mark(boolean voided, @Nullable CityDeckBeard city, @Nullable WorldGenLevel level);

    boolean rdpl$voided();

    @Nullable CityDeckBeard rdpl$city();

    @Nullable WorldGenLevel rdpl$level();
}
