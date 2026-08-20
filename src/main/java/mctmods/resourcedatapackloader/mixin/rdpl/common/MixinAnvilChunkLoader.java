package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.io.DataInputStream;
import java.io.File;

@Mixin(AnvilChunkLoader.class) public abstract class MixinAnvilChunkLoader {
    @Unique private static final ThreadLocal<Long> rdpl$readyStart = ThreadLocal.withInitial(() -> 0L);
    @Unique private static final ThreadLocal<Long> rdpl$writeStart = ThreadLocal.withInitial(() -> 0L);

    @Inject(method = "loadChunk__Async", at = @At("HEAD"), remap = false) private void rdpl$countRead(World worldIn, int x, int z, CallbackInfoReturnable<Object[]> cir) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.read(); }
    }

    @Inject(method = "saveChunk", at = @At("HEAD")) private void rdpl$startReady(World worldIn, Chunk chunkIn, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { rdpl$readyStart.set(System.nanoTime()); }
    }

    @Inject(method = "saveChunk", at = @At("RETURN")) private void rdpl$endReady(World worldIn, Chunk chunkIn, CallbackInfo ci) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.readied(System.nanoTime() - rdpl$readyStart.get()); }
    }

    @Inject(method = "writeNextIO", at = @At("HEAD")) private void rdpl$startWrite(CallbackInfoReturnable<Boolean> cir) {
        if (ContentChunkWatch.watching()) { rdpl$writeStart.set(System.nanoTime()); }
    }

    @Inject(method = "writeNextIO", at = @At("RETURN")) private void rdpl$endWrite(CallbackInfoReturnable<Boolean> cir) {
        if (!ContentChunkWatch.watching() || !cir.getReturnValueZ()) { return; }
        ContentChunkWatch.written(System.nanoTime() - rdpl$writeStart.get());
    }

    @Redirect(method = "loadChunk__Async", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/RegionFileCache;getChunkInputStream(Ljava/io/File;II)Ljava/io/DataInputStream;", remap = true))
    private DataInputStream rdpl$countDisk(File worldDir, int chunkX, int chunkZ) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.readFromDisk(); }
        return RegionFileCache.getChunkInputStream(worldDir, chunkX, chunkZ);
    }
}
