package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class) public abstract class MixinServerGamePacketListenerImpl {
    @Inject(method = "handleChatSessionUpdate", at = @At("HEAD"), cancellable = true) private void rdpl$keepNoSession(ServerboundChatSessionUpdatePacket packet, CallbackInfo ci) {
        if (Config.tweaks.privacy()) { ci.cancel(); }
    }
}
