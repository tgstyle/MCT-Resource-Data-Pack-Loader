package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class) public abstract class MixinClientLevel {
    @Unique private DimensionDef rdpl$def() { return ContentDimensions.def((ClientLevel) (Object) this); }

    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 rdpl$skyColor(Vec3 vec, CubicSampler.Vec3Fetcher fetcher) {
        DimensionDef def = rdpl$def();
        return def != null && def.skyColor() >= 0 ? Vec3.fromRGB24(def.skyColor()) : CubicSampler.gaussianSampleVec3(vec, fetcher);
    }

    @Inject(method = "getCloudColor", at = @At("HEAD"), cancellable = true)
    private void rdpl$cloudColor(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        DimensionDef def = rdpl$def();
        if (def != null && def.cloudColor() >= 0) { cir.setReturnValue(Vec3.fromRGB24(def.cloudColor())); }
    }

    @Inject(method = "getStarBrightness", at = @At("HEAD"), cancellable = true)
    private void rdpl$starBrightness(float partialTick, CallbackInfoReturnable<Float> cir) {
        DimensionDef def = rdpl$def();
        if (def != null && def.starBrightness() >= 0.0F) { cir.setReturnValue(def.starBrightness()); }
    }
}
