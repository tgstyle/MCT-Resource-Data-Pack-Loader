package mctmods.resourcedatapackloader.common.pack.definitions.items;

public final class LocatorDef {
    public final String place;
    public final boolean eye;
    public final String sound;
    public final int range;

    public LocatorDef(String place, boolean eye, String sound, int range) {
        this.place = place;
        this.eye = eye;
        this.sound = sound;
        this.range = range;
    }
}
