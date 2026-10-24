package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.util.ArrayList;
import java.util.List;

public class MessageTalk implements IMessage {
    public int npc;
    public boolean end;
    public String title = "";
    public List<String> lines = new ArrayList<>();
    public List<String> replies = new ArrayList<>();
    public ItemStack icon = ItemStack.EMPTY;
    public String image = "";
    public int background;
    public boolean panel = true;
    public String font = "";

    public MessageTalk() {}

    public MessageTalk(int npc, String title, List<String> lines, List<String> replies, ItemStack icon, String image, int background, boolean panel, String font) {
        this.npc = npc;
        this.title = title;
        this.lines = lines;
        this.replies = replies;
        this.icon = icon;
        this.image = image;
        this.background = background;
        this.panel = panel;
        this.font = font;
    }

    public static MessageTalk ending() {
        MessageTalk message = new MessageTalk();
        message.end = true;
        return message;
    }

    @Override public void fromBytes(ByteBuf buf) {
        npc = buf.readInt();
        end = buf.readBoolean();
        title = ByteBufUtils.readUTF8String(buf);
        lines = strings(buf);
        replies = strings(buf);
        icon = ByteBufUtils.readItemStack(buf);
        image = ByteBufUtils.readUTF8String(buf);
        background = buf.readInt();
        panel = buf.readBoolean();
        font = ByteBufUtils.readUTF8String(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(npc);
        buf.writeBoolean(end);
        ByteBufUtils.writeUTF8String(buf, title);
        write(buf, lines);
        write(buf, replies);
        ByteBufUtils.writeItemStack(buf, icon);
        ByteBufUtils.writeUTF8String(buf, image);
        buf.writeInt(background);
        buf.writeBoolean(panel);
        ByteBufUtils.writeUTF8String(buf, font);
    }

    private static List<String> strings(ByteBuf buf) {
        int count = buf.readByte();
        List<String> read = new ArrayList<>();
        for (int i = 0; i < count; i++) { read.add(ByteBufUtils.readUTF8String(buf)); }
        return read;
    }

    private static void write(ByteBuf buf, List<String> strings) {
        int count = Math.min(strings.size(), 16);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { ByteBufUtils.writeUTF8String(buf, strings.get(i)); }
    }
}
