package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class ContentCityIntersectPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityIntersectPiece::new;
    private static final String LEVEL = "Level";
    private static final String DESIGN = "Design";
    private static final String ROW_LOW = "RowLow";
    private static final String ROW_HIGH = "RowHigh";
    private static final String COLUMN_LOW = "ColLow";
    private static final String COLUMN_HIGH = "ColHigh";
    private final int level;
    private final String design;
    private final int rowLow;
    private final int rowHigh;
    private final int columnLow;
    private final int columnHigh;

    public ContentCityIntersectPiece(int level, String design, int columnLow, int columnHigh, int rowLow, int rowHigh, int reach) {
        super(TYPE, 0, new BoundingBox(columnLow - reach, level, rowLow - reach, columnHigh + reach, level + 1, rowHigh + reach));
        this.level = level;
        this.design = design;
        this.columnLow = columnLow;
        this.columnHigh = columnHigh;
        this.rowLow = rowLow;
        this.rowHigh = rowHigh;
    }

    public ContentCityIntersectPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.design = tag.getString(DESIGN);
        this.rowLow = tag.getInt(ROW_LOW);
        this.rowHigh = tag.getInt(ROW_HIGH);
        this.columnLow = tag.getInt(COLUMN_LOW);
        this.columnHigh = tag.getInt(COLUMN_HIGH);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putString(DESIGN, design);
        tag.putInt(ROW_LOW, rowLow);
        tag.putInt(ROW_HIGH, rowHigh);
        tag.putInt(COLUMN_LOW, columnLow);
        tag.putInt(COLUMN_HIGH, columnHigh);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        PathIntersectDef def = ContentPathIntersects.byName(design);
        if (def == null) { return; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        mouths(level, box, def, at);
        corners(level, box, def, at);
    }

    private void mouths(WorldGenLevel level, BoundingBox box, PathIntersectDef def, BlockPos.MutableBlockPos at) {
        List<String> rows = def.mouth();
        for (int deep = 0; deep < rows.size(); deep++) {
            String row = rows.get(deep);
            if (row.isEmpty()) { continue; }
            for (int across = columnLow; across <= columnHigh; across++) {
                char mark = row.charAt(Math.floorMod(across - columnLow, row.length()));
                paint(level, box, def, mark, at.set(across, this.level, rowLow - 1 - deep));
                paint(level, box, def, mark, at.set(across, this.level, rowHigh + 1 + deep));
            }
            for (int across = rowLow; across <= rowHigh; across++) {
                char mark = row.charAt(Math.floorMod(across - rowLow, row.length()));
                paint(level, box, def, mark, at.set(columnLow - 1 - deep, this.level, across));
                paint(level, box, def, mark, at.set(columnHigh + 1 + deep, this.level, across));
            }
        }
    }

    private void corners(WorldGenLevel level, BoundingBox box, PathIntersectDef def, BlockPos.MutableBlockPos at) {
        List<String> rows = def.corner();
        for (int deep = 0; deep < rows.size(); deep++) {
            String row = rows.get(deep);
            for (int inward = 0; inward < row.length(); inward++) {
                char mark = row.charAt(inward);
                paint(level, box, def, mark, at.set(columnLow + inward, this.level, rowLow + deep));
                paint(level, box, def, mark, at.set(columnHigh - inward, this.level, rowLow + deep));
                paint(level, box, def, mark, at.set(columnLow + inward, this.level, rowHigh - deep));
                paint(level, box, def, mark, at.set(columnHigh - inward, this.level, rowHigh - deep));
            }
        }
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
        if (named.isEmpty()) { return fallback; }
        Block found = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(named));
        return found == null ? fallback : found.defaultBlockState();
    }
}
