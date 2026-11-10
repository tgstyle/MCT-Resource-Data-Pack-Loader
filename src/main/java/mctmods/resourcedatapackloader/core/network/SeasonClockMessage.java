package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.environment.SeasonClock;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SeasonClockMessage implements IMessage {
    private long ticks;

    public SeasonClockMessage() {}

    public SeasonClockMessage(long ticks) { this.ticks = ticks; }

    @Override public void fromBytes(ByteBuf buf) { ticks = buf.readLong(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeLong(ticks); }

    public static class Handler extends AbstractClientMessageHandler<SeasonClockMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, SeasonClockMessage message, MessageContext ctx) { SeasonClock.received(message.ticks); }
    }
}
