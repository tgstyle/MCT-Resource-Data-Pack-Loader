package mctmods.resourcedatapackloader.content.def;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SeasonDef {
    public final int days;
    public final List<List<String>> order;
    public final List<String> seasonal;

    public SeasonDef(int days, List<List<String>> order) {
        this.days = days;
        this.order = order;
        List<String> all = new ArrayList<>();
        for (List<String> entry : order) {
            for (String name : entry) {
                if (!all.contains(name)) { all.add(name); }
            }
        }
        this.seasonal = Collections.unmodifiableList(all);
    }

    public int index(long day) { return (int) Math.floorMod(Math.floorDiv(day, days), order.size()); }
}
