package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

@SuppressWarnings("deprecation") public final class ContentLeavesBlock extends LeavesBlock {
    private final BlockDef def;

    public ContentLeavesBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    @Override public boolean skipRendering(@Nonnull BlockState state, @Nonnull BlockState adjacent, @Nonnull Direction direction) { return def.opaque() && adjacent.is(this) || super.skipRendering(state, adjacent, direction); }
}
