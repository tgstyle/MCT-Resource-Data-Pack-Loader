package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.util.Rgb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import javax.annotation.Nonnull;

public final class ContentTorchBlock extends TorchBlock {
    private final BlockDef def;

    public ContentTorchBlock(BlockDef def, Properties properties) {
        super(properties, flame(def));
        this.def = def;
    }

    static ParticleOptions flame(BlockDef def) {
        if (!BlockDef.PARTICLE_COLORED.equals(def.torchParticle())) { return ParticleTypes.FLAME; }
        int color = def.torchColor();
        return new DustParticleOptions(new Vector3f(Rgb.red(color), Rgb.green(color), Rgb.blue(color)), 1.0F);
    }

    static void spawn(BlockDef def, ParticleOptions flame, Level level, double x, double y, double z) {
        if (def.torchSmoke()) { level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D); }
        if (!BlockDef.PARTICLE_NONE.equals(def.torchParticle())) { level.addParticle(flame, x, y, z, 0.0D, 0.0D, 0.0D); }
    }

    @Override public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) { spawn(def, flameParticle, level, pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D); }
}
