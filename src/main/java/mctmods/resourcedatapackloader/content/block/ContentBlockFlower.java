package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.interfaces.IContentSoil;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.BlockBush;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockFlower extends BlockBush implements IContentBlock, IContentSoil, IVariantBlock {
    public static final int MAX_VARIANTS = 16;
    private final BlockDef def;
    private final GrowthDef growth;
    private final PropertyVariant variant;
    private final ContentSoil soil;

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    public static ContentBlockFlower create(BlockDef def, GrowthDef growth) { return BlockVariants.construct(def, built -> new ContentBlockFlower(built, growth)); }

    protected ContentBlockFlower(BlockDef def, GrowthDef growth) {
        super(def.material);
        this.def = def;
        this.growth = growth;
        this.soil = new ContentSoil(growth.soil, def.registryName);
        this.variant = BlockVariants.property();
        ContentSetup.apply(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setDefaultState(blockState.getBaseState().withProperty(variant, def.at(0).name));
    }

    @Override public ContentSoil soil() { return soil; }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return BlockVariants.container(this); }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    @Override public PropertyVariant getVariantProperty() { return variant; }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) { BlockVariants.subBlocks(this, def, list); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(variant, def.at(meta).name); }

    @Override public int getMetaFromState(IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int damageDropped(@Nonnull IBlockState state) { return getMetaFromState(state); }

    @Override public int getLightValue(@Nonnull IBlockState state) { return def.at(getMetaFromState(state)).light; }

    @Override public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) { return ContentSetup.placeable(this, world, pos); }

    @Override public boolean canBlockStay(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (growth.needsSky && !world.canSeeSky(pos)) { return false; }
        return soil.sustains(world, pos.down(), this);
    }
}
