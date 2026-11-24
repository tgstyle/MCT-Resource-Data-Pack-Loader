package mctmods.resourcedatapackloader.common.anvils;

final class SlotCosts {
    private static final int[] NONE = new int[0];
    private volatile int[] served = NONE;
    private volatile int[] shown = NONE;

    void reset() { served = NONE; }

    int[] served() { return served.clone(); }

    void serve(int[] values) { served = values.clone(); }

    void show(int[] values) { shown = values.length == AnvilCosts.SLOTS ? values.clone() : NONE; }

    int at(boolean client, int slot) {
        int[] held = client ? shown : served;
        return held.length == AnvilCosts.SLOTS && slot >= 0 && slot < AnvilCosts.SLOTS ? held[slot] : slot + 1;
    }
}
