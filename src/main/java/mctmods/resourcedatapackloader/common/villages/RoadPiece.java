package mctmods.resourcedatapackloader.common.villages;

import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.BeardPlots;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public abstract class RoadPiece extends StructureVillagePieces.Road {
    protected RoadPiece() {}

    protected RoadPiece(StructureVillagePieces.Start start) { super(start, 0); }

    public abstract boolean alongX();

    public int rowLeast() { return BeardPlots.least(boundingBox, alongX()); }

    public int rowMost() { return BeardPlots.most(boundingBox, alongX()); }

    protected final void lay(StructureBoundingBox box, boolean alongX) {
        setCoordBaseMode(alongX ? EnumFacing.EAST : EnumFacing.SOUTH);
        this.boundingBox = box;
    }
}
