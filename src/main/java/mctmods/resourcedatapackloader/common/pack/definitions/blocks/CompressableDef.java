package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record CompressableDef(ResourceLocation key, @Nullable ResourceLocation of, @Nullable ResourceLocation item, int layers, @Nullable String texture, boolean recipes) {}
