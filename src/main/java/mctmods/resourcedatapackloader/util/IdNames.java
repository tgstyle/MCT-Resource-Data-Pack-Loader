package mctmods.resourcedatapackloader.util;

public final class IdNames {
    public static final String MINECRAFT = "minecraft:";

    private IdNames() {}

    public static String path(String id) { return id.substring(id.indexOf(':') + 1); }

    public static String namespaced(String id) { return id.indexOf(':') < 0 ? MINECRAFT + id : id; }

    public static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
