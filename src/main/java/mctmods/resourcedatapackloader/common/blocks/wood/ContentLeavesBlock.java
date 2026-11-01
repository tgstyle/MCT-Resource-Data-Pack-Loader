package mctmods.resourcedatapackloader.common.blocks.wood;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import javax.annotation.Nonnull;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation") public final class ContentLeavesBlock extends LeavesBlock {
    private final BlockDef def;

    public ContentLeavesBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public boolean solidLayer() { return "solid".equals(def.renderLayer()); }

    @Override public boolean skipRendering(@Nonnull BlockState state, @Nonnull BlockState adjacent, @Nonnull Direction direction) { return def.opaque() && adjacent.is(this) || super.skipRendering(state, adjacent, direction); }
}
