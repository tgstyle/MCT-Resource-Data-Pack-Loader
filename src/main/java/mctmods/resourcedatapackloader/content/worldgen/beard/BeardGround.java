package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.util.math.MathHelper;
import java.util.List;

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
                for (int y = box.minY - 8; y <= box.minY; y++) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at)) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    if (world.getBlockState(at).getMaterial() == Material.SAND) {
                        world.setBlockState(at, BeardBlocks.footing(world, x, y, z), 2);
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

    public static int bankRing(StructureStart start, StructureComponent piece, World world, StructureBoundingBox box, StructureBoundingBox clip, BlockPos.MutableBlockPos at) {
        int banked = 0;
        int cut = 0;
        int roadGrade = BeardRoadsGrade.roadGradeBeside(world, box);
        int bank = roadGrade == Integer.MIN_VALUE ? box.minY - 1 : roadGrade - 1;
        boolean yarded = ContentBeard.groundCourse(piece) > 0;
        boolean inWindow = box.intersectsWith(clip);
        boolean fillsOnly = yarded || !inWindow;
        ContentLog.LOGGER.debug("{} at {}, {} banks its ring at y {} against road grade {}{}", piece.getClass().getSimpleName(), box.minX, box.minZ, bank, roadGrade == Integer.MIN_VALUE ? "none" : roadGrade, yarded ? ", and lays its own yard, so the ground around it is only filled up to that, never cut down to it" : "");
        List<RailPiece> bores = BeardRails.subways(world, new StructureBoundingBox(box.minX - 4, box.minY, box.minZ - 4, box.maxX + 4, box.maxY, box.maxZ + 4));
        int deepWidth = box.maxX - box.minX + 5;
        int deepDepth = box.maxZ - box.minZ + 5;
        int[] deep = new int[deepWidth * deepDepth];
        int[] shorn = new int[deepWidth * deepDepth];
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (x > box.minX && x < box.maxX && z > box.minZ && z < box.maxZ) { continue; }
                if (BeardPlots.underAnother(start, piece, x, z)) { continue; }
                if (BeardPlots.besideRoad(start, piece, x, z)) { continue; }
                at.setPos(x, bank, z);
                if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                if (world.getBlockState(at).getMaterial().isLiquid()) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid()) {
                    if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }
                    if (fillsOnly) { continue; }
                    if (!BeardPlots.nearRoad(start, piece, x, z, 6)) { continue; }
                    int shaved = BeardBlocks.cutBank(world, at, x, z, bank + 1, bank + 6);
                    cut += shaved;
                    shorn[(x - box.minX + 2) * deepDepth + (z - box.minZ + 2)] = shaved;
                    at.setPos(x, bank - 1, z);
                    if (!world.getBlockState(at).getMaterial().isSolid() && !world.getBlockState(at).getMaterial().isLiquid()) {
                        int propped = BeardBlocks.fillBank(world, at, bores, x, z, bank - 1, bank - 5, piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2);
                        banked += propped;
                        deep[(x - box.minX + 2) * deepDepth + (z - box.minZ + 2)] = propped;
                    }
                    continue;
                }
                int filled = BeardBlocks.fillBank(world, at, bores, x, z, bank, bank - 5, piece instanceof StructureVillagePieces.Field1 || piece instanceof StructureVillagePieces.Field2);
                banked += filled;
                deep[(x - box.minX + 2) * deepDepth + (z - box.minZ + 2)] = filled;
            }
        }
        int tapered = 0;
        if (inWindow) {
            for (int x = box.minX - 3; x <= box.maxX + 3; x++) {
                for (int z = box.minZ - 3; z <= box.maxZ + 3; z++) {
                    if (x > box.minX - 3 && x < box.maxX + 3 && z > box.minZ - 3 && z < box.maxZ + 3) { continue; }
                    if (BeardPlots.underAnother(start, piece, x, z)) { continue; }
                    if (BeardPlots.besideRoad(start, piece, x, z)) { continue; }
                    int inX = MathHelper.clamp(x, box.minX - 2, box.maxX + 2);
                    int inZ = MathHelper.clamp(z, box.minZ - 2, box.maxZ + 2);
                    int index = (inX - box.minX + 2) * deepDepth + (inZ - box.minZ + 2);
                    if (shorn[index] >= 2) {
                        if (yarded) { continue; }
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
                    tapered += BeardBlocks.fillBank(world, at, bores, x, z, bank - 1, bank - 5, false);
                }
            }
        }
        int propped = 0;
        for (int x = box.minX - 4; x <= box.maxX + 4; x++) {
            for (int z = box.minZ - 4; z <= box.maxZ + 4; z++) {
                if (BeardPlots.underAnother(start, piece, x, z)) { continue; }
                at.setPos(x, bank, z);
                if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                if (!world.getBlockState(at).getMaterial().isSolid() || !BeardBlocks.terrainBlock(world.getBlockState(at).getBlock())) { continue; }
                at.setPos(x, bank - 1, z);
                if (world.getBlockState(at).getMaterial().isSolid() || world.getBlockState(at).getMaterial().isLiquid()) { continue; }
                propped += BeardBlocks.fillBank(world, at, bores, x, z, bank - 1, bank - 5, false);
            }
        }
        if (propped > 0) { ContentLog.LOGGER.debug("Propped {} block(s) of earth under ground that {} at {}, {} left hovering at its bank of y {}", propped, piece.getClass().getSimpleName(), box.minX, box.minZ, bank); }
        if (tapered > 0) { ContentLog.LOGGER.debug("Tapered {} block(s) a ring further out from {} at {}, {}, one below its bank at y {}", tapered, piece.getClass().getSimpleName(), box.minX, box.minZ, bank); }
        if (cut > 0) { ContentLog.LOGGER.debug("Cut {} block(s) off the uphill ring of {} at {}, {}, down to its bank at y {}", cut, piece.getClass().getSimpleName(), box.minX, box.minZ, bank); }
        return banked + cut;
    }
}
