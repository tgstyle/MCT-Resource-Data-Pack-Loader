package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.compat.CompatHandler;
import mctmods.resourcedatapackloader.util.world.GenHeights;

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

    @Inject(method = "setCountChunksXYZ", at = @At(value = "HEAD")) private void rdpl$onSetCountChunks(int renderDistanceChunks, CallbackInfo cbi) {
        if (GenHeights.rubic(world)) { this.rdpl$renderDistance = Config.client.verticalCubeLoadDistance * 2 + 1; }
        else { this.rdpl$renderDistance = Coords.blockToCube(GenHeights.ceiling(world, CompatHandler.VANILLA_HEIGHT)) - Coords.blockToCube(GenHeights.floor(world, 0)); }
    }

    @ModifyArg(method = "updateChunkPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunk;setPosition(III)V"), index = 1) private int rdpl$modifyRenderChunkPosWhenUpdatingPositions(int y) { return y + GenHeights.floor(world, 0); }

    @ModifyVariable(method = "markBlocksForUpdate", at = @At("HEAD"), argsOnly = true, index = 2) private int modifyMinYForUpdate(int minY) {
        if (GenHeights.rubic(world)) { return minY; }
        return minY - GenHeights.floor(world, 0);
    }

    @ModifyVariable(method = "markBlocksForUpdate", at = @At("HEAD"), argsOnly = true, index = 5) private int rdpl$modifyMaxYForUpdate(int maxY) {
        if (GenHeights.rubic(world)) { return maxY; }
        return maxY - GenHeights.floor(world, 0);
    }

    @Redirect(method = "getRenderChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I")) private int rdpl$modifyMaxYForUpdate(BlockPos instance) {
        if (GenHeights.rubic(world)) { return instance.getY(); }
        return instance.getY() - GenHeights.floor(world, 0);
    }

    @ModifyConstant(method = "setCountChunksXYZ", constant = @Constant(intValue = 16)) private int rdpl$getYViewDistance(int oldDistance) { return rdpl$renderDistance; }
}
