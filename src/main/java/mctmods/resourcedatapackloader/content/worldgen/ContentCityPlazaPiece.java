package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.Heightmap;
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
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ContentCityPlazaPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityPlazaPiece::new;
    public static final int CLEAR = 4;
    static final int MOUTH_MOST = 4;
    private static final int FILL = 8;
    private static final int TAPER_MOST = 3;
    private static final String LEVEL = "Level";
    private static final String WELL = "Well";
    private static final String PAVED = "Paved";
    private static final String WALK = "Walk";
    private static final String CORE = "Core";
    private static final String DESIGN = "Design";
    private static final String MIDDLE_X = "MidX";
    private static final String MIDDLE_Z = "MidZ";
    private static final String KEEP = "Keep";
    private static final String ARMS = "Arms";
    private static final int ALL_ARMS = CityCross.WEST | CityCross.EAST | CityCross.NORTH | CityCross.SOUTH;
    private final int level;
    private final int[] well;
    private final int paved;
    private final int walk;
    private final int core;
    private final String design;
    private final int middleX;
    private final int middleZ;
    private final int[] keep;
    private final int arms;

    public ContentCityPlazaPiece(int fromX, int fromZ, int toX, int toZ, int level, int paved, int walk, int core, String design, int middleX, int middleZ, int arms, int[] footprints) {
        super(TYPE, 0, new BoundingBox(fromX - paved - walk - MOUTH_MOST - TAPER_MOST, level - FILL, fromZ - paved - walk - MOUTH_MOST - TAPER_MOST, toX + paved + walk + MOUTH_MOST + TAPER_MOST, level + CLEAR + 1, toZ + paved + walk + MOUTH_MOST + TAPER_MOST));
        this.level = level;
        this.well = new int[] {fromX, fromZ, toX, toZ};
        this.paved = paved;
        this.walk = walk;
        this.core = core;
        this.design = design;
        this.middleX = middleX;
        this.middleZ = middleZ;
        this.arms = arms;
        this.keep = CityPlotGround.within(getBoundingBox(), footprints);
    }

    public ContentCityPlazaPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        int[] held = tag.getIntArray(WELL);
        this.well = held.length == 4 ? held : new int[4];
        this.paved = tag.getInt(PAVED);
        this.walk = tag.getInt(WALK);
        this.core = tag.getInt(CORE);
        this.design = tag.getString(DESIGN);
        this.middleX = tag.getInt(MIDDLE_X);
        this.middleZ = tag.getInt(MIDDLE_Z);
        this.keep = tag.getIntArray(KEEP);
        this.arms = tag.contains(ARMS) ? tag.getInt(ARMS) : ALL_ARMS;
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putIntArray(WELL, well);
        tag.putInt(PAVED, paved);
        tag.putInt(WALK, walk);
        tag.putInt(CORE, core);
        tag.putString(DESIGN, design);
        tag.putInt(MIDDLE_X, middleX);
        tag.putInt(MIDDLE_Z, middleZ);
        tag.putIntArray(KEEP, keep);
        tag.putInt(ARMS, arms);
    }

    BoundingBox reached() {
        int ring = paved + walk;
        return new BoundingBox(well[0] - ring, level, well[1] - ring, well[2] + ring, level, well[3] + ring);
    }

    static boolean holding(List<ContentCityPlazaPiece> plazas, int x, int z) {
        for (ContentCityPlazaPiece plaza : plazas) {
            if (CityPlotGround.band(plaza.well, x, z) <= plaza.paved + plaza.walk) { return true; }
        }
        return false;
    }

    @Nullable static BlockState mouthAt(List<ContentCityPlazaPiece> plazas, int x, int z) {
        for (ContentCityPlazaPiece plaza : plazas) {
            PathIntersectDef def = plaza.design.isEmpty() ? null : ContentPathIntersects.byName(plaza.design);
            BlockState road = stateOr(ContentCity.paving(), Blocks.DIRT_PATH.defaultBlockState());
            BlockState marked = plaza.mouth(def, x, z, road, stateOr(ContentCity.lineBlock(), road), stateOr(ContentCity.sidewalkBlock(), road));
            if (marked != null) { return marked; }
        }
        return null;
    }

    @Nullable private BlockState mouth(@Nullable PathIntersectDef def, int x, int z, BlockState road, BlockState edge, BlockState sidewalk) {
        if (def == null || Integer.bitCount(arms) < 3 || (arms & side(x, z)) == 0 || armOffset(x, z) > core) { return null; }
        int line = CityPlotGround.band(well, x, z) - paved - walk - 1;
        if (line < 0 || line >= def.mouth().size()) { return null; }
        String cells = def.mouth().get(line);
        if (cells.isEmpty()) { return null; }
        int across = northSouth(x, z) ? x - middleX : z - middleZ;
        char mark = cells.charAt(Math.floorMod(across + core, cells.length()));
        return mark == PathIntersectDef.KEEP ? null : paint(def, mark, road, edge, sidewalk);
    }

    private int side(int x, int z) {
        if (northSouth(x, z)) { return z < well[1] ? CityCross.NORTH : CityCross.SOUTH; }
        return x < well[0] ? CityCross.WEST : CityCross.EAST;
    }

    private boolean northSouth(int x, int z) { return (z < well[1] || z > well[3]) && Math.abs(x - middleX) <= core + 1; }

    private int armOffset(int x, int z) {
        int acrossX = Math.abs(x - middleX);
        int acrossZ = Math.abs(z - middleZ);
        boolean northSouth = northSouth(x, z);
        boolean eastWest = (x < well[0] || x > well[2]) && acrossZ <= core + 1;
        if (northSouth == eastWest) { return Integer.MAX_VALUE; }
        return northSouth ? acrossX : acrossZ;
    }

    @SuppressWarnings("deprecation") private void laid(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkPos chunk, @Nonnull BoundingBox box) {
        BlockState road = stateOr(ContentCity.paving(), Blocks.DIRT_PATH.defaultBlockState());
        BlockState edge = stateOr(ContentCity.lineBlock(), road);
        BlockState sidewalk = stateOr(ContentCity.sidewalkBlock(), road);
        BlockState air = Blocks.AIR.defaultBlockState();
        PathIntersectDef mouth = design.isEmpty() ? null : ContentPathIntersects.byName(design);
        List<String> rows = mouth == null ? List.of() : mouth.mouth();
        int ring = paved + walk;
        BoundingBox held = getBoundingBox();
        List<BoundingBox> others = ContentCityTrees.foreign(manager, chunk, this, held, piece -> piece instanceof ContentCityTrees.Felling && !(piece instanceof ContentCityPiece) || piece instanceof ContentCityWellPiece);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        List<BlockPos> open = new ArrayList<>();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                int band = CityPlotGround.band(well, x, z);
                boolean paves = !CityPlotGround.kept(keep, x, z) && !CityPlotGround.covered(others, x, z) && (band <= ring || (band <= ring + rows.size() && armOffset(x, z) <= core));
                if (paves && (band > paved || x < well[0] || x > well[2] || z < well[1] || z > well[3])) { open.add(new BlockPos(x, this.level, z)); }
            }
        }
        int felled = ContentCityTrees.fellOver(level, manager, chunk, box, open, this.level + 1, this.level + CLEAR);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the plaza around the well at {}, {} was paved", felled, well[0], well[1]); }
        int columns = 0;
        int wet = 0;
        List<CityRails.Laid> bores = CityRails.subways(CityGround.of(level), held.minX() - TAPER_MOST, held.minZ() - TAPER_MOST, held.maxX() + TAPER_MOST, held.maxZ() + TAPER_MOST);
        int spared = 0;
        int yielded = 0;
        boolean chosen = ContentCity.pathChosen();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                if (CityPlotGround.kept(keep, x, z)) { continue; }
                int band = CityPlotGround.band(well, x, z);
                int arm = armOffset(x, z);
                BlockState laid;
                if (band <= paved) {
                    laid = band == paved && arm > core ? edge : road;
                    if (band == paved && arm == core + 1) { laid = edge; }
                }
                else if (band <= ring) {
                    laid = arm <= core ? road : arm == core + 1 ? edge : sidewalk;
                }
                else if (band <= ring + rows.size() && arm <= core) {
                    BlockState marked = mouth(mouth, x, z, road, edge, sidewalk);
                    if (marked == null) { continue; }
                    laid = marked;
                }
                else { continue; }
                if (CityPlotGround.covered(others, x, z)) {
                    yielded++;
                    continue;
                }
                if (band > ring) {
                    int street = surface(level, x, z, this.level, rows.size() + 1);
                    if (street != Integer.MIN_VALUE) {
                        BlockState painted = chosen ? laid : natural(level.getBlockState(at.set(x, street, z)));
                        level.setBlock(at.set(x, street, z), painted, 2);
                        columns++;
                    }
                    continue;
                }
                boolean outside = band > paved || x < well[0] || x > well[2] || z < well[1] || z > well[3];
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                if (outside && top >= this.level && !level.getBlockState(at.set(x, top, z)).getFluidState().isEmpty()) {
                    wet++;
                    continue;
                }
                if (CityRails.insideBore(bores, x, this.level, z)) {
                    spared++;
                    continue;
                }
                int roof = CityRails.boreRoof(bores, x, z);
                if (!chosen) { laid = natural(level.getBlockState(at.set(x, this.level, z))); }
                at.set(x, this.level, z);
                level.setBlock(at, laid, 2);
                BlockState ground = CityPlotGround.groundFor(level, x, z);
                for (int down = 1; down <= FILL && this.level - down > roof; down++) {
                    at.set(x, this.level - down, z);
                    if (level.getBlockState(at).isSolid()) { break; }
                    level.setBlock(at, ground, 2);
                }
                if (outside) {
                    for (int up = 1; up <= Math.max(CLEAR, top - this.level + 2); up++) {
                        at.set(x, this.level + up, z);
                        BlockState over = level.getBlockState(at);
                        if (ContentCityTrees.clears(level, at, over)) { level.setBlock(at, air, 2); }
                    }
                }
                columns++;
            }
        }
        taper(level, box, ring + rows.size(), bores, others, at);
        if (spared > 0) { ContentLog.LOGGER.debug("Left {} column(s) of the plaza around the well at {}, {} to the subway bore under them", spared, well[0], well[1]); }
        if (yielded > 0) { ContentLog.LOGGER.debug("Left {} column(s) of the plaza around the well at {}, {} to the pieces of a neighboring district standing there", yielded, well[0], well[1]); }
        if (wet > 0) { ContentLog.LOGGER.debug("Left {} column(s) of the plaza around the well at {}, {} to the water standing there", wet, well[0], well[1]); }
        if (columns > 0) { ContentLog.LOGGER.debug("Paved {} column(s) of plaza around the well at {}, {} at y {}, {} out from it with a {} wide sidewalk ring", columns, well[0], well[1], this.level, paved, walk); }
    }

    static int surface(WorldGenLevel level, int x, int z, int around, int reach) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int y = around + reach; y >= around - reach; y--) {
            if (CityPlotGround.solid(level.getBlockState(at.set(x, y, z)))) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    private static BlockState natural(BlockState ground) {
        if (ground.is(BlockTags.SAND)) { return Blocks.SANDSTONE.defaultBlockState(); }
        if (ground.is(BlockTags.TERRACOTTA)) { return Blocks.TERRACOTTA.defaultBlockState(); }
        if (ground.is(Blocks.GRAVEL)) { return Blocks.GRAVEL.defaultBlockState(); }
        return Blocks.DIRT_PATH.defaultBlockState();
    }

    @SuppressWarnings("deprecation") private void taper(WorldGenLevel level, BoundingBox box, int reach, List<CityRails.Laid> bores, List<BoundingBox> others, BlockPos.MutableBlockPos at) {
        for (int x = Math.max(well[0] - reach - TAPER_MOST, box.minX()); x <= Math.min(well[2] + reach + TAPER_MOST, box.maxX()); x++) {
            for (int z = Math.max(well[1] - reach - TAPER_MOST, box.minZ()); z <= Math.min(well[3] + reach + TAPER_MOST, box.maxZ()); z++) {
                int ring = CityPlotGround.band(well, x, z) - reach;
                if (ring < 1 || armOffset(x, z) <= core + walk + 1 || CityPlotGround.kept(keep, x, z) || CityPlotGround.covered(others, x, z)) { continue; }
                BlockState ground = CityPlotGround.groundFor(level, x, z);
                int rings = ground.is(Blocks.SAND) ? 3 : ground.is(Blocks.TERRACOTTA) ? 1 : 2;
                if (ring > rings) { continue; }
                int shelf = this.level - ring;
                BlockState over = level.getBlockState(at.set(x, shelf + 1, z));
                if (over.isSolid() || !over.getFluidState().isEmpty()) { continue; }
                int footing = Integer.MIN_VALUE;
                for (int y = shelf; y >= Math.max(shelf - 4 - FILL, CityRails.boreRoof(bores, x, z) + 1); y--) {
                    BlockState held = level.getBlockState(at.set(x, y, z));
                    if (!held.getFluidState().isEmpty()) { break; }
                    if (held.isSolid()) {
                        footing = y;
                        break;
                    }
                }
                if (footing == Integer.MIN_VALUE) { continue; }
                for (int y = shelf; y > footing; y--) { level.setBlock(at.set(x, y, z), CityPlotGround.exposed(level, at, ground), 2); }
            }
        }
    }

    private static BlockState paint(@Nullable PathIntersectDef def, char mark, BlockState road, BlockState edge, BlockState sidewalk) {
        if (mark == PathIntersectDef.ROAD || mark == PathIntersectDef.CORE || mark == PathIntersectDef.KEEP || def == null) { return road; }
        if (mark == PathIntersectDef.LINE) { return edge; }
        if (mark == PathIntersectDef.WALK) { return sidewalk; }
        String named = def.legend().get(mark);
        return named == null ? road : stateOr(named, road);
    }

    private static BlockState stateOr(String named, BlockState fallback) { return CityPalette.stateOr(named, fallback); }

    @Override @Nonnull public BoundingBox getBeardifierBox() { return CityPlotGround.layer(getBoundingBox(), level); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return ContentCity.adaptation(); }

    @Override public int getGroundLevelDelta() { return 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, manager, chunk, box));
    }
}
