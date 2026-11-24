package mctmods.resourcedatapackloader.common.locate;

import mctmods.resourcedatapackloader.core.util.math.Longs;

final class RingWalk {
    private final int middleX;
    private final int middleZ;
    private int ring;
    private int step;

    RingWalk(int middleX, int middleZ) {
        this.middleX = middleX;
        this.middleZ = middleZ;
    }

    int ring() { return ring; }

    boolean pending() { return step < onRing(ring); }

    long peek() { return spot(middleX, middleZ, ring, step); }

    void advance() { step++; }

    void nextRing() {
        step = 0;
        ring++;
    }

    private static int onRing(int around) { return around == 0 ? 1 : around * 8; }

    private static long spot(int middleX, int middleZ, int ring, int at) {
        if (ring == 0) { return Longs.pack(middleX, middleZ); }
        int side = ring * 2;
        int leg = at / side;
        int along = at % side;
        if (leg == 0) { return Longs.pack(middleX - ring + along, middleZ - ring); }
        if (leg == 1) { return Longs.pack(middleX + ring, middleZ - ring + along); }
        if (leg == 2) { return Longs.pack(middleX + ring - along, middleZ + ring); }
        return Longs.pack(middleX - ring, middleZ + ring - along);
    }
}
