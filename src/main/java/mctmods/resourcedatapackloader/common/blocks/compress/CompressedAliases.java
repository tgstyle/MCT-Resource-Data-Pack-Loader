package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserCompressable;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;

public final class CompressedAliases {
    private static final Set<CompressedSet> LOGGED = new HashSet<>();

    private CompressedAliases() {}

    @SuppressWarnings("unchecked") public static void onMissingMappings(MissingMappingsEvent event) {
        ResourceLocation registry = event.getKey().location();
        if (!registry.equals(Registries.BLOCK.location()) && !registry.equals(Registries.ITEM.location())) { return; }
        remap(event, (ResourceKey<? extends Registry<Object>>) event.getKey(), (IForgeRegistry<Object>) event.getRegistry());
    }

    private static <T> void remap(MissingMappingsEvent event, ResourceKey<? extends Registry<T>> key, IForgeRegistry<T> pool) {
        Map<ResourceLocation, ResourceLocation> aliases = aliases();
        for (MissingMappingsEvent.Mapping<T> mapping : event.getAllMappings(key)) {
            ResourceLocation to = aliases.get(mapping.getKey());
            if (to == null || !pool.containsKey(to)) { continue; }
            mapping.remap(pool.getValue(to));
        }
    }

    private static Map<ResourceLocation, ResourceLocation> aliases() {
        Map<ResourceLocation, ResourceLocation> aliases = new HashMap<>();
        for (CompressedSet set : ContentCompressables.sets()) {
            if (set.layers >= ContentParserCompressable.MAX_LAYERS) { continue; }
            if (LOGGED.add(set)) {
                ContentLog.LOGGER.info("Compressable {} has {} layer(s), so ids layer {} to {} are read as layer {}", set.def.key(), set.layers, set.layers + 1, ContentParserCompressable.MAX_LAYERS, set.layers);
            }
            ResourceLocation to = set.blockId(set.layers);
            for (int layer = set.layers + 1; layer <= ContentParserCompressable.MAX_LAYERS; layer++) {
                aliases.put(set.blockId(layer), to);
            }
        }
        return aliases;
    }
}
