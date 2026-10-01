package mctmods.resourcedatapackloader.content.def;

public final class StorageDef {
    public final int rows;
    public final int columns;
    public final FilterDef items;
    public final int fluidCapacity;
    public final FilterDef fluids;
    public final int energyCapacity;
    public final int energyTransfer;
    public final boolean buckets;
    public final boolean dropsOnDeath;

    public StorageDef(int rows, int columns, FilterDef items, int fluidCapacity, FilterDef fluids, int energyCapacity, int energyTransfer, boolean buckets, boolean dropsOnDeath) {
        this.energyTransfer = energyTransfer;
        this.buckets = buckets;
        this.dropsOnDeath = dropsOnDeath;
        this.rows = rows;
        this.columns = columns;
        this.items = items;
        this.fluidCapacity = fluidCapacity;
        this.fluids = fluids;
        this.energyCapacity = energyCapacity;
    }

    public int slots() { return rows * columns; }

    public int gauges() { return (fluidCapacity > 0 ? 1 : 0) + (energyCapacity > 0 ? 1 : 0); }
}
