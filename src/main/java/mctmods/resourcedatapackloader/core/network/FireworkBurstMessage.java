package mctmods.resourcedatapackloader.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class FireworkBurstMessage implements IMessage {
    public int itemId;
    public double x;
    public double y;
    public double z;
    public float[] velocities = new float[0];

    public FireworkBurstMessage() {}

    public FireworkBurstMessage(int itemId, double x, double y, double z, float[] velocities) {
        this.itemId = itemId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocities = velocities;
    }

    @Override public void fromBytes(ByteBuf buf) {
        itemId = buf.readInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        velocities = new float[buf.readInt()];
        for (int i = 0; i < velocities.length; i++) { velocities[i] = buf.readFloat(); }
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(itemId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(velocities.length);
        for (float velocity : velocities) { buf.writeFloat(velocity); }
    }
}
