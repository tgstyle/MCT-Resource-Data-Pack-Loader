package mctmods.resourcedatapackloader.content.def;

public record StorageDef(int rows, int columns, FilterDef items, int fluidCapacity, FilterDef fluids, int energyCapacity, int energyTransfer, boolean buckets, boolean dropsOnDeath) {
    public int slots() { return rows * columns; }

    public int gauges() { return (fluidCapacity > 0 ? 1 : 0) + (energyCapacity > 0 ? 1 : 0); }

    public int bands() { return rows + gauges(); }
}
