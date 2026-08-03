package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.world.chunk.storage.RegionFileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionFileCache.class)
public abstract class MixinRegionFileCache {
    @Inject(method = "clearRegionFileReferences", at = @At("HEAD"))
    private static void rdpl$countEmptied(CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.emptied(); }
    }
}
