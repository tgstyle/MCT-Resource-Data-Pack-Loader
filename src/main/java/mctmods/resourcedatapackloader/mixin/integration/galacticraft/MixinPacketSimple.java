package mctmods.resourcedatapackloader.mixin.integration.galacticraft;

import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcCelestial;
import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcStationLabels;

import java.util.Map;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PacketSimple.class, remap = false) public class MixinPacketSimple {
    @Redirect(method = "handleClientSide(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/api/galaxies/Satellite;getParentPlanet()Lmicdoodle8/mods/galacticraft/api/galaxies/Planet;")) private Planet rdpl$stationHome(Satellite station) { return GcCelestial.stationHome(station); }

    @Redirect(method = "handleClientSide(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At(value = "FIELD", target = "Lmicdoodle8/mods/galacticraft/core/client/gui/screen/GuiCelestialSelection;spaceStationMap:Ljava/util/Map;", opcode = Opcodes.PUTFIELD)) private void rdpl$stationLabels(GuiCelestialSelection gui, Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> stations) {
        GcStationLabels.apply(stations);
        gui.spaceStationMap = stations;
    }
}
