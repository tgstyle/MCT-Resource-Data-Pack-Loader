package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.BlockDef;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class ContentCropBlock extends CropBlock {
    private final BlockDef def;
    private Set<Block> soil = Collections.emptySet();

    public ContentCropBlock(BlockDef def, Properties properties) {
        super(properties);
        this.def = def;
    }

    public BlockDef getDef() { return def; }

    public void resolveSoil(Iterable<String> names) {
        Set<Block> resolved = new HashSet<>();
        for (String name : names) {
            ResourceLocation key = ResourceLocation.tryParse(name);
            Block block = key == null ? null : ForgeRegistries.BLOCKS.getValue(key);
            if (block != null) { resolved.add(block); }
        }
        soil = resolved;
    }

    @Override public int getMaxAge() { return def.cropMaxAge(); }

    @Override @Nonnull protected ItemLike getBaseSeedId() {
        Item seed = ContentStacks.find(def.key(), def.cropSeed());
        return seed == null ? Items.WHEAT_SEEDS : seed;
    }

    @Override protected boolean mayPlaceOn(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        if (soil.isEmpty()) { return super.mayPlaceOn(state, level, pos); }
        return soil.contains(state.getBlock());
    }
}
