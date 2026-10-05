package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.MessageIntroLandMade;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public class IntroLandMadeHandler implements IMessageHandler<MessageIntroLandMade, IMessage> {
    @Override public IMessage onMessage(MessageIntroLandMade message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(GuiWorldIntro::landMade);
        return null;
    }
}
