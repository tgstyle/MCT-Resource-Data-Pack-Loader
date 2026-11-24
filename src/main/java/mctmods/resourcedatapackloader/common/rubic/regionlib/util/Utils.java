package mctmods.resourcedatapackloader.common.rubic.regionlib.util;

import mctmods.resourcedatapackloader.common.rubic.regionlib.lib.RegionEntryLocation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.util.Arrays;
import java.util.BitSet;

public class Utils {
    public static RegionEntryLocation findFreeSectors(BitSet usedSectors, int requestedSize) {
        int next = 0, current, runSize;
        do {
            int nextClear = usedSectors.nextClearBit(next);
            int nextUsed = usedSectors.nextSetBit(nextClear);
            current = nextClear;
            next = nextUsed;
            runSize = nextUsed < 0 ? Integer.MAX_VALUE : nextUsed - nextClear;
        } while (runSize < requestedSize);
        return new RegionEntryLocation(current, requestedSize);
    }

    public static long fill(FileChannel src, ByteBuffer data, long position) throws IOException {
        long at = position;
        while (data.hasRemaining()) {
            int read = src.read(data, at);
            if (read < 0) { break; }
            at += read;
        }
        return at;
    }

    public static void readFully(FileChannel src, ByteBuffer data, long position) throws IOException {
        long at = fill(src, data, position);
        if (data.hasRemaining()) { throw new ICorruptedDataException("Expected " + data.remaining() + " more byte(s) at " + at + " but the file ends there"); }
    }

    public static void writeFully(ByteChannel dst, ByteBuffer data) throws IOException { while (data.hasRemaining()) { dst.write(data); } }

    public static void writeFully(GatheringByteChannel dst, ByteBuffer[] data) throws IOException {
        long totalRemaining = Arrays.stream(data).mapToLong(ByteBuffer::remaining).sum();
        long totalWritten = 0L;
        while (totalWritten < totalRemaining) { totalWritten += dst.write(data); }
    }
}
