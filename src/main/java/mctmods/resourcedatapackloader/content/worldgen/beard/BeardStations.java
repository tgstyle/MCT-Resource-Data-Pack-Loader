package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public final class BeardStations {
    private static final int RAISE = 2;
    private static final int HEADROOM = 2;
    private static final int CLIMB_LEAST = 3;
    private static final int RUN = 7;
    private static final int WIDE = 5;
    private static final int PASS = 3;
    private static final int PASSAGE_MOST = 32;
    private static final int BOX_X = 0;
    private static final int BOX_Z = 0;
    private static final int DOOR = 7;
    private static final int ROAD_NEAR = 8;
    private static final int SLIDE = 48;
    private static final int WORLDGEN_FLAGS = 2;
    private static boolean toldEntrance = false;
    private static boolean toldStation = false;
    private static boolean toldClimb = false;

    private BeardStations() {}

    private static IBlockState platformBlock(IBlockState lining) { return BeardRoads.pathBlock("villageSubwayPlatformBlock", Config.worldgen.villageSubwayPlatformBlock, lining); }

    private static IBlockState stationLight() { return BeardRoads.pathBlock("villageSubwayTunnelLightBlock", Config.worldgen.villageSubwayTunnelLightBlock, Blocks.AIR.getDefaultState()); }

    private static int stationLightRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageSubwayTunnelLightRun", Config.worldgen.villageSubwayTunnelLightRun)); }

    private static IBlockState stairBlock(IBlockState lining) { return BeardRoads.pathBlock("villageSubwayStairBlock", Config.worldgen.villageSubwayStairBlock, lining); }

    private static int outFromLine() { return Math.max((BeardRoads.pathFullWidth() - 1) / 2 + 4, BeardRails.bedHalf(true) + platformWidth() + 3); }

    public static int reach() { return on() ? outFromLine() + WIDE + 1 : 0; }

    public static boolean on() { return length() > 0 && platformWidth() > 0; }

    static int length() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationLength", Config.worldgen.villageSubwayStationLength)); }

    private static int platformWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayPlatformWidth", Config.worldgen.villageSubwayPlatformWidth)); }

    private static int run() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationRun", Config.worldgen.villageSubwayStationRun)); }

    public static int heartRow(RailPiece rail, int wellRow) {
        int half = length() / 2;
        StructureBoundingBox held = rail.station();
        int heart = held == null ? wellRow
                : rail.alongX() ? (held.minX + held.maxX) / 2 : (held.minZ + held.maxZ) / 2;
        return Math.max(rail.rowLeast() + half, Math.min(rail.rowMost() - half, heart));
    }

    public static boolean stationAt(RailPiece rail, int row, int wellRow) {
        if (!on()) { return false; }
        int half = length() / 2;
        if (rail.stations().isEmpty()) { return Math.abs(row - heartRow(rail, wellRow)) <= half; }
        for (StructureBoundingBox box : rail.stations()) {
            if (Math.abs(row - heartOf(rail, box)) <= half) { return true; }
        }
        return false;
    }

    public static int heartOf(RailPiece rail, StructureBoundingBox box) {
        return rail.alongX() ? (box.minX + box.maxX) / 2 : (box.minZ + box.maxZ) / 2;
    }

    public static int heldLevel(RailPiece rail, BeardRoads.Grade grade, int row) {
        if (!on()) { return Integer.MIN_VALUE; }
        int half = length() / 2;
        for (StructureBoundingBox box : rail.stations()) {
            int heart = heartOf(rail, box);
            if (Math.abs(row - heart) <= half) { return grade.at(heart); }
        }
        return Integer.MIN_VALUE;
    }

    public static int open(World world, StructureBoundingBox clip, boolean alongX, int row, int center, int level, int bedHalf, BeardRoads.Palette linings, IBlockState light, int lightRun, BlockPos.MutableBlockPos at) {
        IBlockState lining = linings.first();
        int reach = bedHalf + platformWidth();
        IBlockState platform = platformBlock(lining);
        IBlockState air = Blocks.AIR.getDefaultState();
        int roof = level + BeardRails.CLEAR + 1 + RAISE;
        int laid = 0;
        for (int across = center - reach - 1; across <= center + reach + 1; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            if (!clip.isVecInside(at.setPos(x, level, z))) { continue; }
            boolean wall = across == center - reach - 1 || across == center + reach + 1;
            boolean added = Math.abs(across - center) > bedHalf;
            if (wall) {
                for (int y = level; y <= roof; y++) { world.setBlockState(at.setPos(x, y, z), linings.pick(world, x, y, z), 2); }
                laid++;
                continue;
            }
            if (added) { world.setBlockState(at.setPos(x, level + 1, z), platform, 2); }
            for (int y = level + (added ? 2 : BeardRails.CLEAR + 1); y <= roof - 1; y++) { world.setBlockState(at.setPos(x, y, z), air, 2); }
            boolean lamp = light.getBlock() != Blocks.AIR && Math.floorMod(row, lightRun) == 0
                    && (across == center || Math.abs(across - center) == bedHalf + platformWidth());
            world.setBlockState(at.setPos(x, roof, z), lamp ? light : linings.pick(world, x, roof, z), 2);
            laid++;
        }
        return laid;
    }

    public static int cap(World world, StructureBoundingBox clip, boolean alongX, int row, int center, int level, int boreLevel, int bedHalf, BeardRoads.Palette linings, BlockPos.MutableBlockPos at) {
        int reach = bedHalf + platformWidth();
        int roof = level + BeardRails.CLEAR + 1 + RAISE;
        int laid = 0;
        for (int across = center - reach - 1; across <= center + reach + 1; across++) {
            boolean overBore = Math.abs(across - center) <= bedHalf;
            int from = overBore ? Math.min(level, boreLevel) + BeardRails.CLEAR + 2 : level;
            if (from > roof) { continue; }
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            for (int y = from; y <= roof; y++) {
                if (!clip.isVecInside(at.setPos(x, y, z))) { continue; }
                world.setBlockState(at, linings.pick(world, x, y, z), WORLDGEN_FLAGS);
                BeardKeep.holdSpot(x, y, z);
                laid++;
            }
        }
        return laid;
    }

    public static void claim(StructureStart start, RailPiece rail, boolean alongX, int center, int wellRow) {
        if (!on() || !rail.stations().isEmpty()) { return; }
        int least = rail.rowLeast();
        int most = rail.rowMost();
        int[] street = BeardRails.streetOver(rail);
        if (street == null) {
            ContentLog.LOGGER.debug("Subway line {} has no street over it, so it gets no stations", rail.line());
            return;
        }
        least = Math.max(least, street[0]);
        most = Math.min(most, street[1]);
        int[] rising = rail.risingKnown();
        if (rising != null) {
            int ramp = BeardRails.subwayDepth() * BeardRails.climb(true);
            if (rising[1] > 0) { most = Math.min(most, rising[0] - ramp - 1); }
            else { least = Math.max(least, rising[0] + ramp + 1); }
            ContentLog.LOGGER.debug("Subway line {} climbs out past row {}, so its stations keep to rows {} to {}", rail.line(), rising[0], least, most);
        }
        claimAt(start, rail, alongX, center, wellRow, least, most);
        int run = run();
        if (run <= 0) { return; }
        for (int heart = wellRow - run; heart >= least; heart -= run) { claimAt(start, rail, alongX, center, heart, least, most); }
        for (int heart = wellRow + run; heart <= most; heart += run) { claimAt(start, rail, alongX, center, heart, least, most); }
        if (rail.stations().size() > 1 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line {} carries {} station(s), one at the well and the rest every {} block(s) along it where the ground allowed one", rail.line(), rail.stations().size(), run); }
    }

    private static boolean streetAlong(List<StructureComponent> components, boolean alongX, int center, int from, int to) {
        int full = BeardRoads.pathFullWidth();
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            if (BeardPlots.roadAlongX(box) != alongX) { continue; }
            if ((alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1 < full) { continue; }
            if (center < (alongX ? box.minZ : box.minX) || center > (alongX ? box.maxZ : box.maxX)) { continue; }
            if ((alongX ? box.minX : box.minZ) <= from && (alongX ? box.maxX : box.maxZ) >= to) { return true; }
        }
        return false;
    }

    private static boolean apart(RailPiece rail, int row) {
        int least = length() + RUN;
        for (StructureBoundingBox box : rail.stations()) {
            if (Math.abs(row - heartOf(rail, box)) < least) { return false; }
        }
        return true;
    }

    private static void claimAt(StructureStart start, RailPiece rail, boolean alongX, int center, int wellRow, int least, int most) {
        List<StructureComponent> components = start.getComponents();
        int out = outFromLine();
        for (int slide = 0; slide <= SLIDE; slide++) {
            for (int way = 0; way < (slide == 0 ? 1 : 2); way++) {
                int row = wellRow + (way == 0 ? slide : -slide);
                int half = length() / 2;
                if (row - 1 < least + half || row + RUN + 1 > most - half) { continue; }
                if (!apart(rail, row)) { continue; }
                if (!streetAlong(components, alongX, center, row - 1, row + RUN + 1)) { continue; }
                for (int s = 0; s < 2; s++) {
                    int near = s == 0 ? center + out : center - out - WIDE + 1;
                    StructureBoundingBox box = alongX
                            ? new StructureBoundingBox(row - 1, 64, near - 1, row + RUN + 1, 78, near + WIDE)
                            : new StructureBoundingBox(near - 1, 64, row - 1, near + WIDE, 78, row + RUN + 1);
                    if (onRoadOrWell(components, box)) { continue; }
                    if (onPlaza(components, box)) { continue; }
                    for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
                        if (piece instanceof RailPiece || piece instanceof StructureVillagePieces.Path || piece instanceof StructureVillagePieces.Well) { continue; }
                        StructureBoundingBox met = piece.getBoundingBox();
                        if (met.maxX < box.minX || met.minX > box.maxX || met.maxZ < box.minZ || met.minZ > box.maxZ) { continue; }
                        components.remove(piece);
                        ContentLog.LOGGER.debug("A plot at {}, {} makes way for the subway station beside the plaza", met.minX, met.minZ);
                    }
                    rail.addStation(box);
                    grow(start.getBoundingBox(), box);
                    ContentLog.LOGGER.debug("A subway station plot is claimed at {}, {} to {}, {}, {} block(s) from the well along the line", box.minX, box.minZ, box.maxX, box.maxZ, Math.abs(row - wellRow));
                    return;
                }
            }
        }
    }

    private static void grow(StructureBoundingBox held, StructureBoundingBox box) {
        held.minX = Math.min(held.minX, box.minX - 1);
        held.minZ = Math.min(held.minZ, box.minZ - 1);
        held.maxX = Math.max(held.maxX, box.maxX + 1);
        held.maxZ = Math.max(held.maxZ, box.maxZ + 1);
    }

    private static boolean onPlaza(List<StructureComponent> components, StructureBoundingBox box) {
        int reach = ContentBeard.plazaReach();
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Well)) { continue; }
            StructureBoundingBox well = piece.getBoundingBox();
            if (box.maxX < well.minX - reach || box.minX > well.maxX + reach) { continue; }
            if (box.maxZ < well.minZ - reach || box.minZ > well.maxZ + reach) { continue; }
            return true;
        }
        return false;
    }

    private static boolean onRoadOrWell(List<StructureComponent> components, StructureBoundingBox box) {
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Path) && !(piece instanceof StructureVillagePieces.Well)) { continue; }
            StructureBoundingBox met = piece.getBoundingBox();
            if (met.maxX < box.minX || met.minX > box.maxX || met.maxZ < box.minZ || met.minZ > box.maxZ) { continue; }
            return true;
        }
        return false;
    }

    public static int stairs(World world, StructureBoundingBox clip, RailPiece rail, int which, boolean alongX, int row, int near, int way, int center, int bedHalf, int level, BeardRoads.Palette linings, BlockPos.MutableBlockPos at) {
        IBlockState lining = linings.first();
        IBlockState step = stairBlock(lining);
        IBlockState air = Blocks.AIR.getDefaultState();
        int surface = topFor(world, rail, which, alongX, row, near + WIDE / 2, level);
        if (surface == Integer.MIN_VALUE || surface - level < CLIMB_LEAST) { return 0; }
        int boxLeastX = alongX ? row - 1 : near - 1;
        int boxLeastZ = alongX ? near - 1 : row - 1;
        int stamped = stamp(world, clip, alongX, way, center, bedHalf, boxLeastX, boxLeastZ, level, surface, linings, at);
        if (stamped > 0) { return stamped; }
        int cut = 0;
        int missed = 0;
        for (int along = -1; along <= RUN + 1; along++) {
            for (int lane = -1; lane <= WIDE; lane++) {
                boolean inside = along >= 0 && along <= RUN && lane >= 0 && lane < WIDE;
                int x = stepX(alongX, row, along, near, lane);
                int z = stepZ(alongX, row, along, near, lane);
                for (int y = level; y <= surface; y++) {
                    if (!clip.isVecInside(at.setPos(x, y, z))) { missed++; continue; }
                    cut++;
                    BeardKeep.holdSpot(x, y, z);
                    boolean divider = inside && lane == WIDE / 2;
                    if (y == level || !inside || divider) { world.setBlockState(at, lining, 2); }
                    else { world.setBlockState(at, air, 2); }
                }
            }
        }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Stair carve at {},{} y{}..{}: clip {},{} to {},{}, carved {} cell(s), {} outside the clip", row, near, level, surface, clip.minX, clip.minZ, clip.maxX, clip.maxZ, cut, missed); }
        int laid = 0;
        int along = 0;
        int flight = 0;
        boolean forward = true;
        for (int y = level + 1; y <= surface; y++) {
            boolean turning = forward ? along == RUN : along == 0;
            int most = turning ? WIDE : WIDE / 2;
            for (int wide = 0; wide < most; wide++) {
                int lane = turning ? wide : flight == 0 ? wide : WIDE / 2 + 1 + wide;
                int x = stepX(alongX, row, along, near, lane);
                int z = stepZ(alongX, row, along, near, lane);
                if (clip.isVecInside(at.setPos(x, y, z))) {
                    world.setBlockState(at, step, 2);
                    BeardKeep.holdSpot(x, y, z);
                    laid++;
                }
                if (!turning || lane != WIDE / 2) { continue; }
                for (int head = 1; head <= HEADROOM; head++) {
                    if (!clip.isVecInside(at.setPos(x, y + head, z))) { continue; }
                    world.setBlockState(at, air, 2);
                    BeardKeep.holdSpot(x, y + head, z);
                }
            }
            if (forward) {
                along++;
                if (along > RUN) { along = RUN; forward = false; flight = 1 - flight; }
            }
            else {
                along--;
                if (along < 0) { along = 0; forward = true; flight = 1 - flight; }
            }
        }
        laid += passage(world, clip, alongX, row, near, way, center, bedHalf, level, lining, at);
        if (laid > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A subway station stair box at {}, {} lays {} step(s), {} wide with a divider, cased from y {} to y {}", alongX ? row : near, alongX ? near : row, laid, WIDE / 2, level, surface); }
        return laid;
    }

    private static int passage(World world, StructureBoundingBox clip, boolean alongX, int row, int near, int way, int center, int bedHalf, int level, IBlockState lining, BlockPos.MutableBlockPos at) {
        IBlockState platform = platformBlock(lining);
        IBlockState air = Blocks.AIR.getDefaultState();
        int wall = center + way * (bedHalf + platformWidth() + 1);
        int from = way > 0 ? near - 1 : near + WIDE;
        if (Math.abs(from - wall) > PASSAGE_MOST) {
            ContentLog.LOGGER.warn("A subway station at {}, {} stands {} block(s) from its platform, further than a passage is dug, so it is left without one", alongX ? row : near, alongX ? near : row, Math.abs(from - wall));
            return 0;
        }
        int floor = level + 1;
        int roof = floor + HEADROOM + 1;
        int laid = 0;
        for (int acr = from; way > 0 ? acr >= wall : acr <= wall; acr -= way) {
            for (int along = -1; along <= PASS; along++) {
                boolean inside = along >= 0 && along < PASS;
                int x = alongX ? row + along : acr;
                int z = alongX ? acr : row + along;
                for (int y = floor; y <= roof; y++) {
                    if (!clip.isVecInside(at.setPos(x, y, z))) { continue; }
                    BeardKeep.holdSpot(x, y, z);
                    if (!inside || y == roof) { world.setBlockState(at, lining, 2); }
                    else if (y == floor) { world.setBlockState(at, platform, 2); }
                    else { world.setBlockState(at, air, 2); }
                    laid++;
                }
            }
        }
        if (laid > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A subway station passage runs {} block(s) from the stair foot at {} across to the platform wall at {}, {} block(s) wide and floored at y {}", Math.abs(from - wall) + 1, from, wall, PASS, floor); }
        return laid;
    }

    private static int stationFoot() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationFoot", Config.worldgen.villageSubwayStationFoot)); }

    private static int stationRepeat() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationRepeat", Config.worldgen.villageSubwayStationRepeat)); }

    private static IBlockState railingBlock() { return BeardRoads.pathBlock("villageSubwayRailingBlock", Config.worldgen.villageSubwayRailingBlock, Blocks.AIR.getDefaultState()); }

    private static IBlockState benchBlock() { return BeardRoads.pathBlock("villageSubwayBenchBlock", Config.worldgen.villageSubwayBenchBlock, Blocks.AIR.getDefaultState()); }

    private static IBlockState benchEndBlock() { return BeardRoads.pathBlock("villageSubwayBenchEndBlock", Config.worldgen.villageSubwayBenchEndBlock, Blocks.AIR.getDefaultState()); }

    private static int benchLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayBenchLength", Config.worldgen.villageSubwayBenchLength)); }

    private static int railing(World world, StructureBoundingBox clip, boolean alongX, int boxLeastX, int boxLeastZ, int wide, int deep, int surface, BlockPos.MutableBlockPos at) {
        IBlockState rail = railingBlock();
        if (rail.getBlock() == Blocks.AIR) { return 0; }
        Set<Long> open = new HashSet<>();
        int leastRow = Integer.MAX_VALUE;
        for (int x = boxLeastX; x < boxLeastX + wide; x++) {
            for (int z = boxLeastZ; z < boxLeastZ + deep; z++) {
                if (!clip.isVecInside(at.setPos(x, surface, z))) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid()) { continue; }
                open.add(packed(x, z));
                leastRow = Math.min(leastRow, alongX ? x : z);
            }
        }
        if (open.isEmpty()) { return 0; }
        int laid = 0;
        for (int x = boxLeastX; x < boxLeastX + wide; x++) {
            for (int z = boxLeastZ; z < boxLeastZ + deep; z++) {
                if (open.contains(packed(x, z))) { continue; }
                boolean beside = false;
                for (int dx = -1; dx <= 1 && !beside; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if ((dx != 0 || dz != 0) && open.contains(packed(x + dx, z + dz))) { beside = true; break; }
                    }
                }
                if (!beside) { continue; }
                if ((alongX ? x : z) < leastRow) { continue; }
                if (!clip.isVecInside(at.setPos(x, surface + 1, z))) { continue; }
                if (world.getBlockState(at).getMaterial().isSolid()) { continue; }
                world.setBlockState(at, rail, WORLDGEN_FLAGS);
                BeardKeep.holdSpot(x, surface + 1, z);
                laid++;
            }
        }
        return laid;
    }

    private static IBlockState faced(IBlockState seat, EnumFacing way) {
        if (!seat.getPropertyKeys().contains(BlockHorizontal.FACING)) { return seat; }
        return seat.withProperty(BlockHorizontal.FACING, way);
    }

    private static int bench(World world, StructureBoundingBox clip, boolean alongX, int from, int across, int y, EnumFacing facing, BlockPos.MutableBlockPos at) {
        IBlockState seat = faced(benchBlock(), facing);
        IBlockState arm = benchEndBlock();
        int span = benchLength();
        if (span < 2 || seat.getBlock() == Blocks.AIR) { return 0; }
        int laid = 0;
        for (int i = 0; i < span; i++) {
            int x = alongX ? from + i : across;
            int z = alongX ? across : from + i;
            boolean end = i == 0 || i == span - 1;
            IBlockState put = end ? arm : seat;
            if (put.getBlock() == Blocks.AIR) { continue; }
            if (!clip.isVecInside(at.setPos(x, y, z))) { continue; }
            if (world.getBlockState(at).getMaterial().isSolid()) { continue; }
            if (!world.getBlockState(at.setPos(x, y - 1, z)).getMaterial().isSolid()) { continue; }
            world.setBlockState(at.setPos(x, y, z), put, WORLDGEN_FLAGS);
            BeardKeep.holdSpot(x, y, z);
            laid++;
        }
        return laid;
    }

    private static EnumFacing awayFrom(boolean alongX, int way) {
        if (alongX) { return way > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH; }
        return way > 0 ? EnumFacing.EAST : EnumFacing.WEST;
    }

    public static int platformBench(World world, StructureBoundingBox clip, boolean alongX, int from, int center, int level, int bedHalf, int way, BlockPos.MutableBlockPos at) {
        if (platformWidth() <= 0) { return 0; }
        int across = center + way * (bedHalf + (platformWidth() + 1) / 2);
        return bench(world, clip, alongX, from, across, level + 2, awayFrom(alongX, way), at);
    }

    private static String stationNamed() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayStation", Config.worldgen.villageSubwayStation).trim(); }

    private static int stamp(World world, StructureBoundingBox clip, boolean alongX, int way, int center, int bedHalf, int boxLeastX, int boxLeastZ, int level, int surface, BeardRoads.Palette linings, BlockPos.MutableBlockPos at) {
        IBlockState lining = linings.first();
        String named = stationNamed();
        if (named.isEmpty() || !(world instanceof WorldServer)) { return 0; }
        WorldServer server = (WorldServer) world;
        Template held = server.getStructureTemplateManager().get(server.getMinecraftServer(), new ResourceLocation(named));
        if (held == null) {
            if (!toldStation) {
                toldStation = true;
                ContentLog.LOGGER.error("villageSubwayStation '{}' could not be loaded, so stations are carved instead of laid from the build", named);
            }
            return 0;
        }
        BlockPos size = held.getSize();
        int tall = size.getY();
        int foot = Math.min(stationFoot(), tall);
        int repeat = stationRepeat();
        int cap = tall - foot - repeat;
        int copies = 1;
        if (repeat > 0 && cap >= 0) {
            copies = Math.max(1, (surface - level - foot - cap) / repeat);
        }
        else { repeat = 0; cap = 0; foot = tall; }
        int grown = foot + repeat * copies + cap;
        int base = surface - grown + 1;
        if (base < level + 1 || base - (level + 1) > PASSAGE_MOST) {
            if (!toldClimb) {
                toldClimb = true;
                ContentLog.LOGGER.info("The station build '{}' is {} block(s) tall and this station climbs {} from y{}, which the build cannot reach from even grown to {}, so this one is carved instead", named, tall, surface - level, level, grown);
            }
            return 0;
        }
        PlacementSettings how = new PlacementSettings();
        how.setRotation(alongX ? (way > 0 ? Rotation.NONE : Rotation.CLOCKWISE_180)
                               : (way > 0 ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90));
        how.setBoundingBox(clip);
        BlockPos near = Template.transformedBlockPos(how, new BlockPos(BOX_X, 0, BOX_Z));
        BlockPos far = Template.transformedBlockPos(how, new BlockPos(size.getX() - 1, 0, size.getZ() - 1));
        int leastX = boxLeastX - Math.min(near.getX(), far.getX());
        int leastZ = boxLeastZ - Math.min(near.getZ(), far.getZ());
        BlockPos origin = new BlockPos(leastX, base, leastZ);
        band(world, clip, held, lining, how, leastX, base, leastZ, base, base + foot - 1);
        for (int copy = 0; copy < copies; copy++) {
            int from = base + foot + copy * repeat;
            band(world, clip, held, lining, how, leastX, base + copy * repeat, leastZ, from, from + repeat - 1);
        }
        if (cap > 0) {
            int from = base + foot + copies * repeat;
            band(world, clip, held, lining, how, leastX, base + (copies - 1) * repeat, leastZ, from, from + cap - 1);
        }
        for (int x = 0; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                BlockPos spot = Template.transformedBlockPos(how, new BlockPos(x, 0, z)).add(origin);
                for (int y = level; y <= surface; y++) { BeardKeep.holdSpot(spot.getX(), y, spot.getZ()); }
                for (int y = level; y < base; y++) {
                    if (!clip.isVecInside(at.setPos(spot.getX(), y, spot.getZ()))) { continue; }
                    world.setBlockState(at, lining, WORLDGEN_FLAGS);
                }
            }
        }
        IBlockState lamp = stationLight();
        int run = stationLightRun();
        int lit = 0;
        if (lamp.getBlock() != Blocks.AIR) {
            for (int along : new int[] { size.getX() / 3, size.getX() * 2 / 3 }) {
                BlockPos mid = Template.transformedBlockPos(how, new BlockPos(along, 0, (size.getZ() - 1) / 2)).add(origin);
                for (int y = base + 2; y < base + grown - 1; y += run) {
                    if (!clip.isVecInside(at.setPos(mid.getX(), y, mid.getZ()))) { continue; }
                    world.setBlockState(at, lamp, WORLDGEN_FLAGS);
                    BeardKeep.holdSpot(mid.getX(), y, mid.getZ());
                    lit++;
                }
            }
        }

        List<BlockPos> mouths = new ArrayList<>();
        for (int door = DOOR; door <= DOOR + 1; door++) { mouths.add(Template.transformedBlockPos(how, new BlockPos(door, 1, 0)).add(origin)); }
        int bored = bore(world, clip, alongX, way, mouths, center, bedHalf, level + 1, linings, at);
        int railed = railing(world, clip, alongX, leastX, leastZ, alongX ? size.getX() : size.getZ(), alongX ? size.getZ() : size.getX(), surface, at);
        BlockPos seatFrom = Template.transformedBlockPos(how, new BlockPos(DOOR - 3, 0, 1)).add(origin);
        BlockPos seatTo = Template.transformedBlockPos(how, new BlockPos(DOOR + 1, 0, 1)).add(origin);
        boolean seatAlongX = seatFrom.getZ() == seatTo.getZ();
        int seated = bench(world, clip, seatAlongX, Math.min(seatAlongX ? seatFrom.getX() : seatFrom.getZ(), seatAlongX ? seatTo.getX() : seatTo.getZ()),
                           seatAlongX ? seatFrom.getZ() : seatFrom.getX(), surface + 1, awayFrom(alongX, way), at);
        if ((railed + seated) > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The station head is railed with {} block(s) and {} bench block(s) stand beside it", railed, seated); }
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A subway station is laid from the build '{}' at {}, {}, {} turn, floor y {} to street y {}, {} block(s) of packing under it, {} of corridor to the platform and {} lamp(s) up the shaft", named, origin.getX(), origin.getZ(), how.getRotation(), base, surface, Math.max(0, base - level), bored, lit); }
        if (copies > 1 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The build '{}' is {} tall and the climb is {}, so its {} block band is laid {} times: the shaft stands {} block(s) tall", named, tall, surface - level, repeat, copies, grown); }
        return 1;
    }

    private static void band(World world, StructureBoundingBox clip, Template held, IBlockState lining, PlacementSettings how, int leastX, int at, int leastZ, int from, int to) {
        if (to < from) { return; }
        StructureBoundingBox only = new StructureBoundingBox(clip.minX, Math.max(clip.minY, from), clip.minZ,
                                                             clip.maxX, Math.min(clip.maxY, to), clip.maxZ);
        if (only.minY > only.maxY) { return; }
        PlacementSettings step = new PlacementSettings();
        step.setRotation(how.getRotation());
        step.setMirror(how.getMirror());
        step.setBoundingBox(only);
        held.addBlocksToWorld(world, new BlockPos(leastX, at, leastZ), dressed(lining), step, WORLDGEN_FLAGS);
    }

    private static ITemplateProcessor dressed(final IBlockState lining) {
        return (reader, at, was) -> {
            if (was.blockState.getBlock() != Blocks.SPONGE) { return was; }
            return new Template.BlockInfo(at, lining, was.tileentityData);
        };
    }

    private static int bore(World world, StructureBoundingBox clip, boolean alongX, int way, List<BlockPos> mouths, int center, int bedHalf, int platform, BeardRoads.Palette linings, BlockPos.MutableBlockPos at) {
        if (mouths.isEmpty()) { return 0; }
        IBlockState air = Blocks.AIR.getDefaultState();
        IBlockState lamp = stationLight();
        int run = stationLightRun();
        int wall = center + way * (bedHalf + platformWidth() + 1);
        BlockPos first = mouths.get(0);
        int fromAcross = alongX ? first.getZ() : first.getX();
        int target = platform + 1;
        int drop = first.getY() - target;
        int straight = Math.abs(fromAcross - wall);

        int extra = Math.max(0, drop - straight);
        if (straight + extra + 2 > PASSAGE_MOST) { return 0; }

        List<int[]> dug = new ArrayList<>();
        Set<Long> taken = new HashSet<>();
        for (BlockPos mouth : mouths) {
            int acr = fromAcross;
            int here = alongX ? mouth.getX() : mouth.getZ();
            int walk = mouth.getY();
            List<int[]> path = new ArrayList<>();
            path.add(new int[] { here, acr });
            if (extra > 0) {
                acr -= way;
                path.add(new int[] { here, acr });
                for (int i = 1; i <= extra; i++) { path.add(new int[] { here - i, acr }); }
                here -= extra;
            }
            while (way > 0 ? acr - way >= wall : acr - way <= wall) {
                acr -= way;
                path.add(new int[] { here, acr });
            }
            boolean start = true;
            for (int[] spot : path) {
                if (!start && walk > target) { walk--; }
                start = false;
                dug.add(new int[] { spot[0], spot[1], walk, mouth.getY() });
                taken.add(packed(spot[0], spot[1]));
            }
        }

        int laid = 0;
        for (int[] spot : dug) {
            int x = alongX ? spot[0] : spot[1];
            int z = alongX ? spot[1] : spot[0];
            boolean litHere = lamp.getBlock() != Blocks.AIR && Math.floorMod(spot[0] + spot[1], run) == 0;
            for (int y = spot[2] - 1; y <= spot[3] + HEADROOM + 1; y++) {
                if (!clip.isVecInside(at.setPos(x, y, z))) { continue; }
                BeardKeep.holdSpot(x, y, z);
                boolean roof = y > spot[2] + HEADROOM;
                boolean shell = y == spot[2] - 1 || roof;
                world.setBlockState(at, roof && litHere ? lamp : shell ? linings.pick(world, x, y, z) : air, WORLDGEN_FLAGS);
                laid++;
            }
        }
        for (int[] spot : dug) {
            for (int side = 0; side < 4; side++) {
                int nr = spot[0] + (side == 0 ? 1 : side == 1 ? -1 : 0);
                int na = spot[1] + (side == 2 ? 1 : side == 3 ? -1 : 0);
                if (taken.contains(packed(nr, na))) { continue; }
                if (way > 0 ? na < wall : na > wall) { continue; }
                int x = alongX ? nr : na;
                int z = alongX ? na : nr;
                for (int y = spot[2] - 1; y <= spot[3] + HEADROOM + 1; y++) {
                    if (!clip.isVecInside(at.setPos(x, y, z))) { continue; }
                    if (BeardKeep.holds(x, y, z)) { continue; }
                    world.setBlockState(at, linings.pick(world, x, y, z), WORLDGEN_FLAGS);
                    laid++;
                }
            }
        }
        if (extra > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The station corridor turns back along the line for {} block(s) to fall {} onto the platform, {} straight across would not have been enough", extra, drop, straight); }
        return laid;
    }

    private static long packed(int row, int across) { return ((long) row << 32) ^ (across & 0xFFFFFFFFL); }

    private static String entranceNamed() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayEntrance", Config.worldgen.villageSubwayEntrance).trim(); }

    public static int entrance(World world, StructureBoundingBox clip, RailPiece rail, int which, boolean alongX, int row, int across, int level) {
        String named = entranceNamed();
        if (named.isEmpty() || !(world instanceof WorldServer)) { return 0; }
        if (!stationNamed().isEmpty()) {
            if (!toldEntrance) {
                toldEntrance = true;
                ContentLog.LOGGER.info("villageSubwayStation names a build, which carries its own way in, so villageSubwayEntrance is left off rather than stood beside it as a shut box");
            }
            return 0;
        }
        int surface = topFor(world, rail, which, alongX, row, across, level);
        if (surface == Integer.MIN_VALUE) { return 0; }
        WorldServer server = (WorldServer) world;
        Template held = server.getStructureTemplateManager().get(server.getMinecraftServer(), new ResourceLocation(named));
        if (held == null) {
            if (!toldEntrance) {
                toldEntrance = true;
                ContentLog.LOGGER.error("villageSubwayEntrance '{}' could not be loaded, so the subway stairs come up bare", named);
            }
            return 0;
        }
        BlockPos size = held.getSize();
        if (onAPiece(x0(alongX, row, across, size), z0(alongX, row, across, size), size)) {
            if (!toldEntrance) {
                toldEntrance = true;
                ContentLog.LOGGER.info("A subway entrance would have stood on a street or the well plaza, so it is left off and the stairs come up bare");
            }
            return 0;
        }
        int x = x0(alongX, row, across, size);
        int z = z0(alongX, row, across, size);
        PlacementSettings how = new PlacementSettings();
        how.setBoundingBox(clip);
        held.addBlocksToWorld(world, new BlockPos(x, surface + 1, z), how, WORLDGEN_FLAGS);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        IBlockState air = Blocks.AIR.getDefaultState();
        for (int step = 0; step < 2; step++) {
            for (int over = 0; over < 2; over++) {
                int sx = alongX ? row + step : across + over;
                int sz = alongX ? across + over : row + step;
                for (int y = surface; y <= surface + size.getY(); y++) {
                    if (clip.isVecInside(at.setPos(sx, y, sz))) { world.setBlockState(at, air, WORLDGEN_FLAGS); }
                }
            }
        }
        return 1;
    }

    private static int stepX(boolean alongX, int row, int along, int near, int lane) { return alongX ? row + along : near + lane; }

    private static int stepZ(boolean alongX, int row, int along, int near, int lane) { return alongX ? near + lane : row + along; }

    private static int x0(boolean alongX, int row, int across, BlockPos size) { return (alongX ? row : across) - size.getX() / 2; }

    private static int z0(boolean alongX, int row, int across, BlockPos size) { return (alongX ? across : row) - size.getZ() / 2; }

    private static boolean onAPiece(int x, int z, BlockPos size) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return false; }
        int plaza = ContentBeard.plazaReach();
        for (StructureComponent piece : pieces) {
            boolean well = piece instanceof StructureVillagePieces.Well;
            if (!(piece instanceof StructureVillagePieces.Path) && !well) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            int out = well ? plaza : 0;
            if (x + size.getX() - 1 >= box.minX - out && x <= box.maxX + out
                    && z + size.getZ() - 1 >= box.minZ - out && z <= box.maxZ + out) { return true; }
        }
        return false;
    }

    private static int topFor(World world, RailPiece rail, int which, boolean alongX, int row, int across, int level) {
        int surface = rail.stationTop(which);
        if (surface != Integer.MIN_VALUE) { return surface; }
        surface = surfaceOver(world, alongX, row, across, level);
        if (surface != Integer.MIN_VALUE) { rail.stationTop(which, surface); }
        return surface;
    }

    private static int surfaceOver(World world, boolean alongX, int row, int across, int level) {
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        int held = roadTop(x, z, level);
        if (held != Integer.MIN_VALUE) { return held; }
        int sampled = ContentBeard.surfaceAt(world, x, z);
        return sampled <= level ? Integer.MIN_VALUE : sampled;
    }

    private static int roadTop(int x, int z, int level) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return Integer.MIN_VALUE; }
        int best = Integer.MIN_VALUE;
        int nearest = Integer.MAX_VALUE;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Path) || !(piece instanceof IRoadLayout)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            int offX = x < box.minX ? box.minX - x : x > box.maxX ? x - box.maxX : 0;
            int offZ = z < box.minZ ? box.minZ - z : z > box.maxZ ? z - box.maxZ : 0;
            int off = Math.max(offX, offZ);
            if (off > ROAD_NEAR || off >= nearest) { continue; }
            BeardRoads.Grade grade = ((IRoadLayout) piece).rdpl$layout();
            if (grade == null) { continue; }
            boolean roadAlongX = box.maxX - box.minX >= box.maxZ - box.minZ;
            int at = grade.at(roadAlongX ? x : z);
            if (at <= level) { continue; }
            nearest = off;
            best = at;
        }
        return best;
    }
}
