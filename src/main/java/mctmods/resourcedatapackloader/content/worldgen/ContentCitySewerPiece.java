package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCitySewerPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCitySewerPiece::new;
    public static final int FLOOR_LEAST = 6;
    public static final int CLEAR_UNDER = 2;
    private static final Direction MANHOLE_BACK = Direction.SOUTH;
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String CROSSINGS = "Cross";
    private static final String WELLS = "Wells";
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final int[] crossings;
    private final int[] wells;

    public ContentCitySewerPiece(int fromX, int fromZ, int toX, int toZ, int level, int middle, boolean alongX, int[] crossings, int[] wells) {
        super(TYPE, 0, box(fromX, fromZ, toX, toZ, level, middle, alongX));
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.crossings = crossings;
        this.wells = wells;
    }

    public ContentCitySewerPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.crossings = tag.getIntArray(CROSSINGS);
        this.wells = tag.getIntArray(WELLS);
    }

    private static BoundingBox box(int fromX, int fromZ, int toX, int toZ, int level, int middle, boolean alongX) {
        int half = ContentCity.sewerWidth() / 2;
        int floor = level - ContentCity.sewerDepth();
        int roof = floor + 2 + ContentCity.sewerHeight();
        int leastX = alongX ? fromX : middle - half;
        int mostX = alongX ? toX : middle + half;
        int leastZ = alongX ? middle - half : fromZ;
        int mostZ = alongX ? middle + half : toZ;
        return new BoundingBox(leastX, floor, leastZ, mostX, Math.max(roof, level), mostZ);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putIntArray(CROSSINGS, crossings);
        tag.putIntArray(WELLS, wells);
    }

    static boolean cramped(int bottom, int street) {
        int floor = street - ContentCity.sewerDepth();
        return floor < bottom + FLOOR_LEAST || floor + ContentCity.sewerHeight() + CLEAR_UNDER >= street;
    }

    private boolean uncrossed(int row, int half) {
        for (int crossing : crossings) { if (Math.abs(row - crossing) <= half) { return false; } }
        return true;
    }

    private boolean withinLoop(int x, int z, int half) {
        int reach = ContentCity.sewerWellEntrance() ? ContentCitySewerLoopPiece.LOOP + half : 0;
        for (int at = 0; at + 3 < wells.length; at += 4) {
            int bandX = x < wells[at] ? wells[at] - x : x > wells[at + 2] ? x - wells[at + 2] : 0;
            int bandZ = z < wells[at + 1] ? wells[at + 1] - z : z > wells[at + 3] ? z - wells[at + 3] : 0;
            if (Math.max(bandX, bandZ) <= reach) { return true; }
        }
        return false;
    }

    static boolean onTrack(WorldGenLevel level, BlockPos.MutableBlockPos at, int x, int y, int z) {
        for (int step = -1; step <= 1; step++) {
            if (level.getBlockState(at.set(x, y + step, z)).getBlock() instanceof BaseRailBlock) { return true; }
        }
        return false;
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState lining = block(ContentCity.sewerBlock());
        if (lining == null) { return; }
        int floor = this.level - ContentCity.sewerDepth();
        int height = ContentCity.sewerHeight();
        if (cramped(level.getMinBuildHeight(), this.level)) { return; }
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState water = CityPalette.stateOr(ContentCity.sewerWaterBlock(), air);
        BlockState walk = CityPalette.stateOr(ContentCity.sewerWalkBlock(), lining);
        BlockState light = block(ContentCity.sewerLightBlock());
        CityPalette moss = CityPalette.mixed(ContentCity.sewerMossBlock());
        BlockState vine = block(ContentCity.sewerVineBlock());
        int mossChance = moss == null ? 0 : ContentCity.sewerMossChance();
        int vineChance = vine == null ? 0 : ContentCity.sewerVineChance();
        int half = ContentCity.sewerWidth() / 2;
        int run = ContentCity.sewerLightRun();
        int roof = floor + 2 + height;
        BoundingBox held = getBoundingBox();
        List<CityRails.Laid> subways = CityRails.subways(CityGround.of(level), held.minX(), held.minZ(), held.maxX(), held.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        int dug = 0;
        int lit = 0;
        int walled = 0;
        for (int row = first; row <= last; row++) {
            if (CityBiome.moved(level, alongX ? row : middle, alongX ? middle : row)) {
                lining = CityPalette.stateOr(ContentCity.sewerBlock(), lining);
                water = CityPalette.stateOr(ContentCity.sewerWaterBlock(), air);
                walk = CityPalette.stateOr(ContentCity.sewerWalkBlock(), lining);
                light = block(ContentCity.sewerLightBlock());
                moss = CityPalette.mixed(ContentCity.sewerMossBlock());
                vine = block(ContentCity.sewerVineBlock());
                mossChance = moss == null ? 0 : ContentCity.sewerMossChance();
                vineChance = vine == null ? 0 : ContentCity.sewerVineChance();
            }
            boolean lightRow = light != null && Math.floorMod(row, run) == 0;
            for (int across = middle - half; across <= middle + half; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                at.set(x, floor, z);
                if (!box.isInside(at) || withinLoop(x, z, half)) { continue; }
                boolean fouled = false;
                for (int y = floor; y <= floor + height + 2 && !fouled; y++) { fouled = CityRails.insideBore(subways, x, y, z); }
                if (fouled) {
                    for (int y = floor; y <= floor + height + 2; y++) {
                        if (CityRails.insideBore(subways, x, y, z) || onTrack(level, at, x, y, z)) { continue; }
                        put(level, at.set(x, y, z), lining, lining, moss, mossChance);
                    }
                    walled++;
                    continue;
                }
                boolean edge = (across == middle - half || across == middle + half) && uncrossed(row, half);
                put(level, at.set(x, floor, z), lining, lining, moss, mossChance);
                put(level, at.set(x, floor + 1, z), edge ? lining : across == middle ? water : walk, lining, moss, mossChance);
                Direction wall = across == middle - half + 1 ? (alongX ? Direction.NORTH : Direction.WEST) : across == middle + half - 1 ? (alongX ? Direction.SOUTH : Direction.EAST) : null;
                if (wall != null && !uncrossed(row, half)) { wall = null; }
                for (int y = floor + 2; y <= floor + 1 + height; y++) {
                    boolean hung = !edge && wall != null && vineChance > 0 && RandomSource.create(ContentCityBlocks.spot(x, z) ^ y).nextInt(100) < vineChance;
                    if (hung) { level.setBlock(at.set(x, y, z), clinging(vine, wall), 2); }
                    else { put(level, at.set(x, y, z), edge ? lining : air, lining, moss, mossChance); }
                }
                boolean lamp = lightRow && across == middle;
                put(level, at.set(x, roof, z), lamp ? light : lining, lining, moss, mossChance);
                if (lamp) { lit++; }
                dug++;
            }
        }
        if (dug > 0) { ContentLog.LOGGER.debug("Sewer under the street at {}, {} laid {} column(s) {} block(s) under the grade, {} lit, {} column(s) walled solid where a subway passes through its depth", held.minX(), held.minZ(), dug, ContentCity.sewerDepth(), lit, walled); }
    }

    static boolean shaft(WorldGenLevel level, BoundingBox box, List<CityRails.Laid> subways, int x, int z, int floor, int roof, int top, BlockState lining, BlockPos.MutableBlockPos at) {
        BlockState ladder = block(ContentCity.sewerLadderBlock());
        BlockState cover = cover();
        if (ladder == null && cover == null) { return false; }
        boolean mouth = box.isInside(at.set(x, top, z));
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int y = floor + 2; y <= top; y++) {
            if (y >= roof && y < top) {
                for (Direction side : Direction.Plane.HORIZONTAL) { shaftCell(level, box, subways, at.set(x + side.getStepX(), y, z + side.getStepZ()), lining); }
            }
            else if (y < roof) { shaftCell(level, box, subways, at.set(x + MANHOLE_BACK.getStepX(), y, z + MANHOLE_BACK.getStepZ()), lining); }
            shaftCell(level, box, subways, at.set(x, y, z), y == top ? cover == null ? air : cover : ladder == null ? air : ladder);
        }
        return mouth;
    }

    private static void shaftCell(WorldGenLevel level, BoundingBox box, List<CityRails.Laid> subways, BlockPos at, BlockState laid) {
        if (!box.isInside(at) || CityRails.insideBore(subways, at.getX(), at.getY(), at.getZ())) { return; }
        level.setBlock(at, laid, 2);
    }

    static void put(WorldGenLevel level, BlockPos at, BlockState laid, BlockState lining, @Nullable CityPalette moss, int chance) {
        boolean mossy = chance > 0 && moss != null && laid == lining && RandomSource.create(ContentCityBlocks.spot(at.getX(), at.getZ()) ^ at.getY()).nextInt(100) < chance;
        level.setBlock(at, mossy ? moss.pick(level.getSeed(), at.getX(), at.getY(), at.getZ()) : laid, 2);
    }

    private static BlockState clinging(BlockState vine, Direction wall) {
        var side = wall == Direction.NORTH ? BlockStateProperties.NORTH : wall == Direction.SOUTH ? BlockStateProperties.SOUTH : wall == Direction.WEST ? BlockStateProperties.WEST : BlockStateProperties.EAST;
        if (!vine.hasProperty(side)) { return vine; }
        return vine.setValue(side, Boolean.TRUE);
    }

    @Nullable static BlockState cover() {
        BlockState asked = block(ContentCity.sewerCoverBlock());
        if (asked == null) { return null; }
        ContentCity.ironCover(asked);
        if (!asked.hasProperty(TrapDoorBlock.HALF)) { return asked; }
        return asked.setValue(TrapDoorBlock.HALF, Half.TOP);
    }

    @Nullable static BlockState block(String named) { return CityPalette.state(named); }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, box));
    }
}
