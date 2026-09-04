package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(World.class) public abstract class MixinWorld implements IRubicWorld {
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
