package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public record PhaseDef(float below, Map<String, Double> attributes, List<TaskDef> tasks, boolean charges, boolean pounces, boolean gusts, float gustPower, @Nullable BossBarDef bossBar) {}
