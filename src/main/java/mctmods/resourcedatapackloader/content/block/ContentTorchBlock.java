package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

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

public class ContentTorchBlock extends TorchBlock {
    private final BlockDef def;
    private final ParticleOptions flame;

    public ContentTorchBlock(BlockDef def, Properties properties) {
        super(ParticleTypes.FLAME, properties);
        this.def = def;
        this.flame = flame(def);
    }

    public BlockDef getDef() { return def; }

    static ParticleOptions flame(BlockDef def) {
        if (!BlockDef.PARTICLE_COLORED.equals(def.torchParticle())) { return ParticleTypes.FLAME; }
        int color = def.torchColor();
        return new DustParticleOptions(new Vector3f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F), 1.0F);
    }

    static boolean particles(BlockDef def) { return !BlockDef.PARTICLE_NONE.equals(def.torchParticle()); }

    @Override public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.7D;
        double z = pos.getZ() + 0.5D;
        if (def.torchSmoke()) { level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D); }
        if (particles(def)) { level.addParticle(flame, x, y, z, 0.0D, 0.0D, 0.0D); }
    }
}
