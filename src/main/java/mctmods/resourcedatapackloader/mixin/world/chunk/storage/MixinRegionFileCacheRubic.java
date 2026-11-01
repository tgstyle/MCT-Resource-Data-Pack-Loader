package mctmods.resourcedatapackloader.mixin.world.chunk.storage;

import mctmods.resourcedatapackloader.common.rubic.server.chunkio.region.RegionCache;

import java.io.IOException;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionFileCache.class) public class MixinRegionFileCacheRubic {
    @Inject(method = "clearRegionFileReferences", at = @At("HEAD")) private static void rdpl$onClearRefs(CallbackInfo cbi) throws IOException { RegionCache.closeAll(); }
}
