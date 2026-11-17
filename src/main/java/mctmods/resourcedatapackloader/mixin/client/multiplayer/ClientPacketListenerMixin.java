package mctmods.resourcedatapackloader.mixin.client.multiplayer;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPacketListener.class) public abstract class ClientPacketListenerMixin {
    @ModifyArg(method = "handleExplosion(Lnet/minecraft/network/protocol/game/ClientboundExplodePacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 6) private float rdpl$ownBlastPitch(float pitch, @Local(argsOnly = true, name = "packet") ClientboundExplodePacket packet) { return ContentEntities.packExplodeSound(packet.explosionSound().value()) ? 1.0F : pitch; }
}
