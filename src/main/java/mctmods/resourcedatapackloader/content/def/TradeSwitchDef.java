package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record TradeSwitchDef(String set, int stamp, List<TradeDef> trades) {
    public static final TradeSwitchDef NONE = new TradeSwitchDef("", 0, List.of());
}
