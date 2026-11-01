package mctmods.resourcedatapackloader.mixin.world.level.block;

import mctmods.resourcedatapackloader.common.util.world.IFaceHiding;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class) public abstract class BlockMixin {
    @Redirect(method = "shouldRenderFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;supportsExternalFaceHiding()Z", remap = false)) private static boolean rdpl$cachedFaceHiding(BlockState state) { return ((IFaceHiding) state).rdpl$faceHiding(); }
}
