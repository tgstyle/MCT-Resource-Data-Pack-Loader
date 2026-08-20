package mctmods.resourcedatapackloader.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketUtils {
    private static final int MASK_6 = (1 << 6) - 1;
    private static final int MASK_7 = (1 << 7) - 1;

    public static void write(ByteBuf buf, BlockPos pos) {
        writeSignedVarInt(buf, pos.getX());
        writeSignedVarInt(buf, pos.getY());
        writeSignedVarInt(buf, pos.getZ());
    }

    public static void write(ByteBuf buf, CubePos pos) {
        writeSignedVarInt(buf, pos.getX());
        writeSignedVarInt(buf, pos.getY());
        writeSignedVarInt(buf, pos.getZ());
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
        while ((b & 0x80) != 0) {
            if (shift > Integer.SIZE) { throw new RuntimeException("VarInt too big"); }
            b = buf.readUnsignedByte();
            val |= (b & MASK_7) << shift;
            shift += 7;
        }
        return sign ? ~val : val;
    }

    private static void writeVarIntByte(ByteBuf buf, int i, boolean hasMore) { buf.writeByte(i | (hasMore ? 0x80 : 0)); }
}
