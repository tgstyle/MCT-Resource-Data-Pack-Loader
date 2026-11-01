package mctmods.resourcedatapackloader.common.pack.definitions.effects;

import mctmods.resourcedatapackloader.common.pack.definitions.items.AttributeDef;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record PotionDef(ResourceLocation key, String name, boolean badEffect, boolean beneficial, int liquidColor, int iconX, int iconY, String iconTexture, boolean instant,List<AttributeDef> attributes, List<String> requires) {}
