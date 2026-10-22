package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.interfaces.IVariantBlock;
import mctmods.resourcedatapackloader.content.item.ContentItemBlock;
import mctmods.resourcedatapackloader.content.types.PropertyVariant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockWall extends BlockWall implements IContentBlock, IVariantBlock {
    public static final Set<String> HIDDEN = Collections.singleton(VARIANT.getName());
    private final BlockDef def;
    private final PropertyVariant variant;

    public static ContentBlockWall create(BlockDef def) { return BlockVariants.construct(def, built -> new ContentBlockWall(built, BlockVariants.property())); }

    protected ContentBlockWall(BlockDef def, PropertyVariant property) {
        super(model(def));
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
                .withProperty(UP, Boolean.FALSE)
                .withProperty(NORTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE)
                .withProperty(SOUTH, Boolean.FALSE).withProperty(WEST, Boolean.FALSE)
                .withProperty(VARIANT, EnumType.NORMAL));
    }

    private static Block model(BlockDef def) {
        Block block = ContentStates.block(def.modelBlock, def.registryName);
        return block == null ? Objects.requireNonNull(Blocks.COBBLESTONE) : block;
    }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, BlockVariants.property(), UP, NORTH, EAST, WEST, SOUTH, VARIANT); }

    @Override public BlockDef getDef() { return def; }

    @Override public PropertyVariant getVariantProperty() { return variant; }

    @Override @Nullable public ItemBlock createItem() { return new ContentItemBlock(this, def); }

    @Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) { BlockVariants.subBlocks(this, def, list); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(variant, def.at(meta).name); }

    @Override public int getMetaFromState(IBlockState state) { return ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public int damageDropped(@Nonnull IBlockState state) { return getMetaFromState(state); }

    @Override public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return def.at(getMetaFromState(state)).light; }

    @Override public int getHarvestLevel(@Nonnull IBlockState state) { return def.at(getMetaFromState(state)).harvestLevel; }

    @Override public float getBlockHardness(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) { return def.at(getMetaFromState(state)).hardness; }

    @Override public float getExplosionResistance(World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) { return ContentSetup.resistanceOf(def, def.at(getMetaFromState(world.getBlockState(pos)))); }

    @Override public int getExpDrop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, int fortune) { return VariantDrops.experience(def, RANDOM); }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        if (def.at(getMetaFromState(state)).drops.isEmpty()) { super.getDrops(drops, world, pos, state, fortune); }
        else { VariantDrops.roll(this, drops, world, state, def.at(getMetaFromState(state)), fortune); }
    }

    @Override public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!world.isRemote) { VariantDrops.spawnEntities(world, pos, def.at(getMetaFromState(state))); }
        super.breakBlock(world, pos, state);
    }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }
}
