package mctmods.resourcedatapackloader.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class WeightMessage implements IMessage {
    public int total;
    public int limit;
    public int tier = -1;
    public boolean exempt;

    public WeightMessage() {}

    public WeightMessage(int total, int limit, int tier, boolean exempt) {
        this.total = total;
        this.limit = limit;
        this.tier = tier;
        this.exempt = exempt;
    }

    @Override public void fromBytes(ByteBuf buf) {
        total = buf.readInt();
        limit = buf.readInt();
        tier = buf.readInt();
        exempt = buf.readBoolean();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(total);
        buf.writeInt(limit);
        buf.writeInt(tier);
        buf.writeBoolean(exempt);
    }
}
