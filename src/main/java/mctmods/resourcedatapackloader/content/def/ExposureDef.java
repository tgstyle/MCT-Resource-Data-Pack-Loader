package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public record ExposureDef(ResourceLocation key, int scanInterval, int range, boolean skipsCreative, int sourcesForNextLevel, String immunity, Map<ResourceLocation, Integer> blocks, Map<ResourceLocation, Integer> items, List<ExposureLevelDef> levels) {
    public String name() { return key.getPath(); }
}
