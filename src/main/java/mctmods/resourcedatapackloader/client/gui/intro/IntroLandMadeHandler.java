package mctmods.resourcedatapackloader.client.gui.intro;

import mctmods.resourcedatapackloader.core.network.IntroLandMadeMessage;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public class IntroLandMadeHandler extends AbstractClientMessageHandler<IntroLandMadeMessage> {
    @Override public void handleClientMessage(World world, EntityPlayer player, IntroLandMadeMessage message, MessageContext ctx) { GuiWorldIntro.landMade(); }
}
