package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentSoil;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCropBlock extends CropBlock implements IContentSoil {
    private final BlockDef def;
    private final ContentSoil soil;
    private Item seed = Items.WHEAT_SEEDS;
    private Item produce = Items.WHEAT;

    public ContentCropBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
        this.soil = new ContentSoil(def.key());
    }

    @Override public void resolveSoil(@Nonnull ContentRegistry.BlockEntry entry) { soil.resolve(soilNames(entry)); }

    private static List<String> soilNames(ContentRegistry.BlockEntry crop) {
        if (crop.def().growth() != null && !crop.def().growth().soil().isEmpty()) { return crop.def().growth().soil(); }
        List<String> names = new ArrayList<>();
        for (ItemDef item : ContentRegistry.itemDefs()) {
            if (!ContentItemTypes.SEED.equals(item.type()) || !crop.id().toString().equals(item.crop()) || item.soil().isEmpty()) { continue; }
            names.add(item.soil());
        }
        return names;
    }

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
