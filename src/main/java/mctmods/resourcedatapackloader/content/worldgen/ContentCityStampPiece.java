package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityStampPiece extends StructurePiece implements ContentCityTrees.Felling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityStampPiece::new;
    private static final int PASSAGE_MOST = 32;
    private static final int DOOR = 7;
    private static final int HEADROOM = 2;
    private static final int HEAD_CLEARANCE = 8;
    private static final String LEVEL = "Level";
    private static final String TOP = "Top";
    private static final String ROW = "Row";
    private static final String NEAR = "Near";
    private static final String WAY = "Way";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String BED_HALF = "Bed";
    private static final String SPAN_X = "SpanX";
    private static final String SPAN_Z = "SpanZ";
    private static final String EDGE = "Edge";
    private static final String DECK = "Deck";
    private final int level;
    private final int top;
    private final int row;
    private final int near;
    private final int way;
    private final int middle;
    private final boolean alongX;
    private final int bedHalf;
    private final int spanX;
    private final int spanZ;
    private final int edge;
    private final int deck;

    public ContentCityStampPiece(int level, int top, int row, int near, int way, int middle, boolean alongX, int bedHalf, int spanX, int spanZ, int edge, int deck) {
        super(TYPE, 0, box(level, top, row, near, way, middle, alongX, spanX, spanZ));
        this.spanX = spanX;
        this.spanZ = spanZ;
        this.level = level;
        this.top = top;
        this.row = row;
        this.near = near;
        this.way = way;
        this.middle = middle;
        this.alongX = alongX;
        this.bedHalf = bedHalf;
        this.edge = edge;
        this.deck = deck;
    }

    public ContentCityStampPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.top = tag.getInt(TOP);
        this.row = tag.getInt(ROW);
        this.near = tag.getInt(NEAR);
        this.way = tag.getInt(WAY);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.bedHalf = tag.getInt(BED_HALF);
        this.spanX = tag.getInt(SPAN_X);
        this.spanZ = tag.getInt(SPAN_Z);
        this.edge = tag.contains(EDGE) ? tag.getInt(EDGE) : ContentCityStairsPiece.wall(near, way);
        this.deck = tag.getInt(DECK);
    }

    private static BoundingBox box(int level, int top, int row, int near, int way, int middle, boolean alongX, int spanX, int spanZ) {
        BoundingBox stairs = ContentCityStairsPiece.box(level, top, row, near, way, middle, alongX);
        int leastX = stairs.minX();
        int mostX = stairs.maxX();
        int leastZ = stairs.minZ();
        int mostZ = stairs.maxZ();
        int reachX = alongX ? spanX : spanZ;
        int reachZ = alongX ? spanZ : spanX;
        int anchorX = alongX ? row - 1 : near - 1;
        int anchorZ = alongX ? near - 1 : row - 1;
        if (reachX > 0 && reachZ > 0) {
            leastX = Math.min(leastX, anchorX);
            mostX = Math.max(mostX, anchorX + reachX - 1);
            leastZ = Math.min(leastZ, anchorZ);
            mostZ = Math.max(mostZ, anchorZ + reachZ - 1);
        }
        return new BoundingBox(leastX, level, leastZ, mostX, Math.max(stairs.maxY(), top + HEAD_CLEARANCE), mostZ);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(TOP, top);
        tag.putInt(ROW, row);
        tag.putInt(NEAR, near);
        tag.putInt(WAY, way);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putInt(BED_HALF, bedHalf);
        tag.putInt(SPAN_X, spanX);
        tag.putInt(SPAN_Z, spanZ);
        tag.putInt(EDGE, edge);
        tag.putInt(DECK, deck);
    }

    private static Rotation turn(boolean alongX, int way) {
        if (alongX) { return way > 0 ? Rotation.NONE : Rotation.CLOCKWISE_180; }
        return way > 0 ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
    }

    private static BlockPos origin(Vec3i size, Rotation turn, boolean alongX, int row, int near, int base) {
        BlockPos front = StructureTemplate.transform(BlockPos.ZERO, Mirror.NONE, turn, BlockPos.ZERO);
        BlockPos back = StructureTemplate.transform(new BlockPos(size.getX() - 1, 0, size.getZ() - 1), Mirror.NONE, turn, BlockPos.ZERO);
        return new BlockPos((alongX ? row - 1 : near - 1) - Math.min(front.getX(), back.getX()), base, (alongX ? near - 1 : row - 1) - Math.min(front.getZ(), back.getZ()));
    }

    private static int platformWall(int middle, int way, int bedHalf) { return middle + way * (bedHalf + ContentCity.platformWidth() + 1); }

    private static int corridor(boolean alongX, BlockPos mouth, int wall, int level) { return Math.abs((alongX ? mouth.getZ() : mouth.getX()) - wall) + turnBack(alongX, mouth, wall, level) + 2; }

    private static int turnBack(boolean alongX, BlockPos mouth, int wall, int level) { return Math.max(0, mouth.getY() - (level + 2) - Math.abs((alongX ? mouth.getZ() : mouth.getX()) - wall)); }

    private static List<BlockPos> mouths(Rotation turn, BlockPos origin) {
        List<BlockPos> mouths = new ArrayList<>();
        for (int door = DOOR; door <= DOOR + 1; door++) { mouths.add(StructureTemplate.transform(new BlockPos(door, 1, 0), Mirror.NONE, turn, BlockPos.ZERO).offset(origin)); }
        return mouths;
    }

    @Nullable static String unreachable(Vec3i size, int level, int top, int row, int near, int way, int middle, boolean alongX, int bedHalf) {
        if (top == Integer.MIN_VALUE) { return "no ground stands over its stair head"; }
        if (top - level < ContentCityStairsPiece.CLIMB_LEAST) { return "the street stands only " + (top - level) + " block(s) over the platform"; }
        Shaft shaft = Shaft.of(size.getY(), level, top);
        if (shaft.fallsShort(level)) { return "the build grown to " + shaft.grown() + " block(s) cannot make the climb of " + (top - level) + " from y" + level + " to the street"; }
        Rotation turn = turn(alongX, way);
        List<BlockPos> mouths = mouths(turn, origin(size, turn, alongX, row, near, shaft.base()));
        int wall = platformWall(middle, way, bedHalf);
        int length = corridor(alongX, mouths.getFirst(), wall, level);
        if (length > PASSAGE_MOST) { return "its corridor to the platform would run " + length + " block(s), past the " + PASSAGE_MOST + " one is dug"; }
        int back = turnBack(alongX, mouths.getFirst(), wall, level);
        int heart = row + ContentCityStairsPiece.RUN / 2;
        for (BlockPos mouth : mouths) {
            int landing = (alongX ? mouth.getX() : mouth.getZ()) - back;
            if (Math.abs(landing - heart) > ContentCity.stationLength() / 2) { return "its corridor would turn back " + back + " block(s) along the line and come down at row " + landing + ", past the end of the platform"; }
        }
        return null;
    }

    private record Shaft(int foot, int repeat, int cap, int copies, int grown, int base) {
        private static Shaft of(int tall, int level, int top) {
            int foot = Math.min(ContentCity.stationFoot(), tall);
            int repeat = ContentCity.stationRepeat();
            int cap = tall - foot - repeat;
            int copies = 1;
            if (repeat > 0 && cap >= 0) { copies = Math.max(1, (top - level - foot - cap) / repeat); }
            else {
                repeat = 0;
                cap = 0;
                foot = tall;
            }
            int grown = foot + repeat * copies + cap;
            return new Shaft(foot, repeat, cap, copies, grown, top - grown + 1);
        }

        private boolean fallsShort(int level) { return base < level + 1 || base - (level + 1) > PASSAGE_MOST; }
    }

    @Override @Nonnull public BoundingBox stood() {
        BoundingBox well = ContentCityStairsPiece.well(level, top, row, near, alongX);
        int reachX = alongX ? spanX : spanZ;
        int reachZ = alongX ? spanZ : spanX;
        if (reachX <= 0 || reachZ <= 0) { return well; }
        return new BoundingBox(well.minX(), level, well.minZ(), Math.max(well.maxX(), well.minX() + reachX - 1), getBoundingBox().maxY(), Math.max(well.maxZ(), well.minZ() + reachZ - 1));
    }

    @Override public int fellFloor() { return top - 1; }

    private void stamp(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkPos chunk, @Nonnull RandomSource random, @Nonnull BoundingBox box) {
        long seed = level.getSeed();
        CityPalette linings = CityPalette.of(ContentCity.railTunnelBlock(true), Blocks.AIR.defaultBlockState());
        BlockState lining = linings.first();
        String named = ContentCity.stationStructure();
        ResourceLocation key = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        StructureTemplate held = key == null ? null : level.getLevel().getServer().getStructureManager().get(key).orElse(null);
        BoundingBox stood = getBoundingBox();
        if (held == null) {
            ContentCity.missingStation(named);
            return;
        }
        Vec3i size = held.getSize();
        String why = unreachable(size, this.level, top, row, near, way, middle, alongX, bedHalf);
        if (why != null) {
            ContentLog.LOGGER.error("The subway station at {}, {} on the line at {} was kept when its city was planned, but its stairs cannot be laid: {}", alongX ? row : near, alongX ? near : row, middle, why);
            return;
        }
        Shaft shaft = Shaft.of(size.getY(), this.level, top);
        int foot = shaft.foot();
        int repeat = shaft.repeat();
        int cap = shaft.cap();
        int copies = shaft.copies();
        int grown = shaft.grown();
        int base = shaft.base();
        int felled = ContentCityTrees.fellAround(level, manager, chunk, this, box);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the station build at {}, {} was stamped", felled, stood.minX(), stood.minZ()); }
        Rotation turn = turn(alongX, way);
        BlockPos origin = origin(size, turn, alongX, row, near, base);
        int leastX = origin.getX();
        int leastZ = origin.getZ();
        band(level, box, held, lining, turn, random, leastX, base, leastZ, base, base + foot - 1);
        for (int copy = 0; copy < copies; copy++) {
            int from = base + foot + copy * repeat;
            band(level, box, held, lining, turn, random, leastX, base + copy * repeat, leastZ, from, from + repeat - 1);
        }
        if (cap > 0) {
            int from = base + foot + copies * repeat;
            band(level, box, held, lining, turn, random, leastX, base + (copies - 1) * repeat, leastZ, from, from + cap - 1);
        }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int packed = 0;
        for (int x = 0; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                BlockPos spot = StructureTemplate.transform(new BlockPos(x, 0, z), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
                for (int y = this.level; y < base; y++) {
                    at.set(spot.getX(), y, spot.getZ());
                    if (!box.isInside(at)) { continue; }
                    level.setBlock(at, lining, 2);
                    packed++;
                }
            }
        }
        int lit = lamps(level, box, size, origin, turn, base, grown, at);
        int opened = headroom(level, box, size, turn, origin, at);
        int bored = bore(level, box, linings, seed, size, turn, origin);
        int railed = railing(level, box, held, turn, origin, at);
        int seated = bench(level, box, turn, origin, at);
        ContentLog.LOGGER.debug("A subway station is laid from the build '{}' at {}, {}, floor y {} to street y {}, {} block(s) of packing under it, {} lamp(s) up the shaft, {} block(s) cleared over its head, {} block(s) of corridor to the platform, {} of railing and {} of bench at its head", named, origin.getX(), origin.getZ(), base, top, packed, lit, opened, bored, railed, seated);
        if (copies > 1) { ContentLog.LOGGER.debug("The build '{}' is {} tall and the climb is {}, so its {} block band is laid {} times: the shaft stands {} block(s) tall", named, size.getY(), top - this.level, repeat, copies, grown); }
        int graded = ContentCityStairsPiece.approach(level, box, top, row, near, way, edge, deck, alongX);
        if (graded > 0) { ContentLog.LOGGER.debug("Graded {} block(s) between the street and the subway station at {}, {} so it is entered at y {}", graded, stood.minX(), stood.minZ(), top); }
    }

    private void band(WorldGenLevel level, BoundingBox box, StructureTemplate held, BlockState lining, Rotation turn, RandomSource random, int leastX, int at, int leastZ, int from, int to) {
        if (to < from) { return; }
        BoundingBox only = new BoundingBox(box.minX(), Math.max(box.minY(), from), box.minZ(), box.maxX(), Math.min(box.maxY(), to), box.maxZ());
        if (only.minY() > only.maxY()) { return; }
        StructurePlaceSettings how = new StructurePlaceSettings();
        how.setRotation(turn);
        how.setMirror(Mirror.NONE);
        how.setBoundingBox(only);
        how.setIgnoreEntities(false);
        how.addProcessor(new ContentCityStationBlocks(lining));
        held.placeInWorld(level, new BlockPos(leastX, at, leastZ), new BlockPos(leastX, at, leastZ), how, random, 2);
    }

    private int headroom(WorldGenLevel level, BoundingBox box, Vec3i size, Rotation turn, BlockPos origin, BlockPos.MutableBlockPos at) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int cleared = 0;
        for (int x = 0; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                BlockPos spot = StructureTemplate.transform(new BlockPos(x, 0, z), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
                if (!box.isInside(at.set(spot.getX(), top + 1, spot.getZ()))) { continue; }
                for (int y = top + 1; y <= top + HEAD_CLEARANCE; y++) {
                    BlockState above = level.getBlockState(at.set(spot.getX(), y, spot.getZ()));
                    if (above.isAir()) { continue; }
                    if (!CityPlotGround.terrain(above) && CityPlotGround.solid(above)) { break; }
                    level.setBlock(at, air, 2);
                    cleared++;
                }
            }
        }
        return cleared;
    }

    private int bore(WorldGenLevel level, BoundingBox box, CityPalette linings, long seed, Vec3i size, Rotation turn, BlockPos origin) {
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState lamp = CityPalette.state(ContentCity.railTunnelLightBlock(true));
        int run = ContentCity.railTunnelLightRun(true);
        int wall = platformWall(middle, way, bedHalf);
        int target = this.level + 2;
        List<BlockPos> mouths = mouths(turn, origin);
        BlockPos first = mouths.getFirst();
        int fromAcross = alongX ? first.getZ() : first.getX();
        int extra = turnBack(alongX, first, wall, this.level);
        if (corridor(alongX, first, wall, this.level) > PASSAGE_MOST) { return 0; }
        if (extra > 0) { ContentLog.LOGGER.debug("The station corridor turns back along the line for {} block(s) to fall {} onto the platform, {} straight across would not have been enough", extra, first.getY() - target, Math.abs(fromAcross - wall)); }

        BlockPos near = StructureTemplate.transform(BlockPos.ZERO, Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
        BlockPos far = StructureTemplate.transform(new BlockPos(size.getX() - 1, 0, size.getZ() - 1), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
        int buildLeastX = Math.min(near.getX(), far.getX());
        int buildMostX = Math.max(near.getX(), far.getX());
        int buildLeastZ = Math.min(near.getZ(), far.getZ());
        int buildMostZ = Math.max(near.getZ(), far.getZ());
        List<int[]> dug = new ArrayList<>();
        Set<Long> taken = new HashSet<>();
        for (BlockPos mouth : mouths) {
            int across = fromAcross;
            int here = alongX ? mouth.getX() : mouth.getZ();
            int walk = mouth.getY();
            List<int[]> path = new ArrayList<>();
            path.add(new int[] {here, across});
            if (extra > 0) {
                across -= way;
                path.add(new int[] {here, across});
                for (int step = 1; step <= extra; step++) { path.add(new int[] {here - step, across}); }
                here -= extra;
            }
            while (way > 0 ? across - way >= wall : across - way <= wall) {
                across -= way;
                path.add(new int[] {here, across});
            }
            boolean start = true;
            for (int[] spot : path) {
                if (!start && walk > target) { walk--; }
                start = false;
                dug.add(new int[] {spot[0], spot[1], walk, mouth.getY()});
                taken.add(packed(spot[0], spot[1]));
            }
        }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int laid = 0;
        for (int[] spot : dug) {
            int x = alongX ? spot[0] : spot[1];
            int z = alongX ? spot[1] : spot[0];
            boolean lit = lamp != null && Math.floorMod(spot[0] + spot[1], run) == 0;
            for (int y = spot[2] - 1; y <= spot[3] + HEADROOM + 1; y++) {
                if (!box.isInside(at.set(x, y, z))) { continue; }
                boolean roof = y > spot[2] + HEADROOM;
                boolean shell = y == spot[2] - 1 || roof;
                level.setBlock(at, roof && lit ? lamp : shell ? linings.pick(seed, x, y, z) : air, 2);
                laid++;
            }
        }
        for (int[] spot : dug) {
            for (int side = 0; side < 4; side++) {
                int nextAlong = spot[0] + (side == 0 ? 1 : side == 1 ? -1 : 0);
                int nextAcross = spot[1] + (side == 2 ? 1 : side == 3 ? -1 : 0);
                if (taken.contains(packed(nextAlong, nextAcross))) { continue; }
                if (way > 0 ? nextAcross < wall : nextAcross > wall) { continue; }
                int x = alongX ? nextAlong : nextAcross;
                int z = alongX ? nextAcross : nextAlong;
                if (x >= buildLeastX && x <= buildMostX && z >= buildLeastZ && z <= buildMostZ) { continue; }
                for (int y = spot[2] - 1; y <= spot[3] + HEADROOM + 1; y++) {
                    if (!box.isInside(at.set(x, y, z))) { continue; }
                    if (level.getBlockState(at).getBlock() instanceof BaseRailBlock) { continue; }
                    level.setBlock(at, linings.pick(seed, x, y, z), 2);
                    laid++;
                }
            }
        }
        return laid;
    }

    private int railing(WorldGenLevel level, BoundingBox box, StructureTemplate held, Rotation turn, BlockPos origin, BlockPos.MutableBlockPos at) {
        CityPalette rails = CityPalette.mixed(ContentCity.railingBlock());
        if (rails == null) { return 0; }
        long seed = level.getSeed();
        boolean[][] open = topOpening(level, held);
        int leastOpen = Integer.MAX_VALUE;
        for (int x = 0; x < open.length; x++) {
            for (int z = 0; z < open[x].length; z++) {
                if (open[x][z]) { leastOpen = Math.min(leastOpen, x); }
            }
        }
        if (leastOpen == Integer.MAX_VALUE) { return 0; }
        int laid = 0;
        for (int x = leastOpen; x < open.length; x++) {
            for (int z = 0; z < open[x].length; z++) {
                if (open[x][z] || !beside(open, x, z)) { continue; }
                BlockPos spot = StructureTemplate.transform(new BlockPos(x, 0, z), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
                if (!box.isInside(at.set(spot.getX(), top + 1, spot.getZ()))) { continue; }
                if (CityPlotGround.solid(level.getBlockState(at))) { continue; }
                level.setBlock(at, rails.pick(seed, spot.getX(), top + 1, spot.getZ()), 2);
                level.getChunk(at).markPosForPostprocessing(at);
                laid++;
            }
        }
        return laid;
    }

    private static boolean[][] topOpening(WorldGenLevel level, StructureTemplate held) {
        Vec3i size = held.getSize();
        boolean[][] open = new boolean[size.getX()][size.getZ()];
        CompoundTag saved = held.save(new CompoundTag());
        ListTag palette = saved.getList("palette", Tag.TAG_COMPOUND);
        ListTag blocks = saved.getList("blocks", Tag.TAG_COMPOUND);
        HolderGetter<Block> lookup = level.holderLookup(Registries.BLOCK);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompound(i);
            ListTag pos = block.getList("pos", Tag.TAG_INT);
            if (pos.getInt(1) != size.getY() - 1) { continue; }
            int x = pos.getInt(0);
            int z = pos.getInt(2);
            if (x < 0 || z < 0 || x >= size.getX() || z >= size.getZ()) { continue; }
            open[x][z] = !CityPlotGround.solid(NbtUtils.readBlockState(lookup, palette.getCompound(block.getInt("state"))));
        }
        return open;
    }

    private static boolean beside(boolean[][] open, int x, int z) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int nextX = x + dx;
                int nextZ = z + dz;
                if ((dx != 0 || dz != 0) && nextX >= 0 && nextZ >= 0 && nextX < open.length && nextZ < open[nextX].length && open[nextX][nextZ]) { return true; }
            }
        }
        return false;
    }

    private int bench(WorldGenLevel level, BoundingBox box, Rotation turn, BlockPos origin, BlockPos.MutableBlockPos at) {
        BlockPos seatFrom = StructureTemplate.transform(new BlockPos(DOOR - 3, 0, 1), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
        BlockPos seatTo = StructureTemplate.transform(new BlockPos(DOOR + 1, 0, 1), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
        boolean seatAlongX = seatFrom.getZ() == seatTo.getZ();
        int from = Math.min(seatAlongX ? seatFrom.getX() : seatFrom.getZ(), seatAlongX ? seatTo.getX() : seatTo.getZ());
        int across = seatAlongX ? seatFrom.getZ() : seatFrom.getX();
        return ContentCityStairsPiece.bench(level, box, seatAlongX, from, across, top + 1, ContentCityStairsPiece.awayFrom(alongX, way), at);
    }

    private static long packed(int along, int across) { return ((long) along << 32) ^ (across & 0xFFFFFFFFL); }

    private int lamps(WorldGenLevel level, BoundingBox box, Vec3i size, BlockPos origin, Rotation turn, int base, int grown, BlockPos.MutableBlockPos at) {
        BlockState lamp = CityPalette.state(ContentCity.railTunnelLightBlock(true));
        if (lamp == null) { return 0; }
        int run = ContentCity.railTunnelLightRun(true);
        int lit = 0;
        for (int along : new int[] {size.getX() / 3, size.getX() * 2 / 3}) {
            BlockPos mid = StructureTemplate.transform(new BlockPos(along, 0, (size.getZ() - 1) / 2), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
            for (int y = base + 2; y < base + grown - 1; y += run) {
                at.set(mid.getX(), y, mid.getZ());
                if (!box.isInside(at)) { continue; }
                level.setBlock(at, lamp, 2);
                lit++;
            }
        }
        return lit;
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> stamp(level, manager, chunk, random, box));
    }
}
