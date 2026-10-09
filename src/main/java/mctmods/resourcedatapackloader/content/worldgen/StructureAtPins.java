package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class StructureAtPins {
    private static volatile Parsed last = new Parsed(null, Map.of());

    private StructureAtPins() {}

    static Map<String, List<int[]>> all() {
        List<String> entries = ContentControl.list(ContentControl.STRUCTURES, "structureAt", Config.worldgen.structureAt());
        Parsed held = last;
        if (entries.equals(held.entries())) { return held.pins(); }
        Map<String, List<int[]>> pins = new LinkedHashMap<>();
        for (String entry : entries) {
            String[] parts = ContentStructureControl.split(entry, "structureAt");
            if (parts == null) { continue; }
            String[] xz = parts[1].split(",");
            if (xz.length != 2) {
                ContentLog.LOGGER.error("structureAt entry '{}' is not written as structure=x,z", entry);
                continue;
            }
            try { pins.computeIfAbsent(parts[0], _ -> new ArrayList<>()).add(new int[] {Integer.parseInt(xz[0].trim()), Integer.parseInt(xz[1].trim())}); }
            catch (NumberFormatException notNumbers) { ContentLog.LOGGER.error("structureAt entry '{}' does not name two whole numbers", entry); }
        }
        last = new Parsed(List.copyOf(entries), pins);
        return pins;
    }

    static List<int[]> of(String named) { return all().getOrDefault(named, List.of()); }

    private record Parsed(List<String> entries, Map<String, List<int[]>> pins) {}
}
