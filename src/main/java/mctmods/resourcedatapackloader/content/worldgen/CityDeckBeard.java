package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import java.util.List;
import javax.annotation.Nonnull;

public final class CityDeckBeard extends Beardifier {
    private final Beardifier beard;

    private CityDeckBeard(Beardifier beard) {
        super(List.of(), List.of(), null);
        this.beard = beard;
    }

    public static Beardifier around(Beardifier beard, StructureManager manager, ChunkPos chunk) { return manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure).isEmpty() ? beard : new CityDeckBeard(beard); }

    @Override public double compute(@Nonnull DensityFunction.FunctionContext context) { return beard.compute(context); }

    @Override public void fillArray(@Nonnull double[] output, @Nonnull DensityFunction.ContextProvider provider) { beard.fillArray(output, provider); }

    public boolean dug(DensityFunction.FunctionContext context, double substance) {
        double carved = compute(context);
        return carved < 0.0 && substance - carved > 0.0;
    }
}
