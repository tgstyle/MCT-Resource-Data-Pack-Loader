package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import java.util.Random;

public final class SaplingDef {
    public final List<String> soil;
    public final int stages;
    public final int chance;
    public final int light;
    public final String structure;
    public final List<PickDef> structures;
    public final String log;
    public final String leaves;
    public final int height;

    public SaplingDef(List<String> soil, int stages, int chance, int light, String structure, List<PickDef> structures, String log, String leaves, int height) {
        this.soil = soil;
        this.stages = stages;
        this.chance = chance;
        this.light = light;
        this.structure = structure;
        this.structures = structures;
        this.log = log;
        this.leaves = leaves;
        this.height = height;
    }

    public boolean usesStructure() { return !structure.isEmpty() || !structures.isEmpty(); }

    public String growsInto(Random random) { return PickDef.pick(structures, random, structure); }
}
