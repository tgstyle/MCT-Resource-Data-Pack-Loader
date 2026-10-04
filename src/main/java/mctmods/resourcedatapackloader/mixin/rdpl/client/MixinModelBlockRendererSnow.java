package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentSnowTint;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class) public abstract class MixinModelBlockRendererSnow {
    @ModifyExpressionValue(method = "putQuadWithTint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;tintIndex()I"))
    private int rdpl$tintSnow(int tintIndex, @Local(argsOnly = true) BlockState state) { return tintIndex == -1 && ContentSnowTint.tints(state) ? 0 : tintIndex; }
}
