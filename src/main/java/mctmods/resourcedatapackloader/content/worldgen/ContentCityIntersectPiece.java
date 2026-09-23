package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PathIntersectDef;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
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
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityIntersectPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityIntersectPiece::new;
    private static final String LEVEL = "Level";
    private static final String DESIGN = "Design";
    private static final String ROW_LOW = "RowLow";
    private static final String ROW_HIGH = "RowHigh";
    private static final String COLUMN_LOW = "ColLow";
    private static final String COLUMN_HIGH = "ColHigh";
    private static final String ARMS = "Arms";
    private static final String CORE_X = "CoreX";
    private static final String CORE_Z = "CoreZ";
    private static final int ALL_ARMS = CityCross.WEST | CityCross.EAST | CityCross.NORTH | CityCross.SOUTH;
    private final int level;
    private final String design;
    private final int rowLow;
    private final int rowHigh;
    private final int columnLow;
    private final int columnHigh;
    private final int arms;
    private final int coreX;
    private final int coreZ;

    public ContentCityIntersectPiece(int level, String design, int columnLow, int columnHigh, int rowLow, int rowHigh, int reach, int arms, int coreX, int coreZ) {
        super(TYPE, 0, new BoundingBox(columnLow - reach, level, rowLow - reach, columnHigh + reach, level + 1, rowHigh + reach));
        this.level = level;
        this.design = design;
        this.columnLow = columnLow;
        this.columnHigh = columnHigh;
        this.rowLow = rowLow;
        this.rowHigh = rowHigh;
        this.arms = arms;
        this.coreX = coreX;
        this.coreZ = coreZ;
    }

    public ContentCityIntersectPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.design = tag.getString(DESIGN);
        this.rowLow = tag.getInt(ROW_LOW);
        this.rowHigh = tag.getInt(ROW_HIGH);
        this.columnLow = tag.getInt(COLUMN_LOW);
        this.columnHigh = tag.getInt(COLUMN_HIGH);
        this.arms = tag.contains(ARMS) ? tag.getInt(ARMS) : ALL_ARMS;
        this.coreX = tag.contains(CORE_X) ? tag.getInt(CORE_X) : (columnHigh - columnLow) / 2;
        this.coreZ = tag.contains(CORE_Z) ? tag.getInt(CORE_Z) : (rowHigh - rowLow) / 2;
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putString(DESIGN, design);
        tag.putInt(ROW_LOW, rowLow);
        tag.putInt(ROW_HIGH, rowHigh);
        tag.putInt(COLUMN_LOW, columnLow);
        tag.putInt(COLUMN_HIGH, columnHigh);
        tag.putInt(ARMS, arms);
        tag.putInt(CORE_X, coreX);
        tag.putInt(CORE_Z, coreZ);
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        PathIntersectDef def = ContentPathIntersects.byName(design);
        if (def == null) { return; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        stamp(level, box, def, true, at);
        stamp(level, box, def, false, at);
    }

    private void stamp(WorldGenLevel level, BoundingBox box, PathIntersectDef def, boolean alongX, BlockPos.MutableBlockPos at) {
        int centerX = columnLow + (columnHigh - columnLow + 1) / 2;
        int centerZ = rowLow + (rowHigh - rowLow + 1) / 2;
        int center = alongX ? centerZ : centerX;
        int core = alongX ? coreZ : coreX;
        int otherCenter = alongX ? centerX : centerZ;
        int otherCore = alongX ? coreX : coreZ;
        boolean before = (arms & (alongX ? CityCross.WEST : CityCross.NORTH)) != 0;
        boolean after = (arms & (alongX ? CityCross.EAST : CityCross.SOUTH)) != 0;
        int low = otherCenter - otherCore - 1;
        int high = otherCenter + otherCore + 1;
        int reach = def.mouth().size();
        for (int across = center - core; across <= center + core; across++) {
            for (int row = low - reach + 1; row <= high + reach - 1; row++) {
                char mark = mark(def, row, across, center, core, otherCenter, otherCore, (row <= low && before) || (row >= high && after));
                if (mark == PathIntersectDef.KEEP) { continue; }
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                int y = row <= low || row >= high ? ContentCityPlazaPiece.surface(level, x, z, this.level, reach + 1) : this.level;
                if (y != Integer.MIN_VALUE) { paint(level, box, def, mark, at.set(x, y, z)); }
            }
        }
    }

    private static char mark(PathIntersectDef def, int row, int across, int center, int core, int otherCenter, int otherCore, boolean mouthed) {
        int low = otherCenter - otherCore - 1;
        int high = otherCenter + otherCore + 1;
        int line = row <= low ? low - row : row - high;
        if (mouthed && line < def.mouth().size()) {
            String cells = def.mouth().get(line);
            char mark = cells.isEmpty() ? PathIntersectDef.KEEP : cells.charAt(Math.floorMod(across - (center - core), cells.length()));
            if (mark != PathIntersectDef.KEEP) { return mark; }
        }
        if (row <= low || row >= high) { return PathIntersectDef.KEEP; }
        int fromRowEdge = Math.min(row - (otherCenter - otherCore), (otherCenter + otherCore) - row);
        int fromColumnEdge = core - Math.abs(across - center);
        if (fromRowEdge >= def.corner().size()) { return PathIntersectDef.KEEP; }
        String cells = def.corner().get(fromRowEdge);
        return fromColumnEdge < cells.length() ? cells.charAt(fromColumnEdge) : PathIntersectDef.KEEP;
    }

    private static void paint(WorldGenLevel level, BoundingBox box, PathIntersectDef def, char mark, BlockPos at) {
        if (mark == PathIntersectDef.KEEP || !box.isInside(at)) { return; }
        BlockState laid = resolve(def, mark);
        if (laid != null) { level.setBlock(at, laid, 2); }
    }

    @Nullable private static BlockState resolve(PathIntersectDef def, char mark) {
        BlockState road = state(ContentCity.paving(), Blocks.DIRT_PATH.defaultBlockState());
        if (mark == PathIntersectDef.ROAD || mark == PathIntersectDef.CORE) { return road; }
        if (mark == PathIntersectDef.LINE) { return state(ContentCity.lineBlock(), road); }
        if (mark == PathIntersectDef.WALK) { return state(ContentCity.sidewalkBlock(), road); }
        String named = def.legend().get(mark);
        return named == null ? road : state(named, road);
    }

    @Nullable private static BlockState state(String named, @Nullable BlockState fallback) {
        BlockState found = CityPalette.state(named);
        return found == null ? fallback : found;
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return CityPlotGround.layer(getBoundingBox(), level); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return ContentCity.adaptation(); }

    @Override public int getGroundLevelDelta() { return 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, box));
    }
}
