package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.util.ArrayList;
import java.util.List;

public class MessageCard implements IMessage {
    public String title = "";
    public List<String> lines = new ArrayList<>();
    public ItemStack icon = ItemStack.EMPTY;
    public String image = "";
    public int background;
    public int text;
    public int ticks;

    public MessageCard() {}

    public MessageCard(String title, List<String> lines, ItemStack icon, String image, int background, int text, int ticks) {
        this.title = title;
        this.lines = lines;
        this.icon = icon;
        this.image = image;
        this.background = background;
        this.text = text;
        this.ticks = ticks;
    }

    @Override public void fromBytes(ByteBuf buf) {
        title = ByteBufUtils.readUTF8String(buf);
        int count = buf.readByte();
        lines = new ArrayList<>();
        for (int i = 0; i < count; i++) { lines.add(ByteBufUtils.readUTF8String(buf)); }
        icon = ByteBufUtils.readItemStack(buf);
        image = ByteBufUtils.readUTF8String(buf);
        background = buf.readInt();
        text = buf.readInt();
        ticks = buf.readInt();
    }

    @Override public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, title);
        buf.writeByte(Math.min(lines.size(), 16));
        for (int i = 0; i < Math.min(lines.size(), 16); i++) { ByteBufUtils.writeUTF8String(buf, lines.get(i)); }
        ByteBufUtils.writeItemStack(buf, icon);
        ByteBufUtils.writeUTF8String(buf, image);
        buf.writeInt(background);
        buf.writeInt(text);
        buf.writeInt(ticks);
    }

    public static class Idle implements IMessageHandler<MessageCard, IMessage> {
        @Override public IMessage onMessage(MessageCard message, MessageContext ctx) { return null; }
    }
}
