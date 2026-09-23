package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.RailPiece;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import java.util.List;

public final class BeardSeams {
    private BeardSeams() {}

    public static boolean doorBeside(World world, BlockPos.MutableBlockPos at, int x, int y, int z) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            at.setPos(x + facing.getXOffset(), y, z + facing.getZOffset());
            if (world.getBlockState(at).getBlock() instanceof BlockDoor) {
                at.setPos(x, y, z);
                return true;
            }
        }
        at.setPos(x, y, z);
        return false;
    }

    public static int levelSeams(StructureStart start, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        StructureBoundingBox village = start.getBoundingBox();
        List<RailPiece> bores = BeardRails.subways(world, clip);
        int filled = 0;
        for (int x = Math.max(village.minX - 2, clip.minX); x <= Math.min(village.maxX + 2, clip.maxX); x++) {
            for (int z = Math.max(village.minZ - 2, clip.minZ); z <= Math.min(village.maxZ + 2, clip.maxZ); z++) {
                if (BeardPlots.underAnother(start, null, x, z)) { continue; }
                int here = surfaceOf(world, at, x, z, village, true);
                if (here == Integer.MIN_VALUE) { continue; }
                int west = surfaceOf(world, at, x - 1, z, village, false);
                int east = surfaceOf(world, at, x + 1, z, village, false);
                int north = surfaceOf(world, at, x, z - 1, village, false);
                int south = surfaceOf(world, at, x, z + 1, village, false);
                int upTo = Integer.MIN_VALUE;
                if (west > here && east > here) { upTo = Math.min(west, east); }
                if (north > here && south > here) { upTo = upTo == Integer.MIN_VALUE ? Math.min(north, south) : Math.min(upTo, Math.min(north, south)); }
                if (upTo == Integer.MIN_VALUE || upTo - here != 1) { continue; }
                for (int y = here + 1; y <= upTo; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardKeep.holds(x, y, z) || BeardRails.insideBore(world, bores, x, y, z)) { break; }
                    if (doorBeside(world, at, x, y, z)) { break; }
                    IBlockState held = world.getBlockState(at);
                    if (held.getMaterial().isSolid()) { break; }
                    world.setBlockState(at, BeardBlocks.fillAt(world, x, y, upTo, z, world.getBlockState(at.down()).getMaterial().isLiquid()), 2);
                    filled++;
                }
            }
        }
        return filled;
    }

    public static int fillPlotPits(StructureStart start, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int filled = 0;
        for (StructureComponent piece : start.getComponents()) {
            if (!BeardCrown.crownable(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            for (int x = Math.max(box.minX, clip.minX); x <= Math.min(box.maxX, clip.maxX); x++) {
                for (int z = Math.max(box.minZ, clip.minZ); z <= Math.min(box.maxZ, clip.maxZ); z++) {
                    if (BeardPlots.underRoad(start, piece, x, z)) { continue; }
                    filled += fillPit(world, box, at, x, z);
                }
            }
        }
        return filled;
    }

    private static int fillPit(World world, StructureBoundingBox box, BlockPos.MutableBlockPos at, int x, int z) {
        int from = box.maxY + 16;
        int floor = box.minY - 8;
        int here = standingTop(world, at, x, z, from, floor);
        if (here == Integer.MIN_VALUE || !BeardBlocks.terrainBlock(world.getBlockState(at.setPos(x, here, z)).getBlock())) { return 0; }
        if (world.getBlockState(at.setPos(x, here + 1, z)).getMaterial().isLiquid()) { return 0; }
        int low = Integer.MAX_VALUE;
        int walls = 0;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            int nx = x + side.getXOffset();
            int nz = z + side.getZOffset();
            int top = standingTop(world, at, nx, nz, from, floor);
            if (top == Integer.MIN_VALUE) { return 0; }
            low = Math.min(low, top);
            if (world.getBlockState(at.setPos(nx, here + 1, nz)).getMaterial().isSolid() && world.getBlockState(at.setPos(nx, here + 2, nz)).getMaterial().isSolid()) { walls++; }
        }
        if (low - here < 2 || walls < 3) { return 0; }
        int filled = 0;
        for (int y = here + 1; y <= low; y++) {
            if (doorBeside(world, at, x, y, z) || BeardKeep.holds(x, y, z) || BeardRails.insideBore(world, x, y, z)) { break; }
            if (world.getBlockState(at).getMaterial().isSolid()) { break; }
            at.setPos(x, y, z);
            IBlockState laid = BeardBlocks.fillGround(world, x, z);
            if (y == low && laid.getBlock() == Blocks.DIRT) { laid = Blocks.GRASS.getDefaultState(); }
            world.setBlockState(at, laid, 2);
            filled++;
        }
        return filled;
    }

    private static int standingTop(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int floor) {
        if (!world.isBlockLoaded(at.setPos(x, from, z))) { return Integer.MIN_VALUE; }
        for (int y = from; y >= floor; y--) {
            Material material = world.getBlockState(at.setPos(x, y, z)).getMaterial();
            if (material.isSolid() && material != Material.LEAVES) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    private static int surfaceOf(World world, BlockPos.MutableBlockPos at, int x, int z, StructureBoundingBox village, boolean terrainOnly) {
        int ground = Integer.MIN_VALUE;
        for (int y = Math.max(1, village.minY - 8); y <= village.minY + 40; y++) {
            at.setPos(x, y, z);
            IBlockState held = world.getBlockState(at);
            if (held.getMaterial().isLiquid()) {
                if (!terrainOnly) { return Integer.MIN_VALUE; }
                continue;
            }
            if (!held.getMaterial().isSolid()) { continue; }
            if (terrainOnly && !BeardBlocks.terrainBlock(held.getBlock())) { continue; }
            at.setPos(x, y + 1, z);
            if (world.getBlockState(at).getMaterial().isSolid()) { continue; }
            ground = y;
        }
        return ground;
    }
}
