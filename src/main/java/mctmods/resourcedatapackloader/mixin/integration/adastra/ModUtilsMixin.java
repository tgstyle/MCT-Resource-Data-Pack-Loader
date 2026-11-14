package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraGates;
import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraHeights;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.utils.ModUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModUtils.class, remap = false) public abstract class ModUtilsMixin {
    @Inject(method = "canTeleportToPlanet(Lnet/minecraft/world/entity/player/Player;Learth/terrarium/adastra/api/planets/Planet;)Z", at = @At("RETURN"), cancellable = true) private static void rdpl$gatedLanding(Player player, Planet targetPlanet, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && player instanceof ServerPlayer traveller && AdAstraGates.refused(traveller, targetPlanet.dimension())) { cir.setReturnValue(false); }
    }

    @Redirect(method = "canTeleportToPlanet(Lnet/minecraft/world/entity/player/Player;Learth/terrarium/adastra/api/planets/Planet;)Z", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/common/config/AdAstraConfig;atmosphereLeave:I", opcode = Opcodes.GETSTATIC)) private static int rdpl$exitHeight(Player player, Planet targetPlanet) { return AdAstraHeights.exit(player.level()); }

    @Inject(method = "teleportToDimension(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At("HEAD")) private static void rdpl$orbitFall(Entity entity, ServerLevel level, CallbackInfoReturnable<Entity> cir) { AdAstraHeights.orbitFall(entity, level); }
}
