package mctmods.resourcedatapackloader.content.def;

public record TradeStackDef(String item, int min, int max) {
    public boolean isEmpty() { return item.isEmpty(); }
}
