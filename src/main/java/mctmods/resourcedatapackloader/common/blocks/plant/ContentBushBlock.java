package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.IContentSoil;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowthDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ContentBushBlock extends BushBlock implements IContentSoil {
    static final VoxelShape SHAPE = Block.box(4.8D, 0.0D, 4.8D, 11.2D, 9.6D, 11.2D);
    private final GrowthDef growth;
    private final ContentSoil soil;

    public ContentBushBlock(BlockDef def, GrowthDef growth, Properties properties) {
        super(properties);
        this.growth = growth;
        this.soil = new ContentSoil(def.key());
    }

    @Override public void resolveSoil(@Nonnull ContentRegistry.BlockEntry entry) { soil.resolve(growth.soil()); }

    @Override @Nonnull protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPE; }

    @Override @Nonnull protected MapCodec<? extends BushBlock> codec() { return MapCodec.unit(this); }

    @Override protected boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (growth.needsSky() && !level.canSeeSky(pos)) { return false; }
        return soil.sustains(level, pos.below(), state);
    }
}
