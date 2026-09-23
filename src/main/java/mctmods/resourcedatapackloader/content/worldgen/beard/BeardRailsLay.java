package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.blastplaster.util.BlastPlasterUtil;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.village.ContentVillageDecor;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeardTrees;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IRoadLayout;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class BeardRailsLay {
    private static final int WET_REACH = 3;
    private static final int CANOPY = 14;
    private static final int BESIDE = 2;
    private static final int LEG = 4;

    private BeardRailsLay() {}

    private static int cutWall(World world, BlockPos.MutableBlockPos at, boolean alongX, int row, int across, int level, int outward, BeardRoads.Palette linings) {
        int top = Integer.MIN_VALUE;
        for (int out = 0; out <= WET_REACH; out++) {
            int side = across + outward * out;
            for (int y = level; y <= level + BeardRails.CLEAR + 1; y++) {
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

    public static void lay(RailPiece rail, World world, StructureBoundingBox clip) {
        BeardBiome.enter(world, (clip.minX + clip.maxX) / 2, (clip.minZ + clip.maxZ) / 2);
        try { laid(rail, world, clip); }
        finally { BeardBiome.leave(); }
    }

    private static final class Dress {
        final IBlockState bed;
        final IBlockState tie;
        final IBlockState track;
        final IBlockState powered;
        final IBlockState powerBase;
        final IBlockState shoulderBlock;
        final IBlockState light;
        final BeardRoads.Palette supports;
        final BeardRoads.Palette decks;
        final BeardRoads.Palette barriers;
        final BeardRoads.Palette linings;

        Dress(boolean sub, boolean alongX) {
            bed = BeardRoads.pathBlock(sub ? "villageSubwayBedBlock" : "villageRailBedBlock", sub ? Config.worldgen.villageSubwayBedBlock : Config.worldgen.villageRailBedBlock, Blocks.GRAVEL.getDefaultState());
            tie = BeardRoads.pathBlock(sub ? "villageSubwayTieBlock" : "villageRailTieBlock", sub ? Config.worldgen.villageSubwayTieBlock : Config.worldgen.villageRailTieBlock, Blocks.PLANKS.getDefaultState());
            track = BeardRails.oriented(BeardRoads.pathBlock(sub ? "villageSubwayBlock" : "villageRailBlock", sub ? Config.worldgen.villageSubwayBlock : Config.worldgen.villageRailBlock, Blocks.RAIL.getDefaultState()), alongX);
            powered = powered(BeardRails.oriented(BeardRoads.pathBlock(sub ? "villageSubwayPowerBlock" : "villageRailPowerBlock", sub ? Config.worldgen.villageSubwayPowerBlock : Config.worldgen.villageRailPowerBlock, Blocks.GOLDEN_RAIL.getDefaultState()), alongX));
            powerBase = BeardRoads.pathBlock(sub ? "villageSubwayPowerBase" : "villageRailPowerBase", sub ? Config.worldgen.villageSubwayPowerBase : Config.worldgen.villageRailPowerBase, Blocks.REDSTONE_BLOCK.getDefaultState());
            shoulderBlock = BeardRoads.pathBlock(sub ? "villageSubwayShoulderBlock" : "villageRailShoulderBlock", sub ? Config.worldgen.villageSubwayShoulderBlock : Config.worldgen.villageRailShoulderBlock, Blocks.AIR.getDefaultState());
            light = BeardRoads.pathBlock(sub ? "villageSubwayTunnelLightBlock" : "villageRailTunnelLightBlock", sub ? Config.worldgen.villageSubwayTunnelLightBlock : Config.worldgen.villageRailTunnelLightBlock, Blocks.AIR.getDefaultState());
            supports = BeardRoads.pathPalette("villageRailSupportBlock", Config.worldgen.villageRailSupportBlock, Blocks.LOG.getDefaultState());
            decks = BeardRoads.pathPalette("villageRailDeckBlock", Config.worldgen.villageRailDeckBlock, Blocks.PLANKS.getDefaultState());
            barriers = BeardRoads.pathPalette("villageRailBarrierBlock", Config.worldgen.villageRailBarrierBlock, Blocks.AIR.getDefaultState());
            linings = BeardRoads.pathPalette(sub ? "villageSubwayTunnelBlock" : "villageRailTunnelBlock", sub ? Config.worldgen.villageSubwayTunnelBlock : Config.worldgen.villageRailTunnelBlock, Blocks.AIR.getDefaultState());
        }
    }

    private static final class Line {
        final RailPiece rail;
        final World world;
        final StructureBoundingBox clip;
        final BlockPos.MutableBlockPos at;
        final BeardRoads.Grade grade;
        final List<StructureComponent> roads;
        final Predicate<BlockPos> within;
        final List<BlockPos> seeds;
        final boolean alongX;
        final boolean inBed;
        final int center;
        final int acrossLeast;
        final int acrossMost;
        final int tracks;
        final int gap;
        final int shoulder;
        int laid;
        int trestled;
        int lined;
        int crossed;

        Line(RailPiece rail, World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, BeardRoads.Grade grade, List<StructureComponent> roads, Predicate<BlockPos> within, List<BlockPos> seeds, boolean inBed, int center, int acrossLeast, int acrossMost, int tracks, int gap, int shoulder) {
            this.rail = rail;
            this.world = world;
            this.clip = clip;
            this.at = at;
            this.grade = grade;
            this.roads = roads;
            this.within = within;
            this.seeds = seeds;
            this.alongX = rail.alongX();
            this.inBed = inBed;
            this.center = center;
            this.acrossLeast = acrossLeast;
            this.acrossMost = acrossMost;
            this.tracks = tracks;
            this.gap = gap;
            this.shoulder = shoulder;
        }
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
        if (grade == null || BeardLinks.partnerless(world, rail)) { return; }
        int center = (rail.acrossLeast() + rail.acrossMost()) / 2;
        int boreHalf = (BeardRails.width(sub) - 1) / 2;
        int bedHalf = BeardRails.bedHalf(sub);
        int acrossLeast = center - boreHalf;
        int acrossMost = center + boreHalf;
        int tracks = BeardRails.tracks(sub);
        int gap = BeardRails.trackGap(sub);
        int shoulder = BeardRails.shoulderWidth(sub);
        Dress dress = new Dress(sub, alongX);
        int tieRun = BeardRails.tieRun(sub);
        boolean inBed = BeardRails.trackInBed(dress.track, sub);
        int powerRun = dress.powered.getBlock() == Blocks.AIR || inBed ? 0 : BeardRails.powerRun(sub);
        int lightRun = BeardRails.tunnelLightRun(sub);
        int depth = BeardRails.tunnelDepth(sub);
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
        Line line = new Line(rail, world, clip, at, grade, roads, within, seeds, inBed, center, acrossLeast, acrossMost, tracks, gap, shoulder);
        boolean[] frames = bridgeFrames(grade);
        int halls = 0;
        int stepped = 0;
        List<StructureBoundingBox> stairs = sub ? rail.stations() : java.util.Collections.emptyList();
        int framed = 0;
        int[] rising = rail.rising(world);
        if (rising != null && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Subway line {} climbs out over the {} row(s) {} of row {}, {} of ramp to rise {} block(s) and the rest of it open track", rail.line(), rising[1] > 0 ? rail.rowMost() - rising[0] : rising[0] - rail.rowLeast(), rising[1] > 0 ? "beyond" : "short", rising[0], BeardRails.subwayDepth() * BeardRails.climb(true), BeardRails.subwayDepth()); }
        for (int row = least; row <= most; row++) {
            if (BeardBiome.moved(world, alongX ? row : center, alongX ? center : row)) { dress = new Dress(sub, alongX); }
            int level = grade.at(row);
            if (level == Integer.MIN_VALUE || BeardLinks.unlaid(world, rail, row)) { continue; }
            int flat = sub ? BeardStations.heldLevel(rail, grade, row) : Integer.MIN_VALUE;
            if (flat != Integer.MIN_VALUE) { level = flat; }
            boolean trestle = grade.bridgedAt(row);
            boolean opened = BeardLinks.opens(rail, row);
            boolean broken = (opened && sub) || BeardRails.surfaced(rising, row) && grade.groundAt(row) != Integer.MIN_VALUE && level >= grade.groundAt(row) - 1;
            boolean tunnel = !opened && ((sub && !broken) || (!trestle && grade.tunneledAt(row, depth) && uncrossedAt(roads, alongX, row, center, level)));
            boolean tieRow = Math.floorMod(row, tieRun) == 0;
            boolean joined = BeardLinks.joinRow(rail, row);
            boolean powerRow = powerRun > 0 && !joined && Math.floorMod(row, powerRun) == 0;
            boolean litRow = dress.light.getBlock() != Blocks.AIR && Math.floorMod(row, lightRun) == 0;
            int mark = row - grade.start();
            boolean frameRow = trestle && !joined && mark >= 0 && mark < frames.length && frames[mark] && uncrossedAt(roads, alongX, row, center, level);
            boolean legRow = Math.floorMod(row, LEG) == 0 || frameRow;
            int platform = BeardLinks.platformAt(rail, row);
            int platformY = inBed ? level : level + 1;
            for (int across = acrossLeast - 1 - Math.max(0, platform); across <= acrossMost + 1 + Math.max(0, platform); across++) {
                cell(line, dress, row, across, level, platform, platformY, tunnel, trestle, broken, tieRow, powerRow, litRow, legRow);
            }
            if (frameRow) { framed += bridgeFrame(world, at, clip, alongX, row, acrossLeast, acrossMost, level); }
            if (sub && tunnel && BeardStations.stationAt(rail, row)) {
                halls += BeardStations.open(world, clip, alongX, row, center, level, bedHalf, dress.linings, dress.light, lightRun, at);
            }
            else if (sub && tunnel && (BeardStations.stationAt(rail, row - 1) || BeardStations.stationAt(rail, row + 1))) {
                int beside = BeardStations.heldLevel(rail, grade, BeardStations.stationAt(rail, row - 1) ? row - 1 : row + 1);
                if (beside != Integer.MIN_VALUE) { halls += BeardStations.cap(world, clip, alongX, row, center, beside, level, bedHalf, dress.linings, at); }
            }
        }
        for (int which = 0; which < stairs.size(); which++) {
            StructureBoundingBox stair = stairs.get(which);
            int stairRow = BeardStations.stairRow(rail, stair);
            int stairLevel = BeardStations.stairLevel(rail, grade, stair);
            if (stairLevel == Integer.MIN_VALUE) { continue; }
            halls += BeardStations.platformBench(world, clip, alongX, alongX ? stair.minX : stair.minZ, center, stairLevel, bedHalf, (alongX ? stair.minZ : stair.minX) + 1 > center ? 1 : -1, at);
            int near = BeardStations.stairNear(rail, stair);
            stepped += BeardStations.stairs(world, clip, rail, which, alongX, stairRow, near, near > center ? 1 : -1, center, bedHalf, stairLevel, dress.linings, at);
        }
        if (!inBed) { line.laid += BeardLinks.force(world, clip, rail, grade, dress.track, at); }
        halls += BeardLinks.benches(world, clip, rail, grade, center, boreHalf, inBed);
        int felled = seeds.isEmpty() ? 0 : ContentBeardTrees.fellTrees(world, seeds, within, at);
        if (line.laid + line.crossed + felled + halls > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Laid railway line {} at {}, {} within its chunk: {} bed column(s), {} support block(s) under trestles, {} tunnel block(s), {} rail(s) across roads, {} overhead frame block(s), {} tree block(s) felled whole where a tree stood over the bed, {} station column(s), {} step(s) to the road side", rail.line(), box.minX, box.minZ, line.laid, line.trestled, line.lined, line.crossed, framed, felled, halls, stepped); }
    }

    private static void cell(Line line, Dress dress, int row, int across, int level, int platform, int platformY, boolean tunnel, boolean trestle, boolean broken, boolean tieRow, boolean powerRow, boolean litRow, boolean legRow) {
        World world = line.world;
        RailPiece rail = line.rail;
        BlockPos.MutableBlockPos at = line.at;
        boolean alongX = line.alongX;
        boolean inBed = line.inBed;
        int acrossLeast = line.acrossLeast;
        int acrossMost = line.acrossMost;
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        at.setPos(x, level, z);
        if (!line.clip.isVecInside(at)) { return; }
        int junction = BeardLinks.junction(rail, row, across);
        if (junction == BeardLinks.OPEN) { return; }
        if (junction == BeardLinks.WALL) {
            if (tunnel) { line.lined += BeardRoadsTunnels.tunnelWall(world, rail, at, x, z, level, dress.linings); }
            return;
        }
        boolean verge = across < acrossLeast || across > acrossMost;
        boolean onRail = junction == BeardLinks.SPUR || junction == BeardLinks.CURVE || (junction == BeardLinks.NONE && onTrack(across, line.center, line.tracks, line.gap));
        boolean edge = junction == BeardLinks.NONE && (across == acrossLeast || across == acrossMost) && (platform <= 0 || !BeardLinks.platformSide(rail, row, across < line.center ? -1 : 1));
        boolean onShoulder = line.shoulder > 0 && (across < acrossLeast + line.shoulder || across > acrossMost - line.shoulder);
        IBlockState laying = junction == BeardLinks.NONE ? dress.track : BeardLinks.junctionTrack(rail, row, across, dress.track);
        if (verge && platform > 0 && BeardLinks.platformSide(rail, row, across < acrossLeast ? -1 : 1)) {
            int out = across < acrossLeast ? acrossLeast - across : across - acrossMost;
            line.laid += platformCell(world, at, x, z, level, platformY, out > platform, trestle, dress.supports, line.within, line.seeds);
            return;
        }
        if (verge && platform > 0 && (across < acrossLeast - 1 || across > acrossMost + 1)) { return; }
        if (verge) {
            if (trestle) { return; }
            if (tunnel) {
                line.lined += BeardRoadsTunnels.tunnelWall(world, rail, at, x, z, level, dress.linings);
            }
            else {
                line.lined += cutWall(world, at, alongX, row, across, level, across < acrossLeast ? -1 : 1, dress.linings);
                BeardRoadsPaving.vergeFill(world, rail, x, z, level, at);
            }
            return;
        }
        StructureComponent road = tunnel ? null : roadAt(line.roads, alongX, x, z);
        if (road != null) {
            if (onRail) {
                int atRoad = line.grade.at(alongX ? (road.getBoundingBox().minX + road.getBoundingBox().maxX) / 2 : (road.getBoundingBox().minZ + road.getBoundingBox().maxZ) / 2);
                if (atRoad == Integer.MIN_VALUE) { atRoad = level; }
                int seat = inBed ? atRoad : atRoad + 1;
                BeardKeep.letGo(x, seat, z);
                set(world, at, x, seat, z, laying);
                line.crossed++;
            }
            return;
        }
        if (trestle) {
            clearAbove(world, at, x, z, level + 1, level + BeardRails.CLEAR, line.within, line.seeds);
            line.laid += set(world, at, x, level, z, onRail && inBed ? laying : dress.decks.pick(world, x, level, z));
            if (edge && legRow) { line.trestled += BeardRoadsDecks.piling(world, alongX, row, across, level - 1, dress.supports.pick(world, x, level - 1, z), at); }
            if (onRail && !inBed) { set(world, at, x, level + 1, z, laying); }
            else if (edge && dress.barriers.first().getBlock() != Blocks.AIR) { set(world, at, x, level + 1, z, dress.barriers.pick(world, x, level + 1, z)); }
            return;
        }
        BlockPos top = GroundLevel.inWindow(world, new BlockPos(x, 64, z)).down();
        clearAbove(world, at, x, z, level + 1, tunnel ? level + BeardRails.CLEAR : Math.max(level + BeardRails.CLEAR, top.getY() + 2), line.within, line.seeds, tunnel || broken);
        BeardBlocks.fillUnder(world, at, x, z, BeardBlocks.belowLoose(world, at, x, z, level - 1, level - BeardRails.FILL_UNDER), level - BeardRails.FILL_UNDER);
        IBlockState base = onRail && inBed ? laying : powerRow && onRail ? dress.powerBase : onShoulder ? dress.shoulderBlock : tieRow ? dress.tie : dress.bed;
        line.laid += set(world, at, x, level, z, base);
        if (onRail && !inBed) { set(world, at, x, level + 1, z, powerRow ? dress.powered : laying); }
        if (tunnel) {
            line.lined += BeardRoadsTunnels.boreCell(world, at, x, z, level + BeardRails.CLEAR + 1, litRow && across == line.center ? dress.light : dress.linings.pick(world, x, level + BeardRails.CLEAR + 1, z));
        }
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
        return seeds.isEmpty() ? 0 : ContentBeardTrees.fellTrees(world, seeds, within, at);
    }

    private static int platformCell(World world, BlockPos.MutableBlockPos at, int x, int z, int level, int top, boolean railed, boolean trestle, BeardRoads.Palette supports, Predicate<BlockPos> within, List<BlockPos> seeds) {
        clearAbove(world, at, x, z, top + 1, top + BeardRails.CLEAR, within, seeds);
        if (trestle) {
            if (Math.floorMod(x + z, LEG) == 0) { BeardRoadsDecks.piling(world, true, x, z, level - 1, supports.pick(world, x, level - 1, z), at); }
        }
        else { BeardBlocks.fillUnder(world, at, x, z, top - 1, top - BeardRails.FILL_UNDER); }
        int laid = 0;
        for (int y = level; y <= top; y++) { laid += set(world, at, x, y, z, BeardLinks.platformBlocks().pick(world, x, y, z)); }
        if (railed) {
            IBlockState railing = BeardLinks.railing().pick(world, x, top + 1, z);
            if (railing.getBlock() != Blocks.AIR) { set(world, at, x, top + 1, z, railing); }
        }
        return laid;
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
            if (BeardRails.alongTheLine(box, alongX)) { continue; }
            if (x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ) { return road; }
        }
        return null;
    }

    private static boolean uncrossedAt(List<StructureComponent> roads, boolean alongX, int row, int center, int level) {
        for (StructureComponent road : roads) {
            StructureBoundingBox box = road.getBoundingBox();
            if (BeardRails.alongTheLine(box, alongX)) { continue; }
            if (row < (alongX ? box.minX : box.minZ) - 1 || row > (alongX ? box.maxX : box.maxZ) + 1) { continue; }
            if (center < (alongX ? box.minZ : box.minX) || center > (alongX ? box.maxZ : box.maxX)) { continue; }
            BeardRoads.Grade grade = road instanceof IRoadLayout ? ((IRoadLayout) road).rdpl$layout() : null;
            if (grade == null) { return false; }
            int at = grade.at(center);
            return at != Integer.MIN_VALUE && at >= level + BeardRails.OVER;
        }
        return true;
    }

    private static boolean[] bridgeFrames(BeardRoads.Grade grade) {
        int rows = grade.rows();
        boolean[] frames = new boolean[rows];
        if (BeardRails.frameBlocks().first().getBlock() == Blocks.AIR) { return frames; }
        for (int i = 0; i < rows; i++) { frames[i] = decking(grade, i); }
        return BeardRoadsDecks.frameRows(frames, BeardRails.frameLeast(), BeardRails.frameRun());
    }

    private static boolean decking(BeardRoads.Grade grade, int i) {
        int row = grade.start() + i;
        return grade.bridgedAt(row) && grade.at(row) != Integer.MIN_VALUE;
    }

    private static int bridgeFrame(World world, BlockPos.MutableBlockPos at, StructureBoundingBox clip, boolean alongX, int row, int acrossLeast, int acrossMost, int level) {
        BeardRoads.Palette posts = BeardRails.frameBlocks();
        BeardRoads.Palette beams = ContentControl.text(ContentControl.VILLAGES, "villageRailBridgeFrameTopBlock", Config.worldgen.villageRailBridgeFrameTopBlock, BeardBiome.building()).trim().isEmpty() ? posts : BeardRoads.pathPalette("villageRailBridgeFrameTopBlock", Config.worldgen.villageRailBridgeFrameTopBlock, posts.first());
        int height = BeardRails.frameHeight();
        int laid = 0;
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            if (!clip.isVecInside(at.setPos(x, level, z))) { continue; }
            if (across == acrossLeast || across == acrossMost) {
                for (int y = level + 1; y <= level + height; y++) { laid += set(world, at, x, y, z, posts.pick(world, x, y, z)); }
            }
            laid += set(world, at, x, level + height + 1, z, beams.pick(world, x, level + height + 1, z));
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
            if (up == Blocks.AIR || BeardKeep.holds(x, y, z) || BeardRails.railBlock(above)) { continue; }
            if (above.getMaterial().isLiquid()) {
                if (!bored) { break; }
                world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
                continue;
            }
            if (tree(world, at, x, y, z, above, within, seeds)) { continue; }
            if (bored || BeardBlocks.terrainBlock(up) || up == Blocks.GRASS_PATH || up == Blocks.SANDSTONE || up == Blocks.MYCELIUM || !above.getMaterial().isSolid()) {
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
        BlockPos trunk = BeardClearing.sustainer(world, new BlockPos(x, y, z), within);
        if (trunk == null) {
            BeardBlocks.note(world, at, "Laying a railway");
            world.setBlockState(at, Blocks.AIR.getDefaultState(), 2);
        }
        else if (!ContentVillageDecor.plantedAt(world, trunk.getX(), trunk.getZ())) { seeds.add(trunk); }
        return true;
    }

    private static IBlockState powered(IBlockState state) {
        if (state.getPropertyKeys().contains(BlockRailPowered.POWERED)) { return state.withProperty(BlockRailPowered.POWERED, true); }
        return state;
    }
}
