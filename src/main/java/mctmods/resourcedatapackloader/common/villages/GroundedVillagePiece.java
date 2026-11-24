package mctmods.resourcedatapackloader.common.villages;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public abstract class GroundedVillagePiece extends StructureVillagePieces.Village {
    protected GroundedVillagePiece() {}

    protected GroundedVillagePiece(StructureVillagePieces.Start start, int type) { super(start, type); }

    protected boolean settle(World world, StructureBoundingBox clip, int extra) {
        if (averageGroundLvl < 0) {
            averageGroundLvl = getAverageGroundLevel(world, clip);
            if (averageGroundLvl < 0) { return false; }
            boundingBox.offset(0, averageGroundLvl - boundingBox.minY + extra, 0);
        }
        return true;
    }
}
