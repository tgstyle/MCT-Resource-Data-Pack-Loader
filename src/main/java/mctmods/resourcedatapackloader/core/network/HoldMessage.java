package mctmods.resourcedatapackloader.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class HoldMessage implements IMessage {
    public boolean held;
    public String warning = "";
    public boolean fog;
    public boolean backdrop = true;
    public String font = "";

    public HoldMessage() {}

    public HoldMessage(boolean held, String warning, boolean fog, boolean backdrop, String font) {
        this.held = held;
        this.warning = warning == null ? "" : warning;
        this.fog = fog;
        this.backdrop = backdrop;
        this.font = font;
    }

    @Override public void fromBytes(ByteBuf buf) {
        held = buf.readBoolean();
        warning = ByteBufUtils.readUTF8String(buf);
        fog = buf.readBoolean();
        backdrop = buf.readBoolean();
        font = ByteBufUtils.readUTF8String(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeBoolean(held);
        ByteBufUtils.writeUTF8String(buf, warning);
        buf.writeBoolean(fog);
        buf.writeBoolean(backdrop);
        ByteBufUtils.writeUTF8String(buf, font);
    }
}
