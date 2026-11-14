package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraHeights;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "earth.terrarium.adastra.common.network.messages.ServerboundLandPacket$Type", remap = false) public abstract class ServerboundLandPacketMixin {
    @SuppressWarnings("target") @ModifyArg(method = "lambda$handle$0(Learth/terrarium/adastra/common/network/messages/ServerboundLandPacket;Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Learth/terrarium/adastra/common/utils/ModUtils;land(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;)V"), index = 2) private static Vec3 rdpl$landingHeight(ServerPlayer player, ServerLevel level, Vec3 pos) { return new Vec3(pos.x, AdAstraHeights.landing(level), pos.z); }
}
