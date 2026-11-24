package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.BlockVariants;
import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.types.PropertyVariant;
import mctmods.resourcedatapackloader.common.util.plant.CropStacks;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.util.world.BlockSupport;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation") public class ContentPlantPartBlock extends Block implements IContentBlock, IGrowable {
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    private final BlockDef def;
    private final int part;
    @Nullable private final PropertyVariant variant;
    private final CropStacks stacks = new CropStacks();

    public ContentPlantPartBlock(BlockDef def, ResourceLocation baseName, int part) {
        super(BlockVariants.property() == null ? Material.PLANTS : def.material);
        this.def = def;
        this.part = part;
        this.variant = BlockVariants.property();
        setRegistryName(baseName.getNamespace(), baseName.getPath() + TallPlants.PART_SUFFIX + part);
        setTranslationKey(ContentSetup.singleKey(def));
        setDefaultState(variant == null ? blockState.getBaseState().withProperty(BlockCrops.AGE, 0) : blockState.getBaseState().withProperty(variant, def.at(0).name));
        ContentSetup.properties(this, def);
    }

    public static ContentPlantPartBlock flower(BlockDef def, ResourceLocation baseName, int part) { return BlockVariants.construct(def, built -> new ContentPlantPartBlock(built, baseName, part)); }

    @Nullable public PropertyVariant variantProperty() { return variant; }

    public CropStacks stacks() { return stacks; }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return null; }

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    @Override @Nonnull protected BlockStateContainer createBlockState() {
        PropertyVariant property = BlockVariants.property();
        return property == null ? new BlockStateContainer(this, BlockCrops.AGE) : new BlockStateContainer(this, property);
    }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) {
        if (variant == null) { return getDefaultState().withProperty(BlockCrops.AGE, meta); }
        return getDefaultState().withProperty(variant, def.at(meta).name);
    }

    @Override public int getMetaFromState(@Nonnull IBlockState state) { return variant == null ? state.getValue(BlockCrops.AGE) : ContentSetup.metaOf(def, state.getValue(variant)); }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return false; }

    @Override @Nullable public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return NULL_AABB; }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return SHAPE; }

    @Override public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) { return super.canPlaceBlockAt(world, pos) && supported(world, pos); }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos from) { checkAndDropBlock(world, pos, state); }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return variant == null ? stacks.seed() : baseStack(state); }

    @Override @Nonnull public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random random, int fortune) { return Items.AIR; }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        TallDef tall = def.tall;
        if (variant != null || tall == null || !TallDef.FROM_ALL.equals(tall.produceFrom)) { return; }
        if (state.getValue(BlockCrops.AGE) >= def.cropMaxAge) { drops.add(stacks.crop()); }
    }

    @Override public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        BlockPos base = TallPlants.base(world, pos);
        boolean removed = super.removedByPlayer(state, world, pos, player, willHarvest);
        TallDef tall = def.tall;
        if (!removed || base == null || tall == null || world.isRemote) { return removed; }
        if (variant != null || !tall.regrow) {
            if (player.capabilities.isCreativeMode) { TallPlants.removeAbove(world, base, false); }
            world.destroyBlock(base, !player.capabilities.isCreativeMode);
        }
        else {
            IBlockState baseState = world.getBlockState(base);
            world.setBlockState(base, baseState.withProperty(BlockCrops.AGE, TallPlants.regrowAge(tall, part)), BlockFlags.UPDATE_ALL);
            TallPlants.sync(world, base);
        }
        return true;
    }

    @Override public boolean canGrow(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
        BlockPos base = TallPlants.base(world, pos);
        if (base == null) { return false; }
        IBlockState baseState = world.getBlockState(base);
        return baseState.getBlock() instanceof IGrowable && ((IGrowable) baseState.getBlock()).canGrow(world, base, baseState, isClient);
    }

    @Override public boolean canUseBonemeal(@Nonnull World world, @Nonnull Random random, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        BlockPos base = TallPlants.base(world, pos);
        if (base == null) { return false; }
        IBlockState baseState = world.getBlockState(base);
        return baseState.getBlock() instanceof IGrowable && ((IGrowable) baseState.getBlock()).canUseBonemeal(world, random, base, baseState);
    }

    @Override public void grow(@Nonnull World world, @Nonnull Random random, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        BlockPos base = TallPlants.base(world, pos);
        if (base == null) { return; }
        IBlockState baseState = world.getBlockState(base);
        if (baseState.getBlock() instanceof IGrowable) { ((IGrowable) baseState.getBlock()).grow(world, random, base, baseState); }
    }

    private ItemStack baseStack(IBlockState state) {
        Block base = TallPlants.baseBlockOf(this);
        return base == null ? ItemStack.EMPTY : new ItemStack(base, 1, getMetaFromState(state));
    }

    private void checkAndDropBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) { BlockSupport.dropUnless(this, world, pos, state, supported(world, pos)); }
    }

    private boolean supported(World world, BlockPos pos) {
        Block below = world.getBlockState(pos.down()).getBlock();
        Block base = TallPlants.baseBlockOf(this);
        if (base == null) { return false; }
        return below == (part == 1 ? base : TallPlants.partBlock(base, part - 1));
    }
}
