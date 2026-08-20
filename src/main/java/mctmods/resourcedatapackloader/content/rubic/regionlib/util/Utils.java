package mctmods.resourcedatapackloader.content.rubic.regionlib.util;

import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.RegionEntryLocation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static void createDirectories(Path dir) throws IOException {
        if (Files.isDirectory(dir)) { return; }
        createDirectories(dir.getParent());
        Files.createDirectory(dir);
    }

    public static void readFully(ByteChannel src, ByteBuffer data) throws IOException {
        while (data.hasRemaining()) { src.read(data); }
    }

    public static void writeFully(ByteChannel dst, ByteBuffer data) throws IOException {
        while (data.hasRemaining()) { dst.write(data); }
    }

    public static void writeFully(GatheringByteChannel dst, ByteBuffer[] data) throws IOException {
        long totalRemaining = Arrays.stream(data).mapToLong(ByteBuffer::remaining).sum();
        long totalWritten = 0L;
        while (totalWritten < totalRemaining) { totalWritten += dst.write(data); }
    }
}
