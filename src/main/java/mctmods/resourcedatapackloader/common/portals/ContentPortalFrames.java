package mctmods.resourcedatapackloader.common.portals;

import mctmods.resourcedatapackloader.common.blocks.fluid.FluidTouch;
import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortalBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalFrameDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorlds;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public final class ContentPortalFrames {
    private static final List<String> FRAME_KEYS = Collections.unmodifiableList(Arrays.asList("legend", "rows", "axis", "name", "maxWidth", "maxHeight"));
    private static final int BUDGET = 20000;
    private static final Map<String, PortalFrameDef> DEFS = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentPortalFrames() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        Json.eachFile(PackManager.PORTALFRAMES, "portal frame", (key, contents) -> {
            JsonObject json = Json.GSON.fromJson(contents, JsonObject.class);
            if (json != null) { Json.unknownKeys(json, "the top level", FRAME_KEYS); }
            PortalFrameDef def = ContentParserWorlds.portalFrame(key, contents);
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

    @Nullable public static PortalFit fit(World world, BlockPos candidate, PortalFrameDef frame) { return fit(world, candidate, frame, null); }

    @Nullable public static PortalFit fit(World world, BlockPos candidate, PortalFrameDef frame, @Nullable BlockPos broken) { return fit(world, candidate, frame, broken, null); }

    @Nullable public static PortalFit fit(World world, BlockPos candidate, PortalFrameDef frame, @Nullable BlockPos broken, @Nullable Fluid wet) {
        int[] budget = { BUDGET };
        for (List<String> shape : PortalShapes.spread(frame)) {
            int rows = shape.size();
            int columns = shape.get(0).length();
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
                            BlockPos offset = PortalShapes.at(BlockPos.ORIGIN, column, row, rows, alongX, flat, mirrored);
                            BlockPos origin = candidate.subtract(offset);
                            PortalFit found = check(world, frame, shape, origin, rows, columns, alongX, flat, mirrored, budget, broken, wet);
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

    @Nullable private static PortalFit check(World world, PortalFrameDef frame, List<String> shape, BlockPos origin, int rows, int columns, boolean alongX, boolean flat, boolean mirrored, int[] budget, @Nullable BlockPos broken, @Nullable Fluid wet) {
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
                IBlockState found = at.equals(broken) ? Blocks.AIR.getDefaultState() : world.getBlockState(at);
                if (held == PortalFrameDef.HOLE) {
                    if (!found.getBlock().isAir(found, world, at) && !(found.getBlock() instanceof ContentPortalBlock) && (wet == null || FluidTouch.fluidOf(found.getBlock()) != wet)) { return null; }
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
