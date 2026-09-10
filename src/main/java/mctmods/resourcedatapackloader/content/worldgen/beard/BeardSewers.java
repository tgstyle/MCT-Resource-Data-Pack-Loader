package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GenHeights;
import mctmods.resourcedatapackloader.util.world.SeededRandom;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.List;

public final class BeardSewers {
    private static boolean warned = false;
    private static final int FLOOR_LEAST = 6;
    private static final int CLEAR_UNDER = 2;
    private static final EnumFacing MANHOLE_BACK = EnumFacing.SOUTH;

    private BeardSewers() {}

    public static IBlockState lining() { return BeardRoads.pathBlock("villageSewerBlock", Config.worldgen.villageSewerBlock, Blocks.AIR.getDefaultState()); }

    public static boolean on() { return lining().getBlock() != Blocks.AIR; }

    private static int depth() { return Math.max(4, ContentControl.number(ContentControl.VILLAGES, "villageSewerDepth", Config.worldgen.villageSewerDepth)); }

    private static int height() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageSewerHeight", Config.worldgen.villageSewerHeight)); }

    private static int width() {
        int asked = Math.max(3, ContentControl.number(ContentControl.VILLAGES, "villageSewerWidth", Config.worldgen.villageSewerWidth));
        return (asked & 1) == 0 ? asked + 1 : asked;
    }

    private static IBlockState water() { return BeardRoads.pathBlock("villageSewerWaterBlock", Config.worldgen.villageSewerWaterBlock, Blocks.AIR.getDefaultState()); }

    private static IBlockState walk() { return BeardRoads.pathBlock("villageSewerWalkBlock", Config.worldgen.villageSewerWalkBlock, lining()); }

    private static IBlockState light() { return BeardRoads.pathBlock("villageSewerLightBlock", Config.worldgen.villageSewerLightBlock, Blocks.AIR.getDefaultState()); }

    private static int lightRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageSewerLightRun", Config.worldgen.villageSewerLightRun)); }

    private static IBlockState ladder() { return BeardRoads.pathBlock("villageSewerLadderBlock", Config.worldgen.villageSewerLadderBlock, Blocks.AIR.getDefaultState()); }

    private static IBlockState cover() {
        IBlockState asked = BeardRoads.pathBlock("villageSewerCoverBlock", Config.worldgen.villageSewerCoverBlock, Blocks.AIR.getDefaultState());
        if (asked.getBlock() != Blocks.AIR && asked.getMaterial() == Material.IRON && !warned) {
            warned = true;
            ContentLog.LOGGER.error("villageSewerCoverBlock '{}' is iron, which no player can open by hand, so the manholes will be shut to anyone without a redstone signal", asked.getBlock().getRegistryName());
        }
        if (!asked.getPropertyKeys().contains(BlockTrapDoor.HALF)) { return asked; }
        return asked.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP);
    }

    private static IBlockState vine() { return BeardRoads.pathBlock("villageSewerVineBlock", Config.worldgen.villageSewerVineBlock, Blocks.AIR.getDefaultState()); }

    private static int vineChance() { return MathHelper.clamp(ContentControl.number(ContentControl.VILLAGES, "villageSewerVineChance", Config.worldgen.villageSewerVineChance), 0, 100); }

    private static IBlockState clinging(IBlockState vine, EnumFacing wall) {
        PropertyBool side = wall == EnumFacing.NORTH ? BlockVine.NORTH : wall == EnumFacing.SOUTH ? BlockVine.SOUTH : wall == EnumFacing.WEST ? BlockVine.WEST : BlockVine.EAST;
        if (!vine.getPropertyKeys().contains(side)) { return vine; }
        return vine.withProperty(side, Boolean.TRUE);
    }

    private static boolean uncrossedSewer(List<StructureBoundingBox> crossed, boolean alongX, int x, int z, int half) {
        for (StructureBoundingBox other : crossed) {
            boolean otherAlongX = BeardPlots.roadAlongX(other);
            if (otherAlongX == alongX) { continue; }
            int across = (otherAlongX ? other.maxZ - other.minZ : other.maxX - other.minX) + 1;
            if (across < BeardRoads.pathFullWidth()) { continue; }
            int middle = otherAlongX ? (other.minZ + other.maxZ) / 2 : (other.minX + other.maxX) / 2;
            int off = Math.abs((otherAlongX ? z : x) - middle);
            boolean along = otherAlongX ? x >= other.minX && x <= other.maxX : z >= other.minZ && z <= other.maxZ;
            if (off <= half && along) { return false; }
        }
        return true;
    }

    private static boolean walled(StructureBoundingBox clip, List<StructureBoundingBox> crossed, boolean alongX, int x, int z, int half, int center) {
        if (!clip.isVecInside(new BlockPos(x, clip.minY, z))) { return false; }
        int across = alongX ? z : x;
        if (across != center - half && across != center + half) { return false; }
        return uncrossedSewer(crossed, alongX, x, z, half);
    }

    private static boolean onTrack(World world, BlockPos.MutableBlockPos at, int x, int y, int z) {
        for (int step = -1; step <= 1; step++) {
            if (world.getBlockState(at.setPos(x, y + step, z)).getBlock() instanceof BlockRailBase) { return true; }
        }
        return false;
    }

    private static IBlockState moss() { return BeardRoads.pathBlock("villageSewerMossBlock", Config.worldgen.villageSewerMossBlock, Blocks.AIR.getDefaultState()); }

    private static int mossChance() { return MathHelper.clamp(ContentControl.number(ContentControl.VILLAGES, "villageSewerMossChance", Config.worldgen.villageSewerMossChance), 0, 100); }

    private static void put(World world, BlockPos.MutableBlockPos at, IBlockState laid, IBlockState lining, IBlockState moss, int chance) {
        if (BeardKeep.holds(at.getX(), at.getY(), at.getZ())) { return; }
        boolean mossy = chance > 0 && moss.getBlock() != Blocks.AIR && laid == lining
                && SeededRandom.at(world, at.getX(), at.getY(), at.getZ()).nextInt(100) < chance;
        world.setBlockState(at, mossy ? moss : laid, 2);
    }

    private static boolean manhole(World world, StructureBoundingBox clip, int x, int z, int level, int floor, int roof, IBlockState lining, IBlockState ladder, IBlockState cover, BlockPos.MutableBlockPos at) {
        if (!clip.isVecInside(at.setPos(x, level, z))) { return false; }
        IBlockState air = Blocks.AIR.getDefaultState();
        for (int y = floor + 2; y <= level; y++) {
            if (y >= roof && y < level) {
                for (EnumFacing side : EnumFacing.HORIZONTALS) {
                    at.setPos(x + side.getXOffset(), y, z + side.getZOffset());
                    if (clip.isVecInside(at)) { world.setBlockState(at, lining, 2); }
                }
            }
            else if (y < roof) {
                at.setPos(x + MANHOLE_BACK.getXOffset(), y, z + MANHOLE_BACK.getZOffset());
                if (clip.isVecInside(at)) { world.setBlockState(at, lining, 2); }
            }
            world.setBlockState(at.setPos(x, y, z), y == level ? cover.getBlock() == Blocks.AIR ? air : cover : ladder, 2);
            BeardKeep.holdSpot(x, y, z);
        }
        return true;
    }

    private static boolean wellEntranceOff() { return !ContentControl.flag(ContentControl.VILLAGES, "villageSewerWellEntrance", Config.worldgen.villageSewerWellEntrance); }

    private static final int LOOP = 5;

    private static int band(StructureBoundingBox box, int x, int z) {
        int bx = x < box.minX ? box.minX - x : x > box.maxX ? x - box.maxX : 0;
        int bz = z < box.minZ ? box.minZ - z : z > box.maxZ ? z - box.maxZ : 0;
        return Math.max(bx, bz);
    }

    private static List<StructureComponent> radials(StructureStart start, StructureBoundingBox box) {
        List<StructureComponent> found = new ArrayList<>();
        int full = BeardRoads.pathFullWidth();
        int reach = ContentBeard.plazaReach();
        for (StructureComponent piece : start.getComponents()) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(road);
            if ((alongX ? road.maxZ - road.minZ : road.maxX - road.minX) + 1 < full) { continue; }
            int center = alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            if (center < (alongX ? box.minZ : box.minX) || center > (alongX ? box.maxZ : box.maxX)) { continue; }
            int wellLo = alongX ? box.minX : box.minZ;
            int wellHi = alongX ? box.maxX : box.maxZ;
            int roadLo = alongX ? road.minX : road.minZ;
            int roadHi = alongX ? road.maxX : road.maxZ;
            if (roadLo > wellHi + reach + 2 || roadHi < wellLo - reach - 2) { continue; }
            if (roadLo <= wellHi && roadHi >= wellLo) { continue; }
            found.add(piece);
        }
        return found;
    }

    private static boolean insideRadial(List<StructureComponent> radials, int x, int z, int half) {
        for (StructureComponent piece : radials) {
            StructureBoundingBox road = piece.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(road);
            int center = alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            if (Math.abs((alongX ? z : x) - center) > half - 1) { continue; }
            if (alongX ? x >= road.minX && x <= road.maxX : z >= road.minZ && z <= road.maxZ) { return true; }
        }
        return false;
    }

    public static List<StructureBoundingBox> loopCrossings(List<StructureComponent> nearby, StructureBoundingBox road) {
        List<StructureBoundingBox> found = new ArrayList<>();
        if (!on() || wellEntranceOff()) { return found; }
        boolean alongX = BeardPlots.roadAlongX(road);
        int full = BeardRoads.pathFullWidth();
        int back = (full - 1) / 2;
        int ahead = full / 2;
        for (StructureComponent piece : nearby) {
            if (!(piece instanceof StructureVillagePieces.Well)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            int center;
            if (alongX) {
                if (road.minX > box.maxX) { center = box.maxX + LOOP; }
                else if (road.maxX < box.minX) { center = box.minX - LOOP; }
                else { continue; }
                found.add(new StructureBoundingBox(center - back, road.minY, road.minZ - 1, center + ahead, road.maxY, road.maxZ + 1));
            }
            else {
                if (road.minZ > box.maxZ) { center = box.maxZ + LOOP; }
                else if (road.maxZ < box.minZ) { center = box.minZ - LOOP; }
                else { continue; }
                found.add(new StructureBoundingBox(road.minX - 1, road.minY, center - back, road.maxX + 1, road.maxY, center + ahead));
            }
        }
        return found;
    }

    public static void wellEntrance(StructureStart start, StructureComponent well, World world, StructureBoundingBox clip, int ground) {
        if (!on() || wellEntranceOff()) { return; }
        StructureBoundingBox box = well.getBoundingBox();
        int depth = depth();
        int height = height();
        int half = width() / 2;
        int floor = ground - depth;
        if (floor < GenHeights.floor(world, FLOOR_LEAST) || floor + height + CLEAR_UNDER >= ground) { return; }
        int roof = floor + 2 + height;
        List<StructureComponent> radials = radials(start, box);
        IBlockState lining = lining();
        IBlockState walk = walk();
        IBlockState water = water();
        IBlockState light = light();
        IBlockState moss = moss();
        int mossChance = mossChance();
        int run = lightRun();
        IBlockState air = Blocks.AIR.getDefaultState();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int dug = 0;
        for (int x = box.minX - LOOP - half; x <= box.maxX + LOOP + half; x++) {
            for (int z = box.minZ - LOOP - half; z <= box.maxZ + LOOP + half; z++) {
                int ring = band(box, x, z);
                if (ring < LOOP - half || ring > LOOP + half) { continue; }
                if (!clip.isVecInside(at.setPos(x, floor, z))) { continue; }
                boolean edge = (ring == LOOP - half || ring == LOOP + half) && !insideRadial(radials, x, z, half);
                boolean channel = ring == LOOP;
                put(world, at.setPos(x, floor, z), lining, lining, moss, mossChance);
                put(world, at.setPos(x, floor + 1, z), edge ? lining : channel ? water : walk, lining, moss, mossChance);
                for (int y = floor + 2; y <= floor + 1 + height; y++) { put(world, at.setPos(x, y, z), edge ? lining : air, lining, moss, mossChance); }
                boolean lamp = channel && light.getBlock() != Blocks.AIR && Math.floorMod(x + z, run) == 0;
                put(world, at.setPos(x, roof, z), lamp ? light : lining, lining, moss, mossChance);
                dug++;
            }
        }
        StructureComponent street = radials.isEmpty() ? null : radials.get(0);
        boolean cut = false;
        int shaftX = 0;
        int shaftZ = 0;
        if (street != null) {
            StructureBoundingBox road = street.getBoundingBox();
            boolean alongX = BeardPlots.roadAlongX(road);
            int center = alongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            int along = alongX ? (road.minX > box.maxX ? box.maxX + LOOP : box.minX - LOOP) : (road.minZ > box.maxZ ? box.maxZ + LOOP : box.minZ - LOOP);
            shaftX = alongX ? along : center + 1;
            shaftZ = alongX ? center + 1 : along;
            cut = manhole(world, clip, shaftX, shaftZ, ground, floor, roof, lining, ladder(), cover(), at);
        }
        if ((dug > 0 || cut) && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The plaza at {}, {} gets a sewer loop of {} column(s) around the well, {} street(s) passing through it{}", box.minX, box.minZ, dug, radials.size(), cut ? ", and a manhole at " + shaftX + ", " + shaftZ + " down to y " + floor : ""); }
    }

    public static boolean covers(List<StructureComponent> pieces, int x, int z) {
        if (!on() || pieces == null) { return false; }
        int half = width() / 2;
        int full = BeardRoads.pathFullWidth();
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            boolean alongX = box.maxX - box.minX >= box.maxZ - box.minZ;
            if (((alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1) < full) { continue; }
            int center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
            boolean along = alongX ? x >= box.minX && x <= box.maxX : z >= box.minZ && z <= box.maxZ;
            if (along && Math.abs((alongX ? z : x) - center) <= half) { return true; }
        }
        return false;
    }

    public static void lay(StructureComponent piece, World world, StructureBoundingBox clip, boolean alongX, BeardRoads.Grade graded, int least, int most, int acrossLeast, int acrossMost, List<StructureBoundingBox> crossed) {
        lay(piece, world, clip, alongX, graded, least, most, acrossLeast, acrossMost, crossed, null);
    }

    public static void lay(StructureComponent piece, World world, StructureBoundingBox clip, boolean alongX, BeardRoads.Grade graded, int least, int most, int acrossLeast, int acrossMost, List<StructureBoundingBox> crossed, @Nullable IntUnaryOperator centers) {
        if (!on() || graded == null) { return; }
        if (acrossMost - acrossLeast + 1 < BeardRoads.pathFullWidth()) { return; }
        IBlockState lining = lining();
        IBlockState walk = walk();
        IBlockState water = water();
        IBlockState light = light();
        IBlockState moss = moss();
        int mossChance = mossChance();
        IBlockState vine = vine();
        int vineChance = vineChance();
        IBlockState air = Blocks.AIR.getDefaultState();
        int depth = depth();
        int height = height();
        int half = width() / 2;
        int run = lightRun();
        int floorLeast = GenHeights.floor(world, FLOOR_LEAST);
        int middle = (acrossLeast + acrossMost) / 2;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int dug = 0;
        int lit = 0;
        int walledOff = 0;
        List<RailPiece> subways = BeardRails.subways(world, piece.getBoundingBox());
        for (int row = least; row <= most; row++) {
            int center = centers == null ? middle : centers.applyAsInt(row);
            if (BeardBiome.moved(world, alongX ? row : center, alongX ? center : row)) {
                lining = lining();
                walk = walk();
                water = water();
                light = light();
                moss = moss();
                vine = vine();
            }
            int index = row - graded.start;
            if (index < 0 || index >= graded.profile.length) { continue; }
            int level = graded.profile[index];
            if (level == Integer.MIN_VALUE || graded.bridged[index]) { continue; }
            int floor = level - depth;
            if (floor < floorLeast) { continue; }
            if (floor + height + CLEAR_UNDER >= level) { continue; }
            boolean lightRow = light.getBlock() != Blocks.AIR && Math.floorMod(row, run) == 0;
            for (int across = center - half; across <= center + half; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                if (!clip.isVecInside(at.setPos(x, floor, z))) { continue; }
                boolean fouled = false;
                for (int y = floor; y <= floor + height + 2 && !fouled; y++) { fouled = BeardRails.insideBore(world, subways, x, y, z); }
                if (fouled) {
                    for (int y = floor; y <= floor + height + 2; y++) {
                        if (BeardRails.insideBore(world, subways, x, y, z)) { continue; }
                        if (onTrack(world, at, x, y, z)) { continue; }
                        put(world, at.setPos(x, y, z), lining, lining, moss, mossChance);
                    }
                    walledOff++;
                    continue;
                }
                boolean edge = (across == center - half || across == center + half) && uncrossedSewer(crossed, alongX, x, z, half);
                put(world, at.setPos(x, floor, z), lining, lining, moss, mossChance);
                put(world, at.setPos(x, floor + 1, z), edge ? lining : across == center ? water : walk, lining, moss, mossChance);
                EnumFacing wall = across == center - half + 1 ? (alongX ? EnumFacing.NORTH : EnumFacing.WEST) : across == center + half - 1 ? (alongX ? EnumFacing.SOUTH : EnumFacing.EAST) : null;
                if (wall != null && !walled(clip, crossed, alongX, x + wall.getXOffset(), z + wall.getZOffset(), half, center)) { wall = null; }
                for (int y = floor + 2; y <= floor + 1 + height; y++) {
                    boolean hung = !edge && wall != null && vineChance > 0 && vine.getBlock() != Blocks.AIR
                            && SeededRandom.at(world, x, y, z).nextInt(100) < vineChance;
                    if (hung) { if (!BeardKeep.holds(x, y, z)) { world.setBlockState(at.setPos(x, y, z), clinging(vine, wall), 2); } }
                    else { put(world, at.setPos(x, y, z), edge ? lining : air, lining, moss, mossChance); }
                }
                boolean lamp = lightRow && across == center;
                put(world, at.setPos(x, floor + 2 + height, z), lamp ? light : lining, lining, moss, mossChance);
                if (lamp) { lit++; }
                dug++;
            }
        }
        int holes = 0;
        IBlockState ladder = ladder();
        IBlockState cover = cover();
        if (alongX && (ladder.getBlock() != Blocks.AIR || cover.getBlock() != Blocks.AIR)) {
            for (StructureBoundingBox other : crossed) {
                if (BeardPlots.roadAlongX(other)) { continue; }
                if (other.maxX - other.minX + 1 < BeardRoads.pathFullWidth()) { continue; }
                int row = (other.minX + other.maxX) / 2;
                int index = row - graded.start;
                if (row < least || row > most || index < 0 || index >= graded.profile.length) { continue; }
                int level = graded.profile[index];
                if (level == Integer.MIN_VALUE || graded.bridged[index]) { continue; }
                int floor = level - depth;
                if (floor < floorLeast || floor + height + CLEAR_UNDER >= level) { continue; }
                if (manhole(world, clip, row, middle + 1, level, floor, floor + 2 + height, lining, ladder, cover, at)) { holes++; }
            }
        }
        if (dug + walledOff > 0 && ContentLog.LOGGER.debugEnabled()) {
            StructureBoundingBox box = piece.getBoundingBox();
            ContentLog.LOGGER.debug("Sewer under the road at {}, {} laid {} column(s) {} block(s) under the grade, {} lit, {} manhole(s), {} column(s) walled solid where a subway passes through its depth", box.minX, box.minZ, dug, depth, lit, holes, walledOff);
        }
    }
}
