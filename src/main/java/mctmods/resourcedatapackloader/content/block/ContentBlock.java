package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBlock extends Block {
    private final BlockDef def;
    @Nullable private final VoxelShape shape;
    private final AmountDef expDrop;

    public ContentBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        double[] bounds = def.bounds();
        this.shape = bounds == null ? null : Block.box(bounds[0] * 16.0D, bounds[1] * 16.0D, bounds[2] * 16.0D, bounds[3] * 16.0D, bounds[4] * 16.0D, bounds[5] * 16.0D);
        this.expDrop = def.expDropMax() <= def.expDropMin() ? AmountDef.of(def.expDropMin()) : new AmountDef(def.expDropMin(), def.expDropMax());
    }

    public BlockDef getDef() { return def; }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape == null ? super.getShape(state, level, pos, context) : shape;
    }

    @Override public int getExpDrop(@Nonnull BlockState state, @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, @Nonnull ItemStack tool) {
        if (!def.dropsExperience()) { return 0; }
        return Math.max(0, expDrop.pick(level.getRandom()));
    }

    @Override public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability() > 0; }

    @Override public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.flammability(); }

    @Override public int getFireSpreadSpeed(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) { return def.fireSpread(); }
}
