package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityStampPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityStampPiece::new;
    public static final int PASSAGE_MOST = 32;
    private static final int DOOR = 7;
    private static final int HEADROOM = 3;
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

    public ContentCityStampPiece(int level, int top, int row, int near, int way, int middle, boolean alongX, int bedHalf, int spanX, int spanZ) {
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
    }

    private static BoundingBox box(int level, int top, int row, int near, int way, int middle, boolean alongX, int spanX, int spanZ) {
        int wall = middle + way * PASSAGE_MOST;
        int leastAcross = Math.min(Math.min(near - 1, near + ContentCityStairsPiece.WIDE), Math.min(middle, wall));
        int mostAcross = Math.max(Math.max(near - 1, near + ContentCityStairsPiece.WIDE), Math.max(middle, wall));
        int leastAlong = row - 1;
        int mostAlong = row + ContentCityStairsPiece.RUN + 1;
        int leastX = alongX ? leastAlong : leastAcross;
        int mostX = alongX ? mostAlong : mostAcross;
        int leastZ = alongX ? leastAcross : leastAlong;
        int mostZ = alongX ? mostAcross : mostAlong;
        int reachX = alongX ? spanX : spanZ;
        int reachZ = alongX ? spanZ : spanX;
        int anchorX = alongX ? row : near;
        int anchorZ = alongX ? near : row;
        if (reachX > 0 && reachZ > 0) {
            leastX = Math.min(leastX, anchorX);
            mostX = Math.max(mostX, anchorX + reachX - 1);
            leastZ = Math.min(leastZ, anchorZ);
            mostZ = Math.max(mostZ, anchorZ + reachZ - 1);
        }
        return new BoundingBox(leastX, level, leastZ, mostX, top + 2, mostZ);
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
    }

    private Rotation turn() {
        if (alongX) { return way > 0 ? Rotation.NONE : Rotation.CLOCKWISE_180; }
        return way > 0 ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull RandomSource random, @Nonnull BoundingBox box) {
        BlockState lining = block(ContentCity.railTunnelBlock(true));
        if (lining == null) { return; }
        String named = ContentCity.stationStructure();
        ResourceLocation key = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        StructureTemplate held = key == null ? null : level.getLevel().getServer().getStructureManager().get(key).orElse(null);
        if (held == null) {
            if (key != null) { ContentCity.missingStation(named); }
            return;
        }
        BoundingBox stood = getBoundingBox();
        int felled = ContentCityTrees.fellAround(level, stood, box, this.level - 1, stood.maxY(), 2);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the station build at {}, {} was stamped", felled, stood.minX(), stood.minZ()); }
        Vec3i size = held.getSize();
        int tall = size.getY();
        int foot = Math.min(ContentCity.stationFoot(), tall);
        int repeat = ContentCity.stationRepeat();
        int cap = tall - foot - repeat;
        int copies = 1;
        if (repeat > 0 && cap >= 0) { copies = Math.max(1, (top - this.level - foot - cap) / repeat); }
        else {
            repeat = 0;
            cap = 0;
            foot = tall;
        }
        int grown = foot + repeat * copies + cap;
        int base = top - grown + 1;
        if (base < this.level + 1 || base - (this.level + 1) > PASSAGE_MOST) {
            ContentCity.stationTooShort(named, tall, top - this.level, grown);
            return;
        }
        Rotation turn = turn();
        BlockPos front = StructureTemplate.transform(BlockPos.ZERO, Mirror.NONE, turn, BlockPos.ZERO);
        BlockPos back = StructureTemplate.transform(new BlockPos(size.getX() - 1, 0, size.getZ() - 1), Mirror.NONE, turn, BlockPos.ZERO);
        int leastX = (alongX ? row : near) - Math.min(front.getX(), back.getX());
        int leastZ = (alongX ? near : row) - Math.min(front.getZ(), back.getZ());
        BlockPos origin = new BlockPos(leastX, base, leastZ);
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
        int bored = bore(level, box, lining, size, turn, origin);
        int railed = railing(level, box, held, size, turn, origin, at);
        int seated = bench(level, box, turn, origin, at);
        int benched = ContentCityStairsPiece.platformBench(level, box, alongX, row, middle, this.level, bedHalf, way, at);
        ContentLog.LOGGER.debug("A subway station is laid from the build '{}' at {}, {}, floor y {} to street y {}, {} block(s) of packing under it, {} lamp(s) up the shaft, {} block(s) of corridor to the platform, {} of railing and {} of bench at its head, {} of bench on the platform", named, origin.getX(), origin.getZ(), base, top, packed, lit, bored, railed, seated, benched);
    }

    private void band(WorldGenLevel level, BoundingBox box, StructureTemplate held, BlockState lining, Rotation turn, RandomSource random, int leastX, int at, int leastZ, int from, int to) {
        if (to < from) { return; }
        BoundingBox only = new BoundingBox(box.minX(), Math.max(box.minY(), from), box.minZ(), box.maxX(), Math.min(box.maxY(), to), box.maxZ());
        if (only.minY() > only.maxY()) { return; }
        StructurePlaceSettings how = new StructurePlaceSettings();
        how.setRotation(turn);
        how.setMirror(Mirror.NONE);
        how.setBoundingBox(only);
        how.setIgnoreEntities(true);
        how.addProcessor(new ContentCityStationBlocks(lining));
        held.placeInWorld(level, new BlockPos(leastX, at, leastZ), new BlockPos(leastX, at, leastZ), how, random, 2);
    }

    private int bore(WorldGenLevel level, BoundingBox box, BlockState lining, Vec3i size, Rotation turn, BlockPos origin) {
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState lamp = block(ContentCity.railTunnelLightBlock(true));
        int run = ContentCity.railTunnelLightRun(true);
        int wall = middle + way * (bedHalf + ContentCity.platformWidth() + 1);
        int target = this.level + 2;
        List<BlockPos> mouths = new ArrayList<>();
        for (int door = DOOR; door <= DOOR + 1; door++) { mouths.add(StructureTemplate.transform(new BlockPos(door, 1, 0), Mirror.NONE, turn, BlockPos.ZERO).offset(origin)); }
        BlockPos first = mouths.get(0);
        int fromAcross = alongX ? first.getZ() : first.getX();
        int drop = first.getY() - target;
        int straight = Math.abs(fromAcross - wall);
        int extra = Math.max(0, drop - straight);
        if (straight + extra + 2 > PASSAGE_MOST) { return 0; }
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
                level.setBlock(at, roof && lit ? lamp : shell ? lining : air, 2);
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
                    level.setBlock(at, lining, 2);
                    laid++;
                }
            }
        }
        return laid;
    }

    private int railing(WorldGenLevel level, BoundingBox box, StructureTemplate held, Vec3i size, Rotation turn, BlockPos origin, BlockPos.MutableBlockPos at) {
        BlockState rail = block(ContentCity.railingBlock());
        if (rail == null) { return 0; }
        int roof = size.getY() - 1;
        Set<Long> open = new HashSet<>();
        int leastOpen = Integer.MAX_VALUE;
        for (StructureTemplate.StructureBlockInfo info : held.filterBlocks(BlockPos.ZERO, new StructurePlaceSettings(), Blocks.AIR)) {
            if (info.pos().getY() != roof) { continue; }
            open.add(packed(info.pos().getX(), info.pos().getZ()));
            leastOpen = Math.min(leastOpen, info.pos().getX());
        }
        if (open.isEmpty()) { return 0; }
        int laid = 0;
        for (int x = leastOpen; x < size.getX(); x++) {
            for (int z = 0; z < size.getZ(); z++) {
                if (open.contains(packed(x, z)) || !beside(open, x, z)) { continue; }
                BlockPos spot = StructureTemplate.transform(new BlockPos(x, 0, z), Mirror.NONE, turn, BlockPos.ZERO).offset(origin);
                if (!box.isInside(at.set(spot.getX(), top + 1, spot.getZ()))) { continue; }
                if (!level.getBlockState(at).isAir()) { continue; }
                level.setBlock(at, rail, 2);
                level.getChunk(at).markPosForPostprocessing(at);
                laid++;
            }
        }
        return laid;
    }

    private static boolean beside(Set<Long> open, int x, int z) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if ((dx != 0 || dz != 0) && open.contains(packed(x + dx, z + dz))) { return true; }
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
        BlockState lamp = block(ContentCity.railTunnelLightBlock(true));
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

    @Nullable private static BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(named));
        return found == null ? null : found.defaultBlockState();
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, random, box); }
        finally { CityBiome.leave(); }
    }
}
