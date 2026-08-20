package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class) public abstract class MixinEntity {
    @Inject(method = "setFire", at = @At("HEAD"), cancellable = true) private void rdpl$neverCatchesFire(int seconds, CallbackInfo ci) {
        if (!ContentEntities.fireproof((Entity) (Object) this)) { return; }

        ci.cancel();
    }

    @Shadow public World world;

    @ModifyConstant(method = "onEntityUpdate", constant = @Constant(doubleValue = -64.0D), require = 1) private double getDeathY(double originalY) {
        return ((IRubicWorld) world).rdpl$getMinHeight() + originalY;
    }

    @Shadow public double posY;

    @Shadow public abstract float getEyeHeight();

    @ModifyArg(method = "getBrightness", index = 1, at = @At(target = "Lnet/minecraft/util/math/BlockPos$MutableBlockPos;<init>(III)V", value = "INVOKE"))
    public int getModifiedYPos_getBrightness(int y) { return MathHelper.floor(this.posY + this.getEyeHeight()); }
}
