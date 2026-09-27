package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import java.util.List;
import javax.annotation.Nonnull;

public final class CityDeckBeard extends Beardifier {
    private final Beardifier beard;

    private CityDeckBeard(Beardifier beard) {
        super(List.of(), List.of(), null);
        this.beard = beard;
    }

    public static Beardifier around(Beardifier beard, StructureManager manager, ChunkPos chunk) { return CityCrown.cities(manager, chunk).isEmpty() ? beard : new CityDeckBeard(beard); }

    @Override public void sampleVolume(@Nonnull SamplerContext context, @Nonnull DensityBuffer outputBuffer, @Nonnull DensityVolume volume) { beard.sampleVolume(context, outputBuffer, volume); }

    @Override public float sampleValue(@Nonnull SamplerContext context, int blockX, int blockY, int blockZ) { return beard.sampleValue(context, blockX, blockY, blockZ); }

    public boolean dug(int x, int y, int z, double substance) {
        double carved = beard.sampleValue(SamplerContext.EMPTY_UNCACHED, x, y, z);
        return carved < 0.0 && substance - carved > 0.0;
    }
}
