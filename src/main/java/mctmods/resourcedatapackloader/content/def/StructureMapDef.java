package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public record StructureMapDef(ResourceLocation key, int cell, int ground, int spacing, int chance, @Nullable int[] at, List<String> dimensions, List<Layer> layers, int cellsWide, int cellsDeep) {
    public static final int LIMIT = 8;

    public int widest() { return Math.max(cellsWide, cellsDeep) * cell; }

    public record Layer(Map<Character, List<PickDef>> palette, List<String> rows) {}
}
