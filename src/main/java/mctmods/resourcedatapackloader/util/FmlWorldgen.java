package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IGameRegistry;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;
import java.util.List;
import java.util.Random;

public final class FmlWorldgen {
    private FmlWorldgen() {}

    public static List<IWorldGenerator> sorted() {
        List<IWorldGenerator> generators = IGameRegistry.getSortedGeneratorList();
        if (generators != null) { return generators; }
        IGameRegistry.computeGenerators();
        generators = IGameRegistry.getSortedGeneratorList();
        assert generators != null;
        return generators;
    }

    public static long chunkSeed(World world, int x, int z) {
        long worldSeed = world.getSeed();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        return (xSeed * x + zSeed * z) ^ worldSeed;
    }
}
