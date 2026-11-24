package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.core.util.PacketUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import java.util.ArrayList;
import java.util.List;

public class CardMessage implements IMessage {
    public String title = "";
    public List<String> lines = new ArrayList<>();
    public ItemStack icon = ItemStack.EMPTY;
    public String image = "";
    public int background;
    public int text;
    public int ticks;
    public boolean center;
    public boolean panel = true;
    public String font = "";

    public CardMessage() {}

    public CardMessage(String title, List<String> lines, ItemStack icon, String image, int background, int text, int ticks, boolean center, boolean panel, String font) {
        this.title = title;
        this.lines = lines;
        this.icon = icon;
        this.image = image;
        this.background = background;
        this.text = text;
        this.ticks = ticks;
        this.center = center;
        this.panel = panel;
        this.font = font;
    }

    @Override public void fromBytes(ByteBuf buf) {
        title = ByteBufUtils.readUTF8String(buf);
        lines = PacketUtils.readLines(buf);
        icon = ByteBufUtils.readItemStack(buf);
        image = ByteBufUtils.readUTF8String(buf);
        background = buf.readInt();
        text = buf.readInt();
        ticks = buf.readInt();
        center = buf.readBoolean();
        panel = buf.readBoolean();
        font = ByteBufUtils.readUTF8String(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, title);
        PacketUtils.writeLines(buf, lines);
        ByteBufUtils.writeItemStack(buf, icon);
        ByteBufUtils.writeUTF8String(buf, image);
        buf.writeInt(background);
        buf.writeInt(text);
        buf.writeInt(ticks);
        buf.writeBoolean(center);
        buf.writeBoolean(panel);
        ByteBufUtils.writeUTF8String(buf, font);
    }
}
