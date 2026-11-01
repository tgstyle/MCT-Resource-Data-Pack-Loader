package mctmods.resourcedatapackloader.mixin.server.network;

import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;

import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class) public abstract class ServerGamePacketListenerImplMixin {
    @Inject(method = "handleChatSessionUpdate", at = @At("HEAD"), cancellable = true) private void rdpl$keepNoSession(ServerboundChatSessionUpdatePacket packet, CallbackInfo ci) { if (ContentServer.privacy()) { ci.cancel(); } }
}
