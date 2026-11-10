package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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

    private boolean listed(Block block) {
        for (BlockMatch match : this.blocks) {
            if (match.matches(block)) { return true; }
        }
        return false;
    }

    public boolean found(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos().set(pos);
        for (int step = 1; step <= this.within; step++) {
            at.move(Direction.UP);
            if (!level.isLoaded(at)) { return false; }
            BlockState state = level.getBlockState(at);
            if (listed(state.getBlock())) { return true; }
            if (this.clear && state.getLightBlock(level, at) >= OPAQUE) { return false; }
        }
        return false;
    }
}
