package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import java.util.Objects;
import javax.annotation.Nullable;

public class ContentBlockStairs extends BlockStairs implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;

    public static ContentBlockStairs create(BlockDef def) { return new ContentBlockStairs(def, model(def)); }

    protected ContentBlockStairs(BlockDef def, IBlockState model) {
        super(model);
        this.def = def;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + def.at(0).name);
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        BlockVariant variant = def.at(0);
        setHardness(variant.hardness);
        setResistance(variant.resistance / def.explosionResistanceDivisor);
        setLightLevel(variant.light / 15.0F);
        setDefaultSlipperiness(def.slipperiness);
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
