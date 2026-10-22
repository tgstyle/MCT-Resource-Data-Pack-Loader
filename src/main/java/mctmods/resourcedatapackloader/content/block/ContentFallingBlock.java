package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentFallingBlock extends LineCompat.FallingBase implements LineCompat.ITreeSoil {
    private final BlockDef def;
    @Nullable private final VoxelShape shape;

    public ContentFallingBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.shape = ContentBlock.shape(def);
    }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return shape == null ? super.getShape(state, level, pos, context) : shape; }

    @Override public int getDustColor(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) { return -16777216; }

    @Override @Nonnull public TriState canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos soilPosition, @Nonnull Direction facing, @Nonnull BlockState plant) { return ContentBlock.sustains(def, facing, plant); }

    @Override public boolean keepsUnderTree() { return def.behavesAs().contains(ContentBlock.BUSH); }
}
