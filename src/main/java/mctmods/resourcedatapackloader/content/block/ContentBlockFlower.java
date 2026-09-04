package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.Block;
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
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockFlower extends BlockBush implements IContentBlock {
    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    public static final int MAX_VARIANTS = 16;
    private final BlockDef def;
    private final GrowthDef growth;
    private final PropertyVariant variant;
    private Set<Block> soil = new HashSet<>();

    public static ContentBlockFlower create(BlockDef def, GrowthDef growth) {
        BlockVariants.begin(def);
        try { return new ContentBlockFlower(def, growth); }
        finally { BlockVariants.end(); }
    }

    protected ContentBlockFlower(BlockDef def, GrowthDef growth) {
        super(def.material);
        this.def = def;
        this.growth = growth;
        this.variant = BlockVariants.property();
        ContentSetup.apply(this, def);
        setHardness(def.at(0).hardness);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setDefaultState(blockState.getBaseState().withProperty(variant, def.at(0).name));
    }

    public void resolveSoil() { this.soil = ContentSetup.resolveSoil(growth.soil, def.registryName); }

    @Override @Nonnull protected BlockStateContainer createBlockState() {
        PropertyVariant property = BlockVariants.fresh();
        return new BlockStateContainer(this, property);
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    public PropertyVariant getVariantProperty() { return variant; }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        for (BlockVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(variant, def.at(meta).name); }

    @Override public int getMetaFromState(IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int damageDropped(@Nonnull IBlockState state) { return getMetaFromState(state); }

    @Override public int getLightValue(@Nonnull IBlockState state) { return def.at(getMetaFromState(state)).light; }

    @Override protected boolean canSustainBush(@Nonnull IBlockState state) {
        if (soil.isEmpty()) { return super.canSustainBush(state); }
        return soil.contains(state.getBlock());
    }

    @Override public boolean canBlockStay(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (growth.needsSky && !world.canSeeSky(pos)) { return false; }
        return canSustainBush(world.getBlockState(pos.down()));
    }
}
