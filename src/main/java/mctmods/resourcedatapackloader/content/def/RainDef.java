package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;

public record RainDef(ResourceLocation particle, ResourceLocation sound, float volume, int interval, int color, int snowColor, float angle, float heading) {}
