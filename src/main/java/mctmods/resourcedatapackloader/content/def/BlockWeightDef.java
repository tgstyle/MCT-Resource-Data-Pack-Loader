package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public record BlockWeightDef(ResourceLocation block, int weight, Map<String, String> properties) {}
