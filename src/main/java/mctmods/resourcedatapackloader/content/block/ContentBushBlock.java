package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Collections;
import java.util.HashSet;
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

    public void resolveSoil() {
        Set<Block> resolved = new HashSet<>();
        for (String name : growth.soil()) {
            ResourceLocation key = ResourceLocation.tryParse(name);
            Block block = key == null ? null : ForgeRegistries.BLOCKS.getValue(key);
            if (block != null) { resolved.add(block); }
        }
        soil = resolved;
    }

    @Override protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        if (soil.isEmpty()) { return super.mayPlaceOn(state, level, pos); }
        return soil.contains(state.getBlock());
    }

    @Override public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
        if (growth.needsSky() && !level.canSeeSky(pos)) { return false; }
        return super.canSurvive(state, level, pos);
    }
}
