package mctmods.resourcedatapackloader.mixin;

import java.util.List;

final class LineMixins {
    static final List<String> NAMES = List.of("common.IRegistryLoadTask", "common.MixinRecipeLoadTask", "common.MixinReloadableServerResources", "common.MixinNoiseChunk", "common.MixinFeaturePlacer");

    private LineMixins() {}
}
