package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcStationLabels;

import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Map;

@Mixin(value = PacketSimple.class, remap = false) public class MixinPacketSimpleStationLabel {
    @Redirect(method = "handleClientSide(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At(value = "FIELD", target = "Lmicdoodle8/mods/galacticraft/core/client/gui/screen/GuiCelestialSelection;spaceStationMap:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private void rdpl$stationLabels(GuiCelestialSelection gui, Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> stations) {
        GcStationLabels.apply(stations);
        gui.spaceStationMap = stations;
    }
}
