package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class) public abstract class MixinEntityLivingBaseRubic extends Entity {
    public MixinEntityLivingBaseRubic(World worldIn) { super(worldIn); }

    @ModifyArg(
            method = "travel",
            index = 1,
            at = @At(
                    target = "Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;setPos(DDD)"
                            + "Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;",
                    value = "INVOKE", ordinal = 1)
    )
    private double moveEntityWithHeading_getReplacedY(double y) { return this.posY; }

    @ModifyConstant(method = "attemptTeleport", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO)) private int rdpl$getMinHeight(int orig) {
        return ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @Redirect(method = "attemptTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I"))
    private int isBlockLoadedTeleportCheck(BlockPos blockPos) { return world.isBlockLoaded(blockPos.down()) ? blockPos.getY() : (Integer.MIN_VALUE + 1); }
}
