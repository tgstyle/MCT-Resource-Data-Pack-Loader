package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.network.MessageIntroPlay;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public class IntroPlayHandler implements IMessageHandler<MessageIntroPlay, IMessage> {
    @Override public IMessage onMessage(MessageIntroPlay message, MessageContext ctx) {
        boolean held = message.held;
        Minecraft.getMinecraft().addScheduledTask(() -> GuiWorldIntro.open(held));
        return null;
    }
}
