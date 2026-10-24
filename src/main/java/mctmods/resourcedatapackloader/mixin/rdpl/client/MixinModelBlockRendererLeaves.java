package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.block.ContentLeavesBlock;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class) public abstract class MixinModelBlockRendererLeaves {
    @ModifyReturnValue(method = "forceOpaque", at = @At("RETURN")) private static boolean rdpl$solidLeaves(boolean forced, boolean cutoutLeaves, BlockState blockState) { return forced || blockState.getBlock() instanceof ContentLeavesBlock leaves && leaves.solidLayer(); }
}
