package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IVillagePiece;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public final class BeardPlaza {
    private BeardPlaza() {}

    public static void bankWell(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, StructureBoundingBox box, BlockPos.MutableBlockPos at) {
        int rim = BeardSite.wellNominal(box);
        int banked = 0;
        int opened = 0;
        for (int x = box.minX - 2; x <= box.maxX + 2; x++) {
            for (int z = box.minZ - 2; z <= box.maxZ + 2; z++) {
                if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { continue; }
                if (BeardPlots.underRoad(start, piece, x, z)) { continue; }
                IBlockState ground = Blocks.DIRT.getDefaultState();
                for (int y = rim; y >= rim - ContentBeard.BAND + 1; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }
                    IBlockState resting = world.getBlockState(at);
                    if (resting.isFullBlock()) { ground = resting; }
                    break;
                }
                for (int y = rim; y >= rim - ContentBeard.BAND + 1; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }
                    IBlockState laidAs = ground;
                    if (y == rim && ground.getBlock() == Blocks.DIRT) { laidAs = Blocks.GRASS.getDefaultState(); }
                    else if (y != rim && ground.getBlock() == Blocks.GRASS) { laidAs = Blocks.DIRT.getDefaultState(); }
                    world.setBlockState(at, laidAs, 2);
                    banked++;
                }
                opened += sweep(start, piece, world, clip, at, x, z, rim + 1, rim + 4);
            }
        }
        int shored = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int lx = x - box.minX;
                int lz = z - box.minZ;
                if (lx != 0 && lx != 5 && lz != 0 && lz != 5) { continue; }
                IBlockState ground = Blocks.DIRT.getDefaultState();
                for (int y = rim - 1; y >= rim - ContentBeard.BAND; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (!world.getBlockState(at).getMaterial().isSolid()) { continue; }
                    IBlockState resting = world.getBlockState(at);
                    if (resting.isFullBlock()) { ground = resting; }
                    break;
                }
                for (int y = rim - 1; y >= rim - ContentBeard.BAND; y--) {
                    at.setPos(x, y, z);
                    if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                    if (world.getBlockState(at).getMaterial().isSolid()) { break; }
                    world.setBlockState(at, ground.getBlock() == Blocks.GRASS ? Blocks.DIRT.getDefaultState() : ground, 2);
                    shored++;
                }
            }
        }
        int swept = 0;
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                int lx = x - box.minX;
                int lz = z - box.minZ;
                boolean frame = lx == 0 || lx == 5 || lz == 0 || lz == 5;
                boolean post = (lx == 1 || lx == 4) && (lz == 1 || lz == 4);
                if (!frame && post) { continue; }
                int from = frame ? rim + 1 : box.maxY - 1;
                int to = frame ? box.maxY + 1 : box.maxY;
                swept += sweep(start, piece, world, clip, at, x, z, from, to);
            }
        }
        if (banked + opened + swept + shored > 0) { ContentLog.LOGGER.debug("Banked {} block(s), opened {}, swept {} out of the frame and shored {} under the rim around the well at {}, {}, up to its rim at y {}", banked, opened, swept, shored, box.minX, box.minZ, rim); }
    }

    public static void wellPlaza(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip) {
        StructureBoundingBox box = piece.getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int reach = ContentBeard.plazaReach();
        int ground = BeardSite.wellNominal(box);
        int walk = BeardRoads.pathSidewalkWidth();
        int lines = BeardRoads.pathLineColumns();
        boolean chosen = BeardRoads.pathChosen();
        IBlockState surface = BeardRoads.pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, ((IVillagePiece) piece).rdpl$biomeBlock(Blocks.GRASS_PATH.getDefaultState()));
        IBlockState line = BeardRoads.pathBlock("villagePathLineBlock", Config.worldgen.villagePathLineBlock, surface);
        IBlockState sidewalk = BeardRoads.pathBlock("villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock, surface);
        int paved = 0;
        for (int x = box.minX - reach; x <= box.maxX + reach; x++) {
            for (int z = box.minZ - reach; z <= box.maxZ + reach; z++) {
                int band = MathUtil.max(box.minX - x, x - box.maxX, box.minZ - z, z - box.maxZ);
                if (band < 1) { continue; }
                at.setPos(x, ground, z);
                if (!clip.isVecInside(at) || BeardPlots.underBuilding(start, piece, x, z)) { continue; }
                if (world.getBlockState(world.getTopSolidOrLiquidBlock(at).down()).getMaterial().isLiquid()) { continue; }
                BeardBlocks.clearAbove(world, at, x, z, ground + 1, ground + 4);
                BeardBlocks.fillUnder(world, at, x, z, ground - 1, ground - 8);
                at.setPos(x, ground, z);
                IBlockState natural = chosen ? surface : BeardRoads.pathForGround(world, x, z, surface, ((IVillagePiece) piece).rdpl$biomeBlock(Blocks.GRAVEL.getDefaultState()), true);
                IBlockState held = band > reach - walk ? sidewalk : lines > 0 && band == reach - walk ? line : natural;
                if (held != natural && BeardPlots.roadCore(start, piece, x, z)) { held = natural; }
                if (!chosen) { held = natural; }
                world.setBlockState(at, held, 2);
                paved++;
            }
        }
        if (paved > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Paved a plaza of {} column(s) around the well at {}, {}, reaching {} out from it", paved, box.minX, box.minZ, reach); }
        int tapered = 0;
        int widest = 0;
        for (int x = box.minX - reach - 3; x <= box.maxX + reach + 3; x++) {
            for (int z = box.minZ - reach - 3; z <= box.maxZ + reach + 3; z++) {
                int band = MathUtil.max(box.minX - x, x - box.maxX, box.minZ - z, z - box.maxZ) - reach;
                if (band < 1) { continue; }
                int rings = plazaTaper(world, x, z);
                if (rings > widest) { widest = rings; }
                if (band > rings) { continue; }
                if (BeardPlots.underBuilding(start, piece, x, z) || BeardPlots.underRoad(start, piece, x, z)) { continue; }
                int shelf = ground - band;
                at.setPos(x, shelf + 1, z);
                if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid() || world.getBlockState(at).getMaterial().isLiquid()) { continue; }
                tapered += BeardBlocks.fillBank(world, at, x, z, shelf, shelf - 4, false);
            }
        }
        if (tapered > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Tapered {} block(s) off the plaza edge at {}, {}, falling a block a ring over {} ring(s) for this biome's ground", tapered, box.minX, box.minZ, widest); }
    }

    public static int plazaTaper(World world, int x, int z) {
        Block ground = BeardBlocks.fillGround(world, x, z).getBlock();
        if (ground == Blocks.SAND) { return 3; }
        if (ground == Blocks.GRAVEL) { return 2; }
        if (ground == Blocks.HARDENED_CLAY) { return 1; }
        return 2;
    }

    private static int sweep(StructureStart start, StructureComponent piece, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int z, int from, int to) {
        int cleared = 0;
        for (int y = from; y <= to; y++) {
            at.setPos(x, y, z);
            if (!clip.isVecInside(at) || BeardPlots.insideAnother(start, piece, at)) { continue; }
            if (BeardKeep.holds(x, y, z)) { continue; }
            Material material = world.getBlockState(at).getMaterial();
            if (BeardBlocks.loose(material)) {
                BeardBlocks.note(world, at, "The plaza");
                world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                cleared++;
            }
        }
        return cleared;
    }
}
