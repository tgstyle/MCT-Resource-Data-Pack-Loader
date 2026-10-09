package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentFogSampler;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.Rgb;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.biome.Biome;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class) public abstract class MixinFogRendererFluid {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getWaterFogColor()I")) private static int rdpl$waterFog(Biome biome) { return ContentFogSampler.waterFog(biome.getWaterFogColor()); }

    @Inject(method = "setupColor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/FogRenderer;fogBlue:F", opcode = Opcodes.PUTSTATIC, ordinal = 1, shift = At.Shift.AFTER)) private static void rdpl$lavaFog(CallbackInfo ci) {
        int color = ContentFogSampler.lavaFog();
        if (color == SkyLookDef.UNSET) { return; }
        fogRed = Rgb.red(color);
        fogGreen = Rgb.green(color);
        fogBlue = Rgb.blue(color);
    }
}
