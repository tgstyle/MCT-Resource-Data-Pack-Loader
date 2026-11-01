package mctmods.resourcedatapackloader.mixin.integration.galacticraft;

import mctmods.resourcedatapackloader.common.gates.ContentGates;
import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcCelestial;

import java.util.List;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorldUtil.class, remap = false) public class MixinWorldUtil {
    @Inject(method = "getPossibleDimensionsForSpaceshipTier(ILnet/minecraft/entity/player/EntityPlayerMP;)Ljava/util/List;", at = @At("RETURN")) private static void rdpl$gatedPlanets(int tier, EntityPlayerMP playerBase, CallbackInfoReturnable<List<Integer>> cir) {
        if (playerBase == null) { return; }
        cir.getReturnValue().removeIf(dimension -> ContentGates.barsDimension(playerBase, dimension));
    }

    @Redirect(method = "bindSpaceStationToNewDimension(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayerMP;I)Lmicdoodle8/mods/galacticraft/core/dimension/SpaceStationWorldData;", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;getParentPlanet()Lmicdoodle8/mods/galacticraft/api/galaxies/Planet;")) private static Planet rdpl$stationHome(Satellite station) { return GcCelestial.stationHome(station); }
}
