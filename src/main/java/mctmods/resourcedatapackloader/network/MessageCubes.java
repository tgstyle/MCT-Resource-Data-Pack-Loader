package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CubeProviderClient;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.PacketUtils;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageCubes implements IMessage {
    private CubePos[] cubePos;
    private byte[] data;
    private List<List<NBTTagCompound>> tileEntityTags;

    private static final int MAX_CUBES = 1024;
    private static final int MAX_BYTES = 512 * 1024;

    public MessageCubes() {}

    public static List<MessageCubes> batched(Collection<Cube> cubes) {
        List<Cube> ordered = new ArrayList<>(cubes);
        ordered.sort(Comparator.<Cube>comparingInt(Cube::getX).thenComparingInt(Cube::getZ).thenComparingInt(Cube::getY));
        List<MessageCubes> packets = new ArrayList<>();
        List<Cube> batch = new ArrayList<>(MAX_CUBES);
        LongOpenHashSet columns = new LongOpenHashSet();
        int bytes = 0;
        for (Cube cube : ordered) {
            long column = ChunkPos.asLong(cube.getX(), cube.getZ());
            int cost = WorldEncoder.getEncodedSize(cube, !cube.isEmpty() && !columns.contains(column));
            if (!batch.isEmpty() && (batch.size() >= MAX_CUBES || bytes + cost > MAX_BYTES)) {
                packets.add(new MessageCubes(batch));
                batch = new ArrayList<>(MAX_CUBES);
                columns.clear();
                bytes = 0;
                cost = WorldEncoder.getEncodedSize(cube, !cube.isEmpty());
            }
            batch.add(cube);
            if (!cube.isEmpty()) { columns.add(column); }
            bytes += cost;
        }
        if (!batch.isEmpty()) { packets.add(new MessageCubes(batch)); }
        return packets;
    }

    public MessageCubes(List<Cube> cubes) {
        cubes.sort(Comparator.<Cube>comparingInt(c -> c.getCoords().getY())
                .thenComparingInt(c -> c.getCoords().getX())
                .thenComparingInt(c -> c.getCoords().getZ()));
        this.cubePos = new CubePos[cubes.size()];
        for (int i = 0; i < cubes.size(); i++) { cubePos[i] = cubes.get(i).getCoords(); }
        this.data = new byte[WorldEncoder.getEncodedSize(cubes)];
        PacketBuffer out = new PacketBuffer(WorldEncoder.createByteBufForWrite(this.data));
        WorldEncoder.encodeCubes(out, cubes);
        this.tileEntityTags = new ArrayList<>();
        cubes.forEach(cube ->
                tileEntityTags.add(cube.getTileEntityMap().values().stream().map(TileEntity::getUpdateTag).collect(Collectors.toList()))
        );
    }

    @Override public void fromBytes(ByteBuf buf) {
        int cubeCount = buf.readUnsignedShort();
        cubePos = new CubePos[cubeCount];
        for (int i = 0; i < this.cubePos.length; i++) { cubePos[i] = PacketUtils.readCubePos(buf); }
        this.data = new byte[buf.readInt()];
        buf.readBytes(this.data);
        this.tileEntityTags = new ArrayList<>();
        for (int i = 0; i < cubeCount; i++) {
            int numTiles = buf.readInt();
            List<NBTTagCompound> tags = new ArrayList<>();
            for (int j = 0; j < numTiles; j++) { tags.add(ByteBufUtils.readTag(buf)); }
            this.tileEntityTags.add(tags);
        }
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeShort(cubePos.length);
        for (CubePos pos : cubePos) { PacketUtils.write(buf, pos); }
        buf.writeInt(this.data.length);
        buf.writeBytes(this.data);
        this.tileEntityTags.forEach(tags -> {
            buf.writeInt(tags.size());
            tags.forEach(tag -> ByteBufUtils.writeTag(buf, tag));
        });
    }

    CubePos[] getCubePos() { return cubePos; }

    byte[] getData() { return data; }

    List<List<NBTTagCompound>> getTileEntityTags() { return this.tileEntityTags; }

    public static class Handler extends AbstractClientMessageHandler<MessageCubes> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageCubes message, MessageContext ctx) {
            WorldClient worldClient = (WorldClient) player.getEntityWorld();
            if (!(worldClient.getChunkProvider() instanceof CubeProviderClient)) {
                Rubic.LOGGER.warn("Ignored cubes sent for a world the client no longer sees as rubic");
                return;
            }
            CubeProviderClient cubeCache = (CubeProviderClient) worldClient.getChunkProvider();
            CubePos[] cubePos = message.getCubePos();
            List<Cube> cubes = new ArrayList<>();
            for (CubePos pos : cubePos) {
                Cube cube = cubeCache.loadCube(pos);
                if (cube == null) { Rubic.LOGGER.error("Out of order cube received! No column for cube at {} exists!", pos); }
                cubes.add(cube);
            }

            byte[] data = message.getData();
            ByteBuf buf = WorldEncoder.createByteBufForRead(data);
            WorldEncoder.decodeCube(new PacketBuffer(buf), cubes);

            cubes.stream().filter(Objects::nonNull).forEach(Cube::markForRenderUpdate);

            message.getTileEntityTags().forEach(tags -> tags.forEach(tag -> {
                int blockX = tag.getInteger("x");
                int blockY = tag.getInteger("y");
                int blockZ = tag.getInteger("z");
                BlockPos pos = new BlockPos(blockX, blockY, blockZ);
                TileEntity tileEntity = worldClient.getTileEntity(pos);
                if (tileEntity != null) { tileEntity.handleUpdateTag(tag); }
            }));
        }
    }
}
