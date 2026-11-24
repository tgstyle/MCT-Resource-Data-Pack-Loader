package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.core.util.PacketUtils;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractStringListMessage implements IMessage {
    public static final int MOST = 4096;
    protected final List<String> values = new ArrayList<>();

    protected AbstractStringListMessage() {}

    protected AbstractStringListMessage(Collection<String> values) { this.values.addAll(values); }

    @Override public void fromBytes(ByteBuf buf) {
        int count = Math.min(buf.readShort(), MOST);
        values.clear();
        values.addAll(PacketUtils.readStrings(buf, count));
    }

    @Override public void toBytes(ByteBuf buf) {
        int count = Math.min(values.size(), MOST);
        buf.writeShort(count);
        PacketUtils.writeStrings(buf, values, count);
    }
}
