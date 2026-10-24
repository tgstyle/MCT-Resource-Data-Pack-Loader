package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class StructureMapDef {
    public static final int LIMIT = 8;
    public final ResourceLocation key;
    public final int cell;
    public final int ground;
    public final int spacing;
    public final int chance;
    @Nullable public final int[] at;
    public final Set<Integer> dimensions;
    public final Layer[] layers;
    @Nullable public final int[] depth;
    public final List<PickDef> spawners;
    @Nullable public final ResourceLocation loot;
    public final int cellsWide;
    public final int cellsDeep;
    public final int widest;

    public StructureMapDef(ResourceLocation key, int cell, int ground, int spacing, int chance, @Nullable int[] at, Set<Integer> dimensions, Layer[] layers, @Nullable int[] depth, List<PickDef> spawners, @Nullable ResourceLocation loot) {
        this.key = key;
        this.cell = cell;
        this.ground = ground;
        this.spacing = spacing;
        this.chance = chance;
        this.at = at;
        this.dimensions = dimensions;
        this.layers = layers;
        this.depth = depth;
        this.spawners = spawners;
        this.loot = loot;
        int wide = 1;
        int deep = 1;
        for (Layer layer : layers) {
            deep = Math.max(deep, layer.rows.length);
            for (String row : layer.rows) { wide = Math.max(wide, row.length()); }
        }
        this.cellsWide = wide;
        this.cellsDeep = deep;
        this.widest = Math.max(wide, deep) * cell;
    }

    public boolean fills() { return !spawners.isEmpty() || loot != null; }

    public int baseY(int anchor, long seed) {
        int sink = depth == null ? 0 : depth[0] + (int) Math.floorMod(MathUtil.mix(seed, 5, LIMIT, 5), depth[1] - depth[0] + 1);
        return anchor + 1 - ground * cell - sink;
    }

    public boolean allowsDimension(int dimension) { return dimensions.isEmpty() || dimensions.contains(dimension); }

    public static final class Layer {
        public final Map<Character, List<PickDef>> palette;
        public final String[] rows;

        public Layer(Map<Character, List<PickDef>> palette, String[] rows) {
            this.palette = palette;
            this.rows = rows;
        }
    }
}
