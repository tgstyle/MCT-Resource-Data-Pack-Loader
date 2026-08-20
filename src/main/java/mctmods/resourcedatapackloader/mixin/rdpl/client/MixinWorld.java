package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(World.class) public abstract class MixinWorld implements IRubicWorld {
    @Final @Shadow public WorldProvider provider;

    @Shadow public abstract boolean isBlockLoaded(BlockPos pos);

    @Shadow public abstract IBlockState getBlockState(BlockPos pos);

    @Shadow public abstract int getLightFor(EnumSkyBlock type, BlockPos pos);

    @Group(name = "getLightFromNeighborsFor", min = 2, max = 3) @ModifyConstant(
            method = "getLightFromNeighborsFor",
            constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.LESS_THAN_ZERO),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getZ()I")
            ))
    private int getLightFromNeighborsFor_getMinHeight(int zero) { return rdpl$getMinHeight(); }

    @Group(name = "getLightFromNeighborsFor") @ModifyArg(method = "getLightFromNeighborsFor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;<init>(III)V"),
            index = 1,
            require = 1)
    private int getLightFromNeighborsForGetMinHeight(int origY) { return this.rdpl$getMinHeight(); }
}
