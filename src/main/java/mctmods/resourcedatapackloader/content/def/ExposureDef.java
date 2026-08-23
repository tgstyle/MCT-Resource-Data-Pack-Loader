package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;

public final class ExposureDef {
    public final ResourceLocation registryName;
    public final String name;
    public final int scanInterval;
    public final int range;
    public final boolean skipsCreative;
    public final int sourcesForNextLevel;
    public final String immunity;
    public final Map<ResourceLocation, Integer> blocks;
    public final Map<ResourceLocation, Integer> items;
    public final List<ExposureLevelDef> levels;

    public ExposureDef(ResourceLocation registryName, int scanInterval, int range, boolean skipsCreative, int sourcesForNextLevel, String immunity, Map<ResourceLocation, Integer> blocks, Map<ResourceLocation, Integer> items, List<ExposureLevelDef> levels) {
        this.registryName = registryName;
        this.name = registryName.getPath();
        this.scanInterval = scanInterval;
        this.range = range;
        this.skipsCreative = skipsCreative;
        this.sourcesForNextLevel = sourcesForNextLevel;
        this.immunity = immunity;
        this.blocks = blocks;
        this.items = items;
        this.levels = levels;
    }
}
