package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.List;

public final class TreeRuleDef {
    public final String id;
    public final List<BlockMatchDef> targets;
    public final List<String> tagTargets;
    public final TreeGrowthDef growth;

    public TreeRuleDef(String id, List<BlockMatchDef> targets, List<String> tagTargets, TreeGrowthDef growth) {
        this.id = id;
        this.targets = targets;
        this.tagTargets = tagTargets;
        this.growth = growth;
    }
}
