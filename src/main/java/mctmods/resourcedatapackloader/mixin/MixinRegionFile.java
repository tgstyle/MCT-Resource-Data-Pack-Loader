package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.world.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.File;

@Mixin(RegionFile.class)
public abstract class MixinRegionFile {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void rdpl$countOpened(File fileNameIn, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.opened(); }
    }
}
