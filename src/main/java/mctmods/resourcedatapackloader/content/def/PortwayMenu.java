package mctmods.resourcedatapackloader.content.def;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PortwayMenu {
    public static final String NEAREST = "nearest";
    public static final String NAME = "name";
    public static final String NEWEST = "newest";
    public static final List<String> ORDERS = Collections.unmodifiableList(Arrays.asList(NEAREST, NAME, NEWEST));
    public static final int BOTTOM = 45;
    public static final int WIDTH = 9;
    public static final int PREVIOUS_COLUMN = 0;
    public static final int SORT_COLUMN = 2;
    public static final int PAGE_COLUMN = 4;
    public static final int NEXT_COLUMN = 8;
    public static final int SCOPE_COLUMN = 6;
    public static final PortwayMenu PLAIN = new PortwayMenu(NEAREST, "minecraft:arrow", "minecraft:arrow", "minecraft:paper", "minecraft:compass", "minecraft:barrier", "minecraft:ender_eye", BOTTOM + PREVIOUS_COLUMN, BOTTOM + SORT_COLUMN, BOTTOM + PAGE_COLUMN, BOTTOM + NEXT_COLUMN, BOTTOM + SCOPE_COLUMN);
    public final String order;
    public final String previous;
    public final String next;
    public final String page;
    public final String sort;
    public final String locked;
    public final String scope;
    public final int previousAt;
    public final int sortAt;
    public final int pageAt;
    public final int nextAt;
    public final int scopeAt;

    public PortwayMenu(String order, String previous, String next, String page, String sort, String locked, String scope, int previousAt, int sortAt, int pageAt, int nextAt, int scopeAt) {
        this.order = order;
        this.previous = previous;
        this.next = next;
        this.page = page;
        this.sort = sort;
        this.locked = locked;
        this.scope = scope;
        this.previousAt = previousAt;
        this.sortAt = sortAt;
        this.pageAt = pageAt;
        this.nextAt = nextAt;
        this.scopeAt = scopeAt;
    }

    public static String after(String order) { return ORDERS.get((Math.max(0, ORDERS.indexOf(order)) + 1) % ORDERS.size()); }
}
