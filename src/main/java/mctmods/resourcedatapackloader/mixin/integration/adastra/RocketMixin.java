package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraHeights;
import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraRockets;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.common_storage_lib.resources.fluid.util.FluidAmounts;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Rocket.class, remap = false) public abstract class RocketMixin {
    @Redirect(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")) private static Object rdpl$properties(Map<?, ?> base, Object type) { return AdAstraRockets.properties(base, type); }

    @Redirect(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Learth/terrarium/adastra/common/entities/vehicles/Rocket$RocketProperties;)V", at = @At(value = "INVOKE", target = "Learth/terrarium/common_storage_lib/resources/fluid/util/FluidAmounts;toPlatformAmount(J)J")) private long rdpl$tank(long millibuckets) { return FluidAmounts.toPlatformAmount(AdAstraRockets.tank((Rocket) (Object) this, millibuckets)); }

    @Redirect(method = "consumeFuel(Z)Z", at = @At(value = "INVOKE", target = "Learth/terrarium/common_storage_lib/resources/fluid/util/FluidAmounts;toPlatformAmount(J)J")) private long rdpl$burn(long millibuckets) { return FluidAmounts.toPlatformAmount(AdAstraRockets.burn(((Rocket) (Object) this).level(), millibuckets)); }

    @Inject(method = "initiateLaunchSequence()V", at = @At("HEAD"), cancellable = true) private void rdpl$requiredPayload(CallbackInfo ci) {
        if (AdAstraRockets.grounded((Rocket) (Object) this)) { ci.cancel(); }
    }

    @Inject(method = "getDropStack()Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN")) private void rdpl$picked(CallbackInfoReturnable<ItemStack> cir) { AdAstraRockets.dropped((Rocket) (Object) this, cir.getReturnValue()); }

    @Redirect(method = "flightTick()V", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/common/config/AdAstraConfig;atmosphereLeave:I", opcode = Opcodes.GETSTATIC)) private int rdpl$exitHeight() { return AdAstraHeights.exit(((Rocket) (Object) this).level()); }
}
