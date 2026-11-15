package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;

public final class TreePickDef {
    public enum Kind { STRUCTURE, VANILLA, OWN }

    public final Kind kind;
    public final String source;
    public final int weight;
    public final List<PickDef> turns;
    public final List<PickDef> mirrors;
    public final Boolean inSteps;

    public TreePickDef(Kind kind, String source, int weight, List<PickDef> turns, List<PickDef> mirrors, Boolean inSteps) {
        this.kind = kind;
        this.source = source;
        this.weight = Math.max(1, weight);
        this.turns = turns;
        this.mirrors = mirrors;
        this.inSteps = inSteps;
    }

    public boolean growsInSteps(boolean foreignSapling) {
        if (inSteps != null) { return inSteps; }
        return kind != Kind.OWN || !foreignSapling;
    }
}
