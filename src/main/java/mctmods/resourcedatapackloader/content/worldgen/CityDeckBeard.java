package mctmods.resourcedatapackloader.content.worldgen;

import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;

import javax.annotation.Nonnull;

public final class CityDeckBeard extends Beardifier {
    private final Beardifier beard;

    private CityDeckBeard(Beardifier beard) {
        super(ObjectLists.<Beardifier.Rigid>emptyList().iterator(), ObjectLists.<JigsawJunction>emptyList().iterator());
        this.beard = beard;
    }

    public static Beardifier around(Beardifier beard, StructureManager manager, ChunkPos chunk) { return manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure).isEmpty() ? beard : new CityDeckBeard(beard); }

    @Override public double compute(@Nonnull DensityFunction.FunctionContext context) { return beard.compute(context); }

    public boolean dug(DensityFunction.FunctionContext context, double substance) {
        double carved = compute(context);
        return carved < 0.0 && substance - carved > 0.0;
    }
}
