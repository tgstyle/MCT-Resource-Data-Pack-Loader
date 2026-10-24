package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageHeartColors implements IMessage {
    public static final int MOST = 32;
    public int[] colors = new int[0];

    public MessageHeartColors() {}

    public MessageHeartColors(int[] colors) { this.colors = colors; }

    @Override public void fromBytes(ByteBuf buf) {
        colors = new int[Math.min(buf.readByte(), MOST)];
        for (int i = 0; i < colors.length; i++) { colors[i] = buf.readInt(); }
    }

    @Override public void toBytes(ByteBuf buf) {
        int count = Math.min(colors.length, MOST);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeInt(colors[i]); }
    }
}
