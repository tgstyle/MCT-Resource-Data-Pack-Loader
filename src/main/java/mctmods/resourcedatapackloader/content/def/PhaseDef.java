package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class PhaseDef {
    public final float below;
    public final Map<String, Double> attributes;
    public final List<TaskDef> tasks;
    public final boolean charges;
    public final boolean pounces;
    public final boolean gusts;
    public final float gustPower;
    @Nullable public final BossBarDef bossBar;

    public PhaseDef(float below, Map<String, Double> attributes, List<TaskDef> tasks, boolean charges, boolean pounces, boolean gusts, float gustPower, @Nullable BossBarDef bossBar) {
        this.below = below;
        this.attributes = attributes;
        this.tasks = tasks;
        this.charges = charges;
        this.pounces = pounces;
        this.gusts = gusts;
        this.gustPower = gustPower;
        this.bossBar = bossBar;
    }
}
