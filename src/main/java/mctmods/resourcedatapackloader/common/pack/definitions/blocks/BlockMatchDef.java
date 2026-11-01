package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public record BlockMatchDef(ResourceLocation block, Map<String, String> properties) {}
