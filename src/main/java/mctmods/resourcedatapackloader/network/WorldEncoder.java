package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.util.Coords;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

class WorldEncoder {
    static void encodeCubes(PacketBuffer out, Collection<Cube> cubes) {
        cubes.forEach(cube -> {
            byte flags = 0;
            if(cube.isEmpty())
                flags |= 1;
            if(cube.getStorage() != null)
                flags |= 2;
            if(cube.getBiomeArray() != null)
                flags |= 4;
            out.writeByte(flags);
        });

        cubes.forEach(cube -> {
            if (!cube.isEmpty()) { Objects.requireNonNull(cube.getStorage()).getData().write(out); }
        });

        cubes.forEach(cube -> {
            ExtendedBlockStorage storage = cube.getStorage();
            if (storage != null) { out.writeBytes(storage.getBlockLight().getData()); }
        });

        cubes.forEach(cube -> {
            ExtendedBlockStorage storage = cube.getStorage();
            if (storage != null && cube.getWorld().provider.hasSkyLight()) { out.writeBytes(storage.getSkyLight().getData()); }
        });

        LongOpenHashSet written = new LongOpenHashSet();
        cubes.forEach(cube -> {
            if (!cube.isEmpty() && written.add(ChunkPos.asLong(cube.getX(), cube.getZ()))) {
                ((IColumnInternal) cube.getColumn()).writeHeightmapDataForClient(out);
            }
        });

        cubes.forEach(cube -> {
            if (cube.getBiomeArray() != null)
                out.writeBytes(cube.getBiomeArray());
        });
    }

    static void encodeColumn(PacketBuffer out, Chunk column) {
        out.writeBytes(column.getBiomeArray());
        ((IColumnInternal) column).writeHeightmapDataForClient(out);
    }

    static void decodeColumn(PacketBuffer in, Chunk column) {
        in.readBytes(column.getBiomeArray());
        if (in.readableBytes() > 0) { ((IColumnInternal) column).loadClientHeightmapData(in); }
    }

    static void decodeCube(PacketBuffer in, List<Cube> cubes) {
        cubes.stream().filter(Objects::nonNull).forEach(Cube::setClientCube);
        boolean[] isEmpty = new boolean[cubes.size()];
        boolean[] hasStorage = new boolean[cubes.size()];
        boolean[] hasCustomBiomeMap = new boolean[cubes.size()];
        for (int i = 0; i < cubes.size(); i++) {
            byte flags = in.readByte();
            isEmpty[i] = (flags & 1) != 0 || cubes.get(i) == null;
            hasStorage[i] = (flags & 2) != 0 && cubes.get(i) != null;
            hasCustomBiomeMap[i] = (flags & 4) != 0 && cubes.get(i) != null;
        }
        for (int i = 0; i < cubes.size(); i++) {
            if (hasStorage[i]) {
                Cube cube = cubes.get(i);
                ExtendedBlockStorage storage = new ExtendedBlockStorage(Coords.cubeToMinBlock(cube.getY()),
                        cube.getWorld().provider.hasSkyLight());
                cube.setStorageFromSave(storage);
            }
        }
        for (int i = 0; i < cubes.size(); i++) {
            if (!isEmpty[i]) { Objects.requireNonNull(cubes.get(i).getStorage()).getData().read(in); }
        }
        for (int i = 0; i < cubes.size(); i++) {
            if (hasStorage[i]) {
                byte[] data = Objects.requireNonNull(cubes.get(i).getStorage()).getBlockLight().getData();
                in.readBytes(data);
            }
        }
        for (int i = 0; i < cubes.size(); i++) {
            if (hasStorage[i] && cubes.get(i).getWorld().provider.hasSkyLight()) {
                byte[] data = Objects.requireNonNull(cubes.get(i).getStorage()).getSkyLight().getData();
                in.readBytes(data);
            }
        }
        LongOpenHashSet loaded = new LongOpenHashSet();
        for (int i = 0; i < cubes.size(); i++) {
            if (!isEmpty[i]) {
                Cube cube = cubes.get(i);
                if (loaded.add(ChunkPos.asLong(cube.getX(), cube.getZ()))) { ((IColumnInternal) cube.getColumn()).loadClientHeightmapData(in); }
                Objects.requireNonNull(cube.getStorage()).recalculateRefCounts();
            }
        }
        for (int i = 0; i < cubes.size(); i++) {
            if (!hasCustomBiomeMap[i]) { continue; }
            Cube cube = cubes.get(i);
            byte[] blockBiomeArray = new byte[Coords.BIOMES_PER_CUBE];
            in.readBytes(blockBiomeArray);
            cube.setBiomeArray(blockBiomeArray);
        }
    }

    static int getEncodedSize(Chunk column) { return column.getBiomeArray().length + Cube.SIZE * Cube.SIZE * Integer.BYTES; }

    static int getEncodedSize(Cube cube, boolean countHeightmap) {
        int size = 1;
        ExtendedBlockStorage storage = cube.getStorage();
        if (!cube.isEmpty()) {
            size += Objects.requireNonNull(storage).getData().getSerializedSize();
            if (countHeightmap) { size += Cube.SIZE * Cube.SIZE * Integer.BYTES; }
        }
        if (storage != null) {
            size += storage.getBlockLight().getData().length;
            if (cube.getWorld().provider.hasSkyLight()) { size += storage.getSkyLight().getData().length; }
        }
        byte[] biomeArray = cube.getBiomeArray();
        if (biomeArray != null) { size += biomeArray.length; }
        return size;
    }

    static int getEncodedSize(Collection<Cube> cubes) {
        int size = 0;
        LongOpenHashSet counted = new LongOpenHashSet();
        for (Cube cube : cubes) { size += getEncodedSize(cube, !cube.isEmpty() && counted.add(ChunkPos.asLong(cube.getX(), cube.getZ()))); }
        return size;
    }

    static ByteBuf createByteBufForWrite(byte[] data) {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(data);
        bytebuf.writerIndex(0);
        return bytebuf;
    }

    static ByteBuf createByteBufForRead(byte[] data) {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(data);
        bytebuf.readerIndex(0);
        return bytebuf;
    }
}
