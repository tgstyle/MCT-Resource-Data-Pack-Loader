package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record PotionDef(ResourceLocation key, String name, boolean badEffect, boolean beneficial, int liquidColor, int iconX, int iconY, String iconTexture, boolean instant,List<AttributeDef> attributes, List<String> requires) {}
