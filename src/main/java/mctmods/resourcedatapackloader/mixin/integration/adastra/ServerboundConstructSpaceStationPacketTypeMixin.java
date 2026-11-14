package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraGates;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.network.messages.ServerboundConstructSpaceStationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "earth.terrarium.adastra.common.network.messages.ServerboundConstructSpaceStationPacket$Type", remap = false) public abstract class ServerboundConstructSpaceStationPacketTypeMixin {
    @Inject(method = "lambda$handle$0(Learth/terrarium/adastra/common/network/messages/ServerboundConstructSpaceStationPacket;Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Learth/terrarium/adastra/common/recipes/SpaceStationRecipe;consumeIngredients(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;)V"), cancellable = true) private static void rdpl$gatedStation(ServerboundConstructSpaceStationPacket packet, Player player, CallbackInfo ci) {
        Planet planet = PlanetApi.API.getPlanet(packet.dimension());
        if (planet != null && AdAstraGates.refused((ServerPlayer) player, planet.orbitIfPresent())) { ci.cancel(); }
    }
}
