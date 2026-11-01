package mctmods.resourcedatapackloader.core.network.world;

import mctmods.resourcedatapackloader.client.util.CubeProviderClient;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ColumnMessage implements IMessage {
    private ChunkPos chunkPos;
    private byte[] data;

    public ColumnMessage() {}

    public ColumnMessage(Chunk column) {
        this.chunkPos = column.getPos();
        this.data = new byte[WorldEncoder.getEncodedSize(column)];
        PacketBuffer out = new PacketBuffer(WorldEncoder.createByteBufForWrite(this.data));
        WorldEncoder.encodeColumn(out, column);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.chunkPos = new ChunkPos(buf.readInt(), buf.readInt());
        this.data = new byte[buf.readInt()];
        buf.readBytes(this.data);
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(chunkPos.x);
        buf.writeInt(chunkPos.z);
        buf.writeInt(this.data.length);
        buf.writeBytes(this.data);
    }

    ChunkPos getChunkPos() { return chunkPos; }

    byte[] getData() { return data; }

    public static class Handler extends AbstractClientMessageHandler<ColumnMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, ColumnMessage packet, MessageContext ctx) {
            IRubicWorld worldClient = (IRubicWorld) world;
            CubeProviderClient cubeCache = (CubeProviderClient) worldClient.rdpl$getCubeCache();
            ChunkPos chunkPos = packet.getChunkPos();
            Chunk column = cubeCache.loadChunk(chunkPos.x, chunkPos.z);
            byte[] data = packet.getData();
            ByteBuf buf = WorldEncoder.createByteBufForRead(data);

            WorldEncoder.decodeColumn(new PacketBuffer(buf), column);
        }
    }
}
