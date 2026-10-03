package mctmods.resourcedatapackloader.content.def;

public record StorageDef(boolean hasItems, FilterDef items, int fluidCapacity, FilterDef fluids, int energyCapacity, int energyTransfer, boolean buckets, boolean dropsOnDeath) {
    public static final int PER_ROW = 9;
    public static final int BANDS = 3;

    public int slots() { return hasItems ? PER_ROW * slotRows() : 0; }

    public int slotRows() { return hasItems ? BANDS - gauges() : 0; }

    public int gauges() { return (fluidCapacity > 0 ? 1 : 0) + (energyCapacity > 0 ? 1 : 0); }

    public int bands() { return slotRows() + gauges(); }
}
