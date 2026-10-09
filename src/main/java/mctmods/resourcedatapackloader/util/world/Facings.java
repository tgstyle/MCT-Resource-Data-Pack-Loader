package mctmods.resourcedatapackloader.util.world;

import net.minecraft.util.EnumFacing;

public final class Facings {
    private Facings() {}

    public static EnumFacing along(boolean alongX, boolean positive) {
        return EnumFacing.getFacingFromAxis(positive ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE, alongX ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
    }

    public static EnumFacing across(boolean alongX, boolean positive) { return along(!alongX, positive); }
}
