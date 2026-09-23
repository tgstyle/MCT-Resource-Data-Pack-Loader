package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import java.util.function.BiConsumer;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentFallingBlock extends FallingBlock {
    private final BlockDef def;
    @Nullable private final VoxelShape shape;

    public ContentFallingBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.shape = ContentBlock.shape(def);
    }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape == null ? super.getShape(state, level, pos, context) : shape;
    }

    @Override @Nonnull protected MapCodec<? extends FallingBlock> codec() { return MapCodec.unit(this); }

    @Override @Nonnull public TriState canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos soilPosition, @Nonnull Direction facing, @Nonnull BlockState plant) { return ContentBlock.sustains(def, facing, plant); }

    @Override public boolean onTreeGrow(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BiConsumer<BlockPos, BlockState> placeFunction, @Nonnull RandomSource randomSource, @Nonnull BlockPos pos, @Nonnull TreeConfiguration config) { return def.behavesAs().contains(ContentBlock.BUSH); }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread(); }
}
