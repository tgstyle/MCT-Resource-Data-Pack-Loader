package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import java.util.List;

public record TradeSwitchDef(String set, int stamp, List<TradeDef> trades) { public static final TradeSwitchDef NONE = new TradeSwitchDef("", 0, List.of()); }
