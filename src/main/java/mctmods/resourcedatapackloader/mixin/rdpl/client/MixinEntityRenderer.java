package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class) public class MixinEntityRenderer {
    @Shadow @Final private Minecraft mc;

    @Redirect(method = {"updateRenderer", "setupCameraTransform", "renderWorldPass", "updateFogColor"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;renderDistanceChunks:I", opcode = Opcodes.GETFIELD)) private int getRenderDistance(GameSettings settings) {
        if (!((IRubicWorld) mc.world).rdpl$isRubicWorld()) { return settings.renderDistanceChunks; }
        return Math.max(settings.renderDistanceChunks, Config.client.verticalCubeLoadDistance);
    }

    @Inject(method = "createWorldIcon", at = @At("HEAD"), cancellable = true) private void rdpl$iconAfterTheFog(CallbackInfo ci) {
        if (HoldView.showing()) { ci.cancel(); }
    }
}
