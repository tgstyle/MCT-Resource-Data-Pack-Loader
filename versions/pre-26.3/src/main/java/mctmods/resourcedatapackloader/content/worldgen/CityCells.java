package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.mixin.rdpl.common.INoiseBasedChunkGenerator;
import mctmods.resourcedatapackloader.mixin.rdpl.common.INoiseChunk;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import java.util.function.Predicate;

final class CityCells {
    private static final DensityFunctions.BeardifierOrMarker MARKER = (DensityFunctions.BeardifierOrMarker) DensityFunctions.BeardifierOrMarker.CODEC.codec().codec().parse(JsonOps.INSTANCE, new JsonObject()).result().orElseThrow();
    private final NoiseChunk noise;
    private final NoiseSettings shape;
    private final BlockState fallback;

    CityCells(NoiseBasedChunkGenerator generator, RandomState random, NoiseSettings shape, int originX, int originZ) {
        NoiseGeneratorSettings settings = generator.generatorSettings().value();
        this.shape = shape;
        this.fallback = settings.defaultBlock();
        this.noise = new NoiseChunk(1, random, originX, originZ, shape, MARKER, settings, ((INoiseBasedChunkGenerator) generator).rdpl$getGlobalFluidPicker().get(), Blender.empty());
        noise.initializeForFirstCellX();
        noise.advanceCellX(0);
    }

    static int width(NoiseSettings shape) { return Math.floorDiv(shape.height(), shape.getCellHeight()) <= 0 ? 0 : shape.getCellWidth(); }

    synchronized int[] tops(int x, int z, int bottom) {
        int tall = shape.getCellHeight();
        int wide = shape.getCellWidth();
        int lowest = Math.floorDiv(shape.minY(), tall);
        double alongX = (double) Math.floorMod(x, wide) / wide;
        double alongZ = (double) Math.floorMod(z, wide) / wide;
        Predicate<BlockState> surface = Heightmap.Types.WORLD_SURFACE_WG.isOpaque();
        Predicate<BlockState> floor = Heightmap.Types.OCEAN_FLOOR_WG.isOpaque();
        int[] tops = {bottom, bottom};
        boolean surfaced = false;
        for (int cellY = Math.floorDiv(shape.height(), tall) - 1; cellY >= 0; cellY--) {
            noise.selectCellYZ(cellY, 0);
            for (int inY = tall - 1; inY >= 0; inY--) {
                int y = (lowest + cellY) * tall + inY;
                noise.updateForY(y, (double) inY / tall);
                noise.updateForX(x, alongX);
                noise.updateForZ(z, alongZ);
                BlockState state = ((INoiseChunk) noise).rdpl$getInterpolatedState();
                BlockState found = state == null ? fallback : state;
                if (!surfaced && surface.test(found)) {
                    tops[0] = y + 1;
                    surfaced = true;
                }
                if (surfaced && floor.test(found)) {
                    tops[1] = y + 1;
                    return tops;
                }
            }
        }
        return tops;
    }
}
