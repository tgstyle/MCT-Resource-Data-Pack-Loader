package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.PacketUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AnvilCostsMessage implements IMessage {
    private int cap;
    private int[] paid = new int[0];
    private int[] lapis = new int[0];
    private boolean opening;

    public AnvilCostsMessage() {}

    public AnvilCostsMessage(int cap, int[] paid, int[] lapis, boolean opening) {
        this.cap = cap;
        this.paid = paid;
        this.lapis = lapis;
        this.opening = opening;
    }

    @Override public void fromBytes(ByteBuf buf) {
        cap = buf.readInt();
        paid = PacketUtils.readInts(buf, AnvilCosts.SLOTS);
        lapis = PacketUtils.readInts(buf, AnvilCosts.SLOTS);
        opening = buf.readBoolean();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(cap);
        PacketUtils.writeInts(buf, paid);
        PacketUtils.writeInts(buf, lapis);
        buf.writeBoolean(opening);
    }

    public static class Handler extends AbstractClientMessageHandler<AnvilCostsMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, AnvilCostsMessage message, MessageContext ctx) { AnvilCosts.show(message.cap, message.paid, message.lapis, message.opening); }
    }
}
