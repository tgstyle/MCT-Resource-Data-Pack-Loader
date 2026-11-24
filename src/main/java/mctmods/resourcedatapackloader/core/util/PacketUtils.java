package mctmods.resourcedatapackloader.core.util;

import mctmods.resourcedatapackloader.common.util.world.CubePos;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketUtils {
    public static final int MOST_LINES = 16;
    private static final int MASK_6 = (1 << 6) - 1;
    private static final int MASK_7 = (1 << 7) - 1;
    private static final int MORE_BIT = 0x80;

    public static void write(ByteBuf buf, ChunkPos pos) {
        buf.writeInt(pos.x);
        buf.writeInt(pos.z);
    }

    public static ChunkPos readChunkPos(ByteBuf buf) { return new ChunkPos(buf.readInt(), buf.readInt()); }

    public static void write(ByteBuf buf, BlockPos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static BlockPos readBlockPos(ByteBuf buf) { return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()); }

    public static void writeBytes(ByteBuf buf, byte[] data) {
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static byte[] readBytes(ByteBuf buf) {
        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        return data;
    }

    public static void write(ByteBuf buf, CubePos pos) {
        writeSignedVarInt(buf, pos.getX());
        writeSignedVarInt(buf, pos.getY());
        writeSignedVarInt(buf, pos.getZ());
    }

    public static void writeFixed(ByteBuf buf, CubePos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static CubePos readFixedCubePos(ByteBuf buf) { return new CubePos(buf.readInt(), buf.readInt(), buf.readInt()); }

    public static List<String> readStrings(ByteBuf buf, int count) {
        List<String> read = new ArrayList<>();
        for (int i = 0; i < count; i++) { read.add(ByteBufUtils.readUTF8String(buf)); }
        return read;
    }

    public static void writeStrings(ByteBuf buf, List<String> values, int count) {
        for (int i = 0; i < count; i++) { ByteBufUtils.writeUTF8String(buf, values.get(i)); }
    }

    public static List<String> readLines(ByteBuf buf) { return readStrings(buf, buf.readByte()); }

    public static void writeLines(ByteBuf buf, List<String> values) {
        int count = Math.min(values.size(), MOST_LINES);
        buf.writeByte(count);
        writeStrings(buf, values, count);
    }

    public static CubePos readCubePos(ByteBuf buf) { return new CubePos(readSignedVarInt(buf), readSignedVarInt(buf), readSignedVarInt(buf)); }

    public static void writeSignedVarInt(ByteBuf buf, int i) {
        int signBit = (i >>> 31) << 6;
        int val = i < 0 ? ~i : i;
        assert val >= 0;
        writeVarIntByte(buf, (val & MASK_6) | signBit, (val >>= 6) > 0);
        while (val > 0) { writeVarIntByte(buf, (val & MASK_7), (val >>= 7) > 0); }
    }

    public static int readSignedVarInt(ByteBuf buf) {
        int val = 0;
        int b = buf.readUnsignedByte();
        boolean sign = ((b >> 6) & 1) != 0;
        val |= b & MASK_6;
        int shift = 6;
        while ((b & MORE_BIT) != 0) {
            if (shift > Integer.SIZE) { throw new RuntimeException("VarInt too big"); }
            b = buf.readUnsignedByte();
            val |= (b & MASK_7) << shift;
            shift += 7;
        }
        return sign ? ~val : val;
    }

    public static int[] readInts(ByteBuf buf, int max) {
        int[] values = new int[Math.min(buf.readByte(), max)];
        for (int i = 0; i < values.length; i++) { values[i] = buf.readInt(); }
        return values;
    }

    public static void writeInts(ByteBuf buf, int[] values, int max) {
        int count = Math.min(values.length, max);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeInt(values[i]); }
    }

    public static void writeInts(ByteBuf buf, int[] values) {
        buf.writeByte(values.length);
        for (int each : values) { buf.writeInt(each); }
    }

    private static void writeVarIntByte(ByteBuf buf, int i, boolean hasMore) { buf.writeByte(i | (hasMore ? MORE_BIT : 0)); }
}
