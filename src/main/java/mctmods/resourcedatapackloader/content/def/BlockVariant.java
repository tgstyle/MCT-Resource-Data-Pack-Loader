package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record BlockVariant(ResourceLocation id, String name, String rarity, int maxSize, List<String> tags, float hardness, float resistance, int harvestLevel, int light, List<DropDef> drops) {
    public int harvestLevelOr(int fallback) { return harvestLevel < 0 ? fallback : harvestLevel; }
}
