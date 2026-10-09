package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.block.ContentPortalBlock;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.PortalFrameDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public final class ContentPortalFrames {
    private static final int BUDGET = 20000;
    private static final Map<String, PortalFrameDef> DEFS = new LinkedHashMap<>();
    private static volatile Map<BlockMatchDef, List<BlockState>> states = Map.of();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentPortalFrames() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        states = Map.of();
        if (Config.definitionsOff()) { return; }
        Json.eachFile(PackManager.PORTALFRAMES, "portal frame", (key, contents) -> {
            PortalFrameDef def = parse(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.info("Loaded {} portal frame(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    @Nullable private static PortalFrameDef parse(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "portal frame file");
        if (json == null) { return null; }
        Map<Character, BlockMatchDef> legend = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> mark : GsonHelper.getAsJsonObject(json, "legend", new JsonObject()).entrySet()) {
            String symbol = mark.getKey().trim();
            if (symbol.length() != 1 || symbol.charAt(0) == PortalFrameDef.HOLE || symbol.charAt(0) == PortalFrameDef.REPEAT || symbol.charAt(0) == PortalFrameDef.SKIP) {
                ContentLog.LOGGER.error("Portal frame {} legend symbol '{}' must be a single character other than '.', '*' and a space", key, mark.getKey());
                continue;
            }
            BlockMatchDef match = ContentParser.match(key, mark.getValue());
            if (match != null) { legend.put(symbol.charAt(0), match); }
        }
        List<String> rows = Json.strings(json, "rows");
        if (rows.isEmpty()) {
            ContentLog.LOGGER.error("Portal frame {} draws no rows, so there is no frame to find", key);
            return null;
        }
        boolean holed = false;
        for (String row : rows) {
            for (char held : row.toCharArray()) {
                if (held == PortalFrameDef.HOLE) { holed = true; }
                if (held == PortalFrameDef.HOLE || held == PortalFrameDef.SKIP || held == PortalFrameDef.REPEAT || legend.containsKey(held)) { continue; }
                ContentLog.LOGGER.error("Portal frame {} uses '{}', which is neither a hole, a gap, a repeat nor in the legend", key, held);
                return null;
            }
        }
        boolean repeating = false;
        for (String row : rows) { repeating |= row.indexOf(PortalFrameDef.REPEAT) >= 0; }
        if (!repeating) {
            for (String row : rows) {
                if (row.length() == rows.getFirst().length()) { continue; }
                ContentLog.LOGGER.error("Portal frame {} has rows of different lengths and no repeat mark, so it is skipped", key);
                return null;
            }
        }
        if (!holed) {
            ContentLog.LOGGER.error("Portal frame {} has no '{}' in it, so nothing would ever stand inside it", key, PortalFrameDef.HOLE);
            return null;
        }
        String axis = Settings.lower(GsonHelper.getAsString(json, "axis", PortalFrameDef.VERTICAL));
        if (!PortalFrameDef.VERTICAL.equals(axis) && !PortalFrameDef.HORIZONTAL.equals(axis) && !PortalFrameDef.BOTH.equals(axis)) {
            ContentLog.LOGGER.error("Portal frame {} asks for axis '{}', which is none of {}, {} or {}, standing it up instead", key, axis, PortalFrameDef.VERTICAL, PortalFrameDef.HORIZONTAL, PortalFrameDef.BOTH);
            axis = PortalFrameDef.VERTICAL;
        }
        PortalFrameDef frame = new PortalFrameDef(key, GsonHelper.getAsString(json, "name", key.getPath()), axis, Map.copyOf(legend), rows,
                Math.max(PortalFrameDef.LEAST_WIDE, GsonHelper.getAsInt(json, "maxWidth", 21)), Math.max(PortalFrameDef.LEAST_TALL, GsonHelper.getAsInt(json, "maxHeight", 21)));
        if (PortalShapes.spread(frame).isEmpty()) {
            ContentLog.LOGGER.error("Portal frame {} never leaves room for a player, who needs a hole {} across and {} up, so nothing could walk through it", key, PortalFrameDef.LEAST_WIDE, frame.leastTall());
            return null;
        }
        return frame;
    }

    @Nullable public static PortalFrameDef byName(String name) {
        PortalFrameDef held = DEFS.get(name);
        if (held != null) { return held; }
        Identifier id = name.indexOf(':') >= 0 ? null : Identifier.tryParse(name);
        return id == null ? null : DEFS.get(id.toString());
    }

    public static boolean matches(BlockState found, BlockMatchDef wanted) {
        List<BlockState> held = states.get(wanted);
        return held != null && held.contains(found);
    }

    public static void resolve() {
        Map<BlockMatchDef, List<BlockState>> made = new HashMap<>();
        for (PortalFrameDef frame : DEFS.values()) {
            for (BlockMatchDef wanted : frame.legend().values()) {
                if (made.containsKey(wanted)) { continue; }
                Block block = Registered.find(BuiltInRegistries.BLOCK, wanted.block());
                made.put(wanted, block == null ? List.of() : ContentStates.matching(block, wanted.properties(), "portal frame legend " + wanted.block()));
            }
        }
        states = Map.copyOf(made);
    }

    @Nullable public static PortalFit fit(Level level, BlockPos candidate, PortalFrameDef frame) { return fit(level, candidate, frame, null); }

    @Nullable public static PortalFit fit(Level level, BlockPos candidate, PortalFrameDef frame, @Nullable BlockPos broken) {
        int[] budget = { BUDGET };
        for (List<String> shape : PortalShapes.spread(frame)) {
            int rows = shape.size();
            int columns = shape.getFirst().length();
            for (int stance = 0; stance < 2; stance++) {
                boolean flat = stance == 1;
                if (flat && !frame.liesFlat()) { continue; }
                if (!flat && frame.flatOnly()) { continue; }
                if (PortalShapes.cramped(shape, frame, flat)) { continue; }
                for (int turn = 0; turn < 4; turn++) {
                    boolean alongX = turn < 2;
                    boolean mirrored = turn % 2 == 1;
                    for (int row = 0; row < rows; row++) {
                        String line = shape.get(row);
                        for (int column = 0; column < columns; column++) {
                            if (line.charAt(column) != PortalFrameDef.HOLE) { continue; }
                            BlockPos offset = PortalShapes.at(BlockPos.ZERO, column, row, rows, alongX, flat, mirrored);
                            BlockPos origin = candidate.subtract(offset);
                            PortalFit found = check(level, frame, shape, origin, rows, columns, alongX, flat, mirrored, budget, broken);
                            if (found != null) { return found; }
                            if (budget[0] <= 0) {
                                ContentLog.LOGGER.debug("Looking for portal frame {} at {} gave up after {} tries, which a smaller limit would avoid", frame.key(), candidate, BUDGET);
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable private static PortalFit check(Level level, PortalFrameDef frame, List<String> shape, BlockPos origin, int rows, int columns, boolean alongX, boolean flat, boolean mirrored, int[] budget, @Nullable BlockPos broken) {
        List<BlockPos> holes = new ArrayList<>();
        Map<BlockPos, BlockState> edge = new LinkedHashMap<>();
        for (int row = 0; row < rows; row++) {
            String line = shape.get(row);
            for (int column = 0; column < columns; column++) {
                char held = line.charAt(column);
                if (held == PortalFrameDef.SKIP) { continue; }
                budget[0]--;
                if (budget[0] <= 0) { return null; }
                BlockPos at = PortalShapes.at(origin, column, row, rows, alongX, flat, mirrored);
                if (!level.isLoaded(at)) { return null; }
                BlockState found = Objects.equals(at, broken) ? Blocks.AIR.defaultBlockState() : level.getBlockState(at);
                if (held == PortalFrameDef.HOLE) {
                    if (!found.isAir() && !(found.getBlock() instanceof ContentPortalBlock)) { return null; }
                    holes.add(at);
                    continue;
                }
                BlockMatchDef wanted = frame.legend().get(held);
                if (wanted == null || !matches(found, wanted)) { return null; }
                edge.put(at, found);
            }
        }
        if (holes.isEmpty()) { return null; }
        return new PortalFit(frame, holes, edge, alongX, flat, rows, columns);
    }
}
