package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.RenderCubeCache;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderChunk.class) public abstract class MixinRenderChunk {
    @Shadow protected abstract void initModelviewMatrix();

    @Inject(method = "<init>", at = @At("RETURN")) private void onConstruct(World worldIn, RenderGlobal renderGlobalIn, int indexIn, CallbackInfo ci) {
        this.initModelviewMatrix();
    }
    @Redirect(method = "setPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;initModelviewMatrix()V"))
    private void noop(RenderChunk instance) {
    }

    @Inject(method = "createRegionRenderCache", at = @At(value = "HEAD"), remap = false, cancellable = true) private void createRubicChunkCache(World world, BlockPos from, BlockPos _to, int subtract, CallbackInfoReturnable<ChunkCache> cbi) {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) { cbi.setReturnValue(new RenderCubeCache(world, from, _to, subtract)); }
    }
}
