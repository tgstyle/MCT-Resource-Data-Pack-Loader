package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import java.util.Random;
import javax.annotation.Nonnull;

public class MergePiece extends StructureVillagePieces.Road {
    public static final String ID = "RdplMerge";
    private int fromCenter;
    private int toCenter;

    @SuppressWarnings("unused") public MergePiece() {}

    public MergePiece(StructureVillagePieces.Start start, StructureBoundingBox box, boolean alongX, int fromCenter, int toCenter) {
        super(start, 0);
        setCoordBaseMode(alongX ? EnumFacing.EAST : EnumFacing.SOUTH);
        this.boundingBox = box;
        this.fromCenter = fromCenter;
        this.toCenter = toCenter;
    }

    public boolean alongX() {
        EnumFacing facing = getCoordBaseMode();
        return facing != null && facing.getAxis() == EnumFacing.Axis.X;
    }

    public int rowLeast() { return alongX() ? boundingBox.minX : boundingBox.minZ; }

    public int rowMost() { return alongX() ? boundingBox.maxX : boundingBox.maxZ; }

    public int centerAt(int row) {
        int rows = rowMost() - rowLeast() + 1;
        int shift = toCenter - fromCenter;
        int steps = Math.abs(shift);
        if (steps == 0 || rows <= 1) { return toCenter; }
        int level = Math.min(steps, (int) Math.floor((row - rowLeast() + 0.5) * (steps + 1) / rows));
        return fromCenter + Integer.signum(shift) * level;
    }

    @Override protected void writeStructureToNBT(@Nonnull NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setInteger("RdplFrom", fromCenter);
        tag.setInteger("RdplTo", toCenter);
    }

    @Override protected void readStructureFromNBT(@Nonnull NBTTagCompound tag, @Nonnull TemplateManager templates) {
        super.readStructureFromNBT(tag, templates);
        fromCenter = tag.getInteger("RdplFrom");
        toCenter = tag.getInteger("RdplTo");
    }

    @Override public boolean addComponentParts(@Nonnull World world, @Nonnull Random random, @Nonnull StructureBoundingBox clip) {
        BeardRoads.paveMerge(this, world, clip);
        return true;
    }
}
