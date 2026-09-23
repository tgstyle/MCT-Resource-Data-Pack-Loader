package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;

public final class BeardRails {
    static final int MARGIN = 7;
    static final int OVER = 6;
    static final int FLOOR_LEAST = 6;
    static final int FILL = 3;
    static final int LEAST_OPEN = 8;
    static final int CLEAR = 4;
    static final int FILL_UNDER = 8;
    static final int BELOW = 24;
    static final int ABOVE = 40;

    private BeardRails() {}

    public static int lines(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayLines" : "villageRailLines", sub ? Config.worldgen.villageSubwayLines : Config.worldgen.villageRailLines)); }

    public static int spacing(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwaySpacing" : "villageRailSpacing", sub ? Config.worldgen.villageSubwaySpacing : Config.worldgen.villageRailSpacing)); }

    public static int width(boolean sub) { return bed(sub) + 2 * shoulderWidth(sub); }

    private static int askedWidth(boolean sub) { return Math.max(3, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayWidth" : "villageRailWidth", sub ? Config.worldgen.villageSubwayWidth : Config.worldgen.villageRailWidth)); }

    static int bedHalf(boolean sub) { return (bed(sub) - 1) / 2; }

    private static int bed(boolean sub) { return Math.max(askedWidth(sub), (tracks(sub) - 1) * trackGap(sub) + 3); }

    static int shoulderWidth(boolean sub) {
        if (BeardRoads.pathBlock(sub ? "villageSubwayShoulderBlock" : "villageRailShoulderBlock", sub ? Config.worldgen.villageSubwayShoulderBlock : Config.worldgen.villageRailShoulderBlock, Blocks.AIR.getDefaultState()).getBlock() == Blocks.AIR) { return 0; }
        return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayShoulderWidth" : "villageRailShoulderWidth", sub ? Config.worldgen.villageSubwayShoulderWidth : Config.worldgen.villageRailShoulderWidth));
    }

    static int tracks(boolean sub) {
        int asked = Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTracks" : "villageRailTracks", sub ? Config.worldgen.villageSubwayTracks : Config.worldgen.villageRailTracks));
        if (asked > 0) { return asked; }
        return askedWidth(sub) >= 5 ? 2 : 1;
    }

    static int trackGap(boolean sub) { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTrackGap" : "villageRailTrackGap", sub ? Config.worldgen.villageSubwayTrackGap : Config.worldgen.villageRailTrackGap)); }

    static boolean trackInBed(IBlockState track, boolean sub) {
        String named = ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTrackSeat" : "villageRailTrackSeat", sub ? Config.worldgen.villageSubwayTrackSeat : Config.worldgen.villageRailTrackSeat).trim().toLowerCase(Locale.ROOT);
        if (named.isEmpty() || "auto".equals(named)) { return !(track.getBlock() instanceof BlockRailBase); }
        if (named.startsWith("i")) { return true; }
        if (named.startsWith("o")) { return false; }
        ContentLog.LOGGER.error("villageRailTrackSeat '{}' is not auto, on or in, so the track is seated the way its block asks for", named);
        return !(track.getBlock() instanceof BlockRailBase);
    }

    public static int climb(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayClimb" : "villageRailClimb", sub ? Config.worldgen.villageSubwayClimb : Config.worldgen.villageRailClimb)); }

    public static int tail(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTail" : "villageRailTail", sub ? Config.worldgen.villageSubwayTail : Config.worldgen.villageRailTail)); }

    static int tieRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTieRun" : "villageRailTieRun", sub ? Config.worldgen.villageSubwayTieRun : Config.worldgen.villageRailTieRun)); }

    static int powerRun(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayPowerRun" : "villageRailPowerRun", sub ? Config.worldgen.villageSubwayPowerRun : Config.worldgen.villageRailPowerRun)); }

    static BeardRoads.Palette frameBlocks() { return BeardRoads.pathPalette("villageRailBridgeFrameBlock", Config.worldgen.villageRailBridgeFrameBlock, Blocks.AIR.getDefaultState()); }

    static int frameHeight() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameHeight", Config.worldgen.villageRailBridgeFrameHeight)); }

    static int frameRun() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameRun", Config.worldgen.villageRailBridgeFrameRun)); }

    static int frameLeast() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameLeast", Config.worldgen.villageRailBridgeFrameLeast)); }

    private static IBlockState tunnelBlock(boolean sub) { return BeardRoads.pathBlock(sub ? "villageSubwayTunnelBlock" : "villageRailTunnelBlock", sub ? Config.worldgen.villageSubwayTunnelBlock : Config.worldgen.villageRailTunnelBlock, Blocks.AIR.getDefaultState()); }

    static int tunnelLightRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTunnelLightRun" : "villageRailTunnelLightRun", sub ? Config.worldgen.villageSubwayTunnelLightRun : Config.worldgen.villageRailTunnelLightRun)); }

    private static int surfacesChance() { return MathHelper.clamp(ContentControl.number(ContentControl.VILLAGES, "villageSubwaySurfaces", Config.worldgen.villageSubwaySurfaces), 0, 100); }

    static boolean alongTheLine(StructureBoundingBox road, boolean alongX) {
        int along = (alongX ? road.maxX - road.minX : road.maxZ - road.minZ) + 1;
        int across = (alongX ? road.maxZ - road.minZ : road.maxX - road.minX) + 1;
        return along > across;
    }

    @Nullable public static int[] streetOver(RailPiece rail) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return null; }
        boolean alongX = rail.alongX();
        int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
        int least = Integer.MAX_VALUE;
        int most = Integer.MIN_VALUE;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            if (!alongTheLine(box, alongX)) { continue; }
            if (center < (alongX ? box.minZ : box.minX) || center > (alongX ? box.maxZ : box.maxX)) { continue; }
            least = Math.min(least, alongX ? box.minX : box.minZ);
            most = Math.max(most, alongX ? box.maxX : box.maxZ);
        }
        return least > most ? null : new int[] { least, most };
    }

    @Nullable public static int[] surfacing(World world, RailPiece rail) {
        if (!rail.subway() || rail.trunk() != null) { return null; }
        if (rail.ends().length > 0) {
            int[] linked = BeardLinks.climbOut(rail);
            ContentLog.LOGGER.debug("Subway line {} {} toward its railway link", rail.line(), linked == null ? "stays buried to meet a trunk underground" : "climbs out at row " + linked[0]);
            return linked;
        }
        int chance = surfacesChance();
        if (chance <= 0) { return null; }
        StructureBoundingBox box = rail.getBoundingBox();
        Random roll = SeededRandom.at(world, box.minX + rail.line(), box.minY, box.minZ);
        if (roll.nextInt(100) >= chance) { ContentLog.LOGGER.debug("Subway line {} rolled to stay buried at {} in a hundred", rail.line(), chance); return null; }
        int least = rail.rowLeast();
        int most = rail.rowMost();
        int ramp = subwayDepth() * climb(true);
        int shortest = ramp + LEAST_OPEN;
        if (most - least < shortest) { return null; }
        int run = ramp + Math.max(LEAST_OPEN, tail(true));
        int stationLeast = Integer.MAX_VALUE;
        int stationMost = Integer.MIN_VALUE;
        for (StructureBoundingBox held : rail.stations()) {
            stationLeast = Math.min(stationLeast, rail.alongX() ? held.minX : held.minZ);
            stationMost = Math.max(stationMost, rail.alongX() ? held.maxX : held.maxZ);
        }
        boolean anyStation = !rail.stations().isEmpty();
        int[] street = streetOver(rail);
        int lowRow = street == null ? least + run : Math.min(least + run, street[0] - 1);
        int highRow = street == null ? most - run : Math.max(most - run, street[1] + 1);
        boolean canLow = lowRow - least >= shortest && (!anyStation || stationLeast > lowRow + ramp);
        boolean canHigh = most - highRow >= shortest && (!anyStation || stationMost < highRow - ramp);
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line {} weighs a climb-out: rows {} to {}, ramp {} and run {} (shortest {}), streets over it {}, stations {} to {}, low end at row {} {}, high end at row {} {}", rail.line(), least, most, ramp, run, shortest, street == null ? "none" : street[0] + " to " + street[1], anyStation ? stationLeast : "none", anyStation ? stationMost : "none", lowRow, canLow ? "can" : "cannot", highRow, canHigh ? "can" : "cannot"); }
        if (!canHigh && !canLow) { return null; }
        boolean high = canHigh && (!canLow || roll.nextBoolean());
        return high ? new int[] { highRow, 1 } : new int[] { lowRow, -1 };
    }

    public static boolean surfaced(@Nullable int[] rising, int row) {
        if (rising == null) { return false; }
        return rising[1] > 0 ? row >= rising[0] : row <= rising[0];
    }

    public static int subwayDepth() { return Math.max(6, ContentControl.number(ContentControl.VILLAGES, "villageSubwayDepth", Config.worldgen.villageSubwayDepth)); }

    public static int tunnelDepth(boolean sub) {
        if (tunnelBlock(sub).getBlock() == Blocks.AIR) { return 0; }
        return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailTunnelDepth", Config.worldgen.villageRailTunnelDepth));
    }

    static boolean direction(Random rand, boolean sub) {
        String named = ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayDirection" : "villageRailDirection", sub ? Config.worldgen.villageSubwayDirection : Config.worldgen.villageRailDirection).trim().toLowerCase(Locale.ROOT);
        if (named.isEmpty() || "any".equals(named)) { return rand.nextBoolean(); }
        if (named.startsWith("e") || named.startsWith("w")) { return true; }
        if (named.startsWith("n") || named.startsWith("s")) { return false; }
        ContentLog.LOGGER.error("villageRailDirection '{}' is not ew, ns or any, so the lines run as they roll", named);
        return rand.nextBoolean();
    }

    static int half(boolean sub) { return (width(sub) - 1) / 2 + (sub ? BeardStations.reach() : 0); }

    static int clear(boolean sub) { return sub ? 0 : ContentBeard.plazaReach() + Math.max(13, ContentVillages.largestPlot()) + BeardRoads.pathFullWidth() + half(false) + 2; }

    private static int acrossReach() { return half(true) + 1 + BeardLinks.linkReach(); }

    static boolean clearsWell(StructureBoundingBox well, boolean alongX, int center, boolean sub) {
        if (!sub) { return true; }
        int reach = acrossReach();
        return center + reach < (alongX ? well.minZ : well.minX) || center - reach > (alongX ? well.maxZ : well.maxX);
    }

    static int offWell(StructureBoundingBox well, boolean alongX, int center, int side, boolean sub) {
        if (clearsWell(well, alongX, center, sub)) { return center; }
        int reach = acrossReach();
        return side >= 0 ? (alongX ? well.maxZ : well.maxX) + reach + 1 : (alongX ? well.minZ : well.minX) - reach - 1;
    }

    public static boolean boreUnder(@Nullable List<StructureComponent> pieces, StructureBoundingBox well) {
        if (pieces == null) { return false; }
        for (StructureComponent piece : pieces) {
            if (!buried(piece)) { continue; }
            StructureBoundingBox bore = dressed(piece);
            if (bore.intersectsWith(well.minX, well.minZ, well.maxX, well.maxZ)) { return true; }
        }
        return false;
    }

    static int[] firstLine(World world, StructureBoundingBox wellBox, boolean sub) {
        Random roll = BeardLinks.roll(world, wellBox, sub);
        boolean alongX = direction(roll, sub);
        int candidate = clear(sub) + roll.nextInt(Math.max(1, spacing(sub) / 2));
        return new int[] { alongX ? 1 : 0, offWell(wellBox, alongX, (alongX ? (wellBox.minZ + wellBox.maxZ) / 2 : (wellBox.minX + wellBox.maxX) / 2) + candidate, 1, sub) };
    }

    public static boolean crosses(StructureBoundingBox rail, StructureBoundingBox box) {
        boolean railAlongX = rail.maxX - rail.minX >= rail.maxZ - rail.minZ;
        boolean boxAlongX = box.maxX - box.minX >= box.maxZ - box.minZ;
        if (railAlongX == boxAlongX) { return false; }
        int stripLeast = railAlongX ? rail.minZ : rail.minX;
        int stripMost = railAlongX ? rail.maxZ : rail.maxX;
        int boxLeast = railAlongX ? box.minZ : box.minX;
        int boxMost = railAlongX ? box.maxZ : box.maxX;
        return boxLeast <= stripLeast - MARGIN && boxMost >= stripMost + MARGIN;
    }

    public static boolean blocked(@Nullable List<StructureComponent> pieces, StructureBoundingBox box) {
        if (pieces == null) { return false; }
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof RailPiece) || buried(piece)) { continue; }
            StructureBoundingBox rail = piece.getBoundingBox();
            if (!rail.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { continue; }
            if (crosses(rail, box)) { continue; }
            ContentLog.LOGGER.debug("A road attempt {} would run onto railway line {} at {}, {} without crossing it clean, so it is refused", box, ((RailPiece) piece).line(), rail.minX, rail.minZ);
            return true;
        }
        return false;
    }

    public static boolean isRail(StructureComponent piece) { return piece instanceof RailPiece; }

    public static boolean buried(StructureComponent piece) { return piece instanceof RailPiece && ((RailPiece) piece).subway(); }

    public static boolean buriedUnder(StructureComponent piece, StructureBoundingBox box) {
        if (!buried(piece)) { return false; }
        RailPiece rail = (RailPiece) piece;
        int[] rising = rail.risingKnown();
        if (rising == null) { return true; }
        boolean alongX = rail.alongX();
        int lo = alongX ? box.minX : box.minZ;
        int hi = alongX ? box.maxX : box.maxZ;
        return rising[1] > 0 ? hi < rising[0] : lo > rising[0];
    }

    public static List<RailPiece> subways(World world, StructureBoundingBox near) {
        List<RailPiece> found = railways(world, near);
        found.removeIf(rail -> !rail.subway());
        return found;
    }

    public static List<RailPiece> railways(World world, StructureBoundingBox near) {
        List<RailPiece> found = new ArrayList<>();
        if (!ContentBeard.wanted()) { return found; }
        for (StructureComponent piece : ContentBeard.everyone(world, ContentBeard.components())) {
            if (!(piece instanceof RailPiece)) { continue; }
            StructureBoundingBox box = dressed(piece);
            if (box.maxX < near.minX || box.minX > near.maxX || box.maxZ < near.minZ || box.minZ > near.maxZ) { continue; }
            found.add((RailPiece) piece);
        }
        return found;
    }

    private static int boreLevel(World world, RailPiece rail, int x, int z) {
        StructureBoundingBox box = dressed(rail);
        if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { return Integer.MIN_VALUE; }
        boolean alongX = rail.alongX();
        int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
        if (Math.abs((alongX ? z : x) - center) > (width(rail.subway()) - 1) / 2 + 1) { return Integer.MIN_VALUE; }
        BeardRoads.Grade grade = rail.grade(world);
        return grade == null ? Integer.MIN_VALUE : grade.at(alongX ? x : z);
    }

    public static boolean insideBore(World world, List<RailPiece> subways, int x, int y, int z) {
        for (RailPiece rail : subways) {
            int level = boreLevel(world, rail, x, z);
            if (level != Integer.MIN_VALUE && y >= level - 1 && y <= level + CLEAR + 1) { return true; }
        }
        return false;
    }

    public static boolean runsInto(World world, List<RailPiece> lines, int x, int y, int z) {
        for (RailPiece rail : lines) {
            int level = boreLevel(world, rail, x, z);
            if (level == Integer.MIN_VALUE || y < level - 1) { continue; }
            if (!rail.subway() || y <= level + CLEAR + 1) { return true; }
        }
        return false;
    }

    public static boolean underBed(World world, List<RailPiece> lines, int x, int y, int z) {
        for (RailPiece rail : lines) {
            int level = boreLevel(world, rail, x, z);
            if (y <= level && y >= level - FILL_UNDER) { return true; }
        }
        return false;
    }

    public static int boreRoof(World world, List<RailPiece> subways, int x, int z) {
        int roof = Integer.MIN_VALUE;
        for (RailPiece rail : subways) {
            int level = boreLevel(world, rail, x, z);
            if (level != Integer.MIN_VALUE) { roof = Math.max(roof, level + CLEAR + 1); }
        }
        return roof;
    }

    public static int boreFloor(World world, List<RailPiece> subways, int x, int z) { return Math.max(1, boreRoof(world, subways, x, z) + 1); }

    public static int boreFloor(World world, int x, int z) {
        return boreFloor(world, subways(world, new StructureBoundingBox(x, 0, z, x, 0, z)), x, z);
    }

    public static boolean insideBore(World world, int x, int y, int z) {
        return insideBore(world, subways(world, new StructureBoundingBox(x, 0, z, x, 0, z)), x, y, z);
    }

    public static boolean meets(StructureBoundingBox rail, StructureBoundingBox box) {
        boolean railAlongX = rail.maxX - rail.minX >= rail.maxZ - rail.minZ;
        boolean boxAlongX = box.maxX - box.minX >= box.maxZ - box.minZ;
        return railAlongX != boxAlongX && rail.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ);
    }

    @Nullable public static int[] lengths(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        if (pieces == null) { return null; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int step = (alongX ? facing.getXOffset() : facing.getZOffset()) >= 0 ? 1 : -1;
        int from = step > 0 ? (alongX ? box.minX : box.minZ) : (alongX ? box.maxX : box.maxZ);
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        int wanted = rows;
        int shortest = rows;
        boolean met = false;
        for (StructureComponent piece : pieces) {
            if (!(piece instanceof RailPiece) || buriedUnder(piece, box)) { continue; }
            RailPiece rail = (RailPiece) piece;
            StructureBoundingBox strip = rail.getBoundingBox();
            if (rail.alongX() == alongX) {
                if (strip.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { return new int[] { -1 }; }
                continue;
            }
            int acrossLeast = alongX ? box.minZ : box.minX;
            int acrossMost = alongX ? box.maxZ : box.maxX;
            if (acrossMost < rail.rowLeast() || acrossLeast > rail.rowMost()) { continue; }
            int stripLeast = rail.acrossLeast();
            int stripMost = rail.acrossMost();
            int near = step > 0 ? stripLeast - from : from - stripMost;
            int far = step > 0 ? stripMost - from : from - stripLeast;
            if (far < 0 || near - MARGIN > rows - 1) { continue; }
            if (near <= 0 || acrossLeast < rail.rowLeast() || acrossMost > rail.rowMost()) { return new int[] { -1 }; }
            met = true;
            wanted = Math.max(wanted, far + 1 + MARGIN);
            shortest = Math.min(shortest, near - MARGIN);
        }
        if (!met) { return null; }
        return new int[] { wanted, shortest };
    }

    public static boolean cramped(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        int kept;
        try { kept = BeardRoadsGrade.roadReach(box, facing); }
        finally { ContentBeard.laying(held); }
        return kept < rows || ContentBeard.roomFor(pieces, box, facing) < rows;
    }

    public static boolean blocks(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        int[] lengths = lengths(pieces, box, facing);
        if (lengths == null) { return false; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        for (int rows : lengths) {
            if (rows < 7) { continue; }
            StructureBoundingBox tried = new StructureBoundingBox(box);
            BeardLayout.trim(tried, alongX, facing, rows);
            if (cramped(pieces, tried, facing)) { continue; }
            BeardLayout.trim(box, alongX, facing, rows);
            ContentLog.LOGGER.debug("A road attempt facing {} is set to {} block(s) {} the railway line in its way", facing, rows, rows == lengths[0] ? "to cross" : "to stop short of");
            return false;
        }
        ContentLog.LOGGER.debug("A road attempt facing {} can neither cross the railway line in its way nor stop short of it, so it is refused", facing);
        return true;
    }

    public static boolean railBlock(IBlockState state) { return state.getBlock() instanceof BlockRailBase; }

    public static int hold(World world, @Nullable StructureComponent piece, boolean alongX, int start, int acrossLeast, int acrossMost, int[] profile, boolean[] held) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return 0; }
        int rowMost = start + profile.length - 1;
        int center = (acrossLeast + acrossMost) / 2;
        int depth = tunnelDepth(false);
        int pinned = 0;
        for (StructureComponent other : pieces) {
            if (other == piece || !(other instanceof RailPiece)) { continue; }
            RailPiece rail = (RailPiece) other;
            if (rail.subway()) { continue; }
            if (rail.alongX() == alongX) { continue; }
            if (rail.rowLeast() > acrossMost || rail.rowMost() < acrossLeast) { continue; }
            int stripLeast = rail.acrossLeast();
            int stripMost = rail.acrossMost();
            if (stripMost < start || stripLeast > rowMost) { continue; }
            BeardRoads.Grade grade = rail.grade(world);
            if (grade == null) { continue; }
            int level = grade.at(center);
            if (level == Integer.MIN_VALUE) { continue; }
            if (grade.tunneledAt(center, depth)) {
                int middle = (Math.max(start, stripLeast) + Math.min(rowMost, stripMost)) / 2;
                int natural = middle >= start && middle <= rowMost ? profile[middle - start] : Integer.MIN_VALUE;
                if (natural != Integer.MIN_VALUE && natural >= level + OVER) { continue; }
            }
            for (int row = Math.max(start, stripLeast - 1); row <= Math.min(rowMost, stripMost + 1); row++) {
                int i = row - start;
                if (held[i]) { continue; }
                profile[i] = level;
                held[i] = true;
                pinned++;
            }
        }
        return pinned;
    }

    public static StructureBoundingBox dressed(StructureComponent piece) {
        StructureBoundingBox box = piece.getBoundingBox();
        if (!(piece instanceof RailPiece)) { return box; }
        int besideX = ((RailPiece) piece).alongX() ? 0 : 1;
        int besideZ = 1 - besideX;
        return new StructureBoundingBox(box.minX - besideX, box.minY, box.minZ - besideZ, box.maxX + besideX, box.maxY, box.maxZ + besideZ);
    }

    public static void dress(StructureStart start) {
        StructureBoundingBox held = start.getBoundingBox();
        for (StructureComponent piece : start.getComponents()) {
            if (piece instanceof RailPiece) { held.expandTo(dressed(piece)); }
        }
    }

    static int[] trackRows(boolean sub, int center) {
        int count = Math.max(1, tracks(sub));
        int gap = trackGap(sub);
        int first = count == 1 ? center : center - (count - 1) * gap / 2;
        int[] rows = new int[count];
        for (int i = 0; i < count; i++) { rows[i] = first + i * gap; }
        return rows;
    }

    static IBlockState shaped(IBlockState state, BlockRailBase.EnumRailDirection wanted, boolean alongX) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property.getValueClass() != BlockRailBase.EnumRailDirection.class) { continue; }
            @SuppressWarnings("unchecked") IProperty<BlockRailBase.EnumRailDirection> shape = (IProperty<BlockRailBase.EnumRailDirection>) property;
            if (shape.getAllowedValues().contains(wanted)) { return state.withProperty(shape, wanted); }
        }
        return oriented(state, alongX);
    }

    @SuppressWarnings("unchecked") static IBlockState oriented(IBlockState state, boolean alongX) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property.getValueClass() != BlockRailBase.EnumRailDirection.class) { continue; }
            IProperty<BlockRailBase.EnumRailDirection> shape = (IProperty<BlockRailBase.EnumRailDirection>) property;
            BlockRailBase.EnumRailDirection wanted = alongX ? BlockRailBase.EnumRailDirection.EAST_WEST : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            if (shape.getAllowedValues().contains(wanted)) { return state.withProperty(shape, wanted); }
        }
        return ContentBeard.axised(state, alongX);
    }
}
