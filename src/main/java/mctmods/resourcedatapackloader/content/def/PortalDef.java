package mctmods.resourcedatapackloader.content.def;


public final class PortalDef {
    public final int dimension;
    public final int returnDimension;
    public final String gate;
    public final int cooldown;
    public final boolean platform;
    public final String platformBlock;
    public final String sound;
    public final boolean owned;

    public PortalDef(int dimension, int returnDimension, String gate, int cooldown, boolean platform, String platformBlock, String sound, boolean owned) {
        this.dimension = dimension;
        this.returnDimension = returnDimension;
        this.gate = gate;
        this.cooldown = cooldown;
        this.platform = platform;
        this.platformBlock = platformBlock;
        this.sound = sound;
        this.owned = owned;
    }
}
