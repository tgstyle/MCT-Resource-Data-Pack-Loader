package mctmods.resourcedatapackloader.common.pack.definitions.effects;

import mctmods.resourcedatapackloader.common.pack.definitions.items.AttributeDef;

import java.util.List;
import net.minecraft.resources.Identifier;

public record PotionDef(Identifier key, String name, boolean badEffect, boolean beneficial, int liquidColor, int iconX, int iconY, String iconTexture, boolean instant,List<AttributeDef> attributes, List<String> requires) {}
