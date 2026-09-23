package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class CityPalette {
    private static final int MIX_MOST = 256;
    private static final Map<String, List<BlockState>> HELD = new ConcurrentHashMap<>();
    private static final Map<String, CityPalette> MIXES = new ConcurrentHashMap<>();
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
    private final BlockState[] drawn;

    private CityPalette(BlockState[] drawn) { this.drawn = drawn; }

    public static CityPalette of(String named, BlockState fallback) {
        CityPalette found = mixed(named);
        return found == null ? new CityPalette(new BlockState[] {fallback}) : found;
    }

    @Nullable public static CityPalette mixed(String named) {
        List<BlockState> found = drawn(named);
        if (found.isEmpty()) { return null; }
        return MIXES.computeIfAbsent(named.trim(), text -> new CityPalette(found.toArray(new BlockState[0])));
    }

    public static boolean holds(String named, BlockState held) {
        for (BlockState state : drawn(named)) {
            if (held.is(state.getBlock())) { return true; }
        }
        return false;
    }

    @Nullable public static BlockState state(String named) {
        List<BlockState> found = drawn(named);
        return found.isEmpty() ? null : found.get(0);
    }

    public static BlockState stateOr(String named, BlockState fallback) {
        BlockState found = state(named);
        return found == null ? fallback : found;
    }

    public static BlockState axised(BlockState state, boolean alongX) {
        Direction.Axis axis = alongX ? Direction.Axis.X : Direction.Axis.Z;
        if (state.hasProperty(BlockStateProperties.AXIS)) { return state.setValue(BlockStateProperties.AXIS, axis); }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) { return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, axis); }
        return state;
    }

    public static BlockState faced(BlockState state, Direction facing) {
        for (Property<?> property : state.getProperties()) {
            if ("facing".equals(property.getName()) && property instanceof DirectionProperty turned && turned.getPossibleValues().contains(facing)) { return state.setValue(turned, facing); }
        }
        return state;
    }

    public BlockState first() { return drawn[0]; }

    public BlockState pick(long seed, int x, int y, int z) {
        if (drawn.length == 1) { return drawn[0]; }
        return drawn[Math.floorMod(Hashes.mix(seed, x, y, z), drawn.length)];
    }

    public static void forget() {
        HELD.clear();
        MIXES.clear();
    }

    private static List<BlockState> drawn(String named) {
        String text = named.trim();
        if (text.isEmpty()) { return List.of(); }
        List<BlockState> held = HELD.get(text);
        if (held != null) { return held; }
        List<BlockState> found = new ArrayList<>();
        for (String part : Settings.entries(text)) {
            String entry = part.trim();
            if (entry.isEmpty()) { continue; }
            int weight = 1;
            int gap = entry.lastIndexOf(' ');
            if (gap > 0 && entry.lastIndexOf(']') < gap && entry.lastIndexOf('}') < gap) {
                try {
                    weight = Integer.parseInt(entry.substring(gap + 1).trim());
                    entry = entry.substring(0, gap).trim();
                }
                catch (NumberFormatException notWeight) { weight = 1; }
            }
            BlockState state = ContentStates.parse(entry, "'" + text + "'");
            if (state == null) {
                if (WARNED.add(text + "|" + entry)) { ContentLog.LOGGER.error("The city block '{}' names '{}', which is not a registered block, so it is left out of the mix", text, entry); }
                continue;
            }
            for (int at = 0; at < Math.max(1, weight) && found.size() < MIX_MOST; at++) { found.add(state); }
        }
        List<BlockState> made = List.copyOf(found);
        HELD.put(text, made);
        return made;
    }
}
