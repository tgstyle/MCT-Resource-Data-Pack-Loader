package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;

import java.util.Random;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockDynamicLiquid.class) public abstract class MixinBlockDynamicLiquid {
    @Shadow protected abstract void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level);

    @Redirect(method = "updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockDynamicLiquid;tryFlowInto(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)V")) private void rdpl$guardFlow(BlockDynamicLiquid self, World worldIn, BlockPos target, IBlockState targetState, int level, World tickWorld, BlockPos pos, IBlockState state, Random rand) {
        if (ClaimGuard.fluidMayFlow(worldIn, pos, target)) { tryFlowInto(worldIn, target, targetState, level); }
    }
}
