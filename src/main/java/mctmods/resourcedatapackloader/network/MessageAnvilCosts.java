package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.AnvilCosts;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAnvilCosts implements IMessage {
    private int cap;
    private int[] paid = new int[0];
    private int[] lapis = new int[0];
    private boolean opening;

    public MessageAnvilCosts() {}

    public MessageAnvilCosts(int cap, int[] paid, int[] lapis, boolean opening) {
        this.cap = cap;
        this.paid = paid;
        this.lapis = lapis;
        this.opening = opening;
    }

    @Override public void fromBytes(ByteBuf buf) {
        cap = buf.readInt();
        paid = readSlots(buf);
        lapis = readSlots(buf);
        opening = buf.readBoolean();
    }

    private static int[] readSlots(ByteBuf buf) {
        int[] values = new int[Math.min(buf.readByte(), AnvilCosts.SLOTS)];
        for (int slot = 0; slot < values.length; slot++) { values[slot] = buf.readInt(); }
        return values;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(cap);
        writeSlots(buf, paid);
        writeSlots(buf, lapis);
        buf.writeBoolean(opening);
    }

    private static void writeSlots(ByteBuf buf, int[] values) {
        buf.writeByte(values.length);
        for (int each : values) { buf.writeInt(each); }
    }

    public static class Handler extends AbstractClientMessageHandler<MessageAnvilCosts> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageAnvilCosts message, MessageContext ctx) { AnvilCosts.show(message.cap, message.paid, message.lapis, message.opening); }
    }
}
