package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;

public record TreePickDef(Kind kind, String source, int weight, List<PickDef> turns, List<PickDef> mirrors, Boolean inSteps) {
    public enum Kind { STRUCTURE, VANILLA, OWN }

    public TreePickDef {
        weight = Math.max(1, weight);
    }

    public boolean growsInSteps(boolean foreignSapling) {
        if (inSteps != null) { return inSteps; }
        return kind != Kind.OWN || !foreignSapling;
    }
}
