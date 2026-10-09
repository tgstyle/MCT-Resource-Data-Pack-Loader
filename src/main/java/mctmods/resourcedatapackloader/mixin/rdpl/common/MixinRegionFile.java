package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.world.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.File;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RegionFile.class) public abstract class MixinRegionFile {
    @Inject(method = "<init>", at = @At("RETURN")) private void rdpl$countOpened(File fileNameIn, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.opened(); }
    }

    @Unique private static final ContentChunkWatch.Timer rdpl$start = new ContentChunkWatch.Timer();

    @Inject(method = "write(II[BI)V", at = @At("HEAD")) private void rdpl$startFile(int x, int z, byte[] data, int length, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { rdpl$start.start(); }
    }

    @Inject(method = "write(II[BI)V", at = @At("RETURN")) private void rdpl$endFile(int x, int z, byte[] data, int length, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.toFile(rdpl$start.stop()); }
    }
}
