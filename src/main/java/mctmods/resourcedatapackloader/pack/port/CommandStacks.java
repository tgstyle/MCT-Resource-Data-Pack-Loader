package mctmods.resourcedatapackloader.pack.port;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class CommandStacks {
    private static final Set<String> TILE_ENTITIES = Set.of("furnace", "chest", "ender_chest", "jukebox", "dispenser", "dropper", "sign", "mob_spawner", "noteblock", "piston", "brewing_stand", "enchanting_table", "end_portal", "beacon", "skull", "daylight_detector", "hopper", "comparator", "flower_pot", "banner", "structure_block", "end_gateway", "command_block", "shulker_box", "bed");
    private static final Map<String, String> TILE_BLOCKS = Map.ofEntries(
            Map.entry("lit_furnace", "furnace"), Map.entry("trapped_chest", "chest"), Map.entry("standing_sign", "sign"), Map.entry("wall_sign", "sign"), Map.entry("piston_extension", "piston"),
            Map.entry("daylight_detector_inverted", "daylight_detector"), Map.entry("unpowered_comparator", "comparator"), Map.entry("powered_comparator", "comparator"),
            Map.entry("standing_banner", "banner"), Map.entry("wall_banner", "banner"), Map.entry("chain_command_block", "command_block"), Map.entry("repeating_command_block", "command_block"));

    private CommandStacks() {}

    static String nbt(String given) {
        String parsed = Ids.nbt(given);
        if (parsed == null) { throw new Commands.Kept("its nbt " + given + " does not parse"); }
        return parsed;
    }

    static String item(String named, int meta, @Nullable String nbt, Ported pack) {
        String name = Commands.namespaced(named);
        if (pack.owns(name)) {
            String own = pack.ownItem(name, meta);
            if (own == null) { own = pack.ownBlock(name, meta); }
            return stack(own == null ? name : own, 0, nbt);
        }
        if (Ids.isModded(name)) {
            if (meta != 0) { throw new Commands.Kept("the data value " + meta + " of another mod's " + name + " has no twin on this version"); }
            return stack(name, 0, nbt);
        }
        return stack(name, meta, nbt);
    }

    private static String stack(String name, int meta, @Nullable String nbt) {
        if (nbt == null && Ids.isModded(name)) { return name; }
        String found = Ids.stack(name, meta, nbt);
        if (found == null) { throw new Commands.Kept("the item " + name + (nbt == null ? "" : " with the nbt " + nbt) + " could not be carried through the data fixers"); }
        return found;
    }

    static String itemTest(String named, int meta, @Nullable String nbt, Ported pack) {
        if (meta >= 0) { return item(named, meta, nbt, pack); }
        String name = Commands.namespaced(named);
        List<String> names = pack.owns(name) ? pack.ownItems(name) : Ids.isModded(name) ? List.of(name) : Ids.items(name);
        if (names.size() > 1) { throw new Commands.Kept(name + " with any data value is " + names.size() + " items on this version, and one test cannot name them all"); }
        return stack(names.getFirst(), 0, nbt);
    }

    static String block(String named, @Nullable String data, @Nullable String nbt, Ported pack) {
        String name = Commands.namespaced(named);
        int meta = 0;
        Map<String, String> legacy = null;
        if (data != null && !"default".equals(data)) {
            if (data.indexOf('=') > 0) { legacy = properties(data); }
            else { meta = Commands.number(data); }
        }
        if (pack.owns(name)) {
            if (legacy != null) { throw new Commands.Kept("the block state " + data + " of the pack's own " + name + " has no twin, since each variant is a block of its own"); }
            String own = pack.ownBlock(name, meta);
            if (own == null) { own = pack.ownItem(name, meta); }
            return (own == null ? name : own) + (nbt == null ? "" : nbt(nbt));
        }
        if (Ids.isModded(name)) {
            if (meta != 0) { throw new Commands.Kept("the data value " + meta + " of another mod's " + name + " has no twin on this version"); }
            return name + (legacy == null ? "" : state(legacy)) + (nbt == null ? "" : nbt(nbt));
        }
        Ids.Block found = legacy == null ? Ids.block(name, meta) : Ids.state(name, legacy);
        if (found == null) { throw new Commands.Kept("the block state " + data + " of " + name + " is not one whole 1.12.2 state the data fixers know"); }
        return found.name() + state(found.properties()) + (nbt == null ? "" : tileData(name, nbt));
    }

    static String blockTest(String named, @Nullable String data, @Nullable String nbt, Ported pack) {
        if (data != null && !"-1".equals(data) && !"*".equals(data)) { return block(named, data, nbt, pack); }
        String name = Commands.namespaced(named);
        List<String> names = pack.owns(name) ? pack.ownItems(name) : Ids.isModded(name) ? List.of(name) : Ids.blocks(name);
        if (names.size() > 1) { throw new Commands.Kept(name + " with any data value is " + names.size() + " blocks on this version, and one test cannot name them all"); }
        return names.getFirst() + (nbt == null ? "" : Ids.isModded(name) ? nbt(nbt) : tileData(name, nbt));
    }

    private static String tileData(String name, String nbt) {
        String path = name.substring(name.indexOf(':') + 1);
        String tile = path.endsWith("_shulker_box") ? "shulker_box" : TILE_BLOCKS.getOrDefault(path, path);
        if (!TILE_ENTITIES.contains(tile)) { return ""; }
        String fixed = Ids.blockEntityData(name, Commands.MINECRAFT + tile, nbt);
        if (fixed == null) { throw new Commands.Kept("the block nbt " + nbt + " could not be carried through the data fixers"); }
        return fixed;
    }

    private static Map<String, String> properties(String data) {
        Map<String, String> out = new LinkedHashMap<>();
        for (String pair : data.split(",")) {
            int equals = pair.indexOf('=');
            if (equals <= 0) { throw new Commands.Kept("the block state " + data + " is not a list of name=value pairs"); }
            out.put(pair.substring(0, equals), pair.substring(equals + 1));
        }
        return out;
    }

    private static String state(Map<String, String> properties) {
        if (properties.isEmpty()) { return ""; }
        List<String> pairs = new ArrayList<>();
        properties.forEach((key, value) -> pairs.add(key + "=" + value));
        return "[" + String.join(",", pairs) + "]";
    }
}
