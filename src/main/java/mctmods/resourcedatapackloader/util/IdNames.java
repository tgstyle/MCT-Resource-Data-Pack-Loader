package mctmods.resourcedatapackloader.util;

public final class IdNames {
    public static final String NAMESPACE = "minecraft";
    public static final String MINECRAFT = NAMESPACE + ":";

    private IdNames() {}

    public static String path(String id) { return id.substring(id.indexOf(':') + 1); }

    public static String namespace(String id) { int colon = id.indexOf(':'); return colon < 0 ? NAMESPACE : id.substring(0, colon); }

    public static String namespaced(String id) { return id.indexOf(':') < 0 ? MINECRAFT + id : id; }

    public static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
