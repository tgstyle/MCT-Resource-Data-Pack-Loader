package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentSeasonTint;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.PacketUtils;

import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EnvironmentsMessage implements IMessage {
    private final Map<Integer, List<String>> active = new LinkedHashMap<>();
    private boolean whole;

    public EnvironmentsMessage() {}

    public EnvironmentsMessage(Map<Integer, List<String>> active, boolean whole) {
        this.active.putAll(active);
        this.whole = whole;
    }

    @Override public void fromBytes(ByteBuf buf) {
        whole = buf.readBoolean();
        int count = buf.readInt();
        for (int index = 0; index < count; index++) {
            int dimension = buf.readInt();
            int size = buf.readInt();
            active.put(dimension, PacketUtils.readStrings(buf, size));
        }
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeBoolean(whole);
        buf.writeInt(active.size());
        for (Map.Entry<Integer, List<String>> entry : active.entrySet()) {
            buf.writeInt(entry.getKey());
            buf.writeInt(entry.getValue().size());
            PacketUtils.writeStrings(buf, entry.getValue(), entry.getValue().size());
        }
    }

    public static class Handler extends AbstractClientMessageHandler<EnvironmentsMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, EnvironmentsMessage message, MessageContext ctx) {
            ContentEnvironments.receive(message.active, message.whole);
            ContentEnvironments.refresh(world);
            ContentSeasonTint.changed();
        }
    }
}
