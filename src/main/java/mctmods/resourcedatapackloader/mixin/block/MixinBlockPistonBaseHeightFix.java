package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.integration.CompatHandler;

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
    @Group(min = 4, max = 4) @Redirect(method = "canPush", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I")) private static int rdpl$getBlockYRedirect(BlockPos posArg, IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing,
                                         boolean destroyBlocks, EnumFacing p_185646_5_) {
        if (!GenHeights.rubic(worldIn)) { return posArg.getY(); }
        int floor = GenHeights.floor(worldIn, 0);
        int top = GenHeights.ceiling(worldIn, CompatHandler.VANILLA_HEIGHT) - 1;
        if (posArg.getY() < floor) { return -1; }
        if (posArg.getY() > top) { return worldIn.getHeight(); }
        if (posArg.getY() == floor) { return 0; }
        if (posArg.getY() == top) { return worldIn.getHeight() - 1; }
        return 1;
    }
}
