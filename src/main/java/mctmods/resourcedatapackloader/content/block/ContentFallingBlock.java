package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public class ContentFallingBlock extends FallingBlock {
    private final BlockDef def;

    public ContentFallingBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public BlockDef getDef() { return def; }

    @Override @Nonnull protected MapCodec<? extends FallingBlock> codec() { return MapCodec.unit(this); }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread(); }
}
