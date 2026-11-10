package mctmods.resourcedatapackloader.common.pack.definitions.items;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class SeasonClockDef {
    @Nullable public final ResourceLocation dimension;
    public final boolean message;

    public SeasonClockDef(@Nullable ResourceLocation dimension, boolean message) {
        this.dimension = dimension;
        this.message = message;
    }
}
