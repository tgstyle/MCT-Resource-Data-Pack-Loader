package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.util.ContentLog;

import mctmods.blastplaster.util.TreeCollector;
import net.minecraft.block.Block;
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

public final class BeardGround {
    private BeardGround() {}

    private static boolean roadBed(World world, StructureStart start, BlockPos at, int x, int z) {
        Block held = world.getBlockState(at).getBlock();
        if (held == Blocks.GRASS_PATH) { return true; }

        return held == Blocks.GRAVEL && BeardPlots.overRoad(start, x, z);
    }
    public static int roadTop(World world, StructureStart start, BlockPos.MutableBlockPos at, int x, int z, int from, int to) {
        if (!world.isChunkGeneratedAt(x >> 4, z >> 4)) { return Integer.MIN_VALUE; }

        for (int y = to; y >= from; y--) {
            at.setPos(x, y, z);
            if (roadBed(world, start, at, x, z)) { return y; }
        }
        return Integer.MIN_VALUE;
    }
    public static int liftOffRoof(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int overhead = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int roof = box.minY + 2;
                for (int y = box.maxY; y > box.minY; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { break; }

                    Block held = world.getBlockState(at).getBlock();
                    if (held == Blocks.AIR || BeardBlocks.terrainBlock(held)) { continue; }

                    roof = y;
                    break;
                }
                for (int y = roof + 1; y <= box.maxY + 4; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (!BeardBlocks.terrainBlock(world.getBlockState(at).getBlock())) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }

                    BeardBlocks.note(world, at, "Opening over a piece");
                    world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                    overhead++;
                }
            }
        }
        return overhead;
    }
    public static void soilField(StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int soiled = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                for (int y = box.minY - 3; y <= box.minY; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    if (world.getBlockState(at).getMaterial() == Material.SAND) {
                        world.setBlockState(at, Blocks.DIRT.getDefaultState(), 2);
                        soiled++;
                    }
                }
            }
        }
        if (soiled > 0) { ContentLog.LOGGER.debug("Turned {} sand block(s) to soil under {} at {}, {}", soiled, piece.getClass().getSimpleName(), box.minX, box.minZ); }
    }
    public static void waystoneRing(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, StructureBoundingBox box, BlockPos.MutableBlockPos at) {
        int ringed = 0;
        int lowX = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;
        int lowZ = Integer.MAX_VALUE;
        int highZ = Integer.MIN_VALUE;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                for (int y = box.minY; y <= box.maxY; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardBlocks.terrainBlock(world.getBlockState(at).getBlock()) || !world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    if (x < lowX) { lowX = x; }
                    if (x > highX) { highX = x; }
                    if (z < lowZ) { lowZ = z; }
                    if (z > highZ) { highZ = z; }
                }
            }
        }
        if (lowX > highX) { return; }

        for (int x = lowX - 1; x <= highX + 1; x++) {
            for (int z = lowZ - 1; z <= highZ + 1; z++) {
                if (x >= lowX && x <= highX && z >= lowZ && z <= highZ) { continue; }

                for (int y = box.minY; y <= box.minY + 2; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at) || BeardPlots.underRoad(start, piece, x, z)) { continue; }

                    IBlockState held = world.getBlockState(at);
                    Material material = held.getMaterial();
                    if (material == Material.PLANTS || material == Material.VINE || material == Material.SNOW || BeardBlocks.overhang(held)) { ringed += BeardBlocks.clearAt(world, at); }
                    else if (y == box.minY && BeardBlocks.opening(material)) { ringed += BeardBlocks.clearAt(world, at); }
                }
            }
        }
        if (ringed > 0) { ContentLog.LOGGER.debug("Cleared {} block(s) ringing the waystone at {}, {}", ringed, box.minX, box.minZ); }
    }

    public static BlockPos sustainer(World world, BlockPos leaf, Predicate<BlockPos> within) {
        BlockPos.MutableBlockPos near = new BlockPos.MutableBlockPos();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 4) { continue; }

                    near.setPos(leaf.getX() + dx, leaf.getY() + dy, leaf.getZ() + dz);
                    if (!within.test(near)) { continue; }
                    if (mctmods.blastplaster.Config.isLog(world.getBlockState(near))) { return near.toImmutable(); }
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
        List<BlockPos> overhangs = new ArrayList<>();
        boolean roadway = piece instanceof StructureVillagePieces.Path;
        int ceiling = Math.max(box.minY + 12, box.maxY + 1);
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (!roadway && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }
                if (BeardPlots.underRoad(start, piece, x, z)) { continue; }

                int bed = BeardGround.roadTop(world, start, at, x, z, box.minY + 1, ceiling);
                for (int y = bed == Integer.MIN_VALUE ? box.minY + 1 : bed + 1; y <= ceiling; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { continue; }
                    if (BeardPlots.insideAnother(start, piece, at)) {
                        spared++;
                        continue;
                    }

                    IBlockState held = world.getBlockState(at);
                    Material material = held.getMaterial();
                    if (!roadway && BeardBlocks.opening(material)) { opened += BeardBlocks.clearAt(world, at); }
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
                    for (int y = box.minY - 2; y <= box.minY + 12; y++) {
                        at.setPos(x, y, z);
                        if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                        if (!(world.getBlockState(at).getBlock() instanceof BlockStairs)) { continue; }

                        int embedded = 0;
                        for (EnumFacing side : EnumFacing.HORIZONTALS) {
                            if (BeardBlocks.opening(world.getBlockState(at.offset(side)).getMaterial())) { embedded++; }
                        }
                        if (embedded >= 2 && world.getBlockState(at.down()).getMaterial().isSolid()) {
                            IBlockState laid = BeardBlocks.fillGround(world, x, z);
                            if (laid.getBlock() == Blocks.DIRT && !world.getBlockState(at.up()).getMaterial().isSolid()) { laid = Blocks.GRASS.getDefaultState(); }
                            if (BeardKeep.holds(x, at.getY(), z)) { continue; }

                            world.setBlockState(at, laid, 2);
                            opened++;
                        }
                        else { opened += BeardBlocks.clearAt(world, at); }
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
        return new int[] {opened, spared, notGround, hangingOver};
    }

    public static int bankRing(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int banked = 0;
        int cut = 0;
        int roadGrade = BeardRoads.roadGradeBeside(world, box);
        int bank = roadGrade == Integer.MIN_VALUE ? box.minY - 1 : roadGrade - 1;
        ContentLog.LOGGER.debug("{} at {}, {} banks its ring at y {} against road grade {}", piece.getClass().getSimpleName(), box.minX, box.minZ, bank, roadGrade == Integer.MIN_VALUE ? "none" : roadGrade);
        int deepWidth = box.maxX - box.minX + 5;
        int deepDepth = box.maxZ - box.minZ + 5;
        int[] deep = new int[deepWidth * deepDepth];
        int[] shorn = new int[deepWidth * deepDepth];
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (x > box.minX && x < box.maxX && z > box.minZ && z < box.maxZ) { continue; }
                if (BeardPlots.underAnother(start, piece, x, z)) { continue; }

                at.setPos(x, bank, z);
                if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                if (world.getBlockState(at).getMaterial().isLiquid()) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid()) {
                    if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }

                    int shaved = BeardBlocks.cutBank(world, at, x, z, bank + 1, bank + 6);
                    cut += shaved;
                    shorn[(x - box.minX + 2) * deepDepth + (z - box.minZ + 2)] = shaved;
                    continue;
                }

                int filled = BeardBlocks.fillBank(world, at, x, z, bank, bank - 5, piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2);
                banked += filled;
                deep[(x - box.minX + 2) * deepDepth + (z - box.minZ + 2)] = filled;
            }
        }
        int tapered = 0;
        for (int x = box.minX - 3; x <= box.maxX + 3; x++) {
            for (int z = box.minZ - 3; z <= box.maxZ + 3; z++) {
                if (x > box.minX - 3 && x < box.maxX + 3 && z > box.minZ - 3 && z < box.maxZ + 3) { continue; }
                if (BeardPlots.underAnother(start, piece, x, z)) { continue; }

                int inX = Math.max(box.minX - 2, Math.min(box.maxX + 2, x));
                int inZ = Math.max(box.minZ - 2, Math.min(box.maxZ + 2, z));
                int index = (inX - box.minX + 2) * deepDepth + (inZ - box.minZ + 2);
                if (shorn[index] >= 2) {
                    at.setPos(x, bank + 1, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }

                    tapered += BeardBlocks.cutBank(world, at, x, z, bank + 2, bank + 6);
                    continue;
                }
                if (deep[index] < 2) { continue; }

                at.setPos(x, bank - 1, z);
                if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid() || world.getBlockState(at).getMaterial().isLiquid()) { continue; }

                tapered += BeardBlocks.fillBank(world, at, x, z, bank - 1, bank - 5, false);
            }
        }
        if (tapered > 0) { ContentLog.LOGGER.debug("Tapered {} block(s) a ring further out from {} at {}, {}, one below its bank at y {}", tapered, piece.getClass().getSimpleName(), box.minX, box.minZ, bank); }
        if (cut > 0) { ContentLog.LOGGER.debug("Cut {} block(s) off the uphill ring of {} at {}, {}, down to its bank at y {}", cut, piece.getClass().getSimpleName(), box.minX, box.minZ, bank); }
        return banked + cut;
    }
}
