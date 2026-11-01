package mctmods.resourcedatapackloader.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GatesMessage implements IMessage {
    public static final int MOST = 256;
    public List<String> gates = new ArrayList<>();

    public GatesMessage() {}

    public GatesMessage(Collection<String> gates) { this.gates = new ArrayList<>(gates); }

    @Override public void fromBytes(ByteBuf buf) {
        int count = Math.min(buf.readShort(), MOST);
        gates = new ArrayList<>(count);
        for (int i = 0; i < count; i++) { gates.add(ByteBufUtils.readUTF8String(buf)); }
    }

    @Override public void toBytes(ByteBuf buf) {
        int count = Math.min(gates.size(), MOST);
        buf.writeShort(count);
        for (int i = 0; i < count; i++) { ByteBufUtils.writeUTF8String(buf, gates.get(i)); }
    }
}
