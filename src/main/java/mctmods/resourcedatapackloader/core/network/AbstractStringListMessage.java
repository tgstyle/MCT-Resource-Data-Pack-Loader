package mctmods.resourcedatapackloader.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractStringListMessage implements IMessage {
    public static final int MOST = 256;
    protected final List<String> values = new ArrayList<>();

    protected AbstractStringListMessage() {}

    protected AbstractStringListMessage(Collection<String> values) { this.values.addAll(values); }

    @Override public void fromBytes(ByteBuf buf) {
        int count = Math.min(buf.readShort(), MOST);
        values.clear();
        for (int i = 0; i < count; i++) { values.add(ByteBufUtils.readUTF8String(buf)); }
    }

    @Override public void toBytes(ByteBuf buf) {
        int count = Math.min(values.size(), MOST);
        buf.writeShort(count);
        for (int i = 0; i < count; i++) { ByteBufUtils.writeUTF8String(buf, values.get(i)); }
    }
}
