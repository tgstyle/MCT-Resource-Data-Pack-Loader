package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class ContentBlockFence extends BlockFence implements IContentBlock {
    private static final ThreadLocal<BlockDef> CONSTRUCTING = new ThreadLocal<>();
    private static final ThreadLocal<PropertyVariant> PROPERTY = new ThreadLocal<>();
    private final BlockDef def;
    private final PropertyVariant variant;

    public static ContentBlockFence create(BlockDef def) {
        CONSTRUCTING.set(def);
        PROPERTY.set(new PropertyVariant(ContentSetup.names(def)));
        try { return new ContentBlockFence(def, PROPERTY.get()); }
        finally {
            CONSTRUCTING.remove();
            PROPERTY.remove();
        }
    }

    protected ContentBlockFence(BlockDef def, PropertyVariant property) {
        super(def.material, def.mapColor);
        this.def = def;
        this.variant = property;

        setRegistryName(def.registryName);
        setTranslationKey(def.registryName.toString());
        ContentSetup.harvest(this, def);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setDefaultSlipperiness(def.slipperiness);
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);

        setDefaultState(this.blockState.getBaseState()
                .withProperty(this.variant, def.at(0).name)
                .withProperty(NORTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE)
                .withProperty(SOUTH, Boolean.FALSE).withProperty(WEST, Boolean.FALSE));
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPERTY.get(), NORTH, EAST, WEST, SOUTH);
    }

    private BlockDef def() { return def == null ? CONSTRUCTING.get() : def; }

    @Override public BlockDef getDef() { return def(); }

    public PropertyVariant getVariantProperty() { return variant; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        for (BlockVariant value : def.visible) { list.add(new ItemStack(this, 1, value.meta)); }
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(variant, def.at(meta).name);
    }

    @Override public int getMetaFromState(IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int damageDropped(@Nonnull IBlockState state) { return getMetaFromState(state); }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return def.at(getMetaFromState(state)).light;
    }

    @Override public int getHarvestLevel(@Nonnull IBlockState state) { return def.at(getMetaFromState(state)).harvestLevel; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        return def.at(getMetaFromState(state)).hardness;
    }

    @Override public float getExplosionResistance(World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) {
        return def.at(getMetaFromState(world.getBlockState(pos))).resistance / def.explosionResistanceDivisor;
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def().renderLayer; }

    @Override public int getFlammability(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def().flammability; }

    @Override public int getFireSpreadSpeed(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def().fireSpread; }

    @Override public boolean isFlammable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing face) { return def().flammability > 0; }
}
