package mctmods.resourcedatapackloader.mixin.client.renderer.debug;

import mctmods.resourcedatapackloader.client.renderer.CubeChunkBorder;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.debug.DebugRendererChunkBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRendererChunkBorder.class) public class MixinDebugRenderChunkBorder {
    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true) private void rdpl$renderChunkBorder(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (((IRubicWorld) Minecraft.getMinecraft().world).rdpl$isRubicWorld()) {
            ci.cancel();
            CubeChunkBorder.render(Minecraft.getMinecraft().player, Tessellator.getInstance(), partialTicks);
        }
    }
}
