package mctmods.resourcedatapackloader.util;


public class Bits {
    public static long packSignedToLong(int signed, int size, int offset) {
        long result = signed & getMask(size);
        return result << offset;
    }

    public static int packUnsignedToInt(int unsigned, int size, int offset) {
        int result = unsigned & getMask(size);
        return result << offset;
    }

    public static int unpackUnsigned(int packed, int size, int offset) {
        packed = packed >> offset;
        return packed & getMask(size);
    }

    public static int getMask(int size) {
        assert (size > 0 && size < 32);
        return 0xffffffff >>> (32 - size);
    }
}
