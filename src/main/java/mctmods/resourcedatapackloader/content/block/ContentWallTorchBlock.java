package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public final class ContentWallTorchBlock extends WallTorchBlock {
    private final BlockDef def;
    private final ParticleOptions flame;

    public ContentWallTorchBlock(BlockDef def, Properties properties) {
        super(ParticleTypes.FLAME, properties);
        this.def = def;
        this.flame = ContentTorchBlock.flame(def);
    }

    @Override public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        Direction facing = state.getValue(FACING).getOpposite();
        ContentTorchBlock.spawn(def, flame, level, pos.getX() + 0.5D + 0.27D * facing.getStepX(), pos.getY() + 0.7D + 0.22D, pos.getZ() + 0.5D + 0.27D * facing.getStepZ());
    }
}
