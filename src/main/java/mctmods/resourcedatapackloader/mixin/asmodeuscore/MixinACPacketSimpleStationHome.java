package mctmods.resourcedatapackloader.mixin.asmodeuscore;

import mctmods.resourcedatapackloader.util.compat.GcCelestial;

import asmodeuscore.core.network.packet.ACPacketSimple;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ACPacketSimple.class, remap = false) public class MixinACPacketSimpleStationHome {
    @Redirect(method = "handleClientSide(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;getParentPlanet()Lmicdoodle8/mods/galacticraft/api/galaxies/Planet;"))
    private Planet rdpl$stationHome(Satellite station) { return GcCelestial.stationHome(station); }
}
