package mctmods.resourcedatapackloader.common.pack.definitions.portals;

public final class PortwayItemDef {
    public static final PortwayItemDef PLAIN = new PortwayItemDef(PortwayDef.PLAIN, false, false);
    public final PortwayDef settings;
    public final boolean consume;
    public final boolean bind;

    public PortwayItemDef(PortwayDef settings, boolean consume, boolean bind) {
        this.settings = settings;
        this.consume = consume;
        this.bind = bind;
    }
}
