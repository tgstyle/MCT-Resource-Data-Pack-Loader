package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.blocks.fluid.FluidTouch;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class FluidReactionDef {
    public final List<String> touching;
    public final List<Direction> faces;
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
    @Nullable private BlockState becomesState;
    @Nullable private BlockState sourceState;
    @Nullable private BlockState flowingState;

    public FluidReactionDef(List<String> touching, List<Direction> faces, float chance, int interval, String becomes, boolean drops, String source, String flowing, @Nullable Explosion explosion, String sound, String particles, String function) {
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

    public void resolve(FluidTouch touch, @Nullable BlockState becomes, @Nullable BlockState source, @Nullable BlockState flowing) {
        this.touch = touch;
        this.becomesState = becomes;
        this.sourceState = source;
        this.flowingState = flowing;
    }

    @Nullable public FluidTouch touch() { return touch; }

    @Nullable public BlockState becomesState() { return becomesState; }

    @Nullable public BlockState sourceState() { return sourceState; }

    @Nullable public BlockState flowingState() { return flowingState; }

    public record Explosion(float power, boolean fire, boolean grief, boolean atSelf) {}
}
