package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record CompressableDef(ResourceLocation key, @Nullable ResourceLocation of, int layers, @Nullable String texture, boolean recipes) {}
