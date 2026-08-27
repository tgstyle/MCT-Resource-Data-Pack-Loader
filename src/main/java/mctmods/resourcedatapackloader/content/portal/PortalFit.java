package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.def.PortalFrameDef;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.Map;

public final class PortalFit {
    public final PortalFrameDef frame;
    public final List<BlockPos> holes;
    public final Map<BlockPos, IBlockState> edge;
    public final boolean alongX;
    public final boolean flat;
    public final int rows;
    public final int columns;

    public PortalFit(PortalFrameDef frame, List<BlockPos> holes, Map<BlockPos, IBlockState> edge, boolean alongX, boolean flat, int rows, int columns) {
        this.frame = frame;
        this.holes = holes;
        this.edge = edge;
        this.alongX = alongX;
        this.flat = flat;
        this.rows = rows;
        this.columns = columns;
    }

    public int size() { return holes.size(); }
}
