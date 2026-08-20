package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CubeProviderClient;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUnloadColumn implements IMessage {
    private ChunkPos chunkPos;

    public MessageUnloadColumn() {}

    public MessageUnloadColumn(ChunkPos chunkPos) { this.chunkPos = chunkPos; }

    @Override public void fromBytes(ByteBuf buf) { this.chunkPos = new ChunkPos(buf.readInt(), buf.readInt()); }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(chunkPos.x);
        buf.writeInt(chunkPos.z);
    }

    ChunkPos getColumnPos() { return Preconditions.checkNotNull(chunkPos); }

    public static class Handler extends AbstractClientMessageHandler<MessageUnloadColumn> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageUnloadColumn message, MessageContext ctx) {
            IRubicWorld worldClient = (IRubicWorld) world;
            if (!worldClient.rdpl$isRubicWorld()) { return; }
            CubeProviderClient cubeCache = (CubeProviderClient) worldClient.rdpl$getCubeCache();
            ChunkPos chunkPos = message.getColumnPos();
            cubeCache.unloadChunk(chunkPos.x, chunkPos.z);
        }
    }
}
