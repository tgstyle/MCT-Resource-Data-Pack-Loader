package mctmods.resourcedatapackloader.content.rubic.lighting;

import java.util.Arrays;

final class LightQueue {
    private static final int START = 1 << 10;
    private int[] xs = new int[START];
    private int[] ys = new int[START];
    private int[] zs = new int[START];
    private int size;

    int size() { return size; }

    boolean isEmpty() { return size == 0; }

    int x(int index) { return xs[index]; }

    int y(int index) { return ys[index]; }

    int z(int index) { return zs[index]; }

    void add(int x, int y, int z) {
        if (size == xs.length) { grow(); }
        xs[size] = x;
        ys[size] = y;
        zs[size] = z;
        size++;
    }

    void clear() { size = 0; }

    private void grow() {
        int wanted = xs.length << 1;
        xs = Arrays.copyOf(xs, wanted);
        ys = Arrays.copyOf(ys, wanted);
        zs = Arrays.copyOf(zs, wanted);
    }
}
