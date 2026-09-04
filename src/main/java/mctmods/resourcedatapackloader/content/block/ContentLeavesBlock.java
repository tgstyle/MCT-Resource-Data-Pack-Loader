package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public class ContentLeavesBlock extends LeavesBlock {
    private static final int DEFAULT_FLAMMABILITY = 60;
    private static final int DEFAULT_SPREAD = 30;
    private final BlockDef def;

    public ContentLeavesBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public BlockDef getDef() { return def; }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return true; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0 ? def.flammability() : DEFAULT_FLAMMABILITY; }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread() > 0 ? def.fireSpread() : DEFAULT_SPREAD; }
}
