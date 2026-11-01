package mctmods.resourcedatapackloader.mixin.client.renderer.block;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentSnowTint;
import mctmods.resourcedatapackloader.common.blocks.wood.ContentLeavesBlock;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class) public abstract class ModelBlockRendererMixin {
    @ModifyReturnValue(method = "forceOpaque", at = @At("RETURN")) private static boolean rdpl$solidLeaves(boolean forced, boolean cutoutLeaves, BlockState blockState) { return forced || blockState.getBlock() instanceof ContentLeavesBlock leaves && leaves.solidLayer(); }

    @ModifyExpressionValue(method = "putQuadWithTint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;tintIndex()I")) private int rdpl$tintSnow(int tintIndex, @Local(argsOnly = true, name = "state") BlockState state) { return tintIndex == -1 && ContentSnowTint.tints(state) ? 0 : tintIndex; }
}
