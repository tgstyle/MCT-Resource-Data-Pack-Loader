package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.mixin.AccessorStructureComponentBox;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public final class BeardOpen {
    private BeardOpen() {}

    public static void around(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        if (BeardPlots.waystone(piece)) { BeardGround.waystoneRing(start, piece, world, clip, box, at); }
        if (piece instanceof StructureVillagePieces.Start) {
            BeardPlaza.bankWell(start, piece, world, clip, box, at);
            return;
        }
        int width = box.maxX - box.minX + 1;
        int depth = box.maxZ - box.minZ + 1;
        int[] footings = new int[width * depth];
        int[] scanned = footings(start, piece, world, box, clip, at, footings, depth);
        int lowestFooting = scanned[0];
        int known = scanned[1];
        int eaves = scanned[2];
        boolean traced = ContentLog.LOGGER.debugEnabled();
        if (traced) { ContentLog.LOGGER.debug("Hook for {} box {},{},{} known {} lowest {}", piece.getClass().getSimpleName(), box.minX, box.minY, box.minZ, known, lowestFooting == Integer.MAX_VALUE ? "none" : String.valueOf(lowestFooting - box.minY)); }
        if (lowestFooting == Integer.MAX_VALUE) { return; }

        StringBuilder trace = traced ? new StringBuilder() : null;
        int grounded = seat(start, piece, world, box, clip, at, footings, depth, traced, trace);
        int overhead = BeardGround.liftOffRoof(start, piece, world, box, clip, at);
        int banked = BeardGround.bankRing(start, piece, world, box, clip, at);
        int[] ring = BeardGround.openOver(start, piece, world, box, clip, at);
        int opened = ring[0];
        int spared = ring[1];
        int notGround = ring[2];
        int hangingOver = ring[3];
        if (traced) { ContentLog.LOGGER.debug("Footing for {} box {},{},{} to {},{},{} lowest {} known {} filled {}: {}", piece.getClass().getSimpleName(), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, lowestFooting - box.minY, known, grounded, trace); }
        int doorways = doorways(start, piece, world, box, clip, at);
        doorways += approaches(start, piece, world, box, clip, at);
        int bridged = bridges(start, piece, world, box, clip, at);
        if (piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2) { BeardGround.soilField(piece, world, box, clip, at); }
        if (opened + eaves + spared + notGround + hangingOver + grounded + overhead + bridged + doorways + banked > 0) { ContentLog.LOGGER.debug("Opened {} block(s) around {} at {}, {}, spared {} inside neighbouring pieces, left {} that were not ground, stood {} block(s) of ground under it, banked {} up to its grade, lifted {} off its roof, bridged {} between it and a neighbour, freed {} in front of its doors, and the hillside still hangs over {} column(s)", opened + eaves, piece.getClass().getSimpleName(), box.minX, box.minZ, spared, notGround, grounded, banked, overhead, bridged, doorways, hangingOver); }
    }

    private static int[] footings(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int[] footings, int depth) {
        int eaves = 0;
        int lowestFooting = Integer.MAX_VALUE;
        int known = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                at.setPos(x, box.minY, z);
                if (!clip.isVecInside(at)) {
                    footings[(x - box.minX) * depth + (z - box.minZ)] = Integer.MIN_VALUE;
                    continue;
                }
                int footing = Integer.MAX_VALUE;
                boolean fenced = false;
                for (int y = box.minY; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    Block held = world.getBlockState(at).getBlock();
                    if (held == Blocks.AIR || BeardBlocks.terrainBlock(held)) { continue; }

                    footing = y - 1;
                    fenced = held instanceof BlockFence;
                    break;
                }
                for (int y = box.minY + 1; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }

                    IBlockState held = world.getBlockState(at);
                    if (held.getBlock() == Blocks.STONE && !held.getValue(BlockStone.VARIANT).isNatural()) { continue; }
                    if (BeardBlocks.terrainBlock(held.getBlock()) || held.getMaterial() == Material.VINE) { eaves += BeardBlocks.clearAt(world, at); }
                }
                footings[(x - box.minX) * depth + (z - box.minZ)] = fenced && footing != Integer.MAX_VALUE ? -footing : footing;
                known++;
                if (footing < lowestFooting) { lowestFooting = footing; }
            }
        }

        return new int[] { lowestFooting, known, eaves };
    }

    private static int seat(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int[] footings, int depth, boolean traced, StringBuilder trace) {
        int width = box.maxX - box.minX + 1;
        int grounded = 0;

        int[] froms = new int[width * depth];
        int[] tops = new int[width * depth];
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int spot = (x - box.minX) * depth + (z - box.minZ);
                int footing = footings[spot];
                boolean fenced = footing < 0 && footing != Integer.MIN_VALUE;
                if (fenced) { footing = -footing; }
                froms[spot] = Integer.MIN_VALUE;
                tops[spot] = Integer.MIN_VALUE;
                if (footing == Integer.MIN_VALUE) { continue; }

                int from = box.minY - 1;
                froms[spot] = from;
                for (int y = from; y >= box.minY - 24; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    tops[spot] = y;
                    break;
                }
            }
        }
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int spot = (x - box.minX) * depth + (z - box.minZ);
                int footing = footings[spot];
                if (footing < 0 && footing != Integer.MIN_VALUE) { footing = -footing; }
                int from = froms[spot];
                char verdict = footing == Integer.MIN_VALUE ? 'c' : from == Integer.MIN_VALUE ? 't' : footing == Integer.MAX_VALUE ? 'y' : 'f';
                int stood = 0;
                if (from != Integer.MIN_VALUE) {
                    IBlockState ground = Blocks.DIRT.getDefaultState();
                    if (tops[spot] != Integer.MIN_VALUE) {
                        at.setPos(x, tops[spot], z);
                        IBlockState resting = world.getBlockState(at);
                        if (resting.isFullBlock()) { ground = resting; }
                    }
                    int floor = BeardPlots.restingFloor(tops, depth, spot, from);
                    for (int y = from; y >= floor; y--) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                        if (world.getBlockState(at).getMaterial().isSolid()) { break; }

                        boolean exposed = !world.getBlockState(at.up()).getMaterial().isSolid();
                        IBlockState laidAs = ground;
                        if (exposed && ground.getBlock() == Blocks.DIRT) { laidAs = Blocks.GRASS.getDefaultState(); }
                        else if (!exposed && ground.getBlock() == Blocks.GRASS) { laidAs = Blocks.DIRT.getDefaultState(); }
                        world.setBlockState(at, laidAs, 2);
                        stood++;
                    }
                    grounded += stood;
                }
                if (traced) { trace.append(x - box.minX).append(',').append(z - box.minZ).append('=').append(footing == Integer.MIN_VALUE ? "clip" : footing == Integer.MAX_VALUE ? "open" : String.valueOf(footing - box.minY)).append(verdict).append(stood > 0 ? "+" + stood : "").append(' '); }
            }
        }
        return grounded;
    }

    private static int doorways(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int doorways = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                if (x != box.minX && x != box.maxX && z != box.minZ && z != box.maxZ) { continue; }

                for (int y = box.minY; y <= box.maxY - 1; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { break; }
                    if (!(world.getBlockState(at).getBlock() instanceof BlockDoor)) { continue; }

                    int outX = x == box.minX ? -1 : x == box.maxX ? 1 : 0;
                    int outZ = outX != 0 ? 0 : z == box.minZ ? -1 : 1;
                    at.setPos(x + outX, y - 1, z + outZ);
                    boolean floored = false;
                    if (clip.isVecInside(at) && !BeardPlots.underRoad(start, piece, x + outX, z + outZ) && !BeardPlots.insideAnother(start, piece, at) && !world.getBlockState(at).getMaterial().isSolid() && !world.getBlockState(at).getMaterial().isLiquid()) {
                        if (BeardKeep.holds(x + outX, y - 1, z + outZ)) { continue; }

                        IBlockState floor = BeardBlocks.fillGround(world, x + outX, z + outZ);
                        if (floor.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { floor = Blocks.GRASS.getDefaultState(); }
                        world.setBlockState(at, floor, 2);
                        doorways++;
                        floored = true;
                    }
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("{} at {}, {} found its door at {}, {}, {} facing out {}, {} and {} the ground in front (spot held {})", piece.getClass().getSimpleName(), box.minX, box.minZ, x, y, z, outX, outZ, floored ? "floored" : "kept", world.getBlockState(at)); }
                    for (int step = 1; step <= 5; step++) {
                        if (BeardPlots.underRoad(start, piece, x + outX * step, z + outZ * step)) { break; }
                        for (int up = 0; up <= 3; up++) {
                            at.setPos(x + outX * step, y + up, z + outZ * step);
                            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }

                            Block held = world.getBlockState(at).getBlock();
                            if (BeardRoads.clearable(world.getBlockState(at)) || held == Blocks.GRASS_PATH) { doorways += BeardBlocks.clearAt(world, at); }
                        }
                    }
                    break;
                }
            }
        }
        return doorways;
    }

    private static int approaches(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int doorways = 0;
        for (StructureComponent other : start.getComponents()) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }

            int[] strip = ContentBeard.facingStrip(box, other.getBoundingBox(), ContentBeard.FACING_GAP);
            if (strip == null) { continue; }

            int fromX = strip[0];
            int toX = strip[1];
            int fromZ = strip[2];
            int toZ = strip[3];
            for (int x = fromX; x <= toX; x++) {
                for (int z = fromZ; z <= toZ; z++) {
                    if (BeardPlots.underRoad(start, piece, x, z)) { continue; }

                    for (int y = box.minY + 1; y <= box.minY + 4; y++) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                        if (BeardRoads.clearable(world.getBlockState(at))) { doorways += BeardBlocks.clearAt(world, at); }
                    }
                }
            }
        }
        return doorways;
    }

    private static int bridges(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int bridged = 0;
        for (StructureComponent other : start.getComponents()) {
            if (other == piece || other instanceof StructureVillagePieces.Path || !(other instanceof StructureVillagePieces.Village)) { continue; }

            bridged += BeardRoads.bridge(world, start, piece, box, ((AccessorStructureComponentBox) other).rdpl$box(), clip, at);
        }
        return bridged;
    }
}
