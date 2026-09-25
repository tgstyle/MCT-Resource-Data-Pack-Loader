package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.List;
import javax.annotation.Nonnull;

public final class ContentCitySewerLoopPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCitySewerLoopPiece::new;
    public static final int LOOP = 5;
    private static final String LEVEL = "Level";
    private static final String WELL = "Well";
    private static final String CROSS_X = "CrossX";
    private static final String CROSS_Z = "CrossZ";
    private static final String KEEP = "Keep";
    private static final String STREETS = "Streets";
    private final int level;
    private final int[] well;
    private final int crossX;
    private final int crossZ;
    private final int[] keep;
    private final int[] streets;

    public ContentCitySewerLoopPiece(int fromX, int fromZ, int toX, int toZ, int level, int crossX, int crossZ, int[] stations, int[] streets) {
        super(TYPE, 0, box(fromX, fromZ, toX, toZ, level));
        this.level = level;
        this.well = new int[] {fromX, fromZ, toX, toZ};
        this.crossX = crossX;
        this.crossZ = crossZ;
        this.keep = CityPlotGround.within(getBoundingBox(), stations);
        this.streets = streets;
    }

    public ContentCitySewerLoopPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        int[] held = tag.getIntArray(WELL);
        this.well = held.length == 4 ? held : new int[4];
        this.crossX = tag.getInt(CROSS_X);
        this.crossZ = tag.getInt(CROSS_Z);
        this.keep = tag.getIntArray(KEEP);
        this.streets = tag.getIntArray(STREETS);
    }

    private static BoundingBox box(int fromX, int fromZ, int toX, int toZ, int level) {
        int reach = LOOP + ContentCity.sewerWidth() / 2;
        int floor = level - ContentCity.sewerDepth();
        int roof = floor + 2 + ContentCity.sewerHeight();
        return new BoundingBox(fromX - reach, floor, fromZ - reach, toX + reach, Math.max(roof, level), toZ + reach);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putIntArray(WELL, well);
        tag.putInt(CROSS_X, crossX);
        tag.putInt(CROSS_Z, crossZ);
        tag.putIntArray(KEEP, keep);
        tag.putIntArray(STREETS, streets);
    }

    private boolean underStreet(int x, int z, int half) { return Math.abs(z - crossZ) <= half - 1 || Math.abs(x - crossX) <= half - 1; }

    private int under(int x, int z) {
        int lowest = Integer.MAX_VALUE;
        for (int at = 0; at + 2 < streets.length; at += 3) {
            if (streets[at] == x && streets[at + 1] == z) { lowest = Math.min(lowest, streets[at + 2] - 2); }
        }
        return lowest;
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState lining = ContentCitySewerPiece.block(ContentCity.sewerBlock());
        if (lining == null) { return; }
        int depth = ContentCity.sewerDepth();
        int floor = this.level - depth;
        int height = ContentCity.sewerHeight();
        if (ContentCitySewerPiece.cramped(level.getMinBuildHeight(), this.level)) { return; }
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState water = CityPalette.stateOr(ContentCity.sewerWaterBlock(), air);
        BlockState walk = CityPalette.stateOr(ContentCity.sewerWalkBlock(), lining);
        BlockState light = ContentCitySewerPiece.block(ContentCity.sewerLightBlock());
        CityPalette moss = CityPalette.mixed(ContentCity.sewerMossBlock());
        int mossChance = moss == null ? 0 : ContentCity.sewerMossChance();
        int half = ContentCity.sewerWidth() / 2;
        int run = ContentCity.sewerLightRun();
        int roof = floor + 2 + height;
        BoundingBox held = getBoundingBox();
        List<CityRails.Laid> subways = CityRails.subways(CityGround.of(level), held.minX(), held.minZ(), held.maxX(), held.maxZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int dug = 0;
        int lit = 0;
        int walled = 0;
        int yielded = 0;
        for (int x = well[0] - LOOP - half; x <= well[2] + LOOP + half; x++) {
            for (int z = well[1] - LOOP - half; z <= well[3] + LOOP + half; z++) {
                int ring = CityPlotGround.band(well, x, z);
                if (ring < LOOP - half || ring > LOOP + half || CityPlotGround.kept(keep, x, z)) { continue; }
                if (!box.isInside(at.set(x, floor, z))) { continue; }
                int top = Math.min(roof, under(x, z));
                if (top < roof) { yielded++; }
                boolean fouled = false;
                for (int y = floor; y <= roof && !fouled; y++) { fouled = CityRails.insideBore(subways, x, y, z); }
                if (fouled) {
                    for (int y = floor; y <= top; y++) {
                        if (CityRails.insideBore(subways, x, y, z) || ContentCitySewerPiece.onTrack(level, at, x, y, z)) { continue; }
                        ContentCitySewerPiece.put(level, at.set(x, y, z), lining, lining, moss, mossChance);
                    }
                    walled++;
                    continue;
                }
                boolean edge = (ring == LOOP - half || ring == LOOP + half) && !underStreet(x, z, half);
                boolean channel = ring == LOOP;
                if (floor <= top) { ContentCitySewerPiece.put(level, at.set(x, floor, z), lining, lining, moss, mossChance); }
                if (floor + 1 <= top) { ContentCitySewerPiece.put(level, at.set(x, floor + 1, z), edge ? lining : channel ? water : walk, lining, moss, mossChance); }
                for (int y = floor + 2; y <= Math.min(floor + 1 + height, top); y++) { ContentCitySewerPiece.put(level, at.set(x, y, z), edge ? lining : air, lining, moss, mossChance); }
                boolean lamp = channel && light != null && Math.floorMod(x + z, run) == 0;
                if (roof <= top) {
                    ContentCitySewerPiece.put(level, at.set(x, roof, z), lamp ? light : lining, lining, moss, mossChance);
                    if (lamp) { lit++; }
                }
                dug++;
            }
        }
        int holes = 0;
        for (int x : new int[] {well[0] - LOOP, well[2] + LOOP}) {
            if (!CityPlotGround.kept(keep, x, crossZ + 1) && ContentCitySewerPiece.shaft(level, box, subways, x, crossZ + 1, floor, roof, this.level, lining, at)) { holes++; }
        }
        if (dug + holes + walled > 0) { ContentLog.LOGGER.debug("The sewer loop around the well at {}, {} laid {} column(s) {} block(s) under the plaza, {} lit, {} manhole(s) down onto it, {} column(s) walled solid where a subway passes through its depth, the street sewers at x {} and z {} running through it, {} column(s) kept under the support of a street graded below the plaza", well[0], well[1], dug, depth, lit, holes, walled, crossX, crossZ, yielded); }
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, box));
    }
}
