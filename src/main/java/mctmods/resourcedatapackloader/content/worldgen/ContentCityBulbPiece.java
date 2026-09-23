package mctmods.resourcedatapackloader.content.worldgen;


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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.List;
import javax.annotation.Nonnull;

public final class ContentCityBulbPiece extends StructurePiece implements PieceBeardifierModifier, ContentCityTrees.Felling {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityBulbPiece::new;
    private static final int CLEAR = 4;
    static final int VERGE = 6;
    private static final String LEVEL = "Level";
    private static final String REACH = "Reach";
    private static final String PAVING = "Paving";
    private static final String WALK = "Walk";
    private static final String EDGE = "Edge";
    private static final String CENTER_X = "CenterX";
    private static final String CENTER_Z = "CenterZ";
    private static final String ALONG_X = "AlongX";
    private static final String LOW = "Low";
    private static final String MIDDLE = "Mid";
    private static final String HALF = "Half";
    private static final String LANE = "Lane";
    private static final String LINES = "Lines";
    private static final String WALK_WIDE = "WalkWide";
    private static final String STEM = "Stem";
    private final Court court;
    private final Dress dress;

    public record Court(int centerX, int centerZ, int level, int reach, boolean alongX, boolean low, int middle, int half, int stem) {
        public int away(int x, int z) { return (x - centerX) * (x - centerX) + (z - centerZ) * (z - centerZ); }

        public int mouthOffset(int x, int z) { return Math.abs((alongX ? z : x) - middle); }

        public boolean throatAt(int x, int z) {
            if (half < 0 || mouthOffset(x, z) > half) { return false; }
            int along = alongX ? x - centerX : z - centerZ;
            return low ? along >= 0 && along <= reach + stem : along <= 0 && along >= -reach - stem;
        }

        public boolean unpavedAt(int x, int z) {
            if (throatAt(x, z)) { return false; }
            if (Math.abs(x - centerX) > reach || Math.abs(z - centerZ) > reach) { return true; }
            return away(x, z) > reach * reach + reach;
        }

        public boolean shoulderAt(int x, int z, int verge) {
            if (!unpavedAt(x, z)) { return false; }
            int shoulder = reach + verge;
            return (Math.abs(x - centerX) <= reach && Math.abs(z - centerZ) <= reach) || away(x, z) <= shoulder * shoulder + shoulder;
        }

        public BoundingBox box(int clear, int verge) { return new BoundingBox(centerX - reach - Math.max(verge, stem), level, centerZ - reach - Math.max(verge, stem), centerX + reach + Math.max(verge, stem), level + clear, centerZ + reach + Math.max(verge, stem)); }
    }

    public record Dress(int lane, int lines, int walk, String paving, String edge, String curb) {}

    public ContentCityBulbPiece(Court court, Dress dress) {
        super(TYPE, 0, court.box(CLEAR, VERGE));
        this.court = court;
        this.dress = dress;
    }

    public ContentCityBulbPiece(CompoundTag tag) {
        super(TYPE, tag);
        int reach = tag.getInt(REACH);
        boolean placed = tag.contains(CENTER_X);
        this.court = new Court(placed ? tag.getInt(CENTER_X) : (boundingBox.minX() + boundingBox.maxX()) / 2, placed ? tag.getInt(CENTER_Z) : (boundingBox.minZ() + boundingBox.maxZ()) / 2, tag.getInt(LEVEL), reach,
                tag.getBoolean(ALONG_X), tag.getBoolean(LOW), tag.getInt(MIDDLE), placed ? tag.getInt(HALF) : -1, tag.getInt(STEM));
        this.dress = new Dress(tag.getInt(LANE), tag.getInt(LINES), placed ? tag.getInt(WALK_WIDE) : 1, tag.getString(PAVING), tag.getString(EDGE), tag.getString(WALK));
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, court.level());
        tag.putInt(REACH, court.reach());
        tag.putInt(CENTER_X, court.centerX());
        tag.putInt(CENTER_Z, court.centerZ());
        tag.putBoolean(ALONG_X, court.alongX());
        tag.putBoolean(LOW, court.low());
        tag.putInt(MIDDLE, court.middle());
        tag.putInt(HALF, court.half());
        tag.putInt(STEM, court.stem());
        tag.putInt(LANE, dress.lane());
        tag.putInt(LINES, dress.lines());
        tag.putInt(WALK_WIDE, dress.walk());
        tag.putString(PAVING, dress.paving());
        tag.putString(EDGE, dress.edge());
        tag.putString(WALK, dress.curb());
    }

    @Override @Nonnull public BoundingBox stood() { return getBeardifierBox(); }

    @Override public int fellFloor() { return court.level() - 1; }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkPos chunk, @Nonnull BoundingBox box) {
        BlockState road = stateOr(dress.paving(), Blocks.DIRT_PATH.defaultBlockState());
        BlockState edge = stateOr(dress.edge(), road);
        BlockState curb = stateOr(dress.curb(), road);
        BlockState air = Blocks.AIR.defaultBlockState();
        BoundingBox held = getBoundingBox();
        ContentCityTrees.fellAround(level, manager, chunk, this, box);
        int pad = court.level();
        List<BoundingBox> others = ContentCityTrees.footprints(manager, chunk, this, box);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                if (court.unpavedAt(x, z)) {
                    if (court.shoulderAt(x, z, VERGE)) { CityPlotGround.vergeFill(level, box, others, x, z, pad, at); }
                    continue;
                }
                int top = Math.max(pad + CLEAR, level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) + 1);
                at.set(x, pad, z);
                level.setBlock(at, switch (role(x, z)) {
                    case CityCross.WALK -> curb;
                    case CityCross.LINE -> edge;
                    default -> road;
                }, 2);
                CityPlotGround.fillUnder(level, box, x, z, pad - 1, pad - CityPlotGround.FILL_UNDER);
                for (int up = 1; up <= CLEAR; up++) {
                    at.set(x, pad + up, z);
                    if (!level.getBlockState(at).isAir()) { level.setBlock(at, air, 2); }
                }
                for (int y = pad + CLEAR + 1; y <= top; y++) {
                    at.set(x, y, z);
                    BlockState over = level.getBlockState(at);
                    if (over.isAir()) { continue; }
                    if (!over.getFluidState().isEmpty() || !CityPlotGround.cuts(level, at, over)) { break; }
                    level.setBlock(at, air, 2);
                }
            }
        }
    }

    private int role(int x, int z) {
        int reach = court.reach();
        int away = court.away(x, z);
        boolean throat = court.throatAt(x, z);
        int offset = court.mouthOffset(x, z);
        boolean driven = offset <= dress.lane() + dress.lines();
        if (away > reach * reach + reach) { return offset <= dress.lane() ? CityCross.CORE : driven ? CityCross.LINE : CityCross.WALK; }
        boolean opening = throat && driven;
        int inner = reach - dress.walk();
        int core = inner - dress.lines();
        if (!opening && away > inner * inner + inner) { return CityCross.WALK; }
        if (!opening && dress.lines() > 0 && away > core * core + core) { return CityCross.LINE; }
        if (opening && away > core * core + core && offset > dress.lane()) { return CityCross.LINE; }
        return CityCross.CORE;
    }

    private static BlockState stateOr(String named, BlockState fallback) { return CityPalette.stateOr(named, fallback); }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return court.box(0, 0); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return ContentCity.adaptation(); }

    @Override public int getGroundLevelDelta() { return 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, manager, chunk, box));
    }
}
