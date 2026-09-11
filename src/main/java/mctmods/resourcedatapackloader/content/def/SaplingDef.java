package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.RandomSource;
import java.util.List;

public record SaplingDef(List<String> soil, int stages, int chance, int light, String structure, List<PickDef> structures, String log, String leaves, int height, boolean vines) {
    public boolean growsVanilla() { return structure.isEmpty() && structures.isEmpty(); }

    public String growsInto(RandomSource random) {
        String picked = PickDef.pick(structures, random);
        return picked == null ? structure : picked;
    }
}
