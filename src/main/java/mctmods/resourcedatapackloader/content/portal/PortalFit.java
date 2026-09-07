package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.def.PortalFrameDef;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.Map;

public record PortalFit(PortalFrameDef frame, List<BlockPos> holes, Map<BlockPos, BlockState> edge, boolean alongX, boolean flat, int rows, int columns) {
    public int size() { return holes.size(); }
}
