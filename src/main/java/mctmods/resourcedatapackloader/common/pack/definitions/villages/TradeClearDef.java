package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record TradeClearDef(ResourceLocation key, String profession, int level, List<String> requires) {
    public static final int ALL_LEVELS = 0;
}
