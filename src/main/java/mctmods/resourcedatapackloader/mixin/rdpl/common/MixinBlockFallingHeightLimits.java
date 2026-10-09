package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BlockFalling.class) public abstract class MixinBlockFallingHeightLimits extends Block {
    public MixinBlockFallingHeightLimits(Material materialIn) { super(materialIn); }

    @Group(name = "checkFallable_getMinY1", min = 1, max = 1) @ModifyConstant(method = "checkFallable", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockFalling;canFallThrough(Lnet/minecraft/block/state/IBlockState;)Z"), to = @At(value = "FIELD", target = "Lnet/minecraft/block/BlockFalling;fallInstantly:Z", opcode = Opcodes.GETSTATIC))) private int rdpl$checkFallable_getMinY1(int orig, World worldIn, BlockPos pos) { return GenHeights.floor(worldIn, orig); }

    @Group(name = "checkFallable_getMinY2", min = 2, max = 2) @ModifyConstant(method = "checkFallable", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO), slice = @Slice(from = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/block/BlockFalling;canFallThrough(Lnet/minecraft/block/state/IBlockState;)Z"), to = @At(value = "INVOKE:ONE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z"))) private int rdpl$checkFallable_getMinY2(int orig, World worldIn, BlockPos pos) { return GenHeights.floor(worldIn, orig); }

    @Redirect(method = "checkFallable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"), slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=32"), to = @At(value = "TAIL")), require = 2) @SuppressWarnings("NameDoesntMatchTargetClass") private IBlockState rdpl$checkCanFallThroughGetBlockState(World world, BlockPos checkPos, World worldIn, BlockPos pos) {
        if (checkPos == pos) { return world.getBlockState(checkPos); }
        if (!GenHeights.rubic(worldIn) || world.isBlockLoaded(checkPos.down(), false)) { return world.getBlockState(checkPos); }
        return Blocks.BEDROCK.getDefaultState();
    }

    @Redirect(method = "checkFallable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAirBlock(Lnet/minecraft/util/math/BlockPos;)Z"), require = 2) private boolean rdpl$checkIsAirBlock(World worldIn, BlockPos pos) {
        if (!GenHeights.rubic(worldIn) || worldIn.isBlockLoaded(pos.down(), false)) { return worldIn.isAirBlock(pos); }
        return false;
    }
}
