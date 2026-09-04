package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CubeProviderClient;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.util.Bits;
import mctmods.resourcedatapackloader.util.CubePos;
import static mctmods.resourcedatapackloader.util.Coords.cubeToMinBlock;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import javax.annotation.Nullable;

public class MessageCubeSkyLightUpdates implements IMessage {
    private CubePos cube;
    private boolean isFullRelight;
    private byte[] data;

    public MessageCubeSkyLightUpdates() {}

    @Override public void fromBytes(ByteBuf buf) {
        this.cube = new CubePos(buf.readInt(), buf.readInt(), buf.readInt());
        this.isFullRelight = buf.readBoolean();
        boolean hasData = buf.readBoolean();
        if (hasData) {
            int size = ByteBufUtils.readVarInt(buf, 3);
            this.data = new byte[size];
            buf.readBytes(this.data);
        }
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(this.cube.getX());
        buf.writeInt(this.cube.getY());
        buf.writeInt(this.cube.getZ());
        buf.writeBoolean(this.isFullRelight);
        buf.writeBoolean(this.data != null);
        if (this.data != null) {
            ByteBufUtils.writeVarInt(buf, this.data.length, 3);
            buf.writeBytes(this.data);
        }
    }

    CubePos getCubePos() { return cube; }

    boolean isFullRelight() { return isFullRelight; }

    @Nullable byte[] getData() { return data; }

    public int updateCount() { return data.length / 2; }

    public static class Handler extends AbstractClientMessageHandler<MessageCubeSkyLightUpdates> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageCubeSkyLightUpdates message, MessageContext ctx) {
            WorldClient worldClient = (WorldClient) world;
            if (!(worldClient.getChunkProvider() instanceof CubeProviderClient)) {
                Rubic.LOGGER.warn("Ignored a sky light update for a world the client no longer sees as rubic");
                return;
            }
            CubeProviderClient cubeCache = (CubeProviderClient) worldClient.getChunkProvider();
            Cube cube = cubeCache.getCube(message.getCubePos());
            if (cube instanceof BlankCube) {
                Rubic.LOGGER.error("Ignored a sky light update to blank cube {}", message.getCubePos());
                return;
            }
            if (message.getData() == null) {
                cube.setStorage(Chunk.NULL_BLOCK_STORAGE);
                return;
            }
            ExtendedBlockStorage storage = cube.getStorage();
            if (cube.getStorage() == null) { cube.setStorage(storage = new ExtendedBlockStorage(cubeToMinBlock(cube.getY()), worldClient.provider.hasSkyLight())); }
            assert storage != null;
            if (message.isFullRelight()) { storage.setSkyLight(new NibbleArray(message.getData())); }
            else {
                for (int i = 0; i < message.updateCount(); i++) {
                    int packed1 = message.getData()[i * 2] & 0xFF;
                    int packed2 = message.getData()[i * 2 + 1] & 0xFF;
                    storage.setSkyLight(Bits.unpackUnsigned(packed1, 4, 0), Bits.unpackUnsigned(packed1, 4, 4),
                        Bits.unpackUnsigned(packed2, 4, 0), Bits.unpackUnsigned(packed2, 4, 4));
                }
            }
            cube.markForRenderUpdate();
        }
    }
}
