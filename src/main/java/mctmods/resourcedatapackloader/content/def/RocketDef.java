package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public final class RocketDef {
    public final int tier;
    public final int fuelTank;
    public final int cargoSlots;
    public final FilterDef cargo;
    public final List<FilterDef.Entry> requiredPayload;
    public final List<FilterDef.Entry> payload;

    public RocketDef(int tier, int fuelTank, int cargoSlots, FilterDef cargo, List<FilterDef.Entry> requiredPayload, List<FilterDef.Entry> payload) {
        this.tier = tier;
        this.fuelTank = fuelTank;
        this.cargoSlots = cargoSlots;
        this.cargo = cargo;
        this.requiredPayload = requiredPayload;
        this.payload = payload;
    }
}
