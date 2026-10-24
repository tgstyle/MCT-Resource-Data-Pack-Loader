package mctmods.resourcedatapackloader.content.def;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PortwayMenu {
    public static final String NEAREST = "nearest";
    public static final String NAME = "name";
    public static final String NEWEST = "newest";
    public static final List<String> ORDERS = Collections.unmodifiableList(Arrays.asList(NEAREST, NAME, NEWEST));
    public static final PortwayMenu PLAIN = new PortwayMenu(NEAREST, "minecraft:arrow", "minecraft:arrow", "minecraft:paper", "minecraft:compass", "minecraft:barrier");
    public final String order;
    public final String previous;
    public final String next;
    public final String page;
    public final String sort;
    public final String locked;

    public PortwayMenu(String order, String previous, String next, String page, String sort, String locked) {
        this.order = order;
        this.previous = previous;
        this.next = next;
        this.page = page;
        this.sort = sort;
        this.locked = locked;
    }

    public static String after(String order) { return ORDERS.get((Math.max(0, ORDERS.indexOf(order)) + 1) % ORDERS.size()); }
}
