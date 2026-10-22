package mctmods.resourcedatapackloader.content.def;

import java.util.Collections;
import java.util.List;

public final class TradeSwitchDef {
    public static final TradeSwitchDef NONE = new TradeSwitchDef("", 0, Collections.emptyList());
    public final String set;
    public final int stamp;
    public final List<TradeDef> trades;

    public TradeSwitchDef(String set, int stamp, List<TradeDef> trades) {
        this.set = set;
        this.stamp = stamp;
        this.trades = Collections.unmodifiableList(trades);
    }
}
