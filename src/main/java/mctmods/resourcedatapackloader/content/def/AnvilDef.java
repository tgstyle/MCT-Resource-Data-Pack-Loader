package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public record AnvilDef(ResourceLocation key, List<String> item, int itemCount, List<String> with, int withCount, String result, int resultCount, int levels, Map<String, Integer> enchantments,
                       String grants, boolean locks) {}
