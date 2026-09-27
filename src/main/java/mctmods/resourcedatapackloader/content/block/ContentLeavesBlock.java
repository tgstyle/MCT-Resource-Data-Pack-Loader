package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public class ContentLeavesBlock extends LeavesBlock {
    private final BlockDef def;

    public ContentLeavesBlock(BlockDef def, Properties properties) {
        super(0.0F, properties);
        this.def = def;
    }

    @Override @Nonnull public MapCodec<? extends LeavesBlock> codec() { return MapCodec.unit(this); }

    @Override protected void spawnFallingLeavesParticle(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {}

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread(); }

    @Override protected boolean skipRendering(@Nonnull BlockState state, @Nonnull BlockState adjacent, @Nonnull Direction direction) { return def.opaque() && adjacent.is(this) || super.skipRendering(state, adjacent, direction); }
}
