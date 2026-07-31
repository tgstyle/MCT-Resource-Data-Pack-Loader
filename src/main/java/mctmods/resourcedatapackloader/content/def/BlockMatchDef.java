package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.Map;

public final class BlockMatchDef {
    public final ResourceLocation block;
    public final int meta;
    public final Map<String, String> properties;

    public BlockMatchDef(ResourceLocation block, int meta, Map<String, String> properties) {
        this.block = block;
        this.meta = meta;
        this.properties = properties;
    }
}
