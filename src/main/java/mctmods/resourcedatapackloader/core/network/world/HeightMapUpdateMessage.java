package mctmods.resourcedatapackloader.core.network.world;

import mctmods.resourcedatapackloader.client.util.CubeProviderClient;
import mctmods.resourcedatapackloader.common.rubic.lighting.interfaces.ILightingManager;
import mctmods.resourcedatapackloader.common.rubic.world.ClientHeightMap;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.address.AddressTools;

import com.google.common.base.Preconditions;
import gnu.trove.list.TByteList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;
import io.netty.buffer.ByteBuf;
import java.util.BitSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HeightMapUpdateMessage implements IMessage {
    private ChunkPos chunk;
    private TByteList updates;
    private TIntList heights;

    public HeightMapUpdateMessage() {}

    public HeightMapUpdateMessage(BitSet updates, Chunk chunk) {
        this.chunk = chunk.getPos();
        this.updates = new TByteArrayList();
        this.heights = new TIntArrayList();
        for (int i = updates.nextSetBit(0); i >= 0; i = updates.nextSetBit(i + 1)) {
            this.updates.add((byte) i);
            this.heights.add(((IColumnInternal) chunk).getTopYWithStaging(AddressTools.getLocalX(i), AddressTools.getLocalZ(i)));
        }
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.chunk = new ChunkPos(buf.readInt(), buf.readInt());
        int size = buf.readUnsignedShort();
        this.updates = new TByteArrayList(size);
        this.heights = new TIntArrayList(size);
        for (int i = 0; i < size; i++) {
            this.updates.add(buf.readByte());
            this.heights.add(ByteBufUtils.readVarInt(buf, 5));
        }
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(this.chunk.x);
        buf.writeInt(this.chunk.z);
        buf.writeShort(this.updates.size());
        for (int i = 0; i < this.updates.size(); i++) {
            buf.writeByte(this.updates.get(i) & 0xFF);
            ByteBufUtils.writeVarInt(buf, this.heights.get(i), 5);
        }
    }

    ChunkPos getColumnPos() { return Preconditions.checkNotNull(this.chunk); }

    TByteList getUpdates() { return this.updates; }

    TIntList getHeights() { return this.heights; }

    public static class Handler extends AbstractClientMessageHandler<HeightMapUpdateMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, HeightMapUpdateMessage message, MessageContext ctx) {
            IRubicWorldInternal.IClient worldClient = (IRubicWorldInternal.IClient) world;
            CubeProviderClient cubeCache = cubeCache(world);
            if (cubeCache == null) { return; }
            int columnX = message.getColumnPos().x;
            int columnZ = message.getColumnPos().z;
            Chunk column = cubeCache.provideColumn(columnX, columnZ);
            if (column instanceof EmptyChunk) {
                ContentLog.LOGGER.error("Ignored block update to blank column {}", message.getColumnPos());
                return;
            }

            ClientHeightMap index = (ClientHeightMap) ((IColumn) column).getOpacityIndex();
            ILightingManager lm = worldClient.rdpl$getLightingManager();
            int size = message.getUpdates().size();

            for (int i = 0; i < size; i++) {
                int packed = message.getUpdates().get(i) & 0xFF;
                int x = AddressTools.getLocalX(packed);
                int z = AddressTools.getLocalZ(packed);
                int height = message.getHeights().get(i);
                int oldHeight = index.getTopBlockY(x, z);
                index.setHeight(x, z, height);
                lm.updateLightBetween(column, x, oldHeight, height, z);
            }
        }
    }
}
