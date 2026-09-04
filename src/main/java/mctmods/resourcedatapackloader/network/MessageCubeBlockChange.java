package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CubeProviderClient;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.lighting.ILightingManager;
import mctmods.resourcedatapackloader.content.rubic.world.ClientHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.AddressTools;
import mctmods.resourcedatapackloader.util.CubePos;

import static net.minecraftforge.fml.common.network.ByteBufUtils.readVarInt;
import gnu.trove.TShortCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCubeBlockChange implements IMessage {
    int[] heightValues;
    CubePos cubePos;
    short[] localAddresses;
    IBlockState[] blockStates;

    public MessageCubeBlockChange() {}

    public MessageCubeBlockChange(Cube cube, TShortCollection localAddresses) {
        this.cubePos = cube.getCoords();
        this.localAddresses = localAddresses.toArray();
        this.blockStates = new IBlockState[localAddresses.size()];
        int i = localAddresses.size() - 1;
        TIntSet xzAddresses = new TIntHashSet();
        for (; i >= 0; i--) {
            int localAddress = this.localAddresses[i];
            int x = AddressTools.getLocalX(localAddress);
            int y = AddressTools.getLocalY(localAddress);
            int z = AddressTools.getLocalZ(localAddress);
            this.blockStates[i] = cube.getBlockState(x, y, z);
            xzAddresses.add(AddressTools.getLocalAddress(x, z));
        }
        this.heightValues = new int[xzAddresses.size()];
        i = 0;
        TIntIterator it = xzAddresses.iterator();
        while (it.hasNext()) {
            int v = it.next();
            int height = ((IColumnInternal) cube.getColumn()).getTopYWithStaging(AddressTools.getLocalX(v), AddressTools.getLocalZ(v));
            v |= height << 8;
            heightValues[i] = v;
            i++;
        }
    }

    @SuppressWarnings("deprecation") @Override public void fromBytes(ByteBuf in) {
        this.cubePos = new CubePos(in.readInt(), in.readInt(), in.readInt());
        short numBlocks = in.readShort();
        localAddresses = new short[numBlocks];
        blockStates = new IBlockState[numBlocks];
        for (int i = 0; i < numBlocks; i++) {
            localAddresses[i] = in.readShort();
            blockStates[i] = Block.BLOCK_STATE_IDS.getByValue(readVarInt(in, 4));
        }
        int numHmapChanges = in.readUnsignedByte();
        heightValues = new int[numHmapChanges];
        for (int i = 0; i < numHmapChanges; i++) { heightValues[i] = in.readInt(); }
    }

    @SuppressWarnings("deprecation") @Override public void toBytes(ByteBuf out) {
        out.writeInt(cubePos.getX());
        out.writeInt(cubePos.getY());
        out.writeInt(cubePos.getZ());
        out.writeShort(localAddresses.length);
        for (int i = 0; i < localAddresses.length; i++) {
            out.writeShort(localAddresses[i]);
            ByteBufUtils.writeVarInt(out, Block.BLOCK_STATE_IDS.get(blockStates[i]), 4);
        }
        out.writeByte(heightValues.length);
        for (int v : heightValues) { out.writeInt(v); }
    }

    public static class Handler extends AbstractClientMessageHandler<MessageCubeBlockChange> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageCubeBlockChange packet, MessageContext ctx) {
            WorldClient worldClient = (WorldClient) world;
            if (!(worldClient.getChunkProvider() instanceof CubeProviderClient)) {
                Rubic.LOGGER.warn("Ignored a cube block change for a world the client no longer sees as rubic");
                return;
            }
            CubeProviderClient cubeCache = (CubeProviderClient) worldClient.getChunkProvider();
            Cube cube = cubeCache.getCube(packet.cubePos);
            if (cube instanceof BlankCube) {
                Rubic.LOGGER.error("Ignored block update to blank cube {}", packet.cubePos);
                return;
            }

            for (int i = 0; i < packet.localAddresses.length; i++) {
                BlockPos pos = cube.localAddressToBlockPos(packet.localAddresses[i]);
                worldClient.invalidateBlockReceiveRegion(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
                worldClient.setBlockState(pos, packet.blockStates[i], 3);
            }

            ClientHeightMap index = (ClientHeightMap) cube.getColumn().getOpacityIndex();
            ILightingManager lm = ((IRubicWorldInternal.IClient) world).rdpl$getLightingManager();
            for (int hmapUpdate : packet.heightValues) {
                int x = hmapUpdate & 0xF;
                int z = (hmapUpdate >> 4) & 0xF;
                int height = hmapUpdate >> 8;
                int oldHeight = index.getTopBlockY(x, z);
                index.setHeight(x, z, height);
                if (oldHeight != height) { lm.updateLightBetween(cube.getColumn(), x, oldHeight, height, z); }
            }
            cube.getTileEntityMap().values().forEach(TileEntity::updateContainingBlockInfo);
        }
    }
}
