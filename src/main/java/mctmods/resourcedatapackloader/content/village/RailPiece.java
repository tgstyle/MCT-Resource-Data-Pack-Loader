package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RailPiece extends StructureVillagePieces.Road {
    public static final String ID = "RdplRail";
    private int line;
    @Nullable private BeardRoads.Grade grade;

    @SuppressWarnings("unused") public RailPiece() {}

    public RailPiece(StructureVillagePieces.Start start, StructureBoundingBox box, boolean alongX, int line) {
        super(start, 0);
        setCoordBaseMode(alongX ? EnumFacing.EAST : EnumFacing.SOUTH);
        this.boundingBox = box;
        this.line = line;
    }

    public boolean alongX() { return boundingBox.maxX - boundingBox.minX >= boundingBox.maxZ - boundingBox.minZ; }

    public int line() { return line; }

    public int rowLeast() { return alongX() ? boundingBox.minX : boundingBox.minZ; }

    public int rowMost() { return alongX() ? boundingBox.maxX : boundingBox.maxZ; }

    public int acrossLeast() { return alongX() ? boundingBox.minZ : boundingBox.minX; }

    public int acrossMost() { return alongX() ? boundingBox.maxZ : boundingBox.maxX; }

    @Nullable public BeardRoads.Grade grade(World world) {
        if (grade != null && (grade.start() != rowLeast() || grade.rows() != rowMost() - rowLeast() + 1)) { grade = null; }
        if (grade == null) { grade = BeardRails.profile(world, this); }
        return grade;
    }

    public void regrade() { grade = null; }

    @Override protected void writeStructureToNBT(@Nonnull NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setInteger("RdplLine", line);
        if (grade != null) { grade.write(tag); }
    }

    @Override protected void readStructureFromNBT(@Nonnull NBTTagCompound tag, @Nonnull TemplateManager templates) {
        super.readStructureFromNBT(tag, templates);
        line = tag.getInteger("RdplLine");
        grade = BeardRoads.Grade.read(tag);
    }

    @Override public boolean addComponentParts(@Nonnull World world, @Nonnull Random random, @Nonnull StructureBoundingBox clip) {
        BeardRails.lay(this, world, clip);
        return true;
    }
}
