package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;

import java.util.Random;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFire.class) public class MixinBlockFire {
    @Inject(method = "tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILjava/util/Random;ILnet/minecraft/util/EnumFacing;)V", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$guardCatchFire(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face, CallbackInfo ci) {
        if (!ClaimGuard.fireMaySpread(worldIn, pos.offset(face), pos)) { ci.cancel(); }
    }

    @Redirect(method = "updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", ordinal = 1)) private boolean rdpl$guardSpread(World world, BlockPos target, IBlockState newState, int flags, World worldIn, BlockPos pos, IBlockState state, Random rand) {
        return ClaimGuard.fireMaySpread(world, pos, target) && world.setBlockState(target, newState, flags);
    }
}
