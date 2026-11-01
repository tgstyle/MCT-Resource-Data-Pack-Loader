package mctmods.resourcedatapackloader.common.pack.definitions.villages;

public record TradeStackDef(String item, int min, int max) {
    public boolean isEmpty() { return item.isEmpty(); }
}
