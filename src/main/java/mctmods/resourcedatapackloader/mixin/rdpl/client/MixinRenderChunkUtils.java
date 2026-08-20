package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.core.optifine.interfaces.IOptifineExtendedBlockStorage;
import mctmods.resourcedatapackloader.core.optifine.interfaces.IOptifineRenderChunk;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo @Mixin(targets = "net.optifine.util.RenderChunkUtils") public class MixinRenderChunkUtils {
    @Dynamic @Inject(method = "getCountBlocks(Lnet/minecraft/client/renderer/chunk/RenderChunk;)I", at = @At("HEAD"),
            cancellable = true, remap = false)
    private static void getCountBlocks(RenderChunk renderChunk, CallbackInfoReturnable<Integer> cbi) {
        if (((IOptifineRenderChunk) renderChunk).isRubic()) {
            ExtendedBlockStorage ebs = ((IOptifineRenderChunk) renderChunk).getCube().getStorage();
            int ret = ebs == null ? 0 : ((IOptifineExtendedBlockStorage) ebs).getBlockRefCount();
            cbi.setReturnValue(ret);
        }
    }
}
