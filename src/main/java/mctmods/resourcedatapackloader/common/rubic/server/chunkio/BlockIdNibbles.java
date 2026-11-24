package mctmods.resourcedatapackloader.common.rubic.server.chunkio;

import javax.annotation.Nullable;
import net.minecraft.world.chunk.NibbleArray;

final class BlockIdNibbles {
    private BlockIdNibbles() {}

    static int decode(int i, byte[] blocks, NibbleArray data, @Nullable NibbleArray add, @Nullable NibbleArray add2) {
        int toAdd = add == null ? 0 : add.getFromIndex(i);
        toAdd = (toAdd & 0xF) | (add2 == null ? 0 : add2.getFromIndex(i) << 4);
        return (toAdd << 12) | ((blocks[i] & 0xFF) << 4) | data.getFromIndex(i);
    }

    static byte[] putNibble(@Nullable byte[] nibbles, int length, int i, int value) {
        if (value == 0) { return nibbles; }
        byte[] target = nibbles == null ? new byte[length] : nibbles;
        target[i >> 1] |= (byte) (value << ((i & 1) << 2));
        return target;
    }
}
