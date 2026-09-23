package mctmods.resourcedatapackloader.content.worldgen;

import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.common.world.PieceBeardifierModifier;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EncapsulateBeard extends Beardifier {
    private static final int NEAR = 12;
    private final Beardifier beard;
    private final List<Beardifier.Rigid> held;

    private EncapsulateBeard(Beardifier beard, List<Beardifier.Rigid> held) {
        super(ObjectLists.<Beardifier.Rigid>emptyList().iterator(), ObjectLists.<JigsawJunction>emptyList().iterator());
        this.beard = beard;
        this.held = held;
    }

    public static Beardifier around(Beardifier beard, StructureManager manager, ChunkPos chunk) {
        Registry<Structure> registry = manager.registryAccess().registryOrThrow(Registries.STRUCTURE);
        List<Beardifier.Rigid> held = new ArrayList<>();
        for (StructureStart start : manager.startsForStructure(chunk, structure -> encapsulated(structure, registry))) {
            boolean city = start.getStructure() instanceof ContentCityStructure;
            for (StructurePiece piece : start.getPieces()) {
                Beardifier.Rigid rigid = piece.isCloseToChunk(chunk, NEAR) ? rigid(piece, city) : null;
                if (rigid != null) { held.add(rigid); }
            }
        }
        return held.isEmpty() ? beard : new EncapsulateBeard(beard, held);
    }

    private static boolean encapsulated(Structure structure, Registry<Structure> registry) {
        if (structure instanceof ContentCityStructure) { return structure.terrainAdaptation() != TerrainAdjustment.NONE && ContentCity.encapsulates(); }
        return structure.terrainAdaptation() == TerrainAdjustment.BURY && ContentStructureControl.encapsulates(registry.getKey(structure));
    }

    @Nullable private static Beardifier.Rigid rigid(StructurePiece piece, boolean city) {
        if (piece instanceof PieceBeardifierModifier modifier) { return city && modifier.getTerrainAdjustment() == TerrainAdjustment.BURY ? new Beardifier.Rigid(modifier.getBeardifierBox(), TerrainAdjustment.BURY, modifier.getGroundLevelDelta()) : null; }
        if (city) { return null; }
        if (piece instanceof PoolElementStructurePiece pool) { return pool.getElement().getProjection() == StructureTemplatePool.Projection.RIGID ? new Beardifier.Rigid(pool.getBoundingBox(), TerrainAdjustment.BURY, pool.getGroundLevelDelta()) : null; }
        return new Beardifier.Rigid(piece.getBoundingBox(), TerrainAdjustment.BURY, 0);
    }

    @Override public double compute(@Nonnull DensityFunction.FunctionContext context) {
        int x = context.blockX();
        int y = context.blockY();
        int z = context.blockZ();
        double density = beard.compute(context);
        for (Beardifier.Rigid rigid : held) { density += contribution(rigid, x, y, z); }
        return density;
    }

    private static double contribution(Beardifier.Rigid rigid, int x, int y, int z) {
        BoundingBox box = rigid.box();
        int dx = Math.max(0, Math.max(box.minX() - x, x - box.maxX()));
        int dz = Math.max(0, Math.max(box.minZ() - z, z - box.maxZ()));
        int dy = Math.max(0, Math.max(box.minY() - y, y - box.maxY()));
        return Mth.clampedMap(Mth.length(dx / 2.0, dy / 2.0, dz / 2.0), 0.0, 6.0, 1.0, 0.0) * 0.8 - getBuryContribution(dx, y - (box.minY() + rigid.groundLevelDelta()), dz);
    }
}
