package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class) public abstract class MixinClientLevel {
    @Unique private DimensionDef rdpl$def() { return ContentDimensions.def((ClientLevel) (Object) this); }

    @Unique private static Vec3 rdpl$color(int rgb) { return new Vec3(((rgb >> 16) & 255) / 255.0D, ((rgb >> 8) & 255) / 255.0D, (rgb & 255) / 255.0D); }

    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void rdpl$skyColor(Vec3 pos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        DimensionDef def = rdpl$def();
        if (def != null && def.skyColor() >= 0) { cir.setReturnValue(rdpl$color(def.skyColor())); }
    }

    @Inject(method = "getCloudColor", at = @At("HEAD"), cancellable = true)
    private void rdpl$cloudColor(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        DimensionDef def = rdpl$def();
        if (def != null && def.cloudColor() >= 0) { cir.setReturnValue(rdpl$color(def.cloudColor())); }
    }

    @Inject(method = "getStarBrightness", at = @At("HEAD"), cancellable = true)
    private void rdpl$starBrightness(float partialTick, CallbackInfoReturnable<Float> cir) {
        DimensionDef def = rdpl$def();
        if (def != null && def.starBrightness() >= 0.0F) { cir.setReturnValue(def.starBrightness()); }
    }
}
