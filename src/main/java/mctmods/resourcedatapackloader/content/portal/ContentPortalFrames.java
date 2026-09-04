package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.block.ContentBlockPortal;
import mctmods.resourcedatapackloader.content.def.PortalFrameDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentPortalFrames {
    private static final int BUDGET = 20000;
    private static final Map<String, PortalFrameDef> DEFS = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentPortalFrames() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        Json.eachFile(PackManager.PORTALFRAMES, "portal frame", (key, contents) -> {
            PortalFrameDef def = ContentParser.portalFrame(key, contents);
            if (def != null) { DEFS.put(key.toString(), def); }
        });
        if (!DEFS.isEmpty()) { ContentLog.LOGGER.info("Loaded {} portal frame(s): {}", DEFS.size(), DEFS.keySet()); }
    }

    @Nullable public static PortalFrameDef byName(String name) {
        load();
        PortalFrameDef held = DEFS.get(name);
        if (held != null) { return held; }
        return name.indexOf(':') >= 0 ? null : DEFS.get(new ResourceLocation(name).toString());
    }

    @Nullable public static PortalFit fit(World world, BlockPos candidate, PortalFrameDef frame) {
        int[] budget = { BUDGET };
        for (List<String> shape : PortalShapes.spread(frame)) {
            int rows = shape.size();
            int columns = shape.get(0).length();
            for (int stance = 0; stance < 2; stance++) {
                boolean flat = stance == 1;
                if (flat && !frame.liesFlat()) { continue; }
                if (!flat && !frame.standsUp()) { continue; }
                if (!PortalShapes.roomy(shape, frame, flat)) { continue; }
                for (int turn = 0; turn < 4; turn++) {
                    boolean alongX = turn < 2;
                    boolean mirrored = turn % 2 == 1;
                    for (int row = 0; row < rows; row++) {
                        String line = shape.get(row);
                        for (int column = 0; column < columns; column++) {
                            if (line.charAt(column) != PortalFrameDef.HOLE) { continue; }
                            BlockPos offset = PortalShapes.at(BlockPos.ORIGIN, column, row, rows, alongX, flat, mirrored);
                            BlockPos origin = candidate.subtract(offset);
                            PortalFit found = check(world, frame, shape, origin, rows, columns, alongX, flat, mirrored, budget);
                            if (found != null) { return found; }
                            if (budget[0] <= 0) {
                                ContentLog.LOGGER.debug("Looking for portal frame {} at {} gave up after {} tries, which a smaller limit would avoid", frame.registryName, candidate, BUDGET);
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable private static PortalFit check(World world, PortalFrameDef frame, List<String> shape, BlockPos origin, int rows, int columns, boolean alongX, boolean flat, boolean mirrored, int[] budget) {
        List<BlockPos> holes = new ArrayList<>();
        Map<BlockPos, IBlockState> edge = new LinkedHashMap<>();
        for (int row = 0; row < rows; row++) {
            String line = shape.get(row);
            for (int column = 0; column < columns; column++) {
                char held = line.charAt(column);
                if (held == PortalFrameDef.SKIP) { continue; }
                budget[0]--;
                if (budget[0] <= 0) { return null; }
                BlockPos at = PortalShapes.at(origin, column, row, rows, alongX, flat, mirrored);
                if (!world.isBlockLoaded(at)) { return null; }
                IBlockState found = world.getBlockState(at);
                if (held == PortalFrameDef.HOLE) {
                    if (found.getBlock() != Blocks.AIR && !(found.getBlock() instanceof ContentBlockPortal)) { return null; }
                    holes.add(at);
                    continue;
                }
                IBlockState wanted = frame.legend.get(held);
                if (wanted == null || !PortalShapes.matches(found, wanted)) { return null; }
                edge.put(at, wanted);
            }
        }
        if (holes.isEmpty()) { return null; }
        return new PortalFit(frame, holes, edge, alongX, flat, rows, columns);
    }
}
