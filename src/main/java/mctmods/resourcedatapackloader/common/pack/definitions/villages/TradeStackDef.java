package mctmods.resourcedatapackloader.common.pack.definitions.villages;

public final class TradeStackDef {
    public final String item;
    public final int min;
    public final int max;

    public TradeStackDef(String item, int min, int max) {
        this.item = item;
        this.min = min;
        this.max = max;
    }

    public boolean isEmpty() { return item.isEmpty(); }
}
