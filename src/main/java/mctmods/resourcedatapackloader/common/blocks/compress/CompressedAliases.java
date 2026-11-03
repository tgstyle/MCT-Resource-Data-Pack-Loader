package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserCompressable;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class CompressedAliases {
    private CompressedAliases() {}

    public static void apply() {
        for (CompressedSet set : ContentCompressables.sets()) {
            if (set.layers >= ContentParserCompressable.MAX_LAYERS) { continue; }
            ContentLog.LOGGER.info("Compressable {} has {} layer(s), so ids layer {} to {} are read as layer {}", set.def.key(), set.layers, set.layers + 1, ContentParserCompressable.MAX_LAYERS, set.layers);
            for (int layer = set.layers + 1; layer <= ContentParserCompressable.MAX_LAYERS; layer++) {
                alias(BuiltInRegistries.BLOCK, set, layer);
                alias(BuiltInRegistries.ITEM, set, layer);
            }
        }
    }

    private static void alias(Registry<?> registry, CompressedSet set, int layer) {
        Identifier from = set.blockId(layer);
        if (registry.containsKey(from)) { return; }
        Identifier to = set.blockId(set.layers);
        try { registry.addAlias(from, to); }
        catch (IllegalArgumentException | IllegalStateException clash) { ContentLog.LOGGER.error("Could not read {} as {}: {}", from, to, clash.getMessage()); }
    }
}
