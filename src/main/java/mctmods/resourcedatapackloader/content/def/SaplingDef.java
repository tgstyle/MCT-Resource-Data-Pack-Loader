package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public final class SaplingDef {
    public final List<String> soil;
    public final int stages;
    public final int chance;
    public final int light;
    public final String structure;
    public final String log;
    public final String leaves;
    public final int height;
    public final boolean vines;

    public SaplingDef(List<String> soil, int stages, int chance, int light, String structure, String log, String leaves, int height, boolean vines) {
        this.soil = soil;
        this.stages = stages;
        this.chance = chance;
        this.light = light;
        this.structure = structure;
        this.log = log;
        this.leaves = leaves;
        this.height = height;
        this.vines = vines;
    }

    public boolean usesStructure() { return !structure.isEmpty(); }
}
