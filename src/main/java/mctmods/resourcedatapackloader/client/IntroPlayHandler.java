package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.network.MessageIntroPlay;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public class IntroPlayHandler extends AbstractClientMessageHandler<MessageIntroPlay> {
    @Override public void handleClientMessage(World world, EntityPlayer player, MessageIntroPlay message, MessageContext ctx) { GuiWorldIntro.open(message.held); }
}
