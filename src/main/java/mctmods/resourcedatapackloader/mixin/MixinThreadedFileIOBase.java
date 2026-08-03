package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkSaves;

import net.minecraft.world.storage.ThreadedFileIOBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThreadedFileIOBase.class)
public abstract class MixinThreadedFileIOBase {
    @Redirect(method = "processQueue", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V", ordinal = 0, remap = false))
    private void rdpl$dontPauseWhenBehind(long millis) throws InterruptedException {
        if (ContentChunkSaves.hurry()) { return; }

        Thread.sleep(millis);
    }
}
