package mctmods.resourcedatapackloader.content.worldgen;

import static mctmods.resourcedatapackloader.content.worldgen.CityRails.CLEAR;
import static mctmods.resourcedatapackloader.content.worldgen.CityRails.LEG;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.common.world.PieceBeardifierModifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityPiece extends StructurePiece implements PieceBeardifierModifier, ContentCityTrees.IFelling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityPiece::new;
    private static final int VERGE = 1;
    private static final int NARROW = 3;
    private static final String PAVING = "Paving";
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String ALLEY = "Alley";
    private static final String WIDTH = "Wide";
    private static final String BRIDGED = "Bridge";
    private static final String BORED = "Bore";
    private static final String LAMP = "Lamp";
    private static final String KEYS = "Keys";
    private static final String FRAMES = "Frames";
    private static final String PINNED = "Pinned";
    private static final int PILING_ROWS = 4;
    private static final int RISE = 8;

    private final String paving;
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final boolean alley;
    private final int width;
    private final boolean bridged;
    private final boolean bored;
    private final int[] frames;
    private final int lampAt;
    private final String keys;
    private final boolean pinned;
    @Nullable private JsonObject roadKeys;
    private boolean keysRead;

    public ContentCityPiece(int fromX, int fromZ, int toX, int toZ, int level, String paving, int middle, boolean alongX, boolean alley, int width, boolean bridged, boolean bored, int[] frames, int lampAt, String keys, boolean pinned) {
        super(TYPE, 0, box(fromX, fromZ, toX, toZ, level, alongX));
        this.paving = paving;
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.alley = alley;
        this.width = width;
        this.bridged = bridged;
        this.bored = bored;
        this.frames = frames;
        this.lampAt = lampAt;
        this.keys = keys;
        this.pinned = pinned;
    }

    public ContentCityPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.paving = tag.getString(PAVING);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.alley = tag.getBoolean(ALLEY);
        this.width = tag.getInt(WIDTH);
        this.bridged = tag.getBoolean(BRIDGED);
        this.bored = tag.getBoolean(BORED);
        this.frames = tag.getIntArray(FRAMES);
        this.lampAt = tag.contains(LAMP) ? tag.getInt(LAMP) : Integer.MIN_VALUE;
        this.keys = tag.getString(KEYS);
        this.pinned = tag.getBoolean(PINNED);
    }

    private static BoundingBox box(int fromX, int fromZ, int toX, int toZ, int level, boolean alongX) {
        return alongX ? new BoundingBox(fromX, level, fromZ - VERGE, toX, level + CLEAR + 1, toZ + VERGE) : new BoundingBox(fromX - VERGE, level, fromZ, toX + VERGE, level + CLEAR + 1, toZ);
    }

    private int pavedLeast() { return middle - width / 2; }

    private int pavedMost() { return pavedLeast() + width - 1; }

    BoundingBox paved() {
        BoundingBox held = getBoundingBox();
        return alongX ? new BoundingBox(held.minX(), held.minY(), pavedLeast(), held.maxX(), held.maxY(), pavedMost()) : new BoundingBox(pavedLeast(), held.minY(), held.minZ(), pavedMost(), held.maxY(), held.maxZ());
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putString(PAVING, paving);
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putBoolean(ALLEY, alley);
        tag.putInt(WIDTH, width);
        tag.putBoolean(BRIDGED, bridged);
        tag.putBoolean(BORED, bored);
        if (frames.length > 0) { tag.putIntArray(FRAMES, frames); }
        if (lampAt != Integer.MIN_VALUE) { tag.putInt(LAMP, lampAt); }
        if (!keys.isEmpty()) { tag.putString(KEYS, keys); }
        if (pinned) { tag.putBoolean(PINNED, true); }
    }

    boolean tunnelNear(BlockPos at) {
        BoundingBox held = getBoundingBox();
        return bored && at.getX() >= held.minX() - 1 && at.getX() <= held.maxX() + 1 && at.getZ() >= held.minZ() - 1 && at.getZ() <= held.maxZ() + 1;
    }

    boolean runsIntoTunnel(int x, int y, int z) {
        BoundingBox held = getBoundingBox();
        return bored && y >= level - 1 && x >= held.minX() && x <= held.maxX() && z >= held.minZ() && z <= held.maxZ();
    }

    @Override @Nullable public BoundingBox stood() { return bored ? null : paved(); }

    @Override @Nonnull public BoundingBox owned() {
        if (bridged) { return ContentCityTrees.bridge(paved(), level, ContentCity.frameHeight()); }
        BoundingBox paved = paved();
        return new BoundingBox(paved.minX(), level, paved.minZ(), paved.maxX(), level, paved.maxZ());
    }

    @Override public int fellFloor() { return level - 1; }

    @Override @Nullable public BoundingBox crowned() {
        if (bored) { return null; }
        BoundingBox paved = paved();
        return new BoundingBox(paved.minX() - CityPlotGround.RING, level + 1, paved.minZ() - CityPlotGround.RING, paved.maxX() + CityPlotGround.RING, Math.max(level + CityPlotClearing.OPEN_LEAST, paved.maxY() + CityPlotClearing.OPEN_LEAST + 1), paved.maxZ() + CityPlotGround.RING);
    }

    private record Dress(BlockState road, BlockState deck, BlockState support, boolean chosen, BlockState edge, BlockState walk, BlockState center) {
        static Dress of(String paving, BlockState planks, @Nullable CityPlan plan, boolean bridged, boolean alley, boolean alongX) {
            BlockState deck = CityPalette.stateOr(ContentCity.bridgeBlock(), planks);
            BlockState road = CityPalette.stateOr(paving, Blocks.DIRT_PATH.defaultBlockState());
            if (bridged) { road = deck; }
            BlockState walk = CityPalette.stateOr(ContentCity.sidewalkBlock(), road);
            if (bridged) { walk = CityPalette.stateOr(ContentCity.bridgeSidewalkBlock(), walk); }
            return new Dress(road, deck, ContentCity.support(plan), bridged || ContentCity.pavingChosen(alley), CityPalette.axised(CityPalette.stateOr(ContentCity.lineBlock(), road), alongX), walk, CityPalette.axised(CityPalette.stateOr(ContentCity.centerBlock(), road), alongX));
        }
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkPos chunk, @Nonnull BoundingBox box) {
        CityGround ground = CityGround.of(level);
        CityPlan plan = plan(ground);
        CityPlan.Line street = plan == null || alley ? null : street(plan);
        List<CityPlan.Line> crossing = street == null ? List.of() : plan.crossing(street);
        Map<CityPlan.Line, Square> squares = new HashMap<>();
        BlockState planks = ContentCity.planks(plan);
        Dress dress = Dress.of(paving, planks, plan, bridged, alley, alongX);
        BlockState air = Blocks.AIR.defaultBlockState();
        CityCross cross = CityCross.of(width, alley);
        int dash = ContentCity.centerDash();
        BoundingBox held = getBoundingBox();
        int felled = ContentCityTrees.fellAround(level, manager, chunk, this, box);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the street at {}, {} was laid", felled, held.minX(), held.minZ()); }
        List<CityRails.Laid> bores = CityRails.subways(ground, held.minX(), held.minZ(), held.maxX(), held.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        List<BoundingBox> wells = ContentCityTrees.foreign(manager, chunk, this, held, ContentCityWellPiece.class::isInstance);
        List<ContentCityPlazaPiece> plazas = bored ? List.of() : ContentCityTrees.everyPiece(manager, chunk, this, held, ContentCityPlazaPiece.class::isInstance).stream().map(ContentCityPlazaPiece.class::cast).toList();
        int spared = 0;
        int blocked = 0;
        int lifted = 0;
        CityClip clip = CityClip.of(box, held, 0);
        for (int x = clip.minX(); x <= clip.maxX(); x++) {
            for (int z = clip.minZ(); z <= clip.maxZ(); z++) {
                if (CityBiome.moved(level, x, z)) {
                    dress = Dress.of(ContentCity.paving(alley), planks, plan, bridged, alley, alongX);
                }
                int across = alongX ? z : x;
                if (across < pavedLeast() || across > pavedMost()) { continue; }
                if (CityPlotGround.covered(wells, x, z) || ContentCityPlazaPiece.holding(plazas, x, z)) {
                    spared++;
                    continue;
                }
                if (CityRails.insideBore(bores, x, this.level, z)) { continue; }
                int deckY = bridged ? risen(level, at, x, z) : this.level;
                if (deckY > this.level) { lifted++; }
                at.set(x, deckY, z);
                BlockState standing = level.getBlockState(at);
                boolean wet = !bridged && overWater(level, ground, at, x, z);
                at.set(x, deckY, z);
                if (!bridged && CityPlotGround.inTheWay(standing, dress.road(), dress.support(), dress.deck())) {
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The street at {}, {} left {}, {}, {} unpaved because {} was already standing there and is not a surface a street may be laid over", held.minX(), held.minZ(), x, this.level, z, standing); }
                    blocked++;
                    continue;
                }
                if (bridged && deckBlocked(level, at, x, deckY, z)) {
                    blocked++;
                    continue;
                }
                BlockState surface = dress.chosen() ? dress.road() : CityPlotGround.pathForGround(level, x, z, dress.road(), dress.support(), CityPlotGround.earthy(standing));
                int along = alongX ? x : z;
                int offset = Math.abs(across - middle);
                CityPlan.Line other = street == null ? null : squareAt(street, crossing, along);
                BlockState mouthed = ContentCityPlazaPiece.mouthAt(plazas, x, z);
                BlockState laid = mouthed != null ? mouthed : other != null ? squares.computeIfAbsent(other, met -> Square.of(ground, plan, alongX ? street : met, alongX ? met : street)).cell(x, z, surface, dress.road(), dress.walk()) : switch (cross.role(offset)) {
                    case CityCross.WALK -> dress.walk() == dress.road() ? surface : dress.walk();
                    case CityCross.LINE -> dress.edge() == dress.road() ? surface : dress.edge();
                    case CityCross.CORE -> offset == 0 && !alley && dashed(along, dash) && dress.center() != dress.road() ? dress.center() : surface;
                    default -> surface;
                };
                at.set(x, deckY, z);
                level.setBlock(at, laid, Block.UPDATE_CLIENTS);
                if (wet) {
                    if (!pinned || onPiling(along, offset)) {
                        at.set(x, this.level - 1, z);
                        level.setBlock(at, dress.support(), Block.UPDATE_CLIENTS);
                        CityPlotGround.pier(level, box, x, this.level - 2, z, CityRails.fillFloor(bores, x, z, level.getMinBuildHeight() + 1), ground.floor(x, z), dress.support());
                    }
                }
                else if (!bridged) { CityPlotGround.fillUnder(level, box, x, z, this.level - 1, CityRails.fillFloor(bores, x, z, this.level - CityPlotGround.FILL_UNDER)); }
                int reach = bridged || bored ? CLEAR : Math.max(CLEAR, level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1 - this.level);
                for (int up = 1; up <= reach; up++) {
                    at.set(x, deckY + up, z);
                    BlockState over = level.getBlockState(at);
                    if (bridged && CityPlotGround.solid(over) || up > CLEAR && CityPlotGround.liquid(over)) { break; }
                    if (ContentCityTrees.clears(level, at, over) && !CityPlotGround.decoration(over)) { level.setBlock(at, air, Block.UPDATE_CLIENTS); }
                    else if (up > CLEAR && CityPlotGround.solid(over)) { break; }
                }
            }
        }
        if (spared > 0) { ContentLog.LOGGER.debug("The street at {}, {} left {} column(s) to a well or the plaza paved around one", held.minX(), held.minZ(), spared); }
        if (blocked > 0) { ContentLog.LOGGER.debug("The street at {}, {} left {} column(s) unpaved to blocks already standing at its level", held.minX(), held.minZ(), blocked); }
        if (lifted > 0) { ContentLog.LOGGER.debug("The street at {}, {} lifts its deck over {} column(s) of water standing at its level", held.minX(), held.minZ(), lifted); }
        if (!bored) { verges(level, box, ContentCityTrees.footprints(manager, chunk, this, box), bores); }
        if (bridged && width > NARROW) { barriers(level, box, cross); }
        if (bridged) { frames(level, manager, chunk, box, cross, bores); }
        if (bridged && width > NARROW) { legs(level, manager, chunk, box, cross, bores); }
        if (bored) { bore(level, box, cross, bores); }
    }

    private boolean overWater(WorldGenLevel level, CityGround ground, BlockPos.MutableBlockPos at, int x, int z) {
        int sea = ground.sea();
        if (ground.floor(x, z) < sea - 1) { return true; }
        int top = Math.max(level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1, sea - 1);
        if (CityPlotGround.liquid(level.getBlockState(at.set(x, top, z)))) { return true; }
        for (int y = this.level - 1; y >= this.level - CityPlotGround.FILL_UNDER; y--) {
            BlockState stood = level.getBlockState(at.set(x, y, z));
            if (CityPlotGround.liquid(stood)) { return true; }
            if (CityPlotGround.solid(stood)) { return false; }
        }
        return false;
    }

    private void verges(WorldGenLevel level, BoundingBox box, List<BoundingBox> others, List<CityRails.Laid> bores) {
        BoundingBox held = getBoundingBox();
        int first = CityClip.first(box, held, alongX);
        int last = CityClip.last(box, held, alongX);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along = first; along <= last; along++) {
            for (int side = 0; side < 2; side++) {
                int across = side == 0 ? pavedLeast() - 1 : pavedMost() + 1;
                int x = alongX ? along : across;
                int z = alongX ? across : along;
                if (CityRails.insideBore(bores, x, this.level, z)) { continue; }
                if (bridged && CityPlotGround.wetBed(level, at, x, z, this.level) != Integer.MIN_VALUE) { continue; }
                CityPlotGround.vergeFill(level, box, others, bores, x, z, this.level, at);
            }
        }
    }

    private void bore(WorldGenLevel level, BoundingBox box, CityCross cross, List<CityRails.Laid> bores) {
        CityPalette linings = CityPalette.mixed(ContentCity.tunnelBlock());
        if (linings == null) { return; }
        long seed = level.getSeed();
        BlockState lamp = CityPalette.state(ContentCity.tunnelLightBlock());
        BlockState air = Blocks.AIR.defaultBlockState();
        int run = ContentCity.tunnelLightRun();
        int wall = cross.curb() + 1;
        int roof = this.level + CLEAR + 1;
        BoundingBox held = getBoundingBox();
        int first = CityClip.first(box, held, alongX);
        int last = CityClip.last(box, held, alongX);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along = first; along <= last; along++) {
            for (int side = -1; side <= 1; side += 2) {
                int wallX = alongX ? along : middle + side * wall;
                int wallZ = alongX ? middle + side * wall : along;
                for (int up = 0; up <= CLEAR; up++) {
                    at.set(wallX, this.level + up, wallZ);
                    if (box.isInside(at)) { level.setBlock(at, linings.pick(seed, wallX, this.level + up, wallZ), Block.UPDATE_CLIENTS); }
                }
                CityPlotGround.wallFoot(level, box, bores, wallX, wallZ, this.level - 1, linings.pick(seed, wallX, this.level - 1, wallZ));
            }
            for (int across = middle - wall; across <= middle + wall; across++) {
                int roofX = alongX ? along : across;
                int roofZ = alongX ? across : along;
                at.set(roofX, roof, roofZ);
                if (!box.isInside(at)) { continue; }
                boolean lit = across == middle && (Math.floorMod(along, run) == 0 || along == lampAt);
                level.setBlock(at, lit && lamp != null ? lamp : linings.pick(seed, roofX, roof, roofZ), Block.UPDATE_CLIENTS);
            }
            for (int across = middle - cross.curb(); across <= middle + cross.curb(); across++) {
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(alongX ? along : across, this.level + up, alongX ? across : along);
                    if (box.isInside(at)) { level.setBlock(at, air, Block.UPDATE_CLIENTS); }
                }
            }
        }
    }

    private void barriers(WorldGenLevel level, BoundingBox box, CityCross cross) {
        BlockState rail = CityPalette.state(ContentCity.bridgeBarrierBlock());
        if (rail == null) { return; }
        int height = ContentCity.bridgeBarrierHeight();
        BoundingBox held = getBoundingBox();
        CityGround ground = CityGround.of(level);
        CityPlan plan = plan(ground);
        CityPlan.Line street = plan == null ? null : street(plan);
        List<CityRails.Laid> tracks = street == null ? List.of() : CityRails.subways(ground, held.minX(), held.minZ(), held.maxX(), held.maxZ());
        List<CityPlan.Line> crossing = street == null ? List.of() : plan.crossing(street);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int from = CityClip.first(box, held, alongX);
        int to = CityClip.last(box, held, alongX);
        for (int along = from; along <= to; along++) {
            CityPlan.Line square = street == null ? null : squareAt(street, crossing, along);
            if (square != null) {
                if (!alley) { corners(level, box, at, rail, height, cross, street, square, along); }
                continue;
            }
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * cross.curb();
                int x = alongX ? along : across;
                int z = alongX ? across : along;
                if (street != null && CityRails.openingAt(tracks, street, along, side > 0) != null) { continue; }
                raise(level, box, at, rail, height, x, z);
            }
        }
    }

    @Nullable private CityPlan.Line squareAt(CityPlan.Line street, List<CityPlan.Line> crossing, int along) {
        for (CityPlan.Line other : crossing) {
            if (!alley && other.alley()) { continue; }
            if (along >= other.at() && along <= other.last() && other.from() <= street.last() + 1 && other.to() >= street.at() - 1) { return other; }
        }
        return null;
    }

    private void corners(WorldGenLevel level, BoundingBox box, BlockPos.MutableBlockPos at, BlockState rail, int height, CityCross cross, CityPlan.Line street, CityPlan.Line other, int along) {
        CityPlan.Line ew = alongX ? street : other;
        CityPlan.Line ns = alongX ? other : street;
        CityCross ewCross = alongX ? cross : CityCross.of(other);
        CityCross nsCross = alongX ? CityCross.of(other) : cross;
        int arms = (ew.from() < ns.at() ? CityCross.WEST : 0) | (ew.to() > ns.last() ? CityCross.EAST : 0) | (ns.from() < ew.at() ? CityCross.NORTH : 0) | (ns.to() > ew.last() ? CityCross.SOUTH : 0);
        Square square = new Square(ew, ns, ewCross, nsCross, arms);
        for (int across = middle - cross.curb(); across <= middle + cross.curb(); across++) {
            int x = alongX ? along : across;
            int z = alongX ? across : along;
            if (square.road(x, z)) { continue; }
            boolean armX = square.armedX(x);
            boolean armZ = square.armedZ(z);
            boolean edgeX = Math.abs(x - ns.middle()) == nsCross.curb();
            boolean edgeZ = Math.abs(z - ew.middle()) == ewCross.curb();
            if (!((!armX && edgeX) || (!armZ && edgeZ) || (armX && armZ && edgeX && edgeZ))) { continue; }
            raise(level, box, at, rail, height, x, z);
        }
    }

    private int risen(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z) {
        int deckY = this.level;
        for (int lift = 0; lift < RISE && CityPlotGround.liquid(level.getBlockState(at.set(x, deckY, z))); lift++) { deckY++; }
        return deckY;
    }

    private void raise(WorldGenLevel level, BoundingBox box, BlockPos.MutableBlockPos at, BlockState rail, int height, int x, int z) { CityPlotGround.raise(level, box, at, rail, height, x, risen(level, at, x, z), z); }

    static boolean deckBlocked(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int deckY, int z) {
        for (int up = 0; up <= CLEAR; up++) {
            BlockState held = level.getBlockState(at.set(x, deckY + up, z));
            if (!CityPlotGround.solid(held)) { continue; }
            if (!CityPlotGround.terrain(held)) { break; }
            level.setBlock(at, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        return CityPlotGround.solid(level.getBlockState(at.set(x, deckY, z)));
    }

    private record Square(CityPlan.Line ew, CityPlan.Line ns, CityCross ewCross, CityCross nsCross, int arms) {
        static Square of(CityGround ground, CityPlan plan, CityPlan.Line ew, CityPlan.Line ns) { return new Square(ew, ns, CityCross.of(ew), CityCross.of(ns), CityCross.arms(ground, plan, ew, ns)); }

        private boolean has(int arm) { return (arms & arm) != 0; }

        private boolean road(int x, int z) {
            int centerX = ns.middle();
            int centerZ = ew.middle();
            int offX = Math.abs(x - centerX);
            int offZ = Math.abs(z - centerZ);
            boolean onEw = offZ <= ewCross.core() && (offX <= nsCross.core() || (x < centerX && has(CityCross.WEST)) || (x > centerX && has(CityCross.EAST)));
            boolean onNs = offX <= nsCross.core() && (offZ <= ewCross.core() || (z < centerZ && has(CityCross.NORTH)) || (z > centerZ && has(CityCross.SOUTH)));
            return onEw || onNs;
        }

        private boolean armedX(int x) { return x > ns.middle() ? has(CityCross.EAST) : x == ns.middle() || has(CityCross.WEST); }

        private boolean armedZ(int z) { return z > ew.middle() ? has(CityCross.SOUTH) : z == ew.middle() || has(CityCross.NORTH); }

        BlockState cell(int x, int z, BlockState surface, BlockState road, BlockState walk) {
            if (road(x, z)) { return surface; }
            int offX = Math.abs(x - ns.middle());
            int offZ = Math.abs(z - ew.middle());
            boolean armX = armedX(x);
            boolean armZ = armedZ(z);
            boolean withinX = offX <= nsCross.core() + nsCross.lines();
            boolean withinZ = offZ <= ewCross.core() + ewCross.lines();
            boolean bandX = offX > nsCross.core() && withinX;
            boolean bandZ = offZ > ewCross.core() && withinZ;
            boolean lined;
            boolean upright;
            if (armX && armZ) {
                lined = bandX || bandZ;
                upright = bandX;
            }
            else if (armX) {
                lined = bandZ;
                upright = false;
            }
            else if (armZ) {
                lined = bandX;
                upright = true;
            }
            else {
                lined = (bandX && withinZ) || (bandZ && withinX);
                upright = bandX;
            }
            if (!lined) { return walk == road ? surface : walk; }
            BlockState line = CityPalette.stateOr(ContentCity.lineBlock(), road);
            return line == road ? surface : CityPalette.axised(line, !upright);
        }
    }

    @Nullable private CityPlan plan(CityGround ground) {
        BoundingBox held = getBoundingBox();
        return CityPlan.of(ground, CityPlan.districtOf(alongX ? held.minX() : middle, true), CityPlan.districtOf(alongX ? middle : held.minZ(), false));
    }

    @Nullable private CityPlan.Line street(CityPlan plan) {
        BoundingBox held = getBoundingBox();
        int along = alongX ? held.minX() : held.minZ();
        for (CityPlan.Line line : alongX ? plan.alongX() : plan.alongZ()) {
            if (line.at() <= middle && line.last() >= middle && line.from() <= along && line.to() >= along) { return line; }
        }
        return null;
    }

    private void legs(WorldGenLevel level, StructureManager manager, ChunkPos chunk, BoundingBox box, CityCross cross, List<CityRails.Laid> bores) {
        BoundingBox held = getBoundingBox();
        CityPlan plan = plan(CityGround.of(level));
        CityPlan.Line street = plan == null ? null : street(plan);
        List<CityPlan.Line> crossing = street == null ? List.of() : plan.crossing(street);
        List<BoundingBox> piers = ContentCityTrees.kin(manager, chunk, this, held, ContentCityPierPiece.class::isInstance);
        BlockState support = ContentCity.support(plan);
        int first = alongX ? held.minX() : held.minZ();
        int last = alongX ? held.maxX() : held.maxZ();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int laid = 0;
        for (int along = Math.max(first, alongX ? box.minX() : box.minZ()); along <= Math.min(last, alongX ? box.maxX() : box.maxZ()); along++) {
            if ((along - first) % LEG != 0 && along != last) { continue; }
            if (street != null && squareAt(street, crossing, along) != null) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                int x = alongX ? along : middle + side * cross.curb();
                int z = alongX ? middle + side * cross.curb() : along;
                if (!box.isInside(at.set(x, this.level, z)) || CityPlotGround.covered(piers, x, z) || !overDryDrop(level, at, x, z)) { continue; }
                laid += CityPlotGround.piling(level, x, z, this.level - 1, support, CityRails.boreRoof(bores, x, z), at);
            }
        }
        if (laid > 0) { ContentLog.LOGGER.debug("The street bridge at {}, {} stands on {} block(s) of legs down to the dry ground under it", held.minX(), held.minZ(), laid); }
    }

    private boolean overDryDrop(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int z) {
        for (int y = this.level - 1; y >= this.level - CityPlotGround.PILING_REACH && y > level.getMinBuildHeight(); y--) {
            BlockState held = level.getBlockState(at.set(x, y, z));
            if (!held.getFluidState().isEmpty()) { return false; }
            if (CityPlotGround.solid(held)) { return this.level - y > CityPlotGround.FILL_UNDER; }
        }
        return false;
    }

    private void frames(WorldGenLevel level, StructureManager manager, ChunkPos chunk, BoundingBox box, CityCross cross, List<CityRails.Laid> bores) {
        if (frames.length == 0) { return; }
        CityPalette posts = CityPalette.mixed(ContentCity.frameBlock());
        if (posts == null) { return; }
        BlockState support = ContentCity.support(plan(CityGround.of(level)));
        CityPalette beams = ContentCity.frameTopBlock().isEmpty() ? posts : CityPalette.of(ContentCity.frameTopBlock(), posts.first());
        long seed = level.getSeed();
        int clear = ContentCity.frameHeight();
        List<BoundingBox> piers = ContentCityTrees.kin(manager, chunk, this, getBoundingBox(), ContentCityPierPiece.class::isInstance);
        List<BoundingBox> plazas = ContentCityTrees.kinPieces(manager, chunk, this, getBoundingBox(), ContentCityPlazaPiece.class::isInstance).stream().map(ContentCityPlazaPiece.class::cast).map(ContentCityPlazaPiece::reached).toList();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int along : frames) {
            if (CityPlotGround.covered(piers, alongX ? along : middle, alongX ? middle : along)) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                int across = middle + side * cross.curb();
                int postX = alongX ? along : across;
                int postZ = alongX ? across : along;
                boolean open = !CityPlotGround.covered(plazas, postX, postZ);
                for (int up = 1; up <= clear; up++) {
                    at.set(postX, this.level + up, postZ);
                    if (open && box.isInside(at)) { level.setBlock(at, posts.pick(seed, postX, this.level + up, postZ), Block.UPDATE_CLIENTS); }
                }
                if (width > NARROW && box.isInside(at.set(postX, this.level, postZ))) { CityPlotGround.piling(level, postX, postZ, this.level - 1, support, CityRails.boreRoof(bores, postX, postZ), at); }
            }
            for (int across = middle - cross.curb(); across <= middle + cross.curb(); across++) {
                int beamX = alongX ? along : across;
                int beamZ = alongX ? across : along;
                if (CityPlotGround.covered(plazas, beamX, beamZ)) { continue; }
                at.set(beamX, this.level + clear + 1, beamZ);
                if (box.isInside(at)) { level.setBlock(at, beams.pick(seed, beamX, this.level + clear + 1, beamZ), Block.UPDATE_CLIENTS); }
            }
        }
    }

    private boolean onPiling(int along, int offset) {
        int edge = Math.min(2 + CityPlan.extraWidth(), CityRails.halfWidth(width));
        return edge < 1 || Math.floorMod(along, PILING_ROWS) == 0 && offset == edge;
    }

    private static boolean dashed(int along, int dash) { return dash <= 0 || Math.floorMod(along, dash + 1) != dash; }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return CityPlotGround.layer(paved(), level); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return ContentCity.adaptation(); }

    @Override public int getGroundLevelDelta() { return 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> ContentControl.layOnRoad(roadKeys(), () -> laid(level, manager, chunk, box)));
    }

    @Nullable private JsonObject roadKeys() {
        if (keys.isEmpty() || keysRead) { return roadKeys; }
        keysRead = true;
        try { roadKeys = JsonParser.parseString(keys).getAsJsonObject(); }
        catch (JsonParseException | IllegalStateException unreadable) { ContentLog.LOGGER.error("A city street at {}, {} carries road settings that no longer read as an object, so it is paved with the city's own: {}", getBoundingBox().minX(), getBoundingBox().minZ(), keys); }
        return roadKeys;
    }
}
