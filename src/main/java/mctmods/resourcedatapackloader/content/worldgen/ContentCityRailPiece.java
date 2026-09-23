package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import it.unimi.dsi.fastutil.shorts.ShortList;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityRailPiece extends StructurePiece implements PieceBeardifierModifier, ContentCityTrees.Felling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityRailPiece::new;
    public static final int CLEAR = CityRails.CLEAR;
    private static final int LEG = 4;
    private static final int WET_REACH = 3;
    private static final int CANOPY = 14;
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String WIDTH = "Wide";
    private static final String BRIDGED = "Trestle";
    private static final String BORED = "Bore";
    private static final String SUBWAY = "Sub";
    private static final String CROSSED = "Cross";
    private static final String FRAMES = "Frames";
    private static final String TRUNK = "RdplTrunk";
    private static final String LINK_ENDS = "RdplLinkEnds";
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final int width;
    private final boolean bridged;
    private final boolean bored;
    private final boolean subway;
    private final boolean crossed;
    private final int[] frames;
    @Nullable private final int[] trunk;
    private final int[] ends;

    public ContentCityRailPiece(int from, int to, int level, int middle, boolean alongX, int width, boolean bridged, boolean bored, boolean subway, boolean crossed, int[] frames, @Nullable int[] trunk, int[] ends) {
        super(TYPE, 0, box(from, to, level, middle, alongX, width, CityLinks.platformOver(trunk, ends, from, to)));
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.width = width;
        this.bridged = bridged;
        this.bored = bored;
        this.subway = subway;
        this.crossed = crossed;
        this.frames = frames;
        this.trunk = trunk;
        this.ends = ends;
    }

    public ContentCityRailPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.width = tag.getInt(WIDTH);
        this.bridged = tag.getBoolean(BRIDGED);
        this.bored = tag.getBoolean(BORED);
        this.subway = tag.getBoolean(SUBWAY);
        this.crossed = tag.getBoolean(CROSSED);
        this.frames = tag.getIntArray(FRAMES);
        this.trunk = tag.contains(TRUNK) ? tag.getIntArray(TRUNK) : null;
        this.ends = tag.getIntArray(LINK_ENDS);
    }

    private static BoundingBox box(int from, int to, int level, int middle, boolean alongX, int width, int platform) {
        int reach = (width - 1) / 2 + 1 + platform;
        return alongX ? new BoundingBox(from, level, middle - reach, to, level + CLEAR + 1, middle + reach) : new BoundingBox(middle - reach, level, from, middle + reach, level + CLEAR + 1, to);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putInt(WIDTH, width);
        tag.putBoolean(BRIDGED, bridged);
        tag.putBoolean(BORED, bored);
        tag.putBoolean(SUBWAY, subway);
        tag.putBoolean(CROSSED, crossed);
        tag.putIntArray(FRAMES, frames);
        if (trunk != null) { tag.putIntArray(TRUNK, trunk); }
        if (ends.length > 0) { tag.putIntArray(LINK_ENDS, ends); }
    }

    private record Dress(BlockState bed, BlockState tie, BlockState track, BlockState powered, BlockState base, BlockState shoulder, @Nullable BlockState light, CityPalette support, CityPalette deck, @Nullable CityPalette barrier, CityPalette linings) {
        static Dress of(boolean subway, boolean alongX) {
            return new Dress(CityPalette.stateOr(ContentCity.railBedBlock(subway), Blocks.GRAVEL.defaultBlockState()), CityPalette.stateOr(ContentCity.railTieBlock(subway), Blocks.OAK_PLANKS.defaultBlockState()),
                    oriented(CityPalette.stateOr(ContentCity.railBlock(subway), Blocks.RAIL.defaultBlockState()), alongX), energized(oriented(CityPalette.stateOr(ContentCity.railPowerBlock(subway), Blocks.POWERED_RAIL.defaultBlockState()), alongX)),
                    CityPalette.stateOr(ContentCity.railPowerBase(subway), Blocks.REDSTONE_BLOCK.defaultBlockState()), CityPalette.stateOr(ContentCity.railShoulderBlock(subway), Blocks.AIR.defaultBlockState()), CityPalette.state(ContentCity.railTunnelLightBlock(subway)),
                    CityPalette.of(ContentCity.railSupportBlock(), Blocks.OAK_LOG.defaultBlockState()), CityPalette.of(ContentCity.railDeckBlock(), Blocks.OAK_PLANKS.defaultBlockState()), CityPalette.mixed(ContentCity.railBarrierBlock()),
                    CityPalette.of(ContentCity.railTunnelBlock(subway), Blocks.AIR.defaultBlockState()));
        }
    }

    @Override @Nullable public BoundingBox stood() { return bored || crossed ? null : getBoundingBox(); }

    boolean subway() { return subway; }

    @Override @Nullable public BoundingBox owned() {
        BoundingBox held = getBoundingBox();
        boolean platformed = CityLinks.platformOver(trunk, ends, alongX ? held.minX() : held.minZ(), alongX ? held.maxX() : held.maxZ()) > 0;
        if (bridged) { return ContentCityTrees.bridge(platformed || CityLinks.beardless(ends, alongX ? held.minX() : held.minZ(), alongX ? held.maxX() : held.maxZ()) ? held : trimmed(),level, ContentCity.railFrameHeight()); }
        return platformed ? held : null;
    }

    @Override public int fellFloor() { return level - 2; }

    @Override @Nullable public BoundingBox felled() { return bored || crossed ? strip(fellFloor()) : ContentCityTrees.Felling.super.felled(); }

    @Override public BoundingBox crowned() { return strip(level + 1); }

    private BoundingBox strip(int bottom) {
        int half = (width - 1) / 2;
        BoundingBox held = getBoundingBox();
        return alongX ? new BoundingBox(held.minX(), bottom, middle - half, held.maxX(), level + CANOPY, middle + half) : new BoundingBox(middle - half, bottom, held.minZ(), middle + half, level + CANOPY, held.maxZ());
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkPos chunk, @Nonnull BoundingBox box) {
        long seed = level.getSeed();
        int boreHalf = (width - 1) / 2;
        int acrossLeast = middle - boreHalf;
        int acrossMost = middle + boreHalf;
        int shoulder = ContentCity.railShoulderWidth(subway);
        int tracks = ContentCity.railTrackCount(subway);
        int gap = ContentCity.railTrackGap(subway);
        int tieRun = ContentCity.railTieRun(subway);
        int lightRun = ContentCity.railTunnelLightRun(subway);
        Dress dress = Dress.of(subway, alongX);
        boolean inBed = ContentCity.trackInBed(dress.track(), subway);
        int powerRun = dress.powered().isAir() || inBed ? 0 : ContentCity.railPowerRun(subway);
        BoundingBox held = getBoundingBox();
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        if (CityLinks.partnerless(level, trunk, held)) { return; }
        int[] unfounded = CityLinks.unfounded(level.getLevel(), ends);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        List<BoundingBox> others = ContentCityTrees.footprints(manager, chunk, this, box);
        if (crossed) {
            crossing(level, box, others, unfounded, first, last, acrossLeast, acrossMost, tracks, gap, dress, inBed, seed, at);
            return;
        }
        List<BoundingBox> under = bored && !subway ? underAnother(manager, chunk, box) : List.of();
        int felled = ContentCityTrees.fellAround(level, manager, chunk, this, box);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the railway at {}, {} was laid", felled, held.minX(), held.minZ()); }
        List<CityRails.Laid> bores = subway ? List.of() : CityRails.subways(CityGround.of(level), held.minX(), held.minZ(), held.maxX(), held.maxZ());
        int columns = 0;
        int lined = 0;
        int piled = 0;
        int platformed = 0;
        int stilled = 0;
        BlockPos.MutableBlockPos near = new BlockPos.MutableBlockPos();
        CityPalette platforms = CityLinks.platformBlocks();
        CityPalette railing = CityPalette.mixed(ContentCity.railingBlock());
        for (int row = first; row <= last; row++) {
            if (CityLinks.unlaid(unfounded, row)) { continue; }
            if (CityBiome.moved(level, alongX ? row : middle, alongX ? middle : row)) {
                dress = Dress.of(subway, alongX);
                inBed = ContentCity.trackInBed(dress.track(), subway);
                powerRun = dress.powered().isAir() || inBed ? 0 : ContentCity.railPowerRun(subway);
            }
            boolean tieRow = Math.floorMod(row, tieRun) == 0;
            boolean powerRow = powerRun > 0 && CityLinks.clearOfJoin(trunk, ends, row) && Math.floorMod(row, powerRun) == 0;
            boolean litRow = dress.light() != null && Math.floorMod(row, lightRun) == 0;
            boolean frameRow = bridged && framed(row);
            boolean legRow = Math.floorMod(row, LEG) == 0 || frameRow;
            int platform = CityLinks.platformAt(trunk, ends, row);
            int platformTop = inBed ? this.level : this.level + 1;
            for (int across = acrossLeast - 1 - Math.max(0, platform); across <= acrossMost + 1 + Math.max(0, platform); across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                if (!box.isInside(at.set(x, this.level, z))) { continue; }
                int junction = CityLinks.junction(trunk, row, across);
                if (junction == CityLinks.OPEN) { continue; }
                if (junction == CityLinks.WALL) {
                    if (bored) { lined += boreWall(level, under, x, z, dress.linings(), seed, at); }
                    continue;
                }
                boolean verge = across < acrossLeast || across > acrossMost;
                boolean onRail = junction == CityLinks.SPUR || junction == CityLinks.CURVE || (junction == CityLinks.NONE && onTrack(across, tracks, gap));
                boolean edge = junction == CityLinks.NONE && (across == acrossLeast || across == acrossMost) && (platform <= 0 || !CityLinks.platformSide(ends, alongX, row, across < middle ? -1 : 1));
                BlockState laying = junction == CityLinks.NONE ? dress.track() : CityLinks.junctionTrack(trunk, row, across, dress.track());
                boolean onShoulder = shoulder > 0 && (across < acrossLeast + shoulder || across > acrossMost - shoulder);
                if (verge && platform > 0 && CityLinks.platformSide(ends, alongX, row, across < acrossLeast ? -1 : 1)) {
                    int out = across < acrossLeast ? acrossLeast - across : across - acrossMost;
                    platformed += platformCell(level, box, x, z, platformTop, out > platform, platforms, railing, dress.support(), seed, bores, at);
                    continue;
                }
                if (verge && platform > 0 && (across < acrossLeast - 1 || across > acrossMost + 1)) { continue; }
                if (verge) {
                    if (bridged) { continue; }
                    if (bored) { lined += boreWall(level, under, x, z, dress.linings(), seed, at); }
                    else {
                        lined += cutWall(level, row, across, across < acrossLeast ? -1 : 1, dress.linings(), seed, at);
                        CityPlotGround.vergeFill(level, box, others, x, z, this.level, at);
                    }
                    continue;
                }
                columns++;
                int top = bridged || bored ? this.level + CLEAR : Math.max(this.level + CLEAR, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 1);
                stilled += clearAbove(level, x, z, this.level + 1, top, bored || subway, at, near);
                if (bridged) {
                    level.setBlock(at.set(x, this.level, z), onRail && inBed ? laying : dress.deck().pick(seed, x, this.level, z), 2);
                    if (edge && legRow) { piled += CityPlotGround.piling(level, x, z, this.level - 1, dress.support().pick(seed, x, this.level - 1, z), CityRails.boreRoof(bores, x, z), at); }
                    if (onRail && !inBed) { level.setBlock(at.set(x, this.level + 1, z), laying, 2); }
                    else if (edge && dress.barrier() != null) { level.setBlock(at.set(x, this.level + 1, z), dress.barrier().pick(seed, x, this.level + 1, z), 2); }
                    continue;
                }
                int floor = fillFloor(bores, x, z);
                CityPlotGround.fillUnder(level, box, x, z, CityPlotGround.belowLoose(level, x, z, this.level - 1, floor, at), floor);
                BlockState base = onRail && inBed ? laying : powerRow && onRail ? dress.base() : onShoulder ? dress.shoulder() : tieRow ? dress.tie() : dress.bed();
                level.setBlock(at.set(x, this.level, z), base, 2);
                if (onRail && !inBed) { level.setBlock(at.set(x, this.level + 1, z), powerRow ? dress.powered() : laying, 2); }
                if (bored) {
                    level.setBlock(at.set(x, this.level + CLEAR + 1, z), litRow && across == middle ? dress.light() : dress.linings().pick(seed, x, this.level + CLEAR + 1, z), 2);
                    lined++;
                }
            }
            if (frameRow) { frame(level, box, row, acrossLeast, acrossMost, at); }
        }
        int forced = inBed ? 0 : CityLinks.force(level, box, trunk, first, last, this.level, dress.track(), at);
        BoundingBox seats = new BoundingBox(Math.max(box.minX(), held.minX()), Math.max(box.minY(), held.minY()), Math.max(box.minZ(), held.minZ()), Math.min(box.maxX(), held.maxX()), Math.min(box.maxY(), held.maxY()), Math.min(box.maxZ(), held.maxZ()));
        int seated = CityLinks.benches(level, seats, trunk, ends, unfounded, alongX, middle, boreHalf, inBed ? this.level : this.level + 1, at);
        if (columns > 0) { ContentLog.LOGGER.debug("The {} at {}, {} laid {} column(s) at y {} in this chunk, {} block(s) of lining, {} of support, {} junction rail(s) forced, {} station platform block(s), {} bench block(s), {} water block(s) beside it left still{}{}", subway ? "subway" : "railway", held.minX(), held.minZ(), columns, this.level, lined, piled, forced, platformed, seated, stilled, bridged ? ", on a trestle" : "", bored ? ", bored" : ""); }
    }

    private void crossing(WorldGenLevel level, BoundingBox box, List<BoundingBox> others, int[] unfounded, int first, int last, int acrossLeast, int acrossMost, int tracks, int gap, Dress dress, boolean inBed, long seed, BlockPos.MutableBlockPos at) {
        BoundingBox held = getBoundingBox();
        int crossing = 0;
        int walled = 0;
        int platformed = 0;
        List<CityRails.Laid> bores = bridged && !subway ? CityRails.subways(CityGround.of(level), held.minX(), held.minZ(), held.maxX(), held.maxZ()) : List.of();
        CityPalette platforms = CityLinks.platformBlocks();
        CityPalette railing = CityPalette.mixed(ContentCity.railingBlock());
        for (int row = first; row <= last; row++) {
            if (CityLinks.unlaid(unfounded, row)) { continue; }
            for (int across = acrossLeast; across <= acrossMost; across++) {
                if (!onTrack(across, tracks, gap)) { continue; }
                at.set(alongX ? row : across, inBed ? this.level : this.level + 1, alongX ? across : row);
                if (!box.isInside(at)) { continue; }
                level.setBlock(at, dress.track(), 2);
                crossing++;
            }
            int platform = Math.max(0, CityLinks.platformAt(trunk, ends, row));
            for (int across = acrossLeast - 1 - platform; across <= acrossMost + 1 + platform; across++) {
                if (across >= acrossLeast && across <= acrossMost) { continue; }
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                int junction = CityLinks.junction(trunk, row, across);
                if (junction == CityLinks.OPEN || junction == CityLinks.WALL || !box.isInside(at.set(x, this.level, z))) { continue; }
                int side = across < acrossLeast ? -1 : 1;
                if (platform > 0 && CityLinks.platformSide(ends, alongX, row, side)) {
                    int out = side < 0 ? acrossLeast - across : across - acrossMost;
                    platformed += platformCell(level, box, x, z, inBed ? this.level : this.level + 1, out > platform, platforms, railing, dress.support(), seed, bores, at);
                    continue;
                }
                if (across < acrossLeast - 1 || across > acrossMost + 1) { continue; }
                walled += cutWall(level, row, across, side, dress.linings(), seed, at);
                CityPlotGround.vergeFill(level, box, others, x, z, this.level, at);
            }
        }
        if (crossing > 0) { ContentLog.LOGGER.debug("The {} at {}, {} laid {} track block(s) across the street it crosses at y {}", subway ? "subway" : "railway", held.minX(), held.minZ(), crossing, this.level); }
        if (platformed > 0) { ContentLog.LOGGER.debug("The {} at {}, {} laid {} station platform block(s) beside the street it crosses at y {}", subway ? "subway" : "railway", held.minX(), held.minZ(), platformed, this.level); }
        if (walled > 0) { ContentLog.LOGGER.debug("The {} at {}, {} walled {} block(s) of its verge against the water beside the street it crosses at y {}", subway ? "subway" : "railway", held.minX(), held.minZ(), walled, this.level); }
    }

    private static int still(WorldGenLevel level, BlockPos at, BlockPos.MutableBlockPos near) {
        int stilled = 0;
        for (Direction side : Direction.values()) {
            near.setWithOffset(at, side);
            if (!level.hasChunk(SectionPos.blockToSectionCoord(near.getX()), SectionPos.blockToSectionCoord(near.getZ())) || level.getFluidState(near).isEmpty()) { continue; }
            ChunkAccess chunk = level.getChunk(near);
            int section = chunk.getSectionIndex(near.getY());
            if (section < 0 || section >= chunk.getPostProcessing().length) { continue; }
            ShortList marks = chunk.getPostProcessing()[section];
            if (marks != null && marks.rem(ProtoChunk.packOffsetCoordinates(near))) { stilled++; }
        }
        return stilled;
    }

    private List<BoundingBox> underAnother(StructureManager manager, ChunkPos chunk, BoundingBox near) {
        List<BoundingBox> found = new ArrayList<>();
        for (StructureStart start : manager.startsForStructure(chunk, structure -> structure instanceof ContentCityStructure)) {
            if (!start.getPieces().contains(this)) { continue; }
            for (StructurePiece piece : start.getPieces()) {
                if (piece == this) { continue; }
                BoundingBox held = footprint(piece);
                if (held != null && held.intersects(near.minX(), near.minZ(), near.maxX(), near.maxZ())) { found.add(held); }
            }
        }
        return found;
    }

    @Nullable private static BoundingBox footprint(StructurePiece piece) {
        if (piece instanceof ContentCityPiece street) { return street.paved(); }
        if (piece instanceof ContentCityRailPiece rail) { return rail.subway || rail.crossed ? null : rail.getBoundingBox(); }
        if (piece instanceof ContentCityBulbPiece bulb) { return bulb.stood(); }
        if (piece instanceof ContentCityPlazaPiece plaza) { return plaza.reached(); }
        if (piece instanceof ContentCityPlotPiece || piece instanceof ContentCityFarmPiece || piece instanceof ContentCityPierPiece || piece instanceof ContentCityStationPiece || piece instanceof ContentCityWellPiece || piece instanceof ContentCityIntersectPiece) { return piece.getBoundingBox(); }
        return null;
    }

    private int boreWall(WorldGenLevel level, List<BoundingBox> under, int x, int z, CityPalette linings, long seed, BlockPos.MutableBlockPos at) {
        if (CityPlotGround.covered(under, x, z)) { return 0; }
        for (int y = this.level; y <= this.level + CLEAR + 1; y++) { level.setBlock(at.set(x, y, z), linings.pick(seed, x, y, z), 2); }
        return CLEAR + 2;
    }

    private boolean framed(int row) {
        for (int held : frames) {
            if (held == row) { return true; }
        }
        return false;
    }

    private boolean onTrack(int across, int tracks, int gap) {
        if (tracks <= 1) { return across == middle; }
        int first = middle - (tracks - 1) * gap / 2;
        for (int line = 0; line < tracks; line++) {
            if (across == first + line * gap) { return true; }
        }
        return false;
    }

    private int cutWall(WorldGenLevel level, int row, int across, int outward, CityPalette linings, long seed, BlockPos.MutableBlockPos at) {
        int top = Integer.MIN_VALUE;
        for (int out = 0; out <= WET_REACH; out++) {
            int side = across + outward * out;
            for (int y = this.level; y <= this.level + CLEAR + 1; y++) {
                at.set(alongX ? row : side, y, alongX ? side : row);
                if (!level.getBlockState(at).getFluidState().isEmpty()) { top = Math.max(top, y); }
            }
        }
        if (top == Integer.MIN_VALUE) { return 0; }
        int x = alongX ? row : across;
        int z = alongX ? across : row;
        int laid = 0;
        for (int y = this.level; y <= top; y++) {
            level.setBlock(at.set(x, y, z), linings.pick(seed, x, y, z), 2);
            laid++;
        }
        return laid;
    }

    private int platformCell(WorldGenLevel level, BoundingBox box, int x, int z, int top, boolean railed, CityPalette platforms, @Nullable CityPalette railing, CityPalette support, long seed, List<CityRails.Laid> bores, BlockPos.MutableBlockPos at) {
        clearAbove(level, x, z, top + 1, top + CLEAR, false, at, new BlockPos.MutableBlockPos());
        if (bridged) {
            if (Math.floorMod(x + z, LEG) == 0) { CityPlotGround.piling(level, x, z, this.level - 1, support.pick(seed, x, this.level - 1, z), CityRails.boreRoof(bores, x, z), at); }
        }
        else { CityPlotGround.fillUnder(level, box, x, z, top - 1, top - CityPlotGround.FILL_UNDER); }
        int laid = 0;
        for (int y = this.level; y <= top; y++) {
            level.setBlock(at.set(x, y, z), platforms.pick(seed, x, y, z), 2);
            laid++;
        }
        if (railed && railing != null) { level.setBlock(at.set(x, top + 1, z), railing.pick(seed, x, top + 1, z), 2); }
        return laid;
    }

    private int fillFloor(List<CityRails.Laid> bores, int x, int z) {
        int deep = this.level - CityPlotGround.FILL_UNDER;
        int roof = CityRails.boreRoof(bores, x, z);
        return roof == Integer.MIN_VALUE ? deep : Math.max(deep, roof + 1);
    }

    private static int clearAbove(WorldGenLevel level, int x, int z, int from, int to, boolean bored, BlockPos.MutableBlockPos at, BlockPos.MutableBlockPos near) {
        int stilled = 0;
        for (int y = from; y <= to; y++) {
            BlockState over = level.getBlockState(at.set(x, y, z));
            if (over.isAir() || over.getBlock() instanceof BaseRailBlock) { continue; }
            if (bored) {
                if (ContentCityTrees.clears(level, at, over)) {
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    stilled += still(level, at, near);
                }
                continue;
            }
            if (!over.getFluidState().isEmpty()) { return stilled; }
            if (CityPlotGround.cuts(level, at, over)) {
                level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                stilled += still(level, at, near);
            }
            else if (!(over.getBlock() instanceof LeavesBlock)) { return stilled; }
        }
        return stilled;
    }

    private void frame(WorldGenLevel level, BoundingBox box, int row, int acrossLeast, int acrossMost, BlockPos.MutableBlockPos at) {
        CityPalette posts = CityPalette.mixed(ContentCity.railFrameBlock());
        if (posts == null) { return; }
        CityPalette beams = ContentCity.railFrameTopBlock().isEmpty() ? posts : CityPalette.of(ContentCity.railFrameTopBlock(), posts.first());
        long seed = level.getSeed();
        int height = ContentCity.railFrameHeight();
        for (int across = acrossLeast; across <= acrossMost; across++) {
            int x = alongX ? row : across;
            int z = alongX ? across : row;
            if (!box.isInside(at.set(x, this.level, z))) { continue; }
            if (across == acrossLeast || across == acrossMost) {
                for (int y = this.level + 1; y <= this.level + height; y++) { level.setBlock(at.set(x, y, z), posts.pick(seed, x, y, z), 2); }
            }
            level.setBlock(at.set(x, this.level + height + 1, z), beams.pick(seed, x, this.level + height + 1, z), 2);
        }
    }

    private static BlockState oriented(BlockState laid, boolean alongX) {
        RailShape shape = alongX ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH;
        if (laid.hasProperty(BlockStateProperties.RAIL_SHAPE)) { return laid.setValue(BlockStateProperties.RAIL_SHAPE, shape); }
        if (laid.hasProperty(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) { return laid.setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, shape); }
        if (laid.hasProperty(BlockStateProperties.AXIS)) { return laid.setValue(BlockStateProperties.AXIS, alongX ? Direction.Axis.X : Direction.Axis.Z); }
        return laid;
    }

    static BlockState shaped(BlockState laid, RailShape wanted, boolean alongX) {
        if (laid.hasProperty(BlockStateProperties.RAIL_SHAPE) && BlockStateProperties.RAIL_SHAPE.getPossibleValues().contains(wanted)) { return laid.setValue(BlockStateProperties.RAIL_SHAPE, wanted); }
        if (laid.hasProperty(BlockStateProperties.RAIL_SHAPE_STRAIGHT) && BlockStateProperties.RAIL_SHAPE_STRAIGHT.getPossibleValues().contains(wanted)) { return laid.setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, wanted); }
        return oriented(laid, alongX);
    }

    private static BlockState energized(BlockState laid) { return laid.hasProperty(BlockStateProperties.POWERED) ? laid.setValue(BlockStateProperties.POWERED, true) : laid; }

    private BoundingBox trimmed() {
        BoundingBox held = getBoundingBox();
        int half = (width - 1) / 2;
        int[] rows = CityLinks.bearded(ends, alongX ? held.minX() : held.minZ(), alongX ? held.maxX() : held.maxZ());
        return alongX ? new BoundingBox(rows[0], level, middle - half, rows[1], level, middle + half) : new BoundingBox(middle - half, level, rows[0], middle + half, level, rows[1]);
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return getBoundingBox(); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.NONE; }

    @Override public int getGroundLevelDelta() { return 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, manager, chunk, box));
    }
}
