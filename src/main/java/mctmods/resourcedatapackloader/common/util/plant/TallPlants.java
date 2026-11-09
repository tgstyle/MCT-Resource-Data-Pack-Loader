package mctmods.resourcedatapackloader.common.util.plant;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentBushBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentCropBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public final class TallPlants {
    public static final String PART_SUFFIX = "_part";

    private TallPlants() {}

    @Nullable public static TallDef tallOf(Block block) {
        BlockDef def = block instanceof ContentCropBlock crop ? crop.def() : block instanceof ContentBushBlock bush ? bush.def() : null;
        return def == null ? null : def.tall();
    }

    public static int ageOf(BlockState state) { return state.getBlock() instanceof ContentCropBlock ? state.getValue(BlockStateProperties.AGE_7) : -1; }

    @Nullable public static Block partBlock(Block base, int part) {
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(base);
        if (name == null) { return null; }
        Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(name.getNamespace(), name.getPath() + PART_SUFFIX + part));
        return block == null || block == Blocks.AIR ? null : block;
    }

    public static int partNumber(Block block) {
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(block);
        if (name == null) { return 0; }
        String path = name.getPath();
        int cut = path.lastIndexOf(PART_SUFFIX);
        if (cut <= 0) { return 0; }
        try { return Integer.parseInt(path.substring(cut + PART_SUFFIX.length())); }
        catch (NumberFormatException e) { return 0; }
    }

    @Nullable public static Block baseBlockOf(Block part) {
        ResourceLocation name = ForgeRegistries.BLOCKS.getKey(part);
        if (partNumber(part) <= 0 || name == null) { return null; }
        String path = name.getPath();
        Block base = ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(name.getNamespace(), path.substring(0, path.lastIndexOf(PART_SUFFIX))));
        return base == null || tallOf(base) == null ? null : base;
    }

    @Nullable public static BlockPos base(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        if (tallOf(block) != null) { return pos; }
        int number = partNumber(block);
        if (number <= 0) { return null; }
        Block expected = baseBlockOf(block);
        BlockPos below = pos.below(number);
        if (expected == null || level.getBlockState(below).getBlock() != expected) { return null; }
        return below;
    }

    public static int regrowAge(TallDef tall, int part) { return tall.rise()[part - 1] - 1; }

    public static boolean canPlaceTall(Level level, BlockPos base, Block baseBlock) {
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return true; }
        for (int part = 1; part < tall.parts(); part++) {
            if (blocked(level.getBlockState(base.above(part)))) { return false; }
        }
        return true;
    }

    public static int limit(Level level, BlockPos base, int age) {
        Block baseBlock = level.getBlockState(base).getBlock();
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return age; }
        for (int candidate = age; candidate > 0; candidate--) {
            if (clear(level, base, baseBlock, tall, candidate)) { return candidate; }
        }
        return 0;
    }

    public static void sync(Level level, BlockPos base) {
        BlockState baseState = level.getBlockState(base);
        Block baseBlock = baseState.getBlock();
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return; }
        int age = ageOf(baseState);
        for (int part = 1; part < tall.parts(); part++) {
            if (age >= 0 && tall.riseAt(part) > age) { continue; }
            Block partBlock = partBlock(baseBlock, part);
            if (partBlock == null) { continue; }
            BlockPos at = base.above(part);
            BlockState current = level.getBlockState(at);
            if (current.getBlock() != partBlock && blocked(current)) { break; }
            BlockState wanted = partBlock.defaultBlockState();
            if (age >= 0) { wanted = wanted.setValue(BlockStateProperties.AGE_7, age); }
            if (current != wanted) { level.setBlock(at, wanted, Block.UPDATE_CLIENTS); }
        }
    }

    public static void removeAbove(Level level, BlockPos from, boolean drops) {
        BlockPos base = base(level, from);
        if (base == null) { return; }
        Block baseBlock = level.getBlockState(base).getBlock();
        TallDef tall = tallOf(baseBlock);
        if (tall == null) { return; }
        for (int part = 1; part < tall.parts(); part++) {
            Block partBlock = partBlock(baseBlock, part);
            BlockPos above = base.above(part);
            if (partBlock == null || above.getY() <= from.getY()) { continue; }
            BlockState state = level.getBlockState(above);
            if (state.getBlock() != partBlock) { continue; }
            if (drops && !level.isClientSide) { Block.dropResources(state, level, above); }
            level.setBlock(above, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static boolean clear(Level level, BlockPos base, Block baseBlock, TallDef tall, int age) {
        for (int part = 1; part < tall.parts(); part++) {
            if (tall.riseAt(part) > age) { continue; }
            BlockState state = level.getBlockState(base.above(part));
            if (state.getBlock() != partBlock(baseBlock, part) && blocked(state)) { return false; }
        }
        return true;
    }

    private static boolean blocked(BlockState state) { return !state.isAir() && !state.canBeReplaced(); }
}
