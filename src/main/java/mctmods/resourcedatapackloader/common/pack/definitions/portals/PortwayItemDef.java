package mctmods.resourcedatapackloader.common.pack.definitions.portals;

public record PortwayItemDef(PortwayDef settings, boolean consume, boolean bind) { public static final PortwayItemDef PLAIN = new PortwayItemDef(PortwayDef.PLAIN, false, false); }
