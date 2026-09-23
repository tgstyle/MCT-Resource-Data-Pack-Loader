package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentCropBlock extends CropBlock {
    private final BlockDef def;
    private Set<Block> soil = Collections.emptySet();
    private Item seed = Items.WHEAT_SEEDS;
    private Item produce = Items.WHEAT;

    public ContentCropBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public void resolveSoil(Iterable<String> names) { soil = ContentRegistry.resolveSoil(names, def.key()); }

    public void resolve(@Nullable Item seedItem, @Nullable Item produceItem) {
        if (seedItem != null) { seed = seedItem; }
        if (produceItem != null) { produce = produceItem; }
    }

    public Item seed() { return seed; }

    public Item produce() { return produce; }

    @Override public int getMaxAge() { return def.cropMaxAge(); }

    @Override @Nonnull protected ItemLike getBaseSeedId() { return seed; }

    @Override protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        if (soil.isEmpty()) { return super.mayPlaceOn(state, level, pos); }
        return soil.contains(state.getBlock());
    }
}
