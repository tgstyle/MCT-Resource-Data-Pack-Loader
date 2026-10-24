package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.def.PortwayMenu;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class PortwayOrder {
    private static final String ORDER = "order";
    private static final Comparator<Entry> NEAREST = Comparator.comparingInt((Entry entry) -> entry.elsewhere)
            .thenComparingInt(entry -> entry.stop.dimension).thenComparingDouble(entry -> entry.near).thenComparingLong(entry -> entry.stop.pos.toLong());
    private static final Comparator<Entry> NAME = Comparator.comparingInt((Entry entry) -> entry.label.isEmpty() ? 1 : 0)
            .thenComparing(entry -> entry.key).thenComparingInt(entry -> entry.stop.dimension)
            .thenComparingInt(entry -> entry.stop.pos.getX()).thenComparingInt(entry -> entry.stop.pos.getY()).thenComparingInt(entry -> entry.stop.pos.getZ());
    private static final Comparator<Entry> NEWEST = Comparator.comparingLong((Entry entry) -> entry.stop.found).reversed();

    private PortwayOrder() {}

    public static final class Entry {
        public final PortwayStop stop;
        public final String label;
        final String key;
        final String search;
        final int elsewhere;
        final double near;

        Entry(PortwayStop stop, String label, String search, int elsewhere, double near) {
            this.stop = stop;
            this.label = label;
            this.elsewhere = elsewhere;
            this.near = near;
            this.key = label.toLowerCase(Locale.ROOT);
            this.search = search.toLowerCase(Locale.ROOT);
        }
    }

    public static List<Entry> entries(EntityPlayer player, List<PortwayStop> stops) {
        List<String> labels = Portways.labels(player, stops);
        int here = player.world.provider.getDimension();
        List<Entry> entries = new ArrayList<>(stops.size());
        for (int index = 0; index < stops.size(); index++) {
            PortwayStop stop = stops.get(index);
            String label = labels.get(index);
            String search = label.isEmpty() ? Lang.tr(player, "rdpl.portway.stop", Portways.dimensionName(stop.dimension), stop.pos.getX(), stop.pos.getY(), stop.pos.getZ()) : label;
            boolean local = stop.dimension == here;
            entries.add(new Entry(stop, label, search, local ? 0 : 1, local ? player.getDistanceSqToCenter(stop.pos) : 0.0D));
        }
        return entries;
    }

    public static void arrange(List<Entry> entries, String order) {
        entries.sort(NEAREST);
        if (PortwayMenu.NAME.equals(order)) { entries.sort(NAME); }
        else if (PortwayMenu.NEWEST.equals(order)) { entries.sort(NEWEST); }
    }

    public static List<Entry> matching(List<Entry> sorted, String filter) {
        if (filter.isEmpty()) { return sorted; }
        String wanted = filter.toLowerCase(Locale.ROOT);
        List<Entry> kept = new ArrayList<>();
        for (Entry entry : sorted) {
            if (entry.search.contains(wanted)) { kept.add(entry); }
        }
        return kept;
    }

    public static String saved(EntityPlayer player, String fallback) {
        String order = PlayerPersisted.read(player, Portways.SECTION).getString(ORDER);
        return PortwayMenu.ORDERS.contains(order) ? order : fallback;
    }

    public static void save(EntityPlayer player, String order) { PlayerPersisted.section(player, Portways.SECTION).setString(ORDER, order); }
}
