package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Entity.class) public abstract class MixinEntityRubic {
    @Shadow public World world;
    @Shadow public double posY;

    @Shadow public abstract float getEyeHeight();

    @ModifyConstant(method = "onEntityUpdate", constant = @Constant(doubleValue = -64.0D), require = 1) private double rdpl$getDeathY(double originalY) { return GenHeights.floor(world, 0) + originalY; }

    @ModifyArg(method = "getBrightness", index = 1, at = @At(target = "Lnet/minecraft/util/math/BlockPos$MutableBlockPos;<init>(III)V", value = "INVOKE")) public int rdpl$getModifiedYPos_getBrightness(int y) { return MathHelper.floor(this.posY + this.getEyeHeight()); }
}
