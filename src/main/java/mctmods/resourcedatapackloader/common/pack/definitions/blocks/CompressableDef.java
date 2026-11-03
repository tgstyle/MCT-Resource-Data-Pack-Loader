package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class CompressableDef {
    public final ResourceLocation key;
    @Nullable public final ResourceLocation of;
    public final int ofMeta;
    public final int layers;
    @Nullable public final String texture;
    public final boolean recipes;

    public CompressableDef(ResourceLocation key, @Nullable ResourceLocation of, int ofMeta, int layers, @Nullable String texture, boolean recipes) {
        this.key = key;
        this.of = of;
        this.ofMeta = ofMeta;
        this.layers = layers;
        this.texture = texture;
        this.recipes = recipes;
    }
}
