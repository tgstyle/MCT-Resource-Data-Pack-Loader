package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiome;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.biome.Biome;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BeardBlocks {
    private static final int FOOTING_REACH = 8;
    private static Set<Block> packGround;
    private static Set<Block> packStone;

    private BeardBlocks() {}

    public static boolean terrainBlock(Block held) {
        return held == Blocks.STONE || held == Blocks.DIRT || held == Blocks.GRASS || held == Blocks.GRAVEL || held == Blocks.SAND
                || held == Blocks.CLAY || held == Blocks.SNOW_LAYER || held == Blocks.SNOW || held == Blocks.ICE || held == Blocks.PACKED_ICE
                || held instanceof BlockOre || held instanceof BlockRedstoneOre || packGround().contains(held);
    }

    private static Set<Block> packGround() {
        if (packGround != null) { return packGround; }
        Set<Block> ground = new HashSet<>();
        for (Biome biome : Biome.REGISTRY) {
            if (!(biome instanceof ContentBiome)) { continue; }
            ground.add(biome.topBlock.getBlock());
            ground.add(biome.fillerBlock.getBlock());
            IBlockState stone = ((ContentBiome) biome).getStoneState();
            if (stone != null) { ground.add(stone.getBlock()); }
        }
        ground.remove(Blocks.AIR);
        packGround = ground;
        return ground;
    }
    public static boolean opening(Material material) {
        return material == Material.ROCK || material == Material.GROUND || material == Material.GRASS || material == Material.SAND || material == Material.CLAY || material == Material.SNOW;
    }
    public static boolean loose(Material material) { return opening(material) || material == Material.CRAFTED_SNOW || material == Material.ICE; }

    public static boolean overhang(IBlockState held) {
        if (held.getMaterial() != Material.LEAVES) { return false; }
        return !held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) || held.getValue(BlockLeaves.DECAYABLE);
    }
    private static final BlockPos.MutableBlockPos AT = new BlockPos.MutableBlockPos();

    private static boolean sandBiome(World world, int x, int z) {
        Biome biome = world.getBiome(AT.setPos(x, 64, z));
        return biome.topBlock.getBlock() == Blocks.SAND;
    }
    private static IBlockState vergeBlock(World world, int x, int y, int z) { return BeardRoads.pathPalette("villagePathVergeBlock", Config.worldgen.villagePathVergeBlock, Blocks.AIR.getDefaultState()).pick(world, x, y, z); }

    public static IBlockState overWater(World world, int x, int y, int z) {
        IBlockState wet = BeardRoads.pathPalette("villagePathVergeWaterBlock", Config.worldgen.villagePathVergeWaterBlock, Blocks.PLANKS.getDefaultState()).pick(world, x, y, z);
        return wet.getBlock() == Blocks.AIR ? Blocks.PLANKS.getDefaultState() : wet;
    }

    public static IBlockState footing(World world, int x, int y, int z) {
        IBlockState asked = vergeBlock(world, x, y, z);
        return asked.getBlock() == Blocks.AIR ? Blocks.DIRT.getDefaultState() : asked;
    }

    public static IBlockState fillAt(World world, int x, int y, int top, int z, boolean overWater) {
        if (overWater) { return overWater(world, x, y, z); }
        IBlockState asked = vergeBlock(world, x, y, z);
        if (asked.getBlock() != Blocks.AIR) { return asked; }
        IBlockState laid = fillGround(world, x, z);
        return laid.getBlock() == Blocks.DIRT && y == top ? Blocks.GRASS.getDefaultState() : laid;
    }

    public static IBlockState fillGround(World world, int x, int z) {
        Biome biome = world.getBiome(AT.setPos(x, 64, z));
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
        if (!held.getMaterial().isSolid()) { return; }
        if (terrainBlock(held.getBlock())) {
            StructureBoundingBox standing = BeardKeep.watchingBox();
            if (standing == null || !standing.isVecInside(at)) { return; }
            ContentLog.LOGGER.debug("{} takes ground {} at {}, {}, {} from inside the box of {}", pass, held.getBlock().getRegistryName(), at.getX(), at.getY(), at.getZ(), BeardKeep.watchingName());
            return;
        }
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
    public static int cutBank(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int roof) {
        int cut = clearAbove(world, at, x, z, from, roof, "Cutting a ring");
        at.setPos(x, from - 1, z);
        if (world.getBlockState(at).getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { world.setBlockState(at, Blocks.GRASS.getDefaultState(), 2); }
        return cut;
    }

    public static int groundTop(World world, BlockPos.MutableBlockPos at, int x, int z, int floor, int roof) {
        boolean eaves = false;
        for (int y = roof; y >= floor; y--) {
            at.setPos(x, y, z);
            IBlockState held = world.getBlockState(at);
            if (!covers(held)) { continue; }
            if (terrainBlock(held.getBlock())) { return eaves && walledIn(world, at, x, y, z, roof) ? Integer.MIN_VALUE : y; }
            at.setPos(x, y - 1, z);
            if (world.getBlockState(at).getMaterial().isSolid()) { return Integer.MIN_VALUE; }
            eaves = true;
        }
        return Integer.MIN_VALUE;
    }

    private static boolean covers(IBlockState held) { return held.getMaterial().isSolid() && held.getMaterial() != Material.LEAVES; }

    private static boolean walledIn(World world, BlockPos.MutableBlockPos at, int x, int y, int z, int roof) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if ((dx != 0 || dz != 0) && openAbove(world, at, x + dx, y + 2, z + dz, roof)) { return false; }
            }
        }
        return true;
    }

    private static boolean openAbove(World world, BlockPos.MutableBlockPos at, int x, int from, int z, int roof) {
        for (int y = roof; y >= from; y--) {
            at.setPos(x, y, z);
            if (covers(world.getBlockState(at))) { return false; }
        }
        return true;
    }

    public static int bornTop(World world, BlockPos.MutableBlockPos at, int x, int z, int floor, int roof) {
        for (int y = roof; y >= floor; y--) {
            at.setPos(x, y, z);
            IBlockState held = world.getBlockState(at);
            if (!held.getMaterial().isSolid() || !terrainBlock(held.getBlock())) { continue; }
            return held.isFullBlock() ? y : Integer.MIN_VALUE;
        }
        return Integer.MIN_VALUE;
    }

    private static Set<Block> packStone() {
        if (packStone != null) { return packStone; }
        Set<Block> rock = new HashSet<>();
        for (Biome biome : Biome.REGISTRY) {
            if (!(biome instanceof ContentBiome)) { continue; }
            IBlockState stone = ((ContentBiome) biome).getStoneState();
            if (stone != null) { rock.add(stone.getBlock()); }
        }
        rock.remove(Blocks.AIR);
        packStone = rock;
        return rock;
    }

    public static boolean underSoil(IBlockState held) {
        Block block = held.getBlock();
        if (block == Blocks.STONE) { return held.getValue(BlockStone.VARIANT).isNatural(); }
        return block instanceof BlockOre || block instanceof BlockRedstoneOre || packStone().contains(block);
    }

    public enum Crowned {
        WRITTEN("crowned"),
        BORN_UNKNOWN("whose born top was no ground of its own"),
        OFF_CLIP("outside the chunk being built"),
        UNDER_ROAD("under a road"),
        TOP_NOT_GROUND("standing under something that is not ground"),
        INSIDE_PIECE("inside another piece"),
        BORN_UNDER_SOIL("born under soil already"),
        TOP_NOT_UNDER_SOIL("wearing their own surface still"),
        HELD("held for a piece of the village"),
        BORED("inside a bore");
        private final String said;
        Crowned(String said) { this.said = said; }
        public String said() { return said; }
    }

    public static Crowned crownGround(World world, BlockPos.MutableBlockPos at, int x, int top, int z, IBlockState born) {
        if (underSoil(born)) { return Crowned.BORN_UNDER_SOIL; }
        at.setPos(x, top, z);
        if (!underSoil(world.getBlockState(at))) { return Crowned.TOP_NOT_UNDER_SOIL; }
        if (BeardKeep.holds(x, top, z)) { return Crowned.HELD; }
        if (BeardRails.insideBore(world, x, top, z)) { return Crowned.BORED; }
        world.setBlockState(at, born, 2);
        return Crowned.WRITTEN;
    }

    public static int fillUnder(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor) {
        int filled = 0;
        for (int y = from; y >= floor; y--) {
            at.setPos(x, y, z);
            if (world.getBlockState(at).getMaterial().isSolid()) { break; }
            if (BeardKeep.holds(x, y, z)) { continue; }
            world.setBlockState(at, fillGround(world, x, z), 2);
            filled++;
        }
        return filled;
    }

    public static int belowLoose(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor) {
        int y = from;
        while (y >= floor) {
            at.setPos(x, y, z);
            if (!(world.getBlockState(at).getBlock() instanceof BlockFalling)) { return y; }
            y--;
        }
        return y;
    }

    public static int fillPier(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor, IBlockState body) {
        int filled = 0;
        for (int y = from; y >= floor && y >= 1; y--) {
            at.setPos(x, y, z);
            if (world.getBlockState(at).getMaterial().isSolid()) { break; }
            world.setBlockState(at, body, 2);
            filled++;
        }
        return filled;
    }

    public static int fillBank(World world, BlockPos.MutableBlockPos at, List<RailPiece> bores, int x, int z, int from, int floor, boolean field) {
        if (bores.isEmpty()) { return fillBank(world, at, x, z, from, floor, field); }
        int bore = BeardRails.boreFloor(world, bores, x, z);
        if (from < bore) { return 0; }
        return fillBank(world, at, x, z, from, Math.max(floor, bore + FOOTING_REACH), field);
    }

    public static int fillBank(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor, boolean field) {
        int footing = Integer.MIN_VALUE;
        for (int y = from; y >= Math.max(1, floor - FOOTING_REACH); y--) {
            at.setPos(x, y, z);
            Material material = world.getBlockState(at).getMaterial();
            if (material.isLiquid()) { return 0; }
            if (material.isSolid()) {
                footing = y;
                break;
            }
        }
        if (footing == Integer.MIN_VALUE) { return 0; }
        int filled = 0;
        for (int y = from; y > footing; y--) {
            if (BeardKeep.holds(x, y, z)) { continue; }
            at.setPos(x, y, z);
            IBlockState laid = fillGround(world, x, z);
            if (field && laid.getBlock() == Blocks.SAND && !sandBiome(world, x, z)) { laid = Blocks.DIRT.getDefaultState(); }
            if (laid.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { laid = Blocks.GRASS.getDefaultState(); }
            world.setBlockState(at, laid, 2);
            filled++;
        }
        return filled;
    }
}
