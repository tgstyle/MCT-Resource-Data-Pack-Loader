package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class WaiverDef {
    public static final int DEFAULT_WITHIN = 8;
    public static final int MAX_WITHIN = 64;
    public static final float DEFAULT_SCALE = 1.0F;
    private static final int OPAQUE = 15;
    private final List<BlockMatch> blocks;
    private final int within;
    public final float scale;
    private final boolean[] kinds;
    private final boolean clear;

    public WaiverDef(List<BlockMatch> blocks, int within, float scale, boolean[] kinds, boolean clear) {
        this.blocks = blocks;
        this.within = within;
        this.scale = scale;
        this.kinds = kinds;
        this.clear = clear;
    }

    public static boolean[] bothKinds() {
        boolean[] kinds = new boolean[GrowSwitchDef.KINDS];
        kinds[GrowSwitchDef.CROPS] = true;
        kinds[GrowSwitchDef.SAPLINGS] = true;
        return kinds;
    }

    public boolean covers(int kind) { return this.kinds[kind]; }

    private boolean listed(Block block, World world, BlockPos pos) { return BlockMatch.anyMatch(this.blocks, block, world, pos); }

    public boolean found(World world, BlockPos pos) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos(pos);
        for (int step = 1; step <= this.within; step++) {
            at.move(EnumFacing.UP);
            if (!world.isBlockLoaded(at)) { return false; }
            IBlockState state = world.getBlockState(at);
            if (listed(state.getBlock(), world, pos)) { return true; }
            if (this.clear && state.getLightOpacity(world, at) >= OPAQUE) { return false; }
        }
        return false;
    }
}
