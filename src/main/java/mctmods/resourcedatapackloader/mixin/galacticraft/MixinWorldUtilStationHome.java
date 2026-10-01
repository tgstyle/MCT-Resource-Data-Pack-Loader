package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcCelestial;

import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldUtil.class, remap = false) public class MixinWorldUtilStationHome {
    @Redirect(method = "bindSpaceStationToNewDimension(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayerMP;I)Lmicdoodle8/mods/galacticraft/core/dimension/SpaceStationWorldData;", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;getParentPlanet()Lmicdoodle8/mods/galacticraft/api/galaxies/Planet;"))
    private static Planet rdpl$stationHome(Satellite station) { return GcCelestial.stationHome(station); }
}
