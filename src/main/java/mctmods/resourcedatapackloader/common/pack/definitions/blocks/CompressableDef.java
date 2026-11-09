package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class CompressableDef {
    public final ResourceLocation key;
    @Nullable public final ResourceLocation of;
    public final int ofMeta;
    @Nullable public final ResourceLocation item;
    public final int itemMeta;
    public final int layers;
    @Nullable public final String texture;
    public final boolean recipes;

    public CompressableDef(ResourceLocation key, @Nullable ResourceLocation of, int ofMeta, @Nullable ResourceLocation item, int itemMeta, int layers, @Nullable String texture, boolean recipes) {
        this.key = key;
        this.of = of;
        this.ofMeta = ofMeta;
        this.item = item;
        this.itemMeta = itemMeta;
        this.layers = layers;
        this.texture = texture;
        this.recipes = recipes;
    }
}
