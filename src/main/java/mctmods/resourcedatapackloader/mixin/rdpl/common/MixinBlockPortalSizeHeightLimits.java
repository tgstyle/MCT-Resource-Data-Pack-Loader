package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import net.minecraft.block.BlockPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockPortal.Size.class) public abstract class MixinBlockPortalSizeHeightLimits {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 0, ordinal = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO), require = 1) private int portalSizeClassInitReplace0(int posY, World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_) {
		return ((IRubicWorld)worldIn).rdpl$getMinHeight();
	}
}
