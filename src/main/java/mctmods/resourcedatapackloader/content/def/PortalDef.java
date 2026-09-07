package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;

public record PortalDef(ResourceLocation dimension, ResourceLocation returnDimension, String gate, int cooldown, boolean platform, String platformBlock, String sound, boolean owned, boolean walkIn) {}
