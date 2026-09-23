package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import java.util.function.BiConsumer;
import net.minecraftforge.common.PlantType;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentFallingBlock extends FallingBlock {
    private final BlockDef def;
    @Nullable private final VoxelShape shape;

    public ContentFallingBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.shape = ContentBlock.shape(def);
    }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape == null ? super.getShape(state, level, pos, context) : shape;
    }

    @Override public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nonnull IPlantable plantable) {
        if (def.sustains(plantable.getPlantType(level, pos.relative(facing)))) { return true; }
        return super.canSustainPlant(state, level, pos, facing, plantable);
    }

    @Override public boolean onTreeGrow(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BiConsumer<BlockPos, BlockState> placeFunction, @Nonnull RandomSource randomSource, @Nonnull BlockPos pos, @Nonnull TreeConfiguration config) { return def.sustains(PlantType.PLAINS); }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread(); }
}
