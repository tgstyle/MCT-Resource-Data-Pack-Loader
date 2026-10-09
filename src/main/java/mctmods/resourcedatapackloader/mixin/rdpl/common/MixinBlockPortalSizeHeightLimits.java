package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.block.BlockPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockPortal.Size.class) public abstract class MixinBlockPortalSizeHeightLimits {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 0, ordinal = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO), require = 1) private int rdpl$portalSizeClassInitReplace0(int posY, World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_) { return GenHeights.floor(worldIn, posY); }
}
