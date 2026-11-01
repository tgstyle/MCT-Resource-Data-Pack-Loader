package mctmods.resourcedatapackloader.common.banners;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public interface IContentBanner {
    Identifier texture();

    boolean modeled();

    static Identifier textureOf(Identifier id) { return Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/banner/" + id.getPath() + ".png"); }

    static boolean shipsBlockstate(String namespace, String name) { return PackManager.get().provides(PackType.CLIENT_RESOURCES, namespace, "blockstates/" + name + PackManager.JSON_SUFFIX); }
}
