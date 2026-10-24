package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record PortwayMenu(String order, String previous, String next, String page, String sort, String locked) {
    public static final String NEAREST = "nearest";
    public static final String NAME = "name";
    public static final String NEWEST = "newest";
    public static final List<String> ORDERS = List.of(NEAREST, NAME, NEWEST);
    public static final PortwayMenu PLAIN = new PortwayMenu(NEAREST, "minecraft:arrow", "minecraft:arrow", "minecraft:paper", "minecraft:compass", "minecraft:barrier");

    public static String after(String order) { return ORDERS.get((Math.max(0, ORDERS.indexOf(order)) + 1) % ORDERS.size()); }
}
