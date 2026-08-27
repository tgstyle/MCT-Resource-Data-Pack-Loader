package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public final class DimensionPortalDef {
    public static final String BUILT = "built";
    public static final String PLAYER = "player";
    public static final String NONE = "none";
    public final List<String> frames;
    public final String ignitedBy;
    public final int color;
    public final String back;
    public final PortalDef travel;

    public DimensionPortalDef(List<String> frames, String ignitedBy, int color, String back, PortalDef travel) {
        this.frames = frames;
        this.ignitedBy = ignitedBy;
        this.color = color;
        this.back = back;
        this.travel = travel;
    }

    public boolean buildsReturn() { return BUILT.equals(back); }

    public boolean lightsBack() { return !NONE.equals(back); }
}
