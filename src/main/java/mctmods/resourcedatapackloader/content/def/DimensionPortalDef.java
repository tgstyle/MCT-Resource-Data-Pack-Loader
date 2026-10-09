package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record DimensionPortalDef(List<String> frames, String ignitedBy, int color, String back, PortalDef travel) {
    public static final String BUILT = "built";
    public static final String PLAYER = "player";
    public static final String NONE = "none";

    public boolean noReturn() { return !BUILT.equals(back); }

    public boolean noLightBack() { return NONE.equals(back); }
}
