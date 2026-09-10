package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.blastplaster.util.BlastPlasterUtil;
import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.ContentVillageDecor;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GenHeights;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import mctmods.resourcedatapackloader.util.world.SeededRandom;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class BeardRails {
    private static final int MARGIN = 7;
    private static final int OVER = 6;
    private static final int FILL = 3;
    private static final int LEAST_OPEN = 8;
    static final int CLEAR = 4;
    private static final int WET_REACH = 3;
    private static final int CANOPY = 14;
    private static final int BESIDE = 2;
    private static final int TOGETHER = 16;
    private static final int LEG = 4;
    private static final int FILL_UNDER = 8;
    private static final int STRETCH = 32;
    private static final int BELOW = 24;
    private static final int ABOVE = 40;

    private BeardRails() {}

    public static int lines(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayLines" : "villageRailLines", sub ? Config.worldgen.villageSubwayLines : Config.worldgen.villageRailLines)); }

    public static int spacing(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwaySpacing" : "villageRailSpacing", sub ? Config.worldgen.villageSubwaySpacing : Config.worldgen.villageRailSpacing)); }

    public static int width(boolean sub) { return bed(sub) + 2 * shoulderWidth(sub); }

    private static int askedWidth(boolean sub) { return Math.max(3, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayWidth" : "villageRailWidth", sub ? Config.worldgen.villageSubwayWidth : Config.worldgen.villageRailWidth)); }

    static int bedHalf(boolean sub) { return (bed(sub) - 1) / 2; }

    private static int bed(boolean sub) { return Math.max(askedWidth(sub), (tracks(sub) - 1) * trackGap(sub) + 3); }

    private static int shoulderWidth(boolean sub) {
        if (BeardRoads.pathBlock(sub ? "villageSubwayShoulderBlock" : "villageRailShoulderBlock", sub ? Config.worldgen.villageSubwayShoulderBlock : Config.worldgen.villageRailShoulderBlock, Blocks.AIR.getDefaultState()).getBlock() == Blocks.AIR) { return 0; }
        return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayShoulderWidth" : "villageRailShoulderWidth", sub ? Config.worldgen.villageSubwayShoulderWidth : Config.worldgen.villageRailShoulderWidth));
    }

    private static int tracks(boolean sub) {
        int asked = Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTracks" : "villageRailTracks", sub ? Config.worldgen.villageSubwayTracks : Config.worldgen.villageRailTracks));
        if (asked > 0) { return asked; }
        return askedWidth(sub) >= 5 ? 2 : 1;
    }

    private static int trackGap(boolean sub) { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTrackGap" : "villageRailTrackGap", sub ? Config.worldgen.villageSubwayTrackGap : Config.worldgen.villageRailTrackGap)); }

    private static boolean trackInBed(IBlockState track, boolean sub) {
        String named = ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTrackSeat" : "villageRailTrackSeat", sub ? Config.worldgen.villageSubwayTrackSeat : Config.worldgen.villageRailTrackSeat).trim().toLowerCase(Locale.ROOT);
        if (named.isEmpty() || "auto".equals(named)) { return !(track.getBlock() instanceof BlockRailBase); }
        if (named.startsWith("i")) { return true; }
        if (named.startsWith("o")) { return false; }
        ContentLog.LOGGER.error("villageRailTrackSeat '{}' is not auto, on or in, so the track is seated the way its block asks for", named);
        return !(track.getBlock() instanceof BlockRailBase);
    }

    public static int climb(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayClimb" : "villageRailClimb", sub ? Config.worldgen.villageSubwayClimb : Config.worldgen.villageRailClimb)); }

    public static int tail(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTail" : "villageRailTail", sub ? Config.worldgen.villageSubwayTail : Config.worldgen.villageRailTail)); }

    private static int tieRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTieRun" : "villageRailTieRun", sub ? Config.worldgen.villageSubwayTieRun : Config.worldgen.villageRailTieRun)); }

    private static int powerRun(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayPowerRun" : "villageRailPowerRun", sub ? Config.worldgen.villageSubwayPowerRun : Config.worldgen.villageRailPowerRun)); }

    private static IBlockState frameBlock() { return BeardRoads.pathBlock("villageRailBridgeFrameBlock", Config.worldgen.villageRailBridgeFrameBlock, Blocks.AIR.getDefaultState()); }

    private static int frameHeight() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameHeight", Config.worldgen.villageRailBridgeFrameHeight)); }

    private static int frameRun() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameRun", Config.worldgen.villageRailBridgeFrameRun)); }

    private static int frameLeast() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameLeast", Config.worldgen.villageRailBridgeFrameLeast)); }

    private static IBlockState tunnelBlock(boolean sub) { return BeardRoads.pathBlock(sub ? "villageSubwayTunnelBlock" : "villageRailTunnelBlock", sub ? Config.worldgen.villageSubwayTunnelBlock : Config.worldgen.villageRailTunnelBlock, Blocks.AIR.getDefaultState()); }

    private static int tunnelLightRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTunnelLightRun" : "villageRailTunnelLightRun", sub ? Config.worldgen.villageSubwayTunnelLightRun : Config.worldgen.villageRailTunnelLightRun)); }

    private static int surfacesChance() { return MathHelper.clamp(ContentControl.number(ContentControl.VILLAGES, "villageSubwaySurfaces", Config.worldgen.villageSubwaySurfaces), 0, 100); }

    private static int holdStation(RailPiece rail, int[] profile, int rowLeast, int rows) {
        if (!rail.subway() || rail.stations().isEmpty()) { return 0; }
        int half = Math.max(0, BeardStations.length() / 2);
        int held = 0;
        for (StructureBoundingBox box : rail.stations()) {
            int heart = BeardStations.heartOf(rail, box) - rowLeast;
            if (heart < 0 || heart >= rows) { continue; }
            int flat = profile[heart];
            if (flat == Integer.MIN_VALUE) { continue; }
            for (int at = Math.max(0, heart - half); at <= Math.min(rows - 1, heart + half); at++) {
                if (profile[at] == Integer.MIN_VALUE || profile[at] == flat) { continue; }
                profile[at] = flat;
                held++;
            }
        }
        return held;
    }

    private static boolean alongTheLine(StructureBoundingBox road, boolean alongX) {
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
        if (!rail.subway()) { return null; }
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

    private static int cutWall(World world, BlockPos.MutableBlockPos at, boolean alongX, int row, int across, int level, int outward, BeardRoads.Palette linings) {
        int top = Integer.MIN_VALUE;
        for (int out = 0; out <= WET_REACH; out++) {
            int side = across + outward * out;
            for (int y = level; y <= level + CLEAR + 1; y++) {
                at.setPos(alongX ? row : side, y, alongX ? side : row);
                if (world.getBlockState(at).getMaterial().isLiquid()) { top = Math.max(top, y); }
            }
        }
        if (top == Integer.MIN_VALUE) { return 0; }
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        int laid = 0;
        for (int y = level; y <= top; y++) {
            if (BeardKeep.holds(x, y, z)) { continue; }
            at.setPos(x, y, z);
            world.setBlockState(at, linings.pick(world, x, y, z), 2);
            laid++;
        }
        return laid;
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

    private static boolean direction(Random rand, boolean sub) {
        String named = ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayDirection" : "villageRailDirection", sub ? Config.worldgen.villageSubwayDirection : Config.worldgen.villageRailDirection).trim().toLowerCase(Locale.ROOT);
        if (named.isEmpty() || "any".equals(named)) { return rand.nextBoolean(); }
        if (named.startsWith("e") || named.startsWith("w")) { return true; }
        if (named.startsWith("n") || named.startsWith("s")) { return false; }
        ContentLog.LOGGER.error("villageRailDirection '{}' is not ew, ns or any, so the lines run as they roll", named);
        return rand.nextBoolean();
    }

    public static void found(World world, StructureStart start, StructureVillagePieces.Start well, Random rand) {
        if (!ContentBeard.wanted() || BeardSurface.unreadable(world)) { return; }
        found(start, well, rand, false);
        found(start, well, rand, true);
    }

    private static void found(StructureStart start, StructureVillagePieces.Start well, Random rand, boolean sub) {
        int lines = lines(sub);
        if (lines <= 0) { return; }
        List<StructureComponent> components = start.getComponents();
        boolean alongX = direction(rand, sub);
        StructureBoundingBox wellBox = well.getBoundingBox();
        int wellX = (wellBox.minX + wellBox.maxX) / 2;
        int wellZ = (wellBox.minZ + wellBox.maxZ) / 2;
        int nominal = BeardSite.wellNominal(wellBox);
        int apart = width(sub) + spacing(sub);
        int half = (width(sub) - 1) / 2 + (sub ? BeardStations.reach() : 0);
        int clear = sub ? 0 : ContentBeard.plazaReach() + Math.max(13, ContentVillages.largestPlot()) + BeardRoads.pathFullWidth() + half + 2;
        int reach = CityGrowth.march() + tail(sub) + STRETCH;
        List<Integer> placed = new ArrayList<>();
        for (int line = 0; line < lines; line++) {
            int side = line % 2 == 0 ? 1 : -1;
            int candidate = side * (clear + (line / 2) * apart + rand.nextInt(Math.max(1, spacing(sub) / 2)));
            boolean moved = true;
            while (moved) {
                moved = false;
                for (int other : placed) {
                    if (Math.abs(candidate - other) >= apart) { continue; }
                    candidate += side * apart;
                    moved = true;
                }
            }
            placed.add(candidate);
            int center = (alongX ? wellZ : wellX) + candidate;
            StructureBoundingBox box = alongX
                    ? new StructureBoundingBox(wellX - reach, nominal - BELOW, center - half, wellX + reach, nominal + ABOVE, center + half)
                    : new StructureBoundingBox(center - half, nominal - BELOW, wellZ - reach, center + half, nominal + ABOVE, wellZ + reach);
            components.add(new RailPiece(well, box, alongX, line, sub));
            ContentLog.LOGGER.debug("{} line {} of the village at {}, {} is laid {} at {} {}, {} wide, at least {} block(s) of ground from any other line of this village, before any street of the village", sub ? "Subway" : "Railway", line, wellX, wellZ, alongX ? "east to west" : "north to south", alongX ? "z" : "x", center, width(sub), spacing(sub));
        }
    }

    private static void seekRoad(List<StructureComponent> components, RailPiece rail, boolean alongX, int reach) {
        StructureBoundingBox box = rail.getBoundingBox();
        int mine = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
        int full = BeardRoads.pathFullWidth();
        int best = Integer.MAX_VALUE;
        int wanted = mine;
        for (StructureComponent other : components) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            boolean roadAlongX = road.maxX - road.minX >= road.maxZ - road.minZ;
            if (roadAlongX != alongX) { continue; }
            if (((roadAlongX ? road.maxZ - road.minZ : road.maxX - road.minX) + 1) < full) { continue; }
            int center = roadAlongX ? (road.minZ + road.maxZ) / 2 : (road.minX + road.maxX) / 2;
            int off = Math.abs(center - mine);
            if (off > reach || off >= best) { continue; }
            best = off;
            wanted = center;
        }
        if (wanted == mine) { return; }
        int shift = wanted - mine;
        if (alongX) {
            box.minZ += shift;
            box.maxZ += shift;
        }
        else {
            box.minX += shift;
            box.maxX += shift;
        }
        rail.regrade();
        ContentLog.LOGGER.debug("Subway line {} is slid {} block(s) onto the street at {} {}, so it runs under a road and its stations can surface at the road side", rail.line(), shift, alongX ? "z" : "x", wanted);
    }

    public static void fit(World world, StructureStart start) {
        List<StructureComponent> components = start.getComponents();
        if (components.isEmpty()) { return; }
        StructureBoundingBox wellBox = components.get(0).getBoundingBox();
        List<StructureComponent> everyone = ContentBeard.everyone(world, components);
        for (StructureComponent piece : components.toArray(new StructureComponent[0])) {
            if (!(piece instanceof RailPiece)) { continue; }
            RailPiece rail = (RailPiece) piece;
            boolean sub = rail.subway();
            int tail = tail(sub);
            boolean alongX = rail.alongX();
            if (sub) { seekRoad(components, rail, alongX, spacing(true)); }
            int least = Integer.MAX_VALUE;
            int most = Integer.MIN_VALUE;
            for (StructureComponent other : components) {
                if (other instanceof RailPiece) { continue; }
                StructureBoundingBox box = other.getBoundingBox();
                least = Math.min(least, alongX ? box.minX : box.minZ);
                most = Math.max(most, alongX ? box.maxX : box.maxZ);
            }
            if (least > most) { continue; }
            StructureBoundingBox box = rail.getBoundingBox();
            int from = Math.max(rail.rowLeast(), least - tail);
            int to = Math.min(rail.rowMost(), most + tail);
            int wellAt = alongX ? (wellBox.minX + wellBox.maxX) / 2 : (wellBox.minZ + wellBox.maxZ) / 2;
            for (StructureComponent other : everyone) {
                if (components.contains(other)) { continue; }
                StructureBoundingBox met = other.getBoundingBox();
                if (!met.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ)) { continue; }
                int metLeast = alongX ? met.minX : met.minZ;
                int metMost = alongX ? met.maxX : met.maxZ;
                if (metLeast > wellAt) { to = Math.min(to, metLeast - 1 - MARGIN); }
                else if (metMost < wellAt) { from = Math.max(from, metMost + 1 + MARGIN); }
                else { to = from - 1; }
                ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} stops short of {} of another village at {}, {}", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, other.getClass().getSimpleName(), met.minX, met.minZ);
            }
            if (from > to) {
                components.remove(piece);
                ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} has no room left beside the villages around it, so it is not laid", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ);
                continue;
            }
            if (alongX) {
                box.minX = from;
                box.maxX = to;
            }
            else {
                box.minZ = from;
                box.maxZ = to;
            }
            rail.regrade();
            if (sub) {
                List<StructureComponent> held = ContentBeard.laid();
                ContentBeard.laying(components);
                try { rail.rising(world); }
                finally { ContentBeard.laying(held); }
                for (StructureComponent plot : components.toArray(new StructureComponent[0])) {
                    if (!(plot instanceof mctmods.resourcedatapackloader.content.village.ContentVillagePiece)) { continue; }
                    StructureBoundingBox met = plot.getBoundingBox();
                    if (!met.intersectsWith(box.minX, box.minZ, box.maxX, box.maxZ) || buriedUnder(rail, met)) { continue; }
                    components.remove(plot);
                    ContentLog.LOGGER.debug("A plot at {}, {} makes way for subway line {} climbing out of the ground", met.minX, met.minZ, rail.line());
                }
            }
            ContentLog.LOGGER.debug("Railway line {} of the village at {}, {} is fitted to rows {} to {} now the village is grown, {} beyond its last piece either way", rail.line(), start.getBoundingBox().minX, start.getBoundingBox().minZ, from, to, tail);
            if (sub) {
                int wellAlong = alongX ? (wellBox.minX + wellBox.maxX) / 2 : (wellBox.minZ + wellBox.maxZ) / 2;
                int acrossMid = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
                List<StructureComponent> held = ContentBeard.laid();
                ContentBeard.laying(components);
                try { BeardStations.claim(start, rail, alongX, acrossMid, wellAlong); }
                finally { ContentBeard.laying(held); }
            }
        }
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
        List<RailPiece> found = new ArrayList<>();
        if (!ContentBeard.wanted()) { return found; }
        for (StructureComponent piece : ContentBeard.everyone(world, ContentBeard.components())) {
            if (!buried(piece)) { continue; }
            StructureBoundingBox box = piece.getBoundingBox();
            if (box.maxX < near.minX || box.minX > near.maxX || box.maxZ < near.minZ || box.minZ > near.maxZ) { continue; }
            found.add((RailPiece) piece);
        }
        return found;
    }

    public static boolean insideBore(World world, List<RailPiece> subways, int x, int y, int z) {
        int reach = (width(true) - 1) / 2 + 1;
        for (RailPiece rail : subways) {
            StructureBoundingBox box = rail.getBoundingBox();
            if (x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { continue; }
            boolean alongX = rail.alongX();
            int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
            if (Math.abs((alongX ? z : x) - center) > reach) { continue; }
            BeardRoads.Grade grade = rail.grade(world);
            if (grade == null) { continue; }
            int level = grade.at(alongX ? x : z);
            if (level == Integer.MIN_VALUE) { continue; }
            if (y >= level - 1 && y <= level + CLEAR + 1) { return true; }
        }
        return false;
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

    public static boolean fits(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        int rows = (alongX ? box.maxX - box.minX : box.maxZ - box.minZ) + 1;
        List<StructureComponent> held = ContentBeard.laid();
        ContentBeard.laying(pieces);
        int kept;
        try { kept = BeardRoads.roadReach(box, facing); }
        finally { ContentBeard.laying(held); }
        return kept >= rows && ContentBeard.roomFor(pieces, box, facing) >= rows;
    }

    public static boolean settle(@Nullable List<StructureComponent> pieces, StructureBoundingBox box, EnumFacing facing) {
        int[] lengths = lengths(pieces, box, facing);
        if (lengths == null) { return true; }
        boolean alongX = facing.getAxis() == EnumFacing.Axis.X;
        for (int rows : lengths) {
            if (rows < 7) { continue; }
            StructureBoundingBox tried = new StructureBoundingBox(box);
            BeardLayout.trim(tried, alongX, facing, rows);
            if (!fits(pieces, tried, facing)) { continue; }
            BeardLayout.trim(box, alongX, facing, rows);
            ContentLog.LOGGER.debug("A road attempt facing {} is set to {} block(s) {} the railway line in its way", facing, rows, rows == lengths[0] ? "to cross" : "to stop short of");
            return true;
        }
        ContentLog.LOGGER.debug("A road attempt facing {} can neither cross the railway line in its way nor stop short of it, so it is refused", facing);
        return false;
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

    @Nullable public static BeardRoads.Grade profile(World world, RailPiece rail) {
        boolean sub = rail.subway();
        boolean alongX = rail.alongX();
        int rowLeast = rail.rowLeast();
        int acrossLeast = rail.acrossLeast();
        int acrossMost = rail.acrossMost();
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces != null) {
            boolean grew = true;
            while (grew) {
                grew = false;
                for (StructureComponent other : pieces) {
                    if (other == rail || !(other instanceof RailPiece) || ((RailPiece) other).alongX() != alongX) { continue; }
                    RailPiece met = (RailPiece) other;
                    if (met.acrossLeast() > acrossMost + TOGETHER || met.acrossMost() < acrossLeast - TOGETHER) { continue; }
                    if (met.acrossLeast() < acrossLeast) {
                        acrossLeast = met.acrossLeast();
                        grew = true;
                    }
                    if (met.acrossMost() > acrossMost) {
                        acrossMost = met.acrossMost();
                        grew = true;
                    }
                }
            }
            if (acrossLeast != rail.acrossLeast() || acrossMost != rail.acrossMost()) {
                ContentLog.LOGGER.debug("Railway line {} at {}, {} grades with the {} block(s) of formation it stands in, {} to {} across, so every line of it lies at one level", rail.line(), rail.getBoundingBox().minX, rail.getBoundingBox().minZ, acrossMost - acrossLeast + 1, acrossLeast, acrossMost);
            }
        }
        int[] ground = BeardGrade.noiseProfile(world, alongX, rowLeast, rail.rowMost(), acrossLeast, acrossMost);
        if (ground == null) { return null; }
        int rows = ground.length;
        int sea = world.getSeaLevel();
        double[] base = new double[rows];
        int i = 0;
        while (i < rows) {
            if (ground[i] != Integer.MIN_VALUE) {
                base[i] = ground[i];
                i++;
                continue;
            }
            int end = i;
            while (end + 1 < rows && ground[end + 1] == Integer.MIN_VALUE) { end++; }
            int before = i > 0 ? ground[i - 1] : Integer.MIN_VALUE;
            int after = end + 1 < rows ? ground[end + 1] : Integer.MIN_VALUE;
            for (int at = i; at <= end; at++) {
                double level;
                if (before == Integer.MIN_VALUE && after == Integer.MIN_VALUE) { level = sea; }
                else if (before == Integer.MIN_VALUE) { level = after; }
                else if (after == Integer.MIN_VALUE) { level = before; }
                else { level = before + (after - before) * (at - i + 1) / (double) (end - i + 2); }
                base[at] = Math.max(sea, level);
            }
            i = end + 1;
        }
        int[] rising = sub ? rail.rising(world) : null;
        if (sub) {
            double down = subwayDepth();
            double floorLeast = GenHeights.floor(world, 6);
            for (int at = 0; at < rows; at++) {
                if (surfaced(rising, rail.rowLeast() + at)) { continue; }
                base[at] = Math.max(floorLeast, base[at] - down);
            }
        }
        int climb = climb(sub);
        int window = Math.max(4, 2 * climb);
        double[] mean = new double[rows];
        for (int at = 0; at < rows; at++) {
            double total = 0.0D;
            int count = 0;
            for (int near = Math.max(0, at - window); near <= Math.min(rows - 1, at + window); near++) {
                total += base[near];
                count++;
            }
            mean[at] = total / count;
        }
        double step = 1.0D / climb;
        double[] lower = mean.clone();
        double[] upper = mean.clone();
        for (int at = 1; at < rows; at++) {
            lower[at] = Math.min(lower[at], lower[at - 1] + step);
            upper[at] = Math.max(upper[at], upper[at - 1] - step);
        }
        for (int at = rows - 2; at >= 0; at--) {
            lower[at] = Math.min(lower[at], lower[at + 1] + step);
            upper[at] = Math.max(upper[at], upper[at + 1] - step);
        }
        int[] profile = new int[rows];
        int level = (int) Math.round((lower[0] + upper[0]) / 2.0D);
        int lastStep = -climb;
        for (int at = 0; at < rows; at++) {
            double want = (lower[at] + upper[at]) / 2.0D;
            if (want >= level + 1 && at - lastStep >= climb) {
                level++;
                lastStep = at;
            }
            else if (want <= level - 1 && at - lastStep >= climb) {
                level--;
                lastStep = at;
            }
            profile[at] = level;
        }
        boolean[] fixed = new boolean[rows];
        int crossings = level(rail, rowLeast, profile, fixed);
        boolean[] settled = fixed;
        if (sub && rising != null) {
            settled = fixed.clone();
            int ramp = subwayDepth() * climb;
            for (int at = 0; at < rows; at++) {
                int row = rowLeast + at;
                if (rising[1] > 0 ? row >= rising[0] - ramp : row <= rising[0] + ramp) { settled[at] = true; }
            }
        }
        if (crossings > 0) {
            rein(profile, fixed);
            int since = -climb;
            for (int at = 1; at < rows; at++) {
                if (profile[at] == profile[at - 1]) { continue; }
                if (at - since >= climb) {
                    since = at;
                    continue;
                }
                int back = at;
                while (back < rows && !settled[back] && profile[back] != profile[at - 1]) {
                    profile[back] = profile[at - 1];
                    back++;
                }
                if (back == at) { since = at; }
            }
            rein(profile, fixed);
        }
        int held = holdStation(rail, profile, rowLeast, rows);
        if (held > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line {} holds {} station row(s) at one level, so its stored grade says what is laid there", rail.line(), held); }
        boolean[] bridged = new boolean[rows];
        for (int at = 0; at < rows; at++) { bridged[at] = !sub && (ground[at] == Integer.MIN_VALUE || profile[at] > ground[at] + FILL); }
        int leveled = BeardGrade.levelDecks(profile, bridged, fixed, climb);
        if (leveled > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Leveled {} row(s) of railway line {} so each of its trestles lies at one height end to end", leveled, rail.line()); }
        if (ContentLog.LOGGER.debugEnabled()) {
            int trestle = 0;
            int cut = 0;
            for (int at = 0; at < rows; at++) {
                if (bridged[at]) { trestle++; }
                else if (ground[at] > profile[at]) { cut++; }
            }
            ContentLog.LOGGER.debug("Railway line {} at {}, {} grades {} row(s) from y {} to y {}, climbing a block every {} row(s) at most, {} on trestle and {} in cut", rail.line(), rail.getBoundingBox().minX, rail.getBoundingBox().minZ, rows, profile[0], profile[rows - 1], climb, trestle, cut);
        }
        return new BeardRoads.Grade(profile, ground, bridged, new boolean[rows], rowLeast, 0);
    }

    private static void rein(int[] profile, boolean[] fixed) {
        for (int at = 1; at < profile.length; at++) {
            if (fixed[at]) { continue; }
            profile[at] = Math.max(profile[at - 1] - 1, Math.min(profile[at - 1] + 1, profile[at]));
        }
        for (int at = profile.length - 2; at >= 0; at--) {
            if (fixed[at]) { continue; }
            profile[at] = Math.max(profile[at + 1] - 1, Math.min(profile[at + 1] + 1, profile[at]));
        }
    }

    private static int level(RailPiece rail, int rowLeast, int[] profile, boolean[] fixed) {
        List<StructureComponent> pieces = ContentBeard.components();
        if (pieces == null) { return 0; }
        boolean alongX = rail.alongX();
        int rows = profile.length;
        int crossings = 0;
        for (StructureComponent other : pieces) {
            if (!(other instanceof StructureVillagePieces.Path) || BeardPlots.roadAlongX(other) == alongX) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (!road.intersectsWith(rail.getBoundingBox().minX, rail.getBoundingBox().minZ, rail.getBoundingBox().maxX, rail.getBoundingBox().maxZ)) { continue; }
            int from = Math.max(0, (alongX ? road.minX : road.minZ) - 1 - rowLeast);
            int to = Math.min(rows - 1, (alongX ? road.maxX : road.maxZ) + 1 - rowLeast);
            if (from > to) { continue; }
            int center = Math.max(0, Math.min(rows - 1, (alongX ? (road.minX + road.maxX) / 2 : (road.minZ + road.maxZ) / 2) - rowLeast));
            int level = profile[center];
            for (int at = from; at <= to; at++) {
                profile[at] = level;
                fixed[at] = true;
            }
            crossings++;
        }
        return crossings;
    }

    public static void lay(RailPiece rail, World world, StructureBoundingBox clip) {
        BeardBiome.enter(world, (clip.minX + clip.maxX) / 2, (clip.minZ + clip.maxZ) / 2);
        try { laid(rail, world, clip); }
        finally { BeardBiome.leave(); }
    }

    private static void laid(RailPiece rail, World world, StructureBoundingBox clip) {
        boolean sub = rail.subway();
        if (!ContentBeard.wanted()) { return; }
        StructureBoundingBox box = rail.getBoundingBox();
        boolean alongX = rail.alongX();
        int least = Math.max(rail.rowLeast(), alongX ? clip.minX : clip.minZ);
        int most = Math.min(rail.rowMost(), alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return; }
        BeardRoads.Grade grade = rail.grade(world);
        if (grade == null) { return; }
        int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
        int boreHalf = (width(sub) - 1) / 2;
        int bedHalf = bedHalf(sub);
        int acrossLeast = center - boreHalf;
        int acrossMost = center + boreHalf;
        int tracks = tracks(sub);
        int gap = trackGap(sub);
        int shoulder = shoulderWidth(sub);
        IBlockState bed = BeardRoads.pathBlock(sub ? "villageSubwayBedBlock" : "villageRailBedBlock", sub ? Config.worldgen.villageSubwayBedBlock : Config.worldgen.villageRailBedBlock, Blocks.GRAVEL.getDefaultState());
        IBlockState tie = BeardRoads.pathBlock(sub ? "villageSubwayTieBlock" : "villageRailTieBlock", sub ? Config.worldgen.villageSubwayTieBlock : Config.worldgen.villageRailTieBlock, Blocks.PLANKS.getDefaultState());
        IBlockState track = oriented(BeardRoads.pathBlock(sub ? "villageSubwayBlock" : "villageRailBlock", sub ? Config.worldgen.villageSubwayBlock : Config.worldgen.villageRailBlock, Blocks.RAIL.getDefaultState()), alongX);
        IBlockState powered = powered(oriented(BeardRoads.pathBlock(sub ? "villageSubwayPowerBlock" : "villageRailPowerBlock", sub ? Config.worldgen.villageSubwayPowerBlock : Config.worldgen.villageRailPowerBlock, Blocks.GOLDEN_RAIL.getDefaultState()), alongX));
        IBlockState powerBase = BeardRoads.pathBlock(sub ? "villageSubwayPowerBase" : "villageRailPowerBase", sub ? Config.worldgen.villageSubwayPowerBase : Config.worldgen.villageRailPowerBase, Blocks.REDSTONE_BLOCK.getDefaultState());
        IBlockState shoulderBlock = BeardRoads.pathBlock(sub ? "villageSubwayShoulderBlock" : "villageRailShoulderBlock", sub ? Config.worldgen.villageSubwayShoulderBlock : Config.worldgen.villageRailShoulderBlock, Blocks.AIR.getDefaultState());
        IBlockState light = BeardRoads.pathBlock(sub ? "villageSubwayTunnelLightBlock" : "villageRailTunnelLightBlock", sub ? Config.worldgen.villageSubwayTunnelLightBlock : Config.worldgen.villageRailTunnelLightBlock, Blocks.AIR.getDefaultState());
        IBlockState support = BeardRoads.pathBlock("villageRailSupportBlock", Config.worldgen.villageRailSupportBlock, Blocks.LOG.getDefaultState());
        IBlockState deck = BeardRoads.pathBlock("villageRailDeckBlock", Config.worldgen.villageRailDeckBlock, Blocks.PLANKS.getDefaultState());
        IBlockState barrier = BeardRoads.pathBlock("villageRailBarrierBlock", Config.worldgen.villageRailBarrierBlock, Blocks.AIR.getDefaultState());
        BeardRoads.Palette linings = BeardRoads.pathPalette(sub ? "villageSubwayTunnelBlock" : "villageRailTunnelBlock", sub ? Config.worldgen.villageSubwayTunnelBlock : Config.worldgen.villageRailTunnelBlock, Blocks.AIR.getDefaultState());
        int tieRun = tieRun(sub);
        boolean inBed = trackInBed(track, sub);
        int powerRun = powered.getBlock() == Blocks.AIR || inBed ? 0 : powerRun(sub);
        int lightRun = tunnelLightRun(sub);
        int depth = tunnelDepth(sub);
        List<StructureComponent> roads = new ArrayList<>();
        for (StructureComponent other : ContentBeard.everyone(world, ContentBeard.components())) {
            if (!(other instanceof StructureVillagePieces.Path)) { continue; }
            StructureBoundingBox road = other.getBoundingBox();
            if (road.intersectsWith(box.minX - 1, box.minZ - 1, box.maxX + 1, box.maxZ + 1)) { roads.add(other); }
        }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        StructureStart holder = ContentBeard.current();
        Predicate<BlockPos> within = holder != null ? BeardPlots.outside(world, holder, rail, box, false, 0) : spot -> world.isChunkGeneratedAt(spot.getX() >> 4, spot.getZ() >> 4);
        List<BlockPos> seeds = new ArrayList<>();
        boolean[] frames = bridgeFrames(grade);
        int halls = 0;
        int stepped = 0;
        int heads = 0;
        List<StructureComponent> mine = ContentBeard.components();
        int wellRow = mine == null || mine.isEmpty() ? rail.rowLeast()
                : alongX ? (mine.get(0).getBoundingBox().minX + mine.get(0).getBoundingBox().maxX) / 2
                         : (mine.get(0).getBoundingBox().minZ + mine.get(0).getBoundingBox().maxZ) / 2;
        List<StructureBoundingBox> stairs = sub ? rail.stations() : java.util.Collections.emptyList();
        int laid = 0;
        int trestled = 0;
        int lined = 0;
        int crossed = 0;
        int framed = 0;
        int[] rising = rail.rising(world);
        if (rising != null && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line {} climbs out over the {} row(s) {} of row {}, {} of ramp to rise {} block(s) and the rest of it open track", rail.line(), rising[1] > 0 ? rail.rowMost() - rising[0] : rising[0] - rail.rowLeast(), rising[1] > 0 ? "beyond" : "short", rising[0], subwayDepth() * climb(true), subwayDepth()); }
        for (int row = least; row <= most; row++) {
            if (BeardBiome.moved(world, alongX ? row : center, alongX ? center : row)) {
                bed = BeardRoads.pathBlock(sub ? "villageSubwayBedBlock" : "villageRailBedBlock", sub ? Config.worldgen.villageSubwayBedBlock : Config.worldgen.villageRailBedBlock, Blocks.GRAVEL.getDefaultState());
                tie = BeardRoads.pathBlock(sub ? "villageSubwayTieBlock" : "villageRailTieBlock", sub ? Config.worldgen.villageSubwayTieBlock : Config.worldgen.villageRailTieBlock, Blocks.PLANKS.getDefaultState());
                track = oriented(BeardRoads.pathBlock(sub ? "villageSubwayBlock" : "villageRailBlock", sub ? Config.worldgen.villageSubwayBlock : Config.worldgen.villageRailBlock, Blocks.RAIL.getDefaultState()), alongX);
                powered = powered(oriented(BeardRoads.pathBlock(sub ? "villageSubwayPowerBlock" : "villageRailPowerBlock", sub ? Config.worldgen.villageSubwayPowerBlock : Config.worldgen.villageRailPowerBlock, Blocks.GOLDEN_RAIL.getDefaultState()), alongX));
                powerBase = BeardRoads.pathBlock(sub ? "villageSubwayPowerBase" : "villageRailPowerBase", sub ? Config.worldgen.villageSubwayPowerBase : Config.worldgen.villageRailPowerBase, Blocks.REDSTONE_BLOCK.getDefaultState());
                shoulderBlock = BeardRoads.pathBlock(sub ? "villageSubwayShoulderBlock" : "villageRailShoulderBlock", sub ? Config.worldgen.villageSubwayShoulderBlock : Config.worldgen.villageRailShoulderBlock, Blocks.AIR.getDefaultState());
                light = BeardRoads.pathBlock(sub ? "villageSubwayTunnelLightBlock" : "villageRailTunnelLightBlock", sub ? Config.worldgen.villageSubwayTunnelLightBlock : Config.worldgen.villageRailTunnelLightBlock, Blocks.AIR.getDefaultState());
                support = BeardRoads.pathBlock("villageRailSupportBlock", Config.worldgen.villageRailSupportBlock, Blocks.LOG.getDefaultState());
                deck = BeardRoads.pathBlock("villageRailDeckBlock", Config.worldgen.villageRailDeckBlock, Blocks.PLANKS.getDefaultState());
                barrier = BeardRoads.pathBlock("villageRailBarrierBlock", Config.worldgen.villageRailBarrierBlock, Blocks.AIR.getDefaultState());
                linings = BeardRoads.pathPalette(sub ? "villageSubwayTunnelBlock" : "villageRailTunnelBlock", sub ? Config.worldgen.villageSubwayTunnelBlock : Config.worldgen.villageRailTunnelBlock, Blocks.AIR.getDefaultState());
            }
            int level = grade.at(row);
            if (level == Integer.MIN_VALUE) { continue; }
            int flat = sub ? BeardStations.heldLevel(rail, grade, row) : Integer.MIN_VALUE;
            if (flat != Integer.MIN_VALUE) { level = flat; }
            boolean trestle = grade.bridgedAt(row);
            boolean broken = surfaced(rising, row) && grade.groundAt(row) != Integer.MIN_VALUE && level >= grade.groundAt(row) - 1;
            boolean tunnel = (sub && !broken) || (!trestle && grade.tunneledAt(row, depth) && uncrossedAt(roads, alongX, row, center, level));
            boolean tieRow = Math.floorMod(row, tieRun) == 0;
            boolean powerRow = powerRun > 0 && Math.floorMod(row, powerRun) == 0;
            boolean litRow = light.getBlock() != Blocks.AIR && Math.floorMod(row, lightRun) == 0;
            int mark = row - grade.start();
            boolean frameRow = trestle && mark >= 0 && mark < frames.length && frames[mark] && uncrossedAt(roads, alongX, row, center, level);
            boolean legRow = Math.floorMod(row, LEG) == 0 || frameRow;
            for (int across = acrossLeast - 1; across <= acrossMost + 1; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                at.setPos(x, level, z);
                if (!clip.isVecInside(at)) { continue; }
                boolean verge = across < acrossLeast || across > acrossMost;
                boolean onRail = onTrack(across, center, tracks, gap);
                boolean edge = across == acrossLeast || across == acrossMost;
                boolean onShoulder = shoulder > 0 && (across < acrossLeast + shoulder || across > acrossMost - shoulder);
                if (verge) {
                    if (trestle) { continue; }
                    if (tunnel) {
                        lined += BeardRoads.tunnelWall(world, rail, at, x, z, level, linings);
                    }
                    else {
                        lined += cutWall(world, at, alongX, row, across, level, across < acrossLeast ? -1 : 1, linings);
                        BeardRoads.vergeFill(world, rail, x, z, level, at);
                    }
                    continue;
                }
                StructureComponent road = tunnel ? null : roadAt(roads, alongX, x, z);
                if (road != null) {
                    if (onRail) {
                        int atRoad = grade.at(alongX ? (road.getBoundingBox().minX + road.getBoundingBox().maxX) / 2 : (road.getBoundingBox().minZ + road.getBoundingBox().maxZ) / 2);
                        if (atRoad == Integer.MIN_VALUE) { atRoad = level; }
                        int seat = inBed ? atRoad : atRoad + 1;
                        BeardKeep.letGo(x, seat, z);
                        set(world, at, x, seat, z, track);
                        crossed++;
                    }
                    continue;
                }
                if (trestle) {
                    clearAbove(world, at, x, z, level + 1, level + CLEAR, within, seeds);
                    laid += set(world, at, x, level, z, onRail && inBed ? track : deck);
                    if (edge && legRow) { trestled += BeardRoads.piling(world, alongX, row, across, level - 1, support, at); }
                    if (onRail && !inBed) { set(world, at, x, level + 1, z, track); }
                    else if (edge && barrier.getBlock() != Blocks.AIR) { set(world, at, x, level + 1, z, barrier); }
                    continue;
                }
                BlockPos top = GroundLevel.inWindow(world, new BlockPos(x, 64, z)).down();
                clearAbove(world, at, x, z, level + 1, tunnel ? level + CLEAR : Math.max(level + CLEAR, top.getY() + 2), within, seeds, tunnel || broken);
                BeardBlocks.fillUnder(world, at, x, z, level - 1, level - FILL_UNDER);
                IBlockState base = onRail && inBed ? track : powerRow && onRail ? powerBase : onShoulder ? shoulderBlock : tieRow ? tie : bed;
                laid += set(world, at, x, level, z, base);
                if (onRail && !inBed) { set(world, at, x, level + 1, z, powerRow ? powered : track); }
                if (tunnel) {
                    lined += BeardRoads.roofCell(world, at, x, z, level + CLEAR + 1, litRow && across == center ? light : linings.pick(world, x, level + CLEAR + 1, z));
                }
            }
            if (frameRow) { framed += bridgeFrame(world, at, clip, alongX, row, acrossLeast, acrossMost, level); }
            if (sub && tunnel && BeardStations.stationAt(rail, row, wellRow)) {
                halls += BeardStations.open(world, clip, alongX, row, center, level, bedHalf, linings, light, lightRun, at);
            }
            else if (sub && tunnel && (BeardStations.stationAt(rail, row - 1, wellRow) || BeardStations.stationAt(rail, row + 1, wellRow))) {
                int beside = BeardStations.heldLevel(rail, grade, BeardStations.stationAt(rail, row - 1, wellRow) ? row - 1 : row + 1);
                if (beside != Integer.MIN_VALUE) { halls += BeardStations.cap(world, clip, alongX, row, center, beside, level, bedHalf, linings, at); }
            }
        }
        for (int which = 0; which < stairs.size(); which++) {
            StructureBoundingBox stair = stairs.get(which);
            int stairRow = (alongX ? stair.minX : stair.minZ) + 1;
            int stairLevel = BeardStations.heldLevel(rail, grade, BeardStations.heartOf(rail, stair));
            if (stairLevel == Integer.MIN_VALUE) { stairLevel = grade.at(stairRow); }
            if (stairLevel == Integer.MIN_VALUE) { continue; }
            halls += BeardStations.platformBench(world, clip, alongX, alongX ? stair.minX : stair.minZ, center, stairLevel, bedHalf, (alongX ? stair.minZ : stair.minX) + 1 > center ? 1 : -1, at);
            int near = (alongX ? stair.minZ : stair.minX) + 1;
            stepped += BeardStations.stairs(world, clip, rail, which, alongX, stairRow, near, near > center ? 1 : -1, center, bedHalf, stairLevel, linings, at);
            heads += BeardStations.entrance(world, clip, rail, which, alongX, stairRow, near, stairLevel);
        }
        int felled = seeds.isEmpty() ? 0 : ContentBeard.fellTrees(world, seeds, within, at);
        if (laid + crossed + felled + halls > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Laid railway line {} at {}, {} within its chunk: {} bed column(s), {} support block(s) under trestles, {} tunnel block(s), {} rail(s) across roads, {} overhead frame block(s), {} tree block(s) felled whole where a tree stood over the bed, {} station column(s), {} step(s) to the road side, {} entrance(s)", rail.line(), box.minX, box.minZ, laid, trestled, lined, crossed, framed, felled, halls, stepped, heads); }
    }

    public static int open(RailPiece rail, World world, StructureStart start, StructureBoundingBox clip) {
        StructureBoundingBox box = rail.getBoundingBox();
        boolean alongX = rail.alongX();
        int least = Math.max(rail.rowLeast(), alongX ? clip.minX : clip.minZ);
        int most = Math.min(rail.rowMost(), alongX ? clip.maxX : clip.maxZ);
        if (most < least) { return 0; }
        BeardRoads.Grade grade = rail.grade(world);
        if (grade == null) { return 0; }
        Predicate<BlockPos> within = BeardPlots.outside(world, start, rail, box, false, 0);
        List<BlockPos> seeds = new ArrayList<>();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int row = least; row <= most; row++) {
            int level = grade.at(row);
            if (level == Integer.MIN_VALUE) { continue; }
            for (int across = rail.acrossLeast() - BESIDE; across <= rail.acrossMost() + BESIDE; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                at.setPos(x, level, z);
                if (!clip.isVecInside(at)) { continue; }
                boolean beside = across < rail.acrossLeast() || across > rail.acrossMost();
                for (int y = level - 2; y <= level + CANOPY; y++) {
                    at.setPos(x, y, z);
                    IBlockState above = world.getBlockState(at);
                    if (above.getBlock() == Blocks.AIR || BeardKeep.holds(x, y, z)) { continue; }
                    if (beside) {
                        if (BlastPlasterUtil.isTreeWood(above) && !ContentVillageDecor.plantedAt(world, x, z)) { seeds.add(new BlockPos(x, y, z)); }
                        continue;
                    }
                    if (y > level) { tree(world, at, x, y, z, above, within, seeds); }
                }
            }
        }
        return seeds.isEmpty() ? 0 : ContentBeard.fellTrees(world, seeds, within, at);
    }

    private static boolean onTrack(int across, int center, int tracks, int gap) {
        if (tracks <= 1) { return across == center; }
        int first = center - (tracks - 1) * gap / 2;
        for (int i = 0; i < tracks; i++) { if (across == first + i * gap) { return true; } }
        return false;
    }

    @Nullable private static StructureComponent roadAt(List<StructureComponent> roads, boolean alongX, int x, int z) {
        for (StructureComponent road : roads) {
            StructureBoundingBox box = road.getBoundingBox();
            if (alongTheLine(box, alongX)) { continue; }
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return road; }
        }
        return null;
    }

    private static boolean uncrossedAt(List<StructureComponent> roads, boolean alongX, int row, int center, int level) {
        for (StructureComponent road : roads) {
            StructureBoundingBox box = road.getBoundingBox();
            if (alongTheLine(box, alongX)) { continue; }
            if (row < (alongX ? box.minX : box.minZ) - 1 || row > (alongX ? box.maxX : box.maxZ) + 1) { continue; }
            if (center < (alongX ? box.minZ : box.minX) || center > (alongX ? box.maxZ : box.maxX)) { continue; }
            BeardRoads.Grade grade = road instanceof IRoadLayout ? ((IRoadLayout) road).rdpl$layout() : null;
            if (grade == null) { return false; }
            int at = grade.at(center);
            return at != Integer.MIN_VALUE && at >= level + OVER;
        }
        return true;
    }

    private static boolean[] bridgeFrames(BeardRoads.Grade grade) {
        int rows = grade.rows();
        boolean[] frames = new boolean[rows];
        if (frameBlock().getBlock() == Blocks.AIR) { return frames; }
        for (int i = 0; i < rows; i++) { frames[i] = decking(grade, i); }
        return BeardRoads.frameRows(frames, frameLeast(), frameRun());
    }

    private static boolean decking(BeardRoads.Grade grade, int i) {
        int row = grade.start() + i;
        return grade.bridgedAt(row) && grade.at(row) != Integer.MIN_VALUE;
    }

    private static int bridgeFrame(World world, BlockPos.MutableBlockPos at, StructureBoundingBox clip, boolean alongX, int row, int acrossLeast, int acrossMost, int level) {
        IBlockState post = frameBlock();
        IBlockState beam = BeardRoads.pathBlock("villageRailBridgeFrameTopBlock", Config.worldgen.villageRailBridgeFrameTopBlock, post);
        int height = frameHeight();
        int laid = 0;
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            if (!clip.isVecInside(at.setPos(x, level, z))) { continue; }
            if (across == acrossLeast || across == acrossMost) {
                for (int y = level + 1; y <= level + height; y++) { laid += set(world, at, x, y, z, post); }
            }
            laid += set(world, at, x, level + height + 1, z, beam);
        }
        return laid;
    }

    private static int set(World world, BlockPos.MutableBlockPos at, int x, int y, int z, IBlockState state) {
        if (BeardKeep.holds(x, y, z)) { return 0; }
        at.setPos(x, y, z);
        if (world.getBlockState(at) == state) { return 0; }
        world.setBlockState(at, state, 2);
        return 1;
    }

    private static void clearAbove(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int to, Predicate<BlockPos> within, List<BlockPos> seeds) {
        clearAbove(world, at, x, z, from, to, within, seeds, false);
    }

    private static void clearAbove(World world, BlockPos.MutableBlockPos at, int x, int z, int from, int to, Predicate<BlockPos> within, List<BlockPos> seeds, boolean bored) {
        int y = from;
        for (; y <= to; y++) {
            at.setPos(x, y, z);
            IBlockState above = world.getBlockState(at);
            Block up = above.getBlock();
            if (up == Blocks.AIR || BeardKeep.holds(x, y, z) || railBlock(above)) { continue; }
            if (above.getMaterial().isLiquid()) {
                if (!bored) { break; }
                world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                continue;
            }
            if (tree(world, at, x, y, z, above, within, seeds)) { continue; }
            if (BeardBlocks.terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || !above.getMaterial().isSolid()) {
                BeardBlocks.note(world, at, "Laying a railway");
                world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                continue;
            }
            break;
        }
        for (; y <= from + CANOPY; y++) {
            at.setPos(x, y, z);
            IBlockState above = world.getBlockState(at);
            if (above.getBlock() == Blocks.AIR || BeardKeep.holds(x, y, z)) { continue; }
            tree(world, at, x, y, z, above, within, seeds);
        }
    }

    private static boolean tree(World world, BlockPos.MutableBlockPos at, int x, int y, int z, IBlockState held, Predicate<BlockPos> within, List<BlockPos> seeds) {
        if (BlastPlasterUtil.isTreeWood(held)) {
            if (!ContentVillageDecor.plantedAt(world, x, z)) { seeds.add(new BlockPos(x, y, z)); }
            return true;
        }
        if (held.getMaterial() != Material.LEAVES) { return false; }
        BlockPos trunk = BeardGround.sustainer(world, new BlockPos(x, y, z), within);
        if (trunk == null) {
            BeardBlocks.note(world, at, "Laying a railway");
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        }
        else if (!ContentVillageDecor.plantedAt(world, trunk.getX(), trunk.getZ())) { seeds.add(trunk); }
        return true;
    }

    @SuppressWarnings("unchecked") private static IBlockState oriented(IBlockState state, boolean alongX) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property.getValueClass() != BlockRailBase.EnumRailDirection.class) { continue; }
            IProperty<BlockRailBase.EnumRailDirection> shape = (IProperty<BlockRailBase.EnumRailDirection>) property;
            BlockRailBase.EnumRailDirection wanted = alongX ? BlockRailBase.EnumRailDirection.EAST_WEST : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            if (shape.getAllowedValues().contains(wanted)) { return state.withProperty(shape, wanted); }
        }
        return ContentBeard.axised(state, alongX);
    }

    private static IBlockState powered(IBlockState state) {
        if (state.getPropertyKeys().contains(BlockRailPowered.POWERED)) { return state.withProperty(BlockRailPowered.POWERED, true); }
        return state;
    }
}
