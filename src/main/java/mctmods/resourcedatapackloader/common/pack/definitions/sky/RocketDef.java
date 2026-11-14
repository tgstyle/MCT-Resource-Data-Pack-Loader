package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.FilterDef;

import java.util.List;

public record RocketDef(int tier, int fuelTank, FilterDef cargo, List<FilterDef.Entry> requiredPayload, List<FilterDef.Entry> payload) {
    public static final int BASE = -1;
}
