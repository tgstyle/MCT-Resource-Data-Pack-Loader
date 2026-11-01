package mctmods.resourcedatapackloader.common.portals;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalFrameDef;

import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record PortalFit(PortalFrameDef frame, List<BlockPos> holes, Map<BlockPos, BlockState> edge, boolean alongX, boolean flat, int rows, int columns) {
    public int size() { return holes.size(); }
}
