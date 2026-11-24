package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public final class Facings {
    private Facings() {}

    public static EnumFacing along(boolean alongX, boolean positive) { return EnumFacing.getFacingFromAxis(positive ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE, alongX ? EnumFacing.Axis.X : EnumFacing.Axis.Z); }

    public static EnumFacing across(boolean alongX, boolean positive) { return along(!alongX, positive); }

    public static boolean alongX(EnumFacing facing) { return facing.getAxis() == EnumFacing.Axis.X; }

    public static int edge(StructureBoundingBox box, EnumFacing facing, int gap) {
        boolean positive = facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE;
        int at = alongX(facing) ? (positive ? box.maxX : box.minX) : (positive ? box.maxZ : box.minZ);
        return positive ? at + gap : at - gap;
    }
}
