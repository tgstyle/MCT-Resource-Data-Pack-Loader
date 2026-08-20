package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.server.chunkio.region.RegionCache;

import net.minecraft.world.chunk.storage.RegionFileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;

@Mixin(RegionFileCache.class) public class MixinRegionFileCacheRubic {
    @Inject(method = "clearRegionFileReferences", at = @At("HEAD")) private static void onClearRefs(CallbackInfo cbi) throws IOException { RegionCache.closeAll(); }
}
