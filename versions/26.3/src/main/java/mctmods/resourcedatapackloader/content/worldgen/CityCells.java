package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.mixin.rdpl.common.INoiseBasedChunkGenerator;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import java.util.function.Predicate;

final class CityCells {
    private static final int WIDTH = 4;
    private static final int UNFOUND = Integer.MIN_VALUE;
    private final int originX;
    private final int originZ;
    private final int[] surfaces = new int[WIDTH * WIDTH];
    private final int[] floors = new int[WIDTH * WIDTH];

    CityCells(NoiseBasedChunkGenerator generator, RandomState random, NoiseSettings shape, int originX, int originZ) {
        this.originX = originX;
        this.originZ = originZ;
        NoiseGeneratorSettings settings = generator.generatorSettings().value();
        BlockState fallback = settings.defaultBlock();
        DensityVolume volume = new DensityVolume(WIDTH, shape.height(), WIDTH, originX, shape.minY(), originZ);
        Predicate<BlockState> surface = Heightmap.Types.WORLD_SURFACE_WG.isOpaque();
        Predicate<BlockState> floor = Heightmap.Types.OCEAN_FLOOR_WG.isOpaque();
        try (NoiseChunk noise = new NoiseChunk(random, null, settings, ((INoiseBasedChunkGenerator) generator).rdpl$getGlobalFluidPicker().get(), Blender.empty(), volume)) {
            Aquifer aquifer = noise.aquifer();
            DensitySampler.Bound density = noise.cachingSamplers().get(settings.noiseRouter().finalDensity());
            try (ScopedDensityBuffer buffer = density.sampleVolume(volume)) {
                for (int x = 0; x < WIDTH; x++) {
                    for (int z = 0; z < WIDTH; z++) {
                        int at = x * WIDTH + z;
                        surfaces[at] = UNFOUND;
                        floors[at] = UNFOUND;
                        for (int y = volume.sizeY() - 1; y >= 0 && floors[at] == UNFOUND; y--) {
                            int blockY = volume.blockY(y);
                            BlockState state = aquifer.computeSubstance(volume.blockX(x), blockY, volume.blockZ(z), buffer.get(volume.indexUnchecked(x, y, z)));
                            BlockState found = state == null ? fallback : state;
                            if (surfaces[at] == UNFOUND && surface.test(found)) { surfaces[at] = blockY + 1; }
                            if (surfaces[at] != UNFOUND && floor.test(found)) { floors[at] = blockY + 1; }
                        }
                    }
                }
            }
        }
    }

    static int width(NoiseSettings shape) { return shape.height() <= 0 ? 0 : WIDTH; }

    int[] tops(int x, int z, int bottom) {
        int at = (x - originX) * WIDTH + (z - originZ);
        return new int[] {surfaces[at] == UNFOUND ? bottom : surfaces[at], floors[at] == UNFOUND ? bottom : floors[at]};
    }
}
