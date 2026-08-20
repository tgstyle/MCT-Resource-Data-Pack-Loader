package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPistonBase.class) public class MixinBlockPistonBaseHeightFix {
    @Group(min = 4, max = 4) @Redirect(
            method = "canPush",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I")
    )
    private static int getBlockYRedirect(BlockPos posArg, IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing,
                                         boolean destroyBlocks, EnumFacing p_185646_5_) {
        IRubicWorld world = (IRubicWorld) worldIn;
        if (posArg.getY() < world.rdpl$getMinHeight() || posArg.getY() >= world.rdpl$getMaxHeight()) { return posArg.getY(); }
        return 64;
    }
}
