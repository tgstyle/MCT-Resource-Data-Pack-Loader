package mctmods.resourcedatapackloader.common.pack.definitions.portals;

import java.util.List;

public final class DimensionPortalDef {
    public static final String BUILT = "built";
    public static final String PLAYER = "player";
    public static final String NONE = "none";
    public final List<String> frames;
    public final String ignitedBy;
    public final String ignitedByFluid;
    public final int color;
    public final String back;
    public final PortalDef travel;

    public DimensionPortalDef(List<String> frames, String ignitedBy, String ignitedByFluid, int color, String back, PortalDef travel) {
        this.frames = frames;
        this.ignitedBy = ignitedBy;
        this.ignitedByFluid = ignitedByFluid;
        this.color = color;
        this.back = back;
        this.travel = travel;
    }

    public boolean noReturn() { return !BUILT.equals(back); }

    public boolean noLightBack() { return NONE.equals(back); }
}
