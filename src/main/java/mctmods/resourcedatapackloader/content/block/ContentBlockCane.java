package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockCane extends Block implements IContentBlock {
    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    public static final int MAX_VARIANTS = 1;
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    private final BlockDef def;
    private final GrowthDef growth;
    private Set<Block> soil = new HashSet<>();

    public ContentBlockCane(BlockDef def, GrowthDef growth) {
        super(def.material, def.mapColor);
        this.def = def;
        this.growth = growth;
        BlockVariant variant = def.at(0);
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + variant.name);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setHardness(variant.hardness);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(AGE, 0));
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
    }

    public void resolveSoil() { this.soil = ContentSetup.resolveSoil(growth); }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, AGE); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(AGE, meta & 15); }

    @Override public int getMetaFromState(IBlockState state) { return state.getValue(AGE); }

    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return false; }

    @Override @Nullable public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return growth.damage ? SHAPE : NULL_AABB;
    }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) { return SHAPE; }

    @Override public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        if (!world.isAreaLoaded(pos, 1)) { return; }
        if (!checkStay(world, pos)) { return; }
        if (!world.isAirBlock(pos.up())) { return; }
        int height = 1;
        while (world.getBlockState(pos.down(height)).getBlock() == this) { height++; }
        if (height >= growth.maxHeight) { return; }
        int age = state.getValue(AGE);
        if (!ForgeHooks.onCropsGrowPre(world, pos, state, true)) { return; }
        if (age >= growth.stages - 1) {
            world.setBlockState(pos.up(), getDefaultState());
            world.setBlockState(pos, state.withProperty(AGE, 0), 4);
        }
        else { world.setBlockState(pos, state.withProperty(AGE, age + 1), 4); }
        ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
    }

    @Override public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) { return super.canPlaceBlockAt(world, pos) && supported(world, pos); }

    @Override public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos from) { checkDrop(world, pos); }

    @Override public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { checkDrop(world, pos); }

    @Override public void onEntityCollision(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
        if (!growth.damage) { return; }
        entity.attackEntityFrom(DamageSource.CACTUS, growth.damageAmount);
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) { ContentSetup.growthDrops(this, def, growth, drops); }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return new ItemStack(this); }

    @Override @Nonnull public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random random, int fortune) { return Item.getItemFromBlock(this); }

    private boolean checkStay(World world, BlockPos pos) {
        if (supported(world, pos)) { return true; }
        dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
        return false;
    }

    private void checkDrop(World world, BlockPos pos) {
        if (world.isRemote) { return; }
        checkStay(world, pos);
    }

    private boolean supported(World world, BlockPos pos) {
        BlockPos below = pos.down();
        IBlockState under = world.getBlockState(below);
        if (under.getBlock() == this) { return true; }
        if (!soil.isEmpty() && !soil.contains(under.getBlock())) { return false; }
        if (growth.needsSky && !world.canSeeSky(pos)) { return false; }
        if (growth.breaksNeighbors && crowded(world, pos)) { return false; }
        if (!growth.needsWater) { return true; }
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            for (int step = 1; step <= growth.waterRange; step++) {
                BlockPos side = below.offset(facing, step);
                if (world.getBlockState(side).getMaterial() == Material.WATER) { return true; }
                if (world.getBlockState(side).getBlock().isAir(world.getBlockState(side), world, side)) { continue; }
                break;
            }
        }
        return false;
    }

    private boolean crowded(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            IBlockState side = world.getBlockState(pos.offset(facing));
            if (side.getMaterial().isSolid() || side.getMaterial() == Material.LAVA) { return true; }
        }
        return false;
    }
}
