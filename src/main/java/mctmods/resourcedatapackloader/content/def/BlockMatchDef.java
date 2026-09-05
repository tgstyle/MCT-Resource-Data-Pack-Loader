package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public record BlockMatchDef(ResourceLocation block, Map<String, String> properties) {}
