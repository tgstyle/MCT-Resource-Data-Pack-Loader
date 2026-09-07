package mctmods.resourcedatapackloader.util.world;

import java.util.Random;

public final class Noise3D {
    private final int[] permutation = new int[512];

    public Noise3D(long seed) {
        int[] base = new int[256];
        for (int i = 0; i < 256; i++) { base[i] = i; }
        Random random = new Random(seed);
        for (int i = 255; i > 0; i--) {
            int swap = random.nextInt(i + 1);
            int held = base[i];
            base[i] = base[swap];
            base[swap] = held;
        }
        for (int i = 0; i < 512; i++) { permutation[i] = base[i & 255]; }
    }

    public float noise(float x, float y, float z) {
        int cellX = floor(x);
        int cellY = floor(y);
        int cellZ = floor(z);
        float fx = x - cellX;
        float fy = y - cellY;
        float fz = z - cellZ;
        int ix = cellX & 255;
        int iy = cellY & 255;
        int iz = cellZ & 255;
        float u = fade(fx);
        float v = fade(fy);
        float w = fade(fz);
        int a = permutation[ix] + iy;
        int aa = permutation[a] + iz;
        int ab = permutation[a + 1] + iz;
        int b = permutation[ix + 1] + iy;
        int ba = permutation[b] + iz;
        int bb = permutation[b + 1] + iz;
        float x1 = lerp(u, grad(permutation[aa], fx, fy, fz), grad(permutation[ba], fx - 1, fy, fz));
        float x2 = lerp(u, grad(permutation[ab], fx, fy - 1, fz), grad(permutation[bb], fx - 1, fy - 1, fz));
        float x3 = lerp(u, grad(permutation[aa + 1], fx, fy, fz - 1), grad(permutation[ba + 1], fx - 1, fy, fz - 1));
        float x4 = lerp(u, grad(permutation[ab + 1], fx, fy - 1, fz - 1), grad(permutation[bb + 1], fx - 1, fy - 1, fz - 1));
        return lerp(w, lerp(v, x1, x2), lerp(v, x3, x4));
    }

    public float octaves(float x, float y, float z, int count, float persistence) {
        float value = 0.0F;
        float amplitude = 1.0F;
        float frequency = 1.0F;
        float total = 0.0F;
        for (int i = 0; i < count; i++) {
            value += noise(x * frequency, y * frequency, z * frequency) * amplitude;
            total += amplitude;
            amplitude *= persistence;
            frequency *= 2.0F;
        }
        return value / total;
    }

    private static int floor(float value) { return value >= 0.0F ? (int) value : (int) value - 1; }

    private static float fade(float t) { return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F); }

    private static float lerp(float t, float a, float b) { return a + t * (b - a); }

    private static float grad(int hash, float x, float y, float z) {
        int h = hash & 15;
        float u = h < 8 ? x : y;
        float v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
