package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BlockLilyPad.class) public abstract class MixinBlockLilyPadHeightLimits extends BlockBush {
    public MixinBlockLilyPadHeightLimits(Material materialIn) { super(materialIn); }

    @ModifyConstant(method = "canBlockStay", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO), slice = @Slice(to = @At(value = "CONSTANT", args = "intValue=256")), require = 1) private int rdpl$canBlockStay_getMinY(int orig, World worldIn, BlockPos pos, IBlockState state) { return GenHeights.floor(worldIn, orig); }

    @ModifyConstant(method = "canBlockStay", constant = @Constant(intValue = 256)) private int rdpl$canBlockStay_getMaxY(int orig, World worldIn, BlockPos pos, IBlockState state) { return GenHeights.ceiling(worldIn, orig); }
}
