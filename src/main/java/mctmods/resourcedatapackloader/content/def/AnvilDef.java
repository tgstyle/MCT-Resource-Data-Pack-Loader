package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Map;

public record AnvilDef(Identifier key, List<String> item, int itemCount, List<String> with, int withCount, String result, int resultCount, int levels, Map<String, Integer> enchantments,
                       String grants, boolean locks) {}
