package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.util.UUID;

public class MessageBossBackground implements IMessage {
    public UUID bar;
    public String texture = "";
    public int width;
    public int height;
    public int x;
    public int y;

    public MessageBossBackground() {}

    public MessageBossBackground(UUID bar, String texture, int width, int height, int x, int y) {
        this.bar = bar;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    @Override public void fromBytes(ByteBuf buf) {
        bar = new UUID(buf.readLong(), buf.readLong());
        texture = ByteBufUtils.readUTF8String(buf);
        width = buf.readInt();
        height = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeLong(bar.getMostSignificantBits());
        buf.writeLong(bar.getLeastSignificantBits());
        ByteBufUtils.writeUTF8String(buf, texture);
        buf.writeInt(width);
        buf.writeInt(height);
        buf.writeInt(x);
        buf.writeInt(y);
    }
}
