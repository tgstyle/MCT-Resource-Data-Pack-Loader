package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.CityDeckBeard;

import javax.annotation.Nullable;

public interface IMarkedNoise {
    void rdpl$mark(boolean voided, @Nullable CityDeckBeard city);

    boolean rdpl$voided();

    @Nullable CityDeckBeard rdpl$city();
}
