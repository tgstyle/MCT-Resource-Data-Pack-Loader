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
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

public class ContentBushBlock extends BushBlock {
    private final BlockDef def;
    private final GrowthDef growth;
    private Set<Block> soil = Collections.emptySet();

    public ContentBushBlock(BlockDef def, GrowthDef growth, Properties properties) {
        super(properties);
        this.def = def;
        this.growth = growth;
    }

    public BlockDef getDef() { return def; }

    public void resolveSoil() { soil = ContentRegistry.resolveSoil(growth.soil(), def.key()); }

    @Override protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        if (soil.isEmpty()) { return super.mayPlaceOn(state, level, pos); }
        return soil.contains(state.getBlock());
    }

    @Override public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (growth.needsSky() && !level.canSeeSky(pos)) { return false; }
        return super.canSurvive(state, level, pos);
    }
}
