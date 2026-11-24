package mctmods.resourcedatapackloader.core.network;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NoteMessage implements IMessage {
    public String said = "";
    public boolean backdrop = true;
    public String font = "";

    public NoteMessage() {}

    public NoteMessage(String said, boolean backdrop, String font) {
        this.said = Strings.nullToEmpty(said);
        this.backdrop = backdrop;
        this.font = font;
    }

    @Override public void fromBytes(ByteBuf buf) {
        said = ByteBufUtils.readUTF8String(buf);
        backdrop = buf.readBoolean();
        font = ByteBufUtils.readUTF8String(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, said);
        buf.writeBoolean(backdrop);
        ByteBufUtils.writeUTF8String(buf, font);
    }
}
