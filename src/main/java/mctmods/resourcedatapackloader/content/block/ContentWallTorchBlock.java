package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public final class ContentWallTorchBlock extends WallTorchBlock {
    private final BlockDef def;

    public ContentWallTorchBlock(BlockDef def, Properties properties) {
        super(properties, ContentTorchBlock.flame(def));
        this.def = def;
    }

    @Override public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        Direction facing = state.getValue(FACING).getOpposite();
        ContentTorchBlock.spawn(def, flameParticle, level, pos.getX() + 0.5D + 0.27D * facing.getStepX(), pos.getY() + 0.7D + 0.22D, pos.getZ() + 0.5D + 0.27D * facing.getStepZ());
    }
}
