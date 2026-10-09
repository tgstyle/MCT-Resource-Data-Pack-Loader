package mctmods.resourcedatapackloader.util.world;

public final class BlockFlags {
    public static final int UPDATE_CLIENTS = 2;
    public static final int UPDATE_KNOWN_SHAPE = 16;
    public static final int UPDATE_CLIENTS_KNOWN_SHAPE = UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE;

    private BlockFlags() {}
}
