package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.CityDeckBeard;

import net.minecraft.world.level.WorldGenLevel;
import javax.annotation.Nullable;

public interface IMarkedNoise {
    void rdpl$mark(boolean voided, @Nullable CityDeckBeard city, @Nullable WorldGenLevel level);

    boolean rdpl$voided();

    @Nullable CityDeckBeard rdpl$city();

    @Nullable WorldGenLevel rdpl$level();
}
