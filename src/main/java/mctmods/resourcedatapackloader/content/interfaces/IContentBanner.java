package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public interface IContentBanner {
    ResourceLocation texture();

    boolean modeled();

    static ResourceLocation textureOf(ResourceLocation id) { return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/entity/banner/" + id.getPath() + ".png"); }

    static boolean shipsBlockstate(String namespace, String name) { return PackManager.get().provides(PackType.CLIENT_RESOURCES, namespace, "blockstates/" + name + ".json"); }
}
