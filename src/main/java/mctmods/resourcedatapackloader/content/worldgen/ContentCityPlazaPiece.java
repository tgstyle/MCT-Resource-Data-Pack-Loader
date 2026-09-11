package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.util.ContentLog;
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
import java.util.ArrayList;
import java.util.List;

public final class ContentCityPlazaPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityPlazaPiece::new;
    public static final int CLEAR = 4;
    static final int MOUTH_MOST = 4;
    private static final String LEVEL = "Level";
    private static final String WELL = "Well";
    private static final String PAVED = "Paved";
    private static final String WALK = "Walk";
    private static final String CORE = "Core";
    private static final String DESIGN = "Design";
    private static final String MIDDLE_X = "MidX";
    private static final String MIDDLE_Z = "MidZ";
    private final int level;
    private final int[] well;
    private final int paved;
    private final int walk;
    private final int core;
    private final String design;
    private final int middleX;
    private final int middleZ;

    public ContentCityPlazaPiece(int fromX, int fromZ, int toX, int toZ, int level, int paved, int walk, int core, String design, int middleX, int middleZ) {
        super(TYPE, 0, new BoundingBox(fromX - paved - walk - MOUTH_MOST, level, fromZ - paved - walk - MOUTH_MOST, toX + paved + walk + MOUTH_MOST, level + CLEAR + 1, toZ + paved + walk + MOUTH_MOST));
        this.level = level;
        this.well = new int[] {fromX, fromZ, toX, toZ};
        this.paved = paved;
        this.walk = walk;
        this.core = core;
        this.design = design;
        this.middleX = middleX;
        this.middleZ = middleZ;
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
    }

    private int band(int x, int z) {
        int bx = x < well[0] ? well[0] - x : x > well[2] ? x - well[2] : 0;
        int bz = z < well[1] ? well[1] - z : z > well[3] ? z - well[3] : 0;
        return Math.max(bx, bz);
    }

    private int armOffset(int x, int z) {
        int acrossX = Math.abs(x - middleX);
        int acrossZ = Math.abs(z - middleZ);
        boolean northSouth = (z < well[1] || z > well[3]) && acrossX <= core + 1;
        boolean eastWest = (x < well[0] || x > well[2]) && acrossZ <= core + 1;
        if (northSouth == eastWest) { return Integer.MAX_VALUE; }
        return northSouth ? acrossX : acrossZ;
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState road = stateOr(ContentCity.paving(), Blocks.DIRT_PATH.defaultBlockState());
        BlockState edge = stateOr(ContentCity.lineBlock(), road);
        BlockState sidewalk = stateOr(ContentCity.sidewalkBlock(), road);
        BlockState under = block(ContentCity.supportBlock());
        BlockState air = Blocks.AIR.defaultBlockState();
        PathIntersectDef mouth = design.isEmpty() ? null : ContentPathIntersects.byName(design);
        List<String> rows = mouth == null ? List.of() : mouth.mouth();
        int ring = paved + walk;
        BoundingBox held = getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        List<BlockPos> open = new ArrayList<>();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                int band = band(x, z);
                boolean paves = band <= ring || (band <= ring + rows.size() && armOffset(x, z) <= core);
                if (paves && (band > paved || x < well[0] || x > well[2] || z < well[1] || z > well[3])) { open.add(new BlockPos(x, this.level, z)); }
            }
        }
        int felled = ContentCityTrees.fellOver(level, box, open, this.level + 1, this.level + CLEAR);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) before the plaza around the well at {}, {} was paved", felled, well[0], well[1]); }
        int columns = 0;
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                int band = band(x, z);
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
                    String row = rows.get(band - ring - 1);
                    laid = row.isEmpty() ? road : paint(mouth, row.charAt(Math.floorMod(arm + core, row.length())), road, edge, sidewalk);
                }
                else { continue; }
                at.set(x, this.level, z);
                level.setBlock(at, laid, 2);
                if (under != null) {
                    at.set(x, this.level - 1, z);
                    level.setBlock(at, under, 2);
                }
                if (band > paved || x < well[0] || x > well[2] || z < well[1] || z > well[3]) {
                    for (int up = 1; up <= CLEAR; up++) {
                        at.set(x, this.level + up, z);
                        BlockState over = level.getBlockState(at);
                        if (ContentCityTrees.clears(level, at, over)) { level.setBlock(at, air, 2); }
                    }
                }
                columns++;
            }
        }
        if (columns > 0) { ContentLog.LOGGER.debug("Paved {} column(s) of plaza around the well at {}, {} at y {}, {} out from it with a {} wide sidewalk ring", columns, well[0], well[1], this.level, paved, walk); }
    }

    private static BlockState paint(@Nullable PathIntersectDef def, char mark, BlockState road, BlockState edge, BlockState sidewalk) {
        if (mark == PathIntersectDef.ROAD || mark == PathIntersectDef.CORE || mark == PathIntersectDef.KEEP || def == null) { return road; }
        if (mark == PathIntersectDef.LINE) { return edge; }
        if (mark == PathIntersectDef.WALK) { return sidewalk; }
        String named = def.legend().get(mark);
        return named == null ? road : stateOr(named, road);
    }

    @Nullable private static BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(named));
        return found == null ? null : found.defaultBlockState();
    }

    private static BlockState stateOr(String named, BlockState fallback) {
        BlockState found = block(named);
        return found == null ? fallback : found;
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
