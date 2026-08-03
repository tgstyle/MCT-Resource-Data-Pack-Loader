package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.world.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionFile.class)
public abstract class MixinRegionFileWrite {
    @Unique private static final ThreadLocal<Long> rdpl$start = ThreadLocal.withInitial(() -> 0L);

    @Inject(method = "write(II[BI)V", at = @At("HEAD"))
    private void rdpl$startFile(int x, int z, byte[] data, int length, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { rdpl$start.set(System.nanoTime()); }
    }

    @Inject(method = "write(II[BI)V", at = @At("RETURN"))
    private void rdpl$endFile(int x, int z, byte[] data, int length, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.toFile(System.nanoTime() - rdpl$start.get()); }
    }
}
