package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ViewFrustum.class) public class MixinViewFrustumVertViewDistance {
    @Shadow @Final protected World world;
    @Unique private int rdpl$renderDistance = 16;

    @Inject(method = "setCountChunksXYZ", at = @At(value = "HEAD")) private void onSetCountChunks(int renderDistanceChunks, CallbackInfo cbi) {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) { this.rdpl$renderDistance = Config.client.verticalCubeLoadDistance * 2 + 1; }
        else {
            IRubicWorld world = (IRubicWorld) this.world;
            this.rdpl$renderDistance = Coords.blockToCube(world.rdpl$getMaxHeight()) - Coords.blockToCube(world.rdpl$getMinHeight());
        }
    }

    @ModifyArg(method = "updateChunkPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;setPosition"
            + "(III)V"), index = 1)
    private int modifyRenderChunkPosWhenUpdatingPositions(int y) { return y + ((IRubicWorld) world).rdpl$getMinHeight(); }

    @ModifyVariable(method = "markBlocksForUpdate", at = @At("HEAD"), argsOnly = true, index = 2) private int modifyMinYForUpdate(int minY) {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) { return minY; }
        return minY - ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @ModifyVariable(method = "markBlocksForUpdate", at = @At("HEAD"), argsOnly = true, index = 5) private int modifyMaxYForUpdate(int maxY) {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) { return maxY; }
        return maxY - ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @Redirect(method = "getRenderChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I"))
    private int modifyMaxYForUpdate(BlockPos instance) {
        if (((IRubicWorld) world).rdpl$isRubicWorld()) { return instance.getY(); }
        return instance.getY() - ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @ModifyConstant(method = "setCountChunksXYZ", constant = @Constant(intValue = 16)) private int getYViewDistance(int oldDistance) { return rdpl$renderDistance; }
}
