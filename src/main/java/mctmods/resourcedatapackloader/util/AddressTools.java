package mctmods.resourcedatapackloader.util;


public class AddressTools {
    public static int getLocalAddress(int localX, int localY, int localZ) {
        return (Bits.packUnsignedToInt(localX, 4, 0)
                | Bits.packUnsignedToInt(localZ, 4, 4)
                | Bits.packUnsignedToInt(localY, 4, 8));
    }

    public static int getLocalAddress(int localX, int localZ) {
        return (Bits.packUnsignedToInt(localX, 4, 0)
                | Bits.packUnsignedToInt(localZ, 4, 4));
    }

    public static int getLocalX(int localAddress) { return Bits.unpackUnsigned(localAddress, 4, 0); }

    public static int getLocalY(int localAddress) { return Bits.unpackUnsigned(localAddress, 4, 8); }

    public static int getLocalZ(int localAddress) { return Bits.unpackUnsigned(localAddress, 4, 4); }

    public static int getBiomeAddress3d(int biomeLocalX, int biomeLocalY, int biomeLocalZ) { return biomeLocalX | biomeLocalY << 2 | biomeLocalZ << 4; }
}
