package mctmods.resourcedatapackloader.content.def;

public final class StorageDef {
    public static final int PER_ROW = 9;
    public static final int BANDS = 3;
    public final boolean hasItems;
    public final FilterDef items;
    public final int fluidCapacity;
    public final FilterDef fluids;
    public final int energyCapacity;
    public final int energyTransfer;
    public final boolean buckets;
    public final boolean dropsOnDeath;
    public final int fluidUse;
    public final int energyUse;
    public final Dry runsDry;

    public StorageDef(boolean hasItems, FilterDef items, int fluidCapacity, FilterDef fluids, int energyCapacity, int energyTransfer, boolean buckets, boolean dropsOnDeath, int fluidUse, int energyUse, Dry runsDry) {
        this.fluidUse = fluidUse;
        this.energyUse = energyUse;
        this.runsDry = runsDry;
        this.energyTransfer = energyTransfer;
        this.buckets = buckets;
        this.dropsOnDeath = dropsOnDeath;
        this.hasItems = hasItems;
        this.items = items;
        this.fluidCapacity = fluidCapacity;
        this.fluids = fluids;
        this.energyCapacity = energyCapacity;
    }

    public int slots() { return hasItems ? PER_ROW * (BANDS - gauges()) : 0; }

    public int gauges() { return (fluidCapacity > 0 ? 1 : 0) + (energyCapacity > 0 ? 1 : 0); }

    public enum Dry { STOPS, SLOWS, HURTS }
}
