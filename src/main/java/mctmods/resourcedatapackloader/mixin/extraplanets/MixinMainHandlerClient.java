package mctmods.resourcedatapackloader.mixin.extraplanets;

import mctmods.resourcedatapackloader.util.compat.GcWorldProvider;

import com.mjr.extraplanets.Config;
import com.mjr.extraplanets.client.handlers.MainHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MainHandlerClient.class, remap = false) public abstract class MixinMainHandlerClient {
    @Shadow public abstract void showPressureHUD(int amount);

    @Inject(method = "onRenderTick", at = @At(value = "FIELD", target = "Lcom/mjr/extraplanets/Config;HIDE_RADIATION_PRESSURE_HUD:Z", opcode = Opcodes.GETSTATIC, ordinal = 0))
    private void rdpl$packPressure(RenderTickEvent event, CallbackInfo ci) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (!Config.PRESSURE || player == null || !(player.world.provider instanceof GcWorldProvider)) { return; }
        int pressure = ((GcWorldProvider) player.world.provider).extraPlanetsPressure();
        if (pressure > 0 || pressure == 0 && !Config.HIDE_RADIATION_PRESSURE_HUD) { showPressureHUD(pressure); }
    }
}
