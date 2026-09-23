package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.FMLCommonHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import javax.annotation.Nullable;

public final class BeardStations {
    private static final int RAISE = 2;
    static final int RUN = 7;
    static final int WIDE = 5;
    private static final int SLIDE = 48;
    static final int WORLDGEN_FLAGS = 2;
    private static final Set<String> MISSING = new HashSet<>();
    private static boolean toldUnnamed = false;

    private BeardStations() {}

    private static BeardRoads.Palette platforms(BeardRoads.Palette linings) { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayPlatformBlock", Config.worldgen.villageSubwayPlatformBlock, BeardBiome.building()).trim().isEmpty() ? linings : BeardRoads.pathPalette("villageSubwayPlatformBlock", Config.worldgen.villageSubwayPlatformBlock, linings.first()); }

    private static int outFromLine() { return Math.max((BeardRoads.pathFullWidth() - 1) / 2 + 4, BeardRails.bedHalf(true) + platformWidth() + 3); }

    public static int reach() { return on() ? outFromLine() + WIDE + 1 : 0; }

    public static boolean on() { return length() > 0 && platformWidth() > 0 && build() != null; }

    @Nullable private static Template build() {
        String named = stationNamed();
        if (named.isEmpty()) {
            if (!toldUnnamed) {
                toldUnnamed = true;
                ContentLog.LOGGER.info("villageSubwayStation names no station build, so subway lines get no stations");
            }
            return null;
        }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || MISSING.contains(named)) { return null; }
        Template held = server.getWorld(0).getStructureTemplateManager().get(server, new ResourceLocation(named));
        if (held == null && MISSING.add(named)) { ContentLog.LOGGER.error("villageSubwayStation '{}' could not be loaded, so subway lines get no stations", named); }
        return held;
    }

    static int length() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationLength", Config.worldgen.villageSubwayStationLength)); }

    static int platformWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayPlatformWidth", Config.worldgen.villageSubwayPlatformWidth)); }

    private static int run() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationRun", Config.worldgen.villageSubwayStationRun)); }

    public static boolean stationAt(RailPiece rail, int row) {
        if (!on() || rail.trunk() != null) { return false; }
        int half = length() / 2;
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
        int reach = bedHalf + platformWidth();
        BeardRoads.Palette platforms = platforms(linings);
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
            if (added) { world.setBlockState(at.setPos(x, level + 1, z), platforms.pick(world, x, level + 1, z), 2); }
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

    @Nullable static StructureComponent streetAlong(@Nullable List<StructureComponent> components, boolean alongX, int center, int from, int to) {
        if (components == null) { return null; }
        for (StructureComponent piece : components) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            if (BeardPlots.roadAlongX(box) != alongX) { continue; }
            if (BeardRoads.roadNarrow(box, alongX)) { continue; }
            if (center < (alongX ? box.minZ : box.minX) || center > (alongX ? box.maxZ : box.maxX)) { continue; }
            if ((alongX ? box.minX : box.minZ) <= from && (alongX ? box.maxX : box.maxZ) >= to) { return piece; }
        }
        return null;
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
                if (streetAlong(components, alongX, center, row - 1, row + RUN + 1) == null) { continue; }
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
        Template held = build();
        if (held == null) { return 0; }
        int surface = topFor(rail, which, alongX, center, row);
        String why = BeardStationShaft.unreachable(held, alongX, way, center, bedHalf, row, near, level, surface);
        if (why != null) {
            ContentLog.LOGGER.error("The subway station at {}, {} on line {} was kept when its village was planned, but its stairs cannot be laid: {}", alongX ? row : near, alongX ? near : row, rail.line(), why);
            return 0;
        }
        int boxLeastX = alongX ? row - 1 : near - 1;
        int boxLeastZ = alongX ? near - 1 : row - 1;
        int stamped = BeardStationShaft.stamp(world, clip, held, alongX, way, center, bedHalf, boxLeastX, boxLeastZ, level, surface, linings, at);
        if (stamped > 0) { BeardStationShaft.approach(world, clip, alongX, row, near, way, center, surface, linings, at); }
        return stamped;
    }

    static BeardRoads.Palette railingBlocks() { return BeardRoads.pathPalette("villageSubwayRailingBlock", Config.worldgen.villageSubwayRailingBlock, Blocks.AIR.getDefaultState()); }

    private static IBlockState benchBlock() { return BeardRoads.pathBlock("villageSubwayBenchBlock", Config.worldgen.villageSubwayBenchBlock, Blocks.AIR.getDefaultState()); }

    private static BeardRoads.Palette benchEndBlocks() { return BeardRoads.pathPalette("villageSubwayBenchEndBlock", Config.worldgen.villageSubwayBenchEndBlock, Blocks.AIR.getDefaultState()); }

    static int benchLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayBenchLength", Config.worldgen.villageSubwayBenchLength)); }

    static boolean edgeRailed(StructureBoundingBox road, boolean alongX, int row, boolean high) { return openingAt(ContentBeard.components(), road, alongX, row, high) == null; }

    @Nullable public static StructureBoundingBox openingAt(@Nullable List<StructureComponent> pieces, StructureBoundingBox road, boolean alongX, int row, boolean high) {
        if (!on() || pieces == null) { return null; }
        int roadLeast = alongX ? road.minZ : road.minX;
        int roadMost = alongX ? road.maxZ : road.maxX;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof RailPiece)) { continue; }
            RailPiece rail = (RailPiece) piece;
            if (rail.alongX() != alongX) { continue; }
            int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
            if (center < roadLeast || center > roadMost) { continue; }
            for (StructureBoundingBox box : rail.stations()) {
                int boxLeast = alongX ? box.minX : box.minZ;
                int boxMost = alongX ? box.maxX : box.maxZ;
                if (row <= boxLeast || row >= boxMost) { continue; }
                if ((alongX ? road.minX : road.minZ) > boxLeast || (alongX ? road.maxX : road.maxZ) < boxMost) { continue; }
                if (high ? (alongX ? box.minZ : box.minX) > roadMost : (alongX ? box.maxZ : box.maxX) < roadLeast) { return box; }
            }
        }
        return null;
    }

    private static IBlockState faced(IBlockState seat, EnumFacing way) {
        if (!seat.getPropertyKeys().contains(BlockHorizontal.FACING)) { return seat; }
        return seat.withProperty(BlockHorizontal.FACING, way);
    }

    static int bench(World world, StructureBoundingBox clip, boolean alongX, int from, int across, int y, EnumFacing facing, BlockPos.MutableBlockPos at) {
        IBlockState seat = faced(benchBlock(), facing);
        BeardRoads.Palette arms = benchEndBlocks();
        int span = benchLength();
        if (span < 2 || seat.getBlock() == Blocks.AIR) { return 0; }
        int laid = 0;
        for (int i = 0; i < span; i++) {
            int x = alongX ? from + i : across;
            int z = alongX ? across : from + i;
            boolean end = i == 0 || i == span - 1;
            IBlockState put = end ? arms.pick(world, x, y, z) : seat;
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

    static EnumFacing awayFrom(boolean alongX, int way) {
        if (alongX) { return way > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH; }
        return way > 0 ? EnumFacing.EAST : EnumFacing.WEST;
    }

    public static int platformBench(World world, StructureBoundingBox clip, boolean alongX, int from, int center, int level, int bedHalf, int way, BlockPos.MutableBlockPos at) {
        if (platformWidth() <= 0) { return 0; }
        int across = center + way * (bedHalf + (platformWidth() + 1) / 2);
        return bench(world, clip, alongX, from, across, level + 2, awayFrom(alongX, way), at);
    }

    static String stationNamed() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayStation", Config.worldgen.villageSubwayStation).trim(); }

    public static int stairRow(RailPiece rail, StructureBoundingBox stair) { return (rail.alongX() ? stair.minX : stair.minZ) + 1; }

    public static int stairNear(RailPiece rail, StructureBoundingBox stair) { return (rail.alongX() ? stair.minZ : stair.minX) + 1; }

    public static int stairLevel(RailPiece rail, BeardRoads.Grade grade, StructureBoundingBox stair) {
        int held = heldLevel(rail, grade, heartOf(rail, stair));
        return held != Integer.MIN_VALUE ? held : grade.at(stairRow(rail, stair));
    }

    public static void prune(World world, StructureStart start) {
        Template held = on() ? build() : null;
        if (held == null) { return; }
        List<StructureComponent> components = start.getComponents();
        List<StructureComponent> laid = ContentBeard.laid();
        ContentBeard.laying(components);
        try {
            for (StructureComponent piece : components) {
                if (piece instanceof RailPiece && ((RailPiece) piece).subway() && ((RailPiece) piece).trunk() == null) { prune(world, (RailPiece) piece, held); }
            }
        }
        finally { ContentBeard.laying(laid); }
    }

    private static void prune(World world, RailPiece rail, Template held) {
        boolean alongX = rail.alongX();
        int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
        int bedHalf = BeardRails.bedHalf(true);
        for (int which = rail.stations().size() - 1; which >= 0; which--) {
            BeardRoads.Grade grade = rail.grade(world);
            if (grade == null) { return; }
            StructureBoundingBox stair = rail.stations().get(which);
            int row = stairRow(rail, stair);
            int near = stairNear(rail, stair);
            int level = stairLevel(rail, grade, stair);
            if (level == Integer.MIN_VALUE) { continue; }
            String why = BeardStationShaft.unreachable(held, alongX, near > center ? 1 : -1, center, bedHalf, row, near, level, streetTop(alongX, center, row));
            if (why == null) { continue; }
            rail.dropStation(which);
            rail.regrade();
            ContentLog.LOGGER.info("The subway station claimed at {}, {} on line {} is not built, since {}", stair.minX, stair.minZ, rail.line(), why);
        }
    }

    private static int topFor(RailPiece rail, int which, boolean alongX, int center, int row) {
        int surface = rail.stationTop(which);
        if (surface != Integer.MIN_VALUE) { return surface; }
        surface = streetTop(alongX, center, row);
        if (surface != Integer.MIN_VALUE) { rail.stationTop(which, surface); }
        return surface;
    }

    private static int streetTop(boolean alongX, int center, int row) {
        StructureComponent street = streetAlong(ContentBeard.components(), alongX, center, row - 1, row + RUN + 1);
        if (!(street instanceof IRoadLayout)) { return Integer.MIN_VALUE; }
        BeardRoads.Grade grade = ((IRoadLayout) street).rdpl$layout();
        return grade == null ? Integer.MIN_VALUE : grade.at(row + RUN / 2);
    }
}
