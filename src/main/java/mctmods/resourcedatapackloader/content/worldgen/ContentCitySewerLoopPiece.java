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

import javax.annotation.Nonnull;

public final class ContentCitySewerLoopPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCitySewerLoopPiece::new;
    public static final int LOOP = 5;
    private static final String LEVEL = "Level";
    private static final String WELL = "Well";
    private static final String CROSS_X = "CrossX";
    private static final String CROSS_Z = "CrossZ";
    private final int level;
    private final int[] well;
    private final int crossX;
    private final int crossZ;

    public ContentCitySewerLoopPiece(int fromX, int fromZ, int toX, int toZ, int level, int crossX, int crossZ) {
        super(TYPE, 0, box(fromX, fromZ, toX, toZ, level));
        this.level = level;
        this.well = new int[] {fromX, fromZ, toX, toZ};
        this.crossX = crossX;
        this.crossZ = crossZ;
    }

    public ContentCitySewerLoopPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        int[] held = tag.getIntArray(WELL);
        this.well = held.length == 4 ? held : new int[4];
        this.crossX = tag.getInt(CROSS_X);
        this.crossZ = tag.getInt(CROSS_Z);
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
    }

    private int band(int x, int z) {
        int bx = x < well[0] ? well[0] - x : x > well[2] ? x - well[2] : 0;
        int bz = z < well[1] ? well[1] - z : z > well[3] ? z - well[3] : 0;
        return Math.max(bx, bz);
    }

    private boolean underStreet(int x, int z, int half) { return Math.abs(z - crossZ) <= half - 1 || Math.abs(x - crossX) <= half - 1; }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState lining = ContentCitySewerPiece.block(ContentCity.sewerBlock());
        if (lining == null) { return; }
        int depth = ContentCity.sewerDepth();
        int floor = this.level - depth;
        int height = ContentCity.sewerHeight();
        if (floor < level.getMinBuildHeight() + ContentCitySewerPiece.FLOOR_LEAST) { return; }
        if (floor + height + ContentCitySewerPiece.CLEAR_UNDER >= this.level) { return; }
        BlockState water = ContentCitySewerPiece.block(ContentCity.sewerWaterBlock());
        BlockState walk = ContentCitySewerPiece.stateOr(ContentCity.sewerWalkBlock(), lining);
        BlockState light = ContentCitySewerPiece.block(ContentCity.sewerLightBlock());
        BlockState moss = ContentCitySewerPiece.block(ContentCity.sewerMossBlock());
        BlockState air = Blocks.AIR.defaultBlockState();
        int mossChance = moss == null ? 0 : ContentCity.sewerMossChance();
        int half = ContentCity.sewerWidth() / 2;
        int run = ContentCity.sewerLightRun();
        int roof = floor + 2 + height;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int dug = 0;
        int lit = 0;
        for (int x = well[0] - LOOP - half; x <= well[2] + LOOP + half; x++) {
            for (int z = well[1] - LOOP - half; z <= well[3] + LOOP + half; z++) {
                int ring = band(x, z);
                if (ring < LOOP - half || ring > LOOP + half) { continue; }
                if (!box.isInside(at.set(x, floor, z))) { continue; }
                boolean edge = (ring == LOOP - half || ring == LOOP + half) && !underStreet(x, z, half);
                boolean channel = ring == LOOP;
                ContentCitySewerPiece.put(level, at.set(x, floor, z), lining, lining, moss, mossChance);
                ContentCitySewerPiece.put(level, at.set(x, floor + 1, z), edge ? lining : channel && water != null ? water : walk, lining, moss, mossChance);
                for (int y = floor + 2; y <= floor + 1 + height; y++) { ContentCitySewerPiece.put(level, at.set(x, y, z), edge ? lining : air, lining, moss, mossChance); }
                boolean lamp = channel && light != null && Math.floorMod(x + z, run) == 0;
                ContentCitySewerPiece.put(level, at.set(x, roof, z), lamp ? light : lining, lining, moss, mossChance);
                if (lamp) { lit++; }
                dug++;
            }
        }
        int holes = 0;
        for (int x : new int[] {well[0] - LOOP, well[2] + LOOP}) {
            if (ContentCitySewerPiece.shaft(level, box, x, crossZ + 1, floor, roof, this.level, lining, at)) { holes++; }
        }
        if (dug + holes > 0) { ContentLog.LOGGER.debug("The sewer loop around the well at {}, {} laid {} column(s) {} block(s) under the plaza, {} lit, {} manhole(s) down onto it, the street sewers at x {} and z {} running through it", well[0], well[1], dug, depth, lit, holes, crossX, crossZ); }
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
