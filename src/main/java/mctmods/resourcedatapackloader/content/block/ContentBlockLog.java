package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockLog extends BlockLog implements IContentBlock, IVariantBlock {
    public static final int MAX_VARIANTS = 4;
    private static final int VARIANT_MASK = 3;
    private static final int AXIS_MASK = 12;
    private static final int AXIS_X_BITS = 4;
    private static final int AXIS_Z_BITS = 8;
    private static final int AXIS_NONE_BITS = 12;
    private final BlockDef def;
    private final PropertyVariant variant;

    public static ContentBlockLog create(BlockDef def) { return BlockVariants.construct(def, built -> new ContentBlockLog(built, BlockVariants.property())); }

    protected ContentBlockLog(BlockDef def, PropertyVariant property) {
        this.def = def;
        this.variant = property;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setDefaultSlipperiness(def.slipperiness);
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
        setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y).withProperty(property, def.at(0).name));
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, LOG_AXIS, BlockVariants.property()); }

    @Override public BlockDef getDef() { return def; }

    @Override public PropertyVariant getVariantProperty() { return variant; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) { BlockVariants.subBlocks(this, def, list); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(variant, def.at(meta & VARIANT_MASK).name);
        switch (meta & AXIS_MASK) {
            case AXIS_X_BITS: return state.withProperty(LOG_AXIS, EnumAxis.X);
            case AXIS_Z_BITS: return state.withProperty(LOG_AXIS, EnumAxis.Z);
            case AXIS_NONE_BITS: return state.withProperty(LOG_AXIS, EnumAxis.NONE);
            default: return state.withProperty(LOG_AXIS, EnumAxis.Y);
        }
    }

    @Override public int getMetaFromState(IBlockState state) {
        int meta = ContentSetup.metaOf(def, state.getValue(variant));
        switch (state.getValue(LOG_AXIS)) {
            case X: return meta | AXIS_X_BITS;
            case Z: return meta | AXIS_Z_BITS;
            case NONE: return meta | AXIS_NONE_BITS;
            default: return meta;
        }
    }

    @Override public int damageDropped(@Nonnull IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return def.at(damageDropped(state)).light; }

    @Override public int getHarvestLevel(@Nonnull IBlockState state) { return def.at(damageDropped(state)).harvestLevel; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) { return def.at(damageDropped(state)).hardness; }

    @Override public int getExpDrop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, int fortune) { return VariantDrops.experience(def, RANDOM); }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        if (def.at(damageDropped(state)).drops.isEmpty()) { super.getDrops(drops, world, pos, state, fortune); }
        else { VariantDrops.roll(this, drops, world, state, def.at(damageDropped(state)), fortune); }
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) { VariantDrops.spawnEntities(world, pos, def.at(damageDropped(state))); }
        super.breakBlock(world, pos, state);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }
}
