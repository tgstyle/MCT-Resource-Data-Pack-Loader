package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record DimensionPortalDef(List<String> frames, String ignitedBy, int color, String back, PortalDef travel) {
    public static final String BUILT = "built";
    public static final String PLAYER = "player";
    public static final String NONE = "none";

    public boolean buildsReturn() { return BUILT.equals(back); }

    public boolean lightsBack() { return !NONE.equals(back); }
}
