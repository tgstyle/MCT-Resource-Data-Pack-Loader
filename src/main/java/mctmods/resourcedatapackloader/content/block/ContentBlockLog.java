package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockLog extends BlockLog implements IContentBlock {
    public static final int MAX_VARIANTS = 4;
    private static final int VARIANT_MASK = 3;
    private static final int AXIS_MASK = 12;
    private final BlockDef def;
    private final PropertyVariant variant;

    public static ContentBlockLog create(BlockDef def) {
        BlockVariants.begin(def, new PropertyVariant(ContentSetup.names(def)));
        try { return new ContentBlockLog(def, BlockVariants.property()); }
        finally { BlockVariants.end(); }
    }

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

    public PropertyVariant getVariantProperty() { return variant; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        for (BlockVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(variant, def.at(meta & VARIANT_MASK).name);
        switch (meta & AXIS_MASK) {
            case 4: return state.withProperty(LOG_AXIS, EnumAxis.X);
            case 8: return state.withProperty(LOG_AXIS, EnumAxis.Z);
            case 12: return state.withProperty(LOG_AXIS, EnumAxis.NONE);
            default: return state.withProperty(LOG_AXIS, EnumAxis.Y);
        }
    }

    @Override public int getMetaFromState(IBlockState state) {
        int meta = ContentSetup.metaOf(def, state.getValue(variant));
        switch (state.getValue(LOG_AXIS)) {
            case X: return meta | 4;
            case Z: return meta | 8;
            case NONE: return meta | 12;
            default: return meta;
        }
    }

    @Override public int damageDropped(@Nonnull IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return def.at(damageDropped(state)).light; }

    @Override public int getHarvestLevel(@Nonnull IBlockState state) { return def.at(damageDropped(state)).harvestLevel; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) { return def.at(damageDropped(state)).hardness; }

    @Override public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def.flammability; }

    @Override public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def.fireSpread; }

    @Override public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def.flammability > 0; }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }
}
