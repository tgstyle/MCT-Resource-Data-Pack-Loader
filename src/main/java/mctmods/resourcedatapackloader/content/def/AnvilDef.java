package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public record AnvilDef(ResourceLocation key, String item, int itemCount, String with, int withCount, String result, int resultCount, int levels, Map<String, Integer> enchantments,
                       String grants, boolean locks) {}
