package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.core.util.PacketUtils;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class HeartColorsMessage implements IMessage {
    public static final int MOST = 32;
    public int[] colors = new int[0];

    public HeartColorsMessage() {}

    public HeartColorsMessage(int[] colors) { this.colors = colors; }

    @Override public void fromBytes(ByteBuf buf) {
        colors = PacketUtils.readInts(buf, MOST);
    }

    @Override public void toBytes(ByteBuf buf) { PacketUtils.writeInts(buf, colors, MOST); }
}
