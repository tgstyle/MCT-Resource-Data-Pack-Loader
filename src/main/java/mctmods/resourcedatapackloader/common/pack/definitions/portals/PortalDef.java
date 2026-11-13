package mctmods.resourcedatapackloader.common.pack.definitions.portals;

public final class PortalDef {
    public final int dimension;
    public final int returnDimension;
    public final String gate;
    public final int cooldown;
    public final boolean platform;
    public final String platformBlock;
    public final String sound;
    public final boolean owned;
    public final boolean walkIn;
    public final boolean sameLanding;
    public final boolean torches;
    public final int useRange;
    public final boolean mounted;
    public final boolean returnToOrigin;

    public PortalDef(int dimension, int returnDimension, String gate, int cooldown, boolean platform, String platformBlock, String sound, boolean owned, boolean walkIn, boolean sameLanding, boolean torches, int useRange, boolean mounted, boolean returnToOrigin) {
        this.dimension = dimension;
        this.returnDimension = returnDimension;
        this.gate = gate;
        this.cooldown = cooldown;
        this.platform = platform;
        this.platformBlock = platformBlock;
        this.sound = sound;
        this.owned = owned;
        this.walkIn = walkIn;
        this.sameLanding = sameLanding;
        this.torches = torches;
        this.useRange = useRange;
        this.mounted = mounted;
        this.returnToOrigin = returnToOrigin;
    }
}
