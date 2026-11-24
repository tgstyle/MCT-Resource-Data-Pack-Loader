package mctmods.resourcedatapackloader.core.network.world;

import mctmods.resourcedatapackloader.client.util.CubeBlockChangeMessageClient;
import mctmods.resourcedatapackloader.client.util.CubeProviderClient;
import mctmods.resourcedatapackloader.common.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.common.util.world.CubePos;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.address.AddressTools;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import static net.minecraftforge.fml.common.network.ByteBufUtils.readVarInt;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CubeBlockChangeMessage implements IMessage {
    int[] heightAddresses;
    int[] heightValues;
    CubePos cubePos;
    short[] localAddresses;
    IBlockState[] blockStates;

    public CubeBlockChangeMessage() {}

    public CubeBlockChangeMessage(Cube cube, TShortCollection localAddresses) {
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
        this.heightAddresses = new int[xzAddresses.size()];
        this.heightValues = new int[xzAddresses.size()];
        i = 0;
        TIntIterator it = xzAddresses.iterator();
        while (it.hasNext()) {
            int v = it.next();
            heightAddresses[i] = v;
            heightValues[i] = WorldEncoder.topY(cube.getColumn(), v);
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
        int numHmapChanges = in.readUnsignedShort();
        heightAddresses = new int[numHmapChanges];
        heightValues = new int[numHmapChanges];
        for (int i = 0; i < numHmapChanges; i++) {
            heightAddresses[i] = in.readUnsignedByte();
            heightValues[i] = readVarInt(in, 5);
        }
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
        out.writeShort(heightValues.length);
        for (int i = 0; i < heightValues.length; i++) {
            WorldEncoder.writeHeight(out, heightAddresses[i], heightValues[i]);
        }
    }

    public static class Handler extends AbstractClientMessageHandler<CubeBlockChangeMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, CubeBlockChangeMessage packet, MessageContext ctx) {
            CubeProviderClient cubeCache = cubeCache(world, "a cube block change");
            if (cubeCache == null) { return; }
            CubeBlockChangeMessageClient.apply(world, cubeCache, packet.cubePos, packet.localAddresses, packet.blockStates, packet.heightAddresses, packet.heightValues);
        }
    }
}
