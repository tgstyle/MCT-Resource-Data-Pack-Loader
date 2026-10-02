package mctmods.resourcedatapackloader.content.rubic;

import mctmods.resourcedatapackloader.content.rubic.world.WorldSavedRubicData;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RubicStandDown {
    @SubscribeEvent public void onWorldAttachCapabilities(AttachCapabilitiesEvent<World> evt) {
        World world = evt.getObject();
        if (world.isRemote || !(world instanceof WorldServer)) { return; }
        WorldSavedRubicData saved = (WorldSavedRubicData) world.getPerWorldStorage().getOrLoadData(WorldSavedRubicData.class, "rdplRubicData");
        if (saved != null && saved.isRubicWorld) { RubicWorldControl.standDownForSavedWorld(); }
        if (RubicWorldControl.claims(world.provider.getDimension())) { RubicWorldControl.wanted(); }
    }
}
