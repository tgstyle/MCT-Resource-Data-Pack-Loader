package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record CompressableDef(Identifier key, @Nullable Identifier of, @Nullable Identifier item, int layers, @Nullable String texture, boolean recipes) {}
