package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.blocks.fluid.FluidTouch;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public final class FluidReactionDef {
    public final List<String> touching;
    public final List<EnumFacing> faces;
    public final float chance;
    public final int interval;
    public final String becomes;
    public final boolean drops;
    public final String source;
    public final String flowing;
    @Nullable public final Explosion explosion;
    public final String sound;
    public final String particles;
    public final String function;
    @Nullable private FluidTouch touch;
    @Nullable private IBlockState becomesState;
    @Nullable private IBlockState sourceState;
    @Nullable private IBlockState flowingState;

    public FluidReactionDef(List<String> touching, List<EnumFacing> faces, float chance, int interval, String becomes, boolean drops, String source, String flowing, @Nullable Explosion explosion, String sound, String particles, String function) {
        this.touching = touching;
        this.faces = faces;
        this.chance = chance;
        this.interval = interval;
        this.becomes = becomes;
        this.drops = drops;
        this.source = source;
        this.flowing = flowing;
        this.explosion = explosion;
        this.sound = sound;
        this.particles = particles;
        this.function = function;
    }

    public void resolve(FluidTouch touch, @Nullable IBlockState becomes, @Nullable IBlockState source, @Nullable IBlockState flowing) {
        this.touch = touch;
        this.becomesState = becomes;
        this.sourceState = source;
        this.flowingState = flowing;
    }

    @Nullable public FluidTouch touch() { return touch; }

    @Nullable public IBlockState becomesState() { return becomesState; }

    @Nullable public IBlockState sourceState() { return sourceState; }

    @Nullable public IBlockState flowingState() { return flowingState; }

    public static final class Explosion {
        public final float power;
        public final boolean fire;
        public final boolean grief;
        public final boolean atSelf;

        public Explosion(float power, boolean fire, boolean grief, boolean atSelf) {
            this.power = power;
            this.fire = fire;
            this.grief = grief;
            this.atSelf = atSelf;
        }
    }
}
