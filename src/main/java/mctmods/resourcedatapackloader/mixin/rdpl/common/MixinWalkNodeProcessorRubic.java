package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Objects;

@Mixin(WalkNodeProcessor.class) public abstract class MixinWalkNodeProcessorRubic extends NodeProcessor {
    @ModifyConstant(method = "getStart", constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO)) private int getMinHeight_GetStart(int originalY) {
        return ((IRubicWorld) this.entity.world).rdpl$getMinHeight() + originalY;
    }

    @Redirect(method = "getStart", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/IBlockAccess;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState getLoadedBlockState_getStart(IBlockAccess access, BlockPos pos) {
        if (!entity.world.isBlockLoaded(pos)) { return Objects.requireNonNull(Blocks.BEDROCK).getDefaultState(); }
        return access.getBlockState(pos);
    }

    @ModifyConstant(
            method = "getSafePoint",
            constant = @Constant(
                    expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO,
                    ordinal = 1
            ))
    private int getMinHeight_GetSafePoint(int originalY) { return ((IRubicWorld) this.entity.world).rdpl$getMinHeight() + originalY; }

    @ModifyConstant(
            method = "getPathNodeType(Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;",
            constant = @Constant(
                    intValue = 1, ordinal = 0
            ))
    private int getMinHeight_GetPathNodeType(int originalY, IBlockAccess blockaccessIn, int x, int y, int z) {
        return ((IMinMaxHeight) blockaccessIn).rdpl$getMinHeight() + originalY;
    }
}
