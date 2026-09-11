package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.core.registries.BuiltInRegistries;

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
    private static final String BORES = "Bores";
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final int[] crossings;
    private final int[] bores;

    public ContentCitySewerPiece(int fromX, int fromZ, int toX, int toZ, int level, int middle, boolean alongX, int[] crossings, int[] bores) {
        super(TYPE, 0, box(fromX, fromZ, toX, toZ, level, middle, alongX));
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.crossings = crossings;
        this.bores = bores;
    }

    public ContentCitySewerPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.crossings = tag.getIntArray(CROSSINGS);
        this.bores = tag.getIntArray(BORES);
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
        tag.putIntArray(BORES, bores);
    }

    private boolean insideBore(int row, int y) {
        for (int at = 0; at + 3 < bores.length; at += 4) {
            if (Math.abs(row - bores[at]) > bores[at + 3]) { continue; }
            if (y >= bores[at + 1] && y <= bores[at + 2]) { return true; }
        }
        return false;
    }

    private boolean foulsBore(int row, int floor, int height) {
        for (int y = floor; y <= floor + height + 2; y++) { if (insideBore(row, y)) { return true; } }
        return false;
    }

    private boolean uncrossed(int row, int half) {
        for (int crossing : crossings) { if (Math.abs(row - crossing) <= half) { return false; } }
        return true;
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState lining = block(ContentCity.sewerBlock());
        if (lining == null) { return; }
        int floor = this.level - ContentCity.sewerDepth();
        int height = ContentCity.sewerHeight();
        if (floor < level.getMinBuildHeight() + FLOOR_LEAST) { return; }
        if (floor + height + CLEAR_UNDER >= this.level) { return; }
        BlockState water = block(ContentCity.sewerWaterBlock());
        BlockState walk = stateOr(ContentCity.sewerWalkBlock(), lining);
        BlockState light = block(ContentCity.sewerLightBlock());
        BlockState moss = block(ContentCity.sewerMossBlock());
        BlockState vine = block(ContentCity.sewerVineBlock());
        BlockState air = Blocks.AIR.defaultBlockState();
        int mossChance = moss == null ? 0 : ContentCity.sewerMossChance();
        int vineChance = vine == null ? 0 : ContentCity.sewerVineChance();
        int half = ContentCity.sewerWidth() / 2;
        int run = ContentCity.sewerLightRun();
        int roof = floor + 2 + height;
        BoundingBox held = getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        int dug = 0;
        int lit = 0;
        int walled = 0;
        for (int row = first; row <= last; row++) {
            if (CityBiome.moved(level, alongX ? row : middle, alongX ? middle : row)) {
                lining = stateOr(ContentCity.sewerBlock(), lining);
                water = block(ContentCity.sewerWaterBlock());
                walk = stateOr(ContentCity.sewerWalkBlock(), lining);
                light = block(ContentCity.sewerLightBlock());
                moss = block(ContentCity.sewerMossBlock());
                vine = block(ContentCity.sewerVineBlock());
                mossChance = moss == null ? 0 : ContentCity.sewerMossChance();
                vineChance = vine == null ? 0 : ContentCity.sewerVineChance();
            }
            boolean lightRow = light != null && Math.floorMod(row, run) == 0;
            for (int across = middle - half; across <= middle + half; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                at.set(x, floor, z);
                if (!box.isInside(at)) { continue; }
                if (foulsBore(row, floor, height)) {
                    for (int y = floor; y <= floor + height + 2; y++) {
                        if (insideBore(row, y)) { continue; }
                        at.set(x, y, z);
                        if (level.getBlockState(at).getBlock() instanceof BaseRailBlock) { continue; }
                        put(level, at, lining, lining, moss, mossChance);
                    }
                    walled++;
                    continue;
                }
                boolean edge = (across == middle - half || across == middle + half) && uncrossed(row, half);
                put(level, at.set(x, floor, z), lining, lining, moss, mossChance);
                put(level, at.set(x, floor + 1, z), edge ? lining : across == middle && water != null ? water : walk, lining, moss, mossChance);
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

    static boolean shaft(WorldGenLevel level, BoundingBox box, int x, int z, int floor, int roof, int top, BlockState lining, BlockPos.MutableBlockPos at) {
        BlockState ladder = block(ContentCity.sewerLadderBlock());
        BlockState cover = cover();
        if (ladder == null && cover == null) { return false; }
        boolean mouth = box.isInside(at.set(x, top, z));
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int y = floor + 2; y <= top; y++) {
            if (y >= roof && y < top) {
                for (Direction side : Direction.Plane.HORIZONTAL) {
                    at.set(x + side.getStepX(), y, z + side.getStepZ());
                    if (box.isInside(at)) { level.setBlock(at, lining, 2); }
                }
            }
            else if (y < roof) {
                at.set(x + MANHOLE_BACK.getStepX(), y, z + MANHOLE_BACK.getStepZ());
                if (box.isInside(at)) { level.setBlock(at, lining, 2); }
            }
            if (box.isInside(at.set(x, y, z))) { level.setBlock(at, y == top ? cover == null ? air : cover : ladder == null ? air : ladder, 2); }
        }
        return mouth;
    }

    static void put(WorldGenLevel level, BlockPos at, BlockState laid, BlockState lining, @Nullable BlockState moss, int chance) {
        boolean mossy = chance > 0 && moss != null && laid == lining && RandomSource.create(ContentCityBlocks.spot(at.getX(), at.getZ()) ^ at.getY()).nextInt(100) < chance;
        level.setBlock(at, mossy ? moss : laid, 2);
    }

    private static BlockState clinging(BlockState vine, Direction wall) {
        var side = wall == Direction.NORTH ? BlockStateProperties.NORTH : wall == Direction.SOUTH ? BlockStateProperties.SOUTH : wall == Direction.WEST ? BlockStateProperties.WEST : BlockStateProperties.EAST;
        if (!vine.hasProperty(side)) { return vine; }
        return vine.setValue(side, Boolean.TRUE);
    }

    @Nullable static BlockState cover() {
        BlockState asked = block(ContentCity.sewerCoverBlock());
        if (asked == null || !asked.hasProperty(TrapDoorBlock.HALF)) { return asked; }
        return asked.setValue(TrapDoorBlock.HALF, Half.TOP);
    }

    @Nullable static BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(named));
        return found == null ? null : found.defaultBlockState();
    }

    static BlockState stateOr(String named, BlockState fallback) {
        BlockState found = block(named);
        return found == null ? fallback : found;
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
