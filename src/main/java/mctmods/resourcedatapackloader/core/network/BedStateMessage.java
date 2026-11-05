package mctmods.resourcedatapackloader.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class BedStateMessage implements IMessage {
    public static final int HIDDEN = 0;
    public static final int NONE = 1;
    public static final int CURRENT = 2;
    public static final int OTHER = 3;
    public static final int UPDATED = 4;
    public int state;

    public BedStateMessage() {}

    public BedStateMessage(int state) { this.state = state; }

    @Override public void fromBytes(ByteBuf buf) { state = buf.readByte(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeByte(state); }
}
