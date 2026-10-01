package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcCelestial;

import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiCelestialSelection.class, remap = false) public class MixinGuiCelestialSelectionStationHome {
    @Redirect(method = "getSatelliteParentID(Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;)I", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;getParentPlanet()Lmicdoodle8/mods/galacticraft/api/galaxies/Planet;"))
    private Planet rdpl$stationHome(Satellite station) { return GcCelestial.stationHome(station); }
}
