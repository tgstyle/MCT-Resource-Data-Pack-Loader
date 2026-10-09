package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.network.MessageIntroLandMade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public class IntroLandMadeHandler extends AbstractClientMessageHandler<MessageIntroLandMade> {
    @Override public void handleClientMessage(World world, EntityPlayer player, MessageIntroLandMade message, MessageContext ctx) { GuiWorldIntro.landMade(); }
}
