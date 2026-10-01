package mctmods.resourcedatapackloader.mixin.asmodeuscore;

import mctmods.resourcedatapackloader.util.compat.GcStationLabels;

import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection;
import asmodeuscore.core.network.packet.ACPacketSimple;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Map;

@Mixin(value = ACPacketSimple.class, remap = false) public class MixinACPacketSimpleStationLabel {
    @Redirect(method = "handleClientSide(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At(value = "FIELD", target = "Lasmodeuscore/core/astronomy/gui/screen/NewGuiCelestialSelection;spaceStationMap:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private void rdpl$stationLabels(NewGuiCelestialSelection gui, Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> stations) {
        GcStationLabels.apply(stations);
        gui.spaceStationMap = stations;
    }
}
