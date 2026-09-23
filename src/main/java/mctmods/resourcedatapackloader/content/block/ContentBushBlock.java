package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

@SuppressWarnings("deprecation") public class ContentBushBlock extends BushBlock {
    static final VoxelShape SHAPE = Block.box(4.8D, 0.0D, 4.8D, 11.2D, 9.6D, 11.2D);
    private final BlockDef def;
    private final GrowthDef growth;
    private Set<Block> soil = Collections.emptySet();

    public ContentBushBlock(BlockDef def, GrowthDef growth, Properties properties) {
        super(properties);
        this.def = def;
        this.growth = growth;
    }

    public void resolveSoil() { soil = ContentRegistry.resolveSoil(growth.soil(), def.key()); }

    @Override @Nonnull public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) { return SHAPE; }

    @Override public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (growth.needsSky() && !level.canSeeSky(pos)) { return false; }
        return ContentRegistry.sustains(soil, level, pos.below(), this);
    }
}
