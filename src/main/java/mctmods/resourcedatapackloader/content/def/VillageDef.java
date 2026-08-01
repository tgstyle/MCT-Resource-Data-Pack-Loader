package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class VillageDef {
    public static final String FARM = "farm";
    public static final String TEMPLATE = "template";
    public final ResourceLocation registryName;
    public final String type;
    public final int weight;
    public final int leastCount;
    public final int mostCount;
    public final int width;
    public final int height;
    public final int depth;
    public final List<String> crops;
    public final String edge;
    public final String soil;
    public final boolean water;
    public final int rowWidth;
    public final String structure;
    public final String ground;
    public final int integrity;
    public final List<String> requires;

    public VillageDef(ResourceLocation registryName, String type, int weight, int leastCount, int mostCount, int width, int height, int depth, List<String> crops, String edge, String soil, boolean water, int rowWidth, String structure, String ground, int integrity, List<String> requires) {
        this.registryName = registryName;
        this.type = type;
        this.weight = weight;
        this.leastCount = leastCount;
        this.mostCount = mostCount;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.crops = crops;
        this.edge = edge;
        this.soil = soil;
        this.water = water;
        this.rowWidth = rowWidth;
        this.structure = structure;
        this.ground = ground;
        this.integrity = integrity;
        this.requires = requires;
    }

    public boolean isTemplate() { return TEMPLATE.equals(type); }
}
