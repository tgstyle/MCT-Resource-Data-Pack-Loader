package mctmods.resourcedatapackloader.core.network.world;

import mctmods.resourcedatapackloader.client.util.CubeProviderClient;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnloadColumnMessage implements IMessage {
    private ChunkPos chunkPos;

    public UnloadColumnMessage() {}

    public UnloadColumnMessage(ChunkPos chunkPos) { this.chunkPos = chunkPos; }

    @Override public void fromBytes(ByteBuf buf) { this.chunkPos = new ChunkPos(buf.readInt(), buf.readInt()); }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(chunkPos.x);
        buf.writeInt(chunkPos.z);
    }

    ChunkPos getColumnPos() { return Preconditions.checkNotNull(chunkPos); }

    public static class Handler extends AbstractClientMessageHandler<UnloadColumnMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, UnloadColumnMessage message, MessageContext ctx) {
            CubeProviderClient cubeCache = cubeCache(world);
            if (cubeCache == null) { return; }
            ChunkPos chunkPos = message.getColumnPos();
            cubeCache.unloadChunk(chunkPos.x, chunkPos.z);
        }
    }
}
