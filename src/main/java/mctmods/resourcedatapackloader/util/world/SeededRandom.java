package mctmods.resourcedatapackloader.util.world;

import net.minecraft.world.World;
import java.util.Random;

public final class SeededRandom {
    private static final long ACROSS = 341873128712L;
    private static final long ALONG = 132897987541L;
    private static final long UPRIGHT = 15485863L;

    private SeededRandom() {}

    public static Random at(World world, int x, int z) { return new Random(world.getSeed() ^ (x * ACROSS + z * ALONG)); }

    public static Random at(World world, int x, int y, int z) { return new Random(world.getSeed() ^ (x * ACROSS + z * ALONG + y * UPRIGHT)); }
}
