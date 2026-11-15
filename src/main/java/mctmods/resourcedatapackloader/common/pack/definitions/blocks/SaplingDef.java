package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;

import java.util.List;
import net.minecraft.util.RandomSource;

public record SaplingDef(List<String> soil, int stages, int chance, int light, String structure, List<PickDef> structures, String log, String leaves, int height, boolean vines, TreeGrowthDef growth) {
    public SaplingDef(List<String> soil, int stages, int chance, int light, String structure, List<PickDef> structures, String log, String leaves, int height, boolean vines) {
        this(soil, stages, chance, light, structure, structures, log, leaves, height, vines, TreeGrowthDef.NONE);
    }

    public boolean growsVanilla() { return structure.isEmpty() && structures.isEmpty(); }

    public String growsInto(RandomSource random) {
        String picked = PickDef.pick(structures, random);
        return picked == null ? structure : picked;
    }
}
