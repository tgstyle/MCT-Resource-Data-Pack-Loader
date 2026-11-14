package mctmods.resourcedatapackloader.mixin.integration.adastra;

import mctmods.resourcedatapackloader.core.integration.adastra.AdAstraGates;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.handlers.SpaceStationHandler;
import earth.terrarium.adastra.common.handlers.base.SpaceStation;
import earth.terrarium.adastra.common.menus.base.PlanetsMenuProvider;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlanetsMenuProvider.class, remap = false) public abstract class PlanetsMenuProviderMixin {
    @Redirect(method = "writeExtraData(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "FIELD", target = "Learth/terrarium/adastra/common/config/AdAstraConfig;disabledPlanets:Ljava/lang/String;", opcode = Opcodes.GETSTATIC)) private String rdpl$gatedPlanets(ServerPlayer player, FriendlyByteBuf buffer) { return AdAstraGates.hidden(player, AdAstraConfig.disabledPlanets); }

    @Redirect(method = "lambda$writeExtraData$2(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceKey;)V", at = @At(value = "INVOKE", target = "Learth/terrarium/adastra/common/handlers/SpaceStationHandler;getAllSpaceStations(Lnet/minecraft/server/level/ServerLevel;)Ljava/util/Map;")) private static Map<UUID, Set<SpaceStation>> rdpl$gatedStations(ServerLevel level, FriendlyByteBuf buffer, ServerPlayer player, ResourceKey<Level> dimension) { return AdAstraGates.hides(player, dimension.location()) ? Map.of() : SpaceStationHandler.getAllSpaceStations(level); }
}
