package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.Map;

public final class BlockWeightDef {
    public final ResourceLocation block;
    public final int meta;
    public final int weight;
    public final Map<String, String> properties;

    public BlockWeightDef(ResourceLocation block, int meta, int weight, Map<String, String> properties) {
        this.block = block;
        this.meta = meta;
        this.weight = weight;
        this.properties = properties;
    }
}
