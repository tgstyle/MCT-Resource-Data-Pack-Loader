package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;

public record BiomeSpawnDef(ResourceLocation entity, String category, int weight, int min, int max) {}
