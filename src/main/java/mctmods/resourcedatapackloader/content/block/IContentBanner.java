package mctmods.resourcedatapackloader.content.block;

import net.minecraft.resources.ResourceLocation;

public interface IContentBanner {
    ResourceLocation texture();

    static ResourceLocation textureOf(ResourceLocation id) { return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/entity/banner/" + id.getPath() + ".png"); }
}
