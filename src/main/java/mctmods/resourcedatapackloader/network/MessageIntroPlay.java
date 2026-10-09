package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageIntroPlay implements IMessage {
    public boolean held;

    public MessageIntroPlay() {}

    public MessageIntroPlay(boolean held) { this.held = held; }

    @Override public void fromBytes(ByteBuf buf) { held = buf.readBoolean(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeBoolean(held); }
}
