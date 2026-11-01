package mctmods.resourcedatapackloader.common.blocks.shape;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;

public class ContentStairsBlock extends BlockStairs implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;

    public static ContentStairsBlock create(BlockDef def) { return new ContentStairsBlock(def, model(def)); }

    protected ContentStairsBlock(BlockDef def, IBlockState model) {
        super(model);
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + def.at(0).name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
    }

    private static IBlockState model(BlockDef def) {
        Block block = ContentStates.block(def.modelBlock, def.registryName);
        return block == null ? Objects.requireNonNull(Blocks.STONE).getDefaultState() : ContentStates.of(block, def.modelMeta);
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }
}
