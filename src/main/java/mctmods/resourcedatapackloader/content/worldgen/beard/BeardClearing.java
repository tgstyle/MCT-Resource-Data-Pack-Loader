package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.ContentVillageDecor;
import mctmods.blastplaster.util.TreeCollector;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public final class BeardClearing {
    private BeardClearing() {}

    public static BlockPos sustainer(World world, BlockPos leaf, Predicate<BlockPos> within) {
        BlockPos.MutableBlockPos near = new BlockPos.MutableBlockPos();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 4) { continue; }
                    near.setPos(leaf.getX() + dx, leaf.getY() + dy, leaf.getZ() + dz);
                    if (!within.test(near)) { continue; }
                    if (mctmods.blastplaster.util.BlastPlasterUtil.isTreeWood(world.getBlockState(near))) { return near.toImmutable(); }
                }
            }
        }
        return null;
    }

    public static int[] openOver(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int opened = 0;
        int spared = 0;
        int notGround = 0;
        int hangingOver = 0;
        int planted = 0;
        List<BlockPos> overhangs = new ArrayList<>();
        boolean roadway = piece instanceof StructureVillagePieces.Road;
        int courses = ContentBeard.groundCourse(piece);
        int yardTop = courses > 0 ? box.minY + courses - 1 : Integer.MIN_VALUE;
        int ceiling = Math.max(box.minY + 12, box.maxY + 1);
        int lid = roadway ? Math.max(ceiling, box.maxY + 13) : ceiling;
        int floor = roadway ? box.minY - 12 : box.minY + 1;
        int sunk = ContentBeard.footingSink(piece);
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (!roadway && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }
                if (roadway ? BeardPlots.underRoad(start, piece, x, z) : BeardPlots.besideRoad(start, piece, x, z)) { continue; }
                int bed = BeardGround.roadTop(world, start, at, x, z, floor, lid);
                for (int y = bed == Integer.MIN_VALUE ? box.minY + 1 + sunk : bed + 1; y <= lid; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { continue; }
                    if (BeardPlots.insideAnother(start, piece, at)) {
                        spared++;
                        continue;
                    }
                    IBlockState held = world.getBlockState(at);
                    Material material = held.getMaterial();
                    if (!roadway && BeardBlocks.opening(material)) {
                        if (yardTop != Integer.MIN_VALUE && y <= yardTop) { continue; }
                        opened += BeardBlocks.clearAt(world, at);
                    }
                    else if (roadway && material == Material.VINE) { opened += BeardBlocks.clearAt(world, at); }
                    else if (roadway && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ && BeardBlocks.overhang(held)) {
                        if (decorated(world, at)) { planted++; }
                        else { opened += BeardBlocks.clearAt(world, at); }
                    }
                    else if (BeardBlocks.overhang(held)) { overhangs.add(at.toImmutable()); }
                    else if (material != Material.AIR) { notGround++; }
                }
                at.setPos(x, ceiling + 1, z);
                if (clip.isVecInside(at) && world.getBlockState(at).getMaterial().isSolid() && !BeardPlots.insideAnother(start, piece, at)) { hangingOver++; }
            }
        }
        if (roadway) {
            for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
                for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                    int roadside = BeardGround.roadTop(world, start, at, x, z, box.minY - 2, box.minY + 12);
                    int reach = roadside == Integer.MIN_VALUE ? box.minY + 1 : roadside + 1;
                    for (int y = box.minY - 2; y <= reach; y++) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at)) { continue; }
                        if (!(world.getBlockState(at).getBlock() instanceof BlockStairs)) { continue; }
                        IBlockState step = world.getBlockState(at);
                        if (step.getMaterial() == Material.ROCK && world.getBlockState(at.down()).getMaterial().isLiquid()) {
                            BeardBlocks.note(world, at, "Dressing a doorstep over water in wood");
                            world.setBlockState(at, BeardBlocks.overWater(world, at.getX(), at.getY(), at.getZ()), 2);
                            opened++;
                            continue;
                        }
                        if (!BeardBlocks.terrainBlock(world.getBlockState(at.down()).getBlock())) { continue; }
                        int embedded = 0;
                        for (EnumFacing side : EnumFacing.HORIZONTALS) {
                            IBlockState beside = world.getBlockState(at.offset(side));
                            if (BeardBlocks.terrainBlock(beside.getBlock()) && BeardBlocks.opening(beside.getMaterial())) { embedded++; }
                        }
                        if (embedded < 2) { BeardBlocks.note(world, at, "Burying a doorstep left standing"); }
                        IBlockState laid = BeardBlocks.fillGround(world, x, z);
                        if (laid.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { laid = Blocks.GRASS.getDefaultState(); }
                        world.setBlockState(at, laid, 2);
                        opened++;
                    }
                }
            }
        }
        Predicate<BlockPos> within = BeardPlots.outside(world, start, piece, box, true, box.maxY);
        Set<BlockPos> felledLogs = new HashSet<>();
        for (BlockPos leaf : overhangs) {
            if (!BeardBlocks.overhang(world.getBlockState(leaf))) { continue; }
            BlockPos trunk = sustainer(world, leaf, within);
            if (trunk == null) {
                at.setPos(leaf.getX(), leaf.getY(), leaf.getZ());
                opened += BeardBlocks.clearAt(world, at);
                continue;
            }
            if (felledLogs.contains(trunk)) { continue; }
            if (ContentVillageDecor.plantedAt(world, trunk.getX(), trunk.getZ())) {
                planted++;
                continue;
            }
            TreeCollector.Tree tree = TreeCollector.collect(world, trunk, mctmods.blastplaster.Config.view(world).getMaxTreeSize(), within);
            for (BlockPos log : tree.logs) {
                felledLogs.add(log);
                at.setPos(log.getX(), log.getY(), log.getZ());
                opened += BeardBlocks.clearAt(world, at);
            }
            for (BlockPos held : tree.leaves) {
                at.setPos(held.getX(), held.getY(), held.getZ());
                opened += BeardBlocks.clearAt(world, at);
            }
        }
        if (planted > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Left {} leaf block(s) of verge plantings over or beside the road at {}, {} alone", planted, box.minX, box.minZ); }
        return new int[] {opened, spared, notGround, hangingOver};
    }

    private static boolean decorated(World world, BlockPos leaf) {
        BlockPos trunk = sustainer(world, leaf, unused -> true);
        return trunk != null && ContentVillageDecor.plantedAt(world, trunk.getX(), trunk.getZ());
    }

    public static int sweepOrphanedLeaves(StructureStart start, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        StructureBoundingBox village = start.getBoundingBox();
        int swept = 0;
        for (int x = Math.max(village.minX - 8, clip.minX); x <= Math.min(village.maxX + 8, clip.maxX); x++) {
            for (int z = Math.max(village.minZ - 8, clip.minZ); z <= Math.min(village.maxZ + 8, clip.maxZ); z++) {
                for (int y = Math.max(1, village.minY - 4); y <= village.minY + 44; y++) {
                    at.setPos(x, y, z);
                    IBlockState held = world.getBlockState(at);
                    if (held.getMaterial() != Material.LEAVES) { continue; }
                    if (held.getPropertyKeys().contains(BlockLeaves.DECAYABLE) && !held.getValue(BlockLeaves.DECAYABLE)) { continue; }
                    if (sustainer(world, at.toImmutable(), unused -> true) != null) { continue; }
                    swept += BeardBlocks.clearAt(world, at);
                    for (int under = y - 1; under >= 1; under--) {
                        at.setPos(x, under, z);
                        if (world.getBlockState(at).getMaterial() != Material.VINE) { break; }
                        swept += BeardBlocks.clearAt(world, at);
                    }
                }
            }
        }
        return swept;
    }

    public static int freeDoors(StructureStart start, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, List<StructureBoundingBox> repaved) {
        int freed = 0;
        StructureBoundingBox village = start.getBoundingBox();
        StructureBoundingBox reach = new StructureBoundingBox(village.minX - 2, 0, village.minZ - 2, village.maxX + 2, 255, village.maxZ + 2);
        for (StructureComponent piece : start.getComponents()) {
            if (!(piece instanceof StructureVillagePieces.Village) || piece instanceof StructureVillagePieces.Road) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean near = clip.intersectsWith(box.minX - 3, box.minZ - 3, box.maxX + 3, box.maxZ + 3);
            for (int i = 0; !near && i < repaved.size(); i++) {
                StructureBoundingBox patch = repaved.get(i);
                near = patch.intersectsWith(box.minX - 3, box.minZ - 3, box.maxX + 3, box.maxZ + 3);
            }
            if (!near) { continue; }
            for (int x = box.minX; x <= box.maxX; x++) {
                for (int z = box.minZ; z <= box.maxZ; z++) {
                    if (x != box.minX && x != box.maxX && z != box.minZ && z != box.maxZ) { continue; }
                    for (int y = box.minY; y <= box.maxY - 1; y++) {
                        at.setPos(x, y, z);
                        boolean door = world.getBlockState(at).getBlock() instanceof BlockDoor;
                        if (!door && !ContentBeard.doorwayAt(world, at, x, y, z)) { continue; }
                        int outX = x == box.minX ? -1 : x == box.maxX ? 1 : 0;
                        int outZ = outX != 0 ? 0 : z == box.minZ ? -1 : 1;
                        for (int up = 0; up <= 1; up++) {
                            at.setPos(x + outX, y + up, z + outZ);
                            if (!reach.isVecInside(at) || !world.isBlockLoaded(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                            IBlockState held = world.getBlockState(at);
                            if (!held.getMaterial().isSolid() || !BeardBlocks.terrainBlock(held.getBlock())) { continue; }
                            BeardKeep.letGo(at.getX(), at.getY(), at.getZ());
                            freed += BeardBlocks.clearAt(world, at);
                        }
                        for (int step = 1; step <= 2; step++) { freed += takeDownLamp(world, reach, at, x + outX * step, y, z + outZ * step); }
                        break;
                    }
                }
            }
        }
        return freed;
    }

    private static int takeDownLamp(World world, StructureBoundingBox reach, BlockPos.MutableBlockPos at, int x, int y, int z) {
        Set<Long> lamp = BeardKeep.takeLamp(x, z);
        if (lamp != null) {
            int taken = 0;
            for (long cell : lamp) {
                int[] spot = BeardKeep.unpacked(cell);
                at.setPos(spot[0], spot[1], spot[2]);
                if (!reach.isVecInside(at) || !world.isBlockLoaded(at)) { continue; }
                world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                taken++;
            }
            return taken;
        }
        IBlockState post = ContentBeard.lampBlock();
        if (post.getBlock() == Blocks.AIR) { return 0; }
        IBlockState head = ContentBeard.lampTop();
        int foot = y;
        while (foot > 1 && world.getBlockState(at.setPos(x, foot - 1, z)).getBlock() == post.getBlock()) { foot--; }
        if (world.getBlockState(at.setPos(x, foot, z)).getBlock() != post.getBlock()) { return 0; }
        int taken = 0;
        for (int up = 0; up <= ContentBeard.lampHeight(); up++) {
            at.setPos(x, foot + up, z);
            if (!reach.isVecInside(at) || !world.isBlockLoaded(at)) { break; }
            Block held = world.getBlockState(at).getBlock();
            if (held != post.getBlock() && held != head.getBlock()) { break; }
            BeardKeep.letGo(x, foot + up, z);
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
            taken++;
        }
        return taken;
    }
}
