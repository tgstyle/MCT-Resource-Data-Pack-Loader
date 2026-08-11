package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class BeardBlocks {
    private BeardBlocks() {}

    public static boolean terrainBlock(Block held) {
        return held == Blocks.STONE || held == Blocks.DIRT || held == Blocks.GRASS || held == Blocks.GRAVEL || held == Blocks.SAND
                || held == Blocks.CLAY || held == Blocks.SNOW_LAYER || held == Blocks.SNOW || held == Blocks.ICE || held == Blocks.PACKED_ICE;
    }
    public static boolean opening(Material material) {
        return material == Material.ROCK || material == Material.GROUND || material == Material.GRASS || material == Material.SAND || material == Material.CLAY || material == Material.SNOW;
    }
    public static boolean loose(Material material) { return opening(material) || material == Material.CRAFTED_SNOW || material == Material.ICE; }

    public static boolean overhang(IBlockState held) {
        if (held.getMaterial() != Material.LEAVES) { return false; }

        return !held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) || held.getValue(BlockLeaves.DECAYABLE);
    }
    private static boolean sandBiome(World world, int x, int z) {
        Biome biome = world.getBiome(new BlockPos(x, 64, z));
        return biome.topBlock.getBlock() == Blocks.SAND;
    }
    public static IBlockState fillGround(World world, int x, int z) {
        Biome biome = world.getBiome(new BlockPos(x, 64, z));
        Block top = biome.topBlock.getBlock();
        Block filler = biome.fillerBlock.getBlock();
        if (top == Blocks.HARDENED_CLAY || top == Blocks.STAINED_HARDENED_CLAY || filler == Blocks.HARDENED_CLAY || filler == Blocks.STAINED_HARDENED_CLAY) { return Blocks.HARDENED_CLAY.getDefaultState(); }
        if (top == Blocks.SAND || filler == Blocks.SAND) { return Blocks.SAND.getDefaultState(); }
        if (top == Blocks.GRAVEL) { return Blocks.GRAVEL.getDefaultState(); }

        return Blocks.DIRT.getDefaultState();
    }
    public static void note(World world, BlockPos.MutableBlockPos at, String pass) {
        if (!ContentLog.LOGGER.debugEnabled()) { return; }

        IBlockState held = world.getBlockState(at);
        if (terrainBlock(held.getBlock()) || !held.getMaterial().isSolid()) { return; }

        ContentLog.LOGGER.debug("{} takes {} at {}, {}, {}", pass, held.getBlock().getRegistryName(), at.getX(), at.getY(), at.getZ());
    }

    public static int clearAt(World world, BlockPos.MutableBlockPos at) {
        if (BeardKeep.holds(at.getX(), at.getY(), at.getZ())) { return 0; }

        note(world, at, "Felling or clearing");

        boolean grassy = world.getBlockState(at).getBlock() == Blocks.GRASS;
        world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        int cleared = 1;
        at.move(EnumFacing.UP);
        if (world.getBlockState(at).getBlock() == Blocks.SNOW_LAYER) {
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
            cleared++;
        }
        at.move(EnumFacing.DOWN);
        at.move(EnumFacing.DOWN);
        if (grassy && !BeardKeep.holds(at.getX(), at.getY(), at.getZ()) && world.getBlockState(at) == Blocks.DIRT.getDefaultState()) { world.setBlockState(at, Blocks.GRASS.getDefaultState(), 2); }
        at.move(EnumFacing.UP);
        return cleared;
    }
    public static void clearAbove(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int roof) { clearAbove(world, at, x, z, from, roof, "Clearing above"); }

    private static int clearAbove(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int roof, String pass) {
        int cleared = 0;
        for (int y = from; y <= roof; y++) {
            at.setPos(x, y, z);
            IBlockState above = world.getBlockState(at);
            if (above.getBlock() == Blocks.AIR) { continue; }
            if (BeardKeep.holds(x, y, z)) { continue; }
            if (!terrainBlock(above.getBlock()) && above.getMaterial().isSolid()) { break; }

            note(world, at, pass);
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
            cleared++;
        }
        return cleared;
    }
    private static boolean afloat(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor) {
        for (int y = from; y >= floor; y--) {
            at.setPos(x, y, z);
            Material material = world.getBlockState(at).getMaterial();
            if (material.isLiquid()) { return true; }
            if (material.isSolid()) { return false; }
        }
        return false;
    }

    public static int cutBank(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int roof) {
        int cut = clearAbove(world, at, x, z, from, roof, "Cutting a ring");
        at.setPos(x, from - 1, z);
        if (world.getBlockState(at).getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { world.setBlockState(at, Blocks.GRASS.getDefaultState(), 2); }
        return cut;
    }

    public static int fillUnder(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor) {
        int filled = 0;
        for (int y = from; y >= floor; y--) {
            at.setPos(x, y, z);
            if (world.getBlockState(at).getMaterial().isSolid()) { break; }

            world.setBlockState(at, fillGround(world, x, z), 2);
            filled++;
        }
        return filled;
    }
    public static int fillBank(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor, boolean field) {
        if (afloat(world, at, x, z, from, floor)) { return 0; }

        int filled = 0;
        for (int y = from; y >= floor; y--) {
            at.setPos(x, y, z);
            IBlockState held = world.getBlockState(at);
            if (held.getMaterial().isLiquid() || held.getMaterial().isSolid()) { break; }

            IBlockState laid = fillGround(world, x, z);
            if (field && laid.getBlock() == Blocks.SAND && !sandBiome(world, x, z)) { laid = Blocks.DIRT.getDefaultState(); }
            if (laid.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { laid = Blocks.GRASS.getDefaultState(); }
            world.setBlockState(at, laid, 2);
            filled++;
        }
        return filled;
    }
}
