package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;

public record PotionDef(Identifier key, String name, boolean badEffect, boolean beneficial, int liquidColor, int iconX, int iconY, String iconTexture, boolean instant, double effectiveness, List<AttributeDef> attributes, List<String> requires) {}
