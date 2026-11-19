package mctmods.resourcedatapackloader.common.pack.definitions.items;

public record LocatorDef(String place, boolean eye, String sound, int range) {
    public boolean empty() { return place.isEmpty(); }
}
