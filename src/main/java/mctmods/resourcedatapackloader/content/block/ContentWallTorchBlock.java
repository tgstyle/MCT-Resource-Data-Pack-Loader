package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public class ContentWallTorchBlock extends WallTorchBlock {
    private final BlockDef def;

    public ContentWallTorchBlock(BlockDef def, Properties properties) {
        super(properties, ContentTorchBlock.flame(def));
        this.def = def;
    }

    public BlockDef getDef() { return def; }

    @Override public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        Direction facing = state.getValue(FACING).getOpposite();
        double x = pos.getX() + 0.5D + 0.27D * facing.getStepX();
        double y = pos.getY() + 0.7D + 0.22D;
        double z = pos.getZ() + 0.5D + 0.27D * facing.getStepZ();
        if (def.torchSmoke()) { level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D); }
        if (ContentTorchBlock.particles(def)) { level.addParticle(flameParticle, x, y, z, 0.0D, 0.0D, 0.0D); }
    }
}
