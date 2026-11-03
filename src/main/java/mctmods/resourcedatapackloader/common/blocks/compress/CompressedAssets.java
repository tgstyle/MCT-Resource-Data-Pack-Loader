package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.generated.ContentPixelMaps;
import mctmods.resourcedatapackloader.common.pack.io.GeneratedResources;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;

import net.minecraft.server.packs.PackType;

public final class CompressedAssets {
    private CompressedAssets() {}

    public static void generate() {
        for (CompressedSet set : ContentCompressables.sets()) {
            String namespace = set.def.key().getNamespace();
            for (int layer = 1; layer <= set.layers; layer++) {
                String path = "textures/block/" + set.assetName(layer) + ContentPixelMaps.PNG;
                if (PackManager.get().provides(PackType.CLIENT_RESOURCES, namespace, path)) { continue; }
                lazy(set, layer, namespace, path);
            }
        }
    }

    private static void lazy(CompressedSet set, int layer, String namespace, String path) {
        GeneratedResources.putLazy(PackType.CLIENT_RESOURCES, namespace, path, () -> CompressedTexture.draw(set, layer));
    }

    public static void forget() { CompressedBase.forget(); }
}
