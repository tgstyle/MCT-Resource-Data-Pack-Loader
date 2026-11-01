package mctmods.resourcedatapackloader.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class IntroPlayMessage implements IMessage {
    public boolean held;

    public IntroPlayMessage() {}

    public IntroPlayMessage(boolean held) { this.held = held; }

    @Override public void fromBytes(ByteBuf buf) { held = buf.readBoolean(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeBoolean(held); }
}
