package mctmods.resourcedatapackloader.mixin.integration.bop;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentCascade;

import biomesoplenty.common.util.block.BlockQuery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockQuery.BlockQueryBase.class, remap = false) public abstract class MixinBlockQueryBase {
    @Redirect(method = "matches(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true)) private IBlockState rdpl$readLoaded(World world, BlockPos pos) { return ContentCascade.stateOrUnloaded(world, pos); }
}
