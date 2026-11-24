package mctmods.resourcedatapackloader.common.util.world;

import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import net.minecraft.world.gen.structure.StructureBoundingBox;

public final class StructureBoxes {
    private StructureBoxes() {}

    public static int centerX(StructureBoundingBox box) { return MathUtil.mid(box.minX, box.maxX); }

    public static int centerZ(StructureBoundingBox box) { return MathUtil.mid(box.minZ, box.maxZ); }

    public static int across(StructureBoundingBox box, boolean alongX) { return alongX ? centerZ(box) : centerX(box); }
}
