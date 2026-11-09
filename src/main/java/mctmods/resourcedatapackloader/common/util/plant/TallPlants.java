package mctmods.resourcedatapackloader.common.util.plant;

import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.blocks.IVariantBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentCropBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentFlowerBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentPlantPartBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.types.PropertyVariant;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class TallPlants {
    public static final String PART_SUFFIX = "_part";

    private TallPlants() {}

    @Nullable public static TallDef tallOf(Block block) {
        if (!(block instanceof ContentCropBlock) && !(block instanceof ContentFlowerBlock)) { return null; }
        return ((IContentBlock) block).getDef().tall;
    }

    public static int ageOf(IBlockState state) { return state.getBlock() instanceof ContentCropBlock ? state.getValue(BlockCrops.AGE) : -1; }

    @Nullable public static Block partBlock(Block base, int part) {
        ResourceLocation name = base.getRegistryName();
        if (name == null) { return null; }
        Block block = Block.REGISTRY.getObject(new ResourceLocation(name.getNamespace(), name.getPath() + PART_SUFFIX + part));
        return block == Blocks.AIR ? null : block;
    }

    public static int partNumber(Block block) {
        ResourceLocation name = block.getRegistryName();
        if (name == null) { return 0; }
        String path = name.getPath();
        int cut = path.lastIndexOf(PART_SUFFIX);
        if (cut <= 0) { return 0; }
        try { return Integer.parseInt(path.substring(cut + PART_SUFFIX.length())); }
        catch (NumberFormatException e) { return 0; }
    }

    @Nullable public static Block baseBlockOf(Block part) {
        int number = partNumber(part);
        ResourceLocation name = part.getRegistryName();
        if (number <= 0 || name == null) { return null; }
        String path = name.getPath();
        Block base = Block.REGISTRY.getObject(new ResourceLocation(name.getNamespace(), path.substring(0, path.lastIndexOf(PART_SUFFIX))));
        return tallOf(base) == null ? null : base;
    }

    @Nullable public static BlockPos base(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (tallOf(block) != null) { return pos; }
        int number = partNumber(block);
        if (number <= 0) { return null; }
        Block expected = baseBlockOf(block);
        BlockPos below = pos.down(number);
        if (expected == null || world.getBlockState(below).getBlock() != expected) { return null; }
        return below;
    }

    public static int regrowAge(TallDef tall, int part) { return tall.rise[part - 1] - 1; }

    public static boolean canPlaceTall(World world, BlockPos base, Block baseBlock) {
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return true; }
        for (int part = 1; part < tall.parts; part++) {
            BlockPos at = base.up(part);
            if (blocked(world, at, world.getBlockState(at))) { return false; }
        }
        return true;
    }

    public static int limit(World world, BlockPos base, int age) {
        Block baseBlock = world.getBlockState(base).getBlock();
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return age; }
        for (int candidate = age; candidate > 0; candidate--) {
            if (clear(world, base, baseBlock, tall, candidate)) { return candidate; }
        }
        return 0;
    }

    public static void sync(World world, BlockPos base) {
        IBlockState baseState = world.getBlockState(base);
        Block baseBlock = baseState.getBlock();
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return; }
        int age = ageOf(baseState);
        for (int part = 1; part < tall.parts; part++) {
            if (age >= 0 && tall.riseAt(part) > age) { continue; }
            Block partBlock = partBlock(baseBlock, part);
            if (partBlock == null) { continue; }
            BlockPos at = base.up(part);
            IBlockState current = world.getBlockState(at);
            if (current.getBlock() != partBlock && blocked(world, at, current)) { break; }
            IBlockState wanted = partState(partBlock, baseState, age);
            if (current != wanted) { world.setBlockState(at, wanted, BlockFlags.UPDATE_CLIENTS); }
        }
    }

    public static void removeAbove(World world, BlockPos from, boolean drops) {
        BlockPos base = base(world, from);
        if (base == null) { return; }
        Block baseBlock = world.getBlockState(base).getBlock();
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return; }
        for (int part = 1; part < tall.parts; part++) {
            Block partBlock = partBlock(baseBlock, part);
            BlockPos above = base.up(part);
            if (partBlock == null || above.getY() <= from.getY()) { continue; }
            IBlockState state = world.getBlockState(above);
            if (state.getBlock() != partBlock) { continue; }
            if (drops && !world.isRemote) { partBlock.dropBlockAsItem(world, above, state, 0); }
            world.setBlockState(above, Blocks.AIR.getDefaultState(), BlockFlags.UPDATE_CLIENTS);
        }
    }

    private static IBlockState partState(Block partBlock, IBlockState baseState, int age) {
        IBlockState state = partBlock.getDefaultState();
        if (age >= 0) { state = state.withProperty(BlockCrops.AGE, age); }
        PropertyVariant partVariant = partBlock instanceof ContentPlantPartBlock ? ((ContentPlantPartBlock) partBlock).variantProperty() : null;
        if (partVariant != null && baseState.getBlock() instanceof IVariantBlock) {
            state = state.withProperty(partVariant, baseState.getValue(((IVariantBlock) baseState.getBlock()).getVariantProperty()));
        }
        return state;
    }

    private static boolean clear(World world, BlockPos base, Block baseBlock, TallDef tall, int age) {
        for (int part = 1; part < tall.parts; part++) {
            if (tall.riseAt(part) > age) { continue; }
            BlockPos at = base.up(part);
            IBlockState state = world.getBlockState(at);
            if (state.getBlock() != partBlock(baseBlock, part) && blocked(world, at, state)) { return false; }
        }
        return true;
    }

    private static boolean blocked(World world, BlockPos pos, IBlockState state) { return !state.getBlock().isAir(state, world, pos) && !state.getBlock().isReplaceable(world, pos); }
}
