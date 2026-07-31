package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class ContentBlockSlab extends BlockSlab implements IContentBlock {
    public static final int MAX_VARIANTS = 8;
    public static final String DOUBLE_SUFFIX = "_double";
    private static final ThreadLocal<BlockDef> CONSTRUCTING = new ThreadLocal<>();
    private static final ThreadLocal<PropertyVariant> PROPERTY = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> DOUBLE = new ThreadLocal<>();
    private final BlockDef def;
    private final PropertyVariant variant;
    private final boolean isDouble;
    @Nullable private ContentBlockSlab other;

    public static ContentBlockSlab create(BlockDef def, boolean isDouble, PropertyVariant property) {
        CONSTRUCTING.set(def);
        PROPERTY.set(property);
        DOUBLE.set(isDouble);
        try { return new ContentBlockSlab(def, isDouble, property); }
        finally {
            CONSTRUCTING.remove();
            PROPERTY.remove();
            DOUBLE.remove();
        }
    }

    protected ContentBlockSlab(BlockDef def, boolean isDouble, PropertyVariant property) {
        super(def.material, def.mapColor);
        this.def = def;
        this.isDouble = isDouble;
        this.variant = property;

        ResourceLocation name = isDouble ? new ResourceLocation(def.registryName.getNamespace(), def.registryName.getPath() + DOUBLE_SUFFIX) : def.registryName;
        setRegistryName(name);
        setTranslationKey(def.registryName.toString());
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setDefaultSlipperiness(def.slipperiness);
        if (!isDouble) { ContentSetup.apply(this, def.creativeTab); }

        IBlockState state = this.blockState.getBaseState().withProperty(this.variant, def.at(0).name);
        setDefaultState(isDouble ? state : state.withProperty(HALF, EnumBlockHalf.BOTTOM));
        useNeighborBrightness = !isDouble;
    }

    public void pair(ContentBlockSlab other) { this.other = other; }

    @Override @Nonnull protected BlockStateContainer createBlockState() {
        PropertyVariant property = PROPERTY.get();
        return Boolean.TRUE.equals(DOUBLE.get())
                ? new BlockStateContainer(this, property)
                : new BlockStateContainer(this, property, HALF);
    }

    private BlockDef def() { return def == null ? CONSTRUCTING.get() : def; }

    @Override public BlockDef getDef() { return def(); }

    @Override public boolean isDouble() { return def == null ? Boolean.TRUE.equals(DOUBLE.get()) : isDouble; }

    @Override @Nonnull public IProperty<?> getVariantProperty() { return variant; }

    @Override @Nonnull public Comparable<?> getTypeForItem(@Nonnull ItemStack stack) { return def().at(stack.getMetadata()).name; }

    @Override @Nonnull public String getTranslationKey(int meta) { return super.getTranslationKey() + "." + def().at(meta).name; }

    @Override @Nullable public ItemBlock createItem() { return isDouble || other == null ? null : new ItemSlab(this, this, other); }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        if (isDouble) { return; }
        for (BlockVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(variant, def.at(meta & 7).name);
        return isDouble ? state : state.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
    }

    @Override public int getMetaFromState(IBlockState state) {
        int meta = ContentSetup.metaOf(def, state.getValue(variant));
        if (!isDouble && state.getValue(HALF) == EnumBlockHalf.TOP) { meta |= 8; }
        return meta;
    }

    @Override public int damageDropped(@Nonnull IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return new ItemStack(single(), 1, damageDropped(state));
    }

    @Override @Nonnull public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
        return Item.getItemFromBlock(single());
    }

    private ContentBlockSlab single() { return isDouble && other != null ? other : this; }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return def.at(getMetaFromState(state) & 7).light;
    }

    @Override public int getHarvestLevel(@Nonnull IBlockState state) { return def.at(getMetaFromState(state) & 7).harvestLevel; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        return def.at(getMetaFromState(state) & 7).hardness;
    }

    @Override public float getExplosionResistance(World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        return def.at(getMetaFromState(state) & 7).resistance / def.explosionResistanceDivisor;
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def().renderLayer; }
}
