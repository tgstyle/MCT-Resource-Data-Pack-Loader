package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.List;

public record TreeRuleDef(String id, List<BlockMatchDef> targets, List<String> tagTargets, TreeGrowthDef growth) {
}
